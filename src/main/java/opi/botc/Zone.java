package opi.botc;

import net.minecraft.nbt.NbtCompound;

public class Zone {
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