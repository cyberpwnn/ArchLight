/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.collections;


/**
 * Represents a number that can be finalized and be changed
 *
 * @author cyberpwn
 */
public class FDouble {
    private double i;

    /**
     * Create a final number
     *
     * @param i
     *     the initial number
     */
    public FDouble(double i) {
        this.i = i;
    }

    /**
     * Get the value
     *
     * @return the number value
     */
    public double get() {
        return i;
    }

    /**
     * Set the value
     *
     * @param i
     *     the number value
     */
    public void set(double i) {
        this.i = i;
    }

    /**
     * Add to this value
     *
     * @param i
     *     the number to add to this value (value = value + i)
     */
    public void add(double i) {
        this.i += i;
    }

    /**
     * Subtract from this value
     *
     * @param i
     *     the number to subtract from this value (value = value - i)
     */
    public void sub(double i) {
        this.i -= i;
    }
}
