package uas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelNota extends JPanel {
    Connection con;
    MenuUtama main;
    JTextField txtNoNota, txtQty;
    JComboBox<String> cmbPelanggan, cmbBarang;
    DefaultTableModel modelKeranjang;
    JTable tblKeranjang;
    JLabel lblTotal;

    public PanelNota(Connection con, MenuUtama main) {
        this.con = con;
        this.main = main;
        setLayout(null);
        initUI();
        loadComboPelanggan();
        loadComboBarang();
    }

    void initUI() {
        JLabel l1 = new JLabel("No Nota"); l1.setBounds(20, 20, 80, 25); add(l1);
        txtNoNota = new JTextField(); txtNoNota.setBounds(100, 20, 120, 25); add(txtNoNota);

        JLabel l2 = new JLabel("Pelanggan"); l2.setBounds(20, 50, 80, 25); add(l2);
        cmbPelanggan = new JComboBox<>(); cmbPelanggan.setBounds(100, 50, 250, 25); add(cmbPelanggan);

        JLabel l3 = new JLabel("Barang"); l3.setBounds(380, 20, 80, 25); add(l3);
        cmbBarang = new JComboBox<>(); cmbBarang.setBounds(460, 20, 250, 25); add(cmbBarang);

        JLabel l4 = new JLabel("Qty"); l4.setBounds(380, 50, 80, 25); add(l4);
        txtQty = new JTextField(); txtQty.setBounds(460, 50, 100, 25); add(txtQty);

        JButton tambah = new JButton("Tambah / Update"); tambah.setBounds(740, 20, 160, 55); add(tambah);

        modelKeranjang = new DefaultTableModel(new String[]{"Kode", "Nama", "Harga", "Qty", "Subtotal"}, 0);
        tblKeranjang = new JTable(modelKeranjang);
        JScrollPane sp = new JScrollPane(tblKeranjang);
        sp.setBounds(20, 100, 900, 300); add(sp);

        JButton btnHapus = new JButton("Hapus Item"); btnHapus.setBounds(20, 420, 120, 30); add(btnHapus);

        lblTotal = new JLabel("Total: 0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setBounds(160, 420, 300, 30); add(lblTotal);

        JButton btnSimpan = new JButton("Simpan & Cetak"); btnSimpan.setBounds(700, 420, 220, 40); add(btnSimpan);

        tambah.addActionListener(e -> {
            try {
                String kode = cmbBarang.getSelectedItem().toString().split(" - ")[0];
                int qty = Integer.parseInt(txtQty.getText());
                ResultSet r = con.createStatement().executeQuery("SELECT nama_barang,harga FROM barang WHERE kode_barang='" + kode + "'");
                if (r.next()) {
                    int harga = r.getInt(2);
                    boolean found = false;
                    for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                        if (modelKeranjang.getValueAt(i, 0).equals(kode)) {
                            modelKeranjang.setValueAt(qty, i, 3);
                            modelKeranjang.setValueAt(qty * harga, i, 4);
                            found = true; break;
                        }
                    }
                    if (!found) modelKeranjang.addRow(new Object[]{kode, r.getString(1), harga, qty, harga * qty});
                    hitungTotal();
                }
            } catch (Exception ex) {}
        });

        btnHapus.addActionListener(e -> {
            int r = tblKeranjang.getSelectedRow();
            if (r != -1) { modelKeranjang.removeRow(r); hitungTotal(); }
        });

        btnSimpan.addActionListener(e -> {
            if (modelKeranjang.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Keranjang kosong!"); return; }
            popupStruk();
        });
    }

    void hitungTotal() {
        int t = 0;
        for (int i = 0; i < modelKeranjang.getRowCount(); i++) t += (int) modelKeranjang.getValueAt(i, 4);
        lblTotal.setText("Total: " + t);
    }

    public void loadComboPelanggan() {
        try {
            cmbPelanggan.removeAllItems();
            ResultSet r = con.createStatement().executeQuery("SELECT id_pelanggan,nama FROM pelanggan");
            while (r.next()) cmbPelanggan.addItem(r.getString(1) + " - " + r.getString(2));
        } catch (Exception e) {}
    }

    public void loadComboBarang() {
        try {
            cmbBarang.removeAllItems();
            ResultSet r = con.createStatement().executeQuery("SELECT kode_barang,nama_barang FROM barang");
            while (r.next()) cmbBarang.addItem(r.getString(1) + " - " + r.getString(2));
        } catch (Exception e) {}
    }

    void simpanNota() {
        try {
            con.setAutoCommit(false);
            String id = cmbPelanggan.getSelectedItem().toString().split(" - ")[0];
            int total = Integer.parseInt(lblTotal.getText().replace("Total: ", ""));
            PreparedStatement ps = con.prepareStatement("INSERT INTO nota VALUES(?,CURDATE(),?,?)");
            ps.setString(1, txtNoNota.getText()); ps.setString(2, id); ps.setInt(3, total);
            ps.executeUpdate();

            PreparedStatement d = con.prepareStatement("INSERT INTO detilnota(no_nota,kode_barang,qty,harga,subtotal) VALUES(?,?,?,?,?)");
            for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                d.setString(1, txtNoNota.getText());
                d.setString(2, modelKeranjang.getValueAt(i, 0).toString());
                d.setInt(3, (int) modelKeranjang.getValueAt(i, 3));
                d.setInt(4, (int) modelKeranjang.getValueAt(i, 2));
                d.setInt(5, (int) modelKeranjang.getValueAt(i, 4));
                d.executeUpdate();
            }
            con.commit();
            JOptionPane.showMessageDialog(this, "Nota tersimpan");
            modelKeranjang.setRowCount(0); lblTotal.setText("Total: 0");
            main.pHistory.loadHistory(""); // Refresh history tab
        } catch (Exception e) { try { con.rollback(); } catch (Exception ex) {} }
    }

    void popupStruk() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Preview Struk", true);
        d.setSize(400, 500); d.setLocationRelativeTo(this);
        JTextArea a = new JTextArea(); a.setEditable(false);
        a.append("===== STRUK PENJUALAN =====\nNo Nota : " + txtNoNota.getText() + "\nPelanggan : " + cmbPelanggan.getSelectedItem() + "\n--------------------------\n");
        for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
            a.append(modelKeranjang.getValueAt(i, 1) + " x" + modelKeranjang.getValueAt(i, 3) + " = " + modelKeranjang.getValueAt(i, 4) + "\n");
        }
        a.append("--------------------------\n" + lblTotal.getText() + "\n");

        JButton bSimpan = new JButton("Simpan"), bPrint = new JButton("Print"), bBatal = new JButton("Batal");
        bSimpan.addActionListener(e -> { simpanNota(); d.dispose(); });
        bPrint.addActionListener(e -> { try { a.print(); } catch (Exception ex) {} });
        bBatal.addActionListener(e -> d.dispose());

        JPanel p = new JPanel(); p.add(bSimpan); p.add(bPrint); p.add(bBatal);
        d.setLayout(new BorderLayout()); d.add(new JScrollPane(a), BorderLayout.CENTER); d.add(p, BorderLayout.SOUTH);
        d.setVisible(true);
    }
}