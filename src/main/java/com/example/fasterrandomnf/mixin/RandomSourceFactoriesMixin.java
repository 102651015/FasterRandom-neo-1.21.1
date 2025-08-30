package com.example.fasterrandomnf.mixin;

import com.example.fasterrandomnf.core.LxmRandomSource;
import net.minecraft.util.RandomSource;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Mixin into the interface RandomSource -> this mixin itself MUST be an interface. */
@Mixin(RandomSource.class)
public interface RandomSourceFactoriesMixin {
    @Inject(method = "create()Lnet/minecraft/util/RandomSource;",
            at = @At("HEAD"), cancellable = true)
    private static void fr$create(CallbackInfoReturnable<RandomSource> cir) {
        cir.setReturnValue(new LxmRandomSource(System.nanoTime() ^ Thread.currentThread().threadId()));
    }

    @Inject(method = "create(J)Lnet/minecraft/util/RandomSource;",
            at = @At("HEAD"), cancellable = true)
    private static void fr$createSeeded(long seed, CallbackInfoReturnable<RandomSource> cir) {
        Log.debug("[FR-NF] Hooked RandomSource.create(long), seed=" + seed);
        cir.setReturnValue(new LxmRandomSource(seed));
    }

    @Inject(method = "createThreadSafe()Lnet/minecraft/util/RandomSource;",
            at = @At("HEAD"), cancellable = true)
    private static void fr$createThreadSafe(CallbackInfoReturnable<RandomSource> cir) {
        cir.setReturnValue(new LxmRandomSource(System.nanoTime() ^ Thread.currentThread().threadId()));
    }

    // 建议一并拦下：线程本地实例工厂（名字以 IDE 里看到的签名为准）
    @Inject(method = "createNewThreadLocalInstance()Lnet/minecraft/util/RandomSource;",
            at = @At("HEAD"), cancellable = true)
    private static void fr$createLocal(CallbackInfoReturnable<RandomSource> cir) {
        cir.setReturnValue(new LxmRandomSource(System.nanoTime() ^ Thread.currentThread().threadId()));
    }


}



