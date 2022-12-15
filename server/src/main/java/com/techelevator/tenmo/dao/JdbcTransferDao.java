package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    private JdbcAccountDao jdbcAccountDao;
    private JdbcTransferDao(JdbcTemplate jdbcTemplate, JdbcAccountDao jdbcAccountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAccountDao = jdbcAccountDao;
    }

    // currently, this method is not updating our database to Approved with the parameter as int transferId, it was working with Transfer transfer
    // this method will most likely take in the reject status as well -- still need to look into this and see if it is the best route
    @Override
    public boolean updateRequestTransfer(int transferId, String transferStatus) { // int transferId
        if (transferStatus.equalsIgnoreCase("approved")) {
            String sql = "UPDATE transfer SET transfer_status = ? WHERE transfer_id = ?;";
            jdbcTemplate.update(sql, transferStatus, transferId);
            return true;
        } else {
            return false;
        }
    }

    // this method requests a transfer from a user (the sender is the person sending the request and the receiver is the one getting the request)
    // this means the receiver will be the person sending money to the person who initiated the request (kinda backwards but thats how venmo kinda works)
    @Override
    public boolean requestTransfer(int senderId, int receiverId, double sendingAmount) {
        if (senderId == receiverId) {
            // these souts will need to removed but they are used for testing purposes if something goes wrong with the method
            System.out.println("had an issue at step 1");
            return false;
        }
        if (sendingAmount <= jdbcAccountDao.getBalance(senderId) && sendingAmount > 0) {
            String sql = "INSERT INTO transfer (sender_id, receiver_id, transfer_type, transfer_amount, transfer_status) " +
                         "VALUES (?,?,'request',?,'pending');";
            jdbcTemplate.update(sql, senderId, receiverId, sendingAmount);
            return true;
        }
        else {
            System.out.println("had an issue at this step 2");
            return false;
        }
    }

    // updated this method to include the sending amount and it should be updating balances of both the sending and receiver
    @Override
    public boolean sendTransfer(int senderId, int receiverId, double sendingAmount) {
        if (senderId == receiverId){
            System.out.println("had an issue at step 1");
            return false;
        }
        if (sendingAmount <= jdbcAccountDao.getBalance(senderId) && sendingAmount > 0){
            String sql = "INSERT INTO transfer (sender_id, receiver_id, transfer_type, transfer_amount, transfer_status) " +
                         "VALUES (?,?,'send',?, 'approved'); ";
            jdbcTemplate.update(sql, senderId, receiverId, sendingAmount);
            // these next two lines update the balances to the sender and the receiver
            jdbcAccountDao.addToBalance(sendingAmount, receiverId);
            jdbcAccountDao.subtractToBalance(sendingAmount, senderId);
            return true;
        }
        else {
            System.out.println("had an issue at this step 2");
            return false;
        }

    }

    @Override
    public List<Transfer> seeAllTransfers(int userId) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_type, transfer_amount, Transfer_status " +
                     "FROM transfer " +
                     "WHERE sender_id = ? OR receiver_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            list.add(transfer);
        }
        return list;
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
        transfer.setTransferStatus(rowSet.getString("transfer_status"));
        return transfer;
    }
}
