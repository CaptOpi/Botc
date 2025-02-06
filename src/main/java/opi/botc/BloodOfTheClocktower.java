package opi.botc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.VaultBlockEntity.Server;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.literal;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class BloodOfTheClocktower implements ModInitializer {
	public static final String MOD_ID = "botc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private ServerPlayerEntity playerFreeze = null;
	private ServerPlayerEntity playerDied = null;
	private boolean deathFreeze = false;
	private boolean voteTime = false;
	private int counter;
	private final ZoneTracker zoneTracker = new ZoneTracker();
	public Map<String, StateSaverAndLoader.Zone> zones = new HashMap<>();
	public static MinecraftServer server = null;

	private void onServerStarted(MinecraftServer server) {
		StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
		this.server = server;
		zones = serverState.getZones();
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
				playerDied = null;
				deathFreeze = false;
				playerFreeze = null;
			}
		});

		AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
			if (entity instanceof PlayerEntity) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("setDie")
						.requires(source -> source.hasPermissionLevel(2))
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
								.requires(source -> source.hasPermissionLevel(2))
								.executes(context -> {
									final var world = context.getSource().getWorld();
									StateSaverAndLoader serverState = StateSaverAndLoader
											.getServerState(world.getServer());
									final var player = EntityArgumentType.getPlayer(context, "player");
									player.setPosition(serverState.deathX, serverState.deathY, serverState.deathZ);
									player.networkHandler.requestTeleport(serverState.deathX, serverState.deathY,
											serverState.deathZ, player.getYaw(), player.getPitch());
									deathFreeze = true;
									playerFreeze = player;

									MinecraftServer server = player.getServer();
									CommandManager commandManager = server.getCommandManager();
									playerDied = player;
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
												.then(CommandManager.argument("x2", DoubleArgumentType.doubleArg())
														.then(CommandManager
																.argument("y2", DoubleArgumentType.doubleArg())
																.then(CommandManager
																		.argument("z2",
																				DoubleArgumentType.doubleArg())
																		.requires(
																				source -> source.hasPermissionLevel(2))
																		.executes(context -> {
																			int size = zones.size();
																			final var world = context.getSource()
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
																					.getString(context, "Zone Name");
																			StateSaverAndLoader serverState = StateSaverAndLoader
																					.getServerState(world.getServer());
																			StateSaverAndLoader.Zone zone = new StateSaverAndLoader.Zone(
																					zoneName, minX, minY, minZ,
																					maxX, maxY, maxZ);
																			serverState.addZone(zone);
																			zones.put(zone.key, zone);
																			context.getSource().sendFeedback(
																					() -> Text.of("Zone added"), true);
																			return 1;
																		}))))))))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("listZones")
						.requires(source -> source.hasPermissionLevel(2))
						.executes(context -> {
							final var world = context.getSource().getWorld();
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
							for (Map.Entry<String, StateSaverAndLoader.Zone> entry : zones.entrySet()) {
								context.getSource().sendFeedback(() -> Text.of(entry.getKey()), true);
							}
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("clearZones")
						.requires(source -> source.hasPermissionLevel(2))
						.executes(context -> {
							final var world = context.getSource().getWorld();
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
							serverState.clearZones();
							zones.clear();
							context.getSource().sendFeedback(() -> Text.of("Zones cleared"), true);
							return 1;
						})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("deleteZone")
				.then(CommandManager.argument("zone", StringArgumentType.string())
						.requires(source -> source.hasPermissionLevel(2))
						.executes(context -> {
							final var world = context.getSource().getWorld();
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
							final var zone = StringArgumentType.getString(context, "zone");
							if(zones.get(zone) != null) {
								serverState.removeZone(zone);
								zones.remove(zone);
								context.getSource().sendFeedback(() -> Text.of("Zone " + zone + " deleted"), true);
							} else {
								context.getSource().sendFeedback(() -> Text.of("Zone " + zone + " does not exist"), true);
							}
							return 1;
						}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess,
				environment) -> dispatcher.register(literal("unDie")
						.then(CommandManager.argument("player", EntityArgumentType.player())
								.requires(source -> source.hasPermissionLevel(2))
								.executes(context -> {
									deathFreeze = false;
									playerFreeze = null;
									voteTime = false;
									return 1;
								}))));

		ServerTickEvents.START_SERVER_TICK.register(server -> {
			if (deathFreeze) {
				final var world = server.getWorld(server.getOverworld().getRegistryKey());
				StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
				playerFreeze.setPosition(serverState.deathX, serverState.deathY, serverState.deathZ);
				playerFreeze.networkHandler.requestTeleport(serverState.deathX, serverState.deathY, serverState.deathZ,
						playerFreeze.getYaw(), playerFreeze.getPitch());
			}
			if (voteTime) {
				if (counter == 100) {
					final var world = server.getWorld(server.getOverworld().getRegistryKey());
					StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());

					for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
						player.setPosition(serverState.deathX, serverState.deathY, serverState.deathZ);
						player.networkHandler.requestTeleport(serverState.deathX, serverState.deathY,
								serverState.deathZ, player.getYaw(), player.getPitch());
					}
					counter = 0;
				}
				counter++;
			}
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
			Map<String, StateSaverAndLoader.Zone> zones = serverState.getZones();
			if(zones.size() > 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					zoneTracker.checkPlayerZone(player, zones);
				}
			}
		});

	}
}