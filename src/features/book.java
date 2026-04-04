/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.swing.JComboBox;
/**
 *
 * @author Cassandra Gallera
 */
public class book extends javax.swing.JFrame {
int xMouse, yMouse;
private String selectedService; 
private JDateChooser dateChooser;
private JComboBox<String> timeCombo;

    /**
     * Creates new form book
     */
    public book() {
        initComponents();
    hideOldPanels();     // hides NetBeans service panels
    initServiceCards();  // adds your new grid
    loadDentistData();
  
    
    
    
    
    dateChooser = new JDateChooser();
dateChooser.setPreferredSize(new Dimension(200, 30));

timeCombo = new JComboBox<>(new String[] {
    "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
    "11:00 AM", "11:30 AM", "01:00 PM", "01:30 PM",
    "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM"
});
timeCombo.setPreferredSize(new Dimension(120, 30));

// Add them to your panel
Jpanel_date_time.setLayout(new FlowLayout());
Jpanel_date_time.add(dateChooser);
Jpanel_date_time.add(timeCombo);

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
jTextField1.setFont(fieldFont);
jTextField2.setFont(fieldFont);
jTextField3.setFont(fieldFont);
jTextField5.setFont(fieldFont);
jTextField6.setFont(fieldFont);
gender.setFont(fieldFont);

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

        // Split service name into words and align center
        String[] words = service[0].split(" ");
        StringBuilder formattedName = new StringBuilder("<html><div style='text-align:center;'>");
        for (String word : words) {
            formattedName.append(word).append("<br>");
        }
        formattedName.append("</div></html>");

        
        JLabel title = new JLabel(formattedName.toString(), SwingConstants.CENTER);

// Use emoji-compatible font instead of plain cardFont

        title.setFont(emojiFont);

        title.setForeground(titleColor);

// Increase card height for better spacing
        card.setPreferredSize(new Dimension(160, 140));

        
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
                card.setBorder(BorderFactory.createLineBorder(highlightColor, 2));
            }
        });

        cardList.add(card);
        serviceGrid.add(card);
    }

    // Clear NetBeans components but keep label + navigation buttons
    jPanel2.removeAll();
    jPanel2.setLayout(new BorderLayout());

    // "Select Service *" label with black text + red asterisk
    JLabel selectLabel = new JLabel(
        "<html><span style='color:black;'>Select Service</span> <span style='color:red;'>*</span></html>",
        SwingConstants.LEFT
    );
    selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    jPanel2.add(selectLabel, BorderLayout.NORTH);

    // Add the grid at the center
    jPanel2.add(serviceGrid, BorderLayout.CENTER);

    // Keep navigation buttons at the bottom
    JPanel navPanel = new JPanel(new BorderLayout());
    navPanel.setBackground(backgroundColor);
    navPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    navPanel.add(prevpane1, BorderLayout.WEST);
    navPanel.add(nextpane1, BorderLayout.EAST);

    jPanel2.add(navPanel, BorderLayout.SOUTH);

    jPanel2.revalidate();
    jPanel2.repaint();
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


private boolean isPersonalInfoValid() {
    String name = jTextField1.getText().trim();     // Full Name
    String email = jTextField2.getText().trim();    // Email
    String age = jTextField3.getText().trim();      // Age
    String genderValue = (String) gender.getSelectedItem(); // Gender
    String phone = jTextField5.getText().trim();    // Phone Number
    String address = jTextField6.getText().trim();  // Address

    return !name.isEmpty()
        && !email.isEmpty()
        && !age.isEmpty()
        && genderValue != null && !genderValue.isEmpty()
        && !phone.isEmpty()
        && !address.isEmpty();
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkbox1 = new java.awt.Checkbox();
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
        jTextField1 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
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
        gender = new javax.swing.JComboBox<>();
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
        nextbtn1 = new javax.swing.JLabel();
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
        nextbtn2 = new javax.swing.JLabel();
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
        nextbtn4 = new javax.swing.JLabel();
        nextsign4 = new javax.swing.JLabel();
        prevpane4 = new javax.swing.JPanel();
        prevbtn4 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField4 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel109 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();

        checkbox1.setLabel("checkbox1");

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

        jLabel16.setText("Full Name");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 80, -1));

        jTextField1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 250, 30));

        jLabel17.setText("Email");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, -1, -1));

        jTextField2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 250, 30));

        jLabel18.setText("Gender");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, 50, -1));

        jTextField3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 250, 30));

        jLabel19.setText("Age");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 40, -1));

        jLabel20.setText("Phone Number");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 110, -1));

        jTextField5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 510, 30));

        jLabel21.setText("Address");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, -1));

        jTextField6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel1.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 510, 30));

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

        jPanel1.add(nextpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 90, 30));

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
        prevpane.add(prevbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 90, 30));

        jPanel1.add(prevpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 100, 30));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 0, 0));
        jLabel25.setText("*");
        jPanel1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 30, 30));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(204, 0, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("*");
        jPanel1.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 40, 20));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(204, 0, 0));
        jLabel27.setText("*");
        jPanel1.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 40, 30));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(204, 0, 0));
        jLabel28.setText("*");
        jPanel1.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 20, 20));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(204, 0, 0));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("*");
        jPanel1.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 30, 30));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(204, 0, 0));
        jLabel30.setText("*");
        jPanel1.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 210, 30, 40));

        gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        gender.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genderActionPerformed(evt);
            }
        });
        jPanel1.add(gender, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, 250, 30));

        jLabel47.setBackground(new java.awt.Color(204, 255, 255));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jLabel47.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel1.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 550, 360));

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 550, 350));

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

        nextbtn1.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        nextbtn1.setForeground(new java.awt.Color(255, 255, 255));
        nextbtn1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextbtn1.setText("Next");
        nextbtn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextbtn1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextbtn1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextbtn1MouseExited(evt);
            }
        });
        nextpane1.add(nextbtn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 30));

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
        jLabel50.setText("Serivice & Dentist");
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

        jLabel59.setText("Select Dentist");
        jPanel3.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 80, -1));

        nextpane2.setBackground(new java.awt.Color(0, 153, 153));
        nextpane2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextbtn2.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        nextbtn2.setForeground(new java.awt.Color(255, 255, 255));
        nextbtn2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextbtn2.setText("Next");
        nextbtn2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextbtn2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextbtn2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextbtn2MouseExited(evt);
            }
        });
        nextpane2.add(nextbtn2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 30));

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
        jPanel3.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 20, 40));

        table_dentist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table_dentist);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 530, 220));

        jLabel95.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jLabel95.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel3.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 550, 350));

        bg2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 550, 340));

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
        prevpane3.add(prevbtn3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 90, 30));

        jPanel8.add(prevpane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 100, 30));

        appointment_date.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        appointment_date.setText("Select Appointment Date & Time");
        jPanel8.add(appointment_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 250, -1));

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(255, 0, 0));
        jLabel72.setText("*");
        jPanel8.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 40, 30));
        jPanel8.add(Jpanel_date_time, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 440, 180));

        jLabel110.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jPanel8.add(jLabel110, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 550, 350));

        bg3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 550, 340));

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

        nextbtn4.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        nextbtn4.setForeground(new java.awt.Color(255, 255, 255));
        nextbtn4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        nextbtn4.setText("Confirm Booking");
        nextbtn4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextbtn4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextbtn4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextbtn4MouseExited(evt);
            }
        });
        nextpane4.add(nextbtn4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 120, 30));

        nextsign4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-done-20.png"))); // NOI18N
        nextsign4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextsign4MouseClicked(evt);
            }
        });
        nextpane4.add(nextsign4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        jPanel10.add(nextpane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 300, 160, 30));

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

        jPanel10.add(prevpane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 100, 30));

        jLabel96.setForeground(new java.awt.Color(204, 204, 204));
        jLabel96.setText("____________________________________________________________________________");
        jPanel10.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        jLabel97.setForeground(new java.awt.Color(204, 204, 204));
        jLabel97.setText("____________________________________________________________________________");
        jPanel10.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, 20));

        jLabel98.setForeground(new java.awt.Color(204, 204, 204));
        jLabel98.setText("____________________________________________________________________________");
        jPanel10.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 70, 540, 30));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel10.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, 180, 28));
        jPanel10.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 180, 28));
        jPanel10.add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 180, 28));

        jLabel99.setForeground(new java.awt.Color(0, 51, 102));
        jLabel99.setText("Payment Method");
        jPanel10.add(jLabel99, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 176, 110, 30));

        jLabel100.setForeground(new java.awt.Color(0, 51, 102));
        jLabel100.setText("GCash Number");
        jPanel10.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 110, 30));

        jLabel101.setForeground(new java.awt.Color(0, 51, 102));
        jLabel101.setText("Ammount to Pay");
        jPanel10.add(jLabel101, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 110, 30));

        jLabel102.setForeground(new java.awt.Color(0, 51, 102));
        jLabel102.setText("Consultation Fee");
        jPanel10.add(jLabel102, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel103.setForeground(new java.awt.Color(0, 51, 102));
        jLabel103.setText("jLabel103");
        jPanel10.add(jLabel103, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jLabel104.setForeground(new java.awt.Color(0, 51, 102));
        jLabel104.setText("Service Fee");
        jPanel10.add(jLabel104, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, 30));

        jLabel105.setForeground(new java.awt.Color(0, 51, 102));
        jLabel105.setText("jLabel105");
        jPanel10.add(jLabel105, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, 30));

        jLabel106.setForeground(new java.awt.Color(0, 51, 102));
        jLabel106.setText("Total Amount");
        jPanel10.add(jLabel106, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jLabel107.setForeground(new java.awt.Color(0, 51, 102));
        jLabel107.setText("jLabel107");
        jPanel10.add(jLabel107, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel10.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 220, 40, 30));

        jLabel109.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/a.jpg"))); // NOI18N
        jPanel10.add(jLabel109, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 550, 350));

        bg4.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 550, 340));

        jLabel92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260322_145831.png"))); // NOI18N
        jLabel92.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        bg4.add(jLabel92, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 470));

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

    private void nextbtn4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn4MouseExited
        nextpane4.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_nextbtn4MouseExited

    private void nextbtn4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn4MouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane4.setBackground(new Color(0,102,102)); // darker on hover
    }//GEN-LAST:event_nextbtn4MouseEntered

    private void nextbtn4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn4MouseClicked
        taab.setSelectedIndex(1);
    }//GEN-LAST:event_nextbtn4MouseClicked

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

    // Move to next booking page
    taab.setSelectedIndex(4);
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

    private void nextbtn2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn2MouseExited
        nextpane2.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_nextbtn2MouseExited

    private void nextbtn2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn2MouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane2.setBackground(new Color(0,102,102)); // darker on hover
    }//GEN-LAST:event_nextbtn2MouseEntered

    private void nextbtn2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn2MouseClicked
        taab.setSelectedIndex(3);
    }//GEN-LAST:event_nextbtn2MouseClicked

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

    private void nextbtn1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn1MouseExited
        nextpane1.setBackground(new Color(0,153,153)); // original color
    }//GEN-LAST:event_nextbtn1MouseExited

    private void nextbtn1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn1MouseEntered
        Color baseColor = new Color(0,102,102);
        nextpane1.setBackground(new Color(0,102,102)); // darker on hover
    }//GEN-LAST:event_nextbtn1MouseEntered

    private void nextbtn1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextbtn1MouseClicked


      taab.setSelectedIndex(2);
    }//GEN-LAST:event_nextbtn1MouseClicked

    private void genderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_genderActionPerformed

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
        landingp booknow = new landingp();
        this.dispose();
        booknow.setVisible(true);
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
        // ✅ Only proceed if valid
        taab.setSelectedIndex(1); // move to Service & Dentist tab
    } else {
        // ❌ Block navigation if invalid
        JOptionPane.showMessageDialog(
            this,
            "Please fill in all required fields before proceeding.",
            "Missing Information",
            JOptionPane.WARNING_MESSAGE
        );
        // Do NOT switch tabs here
    }
    }//GEN-LAST:event_nextbtnMouseClicked

    private void loadDentistData() {


    String sql = "SELECT acc_name AS Dentist, acc_role AS Role " +
                 "FROM tbl_accounts " +
                 "WHERE acc_role = 'dentist'";

    // Use your config helper to load results into the table
    new config().displayData(sql, table_dentist);



    try (Connection con = config.connectDB();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        // Load DB results into table
        table_dentist.setModel(DbUtils.resultSetToTableModel(rs));

        // ✅ Professional dental clinic styling
        table_dentist.setRowHeight(28);
        table_dentist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table_dentist.setGridColor(new Color(220, 220, 220));
        table_dentist.setShowGrid(true);

        // ✅ Header styling
        JTableHeader header = table_dentist.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(200, 230, 240)); // soft healthcare blue
        header.setForeground(Color.DARK_GRAY);
        header.setOpaque(true);
        header.repaint();

        // ✅ Alternate row colors
        table_dentist.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    c.setForeground(new Color(30, 30, 30));
                } else {
                    c.setBackground(new Color(184, 207, 229)); // selection blue
                    c.setForeground(Color.BLACK);
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // padding
                }
                return c;
            }
        });

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error loading dentist data: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
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
    private javax.swing.JLabel appointment_date;
    private javax.swing.JPanel bc;
    private javax.swing.JPanel bg;
    private javax.swing.JPanel bg1;
    private javax.swing.JPanel bg2;
    private javax.swing.JPanel bg3;
    private javax.swing.JPanel bg4;
    private javax.swing.JLabel braceconsultation;
    private javax.swing.JLabel check1;
    private java.awt.Checkbox checkbox1;
    private javax.swing.JPanel dc;
    private javax.swing.JPanel denc;
    private javax.swing.JLabel dentalcheckup;
    private javax.swing.JLabel dentalcleaning;
    private javax.swing.JLabel dentalcrown;
    private javax.swing.JLabel dentalfilling;
    private javax.swing.JPanel df;
    private javax.swing.JPanel gc;
    private javax.swing.JComboBox<String> gender;
    private javax.swing.JLabel hdrpic;
    private javax.swing.JPanel header;
    private javax.swing.JComboBox<String> jComboBox1;
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
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
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
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel nextbtn;
    private javax.swing.JLabel nextbtn1;
    private javax.swing.JLabel nextbtn2;
    private javax.swing.JLabel nextbtn3;
    private javax.swing.JLabel nextbtn4;
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
