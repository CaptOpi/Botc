package opi.botc;

import net.minecraft.nbt.NbtCompound;

public class ColorLocation  extends Location{
    public Colors color;

    public ColorLocation(Colors color, double x, double y, double z) {
        super(x,y,z);
        this.color = color;
    }
    public Colors getColor() {
        return color;
    }
    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = super.toNbt();
        nbt.putString("color", color.name());
        return nbt;
    }
    public static ColorLocation fromNbt(NbtCompound nbt) {
        return new ColorLocation(Colors.valueOf(nbt.getString("color")), nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
}
