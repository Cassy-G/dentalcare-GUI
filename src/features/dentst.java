
package features;

import config.config;
import java.awt.Color;
import internal.session;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 *
 * @author Cassandra Gallera
 */
public class dentst extends javax.swing.JFrame {
 int xMouse, yMouse;

 
private boolean isValidEmail(String email) {

    // Accepts user@domain and user@domain.123 or user@domain.ph
    String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+(\\.[A-Za-z0-9]{2,})?$";
    return email != null && email.matches(emailRegex);
}





    /**
     * Creates new form log
     */
public dentst() {
    initComponents();
    loadSchedule();
    

    // 🔒 login/role checks
    if (session.getId() == 0) {
        JOptionPane.showMessageDialog(this, "Please login first.");
        new login().setVisible(true);
        this.dispose();
        return;
    }
    if (!session.getRole().equalsIgnoreCase("dentist")) {
        JOptionPane.showMessageDialog(this, "Access Denied. Dentists only.");
        this.dispose();
        return;
    }

    // ✅ Show name only, styled smaller
    dr.setText(session.getName());
    Font headerFont = new Font("Segoe UI", Font.BOLD, 15); // clean, smaller size
    dr.setFont(headerFont);
    dr.setForeground(new Color(0, 102, 153)); // professional teal-blue

  // New
    dr.setText(session.getName());

        // ✅ Apply uniform font styling
    Font uniformFont = new Font("Arial", Font.PLAIN, 14);
    name.setFont(uniformFont);
    email.setFont(uniformFont);
    contact.setFont(uniformFont);
    change_name.setFont(uniformFont);
    change_email.setFont(uniformFont);
    change_contact.setFont(uniformFont);
    newpass.setFont(uniformFont);
    confirmnewpass.setFont(uniformFont);
    
    forEDITPICTURE.setPreferredSize(new Dimension(150, 120));
    forEDITPICTURE.setMaximumSize(new Dimension(150, 120));
    forEDITPICTURE.setMinimumSize(new Dimension(150, 120));
    forEDITPICTURE.setHorizontalAlignment(SwingConstants.CENTER);
    forEDITPICTURE.setVerticalAlignment(SwingConstants.CENTER);
    forEDITPICTURE.setBorder(null); // remove extra border spacing

    savePicHereFirst.setPreferredSize(new Dimension(150, 120));
    savePicHereFirst.setMaximumSize(new Dimension(150, 120));
    savePicHereFirst.setMinimumSize(new Dimension(150, 120));
    savePicHereFirst.setHorizontalAlignment(SwingConstants.CENTER);
    savePicHereFirst.setVerticalAlignment(SwingConstants.CENTER);
    savePicHereFirst.setBorder(null);
    
    


     loadprofile();        // schedules, etc.
     loadProfileDisplay(); // ✅ auto-load profile info after login



     save.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        String email = change_email.getText();
     if (email.isEmpty() || !isValidEmail(email)) {

     email = email.trim();

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(null, "Invalid email address.");
            return; // stop save if invalid
        }
     }
        try (Connection conn = config.connectDB()) {
            conn.setAutoCommit(false);

            updateAccountInfo(conn);        // ✅ pass connection
            updateDentistSpecialty(conn);   // ✅ also pass connection

            conn.commit();
            JOptionPane.showMessageDialog(null, "Profile updated successfully!");
            loadprofile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Update failed. Changes reverted.\n" + e.getMessage());
        }
    }
  });

    // Attach Cancel button listener
     cancelMYprofile.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            loadprofile(); // reload original DB values
        }
    });
    
    

     editprofile.addMouseListener(new java.awt.event.MouseAdapter() {
     @Override
     public void mouseClicked(java.awt.event.MouseEvent evt) {
        set_specialization.setEnabled(true); // allow editing
        change_name.setEditable(true);
        change_email.setEditable(true);
        change_contact.setEditable(true);
    }
});


}

// ✅ Utility method to resize images
    private ImageIcon resizeImage(ImageIcon icon, int width, int height) {
    Image img = icon.getImage();
    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImg);
}
 


  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        dashbox = new javax.swing.JPanel();
        dashpnl = new javax.swing.JPanel();
        dashbtn = new javax.swing.JLabel();
        schedpnl = new javax.swing.JPanel();
        s = new javax.swing.JLabel();
        mpa = new javax.swing.JPanel();
        p = new javax.swing.JLabel();
        treatp = new javax.swing.JPanel();
        t = new javax.swing.JLabel();
        sett = new javax.swing.JPanel();
        se = new javax.swing.JLabel();
        lg = new javax.swing.JPanel();
        logout = new javax.swing.JLabel();
        hdr = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        XPNL = new javax.swing.JPanel();
        XBTN = new javax.swing.JLabel();
        cicrcle_profile = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dr = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        dentist = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dashTb = new javax.swing.JTabbedPane();
        db = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        tp = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        completed = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        inProg = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        upcoming = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        sched = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        startTimeCombo = new javax.swing.JComboBox<>();
        jLabel54 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        monCheck = new javax.swing.JCheckBox();
        jPanel24 = new javax.swing.JPanel();
        tueCheck = new javax.swing.JCheckBox();
        jPanel25 = new javax.swing.JPanel();
        wedCheck = new javax.swing.JCheckBox();
        jPanel26 = new javax.swing.JPanel();
        thurCheck = new javax.swing.JCheckBox();
        jPanel27 = new javax.swing.JPanel();
        friCheck = new javax.swing.JCheckBox();
        satCheck = new javax.swing.JCheckBox();
        jPanel29 = new javax.swing.JPanel();
        sunCheck = new javax.swing.JCheckBox();
        jPanel19 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        scheduleTable = new javax.swing.JTable();
        endTimeCombo = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        saveSchedule = new javax.swing.JLabel();
        patient = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        treatment = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        mp1 = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        addpicture1 = new javax.swing.JPanel();
        change_photo = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        forEDITPICTURE = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jPanel46 = new javax.swing.JPanel();
        role_myprofile = new javax.swing.JLabel();
        set_specialization = new javax.swing.JComboBox<>();
        change_name = new javax.swing.JTextField();
        change_email = new javax.swing.JTextField();
        change_contact = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        confirmnewpass = new javax.swing.JTextField();
        newpass = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jPanel47 = new javax.swing.JPanel();
        cancelChangesEditProfile = new javax.swing.JLabel();
        jPanel45 = new javax.swing.JPanel();
        save = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        patient1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        mp = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        addpicture = new javax.swing.JPanel();
        savePicHereFirst = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        name = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPanel41 = new javax.swing.JPanel();
        contact = new javax.swing.JLabel();
        jPanel40 = new javax.swing.JPanel();
        email = new javax.swing.JLabel();
        Dr_lastname = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        SAVECHANGES = new javax.swing.JLabel();
        newpass_myprofile = new javax.swing.JPasswordField();
        confirmpass_myprofile = new javax.swing.JPasswordField();
        jPanel42 = new javax.swing.JPanel();
        changePhoto = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        editprofile = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        cancelpane = new javax.swing.JPanel();
        cancelMYprofile = new javax.swing.JLabel();
        jPanel48 = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        displaySpecialty = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        bg.setBackground(new java.awt.Color(153, 204, 255));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashbox.setBackground(new java.awt.Color(255, 255, 255));
        dashbox.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashpnl.setBackground(new java.awt.Color(255, 255, 255));
        dashpnl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashbtn.setBackground(new java.awt.Color(255, 255, 255));
        dashbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        dashbtn.setForeground(new java.awt.Color(0, 51, 204));
        dashbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-dashboard-layout-24.png"))); // NOI18N
        dashbtn.setText("Dashboard");
        dashbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashbtnMouseExited(evt);
            }
        });
        dashpnl.add(dashbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 160, 30));

        dashbox.add(dashpnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, 30));

        schedpnl.setBackground(new java.awt.Color(255, 255, 255));
        schedpnl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        s.setBackground(new java.awt.Color(255, 255, 255));
        s.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        s.setForeground(new java.awt.Color(0, 51, 204));
        s.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-today-24.png"))); // NOI18N
        s.setText("My Schedule");
        s.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sMouseExited(evt);
            }
        });
        schedpnl.add(s, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 160, 30));

        dashbox.add(schedpnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 30));

        mpa.setBackground(new java.awt.Color(255, 255, 255));
        mpa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        p.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        p.setForeground(new java.awt.Color(0, 51, 204));
        p.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-users-24.png"))); // NOI18N
        p.setText("My Patients");
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pMouseExited(evt);
            }
        });
        mpa.add(p, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 160, 30));

        dashbox.add(mpa, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 30));

        treatp.setBackground(new java.awt.Color(255, 255, 255));
        treatp.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        t.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        t.setForeground(new java.awt.Color(0, 51, 204));
        t.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        t.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-treatment-24.png"))); // NOI18N
        t.setText("Treatment Plans");
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tMouseExited(evt);
            }
        });
        treatp.add(t, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 160, 30));

        dashbox.add(treatp, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 170, 30));

        sett.setBackground(new java.awt.Color(255, 255, 255));
        sett.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        se.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        se.setForeground(new java.awt.Color(0, 51, 204));
        se.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-settings-24.png"))); // NOI18N
        se.setText("Settings");
        se.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                seMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                seMouseExited(evt);
            }
        });
        sett.add(se, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 160, 30));

        dashbox.add(sett, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 170, 30));

        lg.setBackground(new java.awt.Color(255, 255, 255));
        lg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logout.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        logout.setForeground(new java.awt.Color(0, 51, 204));
        logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-logout-24.png"))); // NOI18N
        logout.setText("Logout");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutMouseExited(evt);
            }
        });
        lg.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 150, 30));

        dashbox.add(lg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 170, 30));

        bg.add(dashbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 170, 470));

        hdr.setBackground(new java.awt.Color(255, 255, 255));
        hdr.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                hdrMouseDragged(evt);
            }
        });
        hdr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                hdrMousePressed(evt);
            }
        });
        hdr.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/output-onlinepngtools__3_-removebg-preview.png"))); // NOI18N
        hdr.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        XBTN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        XBTN.setText("X");
        XBTN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                XBTNMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                XBTNMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                XBTNMouseExited(evt);
            }
        });

        javax.swing.GroupLayout XPNLLayout = new javax.swing.GroupLayout(XPNL);
        XPNL.setLayout(XPNLLayout);
        XPNLLayout.setHorizontalGroup(
            XPNLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(XBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );
        XPNLLayout.setVerticalGroup(
            XPNLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(XBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        hdr.add(XPNL, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 40, 30));

        cicrcle_profile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-circle-48.png"))); // NOI18N
        hdr.add(cicrcle_profile, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, -4, 50, 60));

        jLabel2.setBackground(new java.awt.Color(51, 102, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("Dentist ");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        hdr.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 30, 60, 20));

        dr.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        dr.setForeground(new java.awt.Color(51, 51, 51));
        dr.setText("Dr.");
        hdr.add(dr, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, 120, 20));

        jLabel62.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(0, 102, 255));
        jLabel62.setText("Dental");
        hdr.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, -1, 50));

        jLabel66.setFont(new java.awt.Font("Modern No. 20", 3, 17)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(0, 0, 255));
        jLabel66.setText("Care");
        hdr.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 40, 50));

        dentist.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        dentist.setText("ff");
        hdr.add(dentist, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, 150, 40));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/5046faad4a4c9af72bcf4fe75c8a11d0.jpg"))); // NOI18N
        hdr.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 50));

        bg.add(hdr, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 50));

        db.setBackground(new java.awt.Color(255, 255, 255));
        db.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Today's Patients");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 110, 50));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-appointment-25.png"))); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 60));

        tp.setText("jLabel12");
        jPanel1.add(tp, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, -1, -1));

        db.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 193, 80));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 21)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 102));
        jLabel5.setText("Dentist Dashboard");
        db.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 210, -1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        completed.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        completed.setForeground(new java.awt.Color(51, 51, 255));
        completed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        completed.setText("Treatments Completed");
        jPanel2.add(completed, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 160, 50));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-check-mark-25.png"))); // NOI18N
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 50, 60));

        jLabel14.setText("jLabel14");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, -1));

        db.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, 193, 80));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("In Progress Cases");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 130, 50));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-in-progress-25.png"))); // NOI18N
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 60, 60));

        inProg.setText("jLabel16");
        jPanel3.add(inProg, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, -1));

        db.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 90, 193, 80));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-schedule-25.png"))); // NOI18N
        jLabel10.setText("Upcoming Schedule");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 50));

        upcoming.setText("jLabel18");
        jPanel4.add(upcoming, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        db.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 193, 80));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-users-25.png"))); // NOI18N
        jLabel6.setText("Active Patients");
        jPanel5.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 150, 30));

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Access patient records");
        jPanel5.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 190, -1));

        db.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 200, 193, 80));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-treatment-25.png"))); // NOI18N
        jLabel19.setText("Treatment Progress");
        jPanel6.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 190, 30));

        jLabel20.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(102, 102, 102));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Manage active treatments");
        jPanel6.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 180, -1));

        db.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 200, 193, 80));

        jLabel41.setForeground(new java.awt.Color(204, 204, 204));
        jLabel41.setText("_____________________________________________________________________________________");
        db.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jLabel42.setForeground(new java.awt.Color(204, 204, 204));
        jLabel42.setText("_____________________________________________________________________________________");
        db.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        jLabel43.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(0, 51, 102));
        jLabel43.setText("Recent Activity");
        db.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        db.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 590, 140));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        db.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 640, 500));

        dashTb.addTab("dashboard", db);

        sched.setBackground(new java.awt.Color(255, 255, 255));
        sched.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setFont(new java.awt.Font("Times New Roman", 1, 22)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 51, 102));
        jLabel24.setText("Set your Schedule");
        sched.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 40));

        jLabel25.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(204, 204, 204));
        jLabel25.setText("_______________________________________________________________________________________");
        sched.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        jLabel46.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel46.setText("Select Days and Time");
        sched.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        startTimeCombo.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        startTimeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM" }));
        sched.add(startTimeCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 410, 110, 30));

        jLabel54.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel54.setText("Work Hours");
        sched.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, -1, 50));

        jPanel11.setBackground(new java.awt.Color(102, 255, 255));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));

        monCheck.setBackground(new java.awt.Color(255, 255, 255));
        monCheck.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        monCheck.setText(" Monday");
        monCheck.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        monCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monCheckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(monCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 24, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(monCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel11.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 120, 30));

        jPanel24.setBackground(new java.awt.Color(255, 255, 255));

        tueCheck.setBackground(new java.awt.Color(255, 255, 255));
        tueCheck.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        tueCheck.setText("Tuesday");
        tueCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tueCheckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addComponent(tueCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 23, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tueCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel11.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 120, 30));

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));
        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        wedCheck.setBackground(new java.awt.Color(255, 255, 255));
        wedCheck.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        wedCheck.setText("Wednsday");
        jPanel25.add(wedCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        jPanel11.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 120, 30));

        jPanel26.setBackground(new java.awt.Color(255, 255, 255));
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        thurCheck.setBackground(new java.awt.Color(255, 255, 255));
        thurCheck.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        thurCheck.setText("Thursday");
        thurCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thurCheckActionPerformed(evt);
            }
        });
        jPanel26.add(thurCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        jPanel11.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 120, 30));

        jPanel27.setBackground(new java.awt.Color(255, 255, 255));
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        friCheck.setBackground(new java.awt.Color(255, 255, 255));
        friCheck.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        friCheck.setText(" Friday");
        friCheck.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 153, 255), 2));
        friCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                friCheckActionPerformed(evt);
            }
        });
        jPanel27.add(friCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        jPanel11.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 120, 30));

        satCheck.setBackground(new java.awt.Color(255, 255, 255));
        satCheck.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        satCheck.setText(" Saturday");
        satCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                satCheckActionPerformed(evt);
            }
        });
        jPanel11.add(satCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 100, 30));

        jPanel29.setBackground(new java.awt.Color(255, 255, 255));

        sunCheck.setBackground(new java.awt.Color(255, 255, 255));
        sunCheck.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        sunCheck.setText("Sunday");
        sunCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sunCheckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(sunCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 21, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sunCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel11.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 250, 120, 30));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanel11.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, -1, 30));

        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/dsb.png"))); // NOI18N
        jPanel11.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, -14, 80, 310));

        sched.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, 100, 290));

        scheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(scheduleTable);

        sched.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 180, 410, 200));

        endTimeCombo.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        endTimeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM" }));
        sched.add(endTimeCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 410, 110, 30));

        jLabel26.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel26.setText("Start Time:");
        sched.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 410, 70, 30));

        jPanel7.setBackground(new java.awt.Color(0, 102, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveSchedule.setBackground(new java.awt.Color(255, 255, 255));
        saveSchedule.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        saveSchedule.setForeground(new java.awt.Color(255, 255, 255));
        saveSchedule.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveSchedule.setText("Save");
        saveSchedule.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveScheduleMouseClicked(evt);
            }
        });
        jPanel7.add(saveSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        sched.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 410, 90, 30));

        dashTb.addTab("schedule", sched);

        patient.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tbl);

        patient.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 620, 360));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 22)); // NOI18N
        jLabel4.setText("My Patients");
        patient.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setText("Manage Patient Records");
        patient.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        dashTb.addTab("patients", patient);

        treatment.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(jTable3);

        treatment.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 620, 300));

        jLabel27.setFont(new java.awt.Font("Times New Roman", 1, 22)); // NOI18N
        jLabel27.setText("Treamtment Plans");
        treatment.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jLabel28.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(51, 51, 51));
        jLabel28.setText("Monitor Ongoing treatments");
        treatment.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, 20));

        dashTb.addTab("treatmentp", treatment);

        mp1.setBackground(new java.awt.Color(255, 255, 255));
        mp1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel32.setBackground(new java.awt.Color(255, 255, 255));
        jPanel32.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addpicture1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        change_photo.setBackground(new java.awt.Color(255, 255, 255));
        change_photo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel68.setFont(new java.awt.Font("Trebuchet MS", 1, 13)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 102, 204));
        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24 (1).png"))); // NOI18N
        jLabel68.setText("Change Photo");
        jLabel68.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel68MouseClicked(evt);
            }
        });
        change_photo.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 140, 30));

        addpicture1.add(change_photo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 140, 30));

        forEDITPICTURE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forEDITPICTURE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png"))); // NOI18N
        addpicture1.add(forEDITPICTURE, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 160));

        jPanel32.add(addpicture1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 180, 160));

        jLabel48.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel48.setText("Full Name:");
        jPanel32.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 130, -1, 30));

        jLabel50.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel50.setText("Email:");
        jPanel32.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 170, -1, 30));

        jLabel51.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel51.setText("Contact:");
        jPanel32.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 210, -1, 30));

        jLabel52.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel52.setText("Role");
        jPanel32.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 250, 40, 30));

        jLabel47.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel47.setText("My Profile");
        jPanel32.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, 40));

        jLabel67.setForeground(new java.awt.Color(204, 204, 204));
        jLabel67.setText("___________________________________________________________________________________");
        jPanel32.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, 40));

        jLabel69.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel69.setText("Specialization:");
        jPanel32.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 90, -1, 30));

        jPanel46.setBackground(new java.awt.Color(255, 255, 255));
        jPanel46.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel46.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        role_myprofile.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel46.add(role_myprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 110, 30));

        jPanel32.add(jPanel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 250, 260, 30));

        set_specialization.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        set_specialization.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "General Dentistry", "Cosmetic Dentistry", "Oral Surgery", "Endodontics", "Prosthodontics", "Orthodontics" }));
        set_specialization.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel32.add(set_specialization, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 90, 260, 30));

        change_name.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel32.add(change_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 130, 260, 30));

        change_email.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel32.add(change_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 260, 30));

        change_contact.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel32.add(change_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 210, 260, 30));

        jLabel71.setForeground(new java.awt.Color(204, 204, 204));
        jLabel71.setText("____________________________________________________________________________________");
        jPanel32.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 600, 30));

        jLabel72.setForeground(new java.awt.Color(204, 204, 204));
        jLabel72.setText("____________________________________________________________________________________");
        jPanel32.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, -1, -1));

        jLabel91.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel91.setText("Change Password");
        jPanel32.add(jLabel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, -1, 46));

        jLabel93.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel93.setText("New Password:");
        jPanel32.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(156, 360, 90, 30));

        confirmnewpass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel32.add(confirmnewpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 390, 270, 28));

        newpass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel32.add(newpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 360, 270, 28));

        jLabel94.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel94.setText("Confirm New Password:");
        jPanel32.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 390, -1, 30));

        jLabel73.setForeground(new java.awt.Color(204, 204, 204));
        jLabel73.setText("____________________________________________________________________________________");
        jPanel32.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, -1, -1));

        jPanel47.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel47.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cancelChangesEditProfile.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        cancelChangesEditProfile.setText("Cancel");
        cancelChangesEditProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelChangesEditProfileMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelChangesEditProfileMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelChangesEditProfileMouseExited(evt);
            }
        });
        jPanel47.add(cancelChangesEditProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, -1, 20));

        jPanel32.add(jPanel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 450, 100, 22));

        jPanel45.setBackground(new java.awt.Color(0, 51, 204));
        jPanel45.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        save.setFont(new java.awt.Font("Tw Cen MT", 1, 14)); // NOI18N
        save.setForeground(new java.awt.Color(255, 255, 255));
        save.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        save.setText("Save Changes");
        save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveMouseClicked(evt);
            }
        });
        jPanel45.add(save, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 22));

        jPanel32.add(jPanel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 450, 100, 22));

        jLabel70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel32.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 640, 510));

        mp1.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 640, 500));

        dashTb.addTab("editprofile", mp1);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        dashTb.addTab("tab8", jPanel8);

        patient1.setBackground(new java.awt.Color(255, 255, 255));
        patient1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Times New Roman", 1, 22)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 51, 102));
        jLabel17.setText("Settings");
        patient1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 30));

        jLabel18.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(204, 204, 204));
        jLabel18.setText("_____________________________________________________________________________________");
        patient1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 20));

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setBackground(new java.awt.Color(204, 204, 204));
        jLabel21.setForeground(new java.awt.Color(204, 204, 204));
        jLabel21.setText("_________________________________________________________________________");
        jPanel20.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 20));

        jLabel29.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 51, 204));
        jLabel29.setText("Update Personal Info");
        jPanel20.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 30));

        jLabel40.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 51, 204));
        jLabel40.setText("Change Password");
        jPanel20.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        patient1.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 530, 90));

        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        patient1.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 530, 140));

        jPanel21.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 51, 255));
        jLabel23.setText("Profile Settings");
        jPanel21.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 20));

        patient1.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 530, 40));

        dashTb.addTab("settings", patient1);

        mp.setBackground(new java.awt.Color(255, 255, 255));
        mp.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel30.setFont(new java.awt.Font("Times New Roman", 1, 21)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 102, 153));
        jLabel30.setText("Settings");
        mp.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, 50));

        jLabel12.setForeground(new java.awt.Color(204, 204, 204));
        jLabel12.setText("_______________________________________________________________________________________");
        mp.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, -1, 30));

        addpicture.setBackground(new java.awt.Color(204, 204, 204));
        addpicture.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        savePicHereFirst.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        savePicHereFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-photo-60.png"))); // NOI18N
        savePicHereFirst.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                savePicHereFirstMouseClicked(evt);
            }
        });
        addpicture.add(savePicHereFirst, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 120));

        mp.add(addpicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 150, 120));

        jLabel44.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel44.setText("Specialization:");
        mp.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 150, -1, 30));

        jLabel45.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel45.setText("Full Name: ");
        mp.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 190, -1, 30));

        jPanel30.setBackground(new java.awt.Color(255, 255, 255));
        jPanel30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel30.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        name.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jPanel30.add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 30));

        mp.add(jPanel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 190, 300, 30));

        jLabel33.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel33.setText("Email:");
        mp.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 230, -1, 30));

        jLabel34.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel34.setText("Contact:");
        mp.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 269, -1, 30));

        jPanel41.setBackground(new java.awt.Color(255, 255, 255));
        jPanel41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel41.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        contact.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel41.add(contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 30));

        mp.add(jPanel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 270, 300, 30));

        jPanel40.setBackground(new java.awt.Color(255, 255, 255));
        jPanel40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel40.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        email.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel40.add(email, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 230, 30));

        mp.add(jPanel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 230, 300, 30));

        Dr_lastname.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        mp.add(Dr_lastname, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, 190, 30));

        jLabel31.setForeground(new java.awt.Color(204, 204, 204));
        jLabel31.setText("_______________________________________________________________________________________");
        mp.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, -1, 30));

        jLabel32.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel32.setText("Change Password");
        mp.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 319, -1, 40));

        jLabel35.setForeground(new java.awt.Color(204, 204, 204));
        jLabel35.setText("_______________________________________________________________________________________");
        mp.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, -1, 30));

        jLabel59.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel59.setText("New Password:");
        mp.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 370, -1, 30));

        jLabel60.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel60.setText("Confirm New Password:");
        mp.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 400, -1, 30));

        jPanel31.setBackground(new java.awt.Color(0, 51, 255));
        jPanel31.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SAVECHANGES.setBackground(new java.awt.Color(255, 255, 255));
        SAVECHANGES.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        SAVECHANGES.setForeground(new java.awt.Color(255, 255, 255));
        SAVECHANGES.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SAVECHANGES.setText("Save Changes");
        SAVECHANGES.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SAVECHANGESMouseClicked(evt);
            }
        });
        jPanel31.add(SAVECHANGES, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 22));

        mp.add(jPanel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 460, 100, 22));
        mp.add(newpass_myprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 370, 270, 28));
        mp.add(confirmpass_myprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 400, 270, 30));

        jPanel42.setBackground(new java.awt.Color(0, 51, 255));
        jPanel42.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        changePhoto.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        changePhoto.setForeground(new java.awt.Color(255, 255, 255));
        changePhoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        changePhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-image-24.png"))); // NOI18N
        changePhoto.setText("Add Photo");
        changePhoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                changePhotoMouseClicked(evt);
            }
        });
        jPanel42.add(changePhoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 130, 30));

        mp.add(jPanel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 110, 130, 30));

        jPanel43.setBackground(new java.awt.Color(0, 51, 255));
        jPanel43.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel43.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        editprofile.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        editprofile.setForeground(new java.awt.Color(255, 255, 255));
        editprofile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-20.png"))); // NOI18N
        editprofile.setText("Edit Profile");
        editprofile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editprofileMouseClicked(evt);
            }
        });
        jPanel43.add(editprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, 30));

        mp.add(jPanel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, 130, 30));

        jLabel63.setForeground(new java.awt.Color(204, 204, 204));
        jLabel63.setText("_______________________________________________________________________________________");
        mp.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 30));

        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        mp.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 361, 610, 80));

        cancelpane.setBackground(new java.awt.Color(255, 255, 255));
        cancelpane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cancelpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cancelMYprofile.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        cancelMYprofile.setText("Cancel");
        cancelMYprofile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelMYprofileMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelMYprofileMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelMYprofileMouseExited(evt);
            }
        });
        cancelpane.add(cancelMYprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, -1, 20));

        mp.add(cancelpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 460, 100, 22));

        jPanel48.setBackground(new java.awt.Color(255, 255, 255));
        jPanel48.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel48.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel49.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(0, 102, 255));
        jLabel49.setText("Set Specialty");
        jLabel49.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel49MouseClicked(evt);
            }
        });
        jPanel48.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, 30));

        mp.add(jPanel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 150, 300, 30));

        displaySpecialty.setFont(new java.awt.Font("Trebuchet MS", 1, 15)); // NOI18N
        displaySpecialty.setForeground(new java.awt.Color(0, 51, 102));
        displaySpecialty.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        displaySpecialty.setText("jLabel36");
        mp.add(displaySpecialty, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 150, 20));

        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        mp.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 610, 260));

        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jLabel61.setToolTipText("");
        jLabel61.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        mp.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 640, 500));

        dashTb.addTab("myprofile", mp);

        bg.add(dashTb, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 640, 520));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void hdrMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrMouseDragged
        // TODO add your handling code here:
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse,y - yMouse);
    }//GEN-LAST:event_hdrMouseDragged

    private void hdrMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrMousePressed
        // TODO add your handling code here:
                xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_hdrMousePressed

    private void XBTNMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_XBTNMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_XBTNMouseClicked

    private void XBTNMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_XBTNMouseEntered
        // TODO add your handling code here:
        XPNL.setBackground(Color. red);
        XBTN.setForeground(Color.white);
    }//GEN-LAST:event_XBTNMouseEntered

    private void XBTNMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_XBTNMouseExited
        // TODO add your handling code here:
        XPNL.setBackground(Color. white);
        XBTN.setForeground(Color.black);
    }//GEN-LAST:event_XBTNMouseExited

    private void dashbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbtnMouseEntered
        // TODO add your handling code here:
        dashpnl.setBackground(Color. blue);
                dashbtn.setForeground(Color.white);
    }//GEN-LAST:event_dashbtnMouseEntered

    private void dashbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbtnMouseClicked
        dashTb.setSelectedIndex(0);

    }//GEN-LAST:event_dashbtnMouseClicked

    private void dashbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbtnMouseExited
        dashpnl.setBackground(Color. white);
        dashbtn.setForeground(new Color(0, 51, 204));  
        
    }//GEN-LAST:event_dashbtnMouseExited

    private void sMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sMouseClicked
        dashTb.setSelectedIndex(1);
    }//GEN-LAST:event_sMouseClicked

    private void pMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pMouseClicked
        dashTb.setSelectedIndex(2);
    }//GEN-LAST:event_pMouseClicked

    private void tMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tMouseClicked
        dashTb.setSelectedIndex(3);
    }//GEN-LAST:event_tMouseClicked

    private void sMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sMouseEntered
        schedpnl.setBackground(Color. blue);
        s.setForeground(Color.white);
    }//GEN-LAST:event_sMouseEntered

    private void sMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sMouseExited

        schedpnl.setBackground(Color. white);
        s.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_sMouseExited

    private void pMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pMouseEntered
        // TODO add your handling code here:
        mpa.setBackground(Color. blue);
        p.setForeground(Color.white);
    }//GEN-LAST:event_pMouseEntered

    private void pMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pMouseExited
               mpa.setBackground(Color. white);
        p.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_pMouseExited

    private void tMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tMouseEntered
        // TODO add your handling code here:
        treatp.setBackground(Color. blue);
        t.setForeground(Color.white);
    }//GEN-LAST:event_tMouseEntered

    private void tMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tMouseExited
            treatp.setBackground(Color. white);
        t.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_tMouseExited

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
               
 int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Logout",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {

        // ✅ Log the logout event
        int actorId = session.getId();
        String actorRole = session.getRole();
        String actorName = session.getName();

        try (Connection con = config.connectDB()) {
            String logSql = "INSERT INTO tbl_logs (actor_id, actor_role, action, details, created_at) " +
                            "VALUES (?, ?, ?, ?, datetime('now'))";
            PreparedStatement pst = con.prepareStatement(logSql);
            pst.setInt(1, actorId);
            pst.setString(2, actorRole);
            pst.setString(3, "Logout");
            pst.setString(4, actorName + " logged out");
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Clear session
        session.clear();

        // Go back to login page
        landingp home = new landingp();
        this.dispose();
        home.setVisible(true);
    }
    }//GEN-LAST:event_logoutMouseClicked

    private void logoutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseEntered
        lg.setBackground(Color. red);
        logout.setForeground(Color.white);
    }//GEN-LAST:event_logoutMouseEntered

    private void logoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseExited
            lg.setBackground(Color. white);
          logout.setForeground(new Color(0, 51, 204));
    }//GEN-LAST:event_logoutMouseExited

    private void saveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveMouseClicked
    String newName = change_name.getText().trim();
    String newEmail = change_email.getText().trim();
    String newContact = change_contact.getText().trim();
    String newPassVal = newpass.getText().trim();
    String confirmPassVal = confirmnewpass.getText().trim();

    if (newName.isEmpty() && newEmail.isEmpty() && newContact.isEmpty() && newPassVal.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No fields to update!");
        return;
    }

    // ✅ Validate email
    if (!newEmail.isEmpty() && !isValidEmail(newEmail)) {
        JOptionPane.showMessageDialog(this, "Invalid email address.");
        return;
    }

    // ✅ Validate password
    if (!newPassVal.isEmpty() && !newPassVal.equals(confirmPassVal)) {
        JOptionPane.showMessageDialog(this, "Passwords do not match.");
        return;
    }

    try (Connection con = config.connectDB()) {
        con.setAutoCommit(false);

        // --- Check email uniqueness only if changed ---
        if (!newEmail.isEmpty()) {
            String checkSql = "SELECT acc_id FROM tbl_accounts WHERE acc_email=? AND acc_id<>?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setString(1, newEmail);
            checkPs.setInt(2, session.getId());
            ResultSet rsCheck = checkPs.executeQuery();
            if (rsCheck.next()) {
                JOptionPane.showMessageDialog(this, "Email already in use by another account.");
                return;
            }
        }

        // --- Update name/email/contact/photo ---
        StringBuilder sql = new StringBuilder("UPDATE tbl_accounts SET ");
        List<Object> params = new ArrayList<>();
        if (!newName.isEmpty()) { sql.append("acc_name=?, "); params.add(newName); }
        if (!newEmail.isEmpty()) { sql.append("acc_email=?, "); params.add(newEmail); }
        if (!newContact.isEmpty()) { sql.append("acc_contact=?, "); params.add(newContact); }

        Icon icon = forEDITPICTURE.getIcon();
        if (icon != null && icon instanceof ImageIcon) {
            String imgPath = ((ImageIcon) icon).getDescription();
            if (imgPath != null && !imgPath.trim().isEmpty()) {
                sql.append("acc_pic=?, ");
                params.add(imgPath);
            }
        }

        if (params.size() > 0) {
            sql.setLength(sql.length() - 2); // remove last comma
            sql.append(" WHERE acc_id=?");
            params.add(session.getId());

            try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pst.setObject(i + 1, params.get(i));
                }
                pst.executeUpdate();
            }
        }

        // --- Update password if provided ---
        if (!newPassVal.isEmpty()) {
            String hashedPass = config.hashPassword(newPassVal);
            PreparedStatement psPass = con.prepareStatement(
                "UPDATE tbl_accounts SET acc_pass=? WHERE acc_id=?"
            );
            psPass.setString(1, hashedPass);
            psPass.setInt(2, session.getId());
            psPass.executeUpdate();
        }

String selectedSpecialty = (String) set_specialization.getSelectedItem();
if (selectedSpecialty != null && !selectedSpecialty.trim().isEmpty()) {
    PreparedStatement psSpec = con.prepareStatement(
        "UPDATE tbl_dentists SET specialty=? WHERE dentist_id=?"
    );
    psSpec.setString(1, selectedSpecialty);
    psSpec.setInt(2, session.getDentistId());
    psSpec.executeUpdate();
}

// ✅ Update header label with new name
dr.setText(change_name.getText());

// After successful update in EditProfile
name.setText(change_name.getText());
email.setText(change_email.getText());
contact.setText(change_contact.getText());

        con.commit();

        JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        loadprofile();       // refresh My Profile
        loadProfileEdit();   // refresh Edit Profile
        // ✅ Clear fields after save
change_name.setText("");
change_email.setText("");
change_contact.setText("");
newpass.setText("");
confirmnewpass.setText("");

// Lock fields back
change_name.setEditable(false);
change_email.setEditable(false);
change_contact.setEditable(false);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
    }
    }//GEN-LAST:event_saveMouseClicked

    private void sunCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sunCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sunCheckActionPerformed

    private void satCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_satCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_satCheckActionPerformed

    private void friCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_friCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_friCheckActionPerformed

    private void thurCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thurCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_thurCheckActionPerformed

    private void tueCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tueCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tueCheckActionPerformed

    private void monCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_monCheckActionPerformed

    private void seMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seMouseEntered
             // TODO add your handling code here:
        sett.setBackground(Color. blue);
        se.setForeground(Color.white);
    }//GEN-LAST:event_seMouseEntered

    private void seMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seMouseExited
        sett.setBackground(Color. white);
        se.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_seMouseExited

    private void editprofileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editprofileMouseClicked
    dashTb.setSelectedIndex(4);   // Switch to MyProfile tab
    loadProfileDisplay();         // Load profile info automatically
    }//GEN-LAST:event_editprofileMouseClicked

    private void savePicHereFirstMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_savePicHereFirstMouseClicked

    }//GEN-LAST:event_savePicHereFirstMouseClicked

    private void seMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seMouseClicked
      dashTb.setSelectedIndex(7);
    }//GEN-LAST:event_seMouseClicked

    private void changePhotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePhotoMouseClicked
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(
        new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "png", "jpeg", "gif"
        )
    );

    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();

        if (file != null && file.exists()) {
            // Show preview
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            icon.setDescription(file.getAbsolutePath()); // store path

            forEDITPICTURE.setIcon(resizeImage(icon, 150, 120));
            savePicHereFirst.setIcon(resizeImage(icon, 150, 120));

            // ✅ Save path directly to DB
            try (Connection conn = config.connectDB();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE tbl_accounts SET acc_pic=? WHERE acc_id=?")) {

                ps.setString(1, file.getAbsolutePath());
                ps.setInt(2, session.getId());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Profile photo updated successfully!");
                loadProfileDisplay(); // refresh photo after save
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to update photo.\n" + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid file selected.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "No photo selected.");
    }
    }//GEN-LAST:event_changePhotoMouseClicked

    private void SAVECHANGESMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SAVECHANGESMouseClicked
     try (Connection conn = config.connectDB()) {
        conn.setAutoCommit(false);

        // --- Handle photo ---
        Icon icon = savePicHereFirst.getIcon();
        if (icon instanceof ImageIcon) {
            ImageIcon imgIcon = (ImageIcon) icon;
            String imgPath = imgIcon.getDescription();

            if (imgPath != null && !imgPath.trim().isEmpty()) {
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tbl_accounts SET acc_pic=? WHERE acc_id=?"
                );
                ps.setString(1, imgPath);
                ps.setInt(2, session.getId());
                ps.executeUpdate();
            }
        }

        // --- Handle password ---
        String newPassVal = new String(newpass_myprofile.getPassword()).trim();
        String confirmPassVal = new String(confirmpass_myprofile.getPassword()).trim();

        if (!newPassVal.isEmpty() || !confirmPassVal.isEmpty()) {
            if (newPassVal.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.");
                return;
            }
            if (!newPassVal.equals(confirmPassVal)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }

            String hashedPass = config.hashPassword(newPassVal);
            PreparedStatement psPass = conn.prepareStatement(
                "UPDATE tbl_accounts SET acc_pass=? WHERE acc_id=?"
            );
            psPass.setString(1, hashedPass);
            psPass.setInt(2, session.getId());
            psPass.executeUpdate();
        }

        conn.commit();

        JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        loadprofile(); // refresh profile info

        // ✅ Clear password fields
        newpass_myprofile.setText("");
        confirmpass_myprofile.setText("");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage());
    }
    }//GEN-LAST:event_SAVECHANGESMouseClicked

    private void cancelMYprofileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelMYprofileMouseClicked
       // Get current values from My Profile edit fields
    String currentName = change_name.getText().trim();
    String currentEmail = change_email.getText().trim();
    String currentContact = change_contact.getText().trim();
    String currentPass = newpass.getText().trim();
    String confirmPass = confirmnewpass.getText().trim();

    // Compare with original DB values shown in labels
    boolean modified = !currentName.equals(name.getText()) ||
                       !currentEmail.equals(email.getText()) ||
                       !currentContact.equals(contact.getText()) ||
                       !currentPass.isEmpty() ||
                       !confirmPass.isEmpty();

    // --- Check photo changes ---
    Icon editPhoto = forEDITPICTURE.getIcon();
    Icon originalPhoto = savePicHereFirst.getIcon();
    if (editPhoto != null && originalPhoto != null) {
        if (editPhoto instanceof ImageIcon && originalPhoto instanceof ImageIcon) {
            String editDesc = ((ImageIcon) editPhoto).getDescription();
            String origDesc = ((ImageIcon) originalPhoto).getDescription();
            if (editDesc != null && origDesc != null && !editDesc.equals(origDesc)) {
                modified = true;
            }
        }
    }

    // --- Validation result ---
    if (modified) {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "You have unsaved changes. Do you really want to cancel?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            loadprofile(); // reload original DB values
            JOptionPane.showMessageDialog(this, "Changes discarded.");
        }
    } else {
        // No changes → just reload silently
        loadprofile();
        loadProfileDisplay(); // refresh both main and circle profile photos

    }
    }//GEN-LAST:event_cancelMYprofileMouseClicked

    private void jLabel68MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel68MouseClicked
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(
        new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "png", "jpeg", "gif"
        )
    );

    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
        String path = chooser.getSelectedFile().getAbsolutePath();

        try (Connection con = config.connectDB();
             PreparedStatement pst = con.prepareStatement(
                 "UPDATE tbl_accounts SET acc_pic = ? WHERE acc_id = ?"
             )) {

            pst.setString(1, path);
            pst.setInt(2, session.getId());
            pst.executeUpdate();

            // ✅ Commit changes
            con.commit();

            // ✅ Reload picture in UI
            loadProfileDisplay();

            JOptionPane.showMessageDialog(
                this,
                "Profile picture updated successfully!"
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error saving photo: " + ex.getMessage()
            );
            ex.printStackTrace();
        }
    } else {
        // User cancelled the file chooser
        JOptionPane.showMessageDialog(this, "No photo selected.");
    }
    }//GEN-LAST:event_jLabel68MouseClicked

    private void cancelChangesEditProfileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelChangesEditProfileMouseClicked
  // Get current values from Edit Profile fields
    String currentName = change_name.getText().trim();
    String currentEmail = change_email.getText().trim();
    String currentContact = change_contact.getText().trim();
    String currentPass = newpass.getText().trim();
    String confirmPass = confirmnewpass.getText().trim();

    // Compare with original values from My Profile labels
    boolean modified = !currentName.equals(name.getText()) ||
                       !currentEmail.equals(email.getText()) ||
                       !currentContact.equals(contact.getText()) ||
                       !currentPass.isEmpty() ||
                       !confirmPass.isEmpty();

    // --- Check photo changes ---
    Icon editPhoto = forEDITPICTURE.getIcon();
    Icon originalPhoto = savePicHereFirst.getIcon();
    if (editPhoto != null && originalPhoto != null) {
        if (editPhoto instanceof ImageIcon && originalPhoto instanceof ImageIcon) {
            String editDesc = ((ImageIcon) editPhoto).getDescription();
            String origDesc = ((ImageIcon) originalPhoto).getDescription();
            if (editDesc != null && origDesc != null && !editDesc.equals(origDesc)) {
                modified = true;
            }
        }
    }

    // --- Validation result ---
    if (modified) {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "You have unsaved changes. Do you really want to cancel?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            loadProfileDisplay(); // reload original DB values including photo
            JOptionPane.showMessageDialog(this, "Changes discarded.");
        }
    } else {
        // No modifications → just reload profile silently
        loadProfileDisplay();
    }

    // 🔒 Always lock fields back after cancel
    change_name.setEditable(false);
    change_email.setEditable(false);
    change_contact.setEditable(false);
    newpass.setText("");
    confirmnewpass.setText("");
    }//GEN-LAST:event_cancelChangesEditProfileMouseClicked

    private void cancelMYprofileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelMYprofileMouseEntered
   // On hover: light gray background with blue text
    cancelMYprofile.setBackground(new Color(230, 230, 230));   // soft gray background
    cancelMYprofile.setForeground(new Color(0, 51, 204));      // DentalCare blue text              // whit
    }//GEN-LAST:event_cancelMYprofileMouseEntered

    private void cancelMYprofileMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelMYprofileMouseExited
    cancelMYprofile.setBackground(new Color(255, 255, 255));  // white background
    cancelMYprofile.setForeground(new Color(0, 0, 0));        // black text
    }//GEN-LAST:event_cancelMYprofileMouseExited

    private void cancelChangesEditProfileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelChangesEditProfileMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelChangesEditProfileMouseEntered

    private void cancelChangesEditProfileMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelChangesEditProfileMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelChangesEditProfileMouseExited

    private void jLabel49MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel49MouseClicked
 dashTb.setSelectedIndex(4);      
    }//GEN-LAST:event_jLabel49MouseClicked

    private void saveScheduleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveScheduleMouseClicked
     String start = (String) startTimeCombo.getSelectedItem();
    String end   = (String) endTimeCombo.getSelectedItem();

    List<String> days = new ArrayList<>();
    if (monCheck.isSelected()) days.add("Monday");
    if (tueCheck.isSelected()) days.add("Tuesday");
    if (wedCheck.isSelected()) days.add("Wednesday");
    if (thurCheck.isSelected()) days.add("Thursday");
    if (friCheck.isSelected()) days.add("Friday");
    if (satCheck.isSelected()) days.add("Saturday");
    if (sunCheck.isSelected()) days.add("Sunday");

    if (start == null || end == null || days.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select valid start/end times and at least one work day.");
        return;
    }

    try {
        // Parse AM/PM input from combo box
        DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("h:mm a");
        DateTimeFormatter dbFmt    = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime startTime = LocalTime.parse(start, inputFmt);
        LocalTime endTime   = LocalTime.parse(end, inputFmt);

        if (!startTime.isBefore(endTime)) {
            JOptionPane.showMessageDialog(this, "Start time must be before end time.");
            return;
        }

        // Normalize to HH:mm for DB
        String startNormalized = startTime.format(dbFmt);
        String endNormalized   = endTime.format(dbFmt);
        String workDays        = String.join(",", days);

        try (Connection con = config.connectDB();
             PreparedStatement pst = con.prepareStatement(
                 "UPDATE tbl_dentists SET work_start=?, work_end=?, work_days=? WHERE dentist_id=?"
             )) {
            
            pst.setString(1, startNormalized);
            pst.setString(2, endNormalized);
            pst.setString(3, workDays);
            pst.setInt(4, session.getId());

            int updated = pst.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Schedule updated successfully!");
                loadSchedule(); // reload UI
            } else {
                JOptionPane.showMessageDialog(this, "No schedule updated. Dentist not found.");
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving schedule: " + e.getMessage());
    }
    }//GEN-LAST:event_saveScheduleMouseClicked

private void loadprofile() {
  String start = (String) startTimeCombo.getSelectedItem();
    String end   = (String) endTimeCombo.getSelectedItem();

    List<String> days = new ArrayList<>();
    if (monCheck.isSelected()) days.add("Monday");
    if (tueCheck.isSelected()) days.add("Tuesday");
    if (wedCheck.isSelected()) days.add("Wednesday");
    if (thurCheck.isSelected()) days.add("Thursday");
    if (friCheck.isSelected()) days.add("Friday");
    if (satCheck.isSelected()) days.add("Saturday");
    if (sunCheck.isSelected()) days.add("Sunday");

    if (start == null || end == null || days.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select valid start/end times and at least one work day.");
        return;
    }

    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(start, formatter);
        LocalTime endTime   = LocalTime.parse(end, formatter);

        if (!startTime.isBefore(endTime)) {
            JOptionPane.showMessageDialog(this, "Start time must be before end time.");
            return;
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid time format: " + ex.getMessage());
        return;
    }

    String workDays = String.join(",", days);

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(
             "UPDATE tbl_dentists SET work_start=?, work_end=?, work_days=? WHERE dentist_id=?"
         )) {
        
        pst.setString(1, start);
        pst.setString(2, end);
        pst.setString(3, workDays);
        pst.setInt(4, session.getId());

        int updated = pst.executeUpdate();
        if (updated > 0) {
            JOptionPane.showMessageDialog(this, "Schedule updated successfully!");
            // Audit log
            try (PreparedStatement logStmt = con.prepareStatement(
                "INSERT INTO tbl_logs (acc_id, action, timestamp) VALUES (?, ?, CURRENT_TIMESTAMP)"
            )) {
                logStmt.setInt(1, session.getId());
                logStmt.setString(2, "Updated schedule: " + workDays + " (" + start + " - " + end + ")");
                logStmt.executeUpdate();
            }
            loadSchedule(); // reload UI
        } else {
            JOptionPane.showMessageDialog(this, "No schedule updated. Dentist not found.");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving schedule: " + e.getMessage());
    }
}

private void loadSchedule() {
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(
             "SELECT work_start, work_end, work_days FROM tbl_dentists WHERE dentist_id=?"
         )) {

        pst.setInt(1, session.getId());
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // Reset checkboxes
            monCheck.setSelected(false);
            tueCheck.setSelected(false);
            wedCheck.setSelected(false);
            thurCheck.setSelected(false);
            friCheck.setSelected(false);
            satCheck.setSelected(false);
            sunCheck.setSelected(false);

            DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter uiFmt = DateTimeFormatter.ofPattern("h:mm a");

            String start = rs.getString("work_start");
            String end   = rs.getString("work_end");

            if (start != null && !start.isEmpty()) {
                LocalTime startTime = LocalTime.parse(start, dbFmt);
                startTimeCombo.setSelectedItem(startTime.format(uiFmt));
            } else {
                startTimeCombo.setSelectedItem("8:00 AM");
            }

            if (end != null && !end.isEmpty()) {
                LocalTime endTime = LocalTime.parse(end, dbFmt);
                endTimeCombo.setSelectedItem(endTime.format(uiFmt));
            } else {
                endTimeCombo.setSelectedItem("5:00 PM");
            }

            String workDays = rs.getString("work_days");
            List<String> days = new ArrayList<>();
            if (workDays != null && !workDays.isEmpty()) {
                days = Arrays.asList(workDays.split(","));
            }

            monCheck.setSelected(days.contains("Monday"));
            tueCheck.setSelected(days.contains("Tuesday"));
            wedCheck.setSelected(days.contains("Wednesday"));
            thurCheck.setSelected(days.contains("Thursday"));
            friCheck.setSelected(days.contains("Friday"));
            satCheck.setSelected(days.contains("Saturday"));
            sunCheck.setSelected(days.contains("Sunday"));
        } else {
            // Defaults if no schedule
            startTimeCombo.setSelectedItem("8:00 AM");
            endTimeCombo.setSelectedItem("5:00 PM");
            monCheck.setSelected(true);
            tueCheck.setSelected(true);
            wedCheck.setSelected(true);
            thurCheck.setSelected(true);
            friCheck.setSelected(true);
            satCheck.setSelected(false);
            sunCheck.setSelected(false);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading schedule: " + e.getMessage());
    }
}

private void saveSchedule() {
    String start = (String) startTimeCombo.getSelectedItem();
    String end   = (String) endTimeCombo.getSelectedItem();

    // Collect selected days
    List<String> days = new ArrayList<>();
    if (monCheck.isSelected()) days.add("Monday");
    if (tueCheck.isSelected()) days.add("Tuesday");
    if (wedCheck.isSelected()) days.add("Wednesday");
    if (thurCheck.isSelected()) days.add("Thursday");
    if (friCheck.isSelected()) days.add("Friday");
    if (satCheck.isSelected()) days.add("Saturday");
    if (sunCheck.isSelected()) days.add("Sunday");

    // Validation
    if (start == null || end == null) {
        JOptionPane.showMessageDialog(this, "Please select start and end times.");
        return;
    }
    if (days.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select at least one work day.");
        return;
    }

    // Ensure start < end
    try {
        LocalTime startTime = LocalTime.parse(start, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime   = LocalTime.parse(end, DateTimeFormatter.ofPattern("HH:mm"));
        if (!startTime.isBefore(endTime)) {
            JOptionPane.showMessageDialog(this, "Start time must be before end time.");
            return;
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid time format: " + ex.getMessage());
        return;
    }

    String workDays = String.join(",", days);

    // Save to DB
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(
             "UPDATE tbl_dentists SET work_start=?, work_end=?, work_days=? WHERE dentist_id=?"
         )) {
        pst.setString(1, start);
        pst.setString(2, end);
        pst.setString(3, workDays);
        pst.setInt(4, session.getId());

        int updated = pst.executeUpdate();
        if (updated > 0) {
            JOptionPane.showMessageDialog(this, "Schedule updated successfully!");
            loadSchedule(); // reload UI
        } else {
            JOptionPane.showMessageDialog(this, "No schedule updated. Dentist not found.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving schedule: " + e.getMessage());
    }
}


private void updateAccountInfo(Connection conn) {
    try {
        // ✅ Validate inputs before touching the DB
        String name = change_name.getText().trim();
        String email = change_email.getText().trim();
        String contact = change_contact.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty.");
            return;
        }
        if (email.isEmpty() || !isValidEmail(email)) {
            JOptionPane.showMessageDialog(null, "Invalid or empty email.");
            return;
        }
        if (contact.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Contact number cannot be empty.");
            return;
        }

        // ✅ Update query for tbl_accounts
        String sql = "UPDATE tbl_accounts SET acc_name=?, acc_email=?, acc_contact=? WHERE acc_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, contact);
        ps.setInt(4, session.getId()); // dentist/account ID from session

        ps.executeUpdate();

        // ✅ Audit log insert (matches your tbl_logs schema)
        String audit = "INSERT INTO tbl_logs(actor_id, actor_role, action, details) VALUES(?, ?, ?, ?)";
        PreparedStatement log = conn.prepareStatement(audit);
        log.setInt(1, session.getId());
        log.setString(2, session.getRole()); // e.g., "Dentist"
        log.setString(3, "Updated account info");
        log.setString(4, "Name: " + name + ", Email: " + email + ", Contact: " + contact);
        log.executeUpdate();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Account update failed: " + e.getMessage());
        throw new RuntimeException(e); // force rollback in outer try/catch
    }
}

private void updateDentistSpecialty(Connection conn) {
    try {
        // Check if dentist record exists (linked by acc_id)
        String checkSql = "SELECT dentist_id FROM tbl_dentists WHERE acc_id=?";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, session.getId());
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // Dentist exists → update
                String sql = "UPDATE tbl_dentists SET specialty=?, work_start=?, work_end=? WHERE acc_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, set_specialization.getSelectedItem().toString());
                    ps.setString(2, startTimeCombo.getSelectedItem().toString());
                    ps.setString(3, endTimeCombo.getSelectedItem().toString());
                    ps.setInt(4, session.getId());
                    ps.executeUpdate();
                }
            } else {
                // Dentist doesn’t exist → insert
                String sql = "INSERT INTO tbl_dentists(acc_id, specialty, work_start, work_end) VALUES(?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, session.getId());
                    ps.setString(2, set_specialization.getSelectedItem().toString());
                    ps.setString(3, startTimeCombo.getSelectedItem().toString());
                    ps.setString(4, endTimeCombo.getSelectedItem().toString());
                    ps.executeUpdate();
                }
            }
        }

        // ✅ Audit log
        String audit = "INSERT INTO tbl_logs(actor_id, actor_role, action, details) VALUES(?, ?, ?, ?)";
        try (PreparedStatement log = conn.prepareStatement(audit)) {
            log.setInt(1, session.getId());
            log.setString(2, session.getRole());
            log.setString(3, "Updated dentist specialty and schedule");
            log.setString(4, "Specialty: " + set_specialization.getSelectedItem().toString() +
                             ", Start: " + startTimeCombo.getSelectedItem().toString() +
                             ", End: " + endTimeCombo.getSelectedItem().toString());
            log.executeUpdate();
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Specialty update failed: " + e.getMessage());
        throw new RuntimeException(e); // force rollback
    }
}


private void loadProfileEdit() {
    try (Connection con = config.connectDB()) {
        // --- Load account info including photo ---
        String sqlAcc = "SELECT acc_name, acc_email, acc_contact, acc_pic FROM tbl_accounts WHERE acc_id = ?";
        PreparedStatement psAcc = con.prepareStatement(sqlAcc);
        psAcc.setInt(1, session.getId());
        ResultSet rsAcc = psAcc.executeQuery();

        if (rsAcc.next()) {
            // --- Populate Edit Profile text fields ---
            change_name.setText(rsAcc.getString("acc_name"));     
            change_email.setText(rsAcc.getString("acc_email"));
            change_contact.setText(rsAcc.getString("acc_contact"));

            // --- Photo handling (TEXT path) ---
            String imgPath = rsAcc.getString("acc_pic");
            ImageIcon icon;
            if (imgPath != null && !imgPath.trim().isEmpty() && new File(imgPath).exists()) {
                icon = new ImageIcon(imgPath);
            } else {
                icon = new ImageIcon(getClass().getResource("/img/default-user.png"));
            }
            forEDITPICTURE.setIcon(resizeImage(icon, 150, 120));
        }

            String sqlDentist = "SELECT specialty FROM tbl_dentists WHERE dentist_id=?";
            PreparedStatement psDent = con.prepareStatement(sqlDentist);
            psDent.setInt(1, session.getId());
            ResultSet rsDent = psDent.executeQuery();

        if (rsDent.next()) {
            String specialty = rsDent.getString("specialty");
            if (specialty != null && !specialty.trim().isEmpty()) {
                set_specialization.setSelectedItem(specialty);
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading profile edit: " + e.getMessage());
    }
}



    private void saveProfilePhoto(File file) {
    try (Connection conn = config.connectDB();
         PreparedStatement ps = conn.prepareStatement(
             // ✅ Use the correct column name
             "UPDATE tbl_accounts SET acc_pic=? WHERE acc_id=?")) {

        FileInputStream fis = new FileInputStream(file);
        ps.setBinaryStream(1, fis, (int) file.length());
        ps.setInt(2, session.getId());
        ps.executeUpdate();

        // ✅ Update UI immediately with resized photo
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        Image scaledImg = icon.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);

        forEDITPICTURE.setIcon(new ImageIcon(scaledImg));
        savePicHereFirst.setIcon(new ImageIcon(scaledImg));

        JOptionPane.showMessageDialog(null, "Photo updated successfully!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Failed to update photo.\n" + e.getMessage());
    }
}
     private void loadProfileDisplay() {
    try (Connection conn = config.connectDB();
         PreparedStatement ps = conn.prepareStatement(
             "SELECT acc_name, acc_email, acc_contact, acc_pic FROM tbl_accounts WHERE acc_id=?"
         )) {

        ps.setInt(1, session.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            // Load text info
            name.setText(rs.getString("acc_name"));
            email.setText(rs.getString("acc_email"));
            contact.setText(rs.getString("acc_contact"));

            // Load profile picture
            String imgPath = rs.getString("acc_pic");
            if (imgPath != null && !imgPath.trim().isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            ImageIcon resized = resizeImage(icon, 150, 120);
            forEDITPICTURE.setIcon(resized);
            savePicHereFirst.setIcon(resized);
            
            
            }
        }



    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Failed to load profile info.\n" + e.getMessage());
    }
}


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(dentst.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dentst.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dentst.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dentst.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            new dentst().setVisible(true); // display JFrame
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Dr_lastname;
    private javax.swing.JLabel SAVECHANGES;
    private javax.swing.JLabel XBTN;
    private javax.swing.JPanel XPNL;
    private javax.swing.JPanel addpicture;
    private javax.swing.JPanel addpicture1;
    private javax.swing.JPanel bg;
    private javax.swing.JLabel cancelChangesEditProfile;
    private javax.swing.JLabel cancelMYprofile;
    private javax.swing.JPanel cancelpane;
    private javax.swing.JLabel changePhoto;
    private javax.swing.JTextField change_contact;
    private javax.swing.JTextField change_email;
    private javax.swing.JTextField change_name;
    private javax.swing.JPanel change_photo;
    private javax.swing.JLabel cicrcle_profile;
    private javax.swing.JLabel completed;
    private javax.swing.JTextField confirmnewpass;
    private javax.swing.JPasswordField confirmpass_myprofile;
    private javax.swing.JLabel contact;
    private javax.swing.JTabbedPane dashTb;
    private javax.swing.JPanel dashbox;
    private javax.swing.JLabel dashbtn;
    private javax.swing.JPanel dashpnl;
    private javax.swing.JPanel db;
    private javax.swing.JLabel dentist;
    private javax.swing.JLabel displaySpecialty;
    private javax.swing.JLabel dr;
    private javax.swing.JLabel editprofile;
    private javax.swing.JLabel email;
    private javax.swing.JComboBox<String> endTimeCombo;
    private javax.swing.JLabel forEDITPICTURE;
    private javax.swing.JCheckBox friCheck;
    private javax.swing.JPanel hdr;
    private javax.swing.JLabel inProg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JPanel lg;
    private javax.swing.JLabel logout;
    private javax.swing.JCheckBox monCheck;
    private javax.swing.JPanel mp;
    private javax.swing.JPanel mp1;
    private javax.swing.JPanel mpa;
    private javax.swing.JLabel name;
    private javax.swing.JTextField newpass;
    private javax.swing.JPasswordField newpass_myprofile;
    private javax.swing.JLabel p;
    private javax.swing.JPanel patient;
    private javax.swing.JPanel patient1;
    private javax.swing.JLabel role_myprofile;
    private javax.swing.JLabel s;
    private javax.swing.JCheckBox satCheck;
    private javax.swing.JLabel save;
    private javax.swing.JLabel savePicHereFirst;
    private javax.swing.JLabel saveSchedule;
    private javax.swing.JPanel sched;
    private javax.swing.JPanel schedpnl;
    private javax.swing.JTable scheduleTable;
    private javax.swing.JLabel se;
    private javax.swing.JComboBox<String> set_specialization;
    private javax.swing.JPanel sett;
    private javax.swing.JComboBox<String> startTimeCombo;
    private javax.swing.JCheckBox sunCheck;
    private javax.swing.JLabel t;
    private javax.swing.JTable tbl;
    private javax.swing.JCheckBox thurCheck;
    private javax.swing.JLabel tp;
    private javax.swing.JPanel treatment;
    private javax.swing.JPanel treatp;
    private javax.swing.JCheckBox tueCheck;
    private javax.swing.JLabel upcoming;
    private javax.swing.JCheckBox wedCheck;
    // End of variables declaration//GEN-END:variables
}
