import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class RegisterUser {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter phone: ");
        String phone = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, password);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("User registered successfully!");
            } else {
                System.out.println("Registration failed.");
            }

            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }
}