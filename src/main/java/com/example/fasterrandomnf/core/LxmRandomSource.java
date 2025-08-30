package com.example.fasterrandomnf.core;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.jetbrains.annotations.NotNull;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public final class LxmRandomSource implements RandomSource {
    private static final RandomGeneratorFactory<RandomGenerator> FACTORY =
            RandomGeneratorFactory.of("L64X128MixRandom");

    private RandomGenerator rng;

    public LxmRandomSource(long seed) {
        this.rng = FACTORY.create(seed);
    }

    @Override
    public void setSeed(long seed) {
        this.rng = FACTORY.create(seed);
    }

    @Override public int nextInt() { return rng.nextInt(); }
    @Override public int nextInt(int bound) { return rng.nextInt(bound); }
    @Override public long nextLong() { return rng.nextLong(); }
    @Override public boolean nextBoolean() { return rng.nextBoolean(); }
    @Override public float nextFloat() { return rng.nextFloat(); }
    @Override public double nextDouble() { return rng.nextDouble(); }
    @Override public double nextGaussian() { return rng.nextGaussian(); }

    @Override
    public @NotNull RandomSource fork() {
        return new LxmRandomSource(rng.nextLong());
    }

    @Override
    public net.minecraft.world.level.levelgen.@NotNull PositionalRandomFactory forkPositional() {

        final long salt = rng.nextLong();
        return new net.minecraft.world.level.levelgen.PositionalRandomFactory() {

            @Override
            public @NotNull RandomSource at(int x, int y, int z) {
                return new LxmRandomSource(hashPos(x, y, z) ^ salt);
            }

            @Override
            public @NotNull RandomSource fromSeed(long seed) {
                return new LxmRandomSource(seed ^ salt);
            }

            @Override
            public @NotNull RandomSource fromHashOf(@NotNull String seed) {
                return new LxmRandomSource(hashStr(seed) ^ salt);
            }

            @Override
            public void parityConfigString(@NotNull StringBuilder info) {
                info.append("LXM(salt=").append(salt).append(')');
            }

            // Optional (defaults exist in 1.21.1): delegate to the abstract ones
            @Override
            public @NotNull RandomSource at(net.minecraft.core.@NotNull BlockPos pos) {
                return at(pos.getX(), pos.getY(), pos.getZ());
            }

            @Override
            public @NotNull RandomSource fromHashOf(net.minecraft.resources.@NotNull ResourceLocation id) {
                return fromHashOf(id.toString());
            }
        };
    }

    // FNV-1a 64-bit for positions
    private static long hashPos(int x, int y, int z) {
        long h = 0xcbf29ce484222325L;
        h ^= (x & 0xffffffffL); h *= 0x100000001b3L;
        h ^= (y & 0xffffffffL); h *= 0x100000001b3L;
        h ^= (z & 0xffffffffL); h *= 0x100000001b3L;
        return h;
    }

    // Simple FNV-1a 64-bit for strings
    private static long hashStr(String s) {
        long h = 0xcbf29ce484222325L;
        for (int i = 0; i < s.length(); i++) {
            h ^= (s.charAt(i) & 0xffff);
            h *= 0x100000001b3L;
        }
        return h;
    }

    @Override
    public void consumeCount(int count) {
        for (int i = 0; i < count; i++) rng.nextInt();
    }
}


