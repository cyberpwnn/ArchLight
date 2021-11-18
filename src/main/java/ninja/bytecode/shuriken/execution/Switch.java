/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.execution;

public class Switch {
    private volatile boolean b;

    /**
     * Defaulted off
     */
    public Switch() {
        b = false;
    }

    public void flip() {
        b = true;
    }

    public boolean isFlipped() {
        return b;
    }

    public void reset() {
        b = false;
    }
}
