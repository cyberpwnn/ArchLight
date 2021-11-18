/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.execution;

import ninja.bytecode.shuriken.Shuriken;

public class QueueExecutor extends Looper {
    private Queue<Runnable> queue;
    private boolean shutdown;

    public QueueExecutor() {
        queue = new ShurikenQueue<Runnable>();
        Shuriken.profiler.start("executor-" + getId());
        Shuriken.profiler.stop("executor-" + getId());
        shutdown = false;
    }

    public Queue<Runnable> queue() {
        return queue;
    }

    @Override
    protected long loop() {
        Shuriken.profiler.start("executor-" + getId());

        while(queue.hasNext()) {
            try {
                queue.next().run();
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }

        Shuriken.profiler.stop("executor-" + getId());

        if(shutdown && !queue.hasNext()) {
            interrupt();
            return -1;
        }

        return Math.max(500, (long) getRunTime() * 10);
    }

    public double getRunTime() {
        return Shuriken.profiler.getResult("executor-" + getId()).getAverage();
    }

    public void shutdown() {
        shutdown = true;
    }
}
