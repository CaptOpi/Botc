package opi.botc;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ZoneTracker {
    private final Map<UUID, String> playerCurrentZones = new HashMap<>();
    private final Map<String, Set<UUID>> zonePlayers = new HashMap<>();

    public void checkPlayerZone(ServerPlayerEntity player, Map<String, StateSaverAndLoader.Zone> zones) {
        UUID uuid = player.getUuid();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String currentZoneKey = null;
        for (StateSaverAndLoader.Zone zone : zones.values()) {
            if (zone.contains(x, y, z)) {
                currentZoneKey = zone.key;
                break;
            }
        }

        String previousZoneKey = playerCurrentZones.get(uuid);
        if (!Objects.equals(previousZoneKey, currentZoneKey)) {
            if (previousZoneKey != null) {
                notifyPlayersInZone(previousZoneKey, Text.literal(player.getName().getString() + " exited the " + previousZoneKey).setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(0xFF0000))));
                zonePlayers.getOrDefault(previousZoneKey, new HashSet<>()).remove(uuid);
                String command = "execute as " + player.getName().getString() + " run voicechat leave";
                BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(command, BloodOfTheClocktower.server.getCommandSource()), command);
            }
            if (currentZoneKey != null) {
                notifyPlayersInZone(currentZoneKey, Text.literal(player.getName().getString() + " entered the " + currentZoneKey).setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(0xFF0000))));
                zonePlayers.computeIfAbsent(currentZoneKey, k -> new HashSet<>()).add(uuid);
                String command = "execute as " + player.getName().getString() + " run voicechat join " + currentZoneKey;
                BloodOfTheClocktower.server.getCommandManager().execute(BloodOfTheClocktower.server.getCommandManager().getDispatcher().parse(command, BloodOfTheClocktower.server.getCommandSource()), command);
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
