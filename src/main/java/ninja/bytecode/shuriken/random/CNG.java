/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.random;

import ninja.bytecode.shuriken.collections.KList;

public class CNG {
    public static long hits = 0;
    public static long creates = 0;
    public static final NoiseInjector ADD = (s, v) -> new double[] {s + v, 1};
    public static final NoiseInjector SUBTRACT = (s, v) -> new double[] {s - v < 0 ? 0 : s - v, -1};
    public static final NoiseInjector MULTIPLY = (s, v) -> new double[] {s * v, 0};
    public static final NoiseInjector MAX = (s, v) -> new double[] {Math.max(s, v), 0};
    public static final NoiseInjector MIN = (s, v) -> new double[] {Math.min(s, v), 0};
    private double freq;
    private double amp;
    private double scale;
    private double fscale;
    private final KList<CNG> children;
    private CNG fracture;
    private SimplexNoise generator;
    private final double opacity;
    private NoiseInjector injector;
    private RNG rng;
    private int oct;
    private double power;

    public CNG(RNG random, double opacity, int octaves) {
        creates += octaves;
        this.oct = octaves;
        this.rng = random;
        power = 1;
        freq = 1;
        amp = 1;
        scale = 1;
        fscale = 1;
        children = new KList<>();
        fracture = null;
        generator = new SimplexNoise(random);
        this.opacity = opacity;
        this.injector = ADD;
    }

    public CNG child(CNG c) {
        children.add(c);
        return this;
    }

    @Deprecated
    public RNG nextRNG() {
        return getRNG().nextRNG();
    }

    public RNG getRNG() {
        return rng;
    }

    public CNG fractureWith(CNG c, double scale) {
        fracture = c;
        fscale = scale;
        return this;
    }

    public CNG scale(double c) {
        scale = c;
        return this;
    }

    public CNG freq(double c) {
        freq = c;
        return this;
    }

    public CNG amp(double c) {
        amp = c;
        return this;
    }

    public CNG injectWith(NoiseInjector i) {
        injector = i;
        return this;
    }

    public double noise(double... dim) {
        double f = fracture != null ? (fracture.noise(dim) - 0.5) * fscale : 0D;
        double x = dim.length > 0 ? dim[0] + f : 0D;
        double y = dim.length > 1 ? dim[1] - f : 0D;
        double z = dim.length > 2 ? dim[2] + f : 0D;
        double n = ((generator.noise(
            x * scale,
            y * scale,
            z * scale,
            oct,
            freq,
            amp,
            true) / 2D) + 0.5D) * opacity;
        n = power != 1D ? Math.pow(n, power) : n;
        double m = 1;
        hits += oct;

        for(CNG i : children) {
            double[] r = injector.combine(n, i.noise(dim));
            n = r[0];
            m += r[1];
        }

        return n / m;
    }

    public CNG pow(double power) {
        this.power = power;
        return this;
    }
}
