import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;

public class VulnerableApp {

    // 1. Hardcoded Credentials 
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "Production_Secret_Pass_992!";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/prod_db";

    // 2. SQL Injection (SQLi)
    public void getUserData(String username) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();
            
            // Flaw: Unsanitized user input concatenated directly into a SQL query
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            stmt.execute(query);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Path Traversal
    public void readUserFile(String filename) {
        try {
            // Flaw: Unsanitized user input used to construct a file path
            File file = new File("/var/www/uploads/" + filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Weak Cryptography
    public byte[] hashPassword(String password) {
        try {
            // Flaw: MD5 is cryptographically broken and vulnerable to collision attacks
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(password.getBytes());
        } catch (Exception e) {
            return null;
        }
    }

    // 5. OS Command Injection
    public void pingHost(String hostname) {
        try {
            // Flaw: Unsanitized input passed directly to the underlying OS shell
            Runtime.getRuntime().exec("ping -c 1 " + hostname);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
