
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
import java.awt.geom.Ellipse2D;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import javax.swing.table.JTableHeader;
import java.awt.image.BufferedImage;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Cassandra Gallera
 */
public class admin extends javax.swing.JFrame {
 int xMouse, yMouse;
 private int selectedAccId;

private String mapIntToStatus(int status) {
    switch (status) {
        case 1: return "active";
        case 0: return "inactive";
        case 2: return "suspended";
        default: return "inactive"; // sensible fallback
    }
}


private String selectedPhotoPath;

Color editOriginal = new Color(0x007ACC);     // Dental blue
Color editHover = new Color(0x4DA6FF);        // Light blue
Color deleteOriginal = new Color(0xCC0000);   // Alert red
Color deleteHover = new Color(0xFF6666);      // Soft red


private boolean isValidEmail(String email) {
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    return email != null && email.matches(emailRegex);
    
    
}
private int mapStatusToInt(String status) {
    switch (status.toLowerCase()) {
        case "active": return 1;
        case "inactive": return 0;
        case "suspended": return 2;
        default: return 0;
    }

    
}
// --- Map Role ComboBox (with emoji) to DB values ---
private String mapRoleToDb(String roleWithEmoji) {
    switch (roleWithEmoji) {
        case "👑 Admin": return "admin";
        case "🦷 Dentist": return "dentist";
        case "👤 Staff": return "staff";
        case "🧑‍ Patient": return "patient";
        default: return "staff"; // sensible fallback
    }
}


    /**
     * Creates new form log
     */
    public admin() {
        initComponents();
       acctable();
       profilePic.setPreferredSize(new Dimension(150, 150));
      
       loadSystemLogs();
       initSearchPlaceholder();
       staffDentistTable();
       initStaffDentistSearchPlaceholder();
      
       refreshProfile();
       applyStaffDentistFilter();
       loadAllUsers();
      
       loadProfileDisplay();
       loadDentistAvailable(); // ✅ show available dentists count
       
       
       
      setDentist_specialty.setModel(new DefaultComboBoxModel<>(
    new String[] {
        "General Dentistry",
        "Cosmetic Dentistry",
        "Oral Surgery",
        "Endodontics",
        "Prosthodontics",
        "Orthodontics"
    }
));

    // ✅ Insert here — after initComponents()
    editUser_status.setModel(new DefaultComboBoxModel<>(
        new String[] { "active", "inactive", "suspended" }
    ));

       
     // ✅ Role dropdown with All, Admin, Dentist, Staff, Patient
       allUsers.setModel(new DefaultComboBoxModel<>(
       new String[] { "🌐 All", "👑 Admin", "🦷 Dentist", "👤 Staff", "🧑‍ Patient" }
     ));

      // ✅ Status dropdown with emojis
      statusAllUsers.setModel(new DefaultComboBoxModel<>(
      new String[] { "🟢 Active", "🔴 Inactive", "⚪ Suspended" }
      ));

     // ✅ Emoji-friendly font (declare once and reuse)
      Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
      allUsers.setFont(emojiFont);
      statusAllUsers.setFont(emojiFont);

     // ✅ Wire Filter button for Users Management
     filterAllUsers.addMouseListener(new java.awt.event.MouseAdapter() {
     @Override
     public void mouseClicked(java.awt.event.MouseEvent evt) {
        filterUsers();
    }
    });


// ✅ Setup role dropdown with All, Dentist, Staff (Workforce Management)
role_staff_dentist.setModel(new DefaultComboBoxModel<>(
    new String[] { "🌐 All", "🦷 Dentist", "👤 Staff" }
));

// ✅ Status dropdown with emojis (Workforce Management)
status_staff_dentist.setModel(new DefaultComboBoxModel<>(
    new String[] { "🟢 Active", "🔴 Inactive", "⚪ Suspended" }
));

// ✅ Reuse the same emojiFont
role_staff_dentist.setFont(emojiFont);
status_staff_dentist.setFont(emojiFont);

// ✅ Wire Filter button for Workforce Management
filter_search.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        applyStaffDentistFilter();
    }
});

       
allUsers.addItemListener(e -> {
    if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        filterUsers(); // ✅ automatically run filter when role changes
    }
});

statusAllUsers.addItemListener(e -> {
    if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        filterUsers(); // ✅ automatically run filter when status changes
    }
});



       
       delete.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        deleteUser();
    }
});

       
edit_email.addFocusListener(new java.awt.event.FocusAdapter() {
    @Override
    public void focusLost(java.awt.event.FocusEvent evt) {
        String email = edit_email.getText().trim();
        if (!isValidEmail(email)) {
        }
    }
});

       status_newUser.setModel(new DefaultComboBoxModel<>(new String[] { "Active", "Inactive", "Suspended" }));
       role_newUser.setModel(new DefaultComboBoxModel<>(new String[] { "Admin", "Dentist", "Staff", "Patient" }));

       save_newUser.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        saveNewUser();
    }
});

       
    saveEditUser.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        saveEditedUserRoleStatus();
    }
});





       
filterAllUsers.setText("Search"); // emoji on button


   JPanel searchBar = new JPanel(new BorderLayout());
   searchBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

   JLabel emojiLabel = new JLabel("🔎");
   emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
   emojiLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

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
filterAllUsers.addMouseListener(new java.awt.event.MouseAdapter() {
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

        return;
    }

    performSearch(query);

}


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

    
    
// Call this in your constructor after initComponents()

private void initStaffDentistSearchPlaceholder() {
    staff_dentistSearch.setText("🔎 Search Dentist/Staff...");
    staff_dentistSearch.setForeground(Color.GRAY);
    staff_dentistSearch.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // emoji-friendly font

    staff_dentistSearch.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (staff_dentistSearch.getText().equals("🔎 Search Dentist/Staff...")) {
                staff_dentistSearch.setText(""); // clear placeholder
                staff_dentistSearch.setForeground(Color.BLACK);
                staff_dentistSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // normal font
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (staff_dentistSearch.getText().isEmpty()) {
                staff_dentistSearch.setText("🔎 Search Dentist/Staff...");
                staff_dentistSearch.setForeground(Color.GRAY);
                staff_dentistSearch.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            }
        }
    });
}

private void performSearch(String search1) {
    String sql;
    String baseQuery =
        "SELECT acc_id AS Id, " +
        "acc_name AS Name, " +
        "acc_email AS Email, " +
        "acc_contact AS Contact, " +
        "acc_role AS Role, " +
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
                // ✅ Add column width adjustments here
    tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    TableColumnModel columnModel = tbl.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(50);   // id
    columnModel.getColumn(1).setPreferredWidth(150);  // name
    columnModel.getColumn(2).setPreferredWidth(250);  // email
    columnModel.getColumn(3).setPreferredWidth(150);  // contact
    columnModel.getColumn(4).setPreferredWidth(100);  // role
    columnModel.getColumn(5).setPreferredWidth(100);  // status
        }

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Search error: " + e.getMessage());
    }
}


// Helper for circular profile picture in "My Profile" section
private void setCircularProfilePic(File imgFile) {
    try {
        BufferedImage image = ImageIO.read(imgFile);
        int size = profilePic.getWidth();
        if (size <= 0) size = 150; // safe default

        Image scaled = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        BufferedImage circleBuffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = circleBuffer.createGraphics();
        try {
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(scaled, 0, 0, null);
        } finally {
            g2.dispose();
        }

        profilePic.setIcon(new ImageIcon(circleBuffer));
    } catch (Exception e) {
        // fallback default icon if anything fails
        profilePic.setIcon(new ImageIcon(getClass().getResource("/img/default-user.png")));
    }
}

// Helper for circular profile picture in the header (dashboard)
private void setCircularAdminPic(File imgFile) {
    try {
        BufferedImage image = ImageIO.read(imgFile);
        int size = circle_adminPic.getWidth();
        if (size <= 0) size = 100; // safe default

        Image scaled = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        BufferedImage circleBuffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = circleBuffer.createGraphics();
        try {
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(scaled, 0, 0, null);
        } finally {
            g2.dispose();
        }

        circle_adminPic.setIcon(new ImageIcon(circleBuffer));
    } catch (Exception e) {
        // fallback default icon if anything fails
        circle_adminPic.setIcon(new ImageIcon(getClass().getResource("/img/default-user.png")));
    }
}



// Load admin name + profile picture from DB
private void loadAdminProfile() {
    String sql = "SELECT acc_name, acc_pic FROM tbl_accounts WHERE acc_role = 'Admin' LIMIT 1";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        if (rs.next()) {
            String name = rs.getString("acc_name");
            String imgPath = rs.getString("acc_pic");

            // Set admin name in header
            admin_name.setText(name);

            // Set circular profile picture in header
            if (imgPath != null && !imgPath.trim().isEmpty()) {
                File imgFile = new File(imgPath);
                if (imgFile.exists()) {
                    setCircularAdminPic(imgFile);
                } else {
                    // fallback default icon if file missing
                    circle_adminPic.setIcon(new ImageIcon(getClass().getResource("/img/default-user.png")));
                }
            } else {
                // fallback default icon if path is null/empty
                circle_adminPic.setIcon(new ImageIcon(getClass().getResource("/img/default-user.png")));
            }

            circle_adminPic.revalidate();
            circle_adminPic.repaint();
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading admin profile: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
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
        circle_adminPic = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tabbed = new javax.swing.JTabbedPane();
        db = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        todays_appointment = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        patients_in_waiting = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        Dentist_available = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel32 = new javax.swing.JLabel();
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
        filterAllUsers = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        allUsers = new javax.swing.JComboBox<>();
        statusAllUsers = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        staffmanage = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        staff_dentist_table = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        editstaff = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        addstaff = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        deletestaff = new javax.swing.JLabel();
        staff_dentistSearch = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        filter_search = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        role_staff_dentist = new javax.swing.JComboBox<>();
        status_staff_dentist = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        analytics = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jPanel35 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        jLabel73 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jPanel38 = new javax.swing.JPanel();
        jLabel75 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jLabel36 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        systemlogs = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        system_logs = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        seach_logs = new javax.swing.JTextField();
        jPanel24 = new javax.swing.JPanel();
        search_systemlogs = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        myprofile = new javax.swing.JPanel();
        mp = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        addpicture = new javax.swing.JPanel();
        profilePic = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jPanel54 = new javax.swing.JPanel();
        savechanges_profile = new javax.swing.JLabel();
        jPanel55 = new javax.swing.JPanel();
        name = new javax.swing.JLabel();
        jPanel56 = new javax.swing.JPanel();
        email = new javax.swing.JLabel();
        jPanel57 = new javax.swing.JPanel();
        contact = new javax.swing.JLabel();
        jPanel58 = new javax.swing.JPanel();
        role = new javax.swing.JLabel();
        jPanel67 = new javax.swing.JPanel();
        change_photo = new javax.swing.JLabel();
        jPanel68 = new javax.swing.JPanel();
        editprofile = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        mp1 = new javax.swing.JPanel();
        jPanel49 = new javax.swing.JPanel();
        addpicture2 = new javax.swing.JPanel();
        profilePic1 = new javax.swing.JLabel();
        jPanel59 = new javax.swing.JPanel();
        change_photo1 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        edit_newpass = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        edit_confirmpass = new javax.swing.JTextField();
        jPanel61 = new javax.swing.JPanel();
        cancel_edit = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jPanel62 = new javax.swing.JPanel();
        editprofile2 = new javax.swing.JLabel();
        edit_fullname = new javax.swing.JTextField();
        edit_email = new javax.swing.JTextField();
        edit_contact = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        role_ = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        name_newUser = new javax.swing.JTextField();
        email_newUser = new javax.swing.JTextField();
        password_newUser = new javax.swing.JTextField();
        contact_newUser = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        role_newUser = new javax.swing.JComboBox<>();
        status_newUser = new javax.swing.JComboBox<>();
        jPanel26 = new javax.swing.JPanel();
        save_newUser = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jPanel47 = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        editUser_name = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        editUser_email = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        editUser_contact = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        editUser_role = new javax.swing.JComboBox<>();
        jPanel48 = new javax.swing.JPanel();
        saveEditUser = new javax.swing.JLabel();
        jPanel50 = new javax.swing.JPanel();
        jLabel113 = new javax.swing.JLabel();
        editUser_status = new javax.swing.JComboBox<>();
        jLabel99 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel39 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        jPanel40 = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        jPanel41 = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        jPanel42 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jPanel44 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jPanel45 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jComboBox8 = new javax.swing.JComboBox<>();
        jComboBox9 = new javax.swing.JComboBox<>();
        jComboBox10 = new javax.swing.JComboBox<>();
        jPanel46 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel59 = new javax.swing.JLabel();
        app_search = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jPanel33 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel51 = new javax.swing.JPanel();
        jPanel52 = new javax.swing.JPanel();
        jPanel53 = new javax.swing.JPanel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jPanel60 = new javax.swing.JPanel();
        jLabel106 = new javax.swing.JLabel();
        addname_staff = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        addemail_staff = new javax.swing.JTextField();
        jLabel112 = new javax.swing.JLabel();
        addpass_staff = new javax.swing.JTextField();
        jLabel114 = new javax.swing.JLabel();
        addcontact_staff = new javax.swing.JTextField();
        jLabel115 = new javax.swing.JLabel();
        role_staff = new javax.swing.JComboBox<>();
        jLabel116 = new javax.swing.JLabel();
        status_staff = new javax.swing.JComboBox<>();
        jPanel64 = new javax.swing.JPanel();
        cancelAddingstaf = new javax.swing.JLabel();
        jPanel63 = new javax.swing.JPanel();
        saveNewstaff = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jPanel65 = new javax.swing.JPanel();
        jPanel66 = new javax.swing.JPanel();
        jPanel69 = new javax.swing.JPanel();
        jLabel127 = new javax.swing.JLabel();
        jLabel128 = new javax.swing.JLabel();
        jPanel70 = new javax.swing.JPanel();
        jLabel129 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        jLabel135 = new javax.swing.JLabel();
        jPanel71 = new javax.swing.JPanel();
        staff_role = new javax.swing.JLabel();
        staff_stat = new javax.swing.JComboBox<>();
        staff_name = new javax.swing.JLabel();
        staff_email = new javax.swing.JLabel();
        staff_contact = new javax.swing.JLabel();
        jPanel77 = new javax.swing.JPanel();
        saveEdit_staff = new javax.swing.JLabel();
        jPanel78 = new javax.swing.JPanel();
        cancelEditStaff = new javax.swing.JLabel();
        jLabel126 = new javax.swing.JLabel();
        jPanel72 = new javax.swing.JPanel();
        jPanel73 = new javax.swing.JPanel();
        jPanel74 = new javax.swing.JPanel();
        jLabel136 = new javax.swing.JLabel();
        jLabel137 = new javax.swing.JLabel();
        jPanel75 = new javax.swing.JPanel();
        jLabel139 = new javax.swing.JLabel();
        jLabel140 = new javax.swing.JLabel();
        jLabel141 = new javax.swing.JLabel();
        jLabel142 = new javax.swing.JLabel();
        jLabel143 = new javax.swing.JLabel();
        jLabel144 = new javax.swing.JLabel();
        jLabel145 = new javax.swing.JLabel();
        jPanel76 = new javax.swing.JPanel();
        dentist_role = new javax.swing.JLabel();
        dentist_stat = new javax.swing.JComboBox<>();
        dentist_name = new javax.swing.JLabel();
        dentist_email = new javax.swing.JLabel();
        dentist_contact = new javax.swing.JLabel();
        jPanel79 = new javax.swing.JPanel();
        saveEdit_dentist = new javax.swing.JLabel();
        jPanel80 = new javax.swing.JPanel();
        cancelsave_dentist = new javax.swing.JLabel();
        jLabel147 = new javax.swing.JLabel();
        setDentist_specialty = new javax.swing.JComboBox<>();
        jLabel138 = new javax.swing.JLabel();
        jPanel81 = new javax.swing.JPanel();
        jPanel82 = new javax.swing.JPanel();
        jLabel146 = new javax.swing.JLabel();

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
        dashbtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
        dashpnl.add(dashbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 30));

        dashbox.add(dashpnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, 30));

        userpane.setBackground(new java.awt.Color(255, 255, 255));
        userpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        usersbtn.setBackground(new java.awt.Color(255, 255, 255));
        usersbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        usersbtn.setForeground(new java.awt.Color(0, 51, 204));
        usersbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usersbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-users-24.png"))); // NOI18N
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
        userpane.add(usersbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 30));

        dashbox.add(userpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 180, 30));

        staffpane.setBackground(new java.awt.Color(255, 255, 255));

        staffbtn.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        staffbtn.setForeground(new java.awt.Color(0, 51, 204));
        staffbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        staffbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-users-24 (1).png"))); // NOI18N
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
            .addComponent(staffbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
        );
        staffpaneLayout.setVerticalGroup(
            staffpaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, staffpaneLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(staffbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        dashbox.add(staffpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 180, 30));

        analyticpane.setBackground(new java.awt.Color(255, 255, 255));

        analyticsbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        analyticsbtn.setForeground(new java.awt.Color(0, 51, 204));
        analyticsbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        analyticsbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-analytics-24.png"))); // NOI18N
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
            .addGroup(analyticpaneLayout.createSequentialGroup()
                .addComponent(analyticsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 82, Short.MAX_VALUE))
        );
        analyticpaneLayout.setVerticalGroup(
            analyticpaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(analyticsbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        dashbox.add(analyticpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 180, 30));

        systempane.setBackground(new java.awt.Color(255, 255, 255));

        system_logsbtn.setBackground(new java.awt.Color(255, 255, 255));
        system_logsbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        system_logsbtn.setForeground(new java.awt.Color(0, 51, 204));
        system_logsbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        system_logsbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-system-data-24.png"))); // NOI18N
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
            .addGroup(systempaneLayout.createSequentialGroup()
                .addComponent(system_logsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 57, Short.MAX_VALUE))
        );
        systempaneLayout.setVerticalGroup(
            systempaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(system_logsbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        dashbox.add(systempane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 270, 180, 30));

        pp.setBackground(new java.awt.Color(255, 255, 255));
        pp.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        p.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        p.setForeground(new java.awt.Color(0, 51, 204));
        p.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-settings-24.png"))); // NOI18N
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
        pp.add(p, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 2, 170, 30));

        dashbox.add(pp, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 180, 30));

        apppane.setBackground(new java.awt.Color(255, 255, 255));
        apppane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        appbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        appbtn.setForeground(new java.awt.Color(0, 51, 204));
        appbtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        appbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-today-24.png"))); // NOI18N
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
        apppane.add(appbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 140, 30));

        dashbox.add(apppane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 180, 30));

        billpane.setBackground(new java.awt.Color(255, 255, 255));
        billpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        billbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        billbtn.setForeground(new java.awt.Color(0, 51, 204));
        billbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        billbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-billing-24.png"))); // NOI18N
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
        billpane.add(billbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 30));

        dashbox.add(billpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 180, 30));

        logoutpane.setBackground(new java.awt.Color(255, 255, 255));
        logoutpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logoutbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        logoutbtn.setForeground(new java.awt.Color(0, 51, 204));
        logoutbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-logout-24.png"))); // NOI18N
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
        logoutpane.add(logoutbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 30));

        dashbox.add(logoutpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 180, 30));

        bg.add(dashbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 180, 470));

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
            .addComponent(XBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );
        XPNLLayout.setVerticalGroup(
            XPNLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(XBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        hdr.add(XPNL, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 40, 30));

        jLabel2.setBackground(new java.awt.Color(51, 102, 255));
        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 255));
        jLabel2.setText("Dental");
        hdr.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 60, 50));

        admin_name.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        hdr.add(admin_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 0, 110, 30));

        circle_adminPic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        circle_adminPic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-circle-48.png"))); // NOI18N
        hdr.add(circle_adminPic, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 0, 50, 50));

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Admin");
        jLabel13.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        hdr.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 30, 90, 20));

        jLabel22.setFont(new java.awt.Font("Modern No. 20", 3, 17)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Care");
        hdr.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 60, 50));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/5046faad4a4c9af72bcf4fe75c8a11d0.jpg"))); // NOI18N
        hdr.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 50));

        bg.add(hdr, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 50));

        db.setBackground(new java.awt.Color(255, 255, 255));
        db.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 21)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 51, 102));
        jLabel3.setText("System Overview");
        db.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 40));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("________________________________________________________________________________________");
        db.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 30));

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

        todays_appointment.setText("jLabel123");
        jPanel8.add(todays_appointment, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 26, -1, 30));

        db.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 200, 60));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 255));
        jLabel7.setText("Patient's In Waiting");
        jPanel9.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, -1, 40));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-waiting-room-25.png"))); // NOI18N
        jPanel9.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 60, 50));

        patients_in_waiting.setText("jLabel124");
        jPanel9.add(patients_in_waiting, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, -1, 30));

        db.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 60, 200, 60));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-dentist-25.png"))); // NOI18N
        jPanel10.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 70, 50));

        jLabel8.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 255));
        jLabel8.setText("Dentist Available");
        jPanel10.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, -1, 40));

        Dentist_available.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        Dentist_available.setText("jLabel125");
        jPanel10.add(Dentist_available, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 30, -1, 30));

        db.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 200, 60));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(jTable1);

        db.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 300, 180));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane5.setViewportView(jTable2);

        db.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 140, 300, 180));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(jTable3);

        db.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 300, 120));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane7.setViewportView(jTable4);

        db.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 340, 300, 120));

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        db.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 640, 140));

        tabbed.addTab("db", db);

        users.setBackground(new java.awt.Color(255, 255, 255));
        users.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl.setGridColor(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportView(tbl);

        users.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 610, 280));

        jPanel1.setBackground(new java.awt.Color(0, 51, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        add.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        add.setForeground(new java.awt.Color(255, 255, 255));
        add.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-plus-sign-20.png"))); // NOI18N
        add.setText("Register User");
        add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addMouseExited(evt);
            }
        });
        jPanel1.add(add, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        users.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 160, 30));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        edit.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        edit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        edit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-20.png"))); // NOI18N
        edit.setText("Edit User");
        edit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editMouseExited(evt);
            }
        });
        jPanel2.add(edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        users.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 160, 30));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        delete.setBackground(new java.awt.Color(255, 255, 255));
        delete.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        delete.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-trash-20.png"))); // NOI18N
        delete.setText("Delete User");
        delete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteMouseExited(evt);
            }
        });
        jPanel3.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        users.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 160, 30));

        search.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchKeyTyped(evt);
            }
        });
        users.add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 250, 30));

        jPanel4.setBackground(new java.awt.Color(0, 51, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        filterAllUsers.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        filterAllUsers.setForeground(new java.awt.Color(255, 255, 255));
        filterAllUsers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        filterAllUsers.setText("Filter");
        filterAllUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterAllUsersMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                filterAllUsersMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                filterAllUsersMouseExited(evt);
            }
        });
        jPanel4.add(filterAllUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 30));

        users.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 110, 70, 30));

        jLabel15.setFont(new java.awt.Font("Trebuchet MS", 1, 25)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 51, 102));
        jLabel15.setText("Users Management");
        users.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, 50));

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-users-40.png"))); // NOI18N
        users.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 50, 50));

        jLabel31.setForeground(new java.awt.Color(204, 204, 204));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("_____________________________________________________________________________________________");
        users.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, -1, 60));

        allUsers.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Admin", "Dentist", "Staff" }));
        allUsers.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        users.add(allUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 110, 140, 30));

        statusAllUsers.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive", "Suspended" }));
        statusAllUsers.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        users.add(statusAllUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 110, 140, 30));

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        users.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 650, 170));

        tabbed.addTab("users", users);

        staffmanage.setBackground(new java.awt.Color(255, 255, 255));
        staffmanage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255), 0));
        staffmanage.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        staff_dentist_table.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        staff_dentist_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        staff_dentist_table.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(staff_dentist_table);

        staffmanage.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 630, 290));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        editstaff.setBackground(new java.awt.Color(255, 255, 255));
        editstaff.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        editstaff.setForeground(new java.awt.Color(51, 51, 51));
        editstaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editstaff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-20.png"))); // NOI18N
        editstaff.setText("Edit Staff");
        editstaff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editstaffMouseClicked(evt);
            }
        });
        jPanel5.add(editstaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        staffmanage.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 160, 30));

        jLabel16.setFont(new java.awt.Font("Trebuchet MS", 1, 25)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 51, 102));
        jLabel16.setText("Workforce");
        staffmanage.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, 50));

        jPanel6.setBackground(new java.awt.Color(0, 51, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addstaff.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        addstaff.setForeground(new java.awt.Color(255, 255, 255));
        addstaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addstaff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-plus-sign-20.png"))); // NOI18N
        addstaff.setText("Add Staff");
        addstaff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addstaffMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addstaffMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addstaffMouseExited(evt);
            }
        });
        jPanel6.add(addstaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        staffmanage.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 160, 30));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        deletestaff.setBackground(new java.awt.Color(51, 153, 255));
        deletestaff.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        deletestaff.setForeground(new java.awt.Color(51, 51, 51));
        deletestaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deletestaff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-trash-20.png"))); // NOI18N
        deletestaff.setText("Delete Staff");
        jPanel11.add(deletestaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        staffmanage.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 160, 30));

        staff_dentistSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        staff_dentistSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                staff_dentistSearchKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                staff_dentistSearchKeyTyped(evt);
            }
        });
        staffmanage.add(staff_dentistSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 250, 30));

        jPanel15.setBackground(new java.awt.Color(0, 51, 255));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        filter_search.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        filter_search.setForeground(new java.awt.Color(255, 255, 255));
        filter_search.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        filter_search.setText("Filter");
        filter_search.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filter_searchMouseClicked(evt);
            }
        });
        jPanel15.add(filter_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 30));

        staffmanage.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 110, 70, 30));

        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-staff-40.png"))); // NOI18N
        staffmanage.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 50, 60));

        jLabel17.setFont(new java.awt.Font("Trebuchet MS", 1, 25)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 153, 255));
        jLabel17.setText("Management");
        staffmanage.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, -1, 50));

        jLabel62.setForeground(new java.awt.Color(204, 204, 204));
        jLabel62.setText("__________________________________________________________________________________________");
        staffmanage.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 40));

        role_staff_dentist.setBackground(new java.awt.Color(153, 255, 255));
        role_staff_dentist.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Dentist", "Staff" }));
        role_staff_dentist.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        staffmanage.add(role_staff_dentist, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 110, 140, 30));

        status_staff_dentist.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive", "Suspended" }));
        status_staff_dentist.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        staffmanage.add(status_staff_dentist, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 110, 140, 30));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        staffmanage.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -14, 650, 170));

        tabbed.addTab("wm", staffmanage);

        analytics.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel34.setBackground(new java.awt.Color(255, 255, 255));
        jPanel34.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel35.setBackground(new java.awt.Color(255, 255, 255));
        jPanel35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel35.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel72.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(0, 51, 102));
        jLabel72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-revenue-20.png"))); // NOI18N
        jLabel72.setText("Revenue Today");
        jPanel35.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 140, 30));

        jLabel119.setText("jLabel119");
        jPanel35.add(jLabel119, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, -1));

        jPanel34.add(jPanel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 150, 70));

        jPanel36.setBackground(new java.awt.Color(255, 255, 255));
        jPanel36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel36.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel73.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(0, 51, 102));
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-data-pending-20.png"))); // NOI18N
        jLabel73.setText("Pending Balance");
        jPanel36.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 150, 30));

        jLabel120.setText("jLabel120");
        jPanel36.add(jLabel120, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, -1));

        jPanel34.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 150, 70));

        jPanel37.setBackground(new java.awt.Color(255, 255, 255));
        jPanel37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel37.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel74.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(0, 51, 102));
        jLabel74.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jLabel74.setText("Today's Visits");
        jPanel37.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 4, 130, 40));

        jLabel121.setText("jLabel121");
        jPanel37.add(jLabel121, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, -1, -1));

        jPanel34.add(jPanel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 150, 70));

        jPanel38.setBackground(new java.awt.Color(255, 255, 255));
        jPanel38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel38.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel75.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(0, 51, 102));
        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel75.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-20.png"))); // NOI18N
        jLabel75.setText("Monthly Revenue");
        jPanel38.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 150, 30));

        jLabel122.setText("jLabel122");
        jPanel38.add(jLabel122, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, -1, -1));

        jPanel34.add(jPanel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 150, 70));

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane9.setViewportView(jTable6);

        jPanel34.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 610, 180));

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane10.setViewportView(jTable7);

        jPanel34.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 610, 150));

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel34.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 110));

        jLabel95.setForeground(new java.awt.Color(204, 204, 204));
        jLabel95.setText("__________________________________________________________________________________________");
        jPanel34.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 96, -1, 20));

        analytics.add(jPanel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("ana", analytics);

        systemlogs.setBackground(new java.awt.Color(255, 255, 255));
        systemlogs.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        system_logs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(system_logs);

        systemlogs.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 630, 330));

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 1, 25)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 102));
        jLabel5.setText("System Activity Logs");
        systemlogs.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, 40));

        seach_logs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        seach_logs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                seach_logsKeyTyped(evt);
            }
        });
        systemlogs.add(seach_logs, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 530, 30));

        jPanel24.setBackground(new java.awt.Color(0, 51, 255));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        search_systemlogs.setBackground(new java.awt.Color(255, 255, 255));
        search_systemlogs.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
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
        jPanel24.add(search_systemlogs, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 70, 30));

        systemlogs.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, 90, 30));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-analytics-40.png"))); // NOI18N
        systemlogs.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 80, 40));

        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("__________________________________________________________________________________________");
        systemlogs.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 86, -1, 50));

        jLabel85.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        systemlogs.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 650, 120));

        tabbed.addTab("logs", systemlogs);

        myprofile.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mp.setBackground(new java.awt.Color(255, 255, 255));
        mp.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addpicture.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        profilePic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        profilePic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png"))); // NOI18N
        addpicture.add(profilePic, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 150));

        jPanel18.add(addpicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 210, 150));

        jLabel25.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel25.setText("My Profile");
        jPanel18.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 180, 40));

        jLabel76.setForeground(new java.awt.Color(204, 204, 204));
        jLabel76.setText("_____________________________________________________________________________________");
        jPanel18.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 600, 30));

        jLabel30.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel30.setText("Role :");
        jPanel18.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 270, 60, 30));

        jLabel26.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel26.setText("Full Name :");
        jPanel18.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 150, 80, 30));

        jLabel28.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel28.setText("Email :");
        jPanel18.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 190, -1, 30));

        jLabel29.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel29.setText("Contact :");
        jPanel18.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, 80, 30));

        jLabel78.setForeground(new java.awt.Color(204, 204, 204));
        jLabel78.setText("____________________________________________________________________________________");
        jPanel18.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, -1, -1));

        jLabel79.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel79.setText("Change Password");
        jPanel18.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, -1, 20));

        jLabel80.setForeground(new java.awt.Color(204, 204, 204));
        jLabel80.setText("____________________________________________________________________________________");
        jPanel18.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, -1, 40));

        jLabel81.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel81.setText("New Password:");
        jPanel18.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 360, -1, 30));

        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel18.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 360, 270, 28));

        jLabel82.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel82.setText("Confirm New Password:");
        jPanel18.add(jLabel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 390, -1, 30));

        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel18.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 390, 270, 28));

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Cancel");
        jPanel17.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 110, 20));

        jPanel18.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 450, 110, 22));

        jLabel83.setForeground(new java.awt.Color(204, 204, 204));
        jLabel83.setText("____________________________________________________________________________________");
        jPanel18.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 420, -1, -1));

        jPanel54.setBackground(new java.awt.Color(0, 51, 204));
        jPanel54.setForeground(new java.awt.Color(0, 51, 255));
        jPanel54.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        savechanges_profile.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        savechanges_profile.setForeground(new java.awt.Color(255, 255, 255));
        savechanges_profile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        savechanges_profile.setText("Save Changes");
        savechanges_profile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                savechanges_profileMouseClicked(evt);
            }
        });
        jPanel54.add(savechanges_profile, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 22));

        jPanel18.add(jPanel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 450, 100, 22));

        jPanel55.setBackground(new java.awt.Color(255, 255, 255));
        jPanel55.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel55.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        name.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel55.add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 260, 30));

        jPanel18.add(jPanel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 150, 270, 30));

        jPanel56.setBackground(new java.awt.Color(255, 255, 255));
        jPanel56.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel56.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        email.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel56.add(email, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 260, 30));

        jPanel18.add(jPanel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 190, 270, 30));

        jPanel57.setBackground(new java.awt.Color(255, 255, 255));
        jPanel57.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel57.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        contact.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel57.add(contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 260, 30));

        jPanel18.add(jPanel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 230, 270, 30));

        jPanel58.setBackground(new java.awt.Color(255, 255, 255));
        jPanel58.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel58.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        role.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel58.add(role, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 130, 30));

        jPanel18.add(jPanel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 270, 270, 30));

        jPanel67.setBackground(new java.awt.Color(0, 51, 204));

        change_photo.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        change_photo.setForeground(new java.awt.Color(255, 255, 255));
        change_photo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        change_photo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-image-24.png"))); // NOI18N
        change_photo.setText("Add Photo");
        change_photo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                change_photoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel67Layout = new javax.swing.GroupLayout(jPanel67);
        jPanel67.setLayout(jPanel67Layout);
        jPanel67Layout.setHorizontalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(change_photo, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        jPanel67Layout.setVerticalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel67Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(change_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.add(jPanel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 100, 30));

        jPanel68.setBackground(new java.awt.Color(0, 51, 204));
        jPanel68.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        editprofile.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        editprofile.setForeground(new java.awt.Color(255, 255, 255));
        editprofile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editprofile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-20.png"))); // NOI18N
        editprofile.setText("Edit Profile");
        editprofile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editprofileMouseClicked(evt);
            }
        });
        jPanel68.add(editprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel18.add(jPanel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, 100, 30));

        jLabel97.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jPanel18.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 84, 600, 230));

        jLabel98.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jPanel18.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 346, 590, 90));

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel18.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 650, 480));

        mp.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 650, 490));

        myprofile.add(mp, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, -1, 650, 470));

        tabbed.addTab("pfp", myprofile);

        mp1.setBackground(new java.awt.Color(255, 255, 255));
        mp1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel49.setBackground(new java.awt.Color(255, 255, 255));
        jPanel49.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addpicture2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        profilePic1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        profilePic1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png"))); // NOI18N
        profilePic1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                profilePic1MouseClicked(evt);
            }
        });
        addpicture2.add(profilePic1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 160, 110));

        jPanel59.setBackground(new java.awt.Color(255, 255, 255));
        jPanel59.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel59.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        change_photo1.setFont(new java.awt.Font("Trebuchet MS", 1, 13)); // NOI18N
        change_photo1.setForeground(new java.awt.Color(0, 102, 204));
        change_photo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24 (1).png"))); // NOI18N
        change_photo1.setText("Change Photo");
        change_photo1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                change_photo1MouseClicked(evt);
            }
        });
        jPanel59.add(change_photo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 30));

        addpicture2.add(jPanel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 140, 30));

        jPanel49.add(addpicture2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 200, 180));

        jLabel27.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel27.setText("My Profile");
        jPanel49.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 180, 50));

        jLabel77.setForeground(new java.awt.Color(204, 204, 204));
        jLabel77.setText("___________________________________________________________________________________");
        jPanel49.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 590, -1));

        jLabel86.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel86.setText("Role :");
        jPanel49.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 220, 60, 30));

        jLabel87.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel87.setText("Full Name:");
        jPanel49.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 100, 100, 30));

        jLabel88.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel88.setText(" Email :");
        jPanel49.add(jLabel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 140, 50, 30));

        jLabel89.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel89.setText("Contact :");
        jPanel49.add(jLabel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 180, 110, 30));

        jLabel90.setForeground(new java.awt.Color(204, 204, 204));
        jLabel90.setText("___________________________________________________________________________________");
        jPanel49.add(jLabel90, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 290, -1, -1));

        jLabel91.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel91.setText("Change Password");
        jPanel49.add(jLabel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, -1, 30));

        jLabel92.setForeground(new java.awt.Color(204, 204, 204));
        jLabel92.setText("___________________________________________________________________________________");
        jPanel49.add(jLabel92, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, -1, 40));

        jLabel93.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel93.setText("New Password:");
        jPanel49.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 360, -1, 30));

        edit_newpass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel49.add(edit_newpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 360, 270, 28));

        jLabel94.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel94.setText("Confirm New Password:");
        jPanel49.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 390, -1, 30));

        edit_confirmpass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel49.add(edit_confirmpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 390, 270, 28));

        jPanel61.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel61.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel61MouseClicked(evt);
            }
        });
        jPanel61.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cancel_edit.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cancel_edit.setForeground(new java.awt.Color(51, 51, 51));
        cancel_edit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cancel_edit.setText("Cancel");
        jPanel61.add(cancel_edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 0, 50, 20));

        jPanel49.add(jPanel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 450, 110, 20));

        jLabel96.setForeground(new java.awt.Color(204, 204, 204));
        jLabel96.setText("___________________________________________________________________________________");
        jPanel49.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 420, -1, -1));

        jPanel62.setBackground(new java.awt.Color(0, 51, 204));
        jPanel62.setForeground(new java.awt.Color(0, 51, 255));
        jPanel62.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        editprofile2.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        editprofile2.setForeground(new java.awt.Color(255, 255, 255));
        editprofile2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editprofile2.setText("Save Changes");
        editprofile2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editprofile2MouseClicked(evt);
            }
        });
        jPanel62.add(editprofile2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 22));

        jPanel49.add(jPanel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 450, 100, 22));

        edit_fullname.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel49.add(edit_fullname, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 100, 270, 30));

        edit_email.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel49.add(edit_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 140, 270, 30));

        edit_contact.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel49.add(edit_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 180, 270, 30));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        role_.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel16.add(role_, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 30));

        jPanel49.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 220, 270, 30));

        jLabel84.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel49.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 490));

        mp1.add(jPanel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 650, 480));

        tabbed.addTab("editpfp", mp1);

        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel22.setBackground(new java.awt.Color(0, 0, 204));
        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel22.setForeground(new java.awt.Color(255, 255, 255));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Add New User");
        jPanel22.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 200, 70));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50 (1).png"))); // NOI18N
        jPanel22.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 80, 70));

        jPanel23.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 540, 70));

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));
        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel39.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel39.setText("Name:");
        jPanel25.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, 30));

        jLabel42.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel42.setText("Email:");
        jPanel25.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, -1, 30));

        jLabel38.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel38.setText("Password:");
        jPanel25.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, -1, 30));

        jLabel44.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel44.setText("Phone Number:");
        jPanel25.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, 30));

        jLabel46.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel46.setText("Role:");
        jPanel25.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, -1, 40));

        name_newUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel25.add(name_newUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 370, 30));

        email_newUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel25.add(email_newUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 370, 30));

        password_newUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel25.add(password_newUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 350, 30));

        contact_newUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel25.add(contact_newUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 160, 310, 30));

        jLabel24.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel24.setText("Status:");
        jPanel25.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, -1, 30));

        role_newUser.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        role_newUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "staff", "dentist", "staff" }));
        role_newUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        role_newUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                role_newUserMouseClicked(evt);
            }
        });
        jPanel25.add(role_newUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 190, 30));

        status_newUser.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        status_newUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "active", "inactive" }));
        status_newUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel25.add(status_newUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 190, 30));

        jPanel26.setBackground(new java.awt.Color(0, 153, 0));
        jPanel26.setForeground(new java.awt.Color(0, 153, 0));

        save_newUser.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        save_newUser.setForeground(new java.awt.Color(255, 255, 255));
        save_newUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        save_newUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-check-24.png"))); // NOI18N
        save_newUser.setText("Save");
        save_newUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                save_newUserMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(save_newUser, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(save_newUser, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel25.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 300, -1, 30));

        jPanel27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));

        jLabel53.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(102, 102, 102));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cancel-24.png"))); // NOI18N
        jLabel53.setText("Cancel");

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
        );

        jPanel25.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, 100, 30));

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jPanel25.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 40, 30));

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-email-24.png"))); // NOI18N
        jPanel25.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, -1, 30));

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-password-24.png"))); // NOI18N
        jPanel25.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 40, 50));

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-phone-contact-24.png"))); // NOI18N
        jPanel25.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 30, 30));

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jPanel25.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 206, 40, 30));

        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-male-24.png"))); // NOI18N
        jPanel25.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 30, 30));

        jPanel23.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 540, 350));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel23.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 66, 650, 410));

        jPanel21.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 650, 470));

        jPanel20.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 640, 470));

        tabbed.addTab("AU", jPanel20);

        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel19.setBackground(new java.awt.Color(0, 0, 204));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel100.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-50.png"))); // NOI18N
        jPanel19.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 80, 70));

        jLabel101.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel101.setForeground(new java.awt.Color(255, 255, 255));
        jLabel101.setText("Edit User");
        jPanel19.add(jLabel101, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 90, 70));

        jPanel14.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 540, 70));

        jPanel47.setBackground(new java.awt.Color(255, 255, 255));
        jPanel47.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel47.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel102.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel102.setText("Name:");
        jPanel47.add(jLabel102, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, 33));

        editUser_name.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel47.add(editUser_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 200, 33));

        jLabel104.setForeground(new java.awt.Color(204, 204, 204));
        jLabel104.setText("___________________________________________________________________");
        jPanel47.add(jLabel104, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 490, -1));

        jLabel105.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel105.setText("Email:");
        jPanel47.add(jLabel105, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, -1, 30));

        editUser_email.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel47.add(editUser_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 260, 30));

        jLabel107.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel107.setText("Contact: ");
        jPanel47.add(jLabel107, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, 40));

        editUser_contact.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel47.add(editUser_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 230, 30));

        jLabel109.setForeground(new java.awt.Color(204, 204, 204));
        jLabel109.setText("___________________________________________________________________");
        jPanel47.add(jLabel109, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 126, -1, 30));

        jLabel110.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel110.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jLabel110.setText("Role:");
        jPanel47.add(jLabel110, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, 90, 30));

        jLabel111.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-male-24.png"))); // NOI18N
        jLabel111.setText("Status");
        jPanel47.add(jLabel111, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 80, 30));

        editUser_role.setFont(new java.awt.Font("Trebuchet MS", 0, 16)); // NOI18N
        editUser_role.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "patient", "dentist", "staff", "admin" }));
        editUser_role.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel47.add(editUser_role, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 160, 260, 30));

        jPanel48.setBackground(new java.awt.Color(0, 153, 0));
        jPanel48.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveEditUser.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        saveEditUser.setForeground(new java.awt.Color(255, 255, 255));
        saveEditUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveEditUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-done-20.png"))); // NOI18N
        saveEditUser.setText("Save");
        saveEditUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveEditUserMouseClicked(evt);
            }
        });
        jPanel48.add(saveEditUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel47.add(jPanel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 300, 100, 30));

        jPanel50.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jPanel50.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel113.setForeground(new java.awt.Color(102, 102, 102));
        jLabel113.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel113.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cancel-24.png"))); // NOI18N
        jLabel113.setText("Cancel");
        jPanel50.add(jLabel113, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel47.add(jPanel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, 100, 30));

        editUser_status.setFont(new java.awt.Font("Trebuchet MS", 0, 16)); // NOI18N
        editUser_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "active", "inactive", "suspended" }));
        editUser_status.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel47.add(editUser_status, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 200, 260, 30));

        jPanel14.add(jPanel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, 540, 350));

        jLabel99.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel14.add(jLabel99, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 66, 660, 410));

        jPanel7.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, 650, 490));

        tabbed.addTab("EU", jPanel7);

        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel39.setBackground(new java.awt.Color(255, 255, 255));
        jPanel39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel39.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel68.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 51, 102));
        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-revenue-20.png"))); // NOI18N
        jLabel68.setText("Total Revenue Today");
        jPanel39.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 40));

        jPanel13.add(jPanel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 150, 70));

        jPanel40.setBackground(new java.awt.Color(255, 255, 255));
        jPanel40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel40.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel69.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(0, 51, 102));
        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel69.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-data-pending-20.png"))); // NOI18N
        jLabel69.setText("Pending Balance");
        jLabel69.setToolTipText("");
        jPanel40.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 40));

        jPanel13.add(jPanel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 150, 70));

        jPanel41.setBackground(new java.awt.Color(255, 255, 255));
        jPanel41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel41.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel70.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(0, 51, 102));
        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-medium-risk-20.png"))); // NOI18N
        jLabel70.setText("Overdue invoices");
        jPanel41.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 0, 150, 40));

        jPanel13.add(jPanel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, 150, 70));

        jPanel42.setBackground(new java.awt.Color(255, 255, 255));
        jPanel42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel42.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel71.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(0, 51, 102));
        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-20.png"))); // NOI18N
        jLabel71.setText("Monthly Revenue");
        jLabel71.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel42.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 150, -1));

        jPanel13.add(jPanel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, 150, 70));

        jLabel63.setForeground(new java.awt.Color(204, 204, 204));
        jLabel63.setText("_________________________________________________________________________________________");
        jPanel13.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 630, 20));

        jLabel64.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(0, 51, 102));
        jLabel64.setText("Billing Management");
        jPanel13.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, 30));

        jLabel65.setForeground(new java.awt.Color(204, 204, 204));
        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel65.setText("_________________________________________________________________________________________");
        jPanel13.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 126, -1, 30));

        jPanel43.setBackground(new java.awt.Color(0, 51, 255));
        jPanel43.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel67.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(255, 255, 255));
        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel67.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-plus-sign-20.png"))); // NOI18N
        jLabel67.setText("Add Invoice");
        jPanel43.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 30));

        jPanel13.add(jPanel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 140, 30));

        jPanel44.setBackground(new java.awt.Color(255, 255, 255));
        jPanel44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel44.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel52.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-20.png"))); // NOI18N
        jLabel52.setText("Edit Selected");
        jPanel44.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 130, 30));

        jPanel13.add(jPanel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 140, 30));

        jPanel45.setBackground(new java.awt.Color(255, 255, 255));
        jPanel45.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel45.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-trash-20.png"))); // NOI18N
        jLabel21.setText("Delete Selected");
        jPanel45.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 30));

        jPanel13.add(jPanel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 150, 140, 30));
        jPanel13.add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 210, 30));

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane11.setViewportView(jTable8);

        jPanel13.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 630, 230));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel13.add(jComboBox8, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 190, 110, 30));

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel13.add(jComboBox9, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 190, 110, 30));

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel13.add(jComboBox10, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 190, 110, 30));

        jPanel46.setBackground(new java.awt.Color(0, 51, 204));
        jPanel46.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel66.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(255, 255, 255));
        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel66.setText("Filter");
        jPanel46.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 30));

        jPanel13.add(jPanel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 190, 50, 30));

        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel13.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 650, 230));

        jPanel12.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("bill", jPanel12);

        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel29.setBackground(new java.awt.Color(255, 255, 255));
        jPanel29.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel29.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel54.setFont(new java.awt.Font("Trebuchet MS", 1, 25)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(0, 51, 102));
        jLabel54.setText("Appointments Overview");
        jPanel29.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, -1, 40));

        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-38.png"))); // NOI18N
        jPanel29.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 40, 40));

        jPanel30.setBackground(new java.awt.Color(0, 51, 255));
        jPanel30.setForeground(new java.awt.Color(0, 51, 255));
        jPanel30.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel56.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 255, 255));
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-plus-sign-20.png"))); // NOI18N
        jLabel56.setText("Add Appointment");
        jPanel30.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        jPanel29.add(jPanel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 160, 30));

        jPanel31.setBackground(new java.awt.Color(255, 255, 255));
        jPanel31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel31.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel57.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-20.png"))); // NOI18N
        jLabel57.setText("Edit Appointment");
        jPanel31.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 140, 30));

        jPanel29.add(jPanel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 90, 160, 30));

        jPanel32.setBackground(new java.awt.Color(255, 255, 255));
        jPanel32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel32.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel58.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-trash-20.png"))); // NOI18N
        jLabel58.setText("Cancel Appointment");
        jPanel32.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 160, 30));

        jPanel29.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 160, 30));

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane8.setViewportView(jTable5);

        jPanel29.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 620, 260));

        jLabel59.setForeground(new java.awt.Color(204, 204, 204));
        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setText("_______________________________________________________________________________________");
        jLabel59.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel29.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 126, 610, 20));

        app_search.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel29.add(app_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 220, 30));

        jLabel60.setForeground(new java.awt.Color(204, 204, 204));
        jLabel60.setText("_______________________________________________________________________________________");
        jLabel60.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel29.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 174, 610, -1));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel29.add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 150, 90, 30));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel29.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 150, 100, 30));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel29.add(jComboBox5, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 150, 100, 30));

        jPanel33.setBackground(new java.awt.Color(0, 51, 255));
        jPanel33.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel61.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(255, 255, 255));
        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel61.setText("Filter");
        jPanel33.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 30));

        jPanel29.add(jPanel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, 60, 30));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel29.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 640, 200));

        jPanel28.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 640, 470));

        tabbed.addTab("app", jPanel28);

        jPanel51.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel52.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel53.setBackground(new java.awt.Color(0, 0, 204));
        jPanel53.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel117.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel117.setForeground(new java.awt.Color(255, 255, 255));
        jLabel117.setText("Add New Staff");
        jPanel53.add(jLabel117, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, -1, 70));

        jLabel118.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50 (1).png"))); // NOI18N
        jPanel53.add(jLabel118, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 80, 70));

        jPanel52.add(jPanel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 540, 70));

        jPanel60.setBackground(new java.awt.Color(255, 255, 255));
        jPanel60.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel60.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel106.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel106.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jLabel106.setText("Name:");
        jPanel60.add(jLabel106, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, 30));

        addname_staff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel60.add(addname_staff, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 380, 30));

        jLabel108.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel108.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-email-24.png"))); // NOI18N
        jLabel108.setText("Email:");
        jPanel60.add(jLabel108, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 80, 30));

        addemail_staff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        addemail_staff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addemail_staffActionPerformed(evt);
            }
        });
        jPanel60.add(addemail_staff, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 380, 30));

        jLabel112.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-password-24.png"))); // NOI18N
        jLabel112.setText("Password:");
        jPanel60.add(jLabel112, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, 30));

        addpass_staff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        addpass_staff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addpass_staffActionPerformed(evt);
            }
        });
        jPanel60.add(addpass_staff, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 350, 30));

        jLabel114.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel114.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-phone-contact-24.png"))); // NOI18N
        jLabel114.setText("Phone Number");
        jPanel60.add(jLabel114, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, 30));

        addcontact_staff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel60.add(addcontact_staff, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 320, 30));

        jLabel115.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel115.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jLabel115.setText("Role");
        jPanel60.add(jLabel115, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, 30));

        role_staff.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        role_staff.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "dentist", "staff" }));
        role_staff.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 255), 2, true));
        jPanel60.add(role_staff, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, 200, 30));

        jLabel116.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel116.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-male-24.png"))); // NOI18N
        jLabel116.setText("Status");
        jPanel60.add(jLabel116, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, -1, 30));

        status_staff.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        status_staff.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "active", "inactive" }));
        status_staff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        status_staff.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel60.add(status_staff, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 250, 200, 30));

        jPanel64.setBackground(new java.awt.Color(255, 255, 255));
        jPanel64.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel64.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cancelAddingstaf.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        cancelAddingstaf.setForeground(new java.awt.Color(102, 102, 102));
        cancelAddingstaf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cancelAddingstaf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cancel-24.png"))); // NOI18N
        cancelAddingstaf.setText("Cancel");
        cancelAddingstaf.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelAddingstafMouseClicked(evt);
            }
        });
        jPanel64.add(cancelAddingstaf, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel60.add(jPanel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, 100, 30));

        jPanel63.setBackground(new java.awt.Color(0, 153, 0));
        jPanel63.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveNewstaff.setBackground(new java.awt.Color(255, 255, 255));
        saveNewstaff.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        saveNewstaff.setForeground(new java.awt.Color(255, 255, 255));
        saveNewstaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveNewstaff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-check-24.png"))); // NOI18N
        saveNewstaff.setText("Save");
        saveNewstaff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveNewstaffMouseClicked(evt);
            }
        });
        jPanel63.add(saveNewstaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel60.add(jPanel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 300, 100, 30));

        jPanel52.add(jPanel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 540, 350));

        jLabel103.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel52.add(jLabel103, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 650, 480));

        jPanel51.add(jPanel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("AS", jPanel51);

        jPanel65.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel66.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel69.setBackground(new java.awt.Color(0, 0, 204));
        jPanel69.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel127.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-50.png"))); // NOI18N
        jPanel69.add(jLabel127, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 60, 70));

        jLabel128.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel128.setForeground(new java.awt.Color(255, 255, 255));
        jLabel128.setText("Edit Staff");
        jPanel69.add(jLabel128, new org.netbeans.lib.awtextra.AbsoluteConstraints(96, 0, 90, 70));

        jPanel66.add(jPanel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 540, 70));

        jPanel70.setBackground(new java.awt.Color(255, 255, 255));
        jPanel70.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel70.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel129.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel129.setText("Name:");
        jPanel70.add(jLabel129, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, 40));

        jLabel130.setForeground(new java.awt.Color(204, 204, 204));
        jLabel130.setText("___________________________________________________________________");
        jPanel70.add(jLabel130, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, 30));

        jLabel131.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel131.setText("Email:");
        jPanel70.add(jLabel131, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 86, -1, 30));

        jLabel132.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel132.setText("Contact:");
        jPanel70.add(jLabel132, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 121, -1, 30));

        jLabel133.setForeground(new java.awt.Color(204, 204, 204));
        jLabel133.setText("___________________________________________________________________");
        jPanel70.add(jLabel133, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 136, -1, 30));

        jLabel134.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel134.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-male-24.png"))); // NOI18N
        jLabel134.setText("Status");
        jPanel70.add(jLabel134, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, 90, 30));

        jLabel135.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel135.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jLabel135.setText("Role");
        jPanel70.add(jLabel135, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, 80, 30));

        jPanel71.setBackground(new java.awt.Color(255, 255, 255));
        jPanel71.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel71.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        staff_role.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        staff_role.setText("jLabel139");
        jPanel71.add(staff_role, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 30));

        jPanel70.add(jPanel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 260, 30));

        staff_stat.setFont(new java.awt.Font("Trebuchet MS", 0, 16)); // NOI18N
        staff_stat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "active", "inactive", "suspended" }));
        staff_stat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel70.add(staff_stat, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 210, 260, 30));

        staff_name.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        staff_name.setText("jLabel136");
        jPanel70.add(staff_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 290, 40));

        staff_email.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        staff_email.setText("jLabel137");
        jPanel70.add(staff_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 310, 40));

        staff_contact.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        staff_contact.setText("jLabel138");
        jPanel70.add(staff_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 290, 30));

        jPanel77.setBackground(new java.awt.Color(0, 153, 0));
        jPanel77.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveEdit_staff.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        saveEdit_staff.setForeground(new java.awt.Color(255, 255, 255));
        saveEdit_staff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveEdit_staff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-check-24.png"))); // NOI18N
        saveEdit_staff.setText("Save");
        saveEdit_staff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveEdit_staffMouseClicked(evt);
            }
        });
        jPanel77.add(saveEdit_staff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel70.add(jPanel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 300, 100, 30));

        jPanel78.setBackground(new java.awt.Color(255, 255, 255));
        jPanel78.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel78.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cancelEditStaff.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        cancelEditStaff.setForeground(new java.awt.Color(102, 102, 102));
        cancelEditStaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cancelEditStaff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cancel-24.png"))); // NOI18N
        cancelEditStaff.setText("Cancel");
        cancelEditStaff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelEditStaffMouseClicked(evt);
            }
        });
        jPanel78.add(cancelEditStaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel70.add(jPanel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 300, 100, 30));

        jPanel66.add(jPanel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 540, 350));

        jLabel126.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel66.add(jLabel126, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 650, 480));

        jPanel65.add(jPanel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("ES", jPanel65);

        jPanel72.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel73.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel74.setBackground(new java.awt.Color(0, 0, 204));
        jPanel74.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel136.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-50.png"))); // NOI18N
        jPanel74.add(jLabel136, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 60, 70));

        jLabel137.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel137.setForeground(new java.awt.Color(255, 255, 255));
        jLabel137.setText("Edit Dentist");
        jPanel74.add(jLabel137, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 120, 70));

        jPanel73.add(jPanel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 540, 70));

        jPanel75.setBackground(new java.awt.Color(255, 255, 255));
        jPanel75.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel75.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel139.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel139.setText("Name:");
        jPanel75.add(jLabel139, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, 30));

        jLabel140.setForeground(new java.awt.Color(204, 204, 204));
        jLabel140.setText("___________________________________________________________________");
        jPanel75.add(jLabel140, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, -1, -1));

        jLabel141.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel141.setText("Email:");
        jPanel75.add(jLabel141, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 50, 30));

        jLabel142.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel142.setText("Contact:");
        jPanel75.add(jLabel142, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, -1, 40));

        jLabel143.setForeground(new java.awt.Color(204, 204, 204));
        jLabel143.setText("___________________________________________________________________");
        jPanel75.add(jLabel143, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 136, -1, 30));

        jLabel144.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel144.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jLabel144.setText("Role:");
        jPanel75.add(jLabel144, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, 100, 30));

        jLabel145.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel145.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-male-24.png"))); // NOI18N
        jLabel145.setText("Status");
        jPanel75.add(jLabel145, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 80, 30));

        jPanel76.setBackground(new java.awt.Color(255, 255, 255));
        jPanel76.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel76.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dentist_role.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        dentist_role.setText("jLabel146");
        jPanel76.add(dentist_role, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 30));

        jPanel75.add(jPanel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 210, 230, 30));

        dentist_stat.setFont(new java.awt.Font("Trebuchet MS", 0, 16)); // NOI18N
        dentist_stat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "active", "inactive", "suspended" }));
        dentist_stat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel75.add(dentist_stat, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 250, 230, 30));

        dentist_name.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        dentist_name.setText("jLabel146");
        jPanel75.add(dentist_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 240, 30));

        dentist_email.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        dentist_email.setText("jLabel147");
        jPanel75.add(dentist_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 250, 30));

        dentist_contact.setText("jLabel148");
        jPanel75.add(dentist_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 126, 210, 30));

        jPanel79.setBackground(new java.awt.Color(0, 153, 0));
        jPanel79.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveEdit_dentist.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        saveEdit_dentist.setForeground(new java.awt.Color(255, 255, 255));
        saveEdit_dentist.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveEdit_dentist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-check-24.png"))); // NOI18N
        saveEdit_dentist.setText("Save");
        saveEdit_dentist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveEdit_dentistMouseClicked(evt);
            }
        });
        jPanel79.add(saveEdit_dentist, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 90, 30));

        jPanel75.add(jPanel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 300, 100, 30));

        jPanel80.setBackground(new java.awt.Color(255, 255, 255));
        jPanel80.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel80.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cancelsave_dentist.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        cancelsave_dentist.setForeground(new java.awt.Color(102, 102, 102));
        cancelsave_dentist.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cancelsave_dentist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cancel-24.png"))); // NOI18N
        cancelsave_dentist.setText("Cancel");
        jPanel80.add(cancelsave_dentist, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel75.add(jPanel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 300, 100, 30));

        jLabel147.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel147.setText("Set Specialty:");
        jPanel75.add(jLabel147, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 176, -1, 20));

        setDentist_specialty.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        setDentist_specialty.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "General Dentistry", "Cosmetic Dentistry", "Oral Surgery", "Endodontics", "Prosthodontics", "Orthodontics" }));
        jPanel75.add(setDentist_specialty, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, 230, 30));

        jPanel73.add(jPanel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 540, 350));

        jLabel138.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel73.add(jLabel138, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        jPanel72.add(jPanel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("ED", jPanel72);

        jPanel81.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel82.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel146.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel82.add(jLabel146, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        jPanel81.add(jPanel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        tabbed.addTab("EA", jPanel81);

        bg.add(tabbed, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 640, 500));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 810, Short.MAX_VALUE)
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
        dashbtn.setForeground(new Color(0, 51, 204));  
        
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
        usersbtn.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_usersbtnMouseExited

    private void staffbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staffbtnMouseEntered
        staffpane.setBackground(Color. blue);
        staffbtn.setForeground(Color.white);
    }//GEN-LAST:event_staffbtnMouseEntered

    private void staffbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staffbtnMouseExited
        staffpane.setBackground(Color. white);
       staffbtn.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_staffbtnMouseExited

    private void analyticsbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_analyticsbtnMouseEntered
        analyticpane.setBackground(Color. blue);
        analyticsbtn.setForeground(Color. white); 
    }//GEN-LAST:event_analyticsbtnMouseEntered

    private void analyticsbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_analyticsbtnMouseExited
        analyticpane.setBackground(Color. white);
        analyticsbtn.setForeground(new Color(0, 51, 204)); 
    }//GEN-LAST:event_analyticsbtnMouseExited

    private void system_logsbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_system_logsbtnMouseEntered
        systempane.setBackground(Color. blue);
        system_logsbtn.setForeground(Color.white);
    }//GEN-LAST:event_system_logsbtnMouseEntered

    private void system_logsbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_system_logsbtnMouseExited
        systempane.setBackground(Color. white);
        system_logsbtn.setForeground(new Color(0, 51, 204)); 
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
        logoutpane.setBackground(Color. red);
        logoutbtn.setForeground(Color.white);
        
    }//GEN-LAST:event_logoutbtnMouseEntered

    private void logoutbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutbtnMouseExited
          logoutpane.setBackground(Color. white);
          logoutbtn.setForeground(new Color(0, 51, 204));
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
          p.setForeground(new Color(0, 51, 204));
    }//GEN-LAST:event_pMouseExited

    private void appbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseEntered
               // TODO add your handling code here:
        apppane.setBackground(Color. blue);
        appbtn.setForeground(Color.white);
    }//GEN-LAST:event_appbtnMouseEntered

    private void appbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseExited
         apppane.setBackground(Color. white);
        appbtn.setForeground(new Color(0, 51, 204));  
        
    }//GEN-LAST:event_appbtnMouseExited

    private void billbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billbtnMouseExited
         billpane.setBackground(Color. white);
         billbtn.setForeground(new Color(0, 51, 204));
        
    }//GEN-LAST:event_billbtnMouseExited

    private void billbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billbtnMouseEntered
        billpane.setBackground(Color. blue);
        billbtn.setForeground(Color.white);
    }//GEN-LAST:event_billbtnMouseEntered

    private void billbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_billbtnMouseClicked
       tabbed.setSelectedIndex(9);
    }//GEN-LAST:event_billbtnMouseClicked

    private void appbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseClicked
      tabbed.setSelectedIndex(10);
    }//GEN-LAST:event_appbtnMouseClicked

    private void saveEditUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveEditUserMouseClicked
      int accId = selectedAccId;
    String role = editUser_role.getSelectedItem().toString();
    String status = editUser_status.getSelectedItem().toString();

    String roleDb = mapRoleToDb(role);
    int statusDb = mapStatusToInt(status);

    try (Connection conn = config.connectDB()) {
        conn.setAutoCommit(false);

        try {
            // Update account
            String sql = "UPDATE tbl_accounts SET acc_role=?, acc_status=? WHERE acc_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleDb);
                ps.setInt(2, statusDb);
                ps.setInt(3, accId);
                ps.executeUpdate();
            }

            // Dentist logic
            if ("dentist".equalsIgnoreCase(roleDb)) {
                String checkSql = "SELECT 1 FROM tbl_dentists WHERE acc_id=?";
                boolean exists;
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setInt(1, accId);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        exists = rs.next();
                    }
                }

                if (!exists) {
                    String insertSql = "INSERT INTO tbl_dentists (acc_id, specialty, work_start, work_end) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, accId);
                        insertPs.setString(2, "");
                        insertPs.setString(3, "");
                        insertPs.setString(4, "");
                        insertPs.executeUpdate();
                    }
                }
            }

            // Audit log
            String logSql = "INSERT INTO tbl_logs (actor_id, actor_role, action, details, created_at) " +
                            "VALUES (?, ?, ?, ?, datetime('now'))";
            try (PreparedStatement logPst = conn.prepareStatement(logSql)) {
                logPst.setInt(1, accId);
                logPst.setString(2, roleDb);
                logPst.setString(3, "Update User");
                logPst.setString(4, "Role set to " + roleDb + ", status set to " + statusDb);
                logPst.executeUpdate();
            }

            conn.commit();

            JOptionPane.showMessageDialog(this, "User updated successfully.");
            acctable();
            loadSystemLogs();
            tabbed.setSelectedIndex(1);

        } catch (Exception inner) {
            conn.rollback();
            throw inner;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_saveEditUserMouseClicked

    private void save_newUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_save_newUserMouseClicked
        saveNewUser();
    }//GEN-LAST:event_save_newUserMouseClicked

    private void role_newUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_role_newUserMouseClicked
        // Example: force the combo box to only show valid roles
        role_newUser.setModel(new DefaultComboBoxModel<>(
            new String[] {"patient", "dentist", "staff", "admin"}
        ));

        // Optional: show a tooltip or message when clicked
        role_newUser.setToolTipText("Choose one of: patient, dentist, staff, admin");
    }//GEN-LAST:event_role_newUserMouseClicked

    private void editprofile2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editprofile2MouseClicked
        saveProfileChanges();
    }//GEN-LAST:event_editprofile2MouseClicked

    private void jPanel61MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel61MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel61MouseClicked

    private void change_photo1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_change_photo1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_change_photo1MouseClicked

    private void profilePic1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profilePic1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_profilePic1MouseClicked

    private void editprofileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editprofileMouseClicked
        tabbed.setSelectedIndex(6);
    }//GEN-LAST:event_editprofileMouseClicked

    private void change_photoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_change_photoMouseClicked
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Picture");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            selectedPhotoPath = file.getAbsolutePath();   // <-- store path

            try {
                // Load and scale image
                BufferedImage image = ImageIO.read(file);
                Image scaled = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);

                // Create circular mask
                BufferedImage circleBuffer = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = circleBuffer.createGraphics();
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 150, 150));
                g2.drawImage(scaled, 0, 0, null);
                g2.dispose();

                // Show preview in profile panel
                profilePic.setIcon(new ImageIcon(circleBuffer));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(),
                    "Image Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_change_photoMouseClicked

    private void savechanges_profileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_savechanges_profileMouseClicked
        try (Connection con = config.connectDB();
            PreparedStatement pst = con.prepareStatement(
                "UPDATE tbl_accounts SET acc_pic=? WHERE acc_id=?")) {

            int userId = session.getId();
            pst.setString(1, selectedPhotoPath);
            pst.setInt(2, userId);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Profile photo updated successfully!");

                // Refresh circular images everywhere
                if (selectedPhotoPath != null && !selectedPhotoPath.isEmpty()) {
                    File imgFile = new File(selectedPhotoPath);
                    if (imgFile.exists()) {
                        setCircularProfilePic(imgFile);   // profile panel
                        setCircularAdminPic(imgFile);     // header dashboard
                    } else {
                        profilePic.setIcon(new ImageIcon(getClass().getResource("/img/default-user.png")));
                        circle_adminPic.setIcon(new ImageIcon(getClass().getResource("/img/default-user.png")));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating photo: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_savechanges_profileMouseClicked

    private void search_systemlogsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_systemlogsMouseExited
        // Back to original color
        search_systemlogs.setForeground(Color.WHITE);
        search_systemlogs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_search_systemlogsMouseExited

    private void search_systemlogsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_search_systemlogsMouseEntered
        // Highlight on hover with dentalcare accent
        search_systemlogs.setForeground(new Color(102, 204, 255)); // light aqua/sky blue
        search_systemlogs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_search_systemlogsMouseEntered

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

    private void filter_searchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filter_searchMouseClicked

    }//GEN-LAST:event_filter_searchMouseClicked

    private void staff_dentistSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_staff_dentistSearchKeyTyped

    }//GEN-LAST:event_staff_dentistSearchKeyTyped

    private void staff_dentistSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_staff_dentistSearchKeyReleased
        String keyword = staff_dentistSearch.getText().trim();

        if (keyword.isEmpty()) {
            return; // don’t search if empty
        }

        String sql = "SELECT acc_id AS id, acc_name AS name, acc_email AS email, " +
        "acc_contact AS contact, acc_role AS role, " +
        "CASE acc_status " +
        "     WHEN 1 THEN 'Active' " +
        "     WHEN 0 THEN 'Inactive' " +
        "     WHEN 2 THEN 'Suspended' " +
        "END AS status " +
        "FROM tbl_accounts " +
        "WHERE (LOWER(acc_role) = 'dentist' OR LOWER(acc_role) = 'staff') " +
        "AND (acc_name LIKE ? OR acc_id LIKE ? OR acc_email LIKE ? OR acc_contact LIKE ?)";

        try (Connection con = config.connectDB();
            PreparedStatement pst = con.prepareStatement(sql)) {

            String searchTerm = "%" + keyword + "%";
            pst.setString(1, searchTerm);
            pst.setString(2, searchTerm);
            pst.setString(3, searchTerm);
            pst.setString(4, searchTerm);

            try (ResultSet rs = pst.executeQuery()) {
                staff_dentist_table.setModel(DbUtils.resultSetToTableModel(rs));
                staff_dentist_table.setDefaultEditor(Object.class, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "❌ Error searching Dentist/Staff: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_staff_dentistSearchKeyReleased

    private void addstaffMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addstaffMouseExited
        addstaff.setForeground(Color.WHITE);   // text becomes white
        addstaff.setBackground(new Color(0,51,255)); // dental blue background
        addstaff.setOpaque(true);
        addstaff.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
    }//GEN-LAST:event_addstaffMouseExited

    private void addstaffMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addstaffMouseEntered
        addstaff.setForeground(Color.WHITE);
        addstaff.setBackground(new Color(0x007ACC)); // dental blue
        addstaff.setOpaque(true);
        addstaff.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
        add.setToolTipText("Add User");
    }//GEN-LAST:event_addstaffMouseEntered

    private void addstaffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addstaffMouseClicked
    tabbed.setSelectedIndex(11);
    }//GEN-LAST:event_addstaffMouseClicked

    private void filterAllUsersMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterAllUsersMouseExited
        // Back to original color
        filterAllUsers.setForeground(Color.WHITE);
        filterAllUsers.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_filterAllUsersMouseExited

    private void filterAllUsersMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterAllUsersMouseEntered
        // Highlight on hover with dentalcare accent
        filterAllUsers.setForeground(new Color(102, 204, 255)); // light aqua/sky blue
        filterAllUsers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_filterAllUsersMouseEntered

    private void filterAllUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterAllUsersMouseClicked
        filterAllUsers.addMouseListener(new java.awt.event.MouseAdapter() {
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
    }//GEN-LAST:event_filterAllUsersMouseClicked

    private void searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyTyped

    }//GEN-LAST:event_searchKeyTyped

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
    String search1 = search.getText().trim();
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

            // Apply renderer to status column for color coding
            tbl.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        }

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Search error: " + e.getMessage());
    }
    }//GEN-LAST:event_searchKeyReleased

    private void deleteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteMouseExited
        delete.setForeground(Color.BLACK);   // text back to black
        delete.setBackground(new Color(255,255,255)); // original white background
        delete.setOpaque(true);
        delete.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
    }//GEN-LAST:event_deleteMouseExited

    private void deleteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteMouseEntered
        delete.setToolTipText("Delete user");
        delete.setForeground(Color.RED);
        delete.setBackground(new Color(255,255,255)); // dental blue
        delete.setOpaque(true);
        delete.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
    }//GEN-LAST:event_deleteMouseEntered

    private void deleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteMouseClicked

    }//GEN-LAST:event_deleteMouseClicked

    private void editMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editMouseExited
        edit.setForeground(Color.BLACK);   // text back to black
        edit.setBackground(new Color(255,255,255)); // original white background
        edit.setOpaque(true);
        edit.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
    }//GEN-LAST:event_editMouseExited

    private void editMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editMouseEntered
        edit.setForeground(Color.WHITE);
        edit.setBackground(new Color(0x007ACC)); // dental blue
        edit.setOpaque(true);
        edit.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
        edit.setToolTipText("Edit user details");
    }//GEN-LAST:event_editMouseEntered

    private void editMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editMouseClicked
        int selectedRow = tbl.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit.",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ✅ Store the account ID safely
        try {
            selectedAccId = Integer.parseInt(tbl.getValueAt(selectedRow, 0).toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid account ID selected.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Populate non-editable fields (display only)
        editUser_name.setText(String.valueOf(tbl.getValueAt(selectedRow, 1)));
        editUser_email.setText(String.valueOf(tbl.getValueAt(selectedRow, 2)));
        editUser_contact.setText(String.valueOf(tbl.getValueAt(selectedRow, 3)));

        // ✅ Populate editable fields (role + status)
        Object roleValue = tbl.getValueAt(selectedRow, 4);
        Object statusValue = tbl.getValueAt(selectedRow, 5);

        if (roleValue != null) {
            editUser_role.setSelectedItem(roleValue.toString());
        }
        if (statusValue != null) {
            editUser_status.setSelectedItem(statusValue.toString());
        }

        // ✅ Switch to Edit User tab (index 8)
        tabbed.setSelectedIndex(8);
    }//GEN-LAST:event_editMouseClicked

    private void addMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseExited
        add.setForeground(Color.WHITE);   // text becomes white
        add.setBackground(new Color(0,51,255)); // dental blue background
        add.setOpaque(true);
        add.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
    }//GEN-LAST:event_addMouseExited

    private void addMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseEntered
        add.setForeground(Color.WHITE);
        add.setBackground(new Color(0x007ACC)); // dental blue
        add.setOpaque(true);
        add.setBorder(BorderFactory.createLineBorder(new Color(153,153,153), 1));
        add.setToolTipText("Add User");
    }//GEN-LAST:event_addMouseEntered

    private void addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseClicked
        tabbed.setSelectedIndex(7);
    }//GEN-LAST:event_addMouseClicked

    private void addpass_staffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addpass_staffActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addpass_staffActionPerformed

    private void addemail_staffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addemail_staffActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addemail_staffActionPerformed

    private void saveNewstaffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveNewstaffMouseClicked
  // Collect input values
    String name = addname_staff.getText().trim();
    String email = addemail_staff.getText().trim();
    String password = addpass_staff.getText().trim();
    String contact = addcontact_staff.getText().trim();
    String role = role_staff.getSelectedItem().toString();
    String status = status_staff.getSelectedItem().toString();

    // ✅ Validation
    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (!isValidEmail(email)) {
        JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ✅ Map role to DB values
    String dbRole;
    switch (role.toLowerCase()) {
        case "admin": dbRole = "admin"; break;
        case "dentist": dbRole = "dentist"; break;
        case "staff": dbRole = "staff"; break;
        default: dbRole = "staff";
    }

    // ✅ Map status to DB values
    int dbStatus;
    switch (status.toLowerCase()) {
        case "active": dbStatus = 1; break;
        case "inactive": dbStatus = 0; break;
        default: dbStatus = 0;
    }

    // ✅ Hash the password before saving
    String hashedPass = config.hashPassword(password);

    // ✅ Insert into DB
    String sql = "INSERT INTO tbl_accounts (acc_name, acc_email, acc_pass, acc_contact, acc_role, acc_status) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        pst.setString(1, name);
        pst.setString(2, email);
        pst.setString(3, hashedPass);   // store hashed password
        pst.setString(4, contact);
        pst.setString(5, dbRole);
        pst.setInt(6, dbStatus);

        int rows = pst.executeUpdate();
        if (rows > 0) {
            // ✅ Get generated acc_id
            int newAccId = -1;
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    newAccId = rs.getInt(1);
                }
            }

            // ✅ Insert into tbl_dentists if role is dentist
            if ("dentist".equalsIgnoreCase(dbRole)) {
                String dentistSql = "INSERT INTO tbl_dentists (acc_id, specialty, work_start, work_end, work_days) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement dentistPst = con.prepareStatement(dentistSql)) {
                    dentistPst.setInt(1, newAccId);
                    dentistPst.setString(2, "");   // placeholder until schedule editor fills it
                    dentistPst.setString(3, "");   // placeholder work_start
                    dentistPst.setString(4, "");   // placeholder work_end
                    dentistPst.setString(5, "");   // placeholder work_days
                    dentistPst.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "New staff user added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            staffDentistTable(); // refresh table view

            // Clear fields after save
            addname_staff.setText("");
            addemail_staff.setText("");
            addpass_staff.setText("");
            addcontact_staff.setText("");
            role_staff.setSelectedIndex(0);
            status_staff.setSelectedIndex(0);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_saveNewstaffMouseClicked

    private void cancelAddingstafMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelAddingstafMouseClicked
       // ✅ Clear text fields
    addname_staff.setText("");
    addemail_staff.setText("");
    addpass_staff.setText("");
    addcontact_staff.setText("");

    // ✅ Reset dropdowns to first option
    role_staff.setSelectedIndex(0);
    status_staff.setSelectedIndex(0);

    // ✅ Optional: close or hide the Add Staff panel if it’s a dialog
    // If this is a JDialog:
    // this.dispose();

    // If you want to just hide the panel:
    // addStaffPanel.setVisible(false);

    JOptionPane.showMessageDialog(this, "Adding staff cancelled.", 
                                  "Cancelled", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_cancelAddingstafMouseClicked

    private void editstaffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editstaffMouseClicked
     int selectedRow = staff_dentist_table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a staff or dentist account first.");
        return;
    }

    // Extract values from table
    String id = staff_dentist_table.getValueAt(selectedRow, 0).toString();
    String name = staff_dentist_table.getValueAt(selectedRow, 1).toString();
    String email = staff_dentist_table.getValueAt(selectedRow, 2).toString();
    String contact = staff_dentist_table.getValueAt(selectedRow, 3).toString();
    String role = staff_dentist_table.getValueAt(selectedRow, 4).toString();
    String status = staff_dentist_table.getValueAt(selectedRow, 5).toString();

    // ✅ Display values in labels
    staff_name.setText(name);
    staff_email.setText(email);
    staff_contact.setText(contact);
    staff_role.setText(role);

    // ✅ Status remains editable
    staff_stat.setSelectedItem(status.toLowerCase());

    // ✅ Store selected account ID for saving later
    selectedAccId = Integer.parseInt(id);

    // ✅ Switch to correct tab
    switch (role.toLowerCase()) {
        case "staff":   tabbed.setSelectedIndex(12); break;
        case "dentist": tabbed.setSelectedIndex(13); break;
        case "admin":   tabbed.setSelectedIndex(14); break;
    }
    }//GEN-LAST:event_editstaffMouseClicked

    private void saveEdit_staffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveEdit_staffMouseClicked
       if (selectedAccId <= 0) {
        JOptionPane.showMessageDialog(this, "No staff/dentist selected.");
        return;
    }

    // ✅ Use staff_stat combo box
    String newStatus = staff_stat.getSelectedItem().toString().trim();
    editStaffStatus(selectedAccId, newStatus);
    }//GEN-LAST:event_saveEdit_staffMouseClicked

    private void cancelEditStaffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelEditStaffMouseClicked
     // ✅ Validation: only allow cancel if a staff/dentist was selected
    if (selectedAccId <= 0) {
        JOptionPane.showMessageDialog(this, "No staff/dentist is currently being edited.");
        return;
    }

    // ✅ Switch back to Workforce Management tab (assuming index 11)
    tabbed.setSelectedIndex(2);

    // ✅ Reset only the editable field (status combo box)
    staff_stat.setSelectedIndex(-1);

    // ✅ Reset ID so no stale selection remains
    selectedAccId = -1;

    JOptionPane.showMessageDialog(this, "Edit cancelled. Returning to Workforce Management.");
    }//GEN-LAST:event_cancelEditStaffMouseClicked

    private void saveEdit_dentistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveEdit_dentistMouseClicked
      if (selectedAccId <= 0) {
        JOptionPane.showMessageDialog(this, "No dentist selected.");
        return;
    }

    String newStatus = dentist_stat.getSelectedItem().toString().trim();
    String newSpecialty = setDentist_specialty.getSelectedItem().toString().trim();

    editDentistStatusAndSpecialty(selectedAccId, newStatus, newSpecialty);;
    }//GEN-LAST:event_saveEdit_dentistMouseClicked

    
    private void acctable() {
    String sql =
        "SELECT acc_id AS Id, " +
        "acc_name AS name, " +
        "acc_email AS Email, " +
        "acc_contact AS Contact, " +
        "acc_role AS Role, " +
        "CASE acc_status " +
        "     WHEN 1 THEN 'Active' " +
        "     WHEN 0 THEN 'Inactive' " +
        "     WHEN 2 THEN 'Suspended' " +
        "END AS status " +
        "FROM tbl_accounts";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        // Load DB results into table
        tbl.setModel(DbUtils.resultSetToTableModel(rs));

// Professional dental health styling
tbl.setRowHeight(28);
tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
tbl.setGridColor(new Color(220, 220, 220));
tbl.setShowGrid(true);

// Header styling
JTableHeader header = tbl.getTableHeader();
header.setFont(new Font("Segoe UI", Font.BOLD, 14));
header.setBackground(new Color(200, 230, 240)); // soft healthcare blue
header.setForeground(Color.DARK_GRAY);
        // Alternate row colors
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                } else {
                    c.setBackground(new Color(184, 207, 229)); // selection color
                }
                return c;
            }
        });

        // Apply renderer to the status column (index 5)
        tbl.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        // ✅ Make only Status column editable
        DefaultTableModel model = (DefaultTableModel) tbl.getModel();
        Vector<?> dataVector = model.getDataVector();
        Vector<String> columnNames = new Vector<>(Arrays.asList(
            "ID", "Name", "Email", "Contact", "Role", "Status"));

        DefaultTableModel editableModel = new DefaultTableModel(dataVector, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Status column editable
            }
        };
        tbl.setModel(editableModel);

        // ✅ Dropdown editor for Status column
        String[] statuses = {"Active", "Inactive", "Suspended"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        tbl.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(statusCombo));

        // ✅ Listen for changes and update DB
        editableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 5) { // Status column
                String newStatusText = editableModel.getValueAt(row, column).toString();
                int newStatusValue = mapStatusToInt(newStatusText);
                int userId = Integer.parseInt(editableModel.getValueAt(row, 0).toString());

                try (Connection con2 = config.connectDB();
                     PreparedStatement pst2 = con2.prepareStatement(
                         "UPDATE tbl_accounts SET acc_status = ? WHERE acc_id = ?")) {
                    pst2.setInt(1, newStatusValue);
                    pst2.setInt(2, userId);
                    pst2.executeUpdate();
                    JOptionPane.showMessageDialog(this,
                        "Status updated successfully for user ID " + userId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Error updating status: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error loading accounts: " + e.getMessage());
    }
}

private void refreshProfile() {
    String sql = "SELECT acc_id, acc_name, acc_email, acc_contact, acc_role, acc_pic "
               + "FROM tbl_accounts WHERE acc_id = ?";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, session.getId());

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // Update profile section
                name.setText(rs.getString("acc_name"));
                role.setText(rs.getString("acc_role"));
                email.setText(rs.getString("acc_email"));
                contact.setText(rs.getString("acc_contact"));

                // Update header
                admin_name.setText(rs.getString("acc_name"));

                // Handle profile picture safely
                String imgPath = rs.getString("acc_pic");

                if (imgPath != null && !imgPath.trim().isEmpty()) {
                    File imgFile = new File(imgPath);
                    if (imgFile.exists()) {
                        setCircularProfilePic(imgFile);
                        setCircularAdminPic(imgFile);
                    } else {
                        // fallback if file path is invalid
                        profilePic.setIcon(new ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png")));
                        circle_adminPic.setIcon(new ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png")));
                    }
                } else {
                    // fallback if acc_pic is NULL or empty
                    profilePic.setIcon(new ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png")));
                    circle_adminPic.setIcon(new ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png")));
                }

                profilePic.revalidate();
                profilePic.repaint();
                circle_adminPic.revalidate();
                circle_adminPic.repaint();
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}





// Choose and save photo, update DB, and refresh both profile + header
private void chooseAndSavePhoto() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
        "Image files", "jpg", "png", "jpeg", "gif"));
    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
        try {
            File selectedFile = chooser.getSelectedFile();
            File folder = new File("profile_pics");
            if (!folder.exists()) folder.mkdir();

            String newFileName = "user_" + session.getId() + ".jpg";
            File destination = new File(folder, newFileName);
            java.nio.file.Files.copy(selectedFile.toPath(), destination.toPath(),
                                     java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String newPath = destination.getAbsolutePath();

            // Save path in DB
            try (Connection con = config.connectDB();
                 PreparedStatement pst = con.prepareStatement(
                     "UPDATE tbl_accounts SET acc_pic = ? WHERE acc_id = ?")) {
                pst.setString(1, newPath);
                pst.setInt(2, session.getId());
                pst.executeUpdate();
            }

            // ✅ Refresh everything after saving
            refreshProfile();

            JOptionPane.showMessageDialog(this, "Profile picture updated!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                                          "Image Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}



// --- Global Logging Method ---
// Use this to record any action into tbl_logs
// --- Global Logging Method ---
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
        System.out.println("⚠️ Could not log action to tbl_logs: " + e.getMessage());
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
  
private void saveNewUser() {
    String name = name_newUser.getText().trim();
    String email = email_newUser.getText().trim().toLowerCase();
    String password = password_newUser.getText().trim();
    String contact = contact_newUser.getText().trim();
    String roleValue = role_newUser.getSelectedItem().toString();
    String statusText = status_newUser.getSelectedItem().toString();
    int statusValue = mapStatusToInt(statusText);

    // Required fields
    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Name, Email, and Password are required.",
            "Validation",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Email format
    if (!isValidEmail(email)) {
        JOptionPane.showMessageDialog(this,
            "Invalid email format.",
            "Validation",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    try (Connection con = config.connectDB()) {
        // Duplicate check
        String checkSql = "SELECT COUNT(*) FROM tbl_accounts WHERE TRIM(LOWER(acc_email)) = ?";
        try (PreparedStatement checkPst = con.prepareStatement(checkSql)) {
            checkPst.setString(1, email);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Email already exists. Please use a different one.",
                        "Validation",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        // Insert new user with default photo path
        String insertSql = "INSERT INTO tbl_accounts (acc_name, acc_email, acc_contact, acc_role, acc_status, acc_pass, acc_pic) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, contact);
            pst.setString(4, roleValue);
            pst.setInt(5, statusValue);

            // Hash password before saving
            String hashedPassword = config.hashPassword(password);
            pst.setString(6, hashedPassword);

            // Default photo path
            pst.setString(7, "/img/default-user.png");

            int inserted = pst.executeUpdate();
            if (inserted > 0) {
                // Get generated acc_id
                ResultSet rsAcc = pst.getGeneratedKeys();
                int accId = -1;
                if (rsAcc.next()) {
                    accId = rsAcc.getInt(1);
                }

                // If dentist, insert into tbl_dentists automatically
                if ("Dentist".equalsIgnoreCase(roleValue)) {
                    String sqlDent = "INSERT INTO tbl_dentists (acc_id, specialty, work_start, work_end, work_days) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstDent = con.prepareStatement(sqlDent)) {
                        pstDent.setInt(1, accId);
                        pstDent.setString(2, "General Dentistry"); // or from a field
                        pstDent.setString(3, "08:00");
                        pstDent.setString(4, "17:00");
                        pstDent.setString(5, "Monday,Tuesday,Wednesday,Thursday,Friday");
                        pstDent.executeUpdate();
                    }
                }

                JOptionPane.showMessageDialog(this, "New user created successfully!");
                acctable(); // refresh accounts table
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving new user: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


  
  private void staffDentistTable() {
    String sql =
        "SELECT acc_id AS Id, " +
        "acc_name AS Name, " +
        "acc_email AS Email, " +
        "acc_contact AS Contact, " +
        "acc_role AS Role, " +
        "CASE acc_status " +
        "     WHEN 1 THEN 'Active' " +
        "     WHEN 0 THEN 'Inactive' " +
        "     WHEN 2 THEN 'Suspended' " +
        "END AS status " +
        "FROM tbl_accounts " +
        "WHERE acc_role IN ('dentist', 'staff')"; // ✅ filter only dentist & staff

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        staff_dentist_table.setModel(DbUtils.resultSetToTableModel(rs));

// Professional dental health styling
staff_dentist_table.setRowHeight(28);
staff_dentist_table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
staff_dentist_table.setGridColor(new Color(220, 220, 220));
staff_dentist_table.setShowGrid(true);

// Header styling
JTableHeader header = staff_dentist_table.getTableHeader();
header.setFont(new Font("Segoe UI", Font.BOLD, 14));
header.setBackground(new Color(200, 230, 240)); // soft healthcare blue
header.setForeground(Color.DARK_GRAY);
        // Alternate row colors
        staff_dentist_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                } else {
                    c.setBackground(new Color(184, 207, 229)); // soft green highlight
                }
                return c;
            }
        });

        // Apply status renderer
        staff_dentist_table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        // ✅ Dropdown editor for Status column
        String[] statuses = {"Active", "Inactive", "Suspended"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        staff_dentist_table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(statusCombo));

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error loading staff/dentist accounts: " + e.getMessage());
    }
    // Configure selection behavior
staff_dentist_table.setCellSelectionEnabled(false);
staff_dentist_table.setRowSelectionAllowed(true);
staff_dentist_table.setColumnSelectionAllowed(false);
staff_dentist_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

// Require double-click to start editing
DefaultCellEditor statusEditor = new DefaultCellEditor(
    new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"})
);
statusEditor.setClickCountToStart(2); // ✅ double-click only
staff_dentist_table.getColumnModel().getColumn(5).setCellEditor(statusEditor);
staff_dentist_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

TableColumnModel columnModel = staff_dentist_table.getColumnModel();
columnModel.getColumn(0).setPreferredWidth(50);   // ID
columnModel.getColumn(1).setPreferredWidth(150);  // Name
columnModel.getColumn(2).setPreferredWidth(250);  // Email
columnModel.getColumn(3).setPreferredWidth(150);  // Contact
columnModel.getColumn(4).setPreferredWidth(100);  // Role
columnModel.getColumn(5).setPreferredWidth(100);  // Status


// Stop editing automatically when clicking elsewhere
staff_dentist_table.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (staff_dentist_table.isEditing()) {
            staff_dentist_table.getCellEditor().stopCellEditing();
        }
    }
});

}
private void deleteUser() {
    int selectedRow = tbl.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
            "Please select a user to delete.",
            "Validation",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    String userId = tbl.getValueAt(selectedRow, 0).toString(); // acc_id column

    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to permanently delete this user?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection con = config.connectDB();
             PreparedStatement pst = con.prepareStatement(
                 "DELETE FROM tbl_accounts WHERE acc_id = ?")) {

            pst.setString(1, userId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "User deleted successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            performSearch(""); // refresh table

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error deleting user: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
private void saveProfileChanges() {
    String newName = edit_fullname.getText().trim();
    String newEmail = edit_email.getText().trim();
    String newContact = edit_contact.getText().trim();
    String newPassword = edit_newpass.getText().trim();
    String confirmPassword = edit_confirmpass.getText().trim();

    // ✅ Password confirmation check
    if (!newPassword.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this,
            "Passwords do not match.",
            "Validation",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ✅ Hash the password using config.hashPassword
    String hashedPassword = config.hashPassword(newPassword);

    try (Connection con = config.connectDB()) {
        // 🔍 Check if email is already used by another account
        String checkSql = "SELECT acc_id FROM tbl_accounts WHERE acc_email=? AND acc_id<>?";
        PreparedStatement checkPst = con.prepareStatement(checkSql);
        checkPst.setString(1, newEmail);
        checkPst.setInt(2, session.getId());
        ResultSet rs = checkPst.executeQuery();
        if (rs.next()) {
            JOptionPane.showMessageDialog(this,
                "Email already in use by another account.",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            return; // stop here
        }

        // ✅ Proceed with update if email is unique
        String sql = "UPDATE tbl_accounts SET acc_name=?, acc_email=?, acc_contact=?, acc_pass=? WHERE acc_id=?";
        PreparedStatement pst = con.prepareStatement(sql);

        pst.setString(1, newName);
        pst.setString(2, newEmail);
        pst.setString(3, newContact);
        pst.setString(4, hashedPassword); // store hash
        pst.setInt(5, session.getId());   // use session ID

        int updated = pst.executeUpdate();
        if (updated > 0) {
            JOptionPane.showMessageDialog(this,
                "Profile updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh session values
            session.setName(newName);
            session.setEmail(newEmail);
            session.setContact(newContact);

            refreshProfile(); // refresh UI
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error updating profile: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
private void applyStaffDentistFilter() {
    String selectedRole = role_staff_dentist.getSelectedItem().toString()
                                           .replaceAll("[^a-zA-Z]", "");
    String selectedStatus = status_staff_dentist.getSelectedItem().toString()
                                               .replaceAll("[^a-zA-Z]", "");

    String sql;

    if (selectedRole.equalsIgnoreCase("All")) {
        sql = "SELECT acc_id AS Id, acc_name AS Name, acc_email AS Email, acc_contact AS Contact, acc_role AS Role, " +
              "CASE acc_status WHEN 1 THEN 'Active' WHEN 0 THEN 'Inactive' WHEN 2 THEN 'Suspended' END AS status " +
              "FROM tbl_accounts WHERE acc_status = ?";
    } else {
        sql = "SELECT acc_id AS id, acc_name AS name, acc_email AS email, acc_contact AS contact, acc_role AS role, " +
              "CASE acc_status WHEN 1 THEN 'Active' WHEN 0 THEN 'Inactive' WHEN 2 THEN 'Suspended' END AS status " +
              "FROM tbl_accounts WHERE LOWER(acc_role) = LOWER(?) AND acc_status = ?";
    }

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        if (selectedRole.equalsIgnoreCase("All")) {
            pst.setInt(1, mapStatusToInt(selectedStatus));
        } else {
            pst.setString(1, selectedRole);
            pst.setInt(2, mapStatusToInt(selectedStatus));
        }

        try (ResultSet rs = pst.executeQuery()) {
            staff_dentist_table.setModel(DbUtils.resultSetToTableModel(rs));
            staff_dentist_table.setDefaultEditor(Object.class, null);
            staff_dentist_table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
            staff_dentist_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

TableColumnModel columnModel = staff_dentist_table.getColumnModel();
columnModel.getColumn(0).setPreferredWidth(50);   // ID
columnModel.getColumn(1).setPreferredWidth(150);  // Name
columnModel.getColumn(2).setPreferredWidth(250);  // Email
columnModel.getColumn(3).setPreferredWidth(150);  // Contact
columnModel.getColumn(4).setPreferredWidth(100);  // Role
columnModel.getColumn(5).setPreferredWidth(100);  // Status

        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private void filterUsers() {
    String selectedRole = allUsers.getSelectedItem().toString();
    String selectedStatus = statusAllUsers.getSelectedItem().toString().replaceAll("[^a-zA-Z]", "");
    String keyword = search.getText().trim();

    String sql = "SELECT acc_id AS Id, acc_name AS Name, acc_email AS Email, acc_contact AS Contact, acc_role AS Role, " +
                 "CASE acc_status WHEN 1 THEN 'Active' WHEN 0 THEN 'Inactive' WHEN 2 THEN 'Suspended' END AS status " +
                 "FROM tbl_accounts WHERE 1=1"; // ✅ start with no filter

    // Role filter (skip if All)
    if (!selectedRole.equals("🌐 All")) {
        sql += " AND LOWER(acc_role) = LOWER(?)";
    }

    // Status filter (skip if All)
    if (!selectedStatus.equalsIgnoreCase("All")) {
        sql += " AND acc_status = ?";
    }

    // Keyword filter
    if (!keyword.isEmpty() && !keyword.equals("🔎 Search accounts by name, ID, or email...")) {
        sql += " AND (acc_name LIKE ? OR acc_id LIKE ? OR acc_email LIKE ? OR acc_contact LIKE ?)";
    }

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        int paramIndex = 1;

        if (!selectedRole.equals("🌐 All")) {
            pst.setString(paramIndex++, mapRoleToDb(selectedRole));
        }

        if (!selectedStatus.equalsIgnoreCase("All")) {
            pst.setInt(paramIndex++, mapStatusToInt(selectedStatus));
        }

        if (!keyword.isEmpty() && !keyword.equals("🔎 Search accounts by name, ID, or email...")) {
            String searchTerm = "%" + keyword + "%";
            pst.setString(paramIndex++, searchTerm);
            pst.setString(paramIndex++, searchTerm);
            pst.setString(paramIndex++, searchTerm);
            pst.setString(paramIndex++, searchTerm);
        }

        try (ResultSet rs = pst.executeQuery()) {
            tbl.setModel(DbUtils.resultSetToTableModel(rs));
            tbl.setDefaultEditor(Object.class, null);
            tbl.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
                // ✅ Add column width adjustments here
    tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    TableColumnModel columnModel = tbl.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(50);   // id
    columnModel.getColumn(1).setPreferredWidth(150);  // name
    columnModel.getColumn(2).setPreferredWidth(250);  // email
    columnModel.getColumn(3).setPreferredWidth(150);  // contact
    columnModel.getColumn(4).setPreferredWidth(100);  // role
    columnModel.getColumn(5).setPreferredWidth(100);  // status
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error filtering Users: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private void saveEditedUserRoleStatus() {
    String newRole = mapRoleToDb(editUser_role.getSelectedItem().toString());
    int newStatus = mapStatusToInt(editUser_status.getSelectedItem().toString());

    String sql = "UPDATE tbl_accounts SET acc_role = ?, acc_status = ? WHERE acc_id = ?";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, newRole);
        pst.setInt(2, newStatus);
        pst.setInt(3, selectedAccId); // only used in WHERE clause

        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "User updated successfully!");
        acctable(); // refresh table
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void saveNewStaffUser() {
    String name = addname_staff.getText().trim();
    String email = addemail_staff.getText().trim();
    String password = addpass_staff.getText().trim();
    String contact = addcontact_staff.getText().trim();
    String role = role_staff.getSelectedItem().toString();
    String status = status_staff.getSelectedItem().toString();

    // ✅ Validation
    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (!isValidEmail(email)) {
        JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ✅ Map role and status
    String dbRole = role.toLowerCase();
    int dbStatus = status.equalsIgnoreCase("active") ? 1 : 0;

    // ✅ Insert into DB
    String sql = "INSERT INTO tbl_accounts (acc_name, acc_email, acc_pass, acc_contact, acc_role, acc_status) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, name);
        pst.setString(2, email);
        pst.setString(3, password);   // ⚠️ consider hashing
        pst.setString(4, contact);
        pst.setString(5, dbRole);
        pst.setInt(6, dbStatus);

        int rows = pst.executeUpdate();
        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "New staff user added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            staffDentistTable(); // refresh table view

            // ✅ Clear fields after save
            addname_staff.setText("");
            addemail_staff.setText("");
            addpass_staff.setText("");
            addcontact_staff.setText("");
            role_staff.setSelectedIndex(0);
            status_staff.setSelectedIndex(0);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
private void loadAllUsers() {
    String sql = "SELECT acc_id AS Id, acc_name AS Name, acc_email AS Email, acc_contact AS Contact, acc_role AS Role, " +
                 "CASE acc_status WHEN 1 THEN 'Active' WHEN 0 THEN 'Inactive' WHEN 2 THEN 'Suspended' END AS status " +
                 "FROM tbl_accounts";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        tbl.setModel(DbUtils.resultSetToTableModel(rs));
        tbl.setDefaultEditor(Object.class, null);
        tbl.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
    // ✅ Add column width adjustments here
    tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    TableColumnModel columnModel = tbl.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(50);   // id
    columnModel.getColumn(1).setPreferredWidth(150);  // name
    columnModel.getColumn(2).setPreferredWidth(250);  // email
    columnModel.getColumn(3).setPreferredWidth(150);  // contact
    columnModel.getColumn(4).setPreferredWidth(100);  // role
    columnModel.getColumn(5).setPreferredWidth(100);  // status
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error loading Users: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void editStaffStatus(int accId, String newStatus) {
    int statusDb = mapStatusToInt(newStatus);

    if (statusDb == -1) {
        JOptionPane.showMessageDialog(this, "Invalid status selected.");
        return;
    }

    try (Connection conn = config.connectDB()) {
        String sql = "UPDATE tbl_accounts SET acc_status=? WHERE acc_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusDb);
            ps.setInt(2, accId);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Staff/Dentist status updated!");
                staffDentistTable();  // refresh workforce table
                loadAllUsers();       // refresh all users table
            } else {
                JOptionPane.showMessageDialog(this, "No rows updated. Check acc_id.");
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void editDentistStatusAndSpecialty(int accId, String newStatus, String newSpecialty) {
    int statusDb = mapStatusToInt(newStatus);

    if (statusDb == -1) {
        JOptionPane.showMessageDialog(this, "Invalid status selected.");
        return;
    }

    try (Connection conn = config.connectDB()) {
        // Update status in tbl_accounts
        String sqlAcc = "UPDATE tbl_accounts SET acc_status=? WHERE acc_id=?";
        try (PreparedStatement psAcc = conn.prepareStatement(sqlAcc)) {
            psAcc.setInt(1, statusDb);
            psAcc.setInt(2, accId);
            psAcc.executeUpdate();
        }

        // Update specialty in tbl_dentists
        String sqlDentist = "UPDATE tbl_dentists SET specialty=? WHERE acc_id=?";
        try (PreparedStatement psDentist = conn.prepareStatement(sqlDentist)) {
            psDentist.setString(1, newSpecialty);
            psDentist.setInt(2, accId);
            psDentist.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Dentist status and specialty updated!");
        staffDentistTable();  // refresh workforce table
        loadAllUsers();       // refresh all users table
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void loadProfileDisplay() {
    try (Connection conn = config.connectDB();
         PreparedStatement ps = conn.prepareStatement(
             "SELECT acc_name, acc_email, acc_contact, acc_role " +
             "FROM tbl_accounts WHERE acc_id=?")) {

        ps.setInt(1, session.getId()); // dentist’s own account ID
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                dentist_name.setText(rs.getString("acc_name"));
                dentist_email.setText(rs.getString("acc_email"));
                dentist_contact.setText(rs.getString("acc_contact"));
                dentist_role.setText(rs.getString("acc_role"));
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void loadDentistAvailable() {
    String sql = "SELECT COUNT(*) AS availableDentists " +
                 "FROM tbl_accounts " +
                 "WHERE acc_role = 'dentist' AND acc_status = 1"; // 1 = active

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        if (rs.next()) {
            int count = rs.getInt("availableDentists");
            // ✅ Only show the number
            Dentist_available.setText(String.valueOf(count));
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading dentist availability: " + e.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Dentist_available;
    private javax.swing.JLabel XBTN;
    private javax.swing.JPanel XPNL;
    private javax.swing.JLabel add;
    private javax.swing.JTextField addcontact_staff;
    private javax.swing.JTextField addemail_staff;
    private javax.swing.JTextField addname_staff;
    private javax.swing.JTextField addpass_staff;
    private javax.swing.JPanel addpicture;
    private javax.swing.JPanel addpicture2;
    private javax.swing.JLabel addstaff;
    private javax.swing.JLabel admin_name;
    private javax.swing.JComboBox<String> allUsers;
    private javax.swing.JPanel analyticpane;
    private javax.swing.JPanel analytics;
    private javax.swing.JLabel analyticsbtn;
    private javax.swing.JTextField app_search;
    private javax.swing.JLabel appbtn;
    private javax.swing.JPanel apppane;
    private javax.swing.JPanel bg;
    private javax.swing.JLabel billbtn;
    private javax.swing.JPanel billpane;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel cancelAddingstaf;
    private javax.swing.JLabel cancelEditStaff;
    private javax.swing.JLabel cancel_edit;
    private javax.swing.JLabel cancelsave_dentist;
    private javax.swing.JLabel change_photo;
    private javax.swing.JLabel change_photo1;
    private javax.swing.JLabel circle_adminPic;
    private javax.swing.JLabel contact;
    private javax.swing.JTextField contact_newUser;
    private javax.swing.JPanel dashbox;
    private javax.swing.JLabel dashbtn;
    private javax.swing.JPanel dashpnl;
    private javax.swing.JPanel db;
    private javax.swing.JLabel delete;
    private javax.swing.JLabel deletestaff;
    private javax.swing.JLabel dentist_contact;
    private javax.swing.JLabel dentist_email;
    private javax.swing.JLabel dentist_name;
    private javax.swing.JLabel dentist_role;
    private javax.swing.JComboBox<String> dentist_stat;
    private javax.swing.JLabel edit;
    private javax.swing.JLabel editUser_contact;
    private javax.swing.JLabel editUser_email;
    private javax.swing.JLabel editUser_name;
    private javax.swing.JComboBox<String> editUser_role;
    private javax.swing.JComboBox<String> editUser_status;
    private javax.swing.JTextField edit_confirmpass;
    private javax.swing.JTextField edit_contact;
    private javax.swing.JTextField edit_email;
    private javax.swing.JTextField edit_fullname;
    private javax.swing.JTextField edit_newpass;
    private javax.swing.JLabel editprofile;
    private javax.swing.JLabel editprofile2;
    private javax.swing.JLabel editstaff;
    private javax.swing.JLabel email;
    private javax.swing.JTextField email_newUser;
    private javax.swing.JLabel filterAllUsers;
    private javax.swing.JLabel filter_search;
    private javax.swing.JPanel hdr;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
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
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
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
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
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
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel65;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel68;
    private javax.swing.JPanel jPanel69;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel70;
    private javax.swing.JPanel jPanel71;
    private javax.swing.JPanel jPanel72;
    private javax.swing.JPanel jPanel73;
    private javax.swing.JPanel jPanel74;
    private javax.swing.JPanel jPanel75;
    private javax.swing.JPanel jPanel76;
    private javax.swing.JPanel jPanel77;
    private javax.swing.JPanel jPanel78;
    private javax.swing.JPanel jPanel79;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel80;
    private javax.swing.JPanel jPanel81;
    private javax.swing.JPanel jPanel82;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel logoutbtn;
    private javax.swing.JPanel logoutpane;
    private javax.swing.JPanel mp;
    private javax.swing.JPanel mp1;
    private javax.swing.JPanel myprofile;
    private javax.swing.JLabel name;
    private javax.swing.JTextField name_newUser;
    private javax.swing.JLabel p;
    private javax.swing.JTextField password_newUser;
    private javax.swing.JLabel patients_in_waiting;
    private javax.swing.JPanel pp;
    private javax.swing.JLabel profilePic;
    private javax.swing.JLabel profilePic1;
    private javax.swing.JLabel role;
    private javax.swing.JLabel role_;
    private javax.swing.JComboBox<String> role_newUser;
    private javax.swing.JComboBox<String> role_staff;
    private javax.swing.JComboBox<String> role_staff_dentist;
    private javax.swing.JLabel saveEditUser;
    private javax.swing.JLabel saveEdit_dentist;
    private javax.swing.JLabel saveEdit_staff;
    private javax.swing.JLabel saveNewstaff;
    private javax.swing.JLabel save_newUser;
    private javax.swing.JLabel savechanges_profile;
    private javax.swing.JTextField seach_logs;
    private javax.swing.JTextField search;
    private javax.swing.JLabel search_systemlogs;
    private javax.swing.JComboBox<String> setDentist_specialty;
    private javax.swing.JLabel staff_contact;
    private javax.swing.JTextField staff_dentistSearch;
    private javax.swing.JTable staff_dentist_table;
    private javax.swing.JLabel staff_email;
    private javax.swing.JLabel staff_name;
    private javax.swing.JLabel staff_role;
    private javax.swing.JComboBox<String> staff_stat;
    private javax.swing.JLabel staffbtn;
    private javax.swing.JPanel staffmanage;
    private javax.swing.JPanel staffpane;
    private javax.swing.JComboBox<String> statusAllUsers;
    private javax.swing.JComboBox<String> status_newUser;
    private javax.swing.JComboBox<String> status_staff;
    private javax.swing.JComboBox<String> status_staff_dentist;
    private javax.swing.JTable system_logs;
    private javax.swing.JLabel system_logsbtn;
    private javax.swing.JPanel systemlogs;
    private javax.swing.JPanel systempane;
    private javax.swing.JTabbedPane tabbed;
    private javax.swing.JTable tbl;
    private javax.swing.JLabel todays_appointment;
    private javax.swing.JPanel userpane;
    private javax.swing.JPanel users;
    private javax.swing.JLabel usersbtn;
    // End of variables declaration//GEN-END:variables
}
