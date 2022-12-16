package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class JdbcTransferDaoTest extends BaseDaoTests {

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcUserDao jdbcUserDao = new JdbcUserDao(jdbcTemplate);
        JdbcAccountDao jdbcAccountDao  = new JdbcAccountDao(jdbcTemplate,jdbcUserDao);
        sut = new JdbcTransferDao(jdbcTemplate,jdbcAccountDao );
    }

    @Test
    public void getTransferTest() {
        User user = new User();
        user.setId(1001);
        Transfer transfer = sut.getTransfer(user,3001);
        Assert.assertEquals(3001, transfer.getTransferId());

    }

    @Test
    public void getTransferList() {
        User user = new User();
        user.setId(1001);
        List<Transfer> transferResult = sut.seeAllTransfers(1001);
        Assert.assertEquals(2, transferResult.size()); // size
    }
    @Test
    public void sendTransferTest() {
        boolean transfer = sut.sendTransfer(1001,1002,500.00);
        Assert.assertTrue(transfer);
    }

    @Test
    public void requestTransferTest() {
        boolean transfer = sut.requestTransfer(1001,1002,500.00);
        Assert.assertTrue(transfer);
    }

    @Test
    public void sendZeroTransferTest() {
        boolean transfer = sut.sendTransfer(1001,1002,0.0);
        Assert.assertFalse(transfer);

    }

    @Test
    public void sendNegativeTransferTest() {
        boolean transfer = sut.sendTransfer(1001,1002,-500.00);
        Assert.assertFalse(transfer);

    }
    @Test
    public void sendMassiveAmountTransferTest() {
        boolean transfer = sut.sendTransfer(1001,1002,1000000.00);
        Assert.assertFalse(transfer);

    }
    @Test
    public void cantSendToSelfTransferTest() {
        boolean transfer = sut.sendTransfer(1001,1001,500.00);
        Assert.assertFalse(transfer);

    }

}
