
package features;

import config.config;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import net.proteanit.sql.DbUtils;
import com.toedter.calendar.JDateChooser;
import java.awt.FlowLayout;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 *
 * @author Cassandra Gallera
 */
public class book extends javax.swing.JFrame {
int xMouse, yMouse;

private JDateChooser dateChooser;
private JComboBox<String> timeCombo;
private String selectedService;
private String selectedPrice;
private boolean closeOnlyMode = false;

public JTabbedPane getTaab() {
    return taab;
}

public void setCloseOnlyMode(boolean closeOnlyMode) {
    this.closeOnlyMode = closeOnlyMode;
}

protected void handleBookingWindowClose() {
    if (closeOnlyMode) {
        dispose();
        return;
    }

    landingp booknow = new landingp();
    dispose();
    booknow.setVisible(true);
}


// Utility method to record actions into tbl_logs
private void logAction(Connection con, int actorId, String actorRole, String action, String details) {
    String sql = "INSERT INTO tbl_logs (actor_id, actor_role, action, details, created_at) " +
                 "VALUES (?, ?, ?, ?, datetime('now'))";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, actorId);       // who performed the action
        ps.setString(2, actorRole);  // role: "patient", "staff", or "dentist"
        ps.setString(3, action);     // short action name, e.g. "Book Appointment"
        ps.setString(4, details);    // descriptive details
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace(); // optional: don’t block the user if logging fails
    }
}
    /**
     * Creates new form book
     */
    public book() {
        initComponents();
    hideOldPanels();     // hides NetBeans service panels
    initServiceCards();  // adds your new grid
    loadDentistData();
  
    
    
    
    // ✅ Add placeholders to text fields
addPlaceholder(booking_fullname, "Enter Full Name");
addPlaceholder(book_email, "Enter Email Address");
addPlaceholder(book_contact, "Enter Phone Number");
addPlaceholder(streetAddress, "Enter Street Address");

    // ✅ Auto-fill user credentials from session
    booking_fullname.setText(internal.session.getName());
    book_email.setText(internal.session.getEmail());
    book_contact.setText(internal.session.getContact());

    // ✅ If patient, fetch additional details
    if ("patient".equals(internal.session.getRole())) {
        try (Connection conn = config.connectDB()) {
            String sql = "SELECT p.pat_age, p.pat_sex, p.pat_address " +
                         "FROM tbl_patients p " +
                         "WHERE p.customer_id = ? " +
                         "OR p.pat_email = (SELECT acc_email FROM tbl_accounts WHERE acc_id = ?) " +
                         "LIMIT 1";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, internal.session.getId());
            pst.setInt(2, internal.session.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                age_spinner.setValue(rs.getInt("pat_age"));
                book_gender.setSelectedItem(rs.getString("pat_sex"));
                streetAddress.setText(rs.getString("pat_address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Apply summary styling
    styleSummaryLabels();
    
    
    

       // ✅ Create the calendar
    dateChooser = new JDateChooser();
    dateChooser.setPreferredSize(new Dimension(200, 30));
    dateChooser.setDate(new Date()); // Set default to today's date

    // ✅ Attach listener AFTER initialization
    dateChooser.addPropertyChangeListener("date", evt -> {
        if ("date".equals(evt.getPropertyName())) {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                int dentistId = internal.session.getDentistId();
                if (dentistId > 0) { // Only load if dentist is selected
                    loadAvailableTimes(dentistId, selectedDate);
                }
            }
        }
    });

    
    
    
    
    // ✅ Create the time dropdown (empty, will be filled dynamically)
    timeCombo = new JComboBox<>();
    timeCombo.setPreferredSize(new Dimension(120, 30));

    // ✅ Add them to your panel
    Jpanel_date_time.setLayout(new FlowLayout());
    Jpanel_date_time.add(dateChooser);
    Jpanel_date_time.add(timeCombo);
    
    
    
 table_dentist.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        int row = table_dentist.getSelectedRow();
        if (row != -1) {
            String dentistName = table_dentist.getValueAt(row, 0).toString(); // column 0 = Dentist Name

            try (Connection conn = config.connectDB()) {
                // ✅ Get dentist_id from name
                String sqlId = "SELECT dentist_id FROM tbl_dentists d " +
                               "JOIN tbl_accounts a ON d.acc_id = a.acc_id " +
                               "WHERE 'Dr. ' || a.acc_name = ?";
                PreparedStatement pstId = conn.prepareStatement(sqlId);
                pstId.setString(1, dentistName);
                ResultSet rsId = pstId.executeQuery();

                if (rsId.next()) {
                    int dentistId = rsId.getInt("dentist_id");
                    internal.session.setDentistId(dentistId);

                    JOptionPane.showMessageDialog(book.this,
                        "Dentist selected: " + dentistName);

                   String sql = "SELECT a.acc_name AS 'Dentist Name', " +
             "d.specialty AS 'Specialty', " +
             "d.work_days AS 'Work Days', " +
             "d.work_start AS 'Start Time', " +
             "d.work_end AS 'End Time' " +
             "FROM tbl_dentists d " +
             "JOIN tbl_accounts a ON d.acc_id = a.acc_id " +
             "WHERE d.dentist_id = ?";

                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, dentistId);
                    ResultSet rs = pst.executeQuery();
                    selectedDentist.setModel(DbUtils.resultSetToTableModel(rs));

                    // ✅ Style the selectedDentist table
                    selectedDentist.setRowHeight(28);
                    selectedDentist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    JTableHeader header2 = selectedDentist.getTableHeader();
                    header2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    header2.setBackground(new Color(200, 230, 240));
                    header2.setForeground(Color.DARK_GRAY);

                    // ✅ Move to Tab 3
                    taab.setSelectedIndex(3);

                    // 🔗 Load available times
                    if (dateChooser.getDate() != null) {
                        loadAvailableTimes(dentistId, dateChooser.getDate());
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(book.this,
                    "Error fetching dentist data: " + ex.getMessage());
            }
        }
    }
});


nextbtn3.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        // ✅ Collect values from your form/session
        String fullName = booking_fullname.getText().trim();
        String dentist = selectedDentist.getValueAt(0, 1).toString(); // assuming column 1 = dentist name
        String specialty = selectedDentist.getValueAt(0, 2).toString(); // assuming column 2 = specialty
        String date = new java.text.SimpleDateFormat("MMMM dd, yyyy")
                          .format(dateChooser.getDate());
        String time = (String) timeCombo.getSelectedItem();
        String service = selectedService;
        double price = getServicePrice(service);

        // ✅ Assign values to summary labels
        patientFullName.setText(fullName);
        dentistNAME.setText(dentist);
        dentistSpecialty.setText(specialty);
        summaryFulldate.setText(date + " " + time);
        serviceSummary.setText(service);
        moneytopay.setText("₱ " + price);

        // ✅ Switch to Tab 4 (Billing/Summary)
        taab.setSelectedIndex(4);
    }
});



    
    
        // Populate Province ComboBox
    streetprovince_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] { "Cebu", "Bohol", "Negros Oriental", "Leyte", "Others" }
    ));
    
    

// Fonts
Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

// Apply to labels
jLabel16.setFont(labelFont); // Full Name
jLabel17.setFont(labelFont); // Email
jLabel19.setFont(labelFont); // Age
jLabel18.setFont(labelFont); // Gender
jLabel20.setFont(labelFont); // Phone Number
jLabel21.setFont(labelFont); // Address

// Apply to text fields
booking_fullname.setFont(fieldFont);
book_email.setFont(fieldFont);
age_spinner.setFont(fieldFont);
book_contact.setFont(fieldFont);
streetAddress.setFont(fieldFont);
book_gender.setFont(fieldFont);

// Button styling
nextbtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
nextbtn.setForeground(Color.WHITE);
nextpane.setBackground(new Color(0, 123, 255)); // Professional blue

prevbtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
prevbtn.setForeground(new Color(0, 102, 255));

    }

    
private void initServiceCards() {
    Font cardFont = new Font("Segoe UI", Font.BOLD, 14);
    Font priceFont = new Font("Segoe UI", Font.PLAIN, 13);
    Font emojiFont = new Font("Segoe UI Emoji", Font.BOLD, 14);

    // Dental system theme colors
    Color backgroundColor = Color.WHITE;
    Color borderColor = new Color(200, 220, 240);   // soft healthcare gray-blue
    Color highlightColor = new Color(0, 123, 255); // professional dental blue
    Color titleColor = new Color(0, 51, 102);      // deep navy for text
    Color priceColor = new Color(0, 102, 204);     // lighter blue for price

    JPanel serviceGrid = new JPanel(new GridLayout(2, 4, 15, 15));
    serviceGrid.setBackground(backgroundColor);
    serviceGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    String[][] services = {
        {"🦷 Dental Cleaning", "₱ 1,000"},
        {"🩺 General Checkup", "₱ 500"},
        {"🦷 Dental Filling", "₱ 2,000"},
        {"💰 Teeth Whitening", "₱ 14,000"},
        {"🦷 Tooth Extraction", "₱ 1,500"},
        {"🦷 Root Canal", "₱ 8,000"},
        {"💎 Dental Crown", "₱ 15,000"},
        {"👥 Braces Consultation", "₱ 50,000"}
    };

    // Keep references to all cards so we can reset borders
    java.util.List<JPanel> cardList = new java.util.ArrayList<>();

    for (String[] service : services) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 120));
        card.setBackground(backgroundColor);
        card.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));

        // Format service name
        String[] words = service[0].split(" ");
        StringBuilder formattedName = new StringBuilder("<html><div style='text-align:center;'>");
        for (String word : words) {
            formattedName.append(word).append("<br>");
        }
        formattedName.append("</div></html>");

        JLabel title = new JLabel(formattedName.toString(), SwingConstants.CENTER);
        title.setFont(emojiFont);
        title.setForeground(titleColor);

        JLabel price = new JLabel(service[1], SwingConstants.CENTER);
        price.setFont(priceFont);
        price.setForeground(priceColor);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(backgroundColor);
        textPanel.add(title);
        textPanel.add(price);

        card.add(textPanel, BorderLayout.CENTER);
        
        
          card.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        // Reset all cards to default border
        for (JPanel c : cardList) {
            c.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        }

        // Highlight only the clicked card
        selectedService = service[0];
        selectedPrice = service[1];
        card.setBorder(BorderFactory.createLineBorder(highlightColor, 2));

        // ✅ Immediately move to Tab 3 (Date & Time)
if (selectedService != null && !selectedService.isEmpty()) {
    taab.setSelectedIndex(2); // ✅ go to Dentist tab
}

    }
});
        cardList.add(card);
        serviceGrid.add(card);
    }
    jPanel2.removeAll();
    jPanel2.setLayout(new BorderLayout());

    JLabel selectLabel = new JLabel(
        "<html><span style='color:black;'>Select Service</span> <span style='color:red;'>*</span></html>",
        SwingConstants.LEFT
    );
    selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    jPanel2.add(selectLabel, BorderLayout.NORTH);

    jPanel2.add(serviceGrid, BorderLayout.CENTER);

    JPanel navPanel = new JPanel(new BorderLayout());
    navPanel.setBackground(backgroundColor);
    navPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    navPanel.add(prevpane1, BorderLayout.WEST);
    navPanel.add(nextpane1, BorderLayout.EAST);

    jPanel2.add(navPanel, BorderLayout.SOUTH);

    jPanel2.revalidate();
    jPanel2.repaint();
}


// Utility method to add placeholder text to JTextField
private void addPlaceholder(JTextField field, String placeholder) {
    field.setText(placeholder);
    field.setForeground(Color.GRAY);
    field.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // keep consistent style

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (field.getText().equals(placeholder)) {
                field.setText("");
                field.setForeground(Color.BLACK);
                // ❌ removed font change, stays consistent
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (field.getText().isEmpty()) {
                field.setText(placeholder);
                field.setForeground(Color.GRAY);
                // ❌ removed font change, stays consistent
            }
        }
    });
}



private void hideOldPanels() {
    dc.setVisible(false);
    gc.setVisible(false);
    df.setVisible(false);
    tw.setVisible(false);
    te.setVisible(false);
    rc.setVisible(false);
    denc.setVisible(false);
    bc.setVisible(false);

    dentalcheckup.setVisible(false);
    dentalcleaning.setVisible(false);
    dentalfilling.setVisible(false);
    teethwhitening.setVisible(false);
    toothextraction.setVisible(false);
    rootcanal.setVisible(false);
    dentalcrown.setVisible(false);
    braceconsultation.setVisible(false);
}


private void loadAvailableTimes(int dentistId, Date selectedDate) {
    try (Connection conn = config.connectDB()) {
        String sql = "SELECT work_days, work_start, work_end, dentist_stat FROM tbl_dentists WHERE dentist_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, dentistId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String workDays = rs.getString("work_days");
            String startStr = rs.getString("work_start");
            String endStr   = rs.getString("work_end");
            String status   = rs.getString("dentist_stat");

            // Default times if null
            java.time.LocalTime start = parseScheduleTime(startStr);
            if (start == null) {
                start = java.time.LocalTime.of(8, 0);
            }
            java.time.LocalTime end = parseScheduleTime(endStr);
            if (end == null) {
                end = java.time.LocalTime.of(17, 0);
            }

            // Check day availability
            String fullDayName = new java.text.SimpleDateFormat("EEEE").format(selectedDate);
            String shortDayName = new java.text.SimpleDateFormat("EEE").format(selectedDate);

            if (!isDayInWorkDays(workDays, fullDayName, shortDayName)) {
                JOptionPane.showMessageDialog(this, "Dentist not available on " + fullDayName);
                timeCombo.removeAllItems();
                return;
            }

            if (!isDentistStatusBookingAllowed(status)) {
                String message = (status == null || status.trim().isEmpty())
                        ? "Dentist is not accepting appointments right now."
                        : "Dentist is currently " + status + ".";
                JOptionPane.showMessageDialog(this, message);
                timeCombo.removeAllItems();
                return;
            }

            // Populate timeCombo dynamically
            timeCombo.removeAllItems();
            java.time.LocalTime t = start;
            while (!t.isAfter(end)) {
                timeCombo.addItem(t.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));
                t = t.plusMinutes(30);
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading dentist schedule: " + ex.getMessage());
    }
}


private boolean isPersonalInfoValid() {
    String name = booking_fullname.getText().trim();     // Full Name
    String email = book_email.getText().trim();          // Email
    String age = age_spinner.getValue().toString().trim();
    String genderValue = (String) book_gender.getSelectedItem(); // Gender
    String phone = book_contact.getText().trim();        // Phone Number
    String street = streetAddress.getText().trim();      // Street Address
    String province = (String) streetprovince_combobox.getSelectedItem(); // Province

    // Ignore placeholders
    if (name.equals("Enter Full Name")) name = "";
    if (email.equals("Enter Email Address")) email = "";
    if (phone.equals("Enter Phone Number")) phone = "";
    if (street.equals("Enter Street Address")) street = "";
    
    
    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Full Name is required.");
        return false;
    }
    if (email.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Email is required.");
        return false;
    }
    if (age.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Age is required.");
        return false;
    }
    if (genderValue == null || genderValue.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Gender is required.");
        return false;
    }
    if (phone.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Phone Number is required.");
        return false;
    }
    if (street.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Street Address is required.");
        return false;
    }
    if (province == null || province.isEmpty()) {
        JOptionPane.showMessageDialog(this, "State/Province is required.");
        return false;
    }

    return true;
}
// Helper method to map service names to prices
private double getServicePrice(String service) {
    switch (service) {
        case "🦷 Dental Cleaning": return 1000;
        case "🩺 General Checkup": return 500;
        case "🦷 Dental Filling": return 2000;
        case "💰 Teeth Whitening": return 14000;
        case "🦷 Tooth Extraction": return 1500;
        case "🦷 Root Canal": return 8000;
        case "💎 Dental Crown": return 15000;
        case "👥 Braces Consultation": return 50000;
        default: return 0; // fallback if no match
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

        header = new javax.swing.JPanel();
        xpane = new javax.swing.JPanel();
        xbtn = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        hdrpic = new javax.swing.JLabel();
        taab = new javax.swing.JTabbedPane();
        bg = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        booking_fullname = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        book_email = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        book_contact = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        streetAddress = new javax.swing.JTextField();
        nextpane = new javax.swing.JPanel();
        nextbtn = new javax.swing.JLabel();
        nextsign = new javax.swing.JLabel();
        prevpane = new javax.swing.JPanel();
        prevbtn = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        book_gender = new javax.swing.JComboBox<>();
        age_spinner = new javax.swing.JSpinner();
        jLabel108 = new javax.swing.JLabel();
        streetprovince_combobox = new javax.swing.JComboBox<>();
        jLabel111 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        bg1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        nextpane1 = new javax.swing.JPanel();
        nextbtn_selectservicepart = new javax.swing.JLabel();
        nextsign1 = new javax.swing.JLabel();
        prevpane1 = new javax.swing.JPanel();
        prevbtn1 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        dc = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        check1 = new javax.swing.JLabel();
        dentalcheckup = new javax.swing.JLabel();
        gc = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        dentalcleaning = new javax.swing.JLabel();
        df = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        dentalfilling = new javax.swing.JLabel();
        tw = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        teethwhitening = new javax.swing.JLabel();
        te = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        toothextraction = new javax.swing.JLabel();
        rc = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        rootcanal = new javax.swing.JLabel();
        denc = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        dentalcrown = new javax.swing.JLabel();
        bc = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        braceconsultation = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        bg2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        nextpane2 = new javax.swing.JPanel();
        next_thisisSelectdentistPart = new javax.swing.JLabel();
        nextsign2 = new javax.swing.JLabel();
        prevpane2 = new javax.swing.JPanel();
        prevbtn2 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_dentist = new javax.swing.JTable();
        jLabel95 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        bg3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel90 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        nextpane3 = new javax.swing.JPanel();
        nextbtn3 = new javax.swing.JLabel();
        nextsign3 = new javax.swing.JLabel();
        prevpane3 = new javax.swing.JPanel();
        prevbtn3 = new javax.swing.JLabel();
        appointment_date = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        Jpanel_date_time = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        selectedDentist = new javax.swing.JTable();
        jLabel112 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        bg4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        nextpane4 = new javax.swing.JPanel();
        confirmBooking = new javax.swing.JLabel();
        nextsign4 = new javax.swing.JLabel();
        prevpane4 = new javax.swing.JPanel();
        prevbtn4 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        patientFullName = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        dentistNAME = new javax.swing.JLabel();
        dentistSpecialty = new javax.swing.JLabel();
        summaryFulldate = new javax.swing.JLabel();
        serviceSummary = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        moneytopay = new javax.swing.JLabel();
        cash_summary = new javax.swing.JRadioButton();
        debitcard_summary = new javax.swing.JRadioButton();
        gcash_summary = new javax.swing.JRadioButton();
        jPanel16 = new javax.swing.JPanel();
        amountToPay = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        header.setBackground(new java.awt.Color(255, 255, 255));
        header.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        xpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        xbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        xbtn.setText("X");
        xbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                xbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                xbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                xbtnMouseExited(evt);
            }
        });
        xpane.add(xbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 40, 30));

        header.add(xpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 40, 30));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/output-onlinepngtools__3_-removebg-preview.png"))); // NOI18N
        header.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jLabel12.setFont(new java.awt.Font("Trebuchet MS", 1, 15)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 255));
        jLabel12.setText("Book Now");
        header.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 80, 50));

        hdrpic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/5046faad4a4c9af72bcf4fe75c8a11d0.jpg"))); // NOI18N
        hdrpic.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                hdrpicMouseDragged(evt);
            }
        });
        hdrpic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                hdrpicMousePressed(evt);
            }
        });
        header.add(hdrpic, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 810, 60));

        getContentPane().add(header, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 50));

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(204, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 255, 255), 3));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setBackground(new java.awt.Color(153, 204, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 204, 255));
        jLabel3.setText("_____");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel4.setText("Serivice & Dentist");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, -1, -1));

        jLabel5.setBackground(new java.awt.Color(204, 204, 255));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 204, 255));
        jLabel5.setText("_____");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 60, -1));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel6.setText("Date & Time");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, -1, -1));

        jLabel7.setBackground(new java.awt.Color(153, 204, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 204, 255));
        jLabel7.setText("_____");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel8.setText("Billing");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, -1, -1));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-32.png"))); // NOI18N
        jPanel4.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 50, 40));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-clock-32.png"))); // NOI18N
        jPanel4.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 40, 40));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-billing-32.png"))); // NOI18N
        jPanel4.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 40, 40));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-32.png"))); // NOI18N
        jPanel11.add(jLabel10);

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel2.setText("Personal Info");
        jPanel11.add(jLabel2);

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(0, 102, 255));
        jLabel85.setText("____________");
        jPanel11.add(jLabel85);

        jPanel4.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 80));

        bg.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 550, 80));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel16.setText("Full Name");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 70, 30));

        booking_fullname.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(booking_fullname, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 250, 30));

        jLabel17.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel17.setText("Email");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, -1, 30));

        book_email.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(book_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 250, 30));

        jLabel18.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel18.setText("Gender");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, 50, 30));

        jLabel19.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel19.setText("Age");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 40, 30));

        jLabel20.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel20.setText("Phone Number");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 130, 30));

        book_contact.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(book_contact, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 510, 30));

        jLabel21.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel21.setText("Street Address");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 230, -1, 30));

        streetAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel1.add(streetAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, 250, 30));

        nextpane.setBackground(new java.awt.Color(0, 102, 255));
        nextpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        nextbtn.setForeground(new java.awt.Color(255, 255, 255));
        nextbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextbtn.setText("Next");
        nextbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextbtnMouseExited(evt);
            }
        });
        nextpane.add(nextbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        nextsign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-forward-24 (1).png"))); // NOI18N
        nextsign.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextsignMouseClicked(evt);
            }
        });
        nextpane.add(nextsign, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 30, 30));

        jPanel1.add(nextpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 320, 90, 30));

        prevpane.setBackground(new java.awt.Color(255, 255, 255));
        prevpane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 255)));
        prevpane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevpaneMouseEntered(evt);
            }
        });
        prevpane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        prevbtn.setBackground(new java.awt.Color(0, 0, 0));
        prevbtn.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        prevbtn.setForeground(new java.awt.Color(0, 102, 255));
        prevbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-back-24 (1).png"))); // NOI18N
        prevbtn.setText("Back ");
        prevbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevbtnMouseExited(evt);
            }
        });
        prevpane.add(prevbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel1.add(prevpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 100, 30));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 0, 0));
        jLabel25.setText("*");
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 30, 40));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(204, 0, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("*");
        jPanel1.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 30, 40));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(204, 0, 0));
        jLabel27.setText("*");
        jPanel1.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 40, 40));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(204, 0, 0));
        jLabel28.setText("*");
        jPanel1.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 80, -1, 30));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(204, 0, 0));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("*");
        jPanel1.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 30, 60));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(204, 0, 0));
        jLabel30.setText("*");
        jPanel1.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, 40, 50));

        book_gender.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        book_gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        book_gender.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        book_gender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                book_genderActionPerformed(evt);
            }
        });
        jPanel1.add(book_gender, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, 250, 30));
        jPanel1.add(age_spinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 70, 30));

        jLabel108.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        jLabel108.setText("State Province");
        jPanel1.add(jLabel108, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, 30));

        streetprovince_combobox.setFont(new java.awt.Font("Tw Cen MT", 0, 15)); // NOI18N
        streetprovince_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cebu", "Bohol", "Negros Oriental", "Leyte", "Others" }));
        streetprovince_combobox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(streetprovince_combobox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 262, 250, 30));

        jLabel111.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel111.setForeground(new java.awt.Color(204, 0, 0));
        jLabel111.setText("*");
        jPanel1.add(jLabel111, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 210, 40, 50));

        jLabel47.setBackground(new java.awt.Color(204, 255, 255));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jLabel47.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel1.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, 360));

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 550, 360));

        jLabel84.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel84.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jLabel84.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        bg.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 810, 470));

        taab.addTab("book1", bg);

        bg1.setBackground(new java.awt.Color(255, 255, 255));
        bg1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(204, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel9.setText("Personal Info");
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 80, -1));

        jLabel11.setBackground(new java.awt.Color(153, 204, 255));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(153, 204, 255));
        jLabel11.setText("_____");
        jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel5.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        jLabel23.setBackground(new java.awt.Color(204, 204, 255));
        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(153, 204, 255));
        jLabel23.setText("_____");
        jLabel23.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel5.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 60, -1));

        jLabel24.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel24.setText("Date & Time");
        jPanel5.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, -1, -1));

        jLabel31.setBackground(new java.awt.Color(153, 204, 255));
        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(153, 204, 255));
        jLabel31.setText("_____");
        jLabel31.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel5.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jLabel32.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel32.setText("Billing");
        jPanel5.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, -1, -1));

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-32.png"))); // NOI18N
        jPanel5.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, 40));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-clock-32.png"))); // NOI18N
        jPanel5.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 40, 40));

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-billing-32.png"))); // NOI18N
        jPanel5.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 40, 40));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-32.png"))); // NOI18N
        jPanel12.add(jLabel34);

        jLabel22.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel22.setText("Serivice & Dentist");
        jPanel12.add(jLabel22);

        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(0, 102, 255));
        jLabel86.setText("_______________");
        jPanel12.add(jLabel86);

        jPanel5.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 100, 80));

        bg1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 550, 80));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 80, -1));

        jLabel38.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel38.setText("Select Service");
        jPanel2.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, -1));

        nextpane1.setBackground(new java.awt.Color(0, 102, 255));
        nextpane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextbtn_selectservicepart.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        nextbtn_selectservicepart.setForeground(new java.awt.Color(255, 255, 255));
        nextbtn_selectservicepart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextbtn_selectservicepart.setText("Next");
        nextbtn_selectservicepart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextbtn_selectservicepartMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextbtn_selectservicepartMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextbtn_selectservicepartMouseExited(evt);
            }
        });
        nextpane1.add(nextbtn_selectservicepart, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

        nextsign1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-forward-24 (1).png"))); // NOI18N
        nextsign1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextsign1MouseClicked(evt);
            }
        });
        nextpane1.add(nextsign1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 30, 30));

        jPanel2.add(nextpane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 90, 30));

        prevpane1.setBackground(new java.awt.Color(255, 255, 255));
        prevpane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 255)));
        prevpane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        prevbtn1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        prevbtn1.setForeground(new java.awt.Color(0, 102, 255));
        prevbtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-back-24 (1).png"))); // NOI18N
        prevbtn1.setText("Previous");
        prevbtn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevbtn1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevbtn1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevbtn1MouseExited(evt);
            }
        });
        prevpane1.add(prevbtn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel2.add(prevpane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 100, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setText("*");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 40, 20));

        dc.setBackground(new java.awt.Color(255, 255, 255));
        dc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        dc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dcMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dcMouseExited(evt);
            }
        });
        dc.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel39.setFont(new java.awt.Font("Segoe UI Symbol", 0, 14)); // NOI18N
        jLabel39.setText("Dental Cleaning");
        dc.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 1, -1, 30));

        check1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        check1.setForeground(new java.awt.Color(0, 0, 255));
        check1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                check1MouseClicked(evt);
            }
        });
        dc.add(check1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 60, 20));

        dentalcheckup.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        dentalcheckup.setForeground(new java.awt.Color(0, 51, 255));
        dentalcheckup.setText("₱ 1,000");
        dentalcheckup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dentalcheckupKeyTyped(evt);
            }
        });
        dc.add(dentalcheckup, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 30));

        jPanel2.add(dc, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 150, 60));

        gc.setBackground(new java.awt.Color(255, 255, 255));
        gc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        gc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gcMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                gcMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                gcMouseExited(evt);
            }
        });
        gc.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setFont(new java.awt.Font("Segoe UI Symbol", 0, 14)); // NOI18N
        jLabel40.setText("General Checkup");
        gc.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 30));

        dentalcleaning.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        dentalcleaning.setForeground(new java.awt.Color(0, 51, 255));
        dentalcleaning.setText("₱ 500");
        dentalcleaning.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dentalcleaningKeyTyped(evt);
            }
        });
        gc.add(dentalcleaning, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 40, 30));

        jPanel2.add(gc, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, 150, 60));

        df.setBackground(new java.awt.Color(255, 255, 255));
        df.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        df.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dfMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dfMouseExited(evt);
            }
        });
        df.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel41.setFont(new java.awt.Font("Segoe UI Symbol", 0, 12)); // NOI18N
        jLabel41.setText("Dental Filling");
        df.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 110, 30));

        dentalfilling.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        dentalfilling.setForeground(new java.awt.Color(0, 51, 255));
        dentalfilling.setText("₱ 2,000");
        dentalfilling.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dentalfillingKeyTyped(evt);
            }
        });
        df.add(dentalfilling, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 30));

        jPanel2.add(df, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 150, 60));

        tw.setBackground(new java.awt.Color(255, 255, 255));
        tw.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        tw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                twMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                twMouseExited(evt);
            }
        });
        tw.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel42.setFont(new java.awt.Font("Segoe UI Symbol", 0, 14)); // NOI18N
        jLabel42.setText("Teeth Whitening");
        tw.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 1, -1, 30));

        teethwhitening.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        teethwhitening.setForeground(new java.awt.Color(0, 51, 255));
        teethwhitening.setText("₱ 14,000");
        teethwhitening.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teethwhiteningKeyTyped(evt);
            }
        });
        tw.add(teethwhitening, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 30));

        jPanel2.add(tw, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 150, 60));

        te.setBackground(new java.awt.Color(255, 255, 255));
        te.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        te.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                teMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                teMouseExited(evt);
            }
        });
        te.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel43.setFont(new java.awt.Font("Segoe UI Symbol", 0, 14)); // NOI18N
        jLabel43.setText("Tooth Extraction");
        te.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 30));

        toothextraction.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        toothextraction.setForeground(new java.awt.Color(0, 51, 255));
        toothextraction.setText("₱ 1,500");
        toothextraction.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                toothextractionKeyTyped(evt);
            }
        });
        te.add(toothextraction, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 30));

        jPanel2.add(te, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, 150, 60));

        rc.setBackground(new java.awt.Color(255, 255, 255));
        rc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        rc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rcMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                rcMouseExited(evt);
            }
        });
        rc.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel44.setFont(new java.awt.Font("Segoe UI Symbol", 0, 14)); // NOI18N
        jLabel44.setText("Root Canal");
        rc.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 30));

        rootcanal.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        rootcanal.setForeground(new java.awt.Color(0, 51, 255));
        rootcanal.setText("₱ 8,000");
        rootcanal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rootcanalKeyTyped(evt);
            }
        });
        rc.add(rootcanal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 30));

        jPanel2.add(rc, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, 150, 60));

        denc.setBackground(new java.awt.Color(255, 255, 255));
        denc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        denc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dencMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dencMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dencMouseExited(evt);
            }
        });
        denc.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel45.setFont(new java.awt.Font("Segoe UI Symbol", 0, 14)); // NOI18N
        jLabel45.setText("Dental Crown");
        denc.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 30));

        dentalcrown.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        dentalcrown.setForeground(new java.awt.Color(0, 51, 255));
        dentalcrown.setText("₱ 15,000");
        dentalcrown.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dentalcrownKeyTyped(evt);
            }
        });
        denc.add(dentalcrown, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 30));

        jPanel2.add(denc, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 150, 60));

        bc.setBackground(new java.awt.Color(255, 255, 255));
        bc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255), 2));
        bc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bcMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bcMouseExited(evt);
            }
        });
        bc.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel46.setFont(new java.awt.Font("Segoe UI Symbol", 0, 14)); // NOI18N
        jLabel46.setText("Brace Consultation");
        bc.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 30));

        braceconsultation.setFont(new java.awt.Font("Yu Gothic Medium", 0, 12)); // NOI18N
        braceconsultation.setForeground(new java.awt.Color(0, 51, 255));
        braceconsultation.setText("₱ 50,000");
        braceconsultation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                braceconsultationKeyTyped(evt);
            }
        });
        bc.add(braceconsultation, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 30));

        jPanel2.add(bc, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 200, 150, 60));

        jLabel89.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jLabel89.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(jLabel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, 350));

        bg1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 550, 350));

        jLabel88.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        bg1.add(jLabel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 810, 480));

        taab.addTab("book2", bg1);

        bg2.setBackground(new java.awt.Color(255, 255, 255));
        bg2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(204, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel48.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel48.setText("Personal Info");
        jPanel6.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 80, -1));

        jLabel49.setBackground(new java.awt.Color(153, 204, 255));
        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(153, 204, 255));
        jLabel49.setText("_____");
        jLabel49.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel6.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        jLabel51.setBackground(new java.awt.Color(204, 204, 255));
        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(153, 204, 255));
        jLabel51.setText("_____");
        jLabel51.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel6.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 60, -1));

        jLabel52.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel52.setText("Date & Time");
        jPanel6.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, -1, -1));

        jLabel53.setBackground(new java.awt.Color(153, 204, 255));
        jLabel53.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(153, 204, 255));
        jLabel53.setText("_____");
        jLabel53.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel6.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jLabel54.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel54.setText("Billing");
        jPanel6.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, -1, -1));

        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-32.png"))); // NOI18N
        jPanel6.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, 40));

        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-clock-32.png"))); // NOI18N
        jPanel6.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 40, 40));

        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-billing-32.png"))); // NOI18N
        jPanel6.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 40, 40));

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel50.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setText("Service & Dentist");
        jPanel13.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 100, 20));

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-32.png"))); // NOI18N
        jPanel13.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 40, 40));

        jLabel87.setForeground(new java.awt.Color(0, 51, 255));
        jLabel87.setText("______________");
        jLabel87.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel13.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 100, -1));

        jPanel6.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 100, 80));

        bg2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 550, 80));

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel59.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel59.setText("Select Dentist");
        jPanel3.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 100, -1));

        nextpane2.setBackground(new java.awt.Color(0, 153, 153));
        nextpane2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        next_thisisSelectdentistPart.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        next_thisisSelectdentistPart.setForeground(new java.awt.Color(255, 255, 255));
        next_thisisSelectdentistPart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        next_thisisSelectdentistPart.setText("Next");
        next_thisisSelectdentistPart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                next_thisisSelectdentistPartMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                next_thisisSelectdentistPartMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                next_thisisSelectdentistPartMouseExited(evt);
            }
        });
        nextpane2.add(next_thisisSelectdentistPart, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 30));

        nextsign2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-forward-24 (1).png"))); // NOI18N
        nextsign2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextsign2MouseClicked(evt);
            }
        });
        nextpane2.add(nextsign2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 30, 30));

        jPanel3.add(nextpane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 90, 30));

        prevpane2.setBackground(new java.awt.Color(255, 255, 255));
        prevpane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 255)));
        prevpane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevpane2MouseEntered(evt);
            }
        });
        prevpane2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        prevbtn2.setBackground(new java.awt.Color(0, 0, 0));
        prevbtn2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        prevbtn2.setForeground(new java.awt.Color(0, 102, 255));
        prevbtn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-back-24 (1).png"))); // NOI18N
        prevbtn2.setText("Back ");
        prevbtn2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevbtn2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevbtn2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevbtn2MouseExited(evt);
            }
        });
        prevpane2.add(prevbtn2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 90, 30));

        jPanel3.add(prevpane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 100, 30));

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(255, 0, 0));
        jLabel60.setText("*");
        jPanel3.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 20, 40));

        table_dentist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table_dentist);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 530, 220));

        jLabel95.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jLabel95.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel3.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 550, 360));

        bg2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 550, -1));

        jLabel94.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        bg2.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, -10, 810, 480));

        taab.addTab("book3", bg2);

        bg3.setBackground(new java.awt.Color(255, 255, 255));
        bg3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(204, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel61.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel61.setText("Personal Info");
        jPanel7.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 80, -1));

        jLabel62.setBackground(new java.awt.Color(153, 204, 255));
        jLabel62.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(153, 204, 255));
        jLabel62.setText("_____");
        jLabel62.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel7.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        jLabel63.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel63.setText("Serivice & Dentist");
        jPanel7.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, -1, -1));

        jLabel64.setBackground(new java.awt.Color(204, 204, 255));
        jLabel64.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(153, 204, 255));
        jLabel64.setText("_____");
        jLabel64.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel7.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 60, -1));

        jLabel65.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel65.setText("Date & Time");
        jPanel7.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, 70, -1));

        jLabel66.setBackground(new java.awt.Color(153, 204, 255));
        jLabel66.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(153, 204, 255));
        jLabel66.setText("_____");
        jLabel66.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel7.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jLabel67.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel67.setText("Billing");
        jPanel7.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, -1, -1));

        jLabel68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-32.png"))); // NOI18N
        jPanel7.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, 40));

        jLabel69.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-32.png"))); // NOI18N
        jPanel7.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 50, 40));

        jLabel70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-clock-32.png"))); // NOI18N
        jPanel7.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 40, 40));

        jLabel71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-billing-32.png"))); // NOI18N
        jPanel7.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 40, 40));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel90.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel90.setForeground(new java.awt.Color(0, 51, 255));
        jLabel90.setText("________");
        jLabel90.setToolTipText("");
        jPanel14.add(jLabel90, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, -1, 20));

        jPanel7.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 72, 80));

        bg3.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 550, 80));

        jPanel8.setBackground(new java.awt.Color(204, 255, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextpane3.setBackground(new java.awt.Color(0, 153, 153));
        nextpane3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextbtn3.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        nextbtn3.setForeground(new java.awt.Color(255, 255, 255));
        nextbtn3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextbtn3.setText("Next");
        nextbtn3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextbtn3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextbtn3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextbtn3MouseExited(evt);
            }
        });
        nextpane3.add(nextbtn3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 30));

        nextsign3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-forward-24 (1).png"))); // NOI18N
        nextsign3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextsign3MouseClicked(evt);
            }
        });
        nextpane3.add(nextsign3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 30, 30));

        jPanel8.add(nextpane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 90, 30));

        prevpane3.setBackground(new java.awt.Color(255, 255, 255));
        prevpane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 255)));
        prevpane3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevpane3MouseEntered(evt);
            }
        });
        prevpane3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        prevbtn3.setBackground(new java.awt.Color(0, 0, 0));
        prevbtn3.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        prevbtn3.setForeground(new java.awt.Color(0, 102, 255));
        prevbtn3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevbtn3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-back-24 (1).png"))); // NOI18N
        prevbtn3.setText("Back ");
        prevbtn3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevbtn3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevbtn3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevbtn3MouseExited(evt);
            }
        });
        prevpane3.add(prevbtn3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        jPanel8.add(prevpane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 100, 30));

        appointment_date.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        appointment_date.setText("Select Appointment Date & Time");
        jPanel8.add(appointment_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 250, 30));

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(255, 0, 0));
        jLabel72.setText("*");
        jPanel8.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 40, 30));
        jPanel8.add(Jpanel_date_time, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 140, 380, 40));

        selectedDentist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(selectedDentist);

        jPanel8.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 490, 60));

        jLabel112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jLabel112.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel8.add(jLabel112, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, 350));

        jLabel110.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jPanel8.add(jLabel110, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 550, 350));

        bg3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 550, 350));

        jLabel93.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel93.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        bg3.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 810, 480));

        taab.addTab("book4", bg3);

        bg4.setBackground(new java.awt.Color(255, 255, 255));
        bg4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(204, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel73.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel73.setText("Personal Info");
        jPanel9.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 80, -1));

        jLabel74.setBackground(new java.awt.Color(153, 204, 255));
        jLabel74.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(153, 204, 255));
        jLabel74.setText("_____");
        jLabel74.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel9.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        jLabel75.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel75.setText("Serivice & Dentist");
        jPanel9.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, -1, -1));

        jLabel76.setBackground(new java.awt.Color(204, 204, 255));
        jLabel76.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(153, 204, 255));
        jLabel76.setText("_____");
        jLabel76.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel9.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 60, -1));

        jLabel77.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel77.setText("Date & Time");
        jPanel9.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, -1, -1));

        jLabel78.setBackground(new java.awt.Color(153, 204, 255));
        jLabel78.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel78.setForeground(new java.awt.Color(153, 204, 255));
        jLabel78.setText("_____");
        jLabel78.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel9.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jLabel79.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel79.setText("Billing");
        jPanel9.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(484, 50, 60, -1));

        jLabel80.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-user-32.png"))); // NOI18N
        jPanel9.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, 40));

        jLabel81.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-calendar-32.png"))); // NOI18N
        jPanel9.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 50, 40));

        jLabel82.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-clock-32.png"))); // NOI18N
        jPanel9.add(jLabel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 40, 40));

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel83.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-billing-32.png"))); // NOI18N
        jPanel15.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 5, -1, -1));

        jLabel91.setForeground(new java.awt.Color(0, 51, 255));
        jLabel91.setText("__________");
        jPanel15.add(jLabel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 70, -1));

        jPanel9.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 0, 70, 80));

        bg4.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 550, 80));

        jPanel10.setBackground(new java.awt.Color(204, 255, 255));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextpane4.setBackground(new java.awt.Color(0, 153, 153));
        nextpane4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        confirmBooking.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        confirmBooking.setForeground(new java.awt.Color(255, 255, 255));
        confirmBooking.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        confirmBooking.setText("Confirm Booking");
        confirmBooking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                confirmBookingMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                confirmBookingMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                confirmBookingMouseExited(evt);
            }
        });
        nextpane4.add(confirmBooking, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 120, 30));

        nextsign4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-done-20.png"))); // NOI18N
        nextsign4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextsign4MouseClicked(evt);
            }
        });
        nextpane4.add(nextsign4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        jPanel10.add(nextpane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 310, 160, 30));

        prevpane4.setBackground(new java.awt.Color(255, 255, 255));
        prevpane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 255)));
        prevpane4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevpane4MouseEntered(evt);
            }
        });
        prevpane4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        prevbtn4.setBackground(new java.awt.Color(0, 0, 0));
        prevbtn4.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        prevbtn4.setForeground(new java.awt.Color(0, 102, 255));
        prevbtn4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-back-24 (1).png"))); // NOI18N
        prevbtn4.setText("Previous");
        prevbtn4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevbtn4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevbtn4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevbtn4MouseExited(evt);
            }
        });
        prevpane4.add(prevbtn4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 90, 30));

        jPanel10.add(prevpane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 100, 30));

        jLabel96.setForeground(new java.awt.Color(204, 204, 204));
        jLabel96.setText("_________________________________________________________________________");
        jPanel10.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, 20));

        jLabel98.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(204, 204, 204));
        jLabel98.setText("________________________________________________________________________");
        jPanel10.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 520, 20));

        jLabel99.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel99.setForeground(new java.awt.Color(0, 51, 102));
        jLabel99.setText("Payment Method");
        jPanel10.add(jLabel99, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, 110, 30));

        jLabel100.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel100.setForeground(new java.awt.Color(0, 51, 102));
        jLabel100.setText("Treatment/Service Summary");
        jPanel10.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 220, 40));

        jLabel101.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel101.setForeground(new java.awt.Color(0, 51, 102));
        jLabel101.setText("Ammount to Pay:");
        jPanel10.add(jLabel101, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 110, 30));

        jLabel102.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel102.setForeground(new java.awt.Color(0, 51, 102));
        jLabel102.setText("Patient Info");
        jPanel10.add(jLabel102, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        patientFullName.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        patientFullName.setForeground(new java.awt.Color(0, 51, 102));
        patientFullName.setText("jLabel103");
        jPanel10.add(patientFullName, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 40));

        jLabel104.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel104.setForeground(new java.awt.Color(0, 51, 102));
        jLabel104.setText("Dentist & Appointment Info");
        jPanel10.add(jLabel104, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, 20));

        dentistNAME.setForeground(new java.awt.Color(0, 51, 102));
        dentistNAME.setText("Dentist Name");
        jPanel10.add(dentistNAME, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 150, 30));

        dentistSpecialty.setForeground(new java.awt.Color(0, 51, 102));
        dentistSpecialty.setText("Specialty");
        jPanel10.add(dentistSpecialty, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 110, 30));

        summaryFulldate.setForeground(new java.awt.Color(0, 51, 102));
        summaryFulldate.setText("Date");
        jPanel10.add(summaryFulldate, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 220, 30));

        serviceSummary.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        serviceSummary.setForeground(new java.awt.Color(0, 51, 102));
        serviceSummary.setText("Services");
        jPanel10.add(serviceSummary, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, 160, 40));

        jLabel97.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel97.setForeground(new java.awt.Color(0, 51, 102));
        jLabel97.setText("Subtotal");
        jPanel10.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 80, -1, -1));

        moneytopay.setForeground(new java.awt.Color(0, 51, 102));
        moneytopay.setText("moneytopay");
        jPanel10.add(moneytopay, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, -1, 30));

        cash_summary.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        cash_summary.setForeground(new java.awt.Color(0, 51, 102));
        cash_summary.setText("Cash");
        cash_summary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cash_summaryActionPerformed(evt);
            }
        });
        jPanel10.add(cash_summary, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, -1, -1));

        debitcard_summary.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        debitcard_summary.setForeground(new java.awt.Color(0, 51, 102));
        debitcard_summary.setText("Debit Card");
        jPanel10.add(debitcard_summary, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, -1, -1));

        gcash_summary.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        gcash_summary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gcash_summaryActionPerformed(evt);
            }
        });
        jPanel10.add(gcash_summary, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 220, -1, -1));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        amountToPay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        amountToPay.setText("jLabel103");
        jPanel16.add(amountToPay, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 30));

        jPanel10.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 260, 160, 30));

        jLabel105.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/OIP (1).jpg"))); // NOI18N
        jPanel10.add(jLabel105, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 210, 30, 40));

        jLabel106.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel106.setForeground(new java.awt.Color(0, 51, 102));
        jLabel106.setText("GCash");
        jPanel10.add(jLabel106, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 220, 60, 20));

        jLabel109.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/nn.jpg"))); // NOI18N
        jPanel10.add(jLabel109, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -14, 550, 360));

        bg4.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 94, 550, -1));

        jLabel92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jLabel92.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        bg4.add(jLabel92, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 810, 480));

        taab.addTab("book5", bg4);

        getContentPane().add(taab, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 810, 500));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void hdrpicMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrpicMouseDragged
               int x = evt.getXOnScreen();
               int y = evt.getYOnScreen();
               this.setLocation(x - xMouse,y - yMouse);
    }//GEN-LAST:event_hdrpicMouseDragged

    private void hdrpicMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrpicMousePressed
      xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_hdrpicMousePressed

    private void xbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xbtnMouseClicked
      if (closeOnlyMode) {
          dispose();
          return;
      }
      System.exit(0);
    }//GEN-LAST:event_xbtnMouseClicked

    private void xbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xbtnMouseEntered
       xpane.setBackground(Color. red);
        xbtn.setForeground(Color.white);
    }//GEN-LAST:event_xbtnMouseEntered

    private void xbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xbtnMouseExited
         xpane.setBackground(Color. white);
         xbtn.setForeground(Color.black);
    }//GEN-LAST:event_xbtnMouseExited

    private void prevpane4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevpane4MouseEntered

    }//GEN-LAST:event_prevpane4MouseEntered

    private void prevbtn4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn4MouseExited
        prevbtn4.setForeground(new Color(0,102,255)); // original color
    }//GEN-LAST:event_prevbtn4MouseExited

    private void prevbtn4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn4MouseEntered
        Color baseColor =( new Color(0,0,0));
        prevbtn4.setForeground(new Color(0,0,0)); // darker on hover
    }//GEN-LAST:event_prevbtn4MouseEntered

    private void prevbtn4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn4MouseClicked
        taab.setSelectedIndex(3);
    }//GEN-LAST:event_prevbtn4MouseClicked

    private void nextsign4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextsign4MouseClicked

    }//GEN-LAST:event_nextsign4MouseClicked

    private void confirmBookingMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_confirmBookingMouseExited
        nextpane4.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_confirmBookingMouseExited

    private void confirmBookingMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_confirmBookingMouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane4.setBackground(new Color(0,102,102)); // darker on hover
    }//GEN-LAST:event_confirmBookingMouseEntered

    private void confirmBookingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_confirmBookingMouseClicked
   String paymentMethod = "";
    String paymentStatus = "Unpaid";

    if (cash_summary.isSelected()) {
        paymentMethod = "Cash";
        paymentStatus = "Unpaid";
    } else if (debitcard_summary.isSelected()) {
        paymentMethod = "Debit Card";
        paymentStatus = "Paid";
    } else if (gcash_summary.isSelected()) {
        paymentMethod = "GCash";
        paymentStatus = "Paid";
    } else {
        JOptionPane.showMessageDialog(this, "Please select a payment method.");
        return;
    }

    try (Connection conn = config.connectDB()) {
        String sql = "INSERT INTO tbl_appointments " +
                     "(pat_id, dentist_id, app_date, app_time, app_service, app_service_price, app_status, payment_method, payment_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pst = conn.prepareStatement(sql);

        int patId = getOrCreatePatientId(
            patientFullName.getText(),
            book_email.getText(),
            book_contact.getText(),
            age_spinner.getValue().toString(),
            (String) book_gender.getSelectedItem(),
            streetAddress.getText(),
            (String) streetprovince_combobox.getSelectedItem(),
            conn
        );

        pst.setInt(1, patId);
        pst.setInt(2, internal.session.getDentistId());
        pst.setString(3, new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate()));
        pst.setString(4, (String) timeCombo.getSelectedItem());
        pst.setString(5, serviceSummary.getText());
        pst.setDouble(6, getServicePrice(serviceSummary.getText()));
        pst.setString(7, "Scheduled");
        pst.setString(8, paymentMethod);
        pst.setString(9, paymentStatus);

        pst.executeUpdate();

        logAction(conn, internal.session.getId(), internal.session.getRole(),
                  "Book Appointment",
                  "Booked " + serviceSummary.getText() + " with dentist " + dentistNAME.getText() +
                  " on " + summaryFulldate.getText() + " using " + paymentMethod);

        JOptionPane.showMessageDialog(this, "Booking confirmed and saved!");

        // ✅ Clear all fields
        booking_fullname.setText("");
        book_email.setText("");
        book_contact.setText("");
        streetAddress.setText("");
        age_spinner.setValue(0);
        book_gender.setSelectedIndex(-1);
        streetprovince_combobox.setSelectedIndex(-1);

        selectedService = null;
        selectedPrice = null;
        serviceSummary.setText("");
        moneytopay.setText("");

        dateChooser.setDate(null);
        timeCombo.removeAllItems();
        summaryFulldate.setText("");

        dentistNAME.setText("");
        dentistSpecialty.setText("");
        selectedDentist.setModel(new javax.swing.table.DefaultTableModel());

        cash_summary.setSelected(false);
        debitcard_summary.setSelected(false);
        gcash_summary.setSelected(false);

        patientFullName.setText("");
        amountToPay.setText("");

        if (closeOnlyMode) {
            dispose();
            return;
        }

        // ✅ Return to Tab 0 (Personal Info)
        taab.setSelectedIndex(0);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error saving appointment: " + ex.getMessage());
    }
    }//GEN-LAST:event_confirmBookingMouseClicked

    private void prevpane3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevpane3MouseEntered

    }//GEN-LAST:event_prevpane3MouseEntered

    private void prevbtn3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn3MouseExited
        prevbtn3.setForeground(new Color(0,102,255)); // original color
    }//GEN-LAST:event_prevbtn3MouseExited

    private void prevbtn3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn3MouseEntered
        Color baseColor =( new Color(0,0,0));
        prevbtn3.setForeground(new Color(0,0,0)); // darker on hover
    }//GEN-LAST:event_prevbtn3MouseEntered

    private void prevbtn3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn3MouseClicked
        taab.setSelectedIndex(2);
    }//GEN-LAST:event_prevbtn3MouseClicked

    private void nextsign3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextsign3MouseClicked

    }//GEN-LAST:event_nextsign3MouseClicked

    private void nextbtn3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn3MouseExited
        nextpane3.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_nextbtn3MouseExited

    private void nextbtn3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn3MouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane3.setBackground(new Color(0,102,102)); // darker on hover
    }//GEN-LAST:event_nextbtn3MouseEntered

    private void nextbtn3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn3MouseClicked
    Date selectedDate = dateChooser.getDate();
    String selectedTime = (String) timeCombo.getSelectedItem();

    if (selectedDate == null) {
        JOptionPane.showMessageDialog(this, "Please select appointment date!");
        return;
    }
    if (selectedTime == null) {
        JOptionPane.showMessageDialog(this, "Please select appointment time!");
        return;
    }

    // Combine date + time
    Calendar cal = Calendar.getInstance();
    cal.setTime(selectedDate);

    String[] parts = selectedTime.split(":|\\s"); // e.g. "09:30 AM"
    int hour = Integer.parseInt(parts[0]);
    int minute = Integer.parseInt(parts[1]);
    if (parts[2].equalsIgnoreCase("PM") && hour != 12) hour += 12;
    if (parts[2].equalsIgnoreCase("AM") && hour == 12) hour = 0;

    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, 0);

    Date appointmentDateTime = cal.getTime(); // ready to save in DB

    // No appointment is saved yet. This step only prepares the billing summary.
    if (selectedService == null || selectedService.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a service before proceeding.");
        return;
    }
    if (internal.session.getDentistId() == 0) {
        JOptionPane.showMessageDialog(this, "Please select a dentist before proceeding.");
        return;
    }

    String fullName = booking_fullname.getText().trim();
    if (fullName.equals("Enter Full Name")) {
        fullName = "";
    }
    patientFullName.setText(fullName);
    serviceSummary.setText(selectedService);

    String dentist = selectedDentist.getValueAt(0, 0).toString(); // Column 0 = Dentist Name
    String specialty = selectedDentist.getValueAt(0, 1).toString(); // Column 1 = Specialty

    dentistNAME.setText(dentist);
    dentistSpecialty.setText(specialty);
    summaryFulldate.setText(
        new java.text.SimpleDateFormat("MMMM dd, yyyy hh:mm a").format(appointmentDateTime)
    );

    double priceValue = getServicePrice(selectedService);
    NumberFormat pesoFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    String formattedPrice = pesoFormat.format(priceValue);
    moneytopay.setText(formattedPrice);
    amountToPay.setText(formattedPrice);

    taab.setSelectedIndex(4); // move to Billing tab
    }//GEN-LAST:event_nextbtn3MouseClicked

    private void prevpane2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevpane2MouseEntered

    }//GEN-LAST:event_prevpane2MouseEntered

    private void prevbtn2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn2MouseExited
        prevbtn2.setForeground(new Color(0,102,255)); // original color
    }//GEN-LAST:event_prevbtn2MouseExited

    private void prevbtn2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn2MouseEntered
        Color baseColor =( new Color(0,0,0));
        prevbtn2.setForeground(new Color(0,0,0)); // darker on hover
    }//GEN-LAST:event_prevbtn2MouseEntered

    private void prevbtn2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn2MouseClicked
        taab.setSelectedIndex(1);
    }//GEN-LAST:event_prevbtn2MouseClicked

    private void nextsign2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextsign2MouseClicked

    }//GEN-LAST:event_nextsign2MouseClicked

    private void next_thisisSelectdentistPartMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_next_thisisSelectdentistPartMouseExited
        nextpane2.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_next_thisisSelectdentistPartMouseExited

    private void next_thisisSelectdentistPartMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_next_thisisSelectdentistPartMouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane2.setBackground(new Color(0,102,102)); // darker on hover
    }//GEN-LAST:event_next_thisisSelectdentistPartMouseEntered

    private void next_thisisSelectdentistPartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_next_thisisSelectdentistPartMouseClicked
          if (selectedService == null || selectedService.isEmpty() ||
        selectedPrice == null || selectedPrice.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a service before proceeding.");
        return;
    }

    if (internal.session.getDentistId() == 0) {
        JOptionPane.showMessageDialog(this, "Please select a dentist before proceeding.");
        return;
    }

    // ✅ If both are set, move to Date & Time tab
    taab.setSelectedIndex(3);
    }//GEN-LAST:event_next_thisisSelectdentistPartMouseClicked

    private void bcMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bcMouseExited
        bc.setBackground(new Color(255, 255, 255));

        // Original light border color
        bc.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_bcMouseExited

    private void bcMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bcMouseEntered
        bc.setOpaque(true);

        // Light blue background
        bc.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        bc.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_bcMouseEntered

    private void braceconsultationKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_braceconsultationKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_braceconsultationKeyTyped

    private void dencMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dencMouseExited
        denc.setBackground(new Color(255, 255, 255));

        // Original light border color
        denc.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_dencMouseExited

    private void dencMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dencMouseEntered
        denc.setOpaque(true);

        // Light blue background
        denc.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        denc.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_dencMouseEntered

    private void dencMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dencMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_dencMouseClicked

    private void dentalcrownKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dentalcrownKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_dentalcrownKeyTyped

    private void rcMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rcMouseExited
        rc.setBackground(new Color(255, 255, 255));

        // Original light border color
        rc.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_rcMouseExited

    private void rcMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rcMouseEntered
        rc.setOpaque(true);

        // Light blue background
        rc.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        rc.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_rcMouseEntered

    private void rootcanalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rootcanalKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_rootcanalKeyTyped

    private void teMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teMouseExited
        te.setBackground(new Color(255, 255, 255));

        // Original light border color
        te.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_teMouseExited

    private void teMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teMouseEntered
        te.setOpaque(true);

        // Light blue background
        te.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        te.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_teMouseEntered

    private void toothextractionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_toothextractionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_toothextractionKeyTyped

    private void twMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_twMouseExited
        tw.setBackground(new Color(255, 255, 255));

        // Original light border color
        tw.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_twMouseExited

    private void twMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_twMouseEntered
        tw.setOpaque(true);

        // Light blue background
        tw.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        tw.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_twMouseEntered

    private void teethwhiteningKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teethwhiteningKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_teethwhiteningKeyTyped

    private void dfMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dfMouseExited
        df.setBackground(new Color(255, 255, 255));

        // Original light border color
        df.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_dfMouseExited

    private void dfMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dfMouseEntered
        df.setOpaque(true);

        // Light blue background
        df.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        df.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_dfMouseEntered

    private void dentalfillingKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dentalfillingKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_dentalfillingKeyTyped

    private void gcMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gcMouseExited
        gc.setBackground(new Color(255, 255, 255));

        // Original light border color
        gc.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_gcMouseExited

    private void gcMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gcMouseEntered
        gc.setOpaque(true);

        // Light blue background
        gc.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        gc.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_gcMouseEntered

    private void gcMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gcMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_gcMouseClicked

    private void dentalcleaningKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dentalcleaningKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_dentalcleaningKeyTyped

    private void dcMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dcMouseExited
        // Original white background
        dc.setBackground(new Color(255, 255, 255));

        // Original light border color
        dc.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 255), 1));
    }//GEN-LAST:event_dcMouseExited

    private void dcMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dcMouseEntered

        dc.setOpaque(true);

        // Light blue background
        dc.setBackground(new Color(220, 230, 255));

        // Blue border (highlight)
        dc.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
    }//GEN-LAST:event_dcMouseEntered

    private void dentalcheckupKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dentalcheckupKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_dentalcheckupKeyTyped

    private void check1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_check1MouseClicked

    }//GEN-LAST:event_check1MouseClicked

    private void prevbtn1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn1MouseExited
        prevbtn1.setForeground(new Color(0,102,255)); // original color
    }//GEN-LAST:event_prevbtn1MouseExited

    private void prevbtn1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn1MouseEntered
        Color baseColor =( new Color(0,0,0));
        prevbtn1.setForeground(new Color(0,0,0)); // darker on hover
    }//GEN-LAST:event_prevbtn1MouseEntered

    private void prevbtn1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtn1MouseClicked
        taab.setSelectedIndex(0);
    }//GEN-LAST:event_prevbtn1MouseClicked

    private void nextsign1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextsign1MouseClicked

    }//GEN-LAST:event_nextsign1MouseClicked

    private void nextbtn_selectservicepartMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn_selectservicepartMouseExited
        nextpane1.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_nextbtn_selectservicepartMouseExited

    private void nextbtn_selectservicepartMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn_selectservicepartMouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane1.setBackground(new Color(0,102,102)); // darker on hover
    }//GEN-LAST:event_nextbtn_selectservicepartMouseEntered

    private void nextbtn_selectservicepartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn_selectservicepartMouseClicked
        if (selectedService == null || selectedService.isEmpty() ||
            selectedPrice == null || selectedPrice.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a service before proceeding.");
            return;
        }

        // Move to dentist selection next
        taab.setSelectedIndex(2);
    }//GEN-LAST:event_nextbtn_selectservicepartMouseClicked

    private void book_genderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_book_genderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_book_genderActionPerformed

    private void prevpaneMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevpaneMouseEntered

    }//GEN-LAST:event_prevpaneMouseEntered

    private void prevbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtnMouseExited
        prevbtn.setForeground(new Color(0,102,255)); // original color
    }//GEN-LAST:event_prevbtnMouseExited

    private void prevbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtnMouseEntered
        Color baseColor =( new Color(0,0,0));
        prevbtn.setForeground(new Color(0,0,0)); // darker on hover

    }//GEN-LAST:event_prevbtnMouseEntered

    private void prevbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevbtnMouseClicked
        handleBookingWindowClose();
    }//GEN-LAST:event_prevbtnMouseClicked

    private void nextsignMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextsignMouseClicked

    }//GEN-LAST:event_nextsignMouseClicked

    private void nextbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtnMouseExited
        nextpane.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_nextbtnMouseExited

    private void nextbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtnMouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane.setBackground(new Color(0,102,102)); // darker on hover

    }//GEN-LAST:event_nextbtnMouseEntered

    private void nextbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtnMouseClicked
   if (isPersonalInfoValid()) {
        // ✅ Collect street + province
        String street = streetAddress.getText().trim();
        String province = streetprovince_combobox.getSelectedItem().toString();
        String fullAddress = street + ", " + province;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dentalcare.db")) {

            // --- Save patient info ---
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tbl_patients (pat_name, pat_email, pat_age, pat_sex, pat_contact, pat_address, pat_archive) VALUES (?, ?, ?, ?, ?, ?, 0)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, booking_fullname.getText().trim());
            ps.setString(2, book_email.getText().trim());
            ps.setInt(3, (Integer) age_spinner.getValue());
            ps.setString(4, book_gender.getSelectedItem().toString());
            ps.setString(5, book_contact.getText().trim());
            ps.setString(6, fullAddress);
            ps.executeUpdate();

            // --- Get generated patient ID ---
            int patientId = -1;
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                patientId = rs.getInt(1);
            }
            rs.close();
            ps.close();

            // ✅ Log patient registration
            logAction(conn, patientId, "patient", "Register Patient",
                      "Registered new patient: " + booking_fullname.getText().trim() +
                      " (ID=" + patientId + "), email=" + book_email.getText().trim());

            JOptionPane.showMessageDialog(this, "Patient saved successfully!");
            
            // ✅ Move to Service & Dentist tab
            taab.setSelectedIndex(1); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving patient/appointment: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(
            this,
            "Please fill in all required fields before proceeding.",
            "Missing Information",
            JOptionPane.WARNING_MESSAGE
        );
    }
    }//GEN-LAST:event_nextbtnMouseClicked

    private void cash_summaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cash_summaryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cash_summaryActionPerformed

    private void gcash_summaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gcash_summaryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gcash_summaryActionPerformed
private void loadDentistData() {
    String sql = "SELECT " +
                 "'Dr. ' || a.acc_name AS 'Dentist Name', " +
                 "d.specialty AS 'Specialty', " +
                 "d.work_days AS 'Work Days', " +
                 "d.work_start AS 'Start Time', " +
                 "d.work_end AS 'End Time', " +
                 "CASE " +
                 "   WHEN d.dentist_stat IS NOT NULL AND d.dentist_stat != '' THEN d.dentist_stat " +
                 "   WHEN a.acc_status = 1 THEN 'Available' " +
                 "   ELSE 'Unavailable' " +
                 "END AS 'Status' " +
                 "FROM tbl_dentists d " +
                 "JOIN tbl_accounts a ON d.acc_id = a.acc_id " +
                 "WHERE a.acc_role = 'dentist'";

    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        // ✅ Load DB results into table
        table_dentist.setModel(DbUtils.resultSetToTableModel(rs));

        // ✅ Apply styling
        table_dentist.setRowHeight(30);
        table_dentist.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table_dentist.setGridColor(new Color(220, 220, 220));
        table_dentist.setShowGrid(true);

        // ✅ Header styling
        JTableHeader header = table_dentist.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(180, 220, 240)); // soft clinic blue
        header.setForeground(Color.DARK_GRAY);

        // ✅ Disable auto-resize so scrollbars appear
        table_dentist.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // ✅ Column widths (ensures long text is scrollable)
        if (table_dentist.getColumnCount() > 0) {
            table_dentist.getColumnModel().getColumn(0).setPreferredWidth(180); // Dentist Name
            table_dentist.getColumnModel().getColumn(1).setPreferredWidth(200); // Specialty
            table_dentist.getColumnModel().getColumn(2).setPreferredWidth(250); // Work Days
            table_dentist.getColumnModel().getColumn(3).setPreferredWidth(120); // Start Time
            table_dentist.getColumnModel().getColumn(4).setPreferredWidth(120); // End Time
            table_dentist.getColumnModel().getColumn(5).setPreferredWidth(150); // Status
        }

        // ✅ Custom renderer for alternating row colors + status highlighting
        table_dentist.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Alternate row colors
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 250, 255));
                    c.setForeground(new Color(30, 30, 30));
                } else {
                    c.setBackground(new Color(184, 207, 229));
                    c.setForeground(Color.BLACK);
                }

                // Status column color coding
                int statusCol = table.getColumn("Status").getModelIndex();
                if (column == statusCol && value != null) {
                    String status = value.toString();
                    if ("Available".equals(status)) {
                        c.setForeground(new Color(0, 128, 0)); // green
                        ((JLabel)c).setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else if ("Unavailable".equals(status)) {
                        c.setForeground(Color.RED);
                        ((JLabel)c).setFont(new Font("Segoe UI", Font.BOLD, 14));
                    }
                }

                return c;
            }
        });

        // ✅ Ensure scrollbars are always visible
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error loading dentist data: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}


private void styleSummaryLabels() {
    // Clinic summary style
    Font summaryFont = new Font("Segoe UI", Font.BOLD, 16);
    Color summaryColor = new Color(0, 51, 102); // deep navy

    patientFullName.setFont(summaryFont);
    patientFullName.setForeground(summaryColor);

    serviceSummary.setFont(summaryFont);
    serviceSummary.setForeground(summaryColor);

    dentistNAME.setFont(summaryFont);
    dentistNAME.setForeground(summaryColor);

    dentistSpecialty.setFont(summaryFont);
    dentistSpecialty.setForeground(summaryColor);

    summaryFulldate.setFont(summaryFont);
    summaryFulldate.setForeground(summaryColor);

    // Amount to pay should stand out more
    Font amountFont = new Font("Segoe UI", Font.BOLD, 20);
    Color amountColor = new Color(0, 123, 255); // dental blue highlight

    moneytopay.setFont(amountFont);
    moneytopay.setForeground(amountColor);

    amountToPay.setFont(amountFont);
    amountToPay.setForeground(amountColor);
}


private String getDentistSpecialty(int dentistId) {
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:dentalcare.db")) {
        PreparedStatement pst = conn.prepareStatement(
            "SELECT specialty FROM tbl_dentists WHERE dentist_id=?"
        );
        pst.setInt(1, dentistId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getString("specialty");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "";
}
private int getOrCreatePatientId(String name, String email, String phone, String age,
                                 String gender, String street, String province, Connection conn) throws SQLException {
    // Check if patient already exists
    String checkSql = "SELECT pat_id FROM tbl_patients WHERE pat_email=?";
    PreparedStatement checkPst = conn.prepareStatement(checkSql);
    checkPst.setString(1, email);
    ResultSet rs = checkPst.executeQuery();
    if (rs.next()) {
        return rs.getInt("pat_id");
    }

    // Insert new patient
    String insertSql = "INSERT INTO tbl_patients (pat_name, pat_email, pat_age, pat_sex, pat_contact, pat_address, customer_id, pat_archive) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement insertPst = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
    insertPst.setString(1, name);
    insertPst.setString(2, email);
    insertPst.setInt(3, Integer.parseInt(age));
    insertPst.setString(4, gender);
    insertPst.setString(5, phone);
    insertPst.setString(6, street + ", " + province);
    insertPst.setInt(7, 1); // dummy customer_id
    insertPst.setInt(8, 0); // not archived
    insertPst.executeUpdate();

    ResultSet genKeys = insertPst.getGeneratedKeys();
    if (genKeys.next()) {
        return genKeys.getInt(1);
    }
    throw new SQLException("Failed to insert patient");
}

    private boolean isDentistStatusBookingAllowed(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        String normalized = status.trim().toLowerCase();
        return normalized.equals("available")
            || normalized.equals("confirmed")
            || normalized.equals("active")
            || normalized.equals("on duty")
            || normalized.equals("ready");
    }

    private boolean isDayInWorkDays(String workDays, String fullDayName, String shortDayName) {
        if (workDays == null || workDays.trim().isEmpty()) {
            return false;
        }
        String normalized = workDays.toLowerCase();
        return normalized.contains(fullDayName.toLowerCase())
            || normalized.contains(shortDayName.toLowerCase());
    }

    private java.time.LocalTime parseScheduleTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        String trimmed = timeStr.trim();
        java.time.format.DateTimeFormatter[] formats = {
            java.time.format.DateTimeFormatter.ofPattern("h:mm a"),
            java.time.format.DateTimeFormatter.ofPattern("hh:mm a"),
            java.time.format.DateTimeFormatter.ofPattern("H:mm"),
            java.time.format.DateTimeFormatter.ofPattern("HH:mm")
        };
        for (java.time.format.DateTimeFormatter fmt : formats) {
            try {
                return java.time.LocalTime.parse(trimmed, fmt);
            } catch (Exception ignored) {
            }
        }
        return null;
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
            java.util.logging.Logger.getLogger(book.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(book.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(book.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(book.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new book().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Jpanel_date_time;
    private javax.swing.JSpinner age_spinner;
    private javax.swing.JLabel amountToPay;
    private javax.swing.JLabel appointment_date;
    private javax.swing.JPanel bc;
    private javax.swing.JPanel bg;
    private javax.swing.JPanel bg1;
    private javax.swing.JPanel bg2;
    private javax.swing.JPanel bg3;
    private javax.swing.JPanel bg4;
    private javax.swing.JTextField book_contact;
    private javax.swing.JTextField book_email;
    private javax.swing.JComboBox<String> book_gender;
    private javax.swing.JTextField booking_fullname;
    private javax.swing.JLabel braceconsultation;
    private javax.swing.JRadioButton cash_summary;
    private javax.swing.JLabel check1;
    private javax.swing.JLabel confirmBooking;
    private javax.swing.JPanel dc;
    private javax.swing.JRadioButton debitcard_summary;
    private javax.swing.JPanel denc;
    private javax.swing.JLabel dentalcheckup;
    private javax.swing.JLabel dentalcleaning;
    private javax.swing.JLabel dentalcrown;
    private javax.swing.JLabel dentalfilling;
    private javax.swing.JLabel dentistNAME;
    private javax.swing.JLabel dentistSpecialty;
    private javax.swing.JPanel df;
    private javax.swing.JPanel gc;
    private javax.swing.JRadioButton gcash_summary;
    private javax.swing.JLabel hdrpic;
    private javax.swing.JPanel header;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel moneytopay;
    private javax.swing.JLabel next_thisisSelectdentistPart;
    private javax.swing.JLabel nextbtn;
    private javax.swing.JLabel nextbtn3;
    private javax.swing.JLabel nextbtn_selectservicepart;
    private javax.swing.JPanel nextpane;
    private javax.swing.JPanel nextpane1;
    private javax.swing.JPanel nextpane2;
    private javax.swing.JPanel nextpane3;
    private javax.swing.JPanel nextpane4;
    private javax.swing.JLabel nextsign;
    private javax.swing.JLabel nextsign1;
    private javax.swing.JLabel nextsign2;
    private javax.swing.JLabel nextsign3;
    private javax.swing.JLabel nextsign4;
    private javax.swing.JLabel patientFullName;
    private javax.swing.JLabel prevbtn;
    private javax.swing.JLabel prevbtn1;
    private javax.swing.JLabel prevbtn2;
    private javax.swing.JLabel prevbtn3;
    private javax.swing.JLabel prevbtn4;
    private javax.swing.JPanel prevpane;
    private javax.swing.JPanel prevpane1;
    private javax.swing.JPanel prevpane2;
    private javax.swing.JPanel prevpane3;
    private javax.swing.JPanel prevpane4;
    private javax.swing.JPanel rc;
    private javax.swing.JLabel rootcanal;
    private javax.swing.JTable selectedDentist;
    private javax.swing.JLabel serviceSummary;
    private javax.swing.JTextField streetAddress;
    private javax.swing.JComboBox<String> streetprovince_combobox;
    private javax.swing.JLabel summaryFulldate;
    private javax.swing.JTabbedPane taab;
    private javax.swing.JTable table_dentist;
    private javax.swing.JPanel te;
    private javax.swing.JLabel teethwhitening;
    private javax.swing.JLabel toothextraction;
    private javax.swing.JPanel tw;
    private javax.swing.JLabel xbtn;
    private javax.swing.JPanel xpane;
    // End of variables declaration//GEN-END:variables
}
