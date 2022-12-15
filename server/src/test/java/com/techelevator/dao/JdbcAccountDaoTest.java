package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcAccountDaoTest extends BaseDaoTests{

    private JdbcAccountDao sut;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcUserDao jdbcUserDao = new JdbcUserDao(jdbcTemplate);
        sut = new JdbcAccountDao(jdbcTemplate, jdbcUserDao);
    }

/*    @Test
    public void getAccountTest() {

        Account account = sut.getAccount(user);
        Assert.assertEquals("2001", account.getAccountId());
    }*/

    @Test
    public void getBalanceTest() {
        double accountResults = sut.getBalance(1001);
        Assert.assertEquals(1000, accountResults, 2);
    }

    @Test
    public void addToBalanceTest() {
        double accountResults = sut.addToBalance(500, 1001);
        Assert.assertEquals(1500, accountResults, 2);
    }

    @Test
    public void subtractToBalanceTest() {
        double accountResults = sut.subtractToBalance(500, 1001);
        Assert.assertEquals(500, accountResults, 2);
    }

}
