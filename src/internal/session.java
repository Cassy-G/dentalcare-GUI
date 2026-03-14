package internal;

public class session {

    private static int id;
    private static String name;
    private static String email;
    private static String contact;   // ADD THIS
    private static String role;
    private static int userId;
    private static String userRole;

    public static void setSession(int acc_id, String acc_name,
                                  String acc_email, String acc_contact,
                                  String acc_role){

        id = acc_id;
        name = acc_name;
        email = acc_email;
        contact = acc_contact;   // ADD THIS
        role = acc_role;
    }
    // Call this after login succeeds
    public static void setSession(int id, String role) {
        userId = id;
        userRole = role;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUserRole() {
        return userRole;
    }
    public static int getId(){ return id; }
    public static String getName(){ return name; }
    public static String getEmail(){ return email; }
    public static String getContact(){ return contact; } // ADD THIS
    public static String getRole(){ return role; }

    public static void setName(String newName){ name = newName; }
    public static void setEmail(String newEmail){ email = newEmail; }
    public static void setContact(String newContact){ contact = newContact; }

    public static void clear(){
        id = 0;
        name = null;
        email = null;
        contact = null;   // ADD THIS
        role = null;
    }
}