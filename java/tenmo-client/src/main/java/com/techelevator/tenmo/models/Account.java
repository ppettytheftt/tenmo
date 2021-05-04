package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Account {
    private int user_id;
    private BigDecimal balance;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
