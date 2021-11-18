/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.sql;

import lombok.Builder;
import lombok.Data;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.json.JSONArray;
import ninja.bytecode.shuriken.json.JSONObject;
import ninja.bytecode.shuriken.logging.L;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Data
@Builder
public class ReadOnlyConnection {
    private String sqlAddress = "localhost";
    private String sqlDatabase = "ordis";
    private String sqlUsername = "ordis";
    private String sqlPassword = "12345";
    private int sqlPort = 3306;
    private transient KList<Connection> connections = new KList<>();

    private void l(String f) {
        L.l("SQL[RO] " + f);
    }

    private void f(String f) {
        L.f("SQL[RO] " + f);
    }

    public void createConnections(int count, String driver) {
        connections = new KList<>();
        for(int ii = 0; ii < count; ii++) {
            int i = ii;
            l("Creating 0x" + hashCode() + " #" + (i + 1));
            J.attemptAsync(() -> {
                J.sleep(i * 15000L);
                String id = "RO-" + hashCode() + "-" + i;
                Connection c = null;
                l(id + " -> Connecting to MySQL jdbc:mysql://" + sqlAddress + "/" + sqlDatabase + "?username=" + sqlUsername + "&password=" + sqlPassword);

                try {
                    Class.forName(driver).newInstance();
                    Properties p = new Properties();
                    p.setProperty("user", sqlUsername);

                    if(!sqlPassword.equals(".")) {
                        p.setProperty("password", sqlPassword);
                    }

                    c = DriverManager.getConnection("jdbc:mysql://" + sqlAddress + (sqlPort != 3306 ? (":" + sqlPort) : "") + "/" + sqlDatabase, p);
                } catch(InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    L.ex(e);
                    f(id + " -> Failed to instantiate " + driver);
                    throw new SQLException(id + " -> SQL Driver Failure");
                } catch(SQLException e) {
                    L.ex(e);
                    f(id + " -> SQLException: " + e.getMessage());
                    f(id + " -> SQLState: " + e.getSQLState());
                    f(id + " -> VendorError: " + e.getErrorCode());
                    throw new SQLException(id + " -> SQL Connection Failure");
                }

                try {
                    if(c.isValid(1)) {
                        l(id + " -> JDBC driver is connected to " + sqlAddress + "/" + sqlDatabase + " as " + sqlUsername);
                    } else {
                        f(id + " -> JDBC driver failed to connect to database.");
                        throw new SQLException(id + " -> SQL Connection Failure");
                    }
                } catch(SQLException e) {
                    L.ex(e);
                    f(id + " -> SQLException: " + e.getMessage());
                    f(id + " -> SQLState: " + e.getSQLState());
                    f(id + " -> VendorError: " + e.getErrorCode());
                    throw new SQLException(id + " -> SQL Connection Failure");
                } catch(Throwable e) {
                    L.ex(e);
                }

                if(c != null) {
                    connections.add(c);
                }
            });
        }
    }

    public static KList<ReadOnlyConnection> fromConnection(JSONObject jd) {
        JSONArray ja = jd.getJSONArray("readOnlyNodes");
        KList<ReadOnlyConnection> ro = new KList<>();
        if(ja == null) {
            return ro;
        }

        for(int i = 0; i < ja.length(); i++) {
            JSONObject j = ja.getJSONObject(i);
            ro.add(ReadOnlyConnection.builder()
                .sqlUsername(j.getString("username"))
                .sqlDatabase(j.getString("database"))
                .sqlAddress(j.getString("address"))
                .sqlPassword(j.getString("password"))
                .sqlPort(j.getInt("port"))
                .build());
        }

        return ro;
    }
}
