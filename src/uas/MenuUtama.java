package uas;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MenuUtama extends JFrame {
    Connection con;
    // Referensi ke panel agar bisa saling memanggil fungsi
    public PanelBarang pBarang;
    public PanelPelanggan pPelanggan;
    public PanelNota pNota;
    public PanelHistory pHistory;

    public MenuUtama() {
        setTitle("Aplikasi Penjualan - UAS - RANU,RAIHAN,RIFFATH");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        koneksi();

        // --- BAGIAN YANG DIPERBAIKI ---
        pBarang = new PanelBarang(con, this);
        pPelanggan = new PanelPelanggan(con, this); // Menambahkan PanelPelanggan
        pNota = new PanelNota(con, this);           // Memperbaiki typo pNota
        pHistory = new PanelHistory(con);
        // ------------------------------

        JTabbedPane tab = new JTabbedPane();
        tab.add("Barang", pBarang);
        tab.add("Pelanggan", pPelanggan);
        tab.add("Nota", pNota);
        tab.add("History", pHistory);

        add(tab);
        setVisible(true);
    }

    void koneksi() {
        try {
            // Menggunakan Driver MySQL terbaru
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/uas", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Koneksi Gagal: " + e.getMessage());
        }
    }

    // Shared Utility Methods
    public static void placeholder(JTextField t, String text) {
        t.setText(text);
        t.setForeground(Color.GRAY);
        t.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (t.getText().equals(text)) {
                    t.setText("");
                    t.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (t.getText().isEmpty()) {
                    t.setText(text);
                    t.setForeground(Color.GRAY);
                }
            }
        });
    }

    public static String key(JTextField t, String ph) {
        return t.getText().equals(ph) ? "" : t.getText();
    }

    public static void main(String[] args) {
        // Menjalankan UI di Event Dispatch Thread agar lebih stabil
        SwingUtilities.invokeLater(() -> {
            new MenuUtama();
        });
    }
}