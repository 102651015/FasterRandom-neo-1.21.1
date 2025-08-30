package com.example.fasterrandomnf.mixin;

import com.example.fasterrandomnf.core.LxmRandomSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(Entity.class)
public abstract class EntityRandomPatch {
    @Shadow @Final @Mutable
    protected RandomSource random;
    @Unique
    private static boolean logged = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void fr$swapEntityRandom(CallbackInfo ci) {
        this.random = new LxmRandomSource(System.nanoTime() ^ Thread.currentThread().threadId());
        if (!logged) {
            Log.debug("[FR-NF] Entity.random = " + this.random.getClass().getName());
            logged = true;
        }
    }
}


