package uas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PanelHistory extends JPanel {
    Connection con;
    JTable tblHistory;
    JTextField txtCariHistory;

    public PanelHistory(Connection con) {
        this.con = con;
        setLayout(null);
        initUI();
        loadHistory("");
    }

    void initUI() {
        txtCariHistory = new JTextField();
        txtCariHistory.setBounds(20, 15, 300, 30);
        MenuUtama.placeholder(txtCariHistory, "Cari Nota / Pelanggan / Barang");
        add(txtCariHistory);

        JButton cari = new JButton("Cari");
        cari.setBounds(330, 15, 100, 30); add(cari);

        tblHistory = new JTable();
        JScrollPane sp = new JScrollPane(tblHistory);
        sp.setBounds(20, 60, 940, 460); add(sp);

        cari.addActionListener(e -> loadHistory(MenuUtama.key(txtCariHistory, "Cari Nota / Pelanggan / Barang")));
    }

    public void loadHistory(String key) {
        try {
            DefaultTableModel m = new DefaultTableModel(new String[]{"Nota", "Tanggal", "ID", "Nama", "Barang", "Qty", "Subtotal"}, 0);
            ResultSet r = con.createStatement().executeQuery(
                "SELECT n.no_nota,n.tanggal,p.id_pelanggan,p.nama,b.nama_barang,d.qty,d.subtotal " +
                "FROM detilnota d JOIN nota n ON d.no_nota=n.no_nota " +
                "JOIN pelanggan p ON n.id_pelanggan=p.id_pelanggan " +
                "JOIN barang b ON d.kode_barang=b.kode_barang " +
                "WHERE n.no_nota LIKE '%" + key + "%' OR p.id_pelanggan LIKE '%" + key + "%' " +
                "OR p.nama LIKE '%" + key + "%' OR b.nama_barang LIKE '%" + key + "%'");
            while (r.next()) m.addRow(new Object[]{r.getString(1), r.getDate(2), r.getString(3), r.getString(4), r.getString(5), r.getInt(6), r.getInt(7)});
            tblHistory.setModel(m);
        } catch (Exception e) {}
    }
}