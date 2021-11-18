/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.sql;

import ninja.bytecode.shuriken.collections.KList;

public class Query {
    private StringBuilder query;

    public Query() {
        query = new StringBuilder();
    }

    public Query selectAll() {
        query.append(" ");
        query.append("SELECT *");
        return this;
    }

    public Query count() {
        query.append(" ");
        query.append("SELECT *");
        return this;
    }

    public Query select(String... select) {
        //@builder
        query.append(" ");
        query.append("SELECT " + new KList<String>()
            .add(select)
            .convert((a) -> "`" + a + "`").toString(", "));
        //@done
        return this;
    }

    public Query from(String in) {
        query.append(" ");
        query.append("FROM `" + in + "`");
        return this;
    }

    public Query whereEquals(String v, String eq) {
        query.append(" ");
        query.append("WHERE `" + v + "` = '" + eq + "'");
        return this;
    }

    public Query whereAll() {
        query.append(" ");
        query.append("WHERE 1");
        return this;
    }

    public Query where(String where) {
        query.append(" ");
        query.append("WHERE " + where);
        return this;
    }

    public Query limit(int offset, int pageSize) {
        query.append(" ");
        query.append("LIMIT " + offset + "," + pageSize);
        return this;
    }

    public String toString() {
        return query.toString().trim() + ";";
    }
}
