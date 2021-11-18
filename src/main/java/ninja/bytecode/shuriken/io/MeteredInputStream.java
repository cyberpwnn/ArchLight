/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import ninja.bytecode.shuriken.math.M;

import java.io.IOException;
import java.io.InputStream;

public class MeteredInputStream extends InputStream {
    private InputStream stream;
    private long readBytes;
    private long timeStart;

    public MeteredInputStream(InputStream stream) {
        this.stream = stream;
        readBytes = 0;
        timeStart = M.ms();
    }

    @Override
    public int read() throws IOException {
        readBytes++;
        return stream.read();
    }

    public void close() throws IOException {
        stream.close();
    }

    public double getBPS() {
        return ((double) readBytes) / (((double) getTimeElapsed()) / 1000D);
    }

    public long getTimeElapsed() {
        return M.ms() - timeStart;
    }

    public long getReadBytes() {
        return readBytes;
    }

    public long getTimeStart() {
        return timeStart;
    }
}
