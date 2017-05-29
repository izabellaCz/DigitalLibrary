package com.example.vlad.licenta.model;

import java.sql.Date;

public class History {

    private int id;
    private String userId;
    private Date loanDate;
    private Date returnDate;

    public History() {
    }

    public History(int id, String userId, Date loanDate, Date returnDate) {
        this.id = id;
        this.userId = userId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
}
