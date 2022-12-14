package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    private Transfer transfer;
    private Account account;
    private User user;
    private JdbcAccountDao jdbcAccountDao;
    private JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // updated this method to include the sending amount and it should be updating balances of both the sending and receiver
    @Override
    public boolean sendTransfer(User userSender, User userReceiver, double sendingAmount) {
        if (userSender.getId() != userReceiver.getId()){
            return false;
        }
        if (sendingAmount <= jdbcAccountDao.getBalance(userSender.getId()) && sendingAmount > 0){
            String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_type, transfer_amount, Transfer_status " +
                         "FROM transfer " +
                         "WHERE sender_id = ? AND receiver_id = ?;";
            // these next two lines update the balances to the sender and the receiver
            jdbcAccountDao.addToBalance(sendingAmount, userReceiver.getId());
            jdbcAccountDao.subtractToBalance(sendingAmount, userSender.getId());
            jdbcTemplate.update(sql, userSender.getId(), userReceiver.getId());
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public List<Transfer> seeAllTransfers() {
        return null;
    }

    @Override
    public Transfer getTransfer(User user, int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_type, transfer_amount, Transfer_status " +
                     "FROM transfer " +
                     "WHERE (sender_id = ? OR receiver_id = ?) AND transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId(), user.getId(), transferId);
        if (results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setSenderId(rowSet.getInt("sender_id"));
        transfer.setReceiverId(rowSet.getInt("receiver_id"));
        transfer.setTransferType(rowSet.getString("transfer_type"));
        transfer.setTransferAmount(rowSet.getDouble("transfer_amount"));
        transfer.setTransferStatus(rowSet.getBoolean("transfer_status"));
        return transfer;
    }
}
