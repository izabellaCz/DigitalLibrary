package example.com.model;

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@XmlRootElement
public class Book implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(Book.class);
    private int id;
    private byte[] title;
    private Author author;
    private byte[] publisher;
    private byte[] description;
    private int total;
    private int available;
    private byte[] cover;
    private History history;
    private Boolean isFavourite;

    public Book() {
    }

    public Book(int id, byte[] title, Author author, byte[] publisher, byte[] description, int total, int available, byte[] cover, History history, Boolean isFavourite) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.total = total;
        this.available = available;
        this.cover = cover;
        this.history = history;
        this.isFavourite = isFavourite;
    }

    public static Book constructFromResultSet(ResultSet resultSet) throws SQLException, UnsupportedEncodingException {

        return new Book(
                resultSet.getInt("book_id"),
                resultSet.getBytes("title"),
                Author.constructFromResultSet(resultSet),
                resultSet.getBytes("publisher"),
                resultSet.getBytes("description"),
                resultSet.getInt("total"),
                resultSet.getInt("available"),
                resultSet.getBytes("cover"),
                History.constructFromResultSet(resultSet),
                resultSet.getBoolean("is_favourite")
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getTitle() {
        return title;
    }

    public void setTitle(byte[] title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public byte[] getPublisher() {
        return publisher;
    }

    public void setPublisher(byte[] publisher) {
        this.publisher = publisher;
    }

    public byte[] getDescription() {
        return description;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }
}
