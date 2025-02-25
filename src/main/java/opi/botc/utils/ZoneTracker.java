package opi.botc.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import opi.botc.BloodOfTheClocktower;
import opi.botc.zones.ArmorStandLocation;
import opi.botc.zones.Zone;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


public class ZoneTracker {
    private final Map<UUID, String> playerCurrentZones = new HashMap<>();
    private final Map<String, Set<UUID>> zonePlayers = new HashMap<>();

    private final Set<String> armorStandSpawned = new HashSet<>();

    public void checkPlayerZone(ServerPlayerEntity player, Map<String, Zone> zones, Map<String, ArmorStandLocation> locations) {
        UUID uuid = player.getUuid();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String currentZoneKey = null;
        for (Zone zone : zones.values()) {
            if (zone.contains(x, y, z)) {
                currentZoneKey = zone.key;
                break;
            }
        }
        String previousZoneKey = playerCurrentZones.get(uuid);
        if (!Objects.equals(previousZoneKey, currentZoneKey)) {
            if (previousZoneKey != null) {
                if(!player.hasPermissionLevel(2)) {
                    notifyPlayersInZone(previousZoneKey, Text.literal(player.getName().getString() + " exited the " + previousZoneKey).setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(0xFF0000))));
                }
                zonePlayers.getOrDefault(previousZoneKey, new HashSet<>()).remove(uuid);
                String command = "execute as " + player.getName().getString() + " run voicechat leave";
                BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(command, BloodOfTheClocktower.server.getCommandSource()), command);
                String command1 = "execute as " + player.getName().getString() + " run team leave " + player.getName().getString();
                BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(command1, BloodOfTheClocktower.server.getCommandSource()), command1);
                command1 = "execute as " + player.getName().getString() + " run team join " + "Town";
                BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(command1, BloodOfTheClocktower.server.getCommandSource()), command1);
                Set<UUID> remaining = zonePlayers.get(previousZoneKey);
                if (remaining != null && remaining.isEmpty()) {
                    if(armorStandSpawned.contains(previousZoneKey) && locations.containsKey(previousZoneKey)) {
                        String killCmd = "kill @e[type=armor_stand,tag=" + previousZoneKey + "]";
                        BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(killCmd, BloodOfTheClocktower.server.getCommandSource()), killCmd);
                        armorStandSpawned.remove(previousZoneKey);
                    }
                }
            }
            if (currentZoneKey != null) {
                if(!player.hasPermissionLevel(2)) {
                    notifyPlayersInZone(currentZoneKey, Text.literal(player.getName().getString() + " entered the " + currentZoneKey).setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(0xFF0000))));
                }
                zonePlayers.computeIfAbsent(currentZoneKey, k -> new HashSet<>()).add(uuid);
                String command = "execute as " + player.getName().getString() + " run voicechat join " + currentZoneKey;
                BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(command, BloodOfTheClocktower.server.getCommandSource()), command);
                String command1 = "execute as " + player.getName().getString() + " run team join " + currentZoneKey;
                BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(command1, BloodOfTheClocktower.server.getCommandSource()), command1);
                Set<UUID> playersInZone = zonePlayers.get(currentZoneKey);
                if (playersInZone.size() == 1 && !armorStandSpawned.contains(currentZoneKey) && locations.containsKey(currentZoneKey)) {
                    String spawnCmd = "summon minecraft:armor_stand " + locations.get(currentZoneKey).getX() + " " + locations.get(currentZoneKey).getY() + " " + locations.get(currentZoneKey).getZ() + " {ShowArms:1b,Invisible:1b,NoBasePlate:1b,NoGravity:1b,Rotation:[" + locations.get(currentZoneKey).getDirection() + "f" + ",0f],ArmorItems:[{},{},{},{id:barrier,count:1}],ArmorDropChances:[0f,0f,0f,0f],Tags:[" + currentZoneKey + "],DisabledSlots:4144959}";
                    BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(spawnCmd, BloodOfTheClocktower.server.getCommandSource()), spawnCmd);
                    armorStandSpawned.add(currentZoneKey);
                }
            }
            playerCurrentZones.put(uuid, currentZoneKey);
        }
    }

    private void notifyPlayersInZone(String zoneKey, Text message) {
        Set<UUID> playersInZone = zonePlayers.get(zoneKey);
        if (playersInZone != null) {
            for (UUID playerId : playersInZone) {
                ServerPlayerEntity player = BloodOfTheClocktower.server.getPlayerManager().getPlayer(playerId);
                if (player != null) {
                    player.sendMessage(message, false);
                }
            }
        }
    }
}
