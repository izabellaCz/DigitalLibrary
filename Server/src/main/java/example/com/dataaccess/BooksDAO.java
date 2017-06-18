package example.com.dataaccess;

import example.com.model.Book;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BooksDAO {

    private final static Logger LOGGER = Logger.getLogger(BooksDAO.class);
    @Autowired
    private DAOTemplate DAOTemplate;

    public List<Book> getBooks(String userId) throws SQLException, UnsupportedEncodingException {

        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT * , ( " +
                    "CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId +" and fav_book_id = b.book_id )" +
                    "THEN TRUE ELSE FALSE " +
                    "END ) as  `is_favourite`" +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "LEFT JOIN ( " +
                    "   SELECT * FROM history " +
                    "   WHERE history_user_id = " + userId +
                    "   ) AS h on h.history_book_id = b.book_id;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(Book.constructFromResultSet(resultSet));
            }
            return books;
        }

    }

    public List<Book> getFavouriteBooks(String userId) throws SQLException, UnsupportedEncodingException {

        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {

            String query = "SELECT *, (CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId + " and fav_book_id = b.book_id) " +
                    "THEN TRUE ELSE FALSE " +
                    "END) as  `is_favourite` " +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "LEFT JOIN ( " +
                    "SELECT * FROM history " +
                    "WHERE history_user_id = " + userId + " " +
                    ") AS h on h.history_book_id = b.book_id " +
                    "JOIN favourites AS f ON f.fav_book_id = b.book_id " +
                    "WHERE f.fav_user_id = " + userId + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(Book.constructFromResultSet(resultSet));
            }
            return books;
        }

    }

    public Book getBookById(String userId, String bookId) throws SQLException, UnsupportedEncodingException {

        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {

            String query = "SELECT *, (CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId + " and fav_book_id = b.book_id) " +
                    "THEN TRUE ELSE FALSE " +
                    "END) as  `is_favourite` " +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "LEFT JOIN ( " +
                    "SELECT * FROM history " +
                    "WHERE history_user_id = " + userId + " " +
                    ") AS h on h.history_book_id = b.book_id " +
                    "WHERE b.book_id = " + bookId + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            return Book.constructFromResultSet(resultSet);
        }

    }

    public int rentBook(String userId, String bookId, String loanDate, String approverId) throws SQLException {
        return DAOTemplate.executeUpdate("insert into history (history_user_id, history_book_id, loan_date, loan_appr_id) " +
                "values (" + userId + ", " + bookId + ", '" + loanDate + "', " + approverId + ") " +
                "ON DUPLICATE KEY UPDATE loan_date='" + loanDate + "', return_date = null, return_appr_id = null; " +
                "update books set available = available - 1 where book_id = " + bookId + ";");

    }

    public int returnBook(String historyId, String returnDate, String approverId) throws SQLException {
        return DAOTemplate.executeUpdate("update history set return_date = '" +
                returnDate + "', return_appr_id = " + approverId + " where history_id = " + historyId + "; " +
                "update books as b " +
                "join history as h on h.history_book_id = b.book_id " +
                "set b.available = b.available + 1 " +
                "where h.history_id = " + historyId + ";");
    }

    public int registerBook(String title, int authorId, String description, int total, String publisher, byte[] cover)
            throws SQLException {

        String query = "INSERT INTO Books (title, book_author_id, description, publisher, total, available, cover) " +
                "VALUES (?,?,?,?,?,?,?);";
        try (Connection connection = DAOTemplate.getDataSource().getConnection()){

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, authorId);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, publisher);
            preparedStatement.setInt(5, total);
            preparedStatement.setInt(6, total);
            preparedStatement.setBytes(7, cover);

            return preparedStatement.executeUpdate();

        }
    }

    public int removeBook(String bookId) throws SQLException {
        return DAOTemplate.executeUpdate("delete from books where book_id = " + bookId + ";");
    }

    public int addToFavourites(String bookId, String userId) throws SQLException {
        return DAOTemplate.executeUpdate("insert into favourites (fav_user_id, fav_book_id) values " +
                "( " + userId + ", " + bookId + ") ;");
    }

    public int removeFromFavourites(String bookId, String userId) throws SQLException {
        return DAOTemplate.executeUpdate("delete from favourites where " +
                "fav_user_id = " + userId + " and fav_book_id = " + bookId + ";");
    }

    public int isFavourite(String bookId, String userId) throws SQLException {
        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT * " +
                    "FROM favourites " +
                    "WHERE fav_book_id = " + bookId + " AND fav_user_id = " + userId + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            if (preparedStatement.executeQuery().next()) {
                return 1;
            }
            return 0;
        }
    }


    public List<Book> findBooks(String userId, String userQuery) throws SQLException, UnsupportedEncodingException {

        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            /*String query = "SELECT * " +
                    "FROM books AS b " +
                    "JOIN authors AS a ON b.book_author_id = a.author_id " +
                    "WHERE b.title like '%" + userQuery + "%' " +
                    "OR a.name like '%" + userQuery + "%';";*/
            String query = "SELECT * , ( " +
                    "CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId +" and fav_book_id = b.book_id )" +
                    "THEN TRUE ELSE FALSE " +
                    "END ) as  `is_favourite`" +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "LEFT JOIN ( " +
                    "   SELECT * FROM history " +
                    "   WHERE history_user_id = " + userId +
                    "   ) AS h on h.history_book_id = b.book_id " +
                    "WHERE  b.title like '%" + userQuery + "%' " +
                    "OR a.name like '%" + userQuery + "%';";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(Book.constructFromResultSet(resultSet));
            }
            return books;
        }

    }

    /* History */

    public List<Book> getUserHistory(String userId) throws SQLException, UnsupportedEncodingException {

        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT * , ( " +
                    "CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId +" and fav_book_id = b.book_id )" +
                    "THEN TRUE ELSE FALSE " +
                    "END ) as  `is_favourite`" +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "JOIN history AS h on h.history_book_id = b.book_id " +
                    "WHERE history_user_id = " + userId + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(Book.constructFromResultSet(resultSet));
            }
            return books;
        }
    }

    public List<Book> getUsersDueBooks(String userId) throws SQLException, UnsupportedEncodingException {

        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT * , ( " +
                    "CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId +" and fav_book_id = b.book_id )" +
                    "THEN TRUE ELSE FALSE " +
                    "END ) as  `is_favourite`" +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "JOIN history AS h on h.history_book_id = b.book_id " +
                    "WHERE history_user_id = " + userId + " " +
                    "AND h.return_date IS null;";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(Book.constructFromResultSet(resultSet));
            }
            return books;
        }

    }

    public Book getLoanInfo(String userId, String bookId) throws SQLException, UnsupportedEncodingException {
        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT * , ( " +
                    "CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId +" and fav_book_id = b.book_id )" +
                    "THEN TRUE ELSE FALSE " +
                    "END ) as  `is_favourite`" +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "JOIN history AS h on h.history_book_id = b.book_id " +
                    "WHERE history_book_id = " + bookId + " and history_user_id = " + userId + "; ";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            return Book.constructFromResultSet(resultSet);
        }
    }

    public List<Book> getAdminsApproves(String adminId) throws SQLException, UnsupportedEncodingException {
        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT *, " +
                    "FALSE AS  `is_favourite` FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "JOIN ( SELECT * FROM history WHERE loan_appr_id = " + adminId +
                    " or return_appr_id = " + adminId + " ) AS h on h.history_book_id = b.book_id ";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(Book.constructFromResultSet(resultSet));
            }
            return books;
        }
    }

    public List<Book> filterBooks( String userId, String title, String author, String publisher, String isAvailable) throws SQLException, UnsupportedEncodingException {
        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * , ( " +
                    "CASE WHEN EXISTS ( " +
                    "SELECT 1 from favourites " +
                    "WHERE fav_user_id = " + userId +" and fav_book_id = b.book_id )" +
                    "THEN TRUE ELSE FALSE " +
                    "END ) as  `is_favourite`" +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "LEFT JOIN ( " +
                    "   SELECT * FROM history " +
                    "   WHERE history_user_id = " + userId +
                    "   ) AS h on h.history_book_id = b.book_id ");

            StringBuilder filters = new StringBuilder("WHERE");

            if (title != null) {
                filters.append(" b.title like '%" + title + "%' ");
            }
            if (author != null) {
                if (!filters.equals("WHERE")) {
                    filters.append(" and ");
                }
                filters.append(" a.name like '%" + author + "%' ");
            }
            if (publisher != null) {
                if (!filters.equals("WHERE")) {
                    filters.append(" and ");
                }
                filters.append(" b.publisher like '%" + publisher + "%' ");
            }
            if (isAvailable != null) {
                if (!filters.equals("WHERE")) {
                    filters.append(" and ");
                }
                filters.append(" b.available > 0 ");
            }

            if (!filters.equals("WHERE")) {
                query.append(filters);
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(Book.constructFromResultSet(resultSet));
            }
            return books;
        }
    }

    public String hasDueBooks(String userId, String currentDate) throws SQLException {
        try (Connection connection = DAOTemplate.getDataSource().getConnection()) {
            String query = "SELECT 1 " +
                    "FROM books AS b " +
                    "JOIN authors AS a ON a.author_id = b.book_author_id " +
                    "LEFT JOIN ( " +
                    "   SELECT * FROM history " +
                    "   WHERE history_user_id = " + userId +
                    "   ) AS h on h.history_book_id = b.book_id " +
                    " WHERE h.history_user_id = " + userId +
                    " and h.return_date is null and datediff(DATE_ADD(h.loan_date,INTERVAL 30 DAY), '" + currentDate + "') <= 0 ";


            PreparedStatement preparedStatement = connection.prepareStatement(query);

            LOGGER.debug("Executing query: " + query);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return "1";
            }
            return "-1";

        }
    }

    public DAOTemplate getDAOTemplate() {
        return DAOTemplate;
    }

    public void setDAOTemplate(DAOTemplate DAOTemplate) {
        this.DAOTemplate = DAOTemplate;
    }
}
