/*
 * Copyright (c) 2005-2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Joe Desbonnet, jdesbonnet@gmail.com, cyberpwn
 */
public class BinaryTools {
    public static void patch(File oldIn, File diffIn, File newOut) throws IOException {
        BPatch.bspatch(oldIn, newOut, diffIn);
    }

    public static void diff(File oldIn, File newIn, File diffOut) throws IOException {
        BDiff.bsdiff(oldIn, newIn, diffOut);
    }

    /**
     * Equiv of C library memcmp().
     *
     * @param s1
     * @param s1offset
     * @param s2
     * @param n
     * @return
     */
    /*
     * public final static int memcmp(byte[] s1, int s1offset, byte[] s2, int
     * s2offset, int n) {
     *
     * if ((s1offset + n) > s1.length) { n = s1.length - s1offset; } if ((s2offset +
     * n) > s2.length) { n = s2.length - s2offset; } for (int i = 0; i < n; i++) {
     * if (s1[i + s1offset] != s2[i + s2offset]) { return s1[i + s1offset] < s2[i +
     * s2offset] ? -1 : 1; } }
     *
     * return 0; }
     */

    /**
     * Equiv of C library memcmp().
     */
    public final static int memcmp(byte[] s1, int s1offset, byte[] s2, int s2offset) {
        int n = s1.length - s1offset;

        if(n > (s2.length - s2offset)) {
            n = s2.length - s2offset;
        }
        for(int i = 0; i < n; i++) {
            if(s1[i + s1offset] != s2[i + s2offset]) {
                return s1[i + s1offset] < s2[i + s2offset] ? -1 : 1;
            }
        }

        return 0;
    }

    public static final boolean readFromStream(InputStream in, byte[] buf, int offset, int len) throws IOException {
        int totalBytesRead = 0;
        int nbytes;

        while(totalBytesRead < len) {
            nbytes = in.read(buf, offset + totalBytesRead, len - totalBytesRead);
            if(nbytes < 0) {
                System.err.println("readFromStream(): returning prematurely. Read " + totalBytesRead + " bytes");
                return false;
            }
            totalBytesRead += nbytes;
        }

        return true;
    }
}