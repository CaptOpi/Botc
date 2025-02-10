package opi.botc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.MinecraftServer;
import opi.botc.Colors;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class StateSaverAndLoader extends PersistentState {

    public double deathX = 0;
    public double deathY = 0;
    public double deathZ = 0;

    private final Map<String, Zone> zones = new HashMap<>();

    private final Map<String, ArmorStandLocation> armorStandLocations = new HashMap<>();

    private final Map<String, ColorLocation> colorLocations = new HashMap<>();

    public void addColorLocation(String key, ColorLocation colorLocation) {
        colorLocations.put(key, colorLocation);
        markDirty();
    }
    public void removeColorLocation(String key) {
        colorLocations.remove(key);
        markDirty();
    }
    public void clearColorLocations() {
        colorLocations.clear();
        markDirty();
    }
    public Map<String, ColorLocation> getColorLocations() {
        return colorLocations;
    }
    public void addArmorStandLocation(String key, ArmorStandLocation armorStandLocation) {
        armorStandLocations.put(key, armorStandLocation);
        markDirty();
    }
    public void removeArmorStandLocation(String key) {
        armorStandLocations.remove(key);
        markDirty();
    }
    public void clearArmorStandLocations() {
        armorStandLocations.clear();
        markDirty();
    }
    public Map<String, ArmorStandLocation> getArmorStandLocations() {
        return armorStandLocations;
    }
    public void addZone(Zone zone) {
        zones.put(zone.key, zone);
        markDirty();
    }
    public Zone getZone(String key) {
        return zones.get(key);
    }
    public void removeZone(String key) {
        zones.remove(key);
        markDirty();
    }
    public void clearZones() {
        zones.clear();
        markDirty();
    }
    public Map<String, Zone> getZones() {
        return zones;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup registries) {
        nbt.putDouble("deathX", deathX);
        nbt.putDouble("deathY", deathY);
        nbt.putDouble("deathZ", deathZ);

        NbtList zoneList = new NbtList();
        for (Zone zone : zones.values()) {
            zoneList.add(zone.toNbt());
        }

        NbtList armorStandLocationList = new NbtList();
        for(ArmorStandLocation armorStandLocation : armorStandLocations.values()) {
            armorStandLocationList.add(armorStandLocation.toNbt());
        }
        NbtList colorLocationList = new NbtList();
        for (Map.Entry<String, ColorLocation> entry : colorLocations.entrySet()) {
            NbtCompound colorLocation = new NbtCompound();
            colorLocation.putString("key", entry.getKey());
            colorLocation.putString("color", entry.getValue().color.toString());
            colorLocation.put("location", entry.getValue().toNbt());
            colorLocationList.add(colorLocation);
        }
        nbt.put("colorLocations", colorLocationList);
        nbt.put("zones", zoneList);
        nbt.put("armorStandLocation", armorStandLocationList);
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        state.deathX = tag.getDouble("deathX");
        state.deathY = tag.getDouble("deathY");
        state.deathZ = tag.getDouble("deathZ");

        NbtList zoneList = tag.getList("zones", 10);
        for (int i = 0; i < zoneList.size(); i++) {
            Zone zone = Zone.fromNbt(zoneList.getCompound(i));
            state.addZone(zone);
        }
        
        NbtList armorStandLocationList = tag.getList("armorStandLocation", 10);
        for (int i = 0; i < armorStandLocationList.size(); i++) {
            ArmorStandLocation armorStandLocation = ArmorStandLocation.fromNbt(armorStandLocationList.getCompound(i));
            state.addArmorStandLocation(armorStandLocation.getKey(), armorStandLocation);
        }

        if (tag.contains("colorLocations", 9)) {
            NbtList colorLocationList = tag.getList("colorLocations", 10);
            for (int i = 0; i < colorLocationList.size(); i++) {
                NbtCompound colorLocationCompound = colorLocationList.getCompound(i);
                String key = colorLocationCompound.getString("key");
                String colorString = colorLocationCompound.getString("color");
                Colors color = Colors.fromString(colorString);
                NbtCompound locNbt = colorLocationCompound.getCompound("location");
                Location location = Location.fromNbt(locNbt);
                state.addColorLocation(key, new ColorLocation(color, location.getX(), location.getY(), location.getZ()));
            }
        }
        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null);

    public static StateSaverAndLoader getServerState(MinecraftServer server) {

        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, BloodOfTheClocktower.MOD_ID);
        state.markDirty();

        return state;
    }
}