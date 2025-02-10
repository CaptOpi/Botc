package opi.botc;

import net.minecraft.nbt.NbtCompound;

public class Location {
    private double x,y,z;
    
    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putDouble("x", x);
        nbt.putDouble("y", y);
        nbt.putDouble("z", z);
        return nbt;
    }
    public static Location fromNbt(NbtCompound nbt) {
        return new Location(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
} 
