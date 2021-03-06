package example.com.model;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class History {

    private int id;
    private int userId;
    private int loanApprId;
    private int returnApprId;
    private Date loanDate;
    private Date returnDate;

    public History() {
    }

    public History(int id, int userId, int loanApprId, int returnApprId, Date loanDate, Date returnDate) {
        this.id = id;
        this.userId = userId;
        this.loanApprId = loanApprId;
        this.returnApprId = returnApprId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public static History constructFromResultSet(ResultSet resultSet) throws SQLException, UnsupportedEncodingException {
        return new History(
                resultSet.getInt("history_id"),
                resultSet.getInt("history_user_id"),
                resultSet.getInt("loan_appr_id"),
                resultSet.getInt("return_appr_id"),
                resultSet.getDate("loan_date"),
                resultSet.getDate("return_date")
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getLoanApprId() {
        return loanApprId;
    }

    public void setLoanApprId(int loanApprId) {
        this.loanApprId = loanApprId;
    }

    public int getReturnApprId() {
        return returnApprId;
    }

    public void setReturnApprId(int returnApprId) {
        this.returnApprId = returnApprId;
    }
}
