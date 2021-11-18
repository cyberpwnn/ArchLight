/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.random;

import ninja.bytecode.shuriken.math.M;

import java.util.function.Function;

/**
 * Generates cell like regions. You can get the edge value for border detection
 *
 * @author cyberpwn
 */
public class SimplexCellGenerator {
    private CNG[] gen;
    private int possibilities;

    /**
     * Cell Gen
     *
     * @param rng
     *     the RNG object for noise
     * @param possibilities
     *     the amount of possible cell types (think colors)
     * @param scale
     *     the scale of this generator (size of simplex cells)
     * @param octaves
     *     the octaves in each simplex generator. Scale with ease
     * @param factory
     *     the factory for modifying each simplex generator as they are
     *     created. Great for fracturing or applying additional CN generators
     *     to each.
     */
    public SimplexCellGenerator(RNG rng, int possibilities, double scale, int octaves, Function<CNG, CNG> factory) {
        this.possibilities = possibilities;
        gen = new CNG[possibilities];

        for(int i = 0; i < possibilities; i++) {
            gen[i] = new CNG(rng.nextParallelRNG(i), 1D, 1).scale(scale);
            gen[i] = factory.apply(gen[i]);
        }
    }

    /**
     * Get the edge value. The closer to 0.5, the closer the edge.
     *
     * @param dim
     *     the coords
     * @return the edge value
     */
    public double getEdge(double... dim) {
        double b = 0;

        for(int i = 0; i < gen.length; i++) {
            double g = gen[i].noise(dim);

            if(g > b) {
                b = g;
            }
        }

        return b;
    }

    /**
     * Get the selected index for this pos
     *
     * @param dim
     *     the pos
     * @return the index
     */
    public int getIndex(double... dim) {
        double b = 0;
        int index = 0;

        for(int i = 0; i < gen.length; i++) {
            double g = gen[i].noise(dim);

            if(g > b) {
                b = g;
                index = i;
            }
        }

        return index % possibilities;
    }

    /**
     * Check if there is a border nearby
     *
     * @param checks
     *     the number of evenly distributed rays to scatter and check for a
     *     different index than the current one
     * @param distance
     *     the distance for each ray to check
     * @param dims
     *     the position (ONLY SUPPORTS 2 / 3 DIMENSIONAL CHECKS)
     * @return true if a ray-check found another index
     */
    public boolean hasBorder(int checks, double distance, double... dims) {
        if(checks <= 0) {
            throw new RuntimeException("Checks must be above 0");
        }

        int current = getIndex(dims);
        double ajump = 360D / (double) checks;

        if(dims.length == 2) {
            for(int i = 0; i < checks; i++) {
                double dx = M.sin((float) Math.toRadians(ajump * i));
                double dz = M.cos((float) Math.toRadians(ajump * i));
                if(current != getIndex((dx * distance) + dims[0], (dz * distance) + dims[1])) {
                    return true;
                }
            }
        } else if(dims.length == 3) {
            for(int i = 0; i < checks; i++) {
                double dx = M.sin((float) Math.toRadians(ajump * i));
                double dz = M.cos((float) Math.toRadians(ajump * i));
                double dy = -M.sin((float) Math.toRadians(ajump * i));
                if(current != getIndex((dx * distance) + dims[0], (dz * distance) + dims[1], (dy * distance) + dims[2])) {
                    return true;
                }
            }
        } else {
            throw new RuntimeException("Only dims 2 and 3 are supported. Not " + dims.length);
        }

        return false;
    }
}
