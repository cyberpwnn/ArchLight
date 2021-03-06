/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import java.io.IOException;
import java.io.OutputStream;

public class MirroredOutputStream extends OutputStream {
    private OutputStream[] streams;
    private int i;

    public MirroredOutputStream(OutputStream... s) {
        this.streams = s;
        i = 0;
    }

    @Override
    public void write(int b) throws IOException {
        for(i = 0; i < streams.length; i++) {
            streams[i].write(b);
        }
    }

    public void close() throws IOException {
        for(i = 0; i < streams.length; i++) {
            streams[i].close();
        }
    }
}
