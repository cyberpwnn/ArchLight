/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import java.io.IOException;
import java.io.OutputStream;

public class VoidOutputStream extends OutputStream {
    @Override
    public void write(int b) throws IOException {
        // poof
    }
}
