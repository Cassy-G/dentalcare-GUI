
package features;

import config.config;
import features.admin.StatusCellRenderer;
import java.awt.Color;
import internal.session;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import net.proteanit.sql.DbUtils;


public class staff extends javax.swing.JFrame {
 int xMouse, yMouse;
 private static final String DENTIST_PLACEHOLDER = "Search Dentists...";
 private static final String PATIENT_PLACEHOLDER = "Search Patients...";
 private static final String APPOINTMENT_PLACEHOLDER = "Search Appointments...";
 private boolean showingArchivedPatients = false;
 
 
private void filterDentists(String keyword) {
    javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter =
        new javax.swing.table.TableRowSorter<>((javax.swing.table.DefaultTableModel) dentistAvailabilityTABLE.getModel());
    dentistAvailabilityTABLE.setRowSorter(sorter);

    if (keyword == null || keyword.trim().isEmpty() || keyword.equals("🔎 Search Dentists...")) {
        sorter.setRowFilter(null); // show all if empty or placeholder
    } else {
        sorter.setRowFilter(javax.swing.RowFilter.orFilter(Arrays.asList(
            javax.swing.RowFilter.regexFilter("(?i)" + keyword, 1), // Dentist Name column
            javax.swing.RowFilter.regexFilter("(?i)" + keyword, 2)  // Specialty column
        )));
    }
   
}


private void logAction(Connection con, int actorId, String actorRole, String action, String details) {
    String sql = "INSERT INTO tbl_logs (actor_id, actor_role, action, details, created_at) " +
                 "VALUES (?, ?, ?, ?, datetime('now'))";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, actorId);
        ps.setString(2, actorRole);
        ps.setString(3, action);
        ps.setString(4, details);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace(); // optional: don’t block the user if logging fails
    }
}


private void filterPatients(String keyword) {
    javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter =
        new javax.swing.table.TableRowSorter<>((javax.swing.table.DefaultTableModel) ALLPATIENTS.getModel());
    ALLPATIENTS.setRowSorter(sorter);

    if (keyword == null || keyword.trim().isEmpty() || keyword.equals("🔎 Search Patients...")) {
        sorter.setRowFilter(null); // show all if empty or placeholder
    } else {
        sorter.setRowFilter(javax.swing.RowFilter.orFilter(Arrays.asList(
            javax.swing.RowFilter.regexFilter("(?i)" + keyword, 1), // Full Name column
            javax.swing.RowFilter.regexFilter("(?i)" + keyword, 2), // Email Address column
            javax.swing.RowFilter.regexFilter("(?i)" + keyword, 5)  // Contact Number column
        )));
    }
}

private static class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String status = value != null ? value.toString() : "";
        setHorizontalAlignment(CENTER);
        setFont(getFont().deriveFont(Font.BOLD));

        if ("Active".equalsIgnoreCase(status)) {
            c.setBackground(new Color(220, 255, 220)); // light green
            c.setForeground(new Color(0, 100, 0));     // dark green text
        } else if ("Archived".equalsIgnoreCase(status)) {
            c.setBackground(new Color(255, 220, 220)); // light red
            c.setForeground(new Color(139, 0, 0));     // dark red text
        } else {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}

    /**
     * Creates new form log
     */
    public staff() {
        initComponents();
        if (!authorizeStaffSession()) {
            return;
        }
    // 🔒 Check if user is logged in
    if (session.getId() == 0) {
        JOptionPane.showMessageDialog(this, "Please login first.");
        new login().setVisible(true);
        this.dispose();
        return;
    }

    // 🔒 Check if role is STAFF
    if (!session.getRole().equalsIgnoreCase("staff")) {
        JOptionPane.showMessageDialog(this, "Access Denied. Staff only.");
        this.dispose();
        return;
    }

    // ✅ If authorized
    stafflabel.setText("Welcome, " + session.getName()); 
    
  // ✅ Placeholder setup with emoji
filterDentists.setText("🔎 Search Dentists...");
filterDentists.setForeground(Color.GRAY);
filterDentists.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // ensures emoji renders properly

// ✅ Focus listeners (clear when typing, restore when empty)
filterDentists.addFocusListener(new java.awt.event.FocusAdapter() {
    @Override
    public void focusGained(java.awt.event.FocusEvent evt) {
        if (filterDentists.getText().equals("🔎 Search Dentists...")) {
            filterDentists.setText("");              // clear placeholder naturally
            filterDentists.setForeground(Color.BLACK); // normal typing color
        }
    }
    @Override
    public void focusLost(java.awt.event.FocusEvent evt) {
        if (filterDentists.getText().trim().isEmpty()) {
            filterDentists.setText("🔎 Search Dentists...");
            filterDentists.setForeground(Color.GRAY); // placeholder color
        }
    }
});

// ✅ Single KeyListener (no duplication)
filterDentists.addKeyListener(new java.awt.event.KeyAdapter() {
    @Override
    public void keyReleased(java.awt.event.KeyEvent evt) {
        String keyword = filterDentists.getText().trim();
        if (!keyword.equals("🔎 Search Dentists...") && !keyword.isEmpty()) {
            filterDentists(keyword); // call your filter method
        }
    }
});

// ✅ Search icon click (filters but keeps keyword visible)
searchbarDentists.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        String keyword = filterDentists.getText().trim();
        if (!keyword.equals("🔎 Search Dentists...") && !keyword.isEmpty()) {
            filterDentists(keyword);
        }
        // Do NOT clear the text field — keep keyword visible
        // Placeholder will naturally restore if field is emptied and loses focus
        searchbarDentists.setForeground(Color.WHITE);
        searchbarDentists.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }
});




// ✅ Placeholder setup for patient search
searchPatients.setText("🔎 Search Patients...");
searchPatients.setForeground(Color.GRAY);
searchPatients.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

// ✅ Focus listeners
searchPatients.addFocusListener(new java.awt.event.FocusAdapter() {
    @Override
    public void focusGained(java.awt.event.FocusEvent evt) {
        if (searchPatients.getText().equals("🔎 Search Patients...")) {
            searchPatients.setText("");
            searchPatients.setForeground(Color.BLACK);
        }
    }
    @Override
    public void focusLost(java.awt.event.FocusEvent evt) {
        if (searchPatients.getText().trim().isEmpty()) {
            searchPatients.setText("🔎 Search Patients...");
            searchPatients.setForeground(Color.GRAY);
        }
    }
});

// ✅ Key listener for live filtering
searchPatients.addKeyListener(new java.awt.event.KeyAdapter() {
    @Override
    public void keyReleased(java.awt.event.KeyEvent evt) {
        String keyword = searchPatients.getText().trim();
        if (!keyword.equals("🔎 Search Patients...") && !keyword.isEmpty()) {
            filterPatients(keyword);
        } else {
            filterPatients(""); // reset filter
        }
    }
});

        initializeStaffUi();
        loadAllStaffData();
        elevateHeaderProfileCircle();
    }

private boolean authorizeStaffSession() {
    if (session.getId() == 0) {
        JOptionPane.showMessageDialog(this, "Please login first.");
        new login().setVisible(true);
        this.dispose();
        return false;
    }

    if (!session.getRole().equalsIgnoreCase("staff")) {
        JOptionPane.showMessageDialog(this, "Access Denied. Staff only.");
        this.dispose();
        return false;
    }

    return true;
}

private void initializeStaffUi() {
    stafflabel.setText("Welcome, " + session.getName());
    setupPlaceholders();
    wireStaffActions();
    styleAppointmentControls();
    styleHeaderProfileShortcut();
    populateEditProfileFields();
}

private void loadAllStaffData() {
    loadprofile();
    populateEditProfileFields();
    loadDentistAvailability();
    loadAllPatients();
    refreshAppointmentListData();
    loadDashboardData();
}

private void setupPlaceholders() {
    filterDentists.setText(DENTIST_PLACEHOLDER);
    filterDentists.setForeground(Color.GRAY);
    searchPatients.setText(PATIENT_PLACEHOLDER);
    searchPatients.setForeground(Color.GRAY);
    searchbar_appointments.setText(APPOINTMENT_PLACEHOLDER);
    searchbar_appointments.setForeground(Color.GRAY);
}

private void wireStaffActions() {
    java.awt.event.MouseAdapter profileShortcutListener = new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            openStaffProfileOverview();
        }
    };
    jLabel14.addMouseListener(profileShortcutListener);
    stafflabel.addMouseListener(profileShortcutListener);
    jLabel15.addMouseListener(profileShortcutListener);

    filterDentists.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (DENTIST_PLACEHOLDER.equals(filterDentists.getText())) {
                filterDentists.setText("");
                filterDentists.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (filterDentists.getText().trim().isEmpty()) {
                filterDentists.setText(DENTIST_PLACEHOLDER);
                filterDentists.setForeground(Color.GRAY);
            }
        }
    });

    filterDentists.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            String keyword = filterDentists.getText().trim();
            if (keyword.isEmpty() || DENTIST_PLACEHOLDER.equals(keyword)) {
                filterDentists("");
            } else {
                filterDentists(keyword);
            }
        }
    });

    searchPatients.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (PATIENT_PLACEHOLDER.equals(searchPatients.getText())) {
                searchPatients.setText("");
                searchPatients.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (searchPatients.getText().trim().isEmpty()) {
                searchPatients.setText(PATIENT_PLACEHOLDER);
                searchPatients.setForeground(Color.GRAY);
            }
        }
    });

    searchPatients.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            String keyword = searchPatients.getText().trim();
            if (keyword.isEmpty() || PATIENT_PLACEHOLDER.equals(keyword)) {
                filterPatients("");
            } else {
                filterPatients(keyword);
            }
        }
    });

    java.awt.event.MouseAdapter newAppointmentListener = new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            openStaffBookingWindow();
        }
    };
    newAppointments.addMouseListener(newAppointmentListener);
    jPanel11.addMouseListener(newAppointmentListener);

    cancelEditPatients.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            resetPatientEditForm();
            dashTb.setSelectedIndex(2);
        }
    });

    searchbar_appointments.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (APPOINTMENT_PLACEHOLDER.equals(searchbar_appointments.getText())) {
                searchbar_appointments.setText("");
                searchbar_appointments.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (searchbar_appointments.getText().trim().isEmpty()) {
                searchbar_appointments.setText(APPOINTMENT_PLACEHOLDER);
                searchbar_appointments.setForeground(Color.GRAY);
            }
        }
    });

    searchbar_appointments.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            filterAppointments(searchbar_appointments.getText());
        }
    });
}

private void styleAppointmentControls() {
    java.awt.Cursor handCursor = new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR);
    jPanel11.setCursor(handCursor);
    newAppointments.setCursor(handCursor);
    jPanel20.setCursor(handCursor);
    jLabel48.setCursor(handCursor);
    searchbar_appointments.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    searchbar_appointments.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new Color(180, 200, 230)),
        javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)
    ));
    newAppointments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel42.setBounds(10, 0, 25, 40);
    jLabel43.setBounds(10, 0, 25, 40);
    jLabel44.setBounds(10, 0, 25, 40);
    jLabel45.setBounds(10, 0, 25, 40);
    jLabel21.setBounds(46, 0, 75, 40);
    jLabel22.setBounds(46, 0, 75, 40);
    jLabel27.setBounds(38, 0, 88, 40);
    jLabel28.setBounds(42, 0, 70, 18);
    jLabel29.setBounds(28, 20, 85, 16);

    java.awt.event.MouseAdapter hoverListener = new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            jPanel11.setBackground(new Color(0, 102, 255));
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            jPanel11.setBackground(new Color(0, 51, 255));
        }
    };

    jPanel11.addMouseListener(hoverListener);
    newAppointments.addMouseListener(hoverListener);

    java.awt.event.MouseAdapter filterHoverListener = new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            jPanel20.setBackground(new Color(0, 102, 255));
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            jPanel20.setBackground(new Color(0, 51, 255));
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            filterAppointments(searchbar_appointments.getText());
        }
    };

    jPanel20.addMouseListener(filterHoverListener);
    jLabel48.addMouseListener(filterHoverListener);
}

private void styleHeaderProfileShortcut() {
    java.awt.Cursor handCursor = new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR);
    jLabel14.setCursor(handCursor);
    stafflabel.setCursor(handCursor);
    jLabel15.setCursor(handCursor);
}

private void openStaffProfileOverview() {
    loadprofile();
    populateEditProfileFields();
    dashTb.setSelectedIndex(4);
}

private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
    if (icon == null) {
        return null;
    }

    Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(scaled);
}

private ImageIcon createCircularIcon(ImageIcon icon, int size) {
    if (icon == null) {
        return null;
    }

    BufferedImage circleBuffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = circleBuffer.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
    g2.drawImage(icon.getImage(), 0, 0, size, size, null);
    g2.dispose();

    return new ImageIcon(circleBuffer);
}

private void elevateHeaderProfileCircle() {
    if (jLabel14 == null || bg == null || hdr == null) {
        return;
    }

    hdr.remove(jLabel14);
    bg.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 2, 50, 48));
    bg.setComponentZOrder(jLabel14, 0);
    bg.repaint();
    bg.revalidate();
}

private void openStaffBookingWindow() {
    try {
        new book().setVisible(true);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error opening booking window: " + e.getMessage());
    }
}

private void populateEditProfileFields() {
    name.setText(nametxt.getText());
    email.setText(emailtxt.getText());
    contact.setText(contacttxt.getText());
    password.setText("");
}

private void resetPatientEditForm() {
    patientIDlabel.setText("");
    FULLNAME.setText("");
    EMAIL.setText("");
    AGE.setText("");
    CONTACT.setText("");
    ADDRESS.setText("");
    GENDER.setSelectedIndex(-1);
}

private void filterAppointments(String keyword) {
    javax.swing.table.TableRowSorter<javax.swing.table.TableModel> sorter =
        new javax.swing.table.TableRowSorter<>(tbl_AppointmentList.getModel());
    tbl_AppointmentList.setRowSorter(sorter);

    String cleaned = keyword == null ? "" : keyword.trim();
    if (cleaned.isEmpty() || APPOINTMENT_PLACEHOLDER.equals(cleaned)) {
        sorter.setRowFilter(null);
        return;
    }

    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + Pattern.quote(cleaned)));
}

private void loadDashboardData() {
    loadDashboardTables();
    loadDashboardSummary();
}

private void refreshAppointmentListData() {
    String sql = "SELECT a.app_id AS 'Appointment ID', " +
                 "COALESCE(p.pat_name, 'Unknown Patient') AS 'Patient', " +
                 "COALESCE('Dr. ' || accDent.acc_name, 'Unassigned Dentist') AS 'Dentist', " +
                 "COALESCE(d.specialty, 'Not Set') AS 'Specialty', " +
                 "a.app_date AS 'Date', " +
                 "a.app_time AS 'Time', " +
                 "a.app_service AS 'Service', " +
                 "a.app_service_price AS 'Price', " +
                 "a.app_status AS 'Status', " +
                 "a.payment_method AS 'Payment Method', " +
                 "a.payment_status AS 'Payment Status', " +
                 "a.created_at AS 'Created At' " +
                 "FROM tbl_appointments a " +
                 "LEFT JOIN tbl_patients p ON a.pat_id = p.pat_id " +
                 "LEFT JOIN tbl_dentists d ON (a.dentist_id = d.dentist_id OR a.dentist_id = d.acc_id) " +
                 "LEFT JOIN tbl_accounts accDent ON d.acc_id = accDent.acc_id";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        tbl_AppointmentList.setModel(DbUtils.resultSetToTableModel(rs));
        styleAppointmentTable();
        filterAppointments(searchbar_appointments.getText());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error loading appointments: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void loadDashboardTables() {
    loadTableData(
        jTable5,
        "SELECT a.app_id AS 'Appointment ID', COALESCE(p.pat_name, 'Unknown') AS 'Patient', " +
        "a.app_time AS 'Time', a.app_status AS 'Status' " +
        "FROM tbl_appointments a " +
        "LEFT JOIN tbl_patients p ON a.pat_id = p.pat_id " +
        "WHERE date(a.app_date) = date('now') " +
        "ORDER BY a.app_time ASC"
    );

    loadTableData(
        jTable6,
        "SELECT COALESCE(a.acc_name, 'Unassigned') AS 'Dentist', d.specialty AS 'Specialty', " +
        "COALESCE(d.dentist_stat, 'Available') AS 'Status' " +
        "FROM tbl_dentists d " +
        "LEFT JOIN tbl_accounts a ON d.acc_id = a.acc_id " +
        "ORDER BY a.acc_name ASC"
    );

    loadTableData(
        jTable4,
        "SELECT a.app_id AS 'Appointment ID', COALESCE(p.pat_name, 'Unknown') AS 'Patient', " +
        "COALESCE(acc.acc_name, 'Unassigned') AS 'Dentist', a.app_date AS 'Date', a.app_status AS 'Status' " +
        "FROM tbl_appointments a " +
        "LEFT JOIN tbl_patients p ON a.pat_id = p.pat_id " +
        "LEFT JOIN tbl_dentists d ON a.dentist_id = d.dentist_id " +
        "LEFT JOIN tbl_accounts acc ON d.acc_id = acc.acc_id " +
        "ORDER BY a.created_at DESC LIMIT 20"
    );
}

private void loadTableData(JTable table, String sql) {
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        table.setModel(DbUtils.resultSetToTableModel(rs));
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading dashboard data: " + e.getMessage());
    }
}

private void loadDashboardSummary() {
    jLabel42.setText(getCountLabel(
        "SELECT COUNT(*) FROM tbl_appointments WHERE lower(app_status)='completed' AND date(app_date)=date('now')"
    ));
    jLabel43.setText(getCountLabel(
        "SELECT COUNT(*) FROM tbl_appointments WHERE (lower(app_status)='no-show' OR lower(app_status)='cancelled') AND date(app_date)=date('now')"
    ));
    jLabel44.setText(getCountLabel(
        "SELECT COUNT(*) FROM tbl_appointments WHERE date(app_date)=date('now')"
    ));
    jLabel45.setText(getCountLabel(
        "SELECT COUNT(*) FROM tbl_appointments WHERE lower(app_status)='pending' AND date(app_date)=date('now')"
    ));
}

private String getCountLabel(String sql) {
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        if (rs.next()) {
            return String.valueOf(rs.getInt(1));
        }
    } catch (Exception e) {
        return "0";
    }
    return "0";
}



 
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        dashbox = new javax.swing.JPanel();
        dashpnl = new javax.swing.JPanel();
        dashbtn = new javax.swing.JLabel();
        schedpnl = new javax.swing.JPanel();
        schedbtn = new javax.swing.JLabel();
        apppnl = new javax.swing.JPanel();
        appbtn = new javax.swing.JLabel();
        patientpnl = new javax.swing.JPanel();
        patientbtn = new javax.swing.JLabel();
        docpnl = new javax.swing.JPanel();
        docbtn = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        logout = new javax.swing.JLabel();
        hdr = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        XPNL = new javax.swing.JPanel();
        XBTN = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        stafflabel = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dashTb = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_AppointmentList = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        newAppointments = new javax.swing.JLabel();
        searchbar_appointments = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ALLPATIENTS = new javax.swing.JTable();
        searchPatients = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        filterPATIENTS = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        editPatients = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        archive_patients = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        restoreArchive = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dentistAvailabilityTABLE = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        filterDentists = new javax.swing.JTextField();
        jPanel32 = new javax.swing.JPanel();
        searchbarDentists = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        mp = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        addpicture = new javax.swing.JPanel();
        pic = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        nametxt = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        emailtxt = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        contacttxt = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        role = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        editprofile = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        password = new javax.swing.JTextField();
        jPanel28 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        mp1 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        email = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        contact = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        addpicture1 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        acc_pic = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        save = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        PATIENTID = new javax.swing.JPanel();
        patientIDlabel = new javax.swing.JLabel();
        FULLNAME = new javax.swing.JTextField();
        EMAIL = new javax.swing.JTextField();
        AGE = new javax.swing.JTextField();
        CONTACT = new javax.swing.JTextField();
        ADDRESS = new javax.swing.JTextField();
        GENDER = new javax.swing.JComboBox<>();
        jPanel35 = new javax.swing.JPanel();
        saveEditPatients = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        cancelEditPatients = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jPanel38 = new javax.swing.JPanel();
        jPanel82 = new javax.swing.JPanel();
        jPanel85 = new javax.swing.JPanel();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jPanel86 = new javax.swing.JPanel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel125 = new javax.swing.JLabel();
        jLabel148 = new javax.swing.JLabel();
        jLabel149 = new javax.swing.JLabel();
        jLabel150 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jLabel152 = new javax.swing.JLabel();
        jLabel153 = new javax.swing.JLabel();
        jLabel154 = new javax.swing.JLabel();
        jLabel155 = new javax.swing.JLabel();
        jLabel146 = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        jPanel87 = new javax.swing.JPanel();
        jPanel88 = new javax.swing.JPanel();
        jLabel156 = new javax.swing.JLabel();
        jLabel157 = new javax.swing.JLabel();
        jPanel89 = new javax.swing.JPanel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jLabel158 = new javax.swing.JLabel();
        jLabel159 = new javax.swing.JLabel();
        jLabel160 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel161 = new javax.swing.JLabel();
        jLabel162 = new javax.swing.JLabel();
        jLabel163 = new javax.swing.JLabel();
        jLabel164 = new javax.swing.JLabel();
        jLabel165 = new javax.swing.JLabel();
        jLabel166 = new javax.swing.JLabel();
        jLabel167 = new javax.swing.JLabel();
        jLabel168 = new javax.swing.JLabel();
        jLabel169 = new javax.swing.JLabel();
        jLabel170 = new javax.swing.JLabel();

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
        dashbtn.setToolTipText("");
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

        schedbtn.setBackground(new java.awt.Color(255, 255, 255));
        schedbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        schedbtn.setForeground(new java.awt.Color(0, 51, 204));
        schedbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-today-24.png"))); // NOI18N
        schedbtn.setText("Appointments");
        schedbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                schedbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                schedbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                schedbtnMouseExited(evt);
            }
        });
        schedpnl.add(schedbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 130, 30));

        dashbox.add(schedpnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 30));

        apppnl.setBackground(new java.awt.Color(255, 255, 255));

        appbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        appbtn.setForeground(new java.awt.Color(0, 51, 204));
        appbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-users-24.png"))); // NOI18N
        appbtn.setText("Patients");
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

        javax.swing.GroupLayout apppnlLayout = new javax.swing.GroupLayout(apppnl);
        apppnl.setLayout(apppnlLayout);
        apppnlLayout.setHorizontalGroup(
            apppnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, apppnlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        apppnlLayout.setVerticalGroup(
            apppnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, apppnlLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(appbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        dashbox.add(apppnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 30));

        patientpnl.setBackground(new java.awt.Color(255, 255, 255));

        patientbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        patientbtn.setForeground(new java.awt.Color(0, 51, 204));
        patientbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-dentist-25.png"))); // NOI18N
        patientbtn.setText("Dentist Availability");
        patientbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                patientbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                patientbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                patientbtnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout patientpnlLayout = new javax.swing.GroupLayout(patientpnl);
        patientpnl.setLayout(patientpnlLayout);
        patientpnlLayout.setHorizontalGroup(
            patientpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, patientpnlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(patientbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        patientpnlLayout.setVerticalGroup(
            patientpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(patientbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        dashbox.add(patientpnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 170, 30));

        docpnl.setBackground(new java.awt.Color(255, 255, 255));

        docbtn.setBackground(new java.awt.Color(255, 255, 255));
        docbtn.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        docbtn.setForeground(new java.awt.Color(0, 51, 204));
        docbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-settings-24.png"))); // NOI18N
        docbtn.setText("Settings");
        docbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                docbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                docbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                docbtnMouseExited(evt);
            }
        });

        javax.swing.GroupLayout docpnlLayout = new javax.swing.GroupLayout(docpnl);
        docpnl.setLayout(docpnlLayout);
        docpnlLayout.setHorizontalGroup(
            docpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(docpnlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(docbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        docpnlLayout.setVerticalGroup(
            docpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(docbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        dashbox.add(docpnl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 170, 30));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        logout.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logout, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        dashbox.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 170, 30));

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
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        hdr.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        XBTN.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
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

        jLabel2.setBackground(new java.awt.Color(51, 51, 51));
        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 255));
        jLabel2.setText("Dental");
        hdr.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 60, 50));
        hdr.add(stafflabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 130, 30));

        jLabel16.setFont(new java.awt.Font("Modern No. 20", 3, 17)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 255));
        jLabel16.setText("Care");
        hdr.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 40, 50));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-circle-48.png"))); // NOI18N
        hdr.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 50, -1));

        jLabel15.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setText("Staff");
        hdr.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/5046faad4a4c9af72bcf4fe75c8a11d0.jpg"))); // NOI18N
        hdr.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 50));

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 51, 102));
        jLabel4.setText("Staff Management");
        jPanel6.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 40));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(jTable4);

        jPanel6.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 600, 160));

        jLabel23.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel23.setText("Today's Schedule");
        jPanel6.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, -1, -1));

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane5.setViewportView(jTable5);

        jPanel6.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 290, 170));

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(jTable6);

        jPanel6.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 90, 290, 170));

        jLabel26.setForeground(new java.awt.Color(204, 204, 204));
        jLabel26.setText("_____________________________________________________________________________________");
        jPanel6.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, 40));

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jPanel6.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 640, 500));

        dashTb.addTab("db", jPanel6);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 102));
        jLabel5.setText("Appointments");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 40));

        tbl_AppointmentList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tbl_AppointmentList);

        jPanel4.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 600, 260));

        jLabel18.setForeground(new java.awt.Color(204, 204, 204));
        jLabel18.setText("______________________________________________________________________________________");
        jPanel4.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 40));

        jPanel11.setBackground(new java.awt.Color(0, 51, 255));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        newAppointments.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        newAppointments.setForeground(new java.awt.Color(255, 255, 255));
        newAppointments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        newAppointments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-plus-sign-20.png"))); // NOI18N
        newAppointments.setText("New Appointment");
        jPanel11.add(newAppointments, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 30));

        jPanel4.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 80, 140, 30));
        jPanel4.add(searchbar_appointments, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 320, 30));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setForeground(new java.awt.Color(204, 204, 204));
        jLabel17.setText("_______________________________________________________________________________");
        jPanel12.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 30));

        jLabel19.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel19.setText("Today's Status");
        jPanel12.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, -3, -1, 40));

        jPanel13.setBackground(new java.awt.Color(0, 153, 0));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Completed");
        jPanel13.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 70, 40));

        jLabel42.setFont(new java.awt.Font("Trebuchet MS", 1, 13)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setText("jLabel42");
        jPanel13.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 40));

        jPanel12.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 130, 40));

        jPanel14.setBackground(new java.awt.Color(204, 153, 0));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("No-Shows");
        jPanel14.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, -1, 40));

        jLabel43.setFont(new java.awt.Font("Trebuchet MS", 1, 13)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setText("jLabel43");
        jPanel14.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 40));

        jPanel12.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 130, 40));

        jPanel16.setBackground(new java.awt.Color(0, 102, 255));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Avg. Wait Time");
        jPanel16.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(36, 0, 90, 40));

        jLabel44.setFont(new java.awt.Font("Trebuchet MS", 1, 13)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setText("jLabel44");
        jPanel16.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 40));

        jPanel12.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 130, 40));

        jPanel17.setBackground(new java.awt.Color(153, 0, 153));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Pending");
        jPanel17.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 0, 60, 20));

        jLabel29.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Appointments");
        jPanel17.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, -1, -1));

        jLabel45.setFont(new java.awt.Font("Trebuchet MS", 1, 13)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(255, 255, 255));
        jLabel45.setText("jLabel45");
        jPanel17.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 40));

        jPanel12.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 40, 130, 40));

        jPanel4.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, 590, 90));

        jPanel20.setBackground(new java.awt.Color(0, 51, 255));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel48.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 255, 255));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("Filter");
        jPanel20.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 30));

        jPanel4.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, 80, 30));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jLabel20.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel4.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 650, 510));

        dashTb.addTab("app", jPanel4);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 102));
        jLabel6.setText("Manage Patient Records");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 260, 50));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("______________________________________________________________________________________");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, 20));

        jPanel8.setBackground(new java.awt.Color(0, 51, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-plus-sign-20.png"))); // NOI18N
        jLabel8.setText("  Add New Patient ");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 30));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 150, 30));

        ALLPATIENTS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(ALLPATIENTS);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 600, 330));

        searchPatients.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchPatientsActionPerformed(evt);
            }
        });
        searchPatients.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchPatientsKeyReleased(evt);
            }
        });
        jPanel1.add(searchPatients, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 250, 30));

        jPanel15.setBackground(new java.awt.Color(0, 51, 255));
        jPanel15.setForeground(new java.awt.Color(0, 51, 255));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        filterPATIENTS.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        filterPATIENTS.setForeground(new java.awt.Color(255, 255, 255));
        filterPATIENTS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        filterPATIENTS.setText("Filter");
        filterPATIENTS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterPATIENTSMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                filterPATIENTSMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                filterPATIENTSMouseExited(evt);
            }
        });
        jPanel15.add(filterPATIENTS, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        jPanel1.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 40, 90, 30));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        editPatients.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editPatients.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-20.png"))); // NOI18N
        editPatients.setText("Edit Patient");
        editPatients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editPatientsMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editPatientsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editPatientsMouseExited(evt);
            }
        });
        jPanel7.add(editPatients, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 30));

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 150, 30));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        archive_patients.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        archive_patients.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-archive-folder-22.png"))); // NOI18N
        archive_patients.setText("Archive Patient");
        archive_patients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                archive_patientsMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                archive_patientsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                archive_patientsMouseExited(evt);
            }
        });
        jPanel10.add(archive_patients, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 0, 150, 30));

        jPanel1.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 90, 150, 30));

        jPanel37.setBackground(new java.awt.Color(255, 255, 255));
        jPanel37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 204, 255), 2));
        jPanel37.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        restoreArchive.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        restoreArchive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-restore-22.png"))); // NOI18N
        restoreArchive.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                restoreArchiveMouseClicked(evt);
            }
        });
        jPanel37.add(restoreArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 40, 30));

        jPanel1.add(jPanel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 90, 40, 30));

        jLabel46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jPanel1.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 640, 510));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 90, 140, 30));

        dashTb.addTab("patients", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dentistAvailabilityTABLE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(dentistAvailabilityTABLE);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 600, 320));

        jLabel24.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 51, 102));
        jLabel24.setText("Dentist Availability Overview");
        jPanel2.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, 50));

        jLabel25.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("___________________________________________________________________________________");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, 40));

        filterDentists.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterDentistsKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                filterDentistsKeyTyped(evt);
            }
        });
        jPanel2.add(filterDentists, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 280, 30));

        jPanel32.setBackground(new java.awt.Color(0, 51, 204));
        jPanel32.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        searchbarDentists.setBackground(new java.awt.Color(255, 255, 255));
        searchbarDentists.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        searchbarDentists.setForeground(new java.awt.Color(255, 255, 255));
        searchbarDentists.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchbarDentists.setText("Search");
        searchbarDentists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchbarDentistsMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchbarDentistsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchbarDentistsMouseExited(evt);
            }
        });
        jPanel32.add(searchbarDentists, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        jPanel2.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, 90, 30));

        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/dent.png"))); // NOI18N
        jLabel73.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 640, 510));

        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jPanel2.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 640, 520));

        dashTb.addTab("doctors", jPanel2);

        mp.setBackground(new java.awt.Color(255, 255, 255));
        mp.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel30.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel30.setText("My Profile");
        mp.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 130, 40));

        jLabel51.setForeground(new java.awt.Color(204, 204, 204));
        jLabel51.setText("_____________________________________________________________________________________");
        mp.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 30));

        addpicture.setBackground(new java.awt.Color(204, 204, 204));
        addpicture.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-profile-100.png"))); // NOI18N
        addpicture.add(pic, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 150));

        mp.add(addpicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 210, 150));

        jLabel31.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel31.setText("Full Name:");
        mp.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, -1, 30));

        jLabel53.setForeground(new java.awt.Color(204, 204, 204));
        jLabel53.setText("______________________________________________________________________________________");
        mp.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, 30));

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nametxt.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel21.add(nametxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 240, 30));

        mp.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 140, 270, 30));

        jLabel33.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel33.setText("Email:");
        mp.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 180, -1, 30));

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));
        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        emailtxt.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel22.add(emailtxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 240, 30));

        mp.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, 270, 30));

        jLabel34.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel34.setText("Contact");
        mp.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 220, -1, 30));

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        contacttxt.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel23.add(contacttxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 250, 30));

        mp.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, 270, 30));

        jLabel35.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel35.setText("Role");
        mp.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, 50, 30));

        jPanel24.setBackground(new java.awt.Color(255, 255, 255));
        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        role.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jPanel24.add(role, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 180, 30));

        mp.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 260, 270, 30));

        jPanel25.setBackground(new java.awt.Color(0, 51, 204));
        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(255, 255, 255));
        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-image-24.png"))); // NOI18N
        jLabel59.setText("Add Photo");
        jPanel25.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        mp.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 102, 30));

        jPanel26.setBackground(new java.awt.Color(0, 51, 204));
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jPanel26.add(editprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        mp.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 100, 30));

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        mp.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 600, 250));

        jLabel32.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel32.setText("Change Password");
        mp.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, 40));

        jLabel56.setForeground(new java.awt.Color(204, 204, 204));
        jLabel56.setText("______________________________________________________________________________________");
        mp.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, -1, 30));

        jLabel57.setForeground(new java.awt.Color(204, 204, 204));
        jLabel57.setText("______________________________________________________________________________________");
        mp.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, -1, 30));

        jLabel61.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel61.setText("New Password:");
        mp.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 370, -1, 30));
        mp.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 400, 270, 28));

        jLabel62.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel62.setText("Confirm New Password");
        mp.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 400, -1, 30));

        jPanel27.setBackground(new java.awt.Color(0, 51, 204));
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel63.setForeground(new java.awt.Color(255, 255, 255));
        jLabel63.setText("Save Changes");
        jPanel27.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 20));

        mp.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 460, 100, 22));
        mp.add(password, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 370, 270, 28));

        jPanel28.setBackground(new java.awt.Color(255, 255, 255));
        jPanel28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel60.setText("Cancel");
        jPanel28.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, -1, 20));

        mp.add(jPanel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 460, 100, 22));

        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        mp.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 600, 90));

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        mp.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 640, 520));

        jLabel55.setForeground(new java.awt.Color(204, 204, 204));
        jLabel55.setText("________________________________________________________________________________________");
        mp.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 306, -1, -1));

        dashTb.addTab("mp", mp);

        mp1.setBackground(new java.awt.Color(255, 255, 255));
        mp1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel36.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel36.setText("My Profile");
        mp1.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, 50));

        name.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        mp1.add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 100, 260, 30));

        jLabel37.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel37.setText("Full Name:");
        mp1.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 100, -1, 30));

        email.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        mp1.add(email, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 140, 260, 30));

        jLabel39.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel39.setText("Email:");
        mp1.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, -1, 30));

        contact.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        mp1.add(contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 180, 260, 30));

        jLabel40.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel40.setText("Contact:");
        mp1.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 180, -1, 30));

        jLabel41.setFont(new java.awt.Font("Tw Cen MT", 0, 16)); // NOI18N
        jLabel41.setText("Role");
        mp1.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 220, 90, 30));

        addpicture1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel31.setBackground(new java.awt.Color(255, 255, 255));
        jPanel31.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel72.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(0, 102, 204));
        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24 (1).png"))); // NOI18N
        jLabel72.setText("Change Photo");
        jPanel31.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 30));

        addpicture1.add(jPanel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 140, 30));

        acc_pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        acc_pic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50.png"))); // NOI18N
        acc_pic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                acc_picMouseClicked(evt);
            }
        });
        addpicture1.add(acc_pic, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 160, 120));

        mp1.add(addpicture1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 200, 180));

        jLabel38.setForeground(new java.awt.Color(204, 204, 204));
        jLabel38.setText("__________________________________________________________________________________");
        mp1.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, 30));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        mp1.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 220, 260, 30));

        jLabel65.setForeground(new java.awt.Color(204, 204, 204));
        jLabel65.setText("_________________________________________________________________________________");
        mp1.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, -1, 40));

        jLabel66.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel66.setText("Change Password");
        mp1.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 297, -1, 40));

        jLabel67.setForeground(new java.awt.Color(204, 204, 204));
        jLabel67.setText("_________________________________________________________________________________");
        mp1.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, -1, 20));

        jLabel68.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel68.setText("New Password:");
        mp1.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 350, -1, 30));

        jLabel69.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel69.setText("Confirm New Password:");
        mp1.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 386, -1, 20));

        jTextField4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        mp1.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 350, 280, 28));

        jTextField5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        mp1.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, 280, 28));

        jLabel70.setForeground(new java.awt.Color(204, 204, 204));
        jLabel70.setText("_________________________________________________________________________________");
        mp1.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, -1, 30));

        jPanel30.setBackground(new java.awt.Color(255, 255, 255));
        jPanel30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel30.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel71.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(51, 51, 51));
        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setText("Cancel");
        jPanel30.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 23));

        mp1.add(jPanel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 450, 100, 23));

        jPanel29.setBackground(new java.awt.Color(0, 51, 204));
        jPanel29.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        save.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        save.setForeground(new java.awt.Color(255, 255, 255));
        save.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        save.setText("Save Changes");
        save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveMouseClicked(evt);
            }
        });
        jPanel29.add(save, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 22));

        mp1.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 450, 110, -1));

        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        mp1.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 640, 510));

        dashTb.addTab("ep", mp1);

        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel33.setBackground(new java.awt.Color(255, 255, 255));
        jPanel33.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel34.setBackground(new java.awt.Color(0, 51, 255));
        jPanel34.setForeground(new java.awt.Color(0, 0, 153));
        jPanel34.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel77.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(255, 255, 255));
        jLabel77.setText("Edit Patient");
        jPanel34.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 0, -1, 50));

        jLabel79.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-30.png"))); // NOI18N
        jPanel34.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 40, 50));

        jPanel33.add(jPanel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 640, 50));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel11.setText("Patient ID:");
        jPanel33.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, -1, 30));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Full Name:");
        jPanel33.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, -1, 30));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel13.setText("Email Address:");
        jPanel33.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, -1, 30));

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel47.setText("Age:");
        jPanel33.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, 30));

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel74.setText("Gender:");
        jPanel33.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, -1, 30));

        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel75.setText("Contact Number:");
        jPanel33.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 290, -1, 30));

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel76.setText("Address:");
        jPanel33.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 330, -1, -1));

        PATIENTID.setBackground(new java.awt.Color(255, 255, 255));
        PATIENTID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        PATIENTID.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        patientIDlabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        patientIDlabel.setText("jLabel9");
        PATIENTID.add(patientIDlabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 60, 30));

        jPanel33.add(PATIENTID, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 210, 30));

        FULLNAME.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jPanel33.add(FULLNAME, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 390, 30));

        EMAIL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jPanel33.add(EMAIL, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, 390, 30));

        AGE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jPanel33.add(AGE, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 410, 30));

        CONTACT.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jPanel33.add(CONTACT, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 290, 380, 30));

        ADDRESS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jPanel33.add(ADDRESS, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 330, 340, 80));

        GENDER.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        GENDER.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        GENDER.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jPanel33.add(GENDER, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, 170, 30));

        jPanel35.setBackground(new java.awt.Color(51, 204, 0));
        jPanel35.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveEditPatients.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        saveEditPatients.setForeground(new java.awt.Color(255, 255, 255));
        saveEditPatients.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveEditPatients.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-check-24.png"))); // NOI18N
        saveEditPatients.setText("Save");
        saveEditPatients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveEditPatientsMouseClicked(evt);
            }
        });
        jPanel35.add(saveEditPatients, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel33.add(jPanel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 440, 100, 30));

        jPanel36.setBackground(new java.awt.Color(255, 255, 255));
        jPanel36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel36.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cancelEditPatients.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cancelEditPatients.setForeground(new java.awt.Color(102, 102, 102));
        cancelEditPatients.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cancelEditPatients.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-cancel-24.png"))); // NOI18N
        cancelEditPatients.setText("Cancel");
        jPanel36.add(cancelEditPatients, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel33.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 440, 100, 30));

        jLabel78.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel33.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 640, 440));

        jPanel18.add(jPanel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 640, 490));

        dashTb.addTab("editp", jPanel18);

        jPanel38.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel82.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel85.setBackground(new java.awt.Color(0, 0, 204));
        jPanel85.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel119.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel119.setForeground(new java.awt.Color(255, 255, 255));
        jLabel119.setText("Add New Patient");
        jPanel85.add(jLabel119, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 0, -1, 70));

        jLabel120.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-male-50 (1).png"))); // NOI18N
        jPanel85.add(jLabel120, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 70, 70));

        jPanel82.add(jPanel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 540, 68));

        jPanel86.setBackground(new java.awt.Color(255, 255, 255));
        jPanel86.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel86.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel86.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 370, 30));

        jTextField7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel86.add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 370, 30));

        jTextField8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel86.add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, 370, 30));

        jTextField9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel86.add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 200, 300, 30));

        jTextField10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel86.add(jTextField10, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 250, 260, 60));

        jLabel121.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel121.setText("Name:");
        jPanel86.add(jLabel121, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, 30));

        jLabel122.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel122.setText("Email:");
        jPanel86.add(jLabel122, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, -1, 30));

        jLabel124.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel124.setText("Age:");
        jPanel86.add(jLabel124, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, -1, 30));

        jSpinner1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel86.add(jSpinner1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 60, 30));

        jLabel125.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel125.setText("Sex:");
        jPanel86.add(jLabel125, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, 30));

        jLabel148.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel148.setText("Contact Number:");
        jPanel86.add(jLabel148, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, -1, 30));

        jLabel149.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel149.setText("Address:");
        jPanel86.add(jLabel149, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, -1));

        jLabel150.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jPanel86.add(jLabel150, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 30, 30));

        jLabel151.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-email-24.png"))); // NOI18N
        jPanel86.add(jLabel151, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 30, 30));

        jLabel152.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-age-24.png"))); // NOI18N
        jPanel86.add(jLabel152, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 126, 30, 20));

        jLabel153.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-sex-24.png"))); // NOI18N
        jPanel86.add(jLabel153, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 166, 40, -1));

        jLabel154.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-phone-contact-24.png"))); // NOI18N
        jPanel86.add(jLabel154, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 30, 30));

        jLabel155.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-address-24.png"))); // NOI18N
        jPanel86.add(jLabel155, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 40, 40));

        jPanel82.add(jPanel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 540, 350));

        jLabel146.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel82.add(jLabel146, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 510));

        jPanel38.add(jPanel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        dashTb.addTab("AP", jPanel38);

        jPanel39.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel87.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel88.setBackground(new java.awt.Color(0, 0, 204));
        jPanel88.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel156.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel156.setForeground(new java.awt.Color(255, 255, 255));
        jLabel156.setText("Edit Patient");
        jPanel88.add(jLabel156, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 0, -1, 70));

        jLabel157.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-edit-pencil-50.png"))); // NOI18N
        jPanel88.add(jLabel157, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 70, 70));

        jPanel87.add(jPanel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 540, 68));

        jPanel89.setBackground(new java.awt.Color(255, 255, 255));
        jPanel89.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel89.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel89.add(jTextField11, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 370, 30));

        jTextField12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel89.add(jTextField12, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 370, 30));

        jTextField13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel89.add(jTextField13, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, 370, 30));

        jTextField14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel89.add(jTextField14, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 200, 300, 30));

        jTextField15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel89.add(jTextField15, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 250, 260, 60));

        jLabel158.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel158.setText("Name:");
        jPanel89.add(jLabel158, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, 30));

        jLabel159.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel159.setText("Email:");
        jPanel89.add(jLabel159, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, -1, 30));

        jLabel160.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel160.setText("Age:");
        jPanel89.add(jLabel160, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, -1, 30));

        jSpinner2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        jPanel89.add(jSpinner2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 60, 30));

        jLabel161.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel161.setText("Sex:");
        jPanel89.add(jLabel161, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, 30));

        jLabel162.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel162.setText("Contact Number:");
        jPanel89.add(jLabel162, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, -1, 30));

        jLabel163.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel163.setText("Address:");
        jPanel89.add(jLabel163, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, -1));

        jLabel164.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-24.png"))); // NOI18N
        jPanel89.add(jLabel164, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 30, 30));

        jLabel165.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-email-24.png"))); // NOI18N
        jPanel89.add(jLabel165, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 30, 30));

        jLabel166.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-age-24.png"))); // NOI18N
        jPanel89.add(jLabel166, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 126, 30, 20));

        jLabel167.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-sex-24.png"))); // NOI18N
        jPanel89.add(jLabel167, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 166, 40, -1));

        jLabel168.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-phone-contact-24.png"))); // NOI18N
        jPanel89.add(jLabel168, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 30, 30));

        jLabel169.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-address-24.png"))); // NOI18N
        jPanel89.add(jLabel169, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 40, 40));

        jPanel87.add(jPanel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 540, 350));

        jLabel170.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel87.add(jLabel170, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 510));

        jPanel39.add(jPanel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 470));

        dashTb.addTab("tab9", jPanel39);

        hdr.add(dashTb, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 640, 520));

        bg.add(hdr, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 50));

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
        loadDashboardData();
        dashTb.setSelectedIndex(0);

    }//GEN-LAST:event_dashbtnMouseClicked

    private void dashbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbtnMouseExited
         dashpnl.setBackground(Color. white);
        dashbtn.setForeground(new Color(0, 51, 204));  
        
    }//GEN-LAST:event_dashbtnMouseExited

    private void schedbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_schedbtnMouseClicked
        refreshAppointmentListData();
        dashTb.setSelectedIndex(1);
    }//GEN-LAST:event_schedbtnMouseClicked

    private void newAppointmentsMouseEntered(java.awt.event.MouseEvent evt) {
        jPanel11.setBackground(new Color(0, 102, 255));
    }

    private void newAppointmentsMouseExited(java.awt.event.MouseEvent evt) {
        jPanel11.setBackground(new Color(0, 51, 255));
    }

    private void appbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseClicked
        loadAllPatients();
        dashTb.setSelectedIndex(2);
    }//GEN-LAST:event_appbtnMouseClicked

    private void patientbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patientbtnMouseClicked
        loadDentistAvailability();
        dashTb.setSelectedIndex(3);
    }//GEN-LAST:event_patientbtnMouseClicked

    private void docbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_docbtnMouseClicked
       populateEditProfileFields();
       dashTb.setSelectedIndex(4);
    }//GEN-LAST:event_docbtnMouseClicked

    private void schedbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_schedbtnMouseEntered
        schedpnl.setBackground(Color. blue);
        schedbtn.setForeground(Color.white);
    }//GEN-LAST:event_schedbtnMouseEntered

    private void schedbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_schedbtnMouseExited
         schedpnl.setBackground(Color. white);
        schedbtn.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_schedbtnMouseExited

    private void appbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseEntered
        apppnl.setBackground(Color. blue);
        appbtn.setForeground(Color.white);
    }//GEN-LAST:event_appbtnMouseEntered

    private void appbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appbtnMouseExited
        apppnl.setBackground(Color. white);
        appbtn.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_appbtnMouseExited

    private void patientbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patientbtnMouseEntered
        patientpnl.setBackground(Color. blue);
        patientbtn.setForeground(Color.white);
    }//GEN-LAST:event_patientbtnMouseEntered

    private void patientbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patientbtnMouseExited
        patientpnl.setBackground(Color. white);
        patientbtn.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_patientbtnMouseExited

    private void docbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_docbtnMouseEntered
        docpnl.setBackground(Color. blue);
        docbtn.setForeground(Color.white);
    }//GEN-LAST:event_docbtnMouseEntered

    private void docbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_docbtnMouseExited
        docpnl.setBackground(Color. white);
        docbtn.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_docbtnMouseExited

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
              
        
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
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            if (window != null && window.isDisplayable()) {
                window.dispose();
            }
        }
        new login().setVisible(true);
    }
    }//GEN-LAST:event_logoutMouseClicked

    private void logoutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseEntered
        logout.setForeground(Color.red);
    }//GEN-LAST:event_logoutMouseEntered

    private void logoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseExited
        logout.setBackground(Color. white);
        logout.setForeground(new Color(0, 51, 204));  
    }//GEN-LAST:event_logoutMouseExited

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        landingp logo = new landingp();
        this.dispose();
        logo.setVisible(true);
    }//GEN-LAST:event_jLabel1MouseClicked

    private void acc_picMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_acc_picMouseClicked

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "png", "jpeg", "gif"
            )
        );

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            try (Connection con = config.connectDB();
                 PreparedStatement pst = con.prepareStatement(
                     "UPDATE tbl_accounts SET acc_pic = ? WHERE acc_id = ?")) {

                try (FileInputStream fis = new FileInputStream(selectedFile)) {
                    pst.setBinaryStream(1, fis, (int) selectedFile.length());
                    pst.setInt(2, session.getId());
                    int updated = pst.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(this, "Profile picture updated!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating profile picture: " + e.getMessage());
            }

            loadprofile();
        }
    }//GEN-LAST:event_acc_picMouseClicked

    private void editprofileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editprofileMouseClicked
        populateEditProfileFields();
        dashTb.setSelectedIndex(5);
    }//GEN-LAST:event_editprofileMouseClicked

    private void saveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveMouseClicked
    String newName = name.getText().trim();
    String newEmail = email.getText().trim();
    String newContact = contact.getText().trim();
    String newPass = new String(password.getText()).trim();

    int fieldCount = 0;

    if (!newName.isEmpty()) fieldCount++;
    if (!newEmail.isEmpty()) fieldCount++;
    if (!newContact.isEmpty()) fieldCount++;
    if (!newPass.isEmpty()) fieldCount++;

    if (fieldCount == 0) {
        JOptionPane.showMessageDialog(this, "No fields to update!");
        return;
    }

    // ===== BUILD SQL DYNAMICALLY =====
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

    if (!newPass.isEmpty()) {
        sql.append("acc_pass=?, ");
    }

    // remove last ", "
    sql.setLength(sql.length() - 2);
    sql.append(" WHERE acc_id=?");

    // ===== PARAMETERS =====
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

    if (!newPass.isEmpty()) {
        params[index++] = config.hashPassword(newPass);
    }

    params[index] = session.getId();

    // ===== EXECUTE UPDATE =====
    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.length; i++) {
            pst.setObject(i + 1, params[i]);
        }

        int updated = pst.executeUpdate();

        if (updated > 0) {
            // ✅ Log staff action
            logAction(con, session.getId(), "staff", "Update Profile",
                      "Updated profile fields for acc_id=" + session.getId() +
                      (newName.isEmpty() ? "" : ", name=" + newName) +
                      (newEmail.isEmpty() ? "" : ", email=" + newEmail) +
                      (newContact.isEmpty() ? "" : ", contact=" + newContact) +
                      (newPass.isEmpty() ? "" : ", password changed"));

            if (!newName.isEmpty()) {
                session.setName(newName);
            }
            if (!newEmail.isEmpty()) {
                session.setEmail(newEmail);
            }
            if (!newContact.isEmpty()) {
                session.setContact(newContact);
            }

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            loadprofile(); // refresh UI
            populateEditProfileFields();
        } else {
            JOptionPane.showMessageDialog(this, "No changes were made.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
    }
    }//GEN-LAST:event_saveMouseClicked

    private void searchPatientsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchPatientsActionPerformed
        String keyword = searchPatients.getText().trim();
        if (keyword.equals("🔎 Search Patients...")) {
            keyword = "";
        }
        filterPatients(keyword);
    }//GEN-LAST:event_searchPatientsActionPerformed

    private void filterDentistsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterDentistsKeyReleased
       filterDentists(filterDentists.getText());

    }//GEN-LAST:event_filterDentistsKeyReleased

    private void searchbarDentistsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchbarDentistsMouseClicked
     // Get the keyword from the text field
    String keyword = filterDentists.getText().trim();

    // Run the search filter
    if (!keyword.equals("🔎 Search Dentists...") && !keyword.isEmpty()) {
        filterDentists(keyword);
    }

    // ✅ Do NOT clear the text field — keep the keyword visible
    // filterDentists.setText("");  <-- remove this line

    // Reset label styling if needed
    searchbarDentists.setForeground(Color.WHITE);
    searchbarDentists.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_searchbarDentistsMouseClicked

    private void filterDentistsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterDentistsKeyTyped
        filterDentists(filterDentists.getText());
    }//GEN-LAST:event_filterDentistsKeyTyped

    private void searchbarDentistsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchbarDentistsMouseExited
         // Reset background color to original white
    searchbarDentists.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_searchbarDentistsMouseExited

    private void searchbarDentistsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchbarDentistsMouseEntered
        searchbarDentists.setForeground(new Color(102, 204, 255)); // light aqua/sky blue
        searchbarDentists.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_searchbarDentistsMouseEntered

    private void filterPATIENTSMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterPATIENTSMouseClicked
      String keyword = searchPatients.getText().trim();

    // Validation: empty or placeholder
    if (keyword.isEmpty() || keyword.equals("🔎 Search Patients...")) {
        JOptionPane.showMessageDialog(this, 
            "Please enter a patient name, email, or contact number before filtering.",
            "Invalid Search", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Apply filter
    filterPatients(keyword);

    // Validation: check if any rows are visible after filtering
    if (ALLPATIENTS.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, 
            "No patients found matching: " + keyword,
            "Search Result", JOptionPane.INFORMATION_MESSAGE);
        // Reset filter so staff can see all patients again
        filterPatients("");
    }
    }//GEN-LAST:event_filterPATIENTSMouseClicked

    private void filterPATIENTSMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterPATIENTSMouseEntered
// Blue text with blue background when hovered
    filterPATIENTS.setForeground(new Color(0, 51, 204)); // clinic blue
    filterPATIENTS.setOpaque(true);
    filterPATIENTS.setBackground(new Color(204, 229, 255)); // light blue background
    filterPATIENTS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); // optional: hand pointer
    }//GEN-LAST:event_filterPATIENTSMouseEntered

    private void filterPATIENTSMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterPATIENTSMouseExited
           // Reset to default styling (white background, blue text)
    filterPATIENTS.setForeground(Color.WHITE); // original blue
    filterPATIENTS.setOpaque(true);
    filterPATIENTS.setBackground(new Color(0, 51, 204)); // back to white
    }//GEN-LAST:event_filterPATIENTSMouseExited

    private void searchPatientsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchPatientsKeyReleased
       String keyword = searchPatients.getText().trim();
       if (keyword.equals("🔎 Search Patients...")) {
           keyword = "";
       }
       filterPatients(keyword);
    }//GEN-LAST:event_searchPatientsKeyReleased

    private void editPatientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editPatientsMouseClicked
   int selectedRow = ALLPATIENTS.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a patient first.");
        return;
    }

    int modelRow = ALLPATIENTS.convertRowIndexToModel(selectedRow);

    // Extract patient data from table
    String patId = ALLPATIENTS.getModel().getValueAt(modelRow, 0).toString();
    String fullName = ALLPATIENTS.getModel().getValueAt(modelRow, 1).toString();
    String email = ALLPATIENTS.getModel().getValueAt(modelRow, 2).toString();
    String age = ALLPATIENTS.getModel().getValueAt(modelRow, 3).toString();
    String gender = ALLPATIENTS.getModel().getValueAt(modelRow, 4).toString();
    String contact = ALLPATIENTS.getModel().getValueAt(modelRow, 5).toString();
    String address = ALLPATIENTS.getModel().getValueAt(modelRow, 6).toString();

    // Sync into edit form
    patientIDlabel.setText(patId);
    FULLNAME.setText(fullName);
    EMAIL.setText(email);
    AGE.setText(age);
    CONTACT.setText(contact);
    ADDRESS.setText(address);
    GENDER.setSelectedItem(gender);

    // Redirect to Tab 6
    dashTb.setSelectedIndex(6);
    }//GEN-LAST:event_editPatientsMouseClicked

    private void saveEditPatientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveEditPatientsMouseClicked
     String patId = patientIDlabel.getText();
    if (patId == null || patId.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "No patient ID found.");
        return;
    }

    try (Connection con = config.connectDB()) {
        String sql = "UPDATE tbl_patients SET pat_name=?, pat_email=?, pat_age=?, pat_sex=?, pat_contact=?, pat_address=? WHERE pat_id=?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, FULLNAME.getText());
            pst.setString(2, EMAIL.getText());
            pst.setInt(3, Integer.parseInt(AGE.getText()));
            pst.setString(4, GENDER.getSelectedItem().toString());
            pst.setString(5, CONTACT.getText());
            pst.setString(6, ADDRESS.getText());
            pst.setInt(7, Integer.parseInt(patId));
            pst.executeUpdate();
        }

        // ✅ Log staff action
        logAction(con, session.getId(), "staff", "Edit Patient",
                  "Edited patient record: " + FULLNAME.getText() + " (ID=" + patId + ")");

        JOptionPane.showMessageDialog(this, "Patient record updated successfully.");

        // 🔄 Refresh table to sync changes
        loadAllPatients();
        resetPatientEditForm();
        // Optional: redirect back to Patients tab
        dashTb.setSelectedIndex(2);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error updating patient: " + e.getMessage());
    }
    }//GEN-LAST:event_saveEditPatientsMouseClicked

    private void archive_patientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_archive_patientsMouseClicked
 int selectedRow = ALLPATIENTS.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a patient to archive.");
        return;
    }

    int modelRow = ALLPATIENTS.convertRowIndexToModel(selectedRow);
    String patId = ALLPATIENTS.getModel().getValueAt(modelRow, 0).toString();
    String fullName = ALLPATIENTS.getModel().getValueAt(modelRow, 1).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to archive this patient?",
        "Confirm Archive",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection con = config.connectDB()) {
            String sql = "UPDATE tbl_patients SET pat_archive = 0 WHERE pat_id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(patId));
                pst.executeUpdate();

                // ✅ Log staff action (only once, right after update)
                logAction(con, session.getId(), "staff", "Archive Patient",
                          "Archived patient: " + fullName + " (ID=" + patId + ")");
            }

            JOptionPane.showMessageDialog(this, "Patient archived successfully.");
            loadAllPatients(); // reload active patients after archiving

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error archiving patient: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_archive_patientsMouseClicked

    private void editPatientsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editPatientsMouseEntered
        editPatients.setForeground(Color.WHITE);
        editPatients.setOpaque(true);
        editPatients.setBackground(new Color(0, 102, 204));
    }//GEN-LAST:event_editPatientsMouseEntered

    private void editPatientsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editPatientsMouseExited
        editPatients.setForeground(Color.WHITE);
        editPatients.setBackground(new Color(0, 51, 204));
    }//GEN-LAST:event_editPatientsMouseExited

    private void archive_patientsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_archive_patientsMouseEntered
        archive_patients.setForeground(Color.WHITE);
        archive_patients.setOpaque(true);
        archive_patients.setBackground(new Color(204, 51, 0));
    }//GEN-LAST:event_archive_patientsMouseEntered

    private void archive_patientsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_archive_patientsMouseExited
        archive_patients.setForeground(Color.WHITE);
        archive_patients.setBackground(new Color(0, 51, 204));
    }//GEN-LAST:event_archive_patientsMouseExited

    private void restoreArchiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_restoreArchiveMouseClicked
      int selectedRow = ALLPATIENTS.getSelectedRow();

    // If no row is selected, toggle archived patients view
    if (selectedRow == -1) {
        if (showingArchivedPatients) {
            loadAllPatients();
            JOptionPane.showMessageDialog(this, "Active patients loaded successfully.");
            return;
        }

        try {
            String sql = "SELECT pat_id, pat_name, pat_email, pat_age, pat_sex, pat_contact, pat_address, " +
                         "CASE WHEN pat_archive = 1 OR pat_archive IS NULL THEN 'Active' ELSE 'Archived' END AS status " +
                         "FROM tbl_patients WHERE pat_archive = 0";

            config cfg = new config();
            cfg.displayData(sql, ALLPATIENTS);

            String[] newHeaders = {
                "Patient ID",
                "Full Name",
                "Email",
                "Age",
                "Gender",
                "Contact Number",
                "Address",
                "Status"
            };

            for (int i = 0; i < newHeaders.length; i++) {
                ALLPATIENTS.getColumnModel().getColumn(i).setHeaderValue(newHeaders[i]);
            }

            ALLPATIENTS.getTableHeader().repaint();
            javax.swing.table.JTableHeader header = ALLPATIENTS.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 14));
            ALLPATIENTS.setRowHeight(25);

            // Hide internal ID column
            ALLPATIENTS.getColumnModel().getColumn(0).setMinWidth(0);
            ALLPATIENTS.getColumnModel().getColumn(0).setMaxWidth(0);
            ALLPATIENTS.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
            showingArchivedPatients = true;

            JOptionPane.showMessageDialog(this, "Archived patients loaded successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading archived patients: " + e.getMessage());
        }
        return;
    }

    // If a row is selected, restore that patient
    int modelRow = ALLPATIENTS.convertRowIndexToModel(selectedRow);
    String patId = ALLPATIENTS.getModel().getValueAt(modelRow, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
        "Do you want to restore this patient?",
        "Confirm Restore",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection con = config.connectDB()) {
            String sql = "UPDATE tbl_patients SET pat_archive = 1 WHERE pat_id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(patId));
                pst.executeUpdate();
            }

            logAction(con, session.getId(), "staff", "Restore Patient",
                      "Restored patient with ID=" + patId);

            JOptionPane.showMessageDialog(this, "Patient restored successfully.");
            loadAllPatients(); // reload active patients
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error restoring patient: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_restoreArchiveMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
     dashTb.setSelectedIndex(7);
    }//GEN-LAST:event_jLabel8MouseClicked
private void loadprofile() {

    String sql = "SELECT acc_id, acc_name, acc_email, acc_contact, acc_role, acc_pic FROM tbl_accounts WHERE acc_id = ?";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, session.getId());

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String fullName = rs.getString("acc_name");
                nametxt.setText(fullName);
                role.setText(rs.getString("acc_role"));
                emailtxt.setText(rs.getString("acc_email"));
                contacttxt.setText(rs.getString("acc_contact"));

                ImageIcon icon = loadImageIconFromBytes(rs.getBytes("acc_pic"));
                if (icon == null) {
                    icon = loadImageIconFromPath(rs.getString("acc_pic"));
                }
                if (icon == null) {
                    java.net.URL defaultUrl = getClass().getResource("/img/icons8-person-55.png");
                    if (defaultUrl != null) {
                        icon = new ImageIcon(defaultUrl);
                    }
                }

                if (icon != null) {
                    int profileWidth = pic.getWidth() > 0 ? pic.getWidth() : 160;
                    int profileHeight = pic.getHeight() > 0 ? pic.getHeight() : 120;
                    pic.setIcon(scaleIcon(icon, profileWidth, profileHeight));

                    int headerWidth = jLabel14.getWidth() > 0 ? jLabel14.getWidth() : 48;
                    jLabel14.setIcon(createCircularIcon(icon, headerWidth));
                    jLabel14.setText("");
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        e.printStackTrace();
    }
}


private void loadDentistAvailability() {
    try (Connection conn = config.connectDB()) {
        String sql = "SELECT d.dentist_id AS 'ID', " +
                     "a.acc_name AS 'Dentist Name', " +
                     "d.specialty AS 'Specialty', " +
                     "d.work_days AS 'Work Days', " +
                     "d.work_start AS 'Start Time', " +
                     "d.work_end AS 'End Time', " +
                     "d.dentist_stat AS 'Status' " +
                     "FROM tbl_dentists d " +
                     "JOIN tbl_accounts a ON d.acc_id = a.acc_id";

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        dentistAvailabilityTABLE.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));

        // ✅ Apply styling and column widths after model reset
        styleDentistTable();
        adjustColumnWidths();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading dentist availability: " + e.getMessage());
    }
}

private void styleDentistTable() {
    dentistAvailabilityTABLE.setRowHeight(32);
    dentistAvailabilityTABLE.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    dentistAvailabilityTABLE.setGridColor(new Color(220, 220, 220));
    dentistAvailabilityTABLE.setShowGrid(true);
    dentistAvailabilityTABLE.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    JTableHeader header = dentistAvailabilityTABLE.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setBackground(new Color(180, 220, 240)); // soft healthcare blue
    header.setForeground(new Color(50, 50, 70));    // dark slate text
    header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 150, 200)));

    // ✅ Attach custom renderer to Status column
    try {
        int statusIndex = dentistAvailabilityTABLE.getColumnModel().getColumnIndex("Status");
        dentistAvailabilityTABLE.getColumnModel().getColumn(statusIndex).setCellRenderer(new StatusCellRenderer());
    } catch (IllegalArgumentException ex) {
        // Status column not found
    }
}




private void adjustColumnWidths() {
    // ID column small
    dentistAvailabilityTABLE.getColumnModel().getColumn(0).setPreferredWidth(50);

    // Dentist Name column wide
    dentistAvailabilityTABLE.getColumnModel().getColumn(1).setPreferredWidth(160);

    // Specialty column medium
    dentistAvailabilityTABLE.getColumnModel().getColumn(2).setPreferredWidth(200);

    // Work Days column medium
    dentistAvailabilityTABLE.getColumnModel().getColumn(3).setPreferredWidth(225);

    // Start Time column small
    dentistAvailabilityTABLE.getColumnModel().getColumn(4).setPreferredWidth(120);

    // End Time column small
    dentistAvailabilityTABLE.getColumnModel().getColumn(5).setPreferredWidth(120);

    // Status column medium
    dentistAvailabilityTABLE.getColumnModel().getColumn(6).setPreferredWidth(130);
}

private void loadAllPatients() {
    String sql = "SELECT pat_id, pat_name, pat_email, pat_age, pat_sex, pat_contact, pat_address, " +
             "CASE " +
             "   WHEN pat_archive = 1 OR pat_archive IS NULL THEN 'Active' " +
             "   ELSE 'Archived' " +
             "END AS status " +
             "FROM tbl_patients " +
             "WHERE pat_archive = 1 OR pat_archive IS NULL";

    try {
        config cfg = new config();
        cfg.displayData(sql, ALLPATIENTS);

  String[] newHeaders = {
    "Patient ID",
    "Full Name",
    "Email",
    "Age",
    "Gender",
    "Contact",
    "Address",
    "Status"
};


        for (int i = 0; i < newHeaders.length; i++) {
            ALLPATIENTS.getColumnModel().getColumn(i).setHeaderValue(newHeaders[i]);
        }

        ALLPATIENTS.getTableHeader().repaint();
        javax.swing.table.JTableHeader header = ALLPATIENTS.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ALLPATIENTS.setRowHeight(25);

        // Hide internal ID column
        ALLPATIENTS.getColumnModel().getColumn(0).setMinWidth(0);
        ALLPATIENTS.getColumnModel().getColumn(0).setMaxWidth(0);
        ALLPATIENTS.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        showingArchivedPatients = false;

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
    }
}



private ImageIcon loadImageIconFromBytes(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
        return null;
    }
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
        BufferedImage image = ImageIO.read(bais);
        if (image != null) {
            return new ImageIcon(image);
        }
    } catch (Exception ignored) {
    }
    return null;
}

private ImageIcon loadImageIconFromPath(String imgPath) {
    if (imgPath == null || imgPath.trim().isEmpty()) {
        return null;
    }

    try {
        File imgFile = new File(imgPath);
        if (imgFile.exists()) {
            BufferedImage image = ImageIO.read(imgFile);
            if (image != null) {
                return new ImageIcon(image);
            }
        } else {
            java.net.URL resourceUrl = getClass().getResource(imgPath);
            if (resourceUrl == null) {
                resourceUrl = getClass().getResource("/" + imgPath);
            }
            if (resourceUrl != null) {
                BufferedImage image = ImageIO.read(resourceUrl);
                if (image != null) {
                    return new ImageIcon(image);
                }
            }
        }
    } catch (Exception ignored) {
    }
    return null;
}

private void loadAllPatientsWithStatus() {
    String sql = "SELECT pat_id, pat_name, pat_email, pat_age, pat_sex, pat_contact, pat_address, " +
                 "CASE " +
                 "   WHEN pat_archive = 1 OR pat_archive IS NULL THEN 'Active' " +
                 "   ELSE 'Archived' " +
                 "END AS status " +
                 "FROM tbl_patients";

    try {
        config cfg = new config();
        cfg.displayData(sql, ALLPATIENTS);

        String[] newHeaders = {
            "Patient ID",
            "Full Name",
            "Email",
            "Age",
            "Gender",
            "Contact Number",
            "Address",
            "Status"
        };

        for (int i = 0; i < newHeaders.length; i++) {
            ALLPATIENTS.getColumnModel().getColumn(i).setHeaderValue(newHeaders[i]);
        }

        ALLPATIENTS.getTableHeader().repaint();
        javax.swing.table.JTableHeader header = ALLPATIENTS.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ALLPATIENTS.setRowHeight(25);

        // Hide internal ID column
        ALLPATIENTS.getColumnModel().getColumn(0).setMinWidth(0);
        ALLPATIENTS.getColumnModel().getColumn(0).setMaxWidth(0);
          // ✅ Apply custom renderer to Status column (index 7)
        ALLPATIENTS.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
    }
}
private void loadAppointmentList() {
    String sql = "SELECT a.app_id AS 'Appointment ID', " +
                 "a.pat_id AS 'Patient ID', " +   // ✅ show patient ID instead of name
                 "'Dr. ' || accDent.acc_name AS 'Dentist', " +
                 "d.specialty AS 'Specialty', " +
                 "a.app_date AS 'Date', " +
                 "a.app_time AS 'Time', " +
                 "a.app_service AS 'Service', " +
                 "a.app_service_price AS 'Price', " +
                 "a.app_status AS 'Status', " +
                 "a.payment_method AS 'Payment Method', " +
                 "a.payment_status AS 'Payment Status', " +
                 "a.created_at AS 'Created At' " +
                 "FROM tbl_appointments a " +
                 "LEFT JOIN tbl_dentists d ON a.dentist_id = d.dentist_id " +
                 "LEFT JOIN tbl_accounts accDent ON d.acc_id = accDent.acc_id";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        tbl_AppointmentList.setModel(DbUtils.resultSetToTableModel(rs));
        styleAppointmentTable();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error loading appointments: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}


private void styleAppointmentTable() {
    tbl_AppointmentList.setRowHeight(30);
    tbl_AppointmentList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    tbl_AppointmentList.setGridColor(new Color(220, 220, 220));
    tbl_AppointmentList.setShowGrid(true);

    JTableHeader header = tbl_AppointmentList.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 15));
    header.setBackground(new Color(180, 220, 240));
    header.setForeground(Color.DARK_GRAY);

    tbl_AppointmentList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    if (tbl_AppointmentList.getColumnCount() > 0) {
        tbl_AppointmentList.getColumnModel().getColumn(0).setPreferredWidth(150); // Appointment ID
        tbl_AppointmentList.getColumnModel().getColumn(1).setPreferredWidth(180); // Patient Name
        tbl_AppointmentList.getColumnModel().getColumn(2).setPreferredWidth(180); // Dentist
        tbl_AppointmentList.getColumnModel().getColumn(3).setPreferredWidth(150); // Specialty
        tbl_AppointmentList.getColumnModel().getColumn(4).setPreferredWidth(150); // Date
        tbl_AppointmentList.getColumnModel().getColumn(5).setPreferredWidth(120); // Time
        tbl_AppointmentList.getColumnModel().getColumn(6).setPreferredWidth(200); // Service
        tbl_AppointmentList.getColumnModel().getColumn(7).setPreferredWidth(100); // Price
        tbl_AppointmentList.getColumnModel().getColumn(8).setPreferredWidth(120); // Status
        tbl_AppointmentList.getColumnModel().getColumn(9).setPreferredWidth(150); // Payment Method
        tbl_AppointmentList.getColumnModel().getColumn(10).setPreferredWidth(150); // Payment Status
        tbl_AppointmentList.getColumnModel().getColumn(11).setPreferredWidth(180); // Created At
    }

    tbl_AppointmentList.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 250, 255));
                c.setForeground(new Color(30, 30, 30));
            } else {
                c.setBackground(new Color(184, 207, 229));
                c.setForeground(Color.BLACK);
            }

            int statusCol = table.getColumn("Status").getModelIndex();
            if (column == statusCol && value != null) {
                String status = value.toString();
                if ("Confirmed".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(0, 128, 0));
                    ((JLabel)c).setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if ("Pending".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(255, 140, 0));
                    ((JLabel)c).setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if ("Cancelled".equalsIgnoreCase(status)) {
                    c.setForeground(Color.RED);
                    ((JLabel)c).setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
            }
            return c;
        }
    });
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
            java.util.logging.Logger.getLogger(staff.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(staff.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(staff.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(staff.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new staff().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ADDRESS;
    private javax.swing.JTextField AGE;
    private javax.swing.JTable ALLPATIENTS;
    private javax.swing.JTextField CONTACT;
    private javax.swing.JTextField EMAIL;
    private javax.swing.JTextField FULLNAME;
    private javax.swing.JComboBox<String> GENDER;
    private javax.swing.JPanel PATIENTID;
    private javax.swing.JLabel XBTN;
    private javax.swing.JPanel XPNL;
    private javax.swing.JLabel acc_pic;
    private javax.swing.JPanel addpicture;
    private javax.swing.JPanel addpicture1;
    private javax.swing.JLabel appbtn;
    private javax.swing.JPanel apppnl;
    private javax.swing.JLabel archive_patients;
    private javax.swing.JPanel bg;
    private javax.swing.JLabel cancelEditPatients;
    private javax.swing.JTextField contact;
    private javax.swing.JLabel contacttxt;
    private javax.swing.JTabbedPane dashTb;
    private javax.swing.JPanel dashbox;
    private javax.swing.JLabel dashbtn;
    private javax.swing.JPanel dashpnl;
    private javax.swing.JTable dentistAvailabilityTABLE;
    private javax.swing.JLabel docbtn;
    private javax.swing.JPanel docpnl;
    private javax.swing.JLabel editPatients;
    private javax.swing.JLabel editprofile;
    private javax.swing.JTextField email;
    private javax.swing.JLabel emailtxt;
    private javax.swing.JTextField filterDentists;
    private javax.swing.JLabel filterPATIENTS;
    private javax.swing.JPanel hdr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel155;
    private javax.swing.JLabel jLabel156;
    private javax.swing.JLabel jLabel157;
    private javax.swing.JLabel jLabel158;
    private javax.swing.JLabel jLabel159;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel160;
    private javax.swing.JLabel jLabel161;
    private javax.swing.JLabel jLabel162;
    private javax.swing.JLabel jLabel163;
    private javax.swing.JLabel jLabel164;
    private javax.swing.JLabel jLabel165;
    private javax.swing.JLabel jLabel166;
    private javax.swing.JLabel jLabel167;
    private javax.swing.JLabel jLabel168;
    private javax.swing.JLabel jLabel169;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel170;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel82;
    private javax.swing.JPanel jPanel85;
    private javax.swing.JPanel jPanel86;
    private javax.swing.JPanel jPanel87;
    private javax.swing.JPanel jPanel88;
    private javax.swing.JPanel jPanel89;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JLabel logout;
    private javax.swing.JPanel mp;
    private javax.swing.JPanel mp1;
    private javax.swing.JTextField name;
    private javax.swing.JLabel nametxt;
    private javax.swing.JLabel newAppointments;
    private javax.swing.JTextField password;
    private javax.swing.JLabel patientIDlabel;
    private javax.swing.JLabel patientbtn;
    private javax.swing.JPanel patientpnl;
    private javax.swing.JLabel pic;
    private javax.swing.JLabel restoreArchive;
    private javax.swing.JLabel role;
    private javax.swing.JLabel save;
    private javax.swing.JLabel saveEditPatients;
    private javax.swing.JLabel schedbtn;
    private javax.swing.JPanel schedpnl;
    private javax.swing.JTextField searchPatients;
    private javax.swing.JLabel searchbarDentists;
    private javax.swing.JTextField searchbar_appointments;
    private javax.swing.JLabel stafflabel;
    private javax.swing.JTable tbl_AppointmentList;
    // End of variables declaration//GEN-END:variables
}
