package com.example.fasterrandomnf;

import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.utils.Log;

import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Mod("fasterrandom_nf")
public final class FasterRandomNF {
    public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("frnf.debug", "false"));
    public static final Logger LOG = LogManager.getLogger("fasterrandom_nf");

    // Feature flag: whether LXM family exists in the current JRE.
    public static final boolean LXM_AVAILABLE;

    private static final Set<String> LXM_NAMES = Set.of(
            "L32X64MixRandom", "L64X128MixRandom", "L64X256MixRandom", "L128X256MixRandom"
    );

    static {
        boolean ok;
        try {
            // Prefer checking by concrete algorithm names
            ok = RandomGeneratorFactory.all()
                    .map(RandomGeneratorFactory::name)
                    .anyMatch(LXM_NAMES::contains);

            // Fallback: check by group name "LXM"
            if (!ok) {
                ok = RandomGeneratorFactory.all()
                        .map(RandomGeneratorFactory::group)
                        .anyMatch(g -> "LXM".equalsIgnoreCase(g));
            }
        } catch (Throwable t) {
            ok = false;
        }

        LXM_AVAILABLE = ok;
        if (ok) {
            LOG.info("[FasterRandom-NF] Using JDK LXM RandomGenerator backend.");
        } else {
            LOG.warn("[FasterRandom-NF] LXM RandomGenerator not available. Mod will be inert.");
        }
    }

    public FasterRandomNF() {
        if (DEBUG) {
            var rs = net.minecraft.util.RandomSource.create(42L);
            LOG.debug("[FR-NF] RandomSource impl = {}", rs.getClass().getName());
        }
    }
}
