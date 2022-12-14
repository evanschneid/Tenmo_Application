package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface AccountDao {

    Account createNewAccount(Account account);

    Account getAccount(User user);

    List<Account> listAllAccounts();

    double getBalance(int userId);

    double addToBalance(double amountToAdd, int id);

    double subtractToBalance(double amountToSubtract, int id);

}