/*
 * Copyright (c) 2005-2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPOutputStream;

/**
 * Java Binary Diff utility. Based on bsdiff (v4.2) by Colin Percival (see
 * http://www.daemonology.net/bsdiff/ ) and distributed under BSD license.
 *
 * <p>
 * Running this on large files may require an increase of the default maximum
 * heap size (use java -Xmx200m)
 * </p>
 *
 * @author Joe Desbonnet, jdesbonnet@gmail.com
 */

public class BDiff {
    private static final int min(int x, int y) {
        return x < y ? x : y;
    }

    private final static void split(int[] I, int[] V, int start, int len, int h) {

        int i, j, k, x, tmp, jj, kk;

        if(len < 16) {
            for(k = start; k < start + len; k += j) {
                j = 1;
                x = V[I[k] + h];
                for(i = 1; k + i < start + len; i++) {
                    if(V[I[k + i] + h] < x) {
                        x = V[I[k + i] + h];
                        j = 0;
                    }

                    if(V[I[k + i] + h] == x) {
                        tmp = I[k + j];
                        I[k + j] = I[k + i];
                        I[k + i] = tmp;
                        j++;
                    }

                }

                for(i = 0; i < j; i++)
                    V[I[k + i]] = k + j - 1;
                if(j == 1)
                    I[k] = -1;
            }

            return;
        }

        x = V[I[start + len / 2] + h];
        jj = 0;
        kk = 0;
        for(i = start; i < start + len; i++) {
            if(V[I[i] + h] < x)
                jj++;
            if(V[I[i] + h] == x)
                kk++;
        }

        jj += start;
        kk += jj;

        i = start;
        j = 0;
        k = 0;
        while(i < jj) {
            if(V[I[i] + h] < x) {
                i++;
            } else if(V[I[i] + h] == x) {
                tmp = I[i];
                I[i] = I[jj + j];
                I[jj + j] = tmp;
                j++;
            } else {
                tmp = I[i];
                I[i] = I[kk + k];
                I[kk + k] = tmp;
                k++;
            }

        }

        while(jj + j < kk) {
            if(V[I[jj + j] + h] == x) {
                j++;
            } else {
                tmp = I[jj + j];
                I[jj + j] = I[kk + k];
                I[kk + k] = tmp;
                k++;
            }

        }

        if(jj > start) {
            split(I, V, start, jj - start, h);
        }

        for(i = 0; i < kk - jj; i++) {
            V[I[jj + i]] = kk - 1;
        }

        if(jj == kk - 1) {
            I[jj] = -1;
        }

        if(start + len > kk) {
            split(I, V, kk, start + len - kk, h);
        }

    }

    /**
     * Fast suffix sorting (qsufsort). See paper "Faster Suffix Sorting" by N.
     * Jesper Larsson, Kunihiko Sadakane. http://www.larsson.dogma.net/research.html
     */
    private static void qsufsort(int[] I, int[] V, byte[] oldBuf) {

        int oldsize = oldBuf.length;

        int[] buckets = new int[256];
        int i, h, len;

        for(i = 0; i < 256; i++) {
            buckets[i] = 0;
        }

        for(i = 0; i < oldsize; i++) {
            buckets[(int) oldBuf[i] & 0xff]++;
        }

        for(i = 1; i < 256; i++) {
            buckets[i] += buckets[i - 1];
        }

        for(i = 255; i > 0; i--) {
            buckets[i] = buckets[i - 1];
        }

        buckets[0] = 0;

        for(i = 0; i < oldsize; i++) {
            I[++buckets[(int) oldBuf[i] & 0xff]] = i;
        }

        I[0] = oldsize;
        for(i = 0; i < oldsize; i++) {
            V[i] = buckets[(int) oldBuf[i] & 0xff];
        }
        V[oldsize] = 0;

        for(i = 1; i < 256; i++) {
            if(buckets[i] == buckets[i - 1] + 1) {
                I[buckets[i]] = -1;
            }
        }

        I[0] = -1;

        for(h = 1; I[0] != -(oldsize + 1); h += h) {
            len = 0;
            for(i = 0; i < oldsize + 1; ) {
                if(I[i] < 0) {
                    len -= I[i];
                    i -= I[i];
                } else {
                    // if(len) I[i-len]=-len;
                    if(len != 0) {
                        I[i - len] = -len;
                    }
                    len = V[I[i]] + 1 - i;
                    split(I, V, i, len, h);
                    i += len;
                    len = 0;
                }

            }

            if(len != 0) {
                I[i - len] = -len;
            }
        }

        for(i = 0; i < oldsize + 1; i++) {
            I[V[i]] = i;
        }
    }

    /**
     * Count the number of bytes that match in oldBuf (starting at offset oldOffset)
     * and newBuf (starting at offset newOffset).
     */
    private final static int matchlen(byte[] oldBuf, int oldOffset, byte[] newBuf, int newOffset) {
        int end = min(oldBuf.length - oldOffset, newBuf.length - newOffset);
        int i;
        for(i = 0; i < end; i++) {
            if(oldBuf[oldOffset + i] != newBuf[newOffset + i]) {
                break;
            }
        }
        return i;
    }

    private final static int search(int[] I, byte[] oldBuf, byte[] newBuf, int newBufOffset, int start, int end, IntByRef pos) {
        int x, y;

        if(end - start < 2) {
            x = matchlen(oldBuf, I[start], newBuf, newBufOffset);
            y = matchlen(oldBuf, I[end], newBuf, newBufOffset);

            if(x > y) {
                pos.value = I[start];
                return x;
            } else {
                pos.value = I[end];
                return y;
            }
        }

        x = start + (end - start) / 2;
        if(BinaryTools.memcmp(oldBuf, I[x], newBuf, newBufOffset) < 0) {
            return search(I, oldBuf, newBuf, newBufOffset, x, end, pos);
        } else {
            return search(I, oldBuf, newBuf, newBufOffset, start, x, pos);
        }

    }

    public static void bsdiff(File oldFile, File newFile, File diffFile) throws IOException {

        int oldsize = (int) oldFile.length();
        byte[] oldBuf = new byte[oldsize];

        FileInputStream in = new FileInputStream(oldFile);
        BinaryTools.readFromStream(in, oldBuf, 0, oldsize);
        in.close();

        int[] I = new int[oldsize + 1];
        int[] V = new int[oldsize + 1];

        qsufsort(I, V, oldBuf);

        // free(V)
        V = null;
        System.gc();

        int newsize = (int) newFile.length();
        byte[] newBuf = new byte[newsize];
        in = new FileInputStream(newFile);
        BinaryTools.readFromStream(in, newBuf, 0, newsize);
        in.close();

        // diff block
        int dblen = 0;
        byte[] db = new byte[newsize];

        // extra block
        int eblen = 0;
        byte[] eb = new byte[newsize];

        /*
         * Diff file is composed as follows:
         *
         * Header (32 bytes) Data (from offset 32 to end of file)
         *
         * Header: Offset 0, length 8 bytes: file magic "jbdiff40" Offset 8, length 8
         * bytes: length of ctrl block Offset 16, length 8 bytes: length of compressed
         * diff block Offset 24, length 8 bytes: length of new file
         *
         * Data: 32 (length ctrlBlockLen): ctrlBlock 32+ctrlBlockLen (length
         * diffBlockLen): diffBlock (gziped) 32+ctrlBlockLen+diffBlockLen (to end of
         * file): extraBlock (gziped)
         *
         * ctrlBlock comprises a set of records, each record 12 bytes. A record
         * comprises 3 x 32 bit integers. The ctrlBlock is not compressed.
         */

        DataOutputStream diffOut = new DataOutputStream(new FileOutputStream(diffFile));

        /*
         * Write as much of header as we have now. Size of ctrlBlock and diffBlock must
         * be filled in later.
         */
        diffOut.write("jbdiff40".getBytes("US-ASCII"));
        diffOut.writeLong(-1); // place holder for ctrlBlockLen
        diffOut.writeLong(-1); // place holder for diffBlockLen
        diffOut.writeLong(newsize);

        int oldscore, scsc;

        int overlap, Ss, lens;
        int i;
        int scan = 0;
        int len = 0;
        int lastscan = 0;
        int lastpos = 0;
        int lastoffset = 0;

        IntByRef pos = new IntByRef();
        int ctrlBlockLen = 0;

        while(scan < newsize) {

            oldscore = 0;

            for(scsc = scan += len; scan < newsize; scan++) {

                len = search(I, oldBuf, newBuf, scan, 0, oldsize, pos);

                for(; scsc < scan + len; scsc++) {
                    if((scsc + lastoffset < oldsize) && (oldBuf[scsc + lastoffset] == newBuf[scsc])) {
                        oldscore++;
                    }
                }

                if(((len == oldscore) && (len != 0)) || (len > oldscore + 8)) {
                    break;
                }

                if((scan + lastoffset < oldsize) && (oldBuf[scan + lastoffset] == newBuf[scan])) {
                    oldscore--;
                }
            }

            if((len != oldscore) || (scan == newsize)) {
                int s = 0;
                int Sf = 0;
                int lenf = 0;
                for(i = 0; (lastscan + i < scan) && (lastpos + i < oldsize); ) {
                    if(oldBuf[lastpos + i] == newBuf[lastscan + i])
                        s++;
                    i++;
                    if(s * 2 - i > Sf * 2 - lenf) {
                        Sf = s;
                        lenf = i;
                    }
                }

                int lenb = 0;
                if(scan < newsize) {
                    s = 0;
                    int Sb = 0;
                    for(i = 1; (scan >= lastscan + i) && (pos.value >= i); i++) {
                        if(oldBuf[pos.value - i] == newBuf[scan - i])
                            s++;
                        if(s * 2 - i > Sb * 2 - lenb) {
                            Sb = s;
                            lenb = i;
                        }
                    }
                }

                if(lastscan + lenf > scan - lenb) {
                    overlap = (lastscan + lenf) - (scan - lenb);
                    s = 0;
                    Ss = 0;
                    lens = 0;
                    for(i = 0; i < overlap; i++) {
                        if(newBuf[lastscan + lenf - overlap + i] == oldBuf[lastpos + lenf - overlap + i]) {
                            s++;
                        }
                        if(newBuf[scan - lenb + i] == oldBuf[pos.value - lenb + i]) {
                            s--;
                        }
                        if(s > Ss) {
                            Ss = s;
                            lens = i + 1;
                        }
                    }

                    lenf += lens - overlap;
                    lenb -= lens;
                }

                // ? byte casting introduced here -- might affect things
                for(i = 0; i < lenf; i++) {
                    db[dblen + i] = (byte) (newBuf[lastscan + i] - oldBuf[lastpos + i]);
                }

                for(i = 0; i < (scan - lenb) - (lastscan + lenf); i++) {
                    eb[eblen + i] = newBuf[lastscan + lenf + i];
                }

                dblen += lenf;
                eblen += (scan - lenb) - (lastscan + lenf);

                /*
                 * Write control block entry (3 x int)
                 */
                diffOut.writeInt(lenf);
                diffOut.writeInt((scan - lenb) - (lastscan + lenf));
                diffOut.writeInt((pos.value - lenb) - (lastpos + lenf));
                ctrlBlockLen += 12;

                lastscan = scan - lenb;
                lastpos = pos.value - lenb;
                lastoffset = pos.value - scan;
            } // end if
        } // end while loop

        GZIPOutputStream gzOut;

        /*
         * Write diff block
         */
        gzOut = new GZIPOutputStream(diffOut);
        gzOut.write(db, 0, dblen);
        gzOut.finish();
        int diffBlockLen = diffOut.size() - ctrlBlockLen - 32;
        // System.err.println ("diffBlockLen=" + diffBlockLen);

        /*
         * Write extra block
         */
        gzOut = new GZIPOutputStream(diffOut);
        gzOut.write(eb, 0, eblen);
        gzOut.finish();
        diffOut.close();

        /*
         * Write missing header info. Need to reopen the file with RandomAccessFile for
         * this.
         */
        RandomAccessFile diff = new RandomAccessFile(diffFile, "rw");
        diff.seek(8);
        diff.writeLong(ctrlBlockLen); // ctrlBlockLen (compressed) @offset 8
        diff.writeLong(diffBlockLen); // diffBlockLen (compressed) @offset 16
        diff.close();
    }

    private static class IntByRef {
        public int value;
    }
}