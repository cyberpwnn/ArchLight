/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.tools;

import ninja.bytecode.shuriken.collections.KList;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionTools {
    public static String toString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.getBuffer().toString();
    }

    public static KList<String> toStrings(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        KList<String> f = new KList<String>(sw.getBuffer().toString().split("\\r?\\n"));

        return f;
    }
}
