package example.com.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.core.exceptions.MysqlErrorNumbers;
import example.com.dataaccess.UsersDAO;
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
@RequestMapping("/user")
public class UserService {

    private final static Logger LOGGER = Logger.getLogger(UserService.class);
    @Autowired
    private UsersDAO usersDAO;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/login", method = RequestMethod.GET, params = {"username","password"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            return objectMapper.writeValueAsString(usersDAO.getUser(username, password));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, params = {"username","password","type"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String register(@RequestParam(value = "fullname") String fullname, @RequestParam("username") String username, @RequestParam("password") String password,
                           @RequestParam("type") String type) {
        try {
            return String.valueOf(usersDAO.registerUser(fullname, username, password, type));
        } catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) return "User with email address already registered";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DATA_TOO_LONG) return "Data truncation";
            if (e.getErrorCode() == MysqlErrorNumbers.ER_PARSE_ERROR) return "Invalid input";
        }
        return "-1";
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUser(@RequestParam String userId) {
        try {
            return objectMapper.writeValueAsString(usersDAO.getUserById(userId));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

}
