package example.com.dataaccess;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class DAOTemplate {

    private static final Logger LOGGER = Logger.getLogger(DAOTemplate.class);

    @Autowired
    private DataSource dataSource;

    public int executeUpdate(String query) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            LOGGER.debug("Executing update query: " + query);
            return preparedStatement.executeUpdate();

        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
