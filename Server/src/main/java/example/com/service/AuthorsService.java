package example.com.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.core.exceptions.MysqlErrorNumbers;
import example.com.dataaccess.AuthorsDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

@Controller
@RequestMapping("/authors")
public class AuthorsService {

    private final static Logger LOGGER = Logger.getLogger(AuthorsService.class);
    @Autowired
    private AuthorsDAO authorsDAO;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/add", method = RequestMethod.GET, params = {"name"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String add(@RequestParam("name") String name) {
        try {
            return String.valueOf(authorsDAO.registerAuthor(name));
        } catch (SQLException e) {
            LOGGER.error("SQL EXCEPTION: " + e);
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) return "Duplicate entry";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DATA_TOO_LONG) return "Author name too long";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid characters in author's name";
        }
        return "-1";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listAll() {
        try {
            return objectMapper.writeValueAsString(authorsDAO.listAllAuthors());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;

    }

}
