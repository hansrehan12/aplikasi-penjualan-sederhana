package UAS;

import java.sql.*;

public class Koneksi {
    Connection conn;
    
    public Connection bukaKoneksi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/uas",
                "root",
                ""
            );
            return conn;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    public void tutupKoneksi() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}