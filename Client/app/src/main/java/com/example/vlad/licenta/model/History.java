package com.example.vlad.licenta.model;

import java.sql.Date;

public class History {

    private int id;
    private String userId;
    private int loanApprId;
    private int returnApprId;
    private Date loanDate;
    private Date returnDate;

    public History() {
    }

    public History(int id, String userId, int loanApprId, int returnApprId, Date loanDate, Date returnDate) {
        this.id = id;
        this.userId = userId;
        this.loanApprId = loanApprId;
        this.returnApprId = returnApprId;
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
