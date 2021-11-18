/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.bench;

import ninja.bytecode.shuriken.math.M;

/**
 * Timer
 *
 * @author cyberpwn
 */
public class Timer {
    private long tns;
    private long cns;

    /**
     * Create a new timer
     */
    public Timer() {
        tns = 0;
        cns = 0;
    }

    /**
     * Start the timer
     */
    public void start() {
        cns = M.ns();
    }

    /**
     * Stop the timer
     */
    public void stop() {
        tns = M.ns() - cns;
        cns = M.ns();
    }

    /**
     * Get time in nanoseconds
     *
     * @return the time
     */
    public long getTime() {
        return tns;
    }

    /**
     * Get the time in nanoseconds the timer last stopped
     */
    public long getLastRun() {
        return cns;
    }
}