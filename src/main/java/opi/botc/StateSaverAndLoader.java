package opi.botc;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class StateSaverAndLoader extends PersistentState {

    public double deathX = 0;
    public double deathY = 0;
    public double deathZ = 0;

    public static class Zone {
        public String key;
        public double minX, minY, minZ;
        public double maxX, maxY, maxZ;

        public Zone(String key, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.key = key;
            this.minX = Math.min(minX, maxX);
            this.maxX = Math.max(minX, maxX);
            this.minY = Math.min(minY, maxY);
            this.maxY = Math.max(minY, maxY);
            this.minZ = Math.min(minZ, maxZ);
            this.maxZ = Math.max(minZ, maxZ);
        }

        public boolean contains(double x, double y, double z) {
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }

        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("key", key);
            nbt.putDouble("minX", minX);
            nbt.putDouble("minY", minY);
            nbt.putDouble("minZ", minZ);
            nbt.putDouble("maxX", maxX);
            nbt.putDouble("maxY", maxY);
            nbt.putDouble("maxZ", maxZ);
            return nbt;
        }

        public static Zone fromNbt(NbtCompound nbt) {
            return new Zone(nbt.getString("key"), nbt.getDouble("minX"), nbt.getDouble("minY"), nbt.getDouble("minZ"),
                    nbt.getDouble("maxX"), nbt.getDouble("maxY"), nbt.getDouble("maxZ"));
        }
    }

    private final Map<String, Zone> zones = new HashMap<>();

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
        nbt.put("zones", zoneList);
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        state.deathX = tag.getInt("deathX");
        state.deathY = tag.getInt("deathY");
        state.deathZ = tag.getInt("deathZ");

        NbtList zoneList = tag.getList("zones", 10);
        for (int i = 0; i < zoneList.size(); i++) {
            Zone zone = Zone.fromNbt(zoneList.getCompound(i));
            state.addZone(zone);
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