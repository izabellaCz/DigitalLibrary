package example.com.dataaccess;

import example.com.model.Author;
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
public class AuthorsDAO {

    private final static Logger LOGGER = Logger.getLogger(AuthorsDAO.class);
    @Autowired
    private DAOTemplate DAOTemplate;

    public int registerAuthor(String name) throws SQLException {
        return DAOTemplate.executeUpdate("INSERT INTO Authors (name) VALUES ('" + name + "');");
    }

    public List<Author> listAllAuthors() throws SQLException {
        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT * FROM AUTHORS;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Author> authors = new ArrayList<>();
            while (resultSet.next()) {
                authors.add(Author.constructFromResultSet(resultSet));
            }
            return authors;
        }

    }

    public DAOTemplate getDAOTemplate() {
        return DAOTemplate;
    }

    public void setDAOTemplate(DAOTemplate DAOTemplate) {
        this.DAOTemplate = DAOTemplate;
    }
}
