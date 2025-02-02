package opi.botc;

import net.minecraft.nbt.NbtCompound;
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


    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup registries) {
        nbt.putDouble("deathX", deathX);
        nbt.putDouble("deathY", deathY);
        nbt.putDouble("deathZ", deathZ);
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        state.deathX = tag.getInt("deathX");
        state.deathY = tag.getInt("deathY");
        state.deathZ = tag.getInt("deathZ");
        return state;
    }
     private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new, // If there's no 'StateSaverAndLoader' yet create one
            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );
 
    public static StateSaverAndLoader getServerState(MinecraftServer server) {

        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, BloodOfTheClocktower.MOD_ID);
        state.markDirty();
 
        return state;
    }
}