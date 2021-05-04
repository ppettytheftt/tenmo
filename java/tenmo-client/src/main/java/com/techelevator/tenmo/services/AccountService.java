package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.NewTransfer;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.sound.midi.Sequence;
import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    private RestTemplate restTemplate = new RestTemplate();
    private static String BASE_URL = "http://localhost:8080/";

    public BigDecimal getBalance(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<BigDecimal> response = restTemplate.exchange(BASE_URL + "balance", HttpMethod.GET, entity, BigDecimal.class);
        return response.getBody();
    }

    public User[] getOtherUsers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<User[]> response = restTemplate.exchange(BASE_URL + "get-all-users", HttpMethod.GET, entity, User[].class);
        return response.getBody();
    }

    public NewTransfer transferMoney(String token, int fromUserId, int toUserId, BigDecimal amount) {
        NewTransfer newTransfer = new NewTransfer();
        newTransfer.setFromUserId(fromUserId);
        newTransfer.setToUserId(toUserId);
        newTransfer.setAmount(amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(newTransfer, headers);
        ResponseEntity<NewTransfer> response = restTemplate.exchange(BASE_URL + "transfer", HttpMethod.POST, entity, NewTransfer.class);
        return response.getBody();

    }

    public Transfer[] getHistory(String token, int currentUser) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<Transfer[]> response = restTemplate.exchange(BASE_URL + "history/" + currentUser, HttpMethod.GET, entity, Transfer[].class);
        return response.getBody();
    }

    public Transfer historyByTransferId(String token, int currentUser, int transferId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        Transfer transfer = restTemplate.exchange(BASE_URL + "history/" + currentUser + "/" + transferId, HttpMethod.GET, entity, Transfer.class).getBody();
        return transfer;
    }
}
