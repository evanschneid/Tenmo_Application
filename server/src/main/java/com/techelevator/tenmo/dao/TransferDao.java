package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface TransferDao {


    boolean sendTransfer(User userSender, User userReceiver, double sendingAmount);

    List<Transfer> seeAllTransfers();

    Transfer getTransfer(User user, int transferId);
}
