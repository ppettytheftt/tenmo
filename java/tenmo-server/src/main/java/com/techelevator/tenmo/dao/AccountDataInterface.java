package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDataInterface {
    BigDecimal getBalanceByUser (String username);

}
