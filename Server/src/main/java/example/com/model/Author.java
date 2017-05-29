package example.com.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Author {

    private int id;
    private byte[] name;

    public Author() {
    }

    public Author(int id, byte[] name) {
        this.id = id;
        this.name = name;
    }

    public static Author constructFromResultSet(ResultSet resultSet) throws SQLException {
        return new Author(
                resultSet.getInt("author_id"),
                resultSet.getBytes("name")
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }
}
