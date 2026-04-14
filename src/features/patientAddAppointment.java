package features;

/**
 * Dedicated patient-facing Add Appointment window.
 * Reuses the existing NetBeans booking GUI so the design
 * stays consistent with the rest of the system.
 */
public class patientAddAppointment extends book {

    public patientAddAppointment() {
        super();
        setCloseOnlyMode(true);
        setTitle("Patient - Add Appointment");
        getTaab().setSelectedIndex(0);
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new patientAddAppointment().setVisible(true);
            }
        });
    }
}
