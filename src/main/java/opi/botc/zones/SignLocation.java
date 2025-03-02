package opi.botc.zones;

import net.minecraft.nbt.NbtCompound;

public class SignLocation{
    public String key;
    public String direction;
    private int x, y, z;
    
    public SignLocation(String key, int x, int y, int z, String direction) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.key = key;
        this.direction = direction;
    }
    public String getKey() {
        return key;
    }
    public String getDirection() {
        return direction;
    }
    public static SignLocation fromNbt(NbtCompound nbt) {
        return new SignLocation(nbt.getString("key"), nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"), nbt.getString("direction"));
    }
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("key", key);
        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("z", z);
        nbt.putString("direction", direction);
        return nbt;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    

}
