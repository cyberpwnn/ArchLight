/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.random;

@FunctionalInterface
public interface NoiseInjector {
    public double[] combine(double src, double value);
}
