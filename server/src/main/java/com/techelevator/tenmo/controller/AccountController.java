package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(path = "/account")
public class AccountController {

    private JdbcAccountDao jdbcAccountDao;
    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(JdbcAccountDao jdbcAccountDao, JdbcUserDao userDao) {
        this.jdbcAccountDao = jdbcAccountDao;
        this.userDao = userDao;
    }

    @PreAuthorize("permitAll") // this might be taken away in a real world scenario ********
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Account createNewAccount(@Valid @RequestBody Account account) { // this will probz need Principle for when a user wants multiple accounts
        return jdbcAccountDao.createNewAccount(account);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Account getAccount(@Valid Principal principal) {
        String name = principal.getName();
        Account account = jdbcAccountDao.getAccount(userDao.findByUsername(name));
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        }
        else {
            return account;
        }
    }

}