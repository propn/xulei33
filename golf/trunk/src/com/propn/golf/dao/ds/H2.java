/**
 * 
 */
package com.propn.golf.dao.ds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.Server;

public class H2 {

    private static Server server;
    private static final String port = "9094";
    private static final String dbDir = "./db/db";
    private static final String user = "sa";
    private static final String password = "sa";

    public static void startServer() {
        try {
            server = Server.createTcpServer(new String[] { "-tcpPort", port }).start();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    public static Connection getConn() throws Exception {
        return DriverManager.getConnection("jdbc:h2:" + dbDir, user, password);
    }

    public static void main(String[] args) {
        H2.startServer();
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:" + dbDir, user, password);
            Statement stat = conn.createStatement();
            // stat.execute("  drop   table   PERSON");
            // stat.execute("  create   table   PERSON(PERSON_ID varchar(10), PERSON_NAME varchar(64),AGE number)");
            stat.execute("  insert into PERSON (AGE,PERSON_ID,PERSON_NAME) values('1','徐雷',28 )");
            ResultSet result = stat.executeQuery("select * from PERSON");
            int i = 1;
            while (result.next()) {
                System.out.println(i++ + ":" + result.getString("PERSON_NAME"));
            }
            // String sql =
            // "CREATE TABLE BaseEntity( id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,url VARCHAR(100),  site VARCHAR(50), pubDate TIMESTAMP(8), inDate TIMESTAMP(8) )";
            // stat.execute(sql);
            result.close();
            stat.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        H2.stopServer();
        System.out.println("==END==");
    }

}
