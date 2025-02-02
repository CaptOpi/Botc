package opi.botc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.Portal.Effect;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public class BloodOfTheClocktowerClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BloodOfTheClocktowerClient.class);
    @Override
    public void onInitializeClient() {

    }
}