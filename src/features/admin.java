
package features;

import config.config;
import java.awt.Color;
import internal.session;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
/**
 *
 * @author Cassandra Gallera
 */
public class admin extends javax.swing.JFrame {
 int xMouse, yMouse;
    /**
     * Creates new form log
     */
    public admin() {
        initComponents();
       acctable();
       pic.setPreferredSize(new Dimension(150, 150));
       loadprofile();
       loadSystemLogs();
       initSearchPlaceholder();
 
       
       
       
       
searchBTN.setText("Search"); // emoji on button


     // Create a panel to act as the search bar
JPanel searchBar = new JPanel(new BorderLayout());
searchBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

// 🔎 Emoji label on the left (real emoji)
JLabel emojiLabel = new JLabel("🔎");
emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
emojiLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
// Set engaging placeholder text
search.setText("🔎 Search accounts by name, ID, or email...");
search.setForeground(Color.GRAY);

// Focus listeners handle placeholder behavior
search.addFocusListener(new java.awt.event.FocusAdapter() {
    @Override
    public void focusGained(java.awt.event.FocusEvent e) {
        if (search.getText().equals("🔎 Search accounts by name, ID, or email...")) {
            search.setText("");              // remove placeholder
            search.setForeground(Color.BLACK); // switch to black text
        }
    }

    @Override
    public void focusLost(java.awt.event.FocusEvent e) {
        if (search.getText().trim().isEmpty()) {
            search.setText("🔎 Search accounts by name, ID, or email...");
            search.setForeground(Color.GRAY); // revert to gray placeholder
        }
    }
});


// Action when pressing Enter
search.addActionListener(evt -> triggerSearch());

// Action when clicking the button
searchBTN.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        triggerSearch();
    }
});
    }
// Centralized search trigger
private void triggerSearch() {
    String query = search.getText().trim();

    if (query.isEmpty() || query.equals("🔎 Find users by name...")) {
        JOptionPane.showMessageDialog(this,
            "Please type a name, ID, or email before searching.",
            "Validation",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    performSearch(query);

}
  //        lblUser.setText("Welcome, " + name);

public class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null) {
            String status = value.toString();
            switch (status) {
                case "Active":
                    c.setForeground(new Color(0, 128, 0)); // Green
                    break;
                case "Inactive":
                    c.setForeground(Color.GRAY); // Gray
                    break;
                case "Suspended":
                    c.setForeground(Color.RED); // Red
                    break;
                default:
                    c.setForeground(Color.BLACK); // Fallback
            }
        }
        return c;
    }


    }
    private void initSearchPlaceholder() {
    seach_logs.setText("🔍 Search logs...");
    seach_logs.setForeground(Color.GRAY);

    seach_logs.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (seach_logs.getText().equals("🔍 Search logs...")) {
                seach_logs.setText("");
                seach_logs.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (seach_logs.getText().isEmpty()) {
                seach_logs.setText("🔍 Search logs...");
                seach_logs.setForeground(Color.GRAY);
            }
        }
    });
}

    
    
    
    
private void performSearch(String search1) {
    String sql;
    String baseQuery =
        "SELECT acc_id AS id, " +
        "acc_name AS name, " +
        "acc_email AS email, " +
        "acc_contact AS contact, " +
        "acc_role AS role, " +
        "CASE acc_status " +
        "     WHEN 1 THEN 'Active' " +
        "     WHEN 0 THEN 'Inactive' " +
        "     WHEN 2 THEN 'Suspended' " +
        "END AS status " +
        "FROM tbl_accounts";

    if (search1.isEmpty() || search1.equals("🔎 Find users by name...")) {
        sql = baseQuery;
    } else {
        sql = baseQuery + " WHERE acc_name LIKE ? OR acc_id LIKE ? OR acc_email LIKE ?";
    }

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        if (!search1.isEmpty() && !search1.equals("🔎 Find users by name...")) {
            String keyword = "%" + search1 + "%";
            pst.setString(1, keyword);
            pst.setString(2, keyword);
            pst.setString(3, keyword);
        }

        try (ResultSet rs = pst.executeQuery()) {
            tbl.setModel(DbUtils.resultSetToTableModel(rs));
            tbl.setDefaultEditor(Object.class, null);

            // Apply renderer to the status column
            tbl.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        }

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Search error: " + e.getMessage());
    }
}


// Insert a log record into system_logs
private void logSystemAction(int actorId, String actorRole, String action, String details) {
    String sql = "INSERT INTO system_logs (actor_id, actor_role, action, details, created_at) " +
                 "VALUES (?, ?, ?, ?, datetime('now'))";
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, actorId);
        pst.setString(2, actorRole);
        pst.setString(3, action);
        pst.setString(4, details);
        pst.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
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
        userpane = new javax.swing.JPanel();
        usersbtn = new javax.swing.JLabel();
        staffpane = new javax.swing.JPanel();
        staffbtn = new javax.swing.JLabel();
        analyticpane = new javax.swing.JPanel();
        analyticsbtn = new javax.swing.JLabel();
        systempane = new javax.swing.JPanel();
        system_logsbtn = new javax.swing.JLabel();
        pp = new javax.swing.JPanel();
        p = new javax.swing.JLabel();
        apppane = new javax.swing.JPanel();
        appbtn = new javax.swing.JLabel();
        billpane = new javax.swing.JPanel();
        billbtn = new javax.swing.JLabel();
        logoutpane = new javax.swing.JPanel();
        logoutbtn = new javax.swing.JLabel();
        hdr = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        XPNL = new javax.swing.JPanel();
        XBTN = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        admin_name = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tabbed = new javax.swing.JTabbedPane();
        db = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        users = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        add = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        edit = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        delete = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        searchBTN = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        staffmanage = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        stafftbl = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        editstaff = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        addstaff = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        deletestaff = new javax.swing.JLabel();
        srch = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        analytics = new javax.swing.JPanel();
        systemlogs = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        system_logs = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        seach_logs = new javax.swing.JTextField();
        jPanel24 = new javax.swing.JPanel();
        search_systemlogs = new javax.swing.JLabel();
        myprofile = new javax.swing.JPanel();
        mp = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        addpicture = new javax.swing.JPanel();
        addpic_here = new javax.swing.JLabel();
        editprofile = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        contact = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        role = new javax.swing.JLabel();
        name = new javax.swing.JLabel();
        email = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        mp1 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        addpicture1 = new javax.swing.JPanel();
        pic = new javax.swing.JLabel();
        editprofile1 = new javax.swing.JLabel();
        id1 = new javax.swing.JLabel();
        changephoto = new javax.swing.JLabel();
        save = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        ID = new javax.swing.JLabel();
        ROLE = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();

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
        dashbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        dashbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
        dashpnl.add(dashbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 30));

        dashbox.add(dashpnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, 30));

        userpane.setBackground(new java.awt.Color(255, 255, 255));
        userpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        usersbtn.setBackground(new java.awt.Color(255, 255, 255));
        usersbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        usersbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usersbtn.setText("Users");
        usersbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                usersbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                usersbtnMouseExited(evt);
            }
        });
        userpane.add(usersbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 30));

        dashbox.add(userpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 30));

        staffpane.setBackground(new java.awt.Color(255, 255, 255));

        staffbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        staffbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        staffbtn.setText("Workforce Management");
        staffbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                staffbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                staffbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                staffbtnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout staffpaneLayout = new javax.swing.GroupLayout(staffpane);
        staffpane.setLayout(staffpaneLayout);
        staffpaneLayout.setHorizontalGroup(
            staffpaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(staffbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );
        staffpaneLayout.setVerticalGroup(
            staffpaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, staffpaneLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(staffbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        dashbox.add(staffpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 170, 30));

        analyticpane.setBackground(new java.awt.Color(255, 255, 255));

        analyticsbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        analyticsbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        analyticsbtn.setText("Analytics");
        analyticsbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                analyticsbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                analyticsbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                analyticsbtnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout analyticpaneLayout = new javax.swing.GroupLayout(analyticpane);
        analyticpane.setLayout(analyticpaneLayout);
        analyticpaneLayout.setHorizontalGroup(
            analyticpaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(analyticsbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );
        analyticpaneLayout.setVerticalGroup(
            analyticpaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(analyticsbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        dashbox.add(analyticpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 170, 30));

        systempane.setBackground(new java.awt.Color(255, 255, 255));

        system_logsbtn.setBackground(new java.awt.Color(255, 255, 255));
        system_logsbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        system_logsbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        system_logsbtn.setText("System Logs");
        system_logsbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                system_logsbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                system_logsbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                system_logsbtnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout systempaneLayout = new javax.swing.GroupLayout(systempane);
        systempane.setLayout(systempaneLayout);
        systempaneLayout.setHorizontalGroup(
            systempaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(system_logsbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );
        systempaneLayout.setVerticalGroup(
            systempaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(system_logsbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        dashbox.add(systempane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 270, 170, 30));

        pp.setBackground(new java.awt.Color(255, 255, 255));
        pp.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        p.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        p.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        p.setText("Settings");
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
        pp.add(p, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 2, 170, 30));

        dashbox.add(pp, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 170, 30));

        apppane.setBackground(new java.awt.Color(255, 255, 255));
        apppane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        appbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        appbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        appbtn.setText("Appointments");
        appbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                appbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                appbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                appbtnMouseExited(evt);
            }
        });
        apppane.add(appbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 30));

        dashbox.add(apppane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 30));

        billpane.setBackground(new java.awt.Color(255, 255, 255));
        billpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        billbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        billbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        billbtn.setText("Billing");
        billbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                billbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                billbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                billbtnMouseExited(evt);
            }
        });
        billpane.add(billbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 30));

        dashbox.add(billpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 170, 30));

        logoutpane.setBackground(new java.awt.Color(255, 255, 255));
        logoutpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logoutbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        logoutbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoutbtn.setText("Logout");
        logoutbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutbtnMouseExited(evt);
            }
        });
        logoutpane.add(logoutbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 30));

        dashbox.add(logoutpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 170, 30));

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

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/output-onlinepngtools__3_-removebg-preview.png"))); // NOI18N
        logo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoMouseClicked(evt);
            }
        });
        hdr.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        XBTN.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
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
            .addComponent(XBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );
        XPNLLayout.setVerticalGroup(
            XPNLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(XBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        hdr.add(XPNL, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 10, 40, 30));

        jLabel2.setBackground(new java.awt.Color(51, 102, 255));
        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Admin Portal");
        hdr.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 130, 30));

        admin_name.setFont(new java.awt.Font("Trebuchet MS", 0, 15)); // NOI18N
        hdr.add(admin_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 190, 20));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/5046faad4a4c9af72bcf4fe75c8a11d0.jpg"))); // NOI18N
        hdr.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 50));

        bg.add(hdr, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 50));

        db.setBackground(new java.awt.Color(255, 255, 255));
        db.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 21)); // NOI18N
        jLabel3.setText("System Overview");
        db.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 30));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("________________________________________________________________________________________");
        db.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 27, -1, 30));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setBackground(new java.awt.Color(0, 102, 255));
        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 255));
        jLabel6.setText("Today's Appointment");
        jPanel8.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, 20));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-appointment-25.png"))); // NOI18N
        jPanel8.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 60, 50));

        db.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 200, 60));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 255));
        jLabel7.setText("Patient's In Waiting");
        jPanel9.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, -1, 40));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-waiting-room-25.png"))); // NOI18N
        jPanel9.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 60, 50));

        db.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 60, 200, 60));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-dentist-25.png"))); // NOI18N
        jPanel10.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 50));

        jLabel8.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 255));
        jLabel8.setText("Dentist Available");
        jPanel10.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, -1, 40));

        db.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 60, 200, 60));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(jTable1);

        db.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 300, 180));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane5.setViewportView(jTable2);

        db.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 140, 310, 180));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(jTable3);

        db.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 340, 300, 120));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(jTable4);

        db.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 341, 310, 120));

        tabbed.addTab("dashboard", db);

        users.setBackground(new java.awt.Color(255, 255, 255));
        users.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbl);

        users.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 610, 310));

        jPanel1.setBackground(new java.awt.Color(0, 51, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        add.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        add.setForeground(new java.awt.Color(255, 255, 255));
        add.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-plus-sign-20.png"))); // NOI18N
        add.setText("Register User");
        add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addMouseClicked(evt);
            }
        });
        jPanel1.add(add, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 40));

        users.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 140, 40));

        jPanel2.setBackground(new java.awt.Color(0, 51, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        edit.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        edit.setForeground(new java.awt.Color(255, 255, 255));
        edit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        edit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-20.png"))); // NOI18N
        edit.setText("Edit User");
        jPanel2.add(edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 40));

        users.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 140, 40));

        jPanel3.setBackground(new java.awt.Color(0, 51, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        delete.setBackground(new java.awt.Color(255, 255, 255));
        delete.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        delete.setForeground(new java.awt.Color(255, 255, 255));
        delete.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-delete-user-20.png"))); // NOI18N
        delete.setText("Delete User");
        jPanel3.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -1, 140, 40));

        users.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 140, 40));

        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchKeyTyped(evt);
            }
        });
        users.add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 520, 30));

        jPanel4.setBackground(new java.awt.Color(0, 51, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        searchBTN.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        searchBTN.setForeground(new java.awt.Color(255, 255, 255));
        searchBTN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchBTN.setText("Search");
        searchBTN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchBTNMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchBTNMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchBTNMouseExited(evt);
            }
        });
        jPanel4.add(searchBTN, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 30));

        users.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 110, 80, 30));

        jLabel15.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel15.setText("Users Management");
        users.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, 40));

        jLabel47.setForeground(new java.awt.Color(153, 153, 153));
        jLabel47.setText("_______________________________________________________________________________________");
        users.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 26, -1, 30));

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-administrator-male-40.png"))); // NOI18N
        users.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 50, 50));

        tabbed.addTab("users", users);

        staffmanage.setBackground(new java.awt.Color(255, 255, 255));
        staffmanage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 0));
        staffmanage.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        stafftbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stafftbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        stafftbl.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(stafftbl);

        staffmanage.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 630, 310));

        jPanel5.setBackground(new java.awt.Color(51, 153, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        editstaff.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        editstaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editstaff.setText("Edit");
        jPanel5.add(editstaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 40));

        staffmanage.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 110, 40));

        jLabel16.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel16.setText("Staff Management");
        staffmanage.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        jLabel17.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(102, 102, 102));
        jLabel17.setText("Manage all staf members and their roles");
        staffmanage.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, 20));

        jPanel6.setBackground(new java.awt.Color(51, 153, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addstaff.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        addstaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addstaff.setText("Add");
        addstaff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addstaffMouseClicked(evt);
            }
        });
        jPanel6.add(addstaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 130, 40));

        staffmanage.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 130, 40));

        jPanel11.setBackground(new java.awt.Color(51, 153, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        deletestaff.setBackground(new java.awt.Color(51, 153, 255));
        deletestaff.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        deletestaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deletestaff.setText("Delete");
        jPanel11.add(deletestaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 100, 40));

        staffmanage.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, 120, 40));

        srch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        staffmanage.add(srch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 540, 30));

        jPanel15.setBackground(new java.awt.Color(51, 153, 255));
        jPanel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 2));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Search");
        jPanel15.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 30));

        staffmanage.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 120, 70, 30));

        jLabel48.setForeground(new java.awt.Color(153, 153, 153));
        jLabel48.setText("__________________________________________________________________________________________");
        staffmanage.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 30));

        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-staff-40.png"))); // NOI18N
        staffmanage.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 50, 60));

        tabbed.addTab("staff_M", staffmanage);

        javax.swing.GroupLayout analyticsLayout = new javax.swing.GroupLayout(analytics);
        analytics.setLayout(analyticsLayout);
        analyticsLayout.setHorizontalGroup(
            analyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
        analyticsLayout.setVerticalGroup(
            analyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
        );

        tabbed.addTab("analytics", analytics);

        systemlogs.setBackground(new java.awt.Color(255, 255, 255));
        systemlogs.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        system_logs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(system_logs);

        systemlogs.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 630, 300));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel5.setText("System Activity Logs");
        systemlogs.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jLabel14.setBackground(new java.awt.Color(102, 102, 102));
        jLabel14.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("Monitor all system activities and user actions");
        systemlogs.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        seach_logs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                seach_logsKeyTyped(evt);
            }
        });
        systemlogs.add(seach_logs, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 112, 540, 30));

        jPanel24.setBackground(new java.awt.Color(0, 51, 255));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        search_systemlogs.setBackground(new java.awt.Color(255, 255, 255));
        search_systemlogs.setForeground(new java.awt.Color(255, 255, 255));
        search_systemlogs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        search_systemlogs.setText("Search");
        search_systemlogs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                search_systemlogsMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                search_systemlogsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                search_systemlogsMouseExited(evt);
            }
        });
        jPanel24.add(search_systemlogs, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 60, 30));

        systemlogs.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 110, 80, 30));

        tabbed.addTab("logs", systemlogs);

        myprofile.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mp.setBackground(new java.awt.Color(255, 255, 255));
        mp.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel18.setBackground(new java.awt.Color(204, 255, 255));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addpicture.setBackground(new java.awt.Color(255, 255, 255));
        addpicture.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addpic_here.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addpic_here.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png"))); // NOI18N
        addpic_here.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addpic_hereMouseClicked(evt);
            }
        });
        addpicture.add(addpic_here, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 160));

        jPanel18.add(addpicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 180, 160));

        editprofile.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        editprofile.setForeground(new java.awt.Color(51, 0, 255));
        editprofile.setText("Edit Profile");
        editprofile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editprofileMouseClicked(evt);
            }
        });
        jPanel18.add(editprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 390, -1, 40));

        jLabel25.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(51, 0, 204));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("My Profile");
        jPanel18.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 180, 50));

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel27.setText("ID :");
        jPanel17.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        id.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel17.add(id, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 50, 40));

        jLabel26.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel26.setText("Name :");
        jPanel17.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 60, -1));

        jLabel28.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel28.setText("Email :");
        jPanel17.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, 30));

        jLabel29.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel29.setText("Contact :");
        jPanel17.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        contact.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel17.add(contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 210, 30));

        jLabel30.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel30.setText("Role :");
        jPanel17.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 182, 60, 30));

        role.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel17.add(role, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, 170, 40));

        name.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel17.add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 210, 30));

        email.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel17.add(email, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 210, 30));

        jPanel18.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 90, 320, 270));

        jLabel18.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 0, 255));
        jLabel18.setText("Back to dashboard");
        jPanel18.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 396, -1, 30));

        mp.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 570, 430));

        myprofile.add(mp, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, -1, 650, 470));

        tabbed.addTab("profile", myprofile);

        mp1.setBackground(new java.awt.Color(255, 255, 255));
        mp1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel19.setBackground(new java.awt.Color(204, 255, 255));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addpicture1.setBackground(new java.awt.Color(255, 255, 255));
        addpicture1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png"))); // NOI18N
        addpicture1.add(pic, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 160));

        jPanel19.add(addpicture1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 180, 160));

        editprofile1.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        editprofile1.setForeground(new java.awt.Color(51, 0, 255));
        editprofile1.setText("Back to dahsboard");
        editprofile1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editprofile1MouseClicked(evt);
            }
        });
        jPanel19.add(editprofile1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 150, 40));

        id1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jPanel19.add(id1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 50, 30));

        changephoto.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        changephoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        changephoto.setText("Add photo");
        changephoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                changephotoMouseClicked(evt);
            }
        });
        jPanel19.add(changephoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 300, 180, 30));

        save.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        save.setForeground(new java.awt.Color(51, 0, 255));
        save.setText("Save Changes");
        save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveMouseClicked(evt);
            }
        });
        jPanel19.add(save, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 390, 90, 40));

        jLabel31.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(51, 0, 204));
        jLabel31.setText("Edit Profile");
        jPanel19.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, 120, 70));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel33.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel33.setText("ID");
        jPanel16.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        ID.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jPanel16.add(ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 40, 30));

        ROLE.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jPanel16.add(ROLE, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 190, 130, 30));

        jLabel36.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel36.setText("Role");
        jPanel16.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 40, 40));

        jLabel32.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel32.setText("Change Name");
        jPanel16.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        jTextField5.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jPanel16.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 190, 30));

        jTextField3.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jPanel16.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 110, 190, 30));

        jLabel34.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel34.setText("Change Email");
        jPanel16.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, 30));

        jLabel35.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel35.setText("Change Contact");
        jPanel16.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, 30));

        jTextField4.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jPanel16.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 150, 190, 30));

        jPanel19.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 90, 320, 260));

        mp1.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 570, 430));

        tabbed.addTab("editprofile", mp1);

        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel22.setBackground(new java.awt.Color(153, 255, 255));
        jPanel22.setForeground(new java.awt.Color(255, 255, 255));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel19.setText("Edit User");
        jPanel22.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 140, 50));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png"))); // NOI18N
        jPanel22.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 80, 50));

        jPanel21.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 280, 70));

        jPanel23.setBackground(new java.awt.Color(204, 255, 255));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel38.setText("ID");
        jPanel23.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, 30));

        jLabel40.setText("jLabel40");
        jPanel23.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, -1, -1));

        jLabel39.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel39.setText("Name");
        jPanel23.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, 30));

        jLabel43.setText("jLabel43");
        jPanel23.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, -1, -1));

        jLabel42.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel42.setText("Email");
        jPanel23.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, -1));

        jLabel41.setText("jLabel41");
        jPanel23.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, -1, -1));

        jLabel44.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel44.setText("Contact");
        jPanel23.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, -1, -1));

        jLabel45.setText("jLabel45");
        jPanel23.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 200, -1, -1));

        jLabel46.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel46.setText("Role");
        jPanel23.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, -1, -1));

        jPanel21.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 430, 290));

        jPanel20.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 650, 460));

        tabbed.addTab("edit_user", jPanel20);

        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(51, 102, 255));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel7.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("wm", jPanel7);

        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(0, 153, 204));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel12.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("billing", jPanel12);

        bg.add(tabbed, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 650, 500));

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
        tabbed.setSelectedIndex(0);

    }//GEN-LAST:event_dashbtnMouseClicked

    private void dashbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbtnMouseExited
         dashpnl.setBackground(Color. white);
         dashbtn.setForeground(Color.black);
        
    }//GEN-LAST:event_dashbtnMouseExited

    private void usersbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersbtnMouseClicked
        tabbed.setSelectedIndex(1);
    }//GEN-LAST:event_usersbtnMouseClicked

    private void staffbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staffbtnMouseClicked
        tabbed.setSelectedIndex(2);
    }//GEN-LAST:event_staffbtnMouseClicked

    private void analyticsbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_analyticsbtnMouseClicked
        tabbed.setSelectedIndex(3);
    }//GEN-LAST:event_analyticsbtnMouseClicked

    private void system_logsbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_system_logsbtnMouseClicked
       tabbed.setSelectedIndex(4);
       loadSystemLogs();
    }//GEN-LAST:event_system_logsbtnMouseClicked

    private void usersbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersbtnMouseEntered
        userpane.setBackground(Color. blue);
        usersbtn.setForeground(Color.white);
    }//GEN-LAST:event_usersbtnMouseEntered

    private void usersbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersbtnMouseExited
        userpane.setBackground(Color. white);
        usersbtn.setForeground(Color.black);
    }//GEN-LAST:event_usersbtnMouseExited

    private void staffbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staffbtnMouseEntered
        staffpane.setBackground(Color. blue);
        staffbtn.setForeground(Color.white);
    }//GEN-LAST:event_staffbtnMouseEntered

    private void staffbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staffbtnMouseExited
        staffpane.setBackground(Color. white);
        staffbtn.setForeground(Color.black);
    }//GEN-LAST:event_staffbtnMouseExited

    private void analyticsbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_analyticsbtnMouseEntered
        analyticpane.setBackground(Color. blue);
        analyticsbtn.setForeground(Color.white);
    }//GEN-LAST:event_analyticsbtnMouseEntered

    private void analyticsbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_analyticsbtnMouseExited
        analyticpane.setBackground(Color. white);
        analyticsbtn.setForeground(Color.black);
    }//GEN-LAST:event_analyticsbtnMouseExited

    private void system_logsbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_system_logsbtnMouseEntered
        systempane.setBackground(Color. blue);
        system_logsbtn.setForeground(Color.white);
    }//GEN-LAST:event_system_logsbtnMouseEntered

    private void system_logsbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_system_logsbtnMouseExited
        systempane.setBackground(Color. white);
        system_logsbtn.setForeground(Color.black);
    }//GEN-LAST:event_system_logsbtnMouseExited

    private void logoutbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutbtnMouseClicked
               
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Logout",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {

        // ✅ Log the logout event first
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
    
    }//GEN-LAST:event_logoutbtnMouseClicked

    private void logoutbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutbtnMouseEntered
        logoutpane.setBackground(Color. blue);
        logoutbtn.setForeground(Color.white);
    }//GEN-LAST:event_logoutbtnMouseEntered

    private void logoutbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutbtnMouseExited
          logoutpane.setBackground(Color. white);
          logoutbtn.setForeground(Color.black);
    }//GEN-LAST:event_logoutbtnMouseExited

    private void logoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoMouseClicked
        // TODO add your handling code here:
        landingp lndng = new landingp();
        this.dispose();
        lndng.setVisible(true);
    }//GEN-LAST:event_logoMouseClicked

    private void pMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pMouseClicked
        tabbed.setSelectedIndex(5);
    }//GEN-LAST:event_pMouseClicked

    private void pMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pMouseEntered
         pp.setBackground(Color. blue);
         p.setForeground(Color.white);
    }//GEN-LAST:event_pMouseEntered

    private void pMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pMouseExited
          pp.setBackground(Color. white);
          p.setForeground(Color.black);
    }//GEN-LAST:event_pMouseExited

    private void editprofileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editprofileMouseClicked
       tabbed.setSelectedIndex(6);
    }//GEN-LAST:event_editprofileMouseClicked

    private void changephotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changephotoMouseClicked

    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(
        new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "png", "jpeg", "gif"
        )
    );

    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {

        String path = chooser.getSelectedFile().getAbsolutePath();

        // ✅ 1. SHOW IMAGE IMMEDIATELY
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(
                addpic_here.getWidth(),
                addpic_here.getHeight(),
                Image.SCALE_SMOOTH
        );

        addpic_here.setIcon(new ImageIcon(img));

        // ✅ 2. SAVE TO DATABASE
        try (Connection con = config.connectDB();
             PreparedStatement pst = con.prepareStatement(
                     "UPDATE tbl_accounts SET acc_pic = ? WHERE acc_id = ?")) {

            pst.setString(1, path);
            pst.setInt(2, session.getId());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Profile picture updated!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    }//GEN-LAST:event_changephotoMouseClicked

    private void saveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveMouseClicked
        String newName = name.getText().trim();
      
        String newEmail = email.getText().trim();
        String newContact = contact.getText().trim();
       
        //String newPass = new String(password.getPassword()).trim();
       // String confirmPass = new String(cpass.getPassword()).trim();

        int fieldCount = 0;

        if (!newName.isEmpty()) {
            fieldCount++;
        }
      
        if (!newEmail.isEmpty()) {
            fieldCount++;
        }
        if (!newContact.isEmpty()) {
            fieldCount++;
        }
       
//        if (!newPass.isEmpty()) {
//            fieldCount++;
//        }

        if (fieldCount == 0) {
            JOptionPane.showMessageDialog(this, "No fields to update!");
            return;
        }

//        if (!newPass.isEmpty() && !newPass.equals(confirmPass)) {
//            JOptionPane.showMessageDialog(this, "Password and Confirm Password do not match");
//            return;
//        }

        StringBuilder sql = new StringBuilder("UPDATE tbl_accounts SET ");

        if (!newName.isEmpty()) {
            sql.append("acc_name=?, ");
        }
       
        if (!newEmail.isEmpty()) {
            sql.append("acc_email=?, ");
        }
        if (!newContact.isEmpty()) {
            sql.append("acc_contact=?, ");
        }
        
//        if (!newPass.isEmpty()) {
//            sql.append("acc_pass=?, ");
//        }

        sql.setLength(sql.length() - 2); // Remove last comma and space
        sql.append(" WHERE acc_id=?");

        Object[] params = new Object[fieldCount + 1];
        int index = 0;

        if (!newName.isEmpty()) {
            params[index++] = newName;
        }
      
        if (!newEmail.isEmpty()) {
            params[index++] = newEmail;
        }
        if (!newContact.isEmpty()) {
            params[index++] = newContact;
        }
      
//        if (!newPass.isEmpty()) {
//            params[index++] = config.hashPassword(newPass);
//        }

        params[index] = session.getId();

// ===== UPDATE SAFELY =====
        try (Connection con = config.connectDB();
                PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.length; i++) {
                pst.setObject(i + 1, params[i]);
            }

            int updated = pst.executeUpdate();

            if (updated > 0) {
                con.close();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                loadprofile(); // Refresh labels
            } else {
                JOptionPane.showMessageDialog(this, "No changes were made.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
        }
    }//GEN-LAST:event_saveMouseClicked

    private void editprofile1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editprofile1MouseClicked
     tabbed.setSelectedIndex(0);
    }//GEN-LAST:event_editprofile1MouseClicked

    private void addstaffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addstaffMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_addstaffMouseClicked

    private void addpic_hereMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addpic_hereMouseClicked
         chooseAndSavePhoto();  // now picture logic is only triggered here
    }//GEN-LAST:event_addpic_hereMouseClicked

    private void addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseClicked
        
    }//GEN-LAST:event_addMouseClicked

    private void searchBTNMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBTNMouseClicked
searchBTN.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        String search1 = search.getText().trim();

        // ✅ Single professional validation
        if (search1.isEmpty() || search1.length() < 2 || 
            search1.equals("Search by name...") || 
            search1.equals("🔎 Search accounts by name, ID, or email...")) {
            
            JOptionPane.showMessageDialog(null,
                "Please enter at least 2 characters to search.",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // If validation passes → perform search
        performSearch(search1);
    }
});

    }//GEN-LAST:event_searchBTNMouseClicked

    private void searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyTyped
     String search1 = search.getText().trim();
    String sql;

    String baseQuery =
        "SELECT acc_id AS id, " +
        "acc_name AS name, " +
        "acc_email AS email, " +
        "acc_contact AS contact, " +
        "acc_role AS role, " +
        "acc_status AS status " +
        "FROM tbl_accounts";

    if (search1.isEmpty() || search1.equals("🔎 Search accounts by name, ID, or email...")) {
        sql = baseQuery;
    } else {
        sql = baseQuery + " WHERE acc_name LIKE ? OR acc_id LIKE ? OR acc_email LIKE ?";
    }

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        if (!search1.isEmpty() && !search1.equals("🔎 Search accounts by name, ID, or email...")) {
            String keyword = "%" + search1 + "%";
            pst.setString(1, keyword);
            pst.setString(2, keyword);
            pst.setString(3, keyword);
        }

        try (ResultSet rs = pst.executeQuery()) {
            tbl.setModel(DbUtils.resultSetToTableModel(rs));
            tbl.setDefaultEditor(Object.class, null);
        }

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Search error: " + e.getMessage());
    }
    }//GEN-LAST:event_searchKeyTyped

    private void searchBTNMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBTNMouseEntered
    // Highlight on hover with dentalcare accent
    searchBTN.setForeground(new Color(102, 204, 255)); // light aqua/sky blue
    searchBTN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); 
    }//GEN-LAST:event_searchBTNMouseEntered

    private void searchBTNMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBTNMouseExited
// Back to original color
    searchBTN.setForeground(Color.WHITE);  
    searchBTN.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_searchBTNMouseExited

    private void seach_logsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_seach_logsKeyTyped
     String keyword = seach_logs.getText().trim();

    // ✅ Validation: allow only letters, numbers, spaces
    if (!keyword.matches("[a-zA-Z0-9\\s]*")) {
        JOptionPane.showMessageDialog(this, "Invalid characters in search.");
        seach_logs.setText(keyword.replaceAll("[^a-zA-Z0-9\\s]", ""));
        return;
    }

    // ✅ Handle placeholder or empty
    if (keyword.isEmpty() || keyword.equals("🔍 Search logs...")) {
        loadSystemLogs(); // show all logs
        return;
    }

    String sql = "SELECT id AS 'Log ID', actor_id AS 'User ID', actor_role AS 'Role', " +
                 "action AS 'Activity', details AS 'Description', created_at AS 'Logged At' " +
                 "FROM tbl_logs WHERE actor_id LIKE ? OR actor_role LIKE ? OR action LIKE ? OR details LIKE ? " +
                 "ORDER BY created_at DESC";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        String searchTerm = "%" + keyword + "%";
        pst.setString(1, searchTerm);
        pst.setString(2, searchTerm);
        pst.setString(3, searchTerm);
        pst.setString(4, searchTerm);

        try (ResultSet rs = pst.executeQuery()) {
            system_logs.setModel(DbUtils.resultSetToTableModel(rs));
            system_logs.setDefaultEditor(Object.class, null);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }//GEN-LAST:event_seach_logsKeyTyped

    private void search_systemlogsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_systemlogsMouseClicked
       String keyword = seach_logs.getText().trim();

    // ✅ Validation: block placeholder or empty input
    if (keyword.isEmpty() || keyword.equals("🔍 Search logs...")) {
        loadSystemLogs(); // reload all logs if nothing typed
        return;
    }

    // ✅ Validation: allow only letters, numbers, spaces
    if (!keyword.matches("[a-zA-Z0-9\\s]*")) {
        JOptionPane.showMessageDialog(this, "Invalid characters in search.");
        seach_logs.setText(keyword.replaceAll("[^a-zA-Z0-9\\s]", ""));
        return;
    }

    // ✅ SQL query with aliases for clean JTable headers
    String sql = "SELECT id AS 'Log ID', actor_id AS 'User ID', actor_role AS 'Role', " +
                 "action AS 'Activity', details AS 'Description', created_at AS 'Logged At' " +
                 "FROM tbl_logs WHERE actor_id LIKE ? OR actor_role LIKE ? OR action LIKE ? OR details LIKE ? " +
                 "ORDER BY created_at DESC";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        String searchTerm = "%" + keyword + "%";
        pst.setString(1, searchTerm);
        pst.setString(2, searchTerm);
        pst.setString(3, searchTerm);
        pst.setString(4, searchTerm);

        try (ResultSet rs = pst.executeQuery()) {
            system_logs.setModel(DbUtils.resultSetToTableModel(rs));
            system_logs.setDefaultEditor(Object.class, null);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error searching logs: " + e.getMessage());
    }
    }//GEN-LAST:event_search_systemlogsMouseClicked

    private void search_systemlogsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_systemlogsMouseEntered
         // Highlight on hover with dentalcare accent
    search_systemlogs.setForeground(new Color(102, 204, 255)); // light aqua/sky blue
    search_systemlogs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); 
    }//GEN-LAST:event_search_systemlogsMouseEntered

    private void search_systemlogsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_systemlogsMouseExited
      // Back to original color
    search_systemlogs.setForeground(Color.WHITE);  
    search_systemlogs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_search_systemlogsMouseExited

    private void appbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseEntered
               // TODO add your handling code here:
        apppane.setBackground(Color. blue);
        appbtn.setForeground(Color.white);
    }//GEN-LAST:event_appbtnMouseEntered

    private void appbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseExited
         apppane.setBackground(Color. white);
         appbtn.setForeground(Color.black);
        
    }//GEN-LAST:event_appbtnMouseExited

    private void billbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billbtnMouseExited
         billpane.setBackground(Color. white);
         billbtn.setForeground(Color.black);
        
    }//GEN-LAST:event_billbtnMouseExited

    private void billbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billbtnMouseEntered
        billpane.setBackground(Color. blue);
        billbtn.setForeground(Color.white);
    }//GEN-LAST:event_billbtnMouseEntered

    private void billbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billbtnMouseClicked
       tabbed.setSelectedIndex(9);
    }//GEN-LAST:event_billbtnMouseClicked

    private void appbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseClicked
      tabbed.setSelectedIndex(8);
    }//GEN-LAST:event_appbtnMouseClicked
private void acctable() {
    String sql =
        "SELECT acc_id AS id, " +
        "acc_name AS name, " +
        "acc_email AS email, " +
        "acc_contact AS contact, " +
        "acc_role AS role, " +
        "CASE acc_status " +
        "     WHEN 1 THEN 'Active' " +
        "     WHEN 0 THEN 'Inactive' " +
        "     WHEN 2 THEN 'Suspended' " +
        "END AS status " +
        "FROM tbl_accounts";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        tbl.setModel(DbUtils.resultSetToTableModel(rs));
        tbl.setDefaultEditor(Object.class, null);

        // Apply renderer to the status column (adjust index if needed)
        tbl.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error loading accounts: " + e.getMessage());
    }
}

    
   private void chooseAndSavePhoto() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "jpeg", "gif"));
    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
        try {
            File selectedFile = chooser.getSelectedFile();
            File folder = new File("profile_pics");
            if (!folder.exists()) folder.mkdir();

            String newFileName = "user_" + session.getId() + ".jpg";
            File destination = new File(folder, newFileName);
            java.nio.file.Files.copy(selectedFile.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String newPath = destination.getAbsolutePath();

            // Save path in DB
            try (Connection con = config.connectDB();
                 PreparedStatement pst = con.prepareStatement("UPDATE tbl_accounts SET acc_pic = ? WHERE acc_id = ?")) {
                pst.setString(1, newPath);
                pst.setInt(2, session.getId());
                pst.executeUpdate();
            }

            // Update image in Add Picture tab
            ImageIcon icon = new ImageIcon(newPath);
            Image img = icon.getImage().getScaledInstance(pic.getWidth(), pic.getHeight(), Image.SCALE_SMOOTH);
            pic.setIcon(new ImageIcon(img));

            JOptionPane.showMessageDialog(this, "Profile picture updated!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
    
    
private void loadprofile() {
    String sql = "SELECT acc_id, acc_name, acc_email, acc_contact, acc_role, acc_pic "
               + "FROM tbl_accounts WHERE acc_id = ?";

    try (Connection con = new config().connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, session.getId());

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // Set text fields
                name.setText(rs.getString("acc_name"));
                role.setText(rs.getString("acc_role"));
                email.setText(rs.getString("acc_email"));
                contact.setText(rs.getString("acc_contact"));
                id.setText(String.valueOf(rs.getInt("acc_id")));

                // Load image
                String path = rs.getString("acc_pic");
                if (path != null && !path.trim().isEmpty()) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        ImageIcon icon = new ImageIcon(path);

                        // Scale properly even if component not fully rendered yet
                        Image img = icon.getImage();
                        int width = pic.getWidth() > 0 ? pic.getWidth() : 150; // default size
                        int height = pic.getHeight() > 0 ? pic.getHeight() : 150;

                        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        pic.setIcon(new ImageIcon(scaledImg));
                    }
                } else {
                    pic.setIcon(null); // Clear if no image
                }

                pic.revalidate();
                pic.repaint();
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        e.printStackTrace();
    }
}

public static void logAction(int actorId, String actorRole, String action, String details) {
    String sql = "INSERT INTO tbl_logs (actor_id, actor_role, action, details, created_at) " +
                 "VALUES (?, ?, ?, ?, datetime('now'))";
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, actorId);
        pst.setString(2, actorRole);
        pst.setString(3, action);
        pst.setString(4, details);
        pst.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void loadSystemLogs() {
    // Use aliases to make column headers professional
    String sql = 
        "SELECT id AS 'Log ID', " +
        "actor_id AS 'User ID', " +
        "actor_role AS 'Role', " +
        "action AS 'Activity', " +
        "details AS 'Description', " +
        "created_at AS 'Logged At' " +
        "FROM tbl_logs " +
        "ORDER BY created_at DESC";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        
            // Use config helper to display directly into JTable
        config cfg = new config();
        cfg.displayData(sql, system_logs);
        system_logs.setModel(DbUtils.resultSetToTableModel(rs));
        system_logs.setDefaultEditor(Object.class, null); // make table read-only

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error loading system logs: " + e.getMessage());
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
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new admin().setVisible(true);
            }
        });
    }

    private void searchLogs(String keyword) {
    String sql = "SELECT id, actor_id, actor_role, action, details, created_at " +
                 "FROM tbl_logs WHERE actor_role LIKE ? OR action LIKE ? OR details LIKE ? ORDER BY created_at DESC";
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {
        String kw = "%" + keyword + "%";
        pst.setString(1, kw);
        pst.setString(2, kw);
        pst.setString(3, kw);
        ResultSet rs = pst.executeQuery();
        system_logs.setModel(DbUtils.resultSetToTableModel(rs));
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ID;
    private javax.swing.JLabel ROLE;
    private javax.swing.JLabel XBTN;
    private javax.swing.JPanel XPNL;
    private javax.swing.JLabel add;
    private javax.swing.JLabel addpic_here;
    private javax.swing.JPanel addpicture;
    private javax.swing.JPanel addpicture1;
    private javax.swing.JLabel addstaff;
    private javax.swing.JLabel admin_name;
    private javax.swing.JPanel analyticpane;
    private javax.swing.JPanel analytics;
    private javax.swing.JLabel analyticsbtn;
    private javax.swing.JLabel appbtn;
    private javax.swing.JPanel apppane;
    private javax.swing.JPanel bg;
    private javax.swing.JLabel billbtn;
    private javax.swing.JPanel billpane;
    private javax.swing.JLabel changephoto;
    private javax.swing.JLabel contact;
    private javax.swing.JPanel dashbox;
    private javax.swing.JLabel dashbtn;
    private javax.swing.JPanel dashpnl;
    private javax.swing.JPanel db;
    private javax.swing.JLabel delete;
    private javax.swing.JLabel deletestaff;
    private javax.swing.JLabel edit;
    private javax.swing.JLabel editprofile;
    private javax.swing.JLabel editprofile1;
    private javax.swing.JLabel editstaff;
    private javax.swing.JLabel email;
    private javax.swing.JPanel hdr;
    private javax.swing.JLabel id;
    private javax.swing.JLabel id1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
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
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel logoutbtn;
    private javax.swing.JPanel logoutpane;
    private javax.swing.JPanel mp;
    private javax.swing.JPanel mp1;
    private javax.swing.JPanel myprofile;
    private javax.swing.JLabel name;
    private javax.swing.JLabel p;
    private javax.swing.JLabel pic;
    private javax.swing.JPanel pp;
    private javax.swing.JLabel role;
    private javax.swing.JLabel save;
    private javax.swing.JTextField seach_logs;
    private javax.swing.JTextField search;
    private javax.swing.JLabel searchBTN;
    private javax.swing.JLabel search_systemlogs;
    private javax.swing.JTextField srch;
    private javax.swing.JLabel staffbtn;
    private javax.swing.JPanel staffmanage;
    private javax.swing.JPanel staffpane;
    private javax.swing.JTable stafftbl;
    private javax.swing.JTable system_logs;
    private javax.swing.JLabel system_logsbtn;
    private javax.swing.JPanel systemlogs;
    private javax.swing.JPanel systempane;
    private javax.swing.JTabbedPane tabbed;
    private javax.swing.JTable tbl;
    private javax.swing.JPanel userpane;
    private javax.swing.JPanel users;
    private javax.swing.JLabel usersbtn;
    // End of variables declaration//GEN-END:variables
}
