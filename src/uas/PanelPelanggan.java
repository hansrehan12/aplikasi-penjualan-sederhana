package uas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PanelPelanggan extends JPanel {
    Connection con;
    MenuUtama main;
    JTextField txtIdPlg, txtNamaPlg, txtAlamatPlg, txtTelp, txtCariPlg;
    JTable tblPelanggan;

    public PanelPelanggan(Connection con, MenuUtama main) {
        this.con = con;
        this.main = main;
        setLayout(null);
        initUI();
        loadPelanggan("");
    }

    void initUI() {
        JLabel[] l = { new JLabel("ID"), new JLabel("Nama"), new JLabel("Alamat"), new JLabel("Telepon") };
        JTextField[] t = { txtIdPlg=new JTextField(), txtNamaPlg=new JTextField(), txtAlamatPlg=new JTextField(), txtTelp=new JTextField() };

        int y = 20;
        for (int i = 0; i < l.length; i++) {
            l[i].setBounds(20, y, 80, 25); add(l[i]);
            t[i].setBounds(100, y, 200, 25); add(t[i]);
            y += 30;
        }

        JButton simpan = new JButton("Simpan"), update = new JButton("Update"), hapus = new JButton("Hapus");
        simpan.setBounds(20, 160, 80, 30); update.setBounds(110, 160, 80, 30); hapus.setBounds(200, 160, 80, 30);
        add(simpan); add(update); add(hapus);

        txtCariPlg = new JTextField();
        txtCariPlg.setBounds(320, 20, 200, 25);
        MenuUtama.placeholder(txtCariPlg, "Cari ID / Nama / Telp");
        add(txtCariPlg);

        JButton cari = new JButton("Cari");
        cari.setBounds(530, 20, 80, 25); add(cari);

        tblPelanggan = new JTable();
        JScrollPane sp = new JScrollPane(tblPelanggan);
        sp.setBounds(320, 60, 620, 400); add(sp);

        simpan.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO pelanggan VALUES(?,?,?,?)");
                ps.setString(1, txtIdPlg.getText()); ps.setString(2, txtNamaPlg.getText());
                ps.setString(3, txtAlamatPlg.getText()); ps.setString(4, txtTelp.getText());
                ps.executeUpdate();
                loadPelanggan(""); main.pNota.loadComboPelanggan();
            } catch (Exception ex) {}
        });

        update.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE pelanggan SET nama=?, alamat=?, telepon=? WHERE id_pelanggan=?");
                ps.setString(1, txtNamaPlg.getText()); ps.setString(2, txtAlamatPlg.getText());
                ps.setString(3, txtTelp.getText()); ps.setString(4, txtIdPlg.getText());
                ps.executeUpdate();
                loadPelanggan(""); main.pNota.loadComboPelanggan();
            } catch (Exception ex) {}
        });

        hapus.addActionListener(e -> {
            try {
                con.createStatement().executeUpdate("DELETE FROM pelanggan WHERE id_pelanggan='" + txtIdPlg.getText() + "'");
                loadPelanggan(""); main.pNota.loadComboPelanggan();
            } catch (Exception ex) {}
        });

        cari.addActionListener(e -> loadPelanggan(MenuUtama.key(txtCariPlg, "Cari ID / Nama / Telp")));

        tblPelanggan.getSelectionModel().addListSelectionListener(e -> {
            int r = tblPelanggan.getSelectedRow();
            if (r != -1) {
                txtIdPlg.setText(tblPelanggan.getValueAt(r, 0).toString());
                txtNamaPlg.setText(tblPelanggan.getValueAt(r, 1).toString());
                txtAlamatPlg.setText(tblPelanggan.getValueAt(r, 2).toString());
                txtTelp.setText(tblPelanggan.getValueAt(r, 3).toString());
                txtIdPlg.setEditable(false);
            }
        });
    }

    void loadPelanggan(String key) {
        try {
            DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Nama", "Alamat", "Telepon"}, 0);
            ResultSet r = con.createStatement().executeQuery("SELECT * FROM pelanggan WHERE id_pelanggan LIKE '%" + key + "%' OR nama LIKE '%" + key + "%' OR telepon LIKE '%" + key + "%'");
            while (r.next()) m.addRow(new Object[]{r.getString(1), r.getString(2), r.getString(3), r.getString(4)});
            tblPelanggan.setModel(m);
        } catch (Exception e) {}
    }
}