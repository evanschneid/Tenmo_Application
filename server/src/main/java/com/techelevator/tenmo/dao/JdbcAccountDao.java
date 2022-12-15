package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate, JdbcUserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }

/*    @Override
    public Account createNewAccount(Account account) {
        // create new account
        String sql = "INSERT INTO account (user_id, balance)  VALUES (?,?) RETURNING account_id;";
        try {
            int newId = jdbcTemplate.queryForObject(sql, int.class, account.getUserId(), account.getBalance());
            account.setAccountId(newId);
            return account;
        } catch (DataAccessException e) {
            return account;
        }
    }*/

    @Override
    public Account getAccount(int userId) {
        String sql = "SELECT account_id, user_id, balance " +
                     "FROM account " +
                     "WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        Account account = null;
        if (results.next()){
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public List<Account> listAllAccounts() {
        return null;
    }

    // need to look at the catches and see if these are good or not - threw them in to be fancy for now
    @Override
    public double getBalance(int userId) {
        double balance = 0.00;
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                balance = results.getDouble("balance");
            }
        } catch (DataAccessException e) {
            System.out.println("something went wrong here");
        }
        return balance;
    }

    // need to look at the catches and see if these are good or not - threw them in to be fancy for now
    @Override
    public double addToBalance(double amountToAdd, int id) {
        Account account = getAccount(userDao.findByUserId(id).getId());
        double updatedBalance = account.getBalance() + amountToAdd;
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?;";
        try {
            jdbcTemplate.update(sql, updatedBalance, id);
        }
        catch (DataAccessException e) {
            System.out.println("Placeholder");
        }
        return account.getBalance();
    }

    // need to look at the catches and see if these are good or not - threw them in to be fancy for now
    @Override
    public double subtractToBalance(double amountToSubtract, int id) {
        Account account = getAccount(userDao.findByUserId(id).getId());
        double updatedBalance = account.getBalance() - amountToSubtract;
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?;";
        try {
            jdbcTemplate.update(sql, updatedBalance, id);
        }
        catch (DataAccessException e) {
            return account.getBalance();
        }
        return account.getBalance();
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getDouble("balance"));
        return account;
    }
}
