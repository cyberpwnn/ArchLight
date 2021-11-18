/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.execution;

import ninja.bytecode.shuriken.logging.L;

public abstract class Looper extends Thread {
    public void run() {
        while(!interrupted()) {
            try {
                long m = loop();

                if(m < 0) {
                    break;
                }

                Thread.sleep(m);
            } catch(InterruptedException e) {
                break;
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }

        L.i("Thread " + getName() + " Shutdown.");
    }

    protected abstract long loop();
}
