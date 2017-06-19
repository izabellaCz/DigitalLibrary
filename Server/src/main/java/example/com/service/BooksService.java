package example.com.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.core.exceptions.MysqlErrorNumbers;
import example.com.dataaccess.BooksDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

@Controller
@RequestMapping("/library")
public class BooksService {

    private final static Logger LOGGER = Logger.getLogger(BooksService.class);
    @Autowired
    private BooksDAO booksDAO;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/getById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getById(@RequestParam String userId, @RequestParam String bookId) {
        try {
            return objectMapper.writeValueAsString(booksDAO.getBookById(userId, bookId));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAll(@RequestParam String userId) {
        try {
            return objectMapper.writeValueAsString(booksDAO.getBooks(userId));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/getFavouriteBooks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getFavouriteBooks(@RequestParam String userId) {
        try {
            return objectMapper.writeValueAsString(booksDAO.getFavouriteBooks(userId));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/getUserHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUserHistory(@RequestParam String userId) {
        try {
            return objectMapper.writeValueAsString(booksDAO.getUserHistory(userId));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/getUsersDueBooks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUsersDueBooks(@RequestParam String userId) {
        try {
            return objectMapper.writeValueAsString(booksDAO.getUsersDueBooks(userId));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/rentBook", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String rentBook(@RequestParam("userId") String userId, @RequestParam("bookId") String bookId,
                           @RequestParam("loanDate") String loanDate,
                           @RequestParam("approverId") String approverId) {
        try {
            return String.valueOf(booksDAO.rentBook(userId, bookId, loanDate, approverId));
        } catch (SQLException e) {
            LOGGER.error("SQL EXCEPTION: ", e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DATA_TOO_LONG) return "Data truncation";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DATA_OUT_OF_RANGE) return "Not enough books";
        }
        return "-1";
    }

    @RequestMapping(value = "/returnBook", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String returnBook(@RequestParam("historyId") String historyId,
                           @RequestParam("returnDate") String returnDate,
                             @RequestParam("approverId") String approverId) {
        try {
            return String.valueOf(booksDAO.returnBook(historyId, returnDate, approverId));
        } catch (SQLException e) {
            LOGGER.error("ERROR", e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DATA_TOO_LONG) return "Data truncation";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
        }
        return "-1";
    }

    @RequestMapping(value = "/getLoanInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getLoanInfo(@RequestParam String userId, @RequestParam String bookId) {
        try {
            return objectMapper.writeValueAsString(booksDAO.getLoanInfo(userId, bookId));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String add(@RequestParam("title") String title, @RequestParam("author") int authorId,
                      @RequestParam("description") String description, @RequestParam("total") int total,
                      @RequestParam("publisher") String publisher,
                      @RequestParam(value = "cover", required = false) MultipartFile cover) {

        byte[] coverContent = null;
        if (cover != null) {
            try {
                coverContent = cover.getBytes();
            } catch (IOException e) {
                LOGGER.error("ERROR", e);
                return "-1";
            }
        }

        try {
            return String.valueOf(booksDAO.registerBook(title, authorId, description, total, publisher, coverContent));
        } catch (SQLException e) {
            LOGGER.error("ERROR", e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) return "Duplicate entry";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DATA_TOO_LONG) return "Data truncation";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
        }
        return "-1";
    }

    @RequestMapping(value = "/removeBook", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeBook(@RequestParam("bookId") String bookId) {
        try {
            return String.valueOf(booksDAO.removeBook(bookId));
        } catch (SQLException e) {
            LOGGER.error("ERROR", e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
        }
        return "-1";
    }

    @RequestMapping(value = "/addFavourite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addToFavourites(@RequestParam("bookId") String bookId, @RequestParam("userId") String userId) {
        try {
            return String.valueOf(booksDAO.addToFavourites(bookId, userId));
        } catch (SQLException e) {
            LOGGER.error("ERROR", e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
        }
        return "-1";
    }

    @RequestMapping(value = "/removeFavourite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeFavourite(@RequestParam("bookId") String bookId, @RequestParam("userId") String userId) {
        try {
            return String.valueOf(booksDAO.removeFromFavourites(bookId, userId));
        } catch (SQLException e) {
            LOGGER.error("ERROR", e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
        }
        return "-1";
    }

    @RequestMapping(value = "/isFavourite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String isFavourite(@RequestParam("bookId") String bookId, @RequestParam("userId") String userId) {
        try {
            return String.valueOf(booksDAO.isFavourite(bookId, userId));
        } catch (SQLException e) {
            LOGGER.error("ERROR", e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
        }
        return "-1";
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBooks(@RequestParam String userId, @RequestParam String query) {
        try {
            return objectMapper.writeValueAsString(booksDAO.findBooks(userId, query));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/getAdminHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAdminHistory(@RequestParam String userId) {
        try {
            return objectMapper.writeValueAsString(booksDAO.getAdminsApproves(userId));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filterBooks(@RequestParam String userId,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false) String author,
                              @RequestParam(required = false) String publisher,
                              @RequestParam(required = false) String isAvailable) {

        try {
            return objectMapper.writeValueAsString(booksDAO.filterBooks(userId, title, author, publisher, isAvailable));
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return null;
    }

    @RequestMapping(value = "/hasDueBooks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String hasDueBooks(@RequestParam String userId, @RequestParam String currentDate) {
        try {
            return booksDAO.hasDueBooks(userId, currentDate);
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return "-1";
    }

}
