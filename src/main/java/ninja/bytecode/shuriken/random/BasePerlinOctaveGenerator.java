/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.random;

import java.util.Random;

/**
 * Creates perlin noise through unbiased octaves
 */
public class BasePerlinOctaveGenerator extends OctaveGenerator {

    /**
     * Creates a perlin octave generator for the given world
     *
     * @param seed
     *     Seed to construct this generator for
     * @param octaves
     *     Amount of octaves to create
     */
    public BasePerlinOctaveGenerator(long seed, int octaves) {
        this(new Random(seed), octaves);
    }

    /**
     * Creates a perlin octave generator for the given {@link Random}
     *
     * @param rand
     *     Random object to construct this generator for
     * @param octaves
     *     Amount of octaves to create
     */
    public BasePerlinOctaveGenerator(Random rand, int octaves) {
        super(createOctaves(rand, octaves));
    }

    private static NoiseGenerator[] createOctaves(Random rand, int octaves) {
        NoiseGenerator[] result = new NoiseGenerator[octaves];

        for(int i = 0; i < octaves; i++) {
            result[i] = new BasePerlinNoiseGenerator(rand);
        }

        return result;
    }
}
