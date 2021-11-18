/*
 * Copyright (c) 2005-2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Java Binary patcher (based on bspatch by Colin Percival)
 *
 * @author Joe Desbonnet, jdesbonnet@gmail.com
 */
public class BPatch {
    @SuppressWarnings("resource")
    public static void bspatch(File oldFile, File newFile, File diffFile) throws IOException {
        int oldpos, newpos;
        DataInputStream diffIn = new DataInputStream(new FileInputStream(diffFile));

        // Diff file header. Comprises 4 x 64 bit fields:
        // 0 8 16 24 32 (byte offset)
        // +---------------+---------------+---------------+---------------+
        // | headerMagic | ctrlBlockLen | diffBlockLen | newsize |
        // +---------------+---------------+---------------+---------------+
        // headerMagic: Always "jbdiff40" (8 bytes)
        // ctrlBlockLen: Length of gzip compressed ctrlBlock (64 bit long)
        // diffBlockLen: length of gzip compressed diffBlock (64 bit long)
        // newsize: size of new file in bytes (64 bit long)

        diffIn.readLong();
        long ctrlBlockLen = diffIn.readLong();
        long diffBlockLen = diffIn.readLong();
        int newsize = (int) diffIn.readLong();

        FileInputStream in;
        in = new FileInputStream(diffFile);
        in.skip(ctrlBlockLen + 32);
        GZIPInputStream diffBlockIn = new GZIPInputStream(in);
        in = new FileInputStream(diffFile);
        in.skip(diffBlockLen + ctrlBlockLen + 32);
        GZIPInputStream extraBlockIn = new GZIPInputStream(in);

        /*
         * Read in old file (file to be patched) to oldBuf
         */
        int oldsize = (int) oldFile.length();
        byte[] oldBuf = new byte[oldsize + 1];
        FileInputStream oldIn = new FileInputStream(oldFile);
        BinaryTools.readFromStream(oldIn, oldBuf, 0, oldsize);
        oldIn.close();

        byte[] newBuf = new byte[newsize + 1];

        oldpos = 0;
        newpos = 0;
        int[] ctrl = new int[3];
        while(newpos < newsize) {
            for(int i = 0; i <= 2; i++) {
                ctrl[i] = diffIn.readInt();
                // System.err.println (" ctrl[" + i + "]=" + ctrl[i]);
            }

            if(newpos + ctrl[0] > newsize) {
                System.err.println("Corrupt patch\n");
                return;
            }

            /*
             * Read ctrl[0] bytes from diffBlock stream
             */

            if(!BinaryTools.readFromStream(diffBlockIn, newBuf, newpos, ctrl[0])) {
                System.err.println("error reading from extraIn");
                return;
            }

            for(int i = 0; i < ctrl[0]; i++) {
                if((oldpos + i >= 0) && (oldpos + i < oldsize)) {
                    newBuf[newpos + i] += oldBuf[oldpos + i];
                }
            }

            newpos += ctrl[0];
            oldpos += ctrl[0];

            if(newpos + ctrl[1] > newsize) {
                System.err.println("Corrupt patch");
                return;
            }

            if(!BinaryTools.readFromStream(extraBlockIn, newBuf, newpos, ctrl[1])) {
                System.err.println("error reading from extraIn");
                return;
            }

            newpos += ctrl[1];
            oldpos += ctrl[2];
        }

        diffBlockIn.close();
        extraBlockIn.close();
        diffIn.close();

        FileOutputStream out = new FileOutputStream(newFile);
        out.write(newBuf, 0, newBuf.length - 1);
        out.close();
    }
}