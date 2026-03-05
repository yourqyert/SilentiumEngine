package net.silentium.engine.api.utils.math;

import java.util.Random;

public final class RandomUtil {

    private RandomUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Random RANDOM = new Random();

    public static double getRandom(double min, double max) {
        return min + (max - min) * RANDOM.nextDouble();
    }

}
