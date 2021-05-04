package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.OtherUser;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    String findUsernameById(int userId);

    boolean create(String username, String password);

    List<OtherUser> findAllButLoggedIn(String username);
}
