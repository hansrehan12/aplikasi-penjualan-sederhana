package uas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PanelBarang extends JPanel {
    Connection con;
    MenuUtama main;
    JTextField txtKode, txtNama, txtSatuan, txtHarga, txtStok, txtCariBarang;
    JTable tblBarang;
    String kodeBarangLama = "";

    public PanelBarang(Connection con, MenuUtama main) {
        this.con = con;
        this.main = main;
        setLayout(null);
        initUI();
        loadBarang("");
    }

    void initUI() {
        JLabel[] l = { new JLabel("Kode"), new JLabel("Nama"), new JLabel("Satuan"), new JLabel("Harga"), new JLabel("Stok") };
        JTextField[] t = { txtKode=new JTextField(), txtNama=new JTextField(), txtSatuan=new JTextField(), txtHarga=new JTextField(), txtStok=new JTextField() };

        int y = 20;
        for (int i = 0; i < l.length; i++) {
            l[i].setBounds(20, y, 80, 25); add(l[i]);
            t[i].setBounds(100, y, 150, 25); add(t[i]);
            y += 30;
        }

        JButton simpan = new JButton("Simpan"), update = new JButton("Update"), hapus = new JButton("Hapus");
        simpan.setBounds(20, 190, 80, 30); update.setBounds(110, 190, 80, 30); hapus.setBounds(200, 190, 80, 30);
        add(simpan); add(update); add(hapus);

        txtCariBarang = new JTextField();
        txtCariBarang.setBounds(300, 20, 200, 25);
        MenuUtama.placeholder(txtCariBarang, "Cari kode / nama barang");
        add(txtCariBarang);

        JButton cari = new JButton("Cari");
        cari.setBounds(510, 20, 80, 25); add(cari);

        tblBarang = new JTable();
        JScrollPane sp = new JScrollPane(tblBarang);
        sp.setBounds(300, 60, 650, 400); add(sp);

        simpan.addActionListener(e -> {
            try {
                PreparedStatement cek = con.prepareStatement("SELECT COUNT(*) FROM barang WHERE kode_barang=?");
                cek.setString(1, txtKode.getText());
                ResultSet r = cek.executeQuery(); r.next();
                if (r.getInt(1) > 0) { JOptionPane.showMessageDialog(this, "Kode barang sudah ada!"); return; }

                PreparedStatement ps = con.prepareStatement("INSERT INTO barang VALUES(?,?,?,?,?)");
                ps.setString(1, txtKode.getText()); ps.setString(2, txtNama.getText());
                ps.setString(3, txtSatuan.getText()); ps.setInt(4, Integer.parseInt(txtHarga.getText()));
                ps.setInt(5, Integer.parseInt(txtStok.getText()));
                ps.executeUpdate();
                loadBarang(""); clearBarang(); main.pNota.loadComboBarang();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        update.addActionListener(e -> {
            if (kodeBarangLama.equals("")) { JOptionPane.showMessageDialog(this, "Pilih barang dulu!"); return; }
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE barang SET nama_barang=?, satuan=?, harga=?, stok=? WHERE kode_barang=?");
                ps.setString(1, txtNama.getText()); ps.setString(2, txtSatuan.getText());
                ps.setInt(3, Integer.parseInt(txtHarga.getText())); ps.setInt(4, Integer.parseInt(txtStok.getText()));
                ps.setString(5, kodeBarangLama);
                ps.executeUpdate();
                loadBarang(""); clearBarang(); main.pNota.loadComboBarang();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        hapus.addActionListener(e -> {
            int r = tblBarang.getSelectedRow(); if (r == -1) return;
            try {
                String kode = tblBarang.getValueAt(r, 0).toString();
                con.createStatement().executeUpdate("DELETE FROM detilnota WHERE kode_barang='" + kode + "'");
                con.createStatement().executeUpdate("DELETE FROM barang WHERE kode_barang='" + kode + "'");
                loadBarang(""); clearBarang(); main.pNota.loadComboBarang();
            } catch (Exception ex) {}
        });

        cari.addActionListener(e -> loadBarang(MenuUtama.key(txtCariBarang, "Cari kode / nama barang")));

        tblBarang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tblBarang.getSelectedRow();
                if (r != -1) {
                    txtKode.setText(tblBarang.getValueAt(r, 0).toString());
                    txtNama.setText(tblBarang.getValueAt(r, 1).toString());
                    txtSatuan.setText(tblBarang.getValueAt(r, 2).toString());
                    txtHarga.setText(tblBarang.getValueAt(r, 3).toString());
                    txtStok.setText(tblBarang.getValueAt(r, 4).toString());
                    kodeBarangLama = txtKode.getText();
                    txtKode.setEditable(false);
                }
            }
        });
    }

    void clearBarang() {
        txtKode.setText(""); txtNama.setText(""); txtSatuan.setText(""); txtHarga.setText(""); txtStok.setText("");
        txtKode.setEditable(true); kodeBarangLama = "";
    }

    void loadBarang(String key) {
        try {
            DefaultTableModel m = new DefaultTableModel(new String[]{"Kode", "Nama", "Satuan", "Harga", "Stok"}, 0);
            ResultSet r = con.createStatement().executeQuery("SELECT * FROM barang WHERE kode_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'");
            while (r.next()) m.addRow(new Object[]{r.getString(1), r.getString(2), r.getString(3), r.getInt(4), r.getInt(5)});
            tblBarang.setModel(m);
        } catch (Exception e) {}
    }
}