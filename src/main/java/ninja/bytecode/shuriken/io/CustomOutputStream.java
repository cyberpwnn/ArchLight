/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class CustomOutputStream extends GZIPOutputStream {
    public CustomOutputStream(OutputStream out, int level) throws IOException {
        super(out);
        def.setLevel(level);
    }
}
