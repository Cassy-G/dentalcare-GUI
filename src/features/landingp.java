/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package features;

import java.awt.Color;
import static java.lang.System.exit;
import config.config;
import java.awt.Image;
import javax.swing.ImageIcon;


/**
 *
 * @author Cassandra Gallera
 */
public class landingp extends javax.swing.JFrame {
 int xMouse, yMouse;
    /**
     * Creates new form landingp
     */
    public landingp() {
        initComponents();
        
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        hdr = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        exitp = new javax.swing.JPanel();
        exitb = new javax.swing.JLabel();
        Home = new javax.swing.JLabel();
        about = new javax.swing.JLabel();
        services = new javax.swing.JLabel();
        lgn = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        bookpnel = new javax.swing.JPanel();
        bookbtn = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        hdr.setBackground(new java.awt.Color(255, 255, 255));
        hdr.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                hdrMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                hdrMouseMoved(evt);
            }
        });
        hdr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hdrMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                hdrMousePressed(evt);
            }
        });
        hdr.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/output-onlinepngtools__3_-removebg-preview.png"))); // NOI18N
        hdr.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 50));

        exitb.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        exitb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exitb.setText("X");
        exitb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitbMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitbMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitbMouseExited(evt);
            }
        });

        javax.swing.GroupLayout exitpLayout = new javax.swing.GroupLayout(exitp);
        exitp.setLayout(exitpLayout);
        exitpLayout.setHorizontalGroup(
            exitpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exitpLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exitb, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        exitpLayout.setVerticalGroup(
            exitpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exitpLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exitb, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        hdr.add(exitp, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 40, 30));

        Home.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        Home.setText("Home");
        Home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HomeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                HomeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                HomeMouseExited(evt);
            }
        });
        hdr.add(Home, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 0, 60, 50));

        about.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        about.setText("About Us");
        about.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                aboutMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                aboutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                aboutMouseExited(evt);
            }
        });
        hdr.add(about, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 0, 70, 50));

        services.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        services.setText("Services");
        services.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                servicesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                servicesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                servicesMouseExited(evt);
            }
        });
        hdr.add(services, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 0, -1, 50));

        lgn.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        lgn.setText("Login");
        lgn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lgnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lgnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lgnMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lgnMousePressed(evt);
            }
        });
        hdr.add(lgn, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 60, 50));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/5046faad4a4c9af72bcf4fe75c8a11d0.jpg"))); // NOI18N
        hdr.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 50));

        bg.add(hdr, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 50));

        jPanel1.setBackground(new java.awt.Color(153, 204, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bookpnel.setBackground(new java.awt.Color(255, 255, 255));
        bookpnel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bookbtn.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        bookbtn.setForeground(new java.awt.Color(51, 51, 51));
        bookbtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bookbtn.setText("Book An Appointment");
        bookbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bookbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bookbtnMouseExited(evt);
            }
        });
        bookpnel.add(bookbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 55));

        jPanel1.add(bookpnel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 390, 240, 55));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Copilot_20260317_174616 (6).png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 470));

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 810, 470));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bookbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookbtnMouseExited
        // TODO add your handling code here:
        bookpnel.setBackground(Color. white);
        bookbtn.setForeground(Color.black);
    }//GEN-LAST:event_bookbtnMouseExited

    private void bookbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookbtnMouseEntered
        // TODO add your handling code here:
        bookpnel.setBackground(Color. blue);
        bookbtn.setForeground(Color.white);
    }//GEN-LAST:event_bookbtnMouseEntered

    private void bookbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookbtnMouseClicked
        // TODO add your handling code here:
        book booknow = new book();
        this.dispose();
        booknow.setVisible(true);
    }//GEN-LAST:event_bookbtnMouseClicked

    private void hdrMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrMousePressed
        // TODO add your handling code here:
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_hdrMousePressed

    private void hdrMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_hdrMouseExited

    private void hdrMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_hdrMouseMoved

    private void hdrMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hdrMouseDragged
        // TODO add your handling code here:
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse,y - yMouse);
    }//GEN-LAST:event_hdrMouseDragged

    private void lgnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lgnMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lgnMousePressed

    private void lgnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lgnMouseExited
        // TODO add your handling code here:
        lgn.setBackground(Color. white);
        lgn.setForeground(Color.black);
    }//GEN-LAST:event_lgnMouseExited

    private void lgnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lgnMouseEntered
        // TODO add your handling code here:

        lgn.setForeground(Color.blue);
    }//GEN-LAST:event_lgnMouseEntered

    private void lgnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lgnMouseClicked
        // TODO add your handling code here:
        login lgn = new login();
        this.dispose();
        lgn.setVisible(true);
    }//GEN-LAST:event_lgnMouseClicked

    private void servicesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_servicesMouseExited
        // TODO add your handling code here:
        services.setBackground(Color. white);
        services.setForeground(Color.black);
    }//GEN-LAST:event_servicesMouseExited

    private void servicesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_servicesMouseEntered
        // TODO add your handling code here:
        services.setForeground(Color.blue);
    }//GEN-LAST:event_servicesMouseEntered

    private void servicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_servicesMouseClicked
        services consultation = new services();
        this.dispose();
        consultation.setVisible(true);
    }//GEN-LAST:event_servicesMouseClicked

    private void aboutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutMouseExited
        // TODO add your handling code here:
        about.setBackground(Color. white);
        about.setForeground(Color.black);
    }//GEN-LAST:event_aboutMouseExited

    private void aboutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutMouseEntered
        // TODO add your handling code here:
        about.setForeground(Color.blue);
    }//GEN-LAST:event_aboutMouseEntered

    private void aboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutMouseClicked
        // TODO add your handling code here:
        aboutUs abouts = new aboutUs();
        this.dispose();
        abouts.setVisible(true);
    }//GEN-LAST:event_aboutMouseClicked

    private void HomeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeMouseExited
        // TODO add your handling code here:
        Home.setBackground(Color. white);
        Home.setForeground(Color.black);
    }//GEN-LAST:event_HomeMouseExited

    private void HomeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeMouseEntered
        // TODO add your handling code here:
        Home.setForeground(Color.blue);
    }//GEN-LAST:event_HomeMouseEntered

    private void HomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeMouseClicked
        // TODO add your handling code here:
        landingp home = new landingp();
        this.dispose();
        home.setVisible(true);
    }//GEN-LAST:event_HomeMouseClicked

    private void exitbMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitbMouseExited
        // TODO add your handling code here:
        exitp.setBackground(Color. white);
        exitb.setForeground(Color.black);
    }//GEN-LAST:event_exitbMouseExited

    private void exitbMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitbMouseEntered
        // TODO add your handling code here:
        exitp.setBackground(Color. red);
        exitb.setForeground(Color.white);
    }//GEN-LAST:event_exitbMouseEntered

    private void exitbMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitbMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitbMouseClicked

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
            java.util.logging.Logger.getLogger(landingp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(landingp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(landingp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(landingp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new landingp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Home;
    private javax.swing.JLabel about;
    private javax.swing.JPanel bg;
    private javax.swing.JLabel bookbtn;
    private javax.swing.JPanel bookpnel;
    private javax.swing.JLabel exitb;
    private javax.swing.JPanel exitp;
    private javax.swing.JPanel hdr;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lgn;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel services;
    // End of variables declaration//GEN-END:variables

 
}
