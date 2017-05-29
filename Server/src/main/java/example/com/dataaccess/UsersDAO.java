package example.com.dataaccess;

import example.com.model.Book;
import example.com.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UsersDAO {

    private final static Logger LOGGER = Logger.getLogger(UsersDAO.class);
    @Autowired
    private DAOTemplate DAOTemplate;

    public User getUser(String username, String password) throws SQLException {

        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT * FROM users WHERE username='" + username + "' and password='" + password + "';";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            return User.constructFromResultSet(resultSet);
        }

    }

    public int registerUser(String fullname, String username, String password, String type) throws SQLException {
        return DAOTemplate.executeUpdate("INSERT INTO users (fullname, username, password, type) VALUES ('" +
                fullname + "', '" +
                username + "', '" +
                password + "', '" +
                type + "');");
    }

    public DAOTemplate getDAOTemplate() {
        return DAOTemplate;
    }

    public void setDAOTemplate(DAOTemplate DAOTemplate) {
        this.DAOTemplate = DAOTemplate;
    }
}
