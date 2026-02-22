package internal;

public class session {

    private static int id;
    private static String name;
    private static String email;
    private static String role; 

    public static void setSession(int acc_id, String acc_name, String acc_email, String acc_role){
        id = acc_id;
        name = acc_name;
        email = acc_email;  // FIXED
        role = acc_role;
    }

    public static int getId(){ return id; }
    public static String getName(){ return name; }
    public static String getEmail(){ return email; }
    public static String getRole(){ return role; }

    public static void clear(){
        id = 0;
        name = null;
        email = null;
        role = null;
    }
    
}