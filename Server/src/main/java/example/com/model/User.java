package example.com.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private int id;
    private byte[] fullname;
    private String username;
    private String password;
    private String type;

    public User() {
    }

    public User(int id, byte[] fullname, String username, String password, String type) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public static User constructFromResultSet(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("user_id"),
                resultSet.getBytes("fullname"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("type")
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getFullname() {
        return fullname;
    }

    public void setFullname(byte[] fullname) {
        this.fullname = fullname;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
