package opi.botc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import opi.botc.utils.BookBuilder;
import opi.botc.utils.Colors;
import opi.botc.utils.Randomize;
import opi.botc.utils.StateSaverAndLoader;
import opi.botc.utils.ZoneTracker;
import opi.botc.zones.ArmorStandLocation;
import opi.botc.zones.ColorLocation;
import opi.botc.zones.Zone;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class BloodOfTheClocktower implements ModInitializer {
	public static final String MOD_ID = "botc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private ServerPlayerEntity playerDied = null;
	private final ZoneTracker zoneTracker = new ZoneTracker();
	public Map<String, Zone> zones = new HashMap<>();
	public static MinecraftServer server = null;
	public Map<String, ArmorStandLocation> armorStandLocations = new HashMap<>();
	public Map<String, ColorLocation> colorLocations = new HashMap<>();
	public Map<UUID, Colors> playerColors = new HashMap<>();
	Randomize random = new Randomize(colorLocations, LOGGER);

	private void onServerStarted(MinecraftServer server) {
		StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
		BloodOfTheClocktower.server = server;
		zones = serverState.getZones();
		armorStandLocations = serverState.getArmorStandLocations();
		colorLocations = serverState.getColorLocations();
	}

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (playerDied != null && playerDied.getUuid().equals(newPlayer.getUuid())) {
				newPlayer.addStatusEffect(
						new StatusEffectInstance(StatusEffects.REGENERATION, StatusEffectInstance.INFINITE, 10));
				newPlayer.addStatusEffect(
						new StatusEffectInstance(StatusEffects.INVISIBILITY, StatusEffectInstance.INFINITE, 1));
				String command = "execute as " + playerDied.getName().getString()
						+ " run attribute @s minecraft:movement_speed base set 0.1";
				server.getCommandManager().execute(
						server.getCommandManager().getDispatcher().parse(command, server.getCommandSource()), command);
				command = "execute as " + playerDied.getName().getString()
						+ " run attribute @s minecraft:jump_strength base set 0.42";
				server.getCommandManager().execute(
						server.getCommandManager().getDispatcher().parse(command, server.getCommandSource()), command);
				playerDied = null;
			}
		});

		AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
			if (entity instanceof PlayerEntity && !(player.hasPermissionLevel(2))) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("dieSet")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.then(CommandManager.argument("v1", DoubleArgumentType.doubleArg())
								.then(CommandManager.argument("v2", DoubleArgumentType.doubleArg())
										.then(CommandManager.argument("v3", DoubleArgumentType.doubleArg())
												.executes(context -> {
													final var world = context.getSource().getWorld();
													final double deathX = DoubleArgumentType.getDouble(context, "v1");
													final double deathY = DoubleArgumentType.getDouble(context, "v2");
													final double deathZ = DoubleArgumentType.getDouble(context, "v3");
													StateSaverAndLoader serverState = StateSaverAndLoader
															.getServerState(world.getServer());
													serverState.deathX = deathX;
													serverState.deathY = deathY;
													serverState.deathZ = deathZ;
													return 1;
												}))))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("die")
						.then(CommandManager.argument("player", EntityArgumentType.player())
								.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
								.executes(context -> {
									final var world = context.getSource().getWorld();
									StateSaverAndLoader serverState = StateSaverAndLoader
											.getServerState(world.getServer());
									final var player = EntityArgumentType.getPlayer(context, "player");
									player.setPosition(serverState.deathX, serverState.deathY, serverState.deathZ);
									player.networkHandler.requestTeleport(serverState.deathX, serverState.deathY,
											serverState.deathZ, player.getYaw(), player.getPitch());

									MinecraftServer server = player.getServer();
									CommandManager commandManager = server.getCommandManager();
									playerDied = player;
									String command1 = "execute as " + player.getName().getString()
											+ " run attribute @s minecraft:movement_speed base set 0";
									commandManager.execute(
											commandManager.getDispatcher().parse(command1, server.getCommandSource()),
											command1);
									command1 = "execute as " + player.getName().getString()
											+ " run attribute @s minecraft:jump_strength base set 0";
									commandManager.execute(
											commandManager.getDispatcher().parse(command1, server.getCommandSource()),
											command1);
									String command = "execute at" + " " + player.getName().getString()
											+ " run setblock " + "~ ~100 ~" + " " + "damaged_anvil";
									commandManager.execute(
											commandManager.getDispatcher().parse(command, server.getCommandSource()),
											command);
									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneAdd")
						.then(CommandManager.argument("Zone Name", StringArgumentType.string())
								.then(CommandManager.argument("x1", DoubleArgumentType.doubleArg())
										.then(CommandManager.argument("y1", DoubleArgumentType.doubleArg())
												.then(CommandManager.argument("z1", DoubleArgumentType.doubleArg())
														.then(CommandManager
																.argument("x2", DoubleArgumentType.doubleArg())
																.then(CommandManager
																		.argument("y2", DoubleArgumentType.doubleArg())
																		.then(CommandManager
																				.argument("z2",
																						DoubleArgumentType.doubleArg())
																				.requires(
																						source -> source.hasPermissionLevel(2) || source.getEntity() == null)
																				.executes(context -> {
																					final var world = context
																							.getSource()
																							.getWorld();
																					final double minX = DoubleArgumentType
																							.getDouble(context, "x1");
																					final double minY = DoubleArgumentType
																							.getDouble(context, "y1");
																					final double minZ = DoubleArgumentType
																							.getDouble(context, "z1");
																					final double maxX = DoubleArgumentType
																							.getDouble(context, "x2");
																					final double maxY = DoubleArgumentType
																							.getDouble(context, "y2");
																					final double maxZ = DoubleArgumentType
																							.getDouble(context, "z2");
																					final String zoneName = StringArgumentType
																							.getString(context,
																									"Zone Name");
																					StateSaverAndLoader serverState = StateSaverAndLoader
																							.getServerState(
																									world.getServer());
																					Zone zone = new Zone(
																							zoneName, minX, minY, minZ,
																							maxX, maxY, maxZ);
																					serverState.addZone(zone);
																					zones.put(zone.key, zone);
																					context.getSource().sendFeedback(
																							() -> Text.of("Zone added"),
																							false);
																					return 1;
																				}))))))))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneList")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							if(zones.size() == 0) {
								context.getSource().sendFeedback(() -> Text.of("No zones"), false);
							}
							for (Map.Entry<String, Zone> entry : zones.entrySet()) {
								context.getSource().sendFeedback(() -> Text.of(entry.getKey()), false);
							}
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneClear")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							final var world = context.getSource().getWorld();
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
							serverState.clearZones();
							zones.clear();
							context.getSource().sendFeedback(() -> Text.of("Zones cleared"), false);
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneRemove")
						.then(CommandManager.argument("zone", StringArgumentType.string())
								.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
								.executes(context -> {
									final var world = context.getSource().getWorld();
									StateSaverAndLoader serverState = StateSaverAndLoader
											.getServerState(world.getServer());
									final var zone = StringArgumentType.getString(context, "zone");
									if (zones.get(zone) != null) {
										serverState.removeZone(zone);
										zones.remove(zone);
										context.getSource().sendFeedback(() -> Text.of("Zone " + zone + " deleted"),
												false);
									} else {
										context.getSource()
												.sendFeedback(() -> Text.of("Zone " + zone + " does not exist"), false);
									}
									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneStand")
						.then(CommandManager.argument("key", StringArgumentType.string())
								.then(CommandManager.argument("x", DoubleArgumentType.doubleArg())
										.then(CommandManager.argument("y", DoubleArgumentType.doubleArg())
												.then(CommandManager.argument("z", DoubleArgumentType.doubleArg())
														.then(CommandManager
																.argument("direction", StringArgumentType.string())
																.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
																.executes(context -> {
																	final var key = StringArgumentType.getString(
																			context,
																			"key");
																	final var x = DoubleArgumentType.getDouble(context,
																			"x");
																	final var y = DoubleArgumentType.getDouble(context,
																			"y");
																	final var z = DoubleArgumentType.getDouble(context,
																			"z");
																	final var direction = StringArgumentType.getString(
																			context,
																			"direction");
																	final var world = context.getSource().getWorld();
																	StateSaverAndLoader serverState = StateSaverAndLoader
																			.getServerState(world.getServer());
																	ArmorStandLocation location = new ArmorStandLocation(
																			key, x, y, z, direction);
																	serverState.addArmorStandLocation(key,
																			location);
																	armorStandLocations.put(key,
																			location);
																	context.getSource().sendFeedback(
																			() -> Text.of("Location added"),
																			false);
																	return 1;
																}))))))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneStandRemove")
						.then(CommandManager.argument("key", StringArgumentType.string())
								.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
								.executes(context -> {
									ArmorStandLocation location = armorStandLocations
											.get(StringArgumentType.getString(context, "key"));
									if (location != null) {
										armorStandLocations.remove(location.getKey());
										StateSaverAndLoader serverState = StateSaverAndLoader
												.getServerState(context.getSource().getWorld().getServer());
										serverState.removeArmorStandLocation(location.getKey());
										context.getSource().sendFeedback(
												() -> Text.of("Location " + location.key + " deleted"),
												false);
									} else {
										context.getSource()
												.sendFeedback(() -> Text.of("Location does not exist"), false);
									}
									return 1;
								}))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneStandList")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							if(armorStandLocations.size() == 0) {
								context.getSource().sendFeedback(() -> Text.of("No locations"), false);
							}
							for (Map.Entry<String, ArmorStandLocation> entry : armorStandLocations.entrySet()) {
								context.getSource().sendFeedback(() -> Text.of(entry.getKey()), false);
							}
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("zoneStandClear")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							final var world = context.getSource().getWorld();
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
							serverState.clearArmorStandLocations();
							armorStandLocations.clear();
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("dieUndo")
						.then(CommandManager.argument("player", EntityArgumentType.player())
								.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
								.executes(context -> {
									final var player = EntityArgumentType.getPlayer(context, "player");
									String command = "execute at" + " " + player.getName().getString()
											+ " run attribute @s minecraft:movement_speed base set 0.1";
									server.getCommandManager().execute(server.getCommandManager().getDispatcher()
											.parse(command, server.getCommandSource()), command);
									command = "execute at" + " " + player.getName().getString()
											+ " run attribute @s minecraft:jump_strength base set 0.42";
									server.getCommandManager().execute(server.getCommandManager().getDispatcher()
											.parse(command, server.getCommandSource()), command);
									return 1;
								}))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("houseSet")
						.then(CommandManager.argument("color", StringArgumentType.string())
								.then(CommandManager.argument("x", DoubleArgumentType.doubleArg())
										.then(CommandManager.argument("y", DoubleArgumentType.doubleArg())
												.then(CommandManager.argument("z", DoubleArgumentType.doubleArg())
														.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
														.executes(context -> {
															final var world = context.getSource().getWorld();
															StateSaverAndLoader serverState = StateSaverAndLoader
																	.getServerState(world.getServer());
															final double x = DoubleArgumentType.getDouble(context, "x");
															final double y = DoubleArgumentType.getDouble(context, "y");
															final double z = DoubleArgumentType.getDouble(context, "z");
															final String color = StringArgumentType.getString(context,
																	"color");
															ColorLocation location = new ColorLocation(
																	Colors.fromString(color), x, y, z);
															serverState.addColorLocation(color, location);
															colorLocations.put(color, location);
															return 1;
														})))))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("houseList")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							if(colorLocations.size() == 0) {
								context.getSource().sendFeedback(() -> Text.of("No houses"),false);
							}
							for (Map.Entry<String, ColorLocation> entry : colorLocations.entrySet()) {
								context.getSource().sendFeedback(() -> Text.of(entry.getKey()), false);
							}
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("houseClear")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							final var world = context.getSource().getWorld();
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
							serverState.clearColorLocations();
							colorLocations.clear();
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("houseRemove")
						.then(CommandManager.argument("color", StringArgumentType.string())
								.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
								.executes(context -> {
									final var world = context.getSource().getWorld();
									StateSaverAndLoader serverState = StateSaverAndLoader
											.getServerState(world.getServer());
									final var color = StringArgumentType.getString(context, "color");
									if (colorLocations.get(color) != null) {
										serverState.removeColorLocation(color);
										colorLocations.remove(color);
										context.getSource().sendFeedback(() -> Text.of("House " + color + " deleted"),
												false);
									} else {
										context.getSource().sendFeedback(
												() -> Text.of("House " + color + " does not exist"),
												false);
									}
									return 1;
								}))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("gameStart")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							if(colorLocations.size() != 8) {
								context.getSource().sendFeedback(() -> Text.of("Need 8 houses"), false);
								return 1;
							}
							random.putAllColorLocations(colorLocations);
							random.randomize(server.getPlayerManager().getPlayerList());
							playerColors.putAll(random.getPlayerColors());
							for (Map.Entry<UUID, Colors> entry : playerColors.entrySet()) {
								ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
								if (player == null) {
									LOGGER.warn("Skipping: Player with UUID {} not found.", entry.getKey());
									continue;
								}
								String colorKey = entry.getValue().toString();
								ColorLocation location = colorLocations.get(colorKey);
								if (location == null) {
									LOGGER.warn("Skipping: No location found for color {}", colorKey);
									continue;
								}
								player.teleport(location.getX(), location.getY(), location.getZ(), true);
								player.networkHandler.requestTeleport(location.getX(), location.getY(), location.getZ(), player.getYaw(), player.getPitch());
								context.getSource().sendFeedback(() -> Text.of("Teleported " + player.getName().getString() + " to " + colorKey), false);

							}
							BookBuilder bookBuilder = new BookBuilder(playerColors, server);
							String giveCommand = bookBuilder.createGiveCommand();
							LOGGER.info(giveCommand);
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("gameReset")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							random.putAllColorLocations(colorLocations);
							random.clearPlayerColors();
							playerColors.clear();
							
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("playerColorList")
						.requires(source -> source.hasPermissionLevel(2) || source.getEntity() == null)
						.executes(context -> {
							if(playerColors.size() == 0) {
								context.getSource().sendFeedback(() -> Text.of("No player colors"), false);
							}
							for (Map.Entry<UUID, Colors> entry : playerColors.entrySet()) {
								context.getSource().sendFeedback(() -> Text.of(server.getPlayerManager().getPlayer(entry.getKey()).getName().toString() + " " + entry.getValue()),
										false);
							}
							return 1;
						})));
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
			Map<String, Zone> zones = serverState.getZones();
			if (zones.size() > 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					zoneTracker.checkPlayerZone(player, zones, armorStandLocations);
				}
			}
		});

	}
}