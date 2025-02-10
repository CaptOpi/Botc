package opi.botc;

import java.util.*;

import net.minecraft.server.network.ServerPlayerEntity;

public class Randomize {
    private final Map<String, ColorLocation> colorLocations = new HashMap<>();
    private final Map<UUID, Colors> playerColors = new HashMap<>();

    public Randomize(Map<String, ColorLocation> colorLocations) {
        this.colorLocations.putAll(colorLocations);
    }

    public void randomize(List<ServerPlayerEntity> players) {
        if (players.size() > Colors.values().length) {
            return; 
        }

        
        List<Colors> availableColors = new ArrayList<>(Arrays.asList(Colors.values()));
        Collections.shuffle(availableColors);

        int index = 0;
        for (ServerPlayerEntity player : players) {
            if (player.hasPermissionLevel(3)) {
                continue; 
            }
            
            playerColors.put(player.getUuid(), availableColors.get(index));
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
