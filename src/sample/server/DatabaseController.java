package sample.server;

import java.sql.*;

public class DatabaseController {
    private static DatabaseController DBControllerInstance = new DatabaseController();
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;
    private static Connection connection;

    private DatabaseController() {

            try {

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/crypto?serverTimezone=AST", "root", "MyNewPass");
                this.connection = con;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

    }


    public static DatabaseController getInstance()
    {
        if(DBControllerInstance == null)
            DBControllerInstance = new DatabaseController();
        return DBControllerInstance;
    }



        public boolean verifyImage(String hash, String username) throws SQLException {

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM crypto.imageshash WHERE Username = '"+username+"' and HashCode = '"+hash+"'");

            while(rs.next()) {
                if (rs.getInt(1) != 0)
                    return true;

            }
            return false;
    }

    public boolean verifyUser(String username, String password) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM crypto.users WHERE Username = '"+username+"' and Password = '"+password+"'");

        while(rs.next()) {
            if (rs.getInt(1) != 0)
                return true;

        }
        return false;
    }
}


