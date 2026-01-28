package UAS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MenuUtama extends JFrame {

    Connection con;

    // BARANG
    JTextField txtKode, txtNama, txtSatuan, txtHarga, txtStok, txtCariBarang;
    JTable tblBarang;
    String kodeBarangLama = "";

    // PELANGGAN
    JTextField txtIdPlg, txtNamaPlg, txtAlamatPlg, txtTelp, txtCariPlg;
    JTable tblPelanggan;

    // NOTA
    JTextField txtNoNota, txtQty;
    JComboBox<String> cmbPelanggan, cmbBarang;
    DefaultTableModel modelKeranjang;
    JTable tblKeranjang;
    JLabel lblTotal;

    // HISTORY
    JTable tblHistory;
    JTextField txtCariHistory;

    public MenuUtama() {
        setTitle("Aplikasi Penjualan - UAS - RANU,RAIHAN,RIFFATH");
        setSize(1000,650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        koneksi();

        JTabbedPane tab = new JTabbedPane();
        tab.add("Barang", panelBarang());
        tab.add("Pelanggan", panelPelanggan());
        tab.add("Nota", panelNota());
        tab.add("History", panelHistory());

        add(tab);
        setVisible(true);
    }

    void koneksi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost/uas","root","");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= PLACEHOLDER SEARCH =================
    void placeholder(JTextField t, String text){
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

    String key(JTextField t, String ph){
        return t.getText().equals(ph) ? "" : t.getText();
    }

    // ================= TAB BARANG =================
    JPanel panelBarang() {
        JPanel p = new JPanel(null);

        JLabel[] l = {
            new JLabel("Kode"), new JLabel("Nama"),
            new JLabel("Satuan"), new JLabel("Harga"), new JLabel("Stok")
        };
        JTextField[] t = {
            txtKode=new JTextField(),
            txtNama=new JTextField(),
            txtSatuan=new JTextField(),
            txtHarga=new JTextField(),
            txtStok=new JTextField()
        };

        int y=20;
        for(int i=0;i<l.length;i++){
            l[i].setBounds(20,y,80,25); p.add(l[i]);
            t[i].setBounds(100,y,150,25); p.add(t[i]);
            y+=30;
        }

        JButton simpan=new JButton("Simpan");
        JButton update=new JButton("Update");
        JButton hapus=new JButton("Hapus");
        simpan.setBounds(20,190,80,30);
        update.setBounds(110,190,80,30);
        hapus.setBounds(200,190,80,30);
        p.add(simpan); p.add(update); p.add(hapus);

        txtCariBarang=new JTextField();
        txtCariBarang.setBounds(300,20,200,25);
        placeholder(txtCariBarang,"Cari kode / nama barang");
        p.add(txtCariBarang);

        JButton cari=new JButton("Cari");
        cari.setBounds(510,20,80,25);
        p.add(cari);

        tblBarang=new JTable();
        JScrollPane sp=new JScrollPane(tblBarang);
        sp.setBounds(300,60,650,400);
        p.add(sp);

        simpan.addActionListener(e->{
            try{
                PreparedStatement cek = con.prepareStatement(
                    "SELECT COUNT(*) FROM barang WHERE kode_barang=?");
                cek.setString(1,txtKode.getText());
                ResultSet r=cek.executeQuery();
                r.next();
                if(r.getInt(1)>0){
                    JOptionPane.showMessageDialog(this,"Kode barang sudah ada!");
                    return;
                }

                PreparedStatement ps=con.prepareStatement(
                    "INSERT INTO barang VALUES(?,?,?,?,?)");
                ps.setString(1,txtKode.getText());
                ps.setString(2,txtNama.getText());
                ps.setString(3,txtSatuan.getText());
                ps.setInt(4,Integer.parseInt(txtHarga.getText()));
                ps.setInt(5,Integer.parseInt(txtStok.getText()));
                ps.executeUpdate();

                loadBarang("");
                loadComboBarang();
                clearBarang();
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });

        update.addActionListener(e->{
            if(kodeBarangLama.equals("")){
                JOptionPane.showMessageDialog(this,"Pilih barang dulu!");
                return;
            }
            try{
                PreparedStatement ps=con.prepareStatement(
                    "UPDATE barang SET nama_barang=?, satuan=?, harga=?, stok=? WHERE kode_barang=?");
                ps.setString(1,txtNama.getText());
                ps.setString(2,txtSatuan.getText());
                ps.setInt(3,Integer.parseInt(txtHarga.getText()));
                ps.setInt(4,Integer.parseInt(txtStok.getText()));
                ps.setString(5,kodeBarangLama);
                ps.executeUpdate();

                loadBarang("");
                loadComboBarang();
                clearBarang();
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });

        hapus.addActionListener(e->{
            int r=tblBarang.getSelectedRow();
            if(r==-1) return;
            try{
                String kode=tblBarang.getValueAt(r,0).toString();
                con.createStatement().executeUpdate(
                    "DELETE FROM detilnota WHERE kode_barang='"+kode+"'");
                con.createStatement().executeUpdate(
                    "DELETE FROM barang WHERE kode_barang='"+kode+"'");
                loadBarang("");
                loadComboBarang();
                clearBarang();
            }catch(Exception ex){}
        });

        cari.addActionListener(e->loadBarang(key(txtCariBarang,"Cari kode / nama barang")));

        tblBarang.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                int r=tblBarang.getSelectedRow();
                if(r!=-1){
                    txtKode.setText(tblBarang.getValueAt(r,0).toString());
                    txtNama.setText(tblBarang.getValueAt(r,1).toString());
                    txtSatuan.setText(tblBarang.getValueAt(r,2).toString());
                    txtHarga.setText(tblBarang.getValueAt(r,3).toString());
                    txtStok.setText(tblBarang.getValueAt(r,4).toString());
                    kodeBarangLama=txtKode.getText();
                    txtKode.setEditable(false);
                }
            }
        });

        loadBarang("");
        return p;
    }

    void clearBarang(){
        txtKode.setText("");
        txtNama.setText("");
        txtSatuan.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        txtKode.setEditable(true);
        kodeBarangLama="";
    }

    void loadBarang(String key){
        try{
            DefaultTableModel m=new DefaultTableModel(
                new String[]{"Kode","Nama","Satuan","Harga","Stok"},0);
            ResultSet r=con.createStatement().executeQuery(
                "SELECT * FROM barang WHERE kode_barang LIKE '%"+key+"%' OR nama_barang LIKE '%"+key+"%'");
            while(r.next())
                m.addRow(new Object[]{
                    r.getString(1),r.getString(2),
                    r.getString(3),r.getInt(4),r.getInt(5)});
            tblBarang.setModel(m);
        }catch(Exception e){}
    }

    // ================= TAB PELANGGAN =================
    JPanel panelPelanggan(){
        JPanel p=new JPanel(null);

        JLabel[] l={
            new JLabel("ID"),new JLabel("Nama"),
            new JLabel("Alamat"),new JLabel("Telepon")
        };
        JTextField[] t={
            txtIdPlg=new JTextField(),
            txtNamaPlg=new JTextField(),
            txtAlamatPlg=new JTextField(),
            txtTelp=new JTextField()
        };

        int y=20;
        for(int i=0;i<l.length;i++){
            l[i].setBounds(20,y,80,25); p.add(l[i]);
            t[i].setBounds(100,y,200,25); p.add(t[i]);
            y+=30;
        }

        JButton simpan=new JButton("Simpan");
        JButton update=new JButton("Update");
        JButton hapus=new JButton("Hapus");
        simpan.setBounds(20,160,80,30);
        update.setBounds(110,160,80,30);
        hapus.setBounds(200,160,80,30);
        p.add(simpan); p.add(update); p.add(hapus);

        txtCariPlg=new JTextField();
        txtCariPlg.setBounds(320,20,200,25);
        placeholder(txtCariPlg,"Cari ID / Nama / Telp");
        p.add(txtCariPlg);

        JButton cari=new JButton("Cari");
        cari.setBounds(530,20,80,25);
        p.add(cari);

        tblPelanggan=new JTable();
        JScrollPane sp=new JScrollPane(tblPelanggan);
        sp.setBounds(320,60,620,400);
        p.add(sp);

        simpan.addActionListener(e->{
            try{
                PreparedStatement cek=con.prepareStatement(
                    "SELECT COUNT(*) FROM pelanggan WHERE id_pelanggan=?");
                cek.setString(1,txtIdPlg.getText());
                ResultSet r=cek.executeQuery(); r.next();
                if(r.getInt(1)>0){
                    JOptionPane.showMessageDialog(this,"ID pelanggan sudah ada!");
                    return;
                }

                PreparedStatement ps=con.prepareStatement(
                    "INSERT INTO pelanggan VALUES(?,?,?,?)");
                ps.setString(1,txtIdPlg.getText());
                ps.setString(2,txtNamaPlg.getText());
                ps.setString(3,txtAlamatPlg.getText());
                ps.setString(4,txtTelp.getText());
                ps.executeUpdate();

                loadPelanggan("");
                loadComboPelanggan();
            }catch(Exception ex){}
        });

        update.addActionListener(e->{
            try{
                PreparedStatement ps=con.prepareStatement(
                    "UPDATE pelanggan SET nama=?, alamat=?, telepon=? WHERE id_pelanggan=?");
                ps.setString(1,txtNamaPlg.getText());
                ps.setString(2,txtAlamatPlg.getText());
                ps.setString(3,txtTelp.getText());
                ps.setString(4,txtIdPlg.getText());
                ps.executeUpdate();
                loadPelanggan("");
                loadComboPelanggan();
            }catch(Exception ex){}
        });

        hapus.addActionListener(e->{
            try{
                con.createStatement().executeUpdate(
                    "DELETE FROM pelanggan WHERE id_pelanggan='"+txtIdPlg.getText()+"'");
                loadPelanggan("");
                loadComboPelanggan();
            }catch(Exception ex){}
        });

        cari.addActionListener(e->loadPelanggan(key(txtCariPlg,"Cari ID / Nama / Telp")));

        tblPelanggan.getSelectionModel().addListSelectionListener(e->{
            int r=tblPelanggan.getSelectedRow();
            if(r!=-1){
                txtIdPlg.setText(tblPelanggan.getValueAt(r,0).toString());
                txtNamaPlg.setText(tblPelanggan.getValueAt(r,1).toString());
                txtAlamatPlg.setText(tblPelanggan.getValueAt(r,2).toString());
                txtTelp.setText(tblPelanggan.getValueAt(r,3).toString());
                txtIdPlg.setEditable(false);
            }
        });

        loadPelanggan("");
        return p;
    }

    void loadPelanggan(String key){
        try{
            DefaultTableModel m=new DefaultTableModel(
                new String[]{"ID","Nama","Alamat","Telepon"},0);
            ResultSet r=con.createStatement().executeQuery(
                "SELECT * FROM pelanggan WHERE id_pelanggan LIKE '%"+key+"%' OR nama LIKE '%"+key+"%' OR telepon LIKE '%"+key+"%'");
            while(r.next())
                m.addRow(new Object[]{
                    r.getString(1),r.getString(2),
                    r.getString(3),r.getString(4)});
            tblPelanggan.setModel(m);
        }catch(Exception e){}
    }

    // ================= TAB NOTA =================
    JPanel panelNota(){
        JPanel p=new JPanel(null);

        p.add(new JLabel("No Nota")).setBounds(20,20,80,25);
        txtNoNota=new JTextField();
        txtNoNota.setBounds(100,20,120,25);
        p.add(txtNoNota);

        p.add(new JLabel("Pelanggan")).setBounds(20,50,80,25);
        cmbPelanggan=new JComboBox<>();
        cmbPelanggan.setBounds(100,50,250,25);
        p.add(cmbPelanggan);

        p.add(new JLabel("Barang")).setBounds(380,20,80,25);
        cmbBarang=new JComboBox<>();
        cmbBarang.setBounds(460,20,250,25);
        p.add(cmbBarang);

        p.add(new JLabel("Qty")).setBounds(380,50,80,25);
        txtQty=new JTextField();
        txtQty.setBounds(460,50,100,25);
        p.add(txtQty);

        JButton tambah=new JButton("Tambah / Update");
        tambah.setBounds(740,20,160,55);
        p.add(tambah);

        modelKeranjang=new DefaultTableModel(
            new String[]{"Kode","Nama","Harga","Qty","Subtotal"},0);
        tblKeranjang=new JTable(modelKeranjang);
        JScrollPane sp=new JScrollPane(tblKeranjang);
        sp.setBounds(20,100,900,300);
        p.add(sp);

        JButton hapus=new JButton("Hapus Item");
        hapus.setBounds(20,420,120,30); p.add(hapus);

        lblTotal=new JLabel("Total: 0");
        lblTotal.setFont(new Font("Arial",Font.BOLD,18));
        lblTotal.setBounds(160,420,300,30); p.add(lblTotal);

        JButton simpan=new JButton("Simpan & Cetak");
        simpan.setBounds(700,420,220,40); p.add(simpan);

        loadComboPelanggan();
        loadComboBarang();

        tambah.addActionListener(e->{
            try{
                String kode=cmbBarang.getSelectedItem().toString().split(" - ")[0];
                int qty=Integer.parseInt(txtQty.getText());

                ResultSet r=con.createStatement().executeQuery(
                    "SELECT nama_barang,harga FROM barang WHERE kode_barang='"+kode+"'");
                if(r.next()){
                    int harga=r.getInt(2);
                    boolean found=false;
                    for(int i=0;i<modelKeranjang.getRowCount();i++){
                        if(modelKeranjang.getValueAt(i,0).equals(kode)){
                            modelKeranjang.setValueAt(qty,i,3);
                            modelKeranjang.setValueAt(qty*harga,i,4);
                            found=true;
                            break;
                        }
                    }
                    if(!found){
                        modelKeranjang.addRow(new Object[]{
                            kode,r.getString(1),harga,qty,harga*qty});
                    }
                    hitungTotal();
                }
            }catch(Exception ex){}
        });

        hapus.addActionListener(e->{
            int r=tblKeranjang.getSelectedRow();
            if(r!=-1){
                modelKeranjang.removeRow(r);
                hitungTotal();
            }
        });

        simpan.addActionListener(e->{
    if(modelKeranjang.getRowCount()==0){
        JOptionPane.showMessageDialog(this,"Keranjang kosong!");
        return;
    }
    popupStruk(); // PREVIEW DULU
});

        return p;
    }

    void hitungTotal(){
        int t=0;
        for(int i=0;i<modelKeranjang.getRowCount();i++)
            t+=(int)modelKeranjang.getValueAt(i,4);
        lblTotal.setText("Total: "+t);
    }

    void simpanNota(){
        try{
            PreparedStatement cek=con.prepareStatement(
                "SELECT COUNT(*) FROM nota WHERE no_nota=?");
            cek.setString(1,txtNoNota.getText());
            ResultSet rs=cek.executeQuery(); rs.next();
            if(rs.getInt(1)>0){
                JOptionPane.showMessageDialog(this,"No nota sudah ada!");
                return;
            }

            con.setAutoCommit(false);
            String id=cmbPelanggan.getSelectedItem().toString().split(" - ")[0];
            int total=Integer.parseInt(lblTotal.getText().replace("Total: ",""));

            PreparedStatement ps=con.prepareStatement(
                "INSERT INTO nota VALUES(?,CURDATE(),?,?)");
            ps.setString(1,txtNoNota.getText());
            ps.setString(2,id);
            ps.setInt(3,total);
            ps.executeUpdate();

            PreparedStatement d=con.prepareStatement(
                "INSERT INTO detilnota(no_nota,kode_barang,qty,harga,subtotal) VALUES(?,?,?,?,?)");
            for(int i=0;i<modelKeranjang.getRowCount();i++){
                d.setString(1,txtNoNota.getText());
                d.setString(2,modelKeranjang.getValueAt(i,0).toString());
                d.setInt(3,(int)modelKeranjang.getValueAt(i,3));
                d.setInt(4,(int)modelKeranjang.getValueAt(i,2));
                d.setInt(5,(int)modelKeranjang.getValueAt(i,4));
                d.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this,"Nota tersimpan");
            modelKeranjang.setRowCount(0);
            lblTotal.setText("Total: 0");
        }catch(Exception e){
            try{con.rollback();}catch(Exception ex){}
        }
    }
    void popupStruk(){
    JDialog d=new JDialog(this,"Preview Struk",true);
    d.setSize(400,500);
    d.setLocationRelativeTo(this);

    JTextArea a=new JTextArea();
    a.setEditable(false);

    a.append("===== STRUK PENJUALAN =====\n");
    a.append("No Nota : "+txtNoNota.getText()+"\n");
    a.append("Pelanggan : "+cmbPelanggan.getSelectedItem()+"\n");
    a.append("--------------------------\n");

    for(int i=0;i<modelKeranjang.getRowCount();i++){
        a.append(
            modelKeranjang.getValueAt(i,1)+" x"+
            modelKeranjang.getValueAt(i,3)+" = "+
            modelKeranjang.getValueAt(i,4)+"\n"
        );
    }

    a.append("--------------------------\n");
    a.append(lblTotal.getText()+"\n");

    JButton btnSimpan=new JButton("Simpan");
    JButton btnPrint=new JButton("Print / PDF");
    JButton btnBatal=new JButton("Batal");

    btnSimpan.addActionListener(e->{
        simpanNota();   // ✅ BARU SIMPAN DI SINI
        d.dispose();
    });

    btnPrint.addActionListener(e->{
        try{ a.print(); }catch(Exception ex){}
    });

    btnBatal.addActionListener(e-> d.dispose());

    JPanel bawah=new JPanel();
    bawah.add(btnSimpan);
    bawah.add(btnPrint);
    bawah.add(btnBatal);

    d.setLayout(new BorderLayout());
    d.add(new JScrollPane(a),BorderLayout.CENTER);
    d.add(bawah,BorderLayout.SOUTH);
    d.setVisible(true);
}

    

    // ================= TAB HISTORY =================
    JPanel panelHistory(){
        JPanel p=new JPanel(null);

        txtCariHistory=new JTextField();
        txtCariHistory.setBounds(20,15,300,30);
        placeholder(txtCariHistory,"Cari Nota / Pelanggan / Barang");
        p.add(txtCariHistory);

        JButton cari=new JButton("Cari");
        cari.setBounds(330,15,100,30);
        p.add(cari);

        tblHistory=new JTable();
        JScrollPane sp=new JScrollPane(tblHistory);
        sp.setBounds(20,60,940,460);
        p.add(sp);

        cari.addActionListener(e->loadHistory(
            key(txtCariHistory,"Cari Nota / Pelanggan / Barang")));

        loadHistory("");
        return p;
    }

    void loadHistory(String key){
        try{
            DefaultTableModel m=new DefaultTableModel(
                new String[]{"Nota","Tanggal","ID","Nama","Barang","Qty","Subtotal"},0);
            ResultSet r=con.createStatement().executeQuery(
                "SELECT n.no_nota,n.tanggal,p.id_pelanggan,p.nama,b.nama_barang,d.qty,d.subtotal " +
                "FROM detilnota d JOIN nota n ON d.no_nota=n.no_nota " +
                "JOIN pelanggan p ON n.id_pelanggan=p.id_pelanggan " +
                "JOIN barang b ON d.kode_barang=b.kode_barang " +
                "WHERE n.no_nota LIKE '%"+key+"%' OR p.id_pelanggan LIKE '%"+key+"%' " +
                "OR p.nama LIKE '%"+key+"%' OR b.nama_barang LIKE '%"+key+"%'");
            while(r.next())
                m.addRow(new Object[]{
                    r.getString(1),r.getDate(2),
                    r.getString(3),r.getString(4),
                    r.getString(5),r.getInt(6),r.getInt(7)});
            tblHistory.setModel(m);
        }catch(Exception e){}
    }

    void loadComboPelanggan(){
        try{
            cmbPelanggan.removeAllItems();
            ResultSet r=con.createStatement().executeQuery(
                "SELECT id_pelanggan,nama FROM pelanggan");
            while(r.next())
                cmbPelanggan.addItem(
                    r.getString(1)+" - "+r.getString(2));
        }catch(Exception e){}
    }

    void loadComboBarang(){
        try{
            cmbBarang.removeAllItems();
            ResultSet r=con.createStatement().executeQuery(
                "SELECT kode_barang,nama_barang FROM barang");
            while(r.next())
                cmbBarang.addItem(
                    r.getString(1)+" - "+r.getString(2));
        }catch(Exception e){}
    }

    public static void main(String[] args){
        new MenuUtama();
    }
}