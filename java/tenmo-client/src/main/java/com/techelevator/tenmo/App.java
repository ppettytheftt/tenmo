package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {


    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private JdbcTemplate jdbcTemplate;
    private AccountService accountService;


    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = new AccountService();
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalance(currentUser.getToken());
        System.out.println("Your current account balance is: \nTE$" + balance + "\n");

    }

    private void viewTransferHistory() {
        System.out.println("-------------------------------------------\n" + "TRANSFER HISTORY:\n" + "\n" +
                "TRANSFER ID      FROM/TO        AMOUNT\n-------------------------------------------");
        Transfer[] transferList = accountService.getHistory(currentUser.getToken(), currentUser.getUser().getId());
        for (Transfer transfer : transferList) {

            if (transfer.isWasSentToUs()){
                System.out.println(transfer.getTransferId() + "          FROM: " + transfer.getOtherUser() +
                            "       TE$" + transfer.getAmount() + "\n");
                } else {
                    System.out.println(transfer.getTransferId() + "          TO:   " + transfer.getOtherUser() +
                            "       TE$" + transfer.getAmount() + "\n");
                }
            }

        int transferId = console.getUserInputInteger("Enter the transfer ID for the transfer you want to view in detail");

        for (Transfer requestedTransfer : transferList) {
            if (requestedTransfer.getTransferId() == transferId && requestedTransfer.isWasSentToUs()) {
                Transfer transferById = accountService.historyByTransferId(currentUser.getToken(), currentUser.getUser().getId(), transferId);
                System.out.println("\nID: " + transferById.getTransferId() + "\nFrom User: " + transferById.getOtherUser() + "\nTo User: " + currentUser.getUser().getUsername() +
                        "\nTransfer status: Approved" + "\nFor the Amount of: TE$" + transferById.getAmount());
                break;

            } else if (requestedTransfer.getTransferId() == transferId && !requestedTransfer.isWasSentToUs()) {
                Transfer transferById = accountService.historyByTransferId(currentUser.getToken(), currentUser.getUser().getId(), transferId);
                System.out.println("\nID: " + transferById.getTransferId() + "\nFrom User: " + currentUser.getUser().getUsername() + "\nTo User: " + transferById.getOtherUser() +
                        "\nTransfer Status: Approved" + "\nFor the Amount of: TE$" + transferById.getAmount());
                break;
            }
        }

        }



    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void sendBucks() {
        System.out.println("-------------------------------------------\n" + "Users\n" + "ID		Name\n" + "-------------------------------------------\n");
        List<User> otherUsersList = new ArrayList<>(Arrays.asList(accountService.getOtherUsers(currentUser.getToken())));

        for (User u : otherUsersList) {
            System.out.println(u.getId() + "        " + u.getUsername());
        }
        NewTransfer newTransfer = new NewTransfer();
        int accountTo = console.getUserInputInteger("Enter the ID you are sending to (0 to Cancel)");
        boolean accountFound = false;
        if (accountTo == 0) {
            System.out.println("Returning to Main Menu");
        } else {
            for (User u : otherUsersList) {
                if (u.getId() == accountTo) {
                    newTransfer.setToUserId(accountTo);


                    accountFound = true;
                }

            }
            if (!accountFound) {
                System.out.println("Not a valid User ID!");
            } else {

                BigDecimal amountToTransfer = BigDecimal.valueOf(Double.parseDouble(console.getUserInput("Enter amount of transfer")));
                newTransfer.setAmount(amountToTransfer);
                BigDecimal userBalance = accountService.getBalance(currentUser.getToken());
                if (userBalance.compareTo(amountToTransfer) < 0) {
                    System.out.println("NOT ENOUGH MONEY!");
                } else {
                    accountService.transferMoney(currentUser.getToken(), currentUser.getUser().getId(), newTransfer.getToUserId(), newTransfer.getAmount());
                }
            }
        }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
