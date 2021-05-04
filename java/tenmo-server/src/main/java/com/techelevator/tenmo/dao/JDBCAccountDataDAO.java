package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class JDBCAccountDataDAO implements AccountDataInterface {

    private JdbcTemplate jdbcTemplate;
    public JDBCAccountDataDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalanceByUser(String username) {
        String sqlFindBalanceById= "select accounts.balance from accounts join users on accounts.user_id = users.user_id where users.username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindBalanceById,username);
        BigDecimal balance = new BigDecimal(0.00);
        while (results.next()){
            balance = results.getBigDecimal("balance");
        }

        return balance;
    }


    }





