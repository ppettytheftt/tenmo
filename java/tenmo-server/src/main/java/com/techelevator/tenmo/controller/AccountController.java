package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDataInterface;
import com.techelevator.tenmo.dao.JdbcUserDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.NewTransfer;
import com.techelevator.tenmo.model.OtherUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {
    @Autowired
    private AccountDataInterface accountDataDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TransferDAO transferDAO;


    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        BigDecimal balance = new BigDecimal(String.valueOf(accountDataDAO.getBalanceByUser(principal.getName())));
        return balance;
    }

    @RequestMapping(path = "/get-all-users", method = RequestMethod.GET)
    public List<OtherUser> users(Principal principal) {
        List<OtherUser> users = userDAO.findAllButLoggedIn(principal.getName());
        for (OtherUser u : users) {
            System.out.println(u.getId() + " " + u.getUsername());
        }
        return users;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public NewTransfer newBalance(@RequestBody NewTransfer newTransfer) {
        transferDAO.deductFrom(newTransfer.getFromUserId(), newTransfer.getAmount());
        transferDAO.addMoneyTo(newTransfer.getToUserId(), newTransfer.getAmount());
        transferDAO.addToTransferTable(newTransfer);


        return newTransfer;
    }

    @RequestMapping(path = "/history/{id}", method = RequestMethod.GET)
    public List<Transfer> showHistory(@PathVariable int id) {
        List<Transfer> output = transferDAO.showTransfers(id);


        return output;

    }

    @RequestMapping(path = "/history/{id}/{transferId}", method = RequestMethod.GET)
    public Transfer historyByTransferId(@PathVariable int id, @PathVariable int transferId) {
        Transfer requestedByTransferId = transferDAO.getTransferById(id, transferId);
        return requestedByTransferId;
    }
}
