/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import ninja.bytecode.shuriken.random.RNG;

import java.io.IOException;
import java.io.InputStream;

public class RandomInputStream extends InputStream {
    private RNG rng;

    public RandomInputStream(String seed) {
        rng = new RNG(seed);
    }

    public RandomInputStream(long seed) {
        rng = new RNG(seed);
    }

    public RandomInputStream() {
        rng = new RNG();
    }

    @Override
    public int read() throws IOException {
        return (int) (rng.imax() % 256);
    }
}
