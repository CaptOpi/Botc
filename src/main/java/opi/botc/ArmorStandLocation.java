package opi.botc;

import net.minecraft.nbt.NbtCompound;

public class ArmorStandLocation extends Location{
    public String key;
    public String direction;
    
    public ArmorStandLocation(String key, double x, double y, double z, String direction) {
        super(x,y,z);
        this.key = key;
        this.direction = direction;
    }
    public String getKey() {
        return key;
    }
    public String getDirection() {
        return direction;
    }
    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = super.toNbt();
        nbt.putString("key", key);
        nbt.putString("direction", direction);
        return nbt;
    }
    public static ArmorStandLocation fromNbt(NbtCompound nbt) {
        return new ArmorStandLocation(nbt.getString("key"), nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"), nbt.getString("direction"));
    }

}
