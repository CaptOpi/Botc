package opi.botc.utils;

import java.util.*;

import org.slf4j.Logger;

import net.minecraft.server.network.ServerPlayerEntity;
import opi.botc.zones.ColorLocation;

public class Randomize {
    private final Map<String, ColorLocation> colorLocations = new HashMap<>();
    private final Map<UUID, Colors> playerColors = new HashMap<>();
    private Logger LOGGER = null;

    public Randomize(Map<String, ColorLocation> colorLocations, Logger LOGGER) {
        this.colorLocations.putAll(colorLocations);
        this.LOGGER = LOGGER;
    }

    public void randomize(List<ServerPlayerEntity> players) {
        List<ServerPlayerEntity> nonOpPlayers = new ArrayList<>();
        Iterator<ServerPlayerEntity> iterator = players.iterator();
        while (iterator.hasNext()) {
            ServerPlayerEntity player = iterator.next();
            if (!player.hasPermissionLevel(2)) {
                nonOpPlayers.add(player);
            }
        }
        
        List<Colors> availableColors = new ArrayList<>(Arrays.asList(Colors.values()));
        Collections.shuffle(availableColors);

        int index = 0;
        if(players.size() > availableColors.size()) {
            LOGGER.warn("Too many players. Max is 8, but there are " + players.size() + " players.");
            return;
        }
        for (ServerPlayerEntity nonOp  : nonOpPlayers) {
            playerColors.put(nonOp.getUuid(), availableColors.get(index));
            index++;
        }
    }

    public Map<String, ColorLocation> getColorLocations() {
        return colorLocations;
    }

    public Map<UUID, Colors> getPlayerColors() {
        return playerColors;
    }

    public void putAllColorLocations(Map<String, ColorLocation> colorLocations) {
        this.colorLocations.clear();
        this.colorLocations.putAll(colorLocations);
    }

    public void clearPlayerColors() {
        playerColors.clear();
    }
}
