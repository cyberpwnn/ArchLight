/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.sql;

import ninja.bytecode.shuriken.math.M;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class SleepingConnection implements SleepyConnection {
    private Connection c;
    private long use;

    public SleepingConnection(Connection c) {
        this.c = c;
        use = M.ms();
    }

    @Override
    public void abort(Executor executor) throws SQLException {

        c.abort(executor);
    }

    @Override
    public void clearWarnings() throws SQLException {

        c.clearWarnings();
    }

    @Override
    public void close() throws SQLException {

        c.close();
    }

    @Override
    public void commit() throws SQLException {

        c.commit();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {

        return c.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob() throws SQLException {

        return c.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {

        return c.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {

        return c.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {

        return c.createSQLXML();
    }

    @Override
    public Statement createStatement() throws SQLException {

        return c.createStatement();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {

        return c.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        return c.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {

        return c.createStruct(typeName, attributes);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {

        return c.getAutoCommit();
    }

    @Override
    public String getCatalog() throws SQLException {

        return c.getCatalog();
    }

    @Override
    public Properties getClientInfo() throws SQLException {

        return c.getClientInfo();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {

        return c.getClientInfo(name);
    }

    @Override
    public int getHoldability() throws SQLException {

        return c.getHoldability();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {

        return c.getMetaData();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {

        return c.getNetworkTimeout();
    }

    @Override
    public String getSchema() throws SQLException {

        return c.getSchema();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {

        return c.getTransactionIsolation();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {

        return c.getTypeMap();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {

        return c.getWarnings();
    }

    @Override
    public boolean isClosed() throws SQLException {

        return c.isClosed();
    }

    @Override
    public boolean isReadOnly() throws SQLException {

        return c.isReadOnly();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {

        return c.isValid(timeout);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {

        return c.nativeSQL(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {

        return c.prepareCall(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        return c.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        return c.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        use = M.ms();
        return c.prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {

        return c.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {

        return c.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {

        return c.prepareStatement(sql, columnNames);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        return c.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        return c.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

        c.releaseSavepoint(savepoint);
    }

    @Override
    public void rollback() throws SQLException {

        c.rollback();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

        c.rollback(savepoint);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

        c.setAutoCommit(autoCommit);
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

        c.setCatalog(catalog);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

        c.setClientInfo(properties);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

        c.setClientInfo(name, value);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

        c.setHoldability(holdability);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

        c.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

        c.setReadOnly(readOnly);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {

        return setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {

        return c.setSavepoint(name);
    }

    @Override
    public void setSchema(String schema) throws SQLException {

        c.setSchema(schema);
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

        c.setTransactionIsolation(level);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

        c.setTypeMap(map);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        return c.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {

        return c.unwrap(iface);
    }

    @Override
    public long getTimeSinceLastUsage() {
        return M.ms() - use;
    }
}
