package internal;

public class session {

    // Core user details
    private static int id;              // account ID
    private static String name;         // account name
    private static String email;        // account email
    private static String contact;      // account contact
    private static String role;         // account role

    // --- Setters for full session (after login) ---
    public static void setSession(int acc_id, String acc_name,
                                  String acc_email, String acc_contact,
                                  String acc_role) {
        id = acc_id;
        name = acc_name;
        email = acc_email;
        contact = acc_contact;
        role = acc_role;
    }

    // --- Getters ---
    public static int getId() { return id; }
    public static String getName() { return name; }
    public static String getEmail() { return email; }
    public static String getContact() { return contact; }
    public static String getRole() { return role; }

    // --- Update session values when profile changes ---
    public static void setName(String newName) { name = newName; }
    public static void setEmail(String newEmail) { email = newEmail; }
    public static void setContact(String newContact) { contact = newContact; }
    public static void setRole(String newRole) { role = newRole; }

    // Add dentistId field
private static int dentistId; // dentist_id from tbl_dentists

public static void setDentistId(int id) { dentistId = id; }
public static int getDentistId() { return dentistId; }

    
    
// --- Clear session (logout) ---
public static void clear() {
    id = 0;
    name = null;
    email = null;
    contact = null;
    role = null;
    dentistId = 0; // ✅ reset dentistId too
}

}
