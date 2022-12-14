package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(path = "/transfer")
public class TransferController {

    private JdbcTransferDao jdbcTransferDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public TransferController(JdbcTransferDao jdbcTransferDao, JdbcUserDao userDao) {
        this.jdbcTransferDao = jdbcTransferDao;
        this.userDao = userDao;
    }

    // This method will most likely be removed, but keeping until the sendTransfer one below works
    @RequestMapping(method = RequestMethod.PUT)
    public void sendTransfer(@Valid Principal senderPrincipal, User receiver){ // change to int senderId instead of user???
        String senderName = senderPrincipal.getName();
       // jdbcTransferDao.sendTransfer(userDao.findByUsername(senderName), receiver); //---- added a double in the parameter so that is missing here now
        //transferDao.getTransfer() --- try and implement this once we get the getTransfer method up and running - change method from a void to return something
    }

    // trying to get the send transfer to work, currently the postman gives us false, but we want to return true
    // thinking about a take a turn to see why this is the case and if we are missing something important with this method or the jdbc sendTransfer one
    // maybe the missing extra path is the issue? or that might not be needed at this point.....
    @RequestMapping(method = RequestMethod.POST)
    public Boolean sendTransfer(@Valid Principal senderPrincipal, @RequestBody Transfer transfer) {
        // public Boolean sendTransfer(@Valid Principal senderPrincipal, String receiverName, double sentAmount) {
        String senderName = senderPrincipal.getName();
        boolean transfer2 = jdbcTransferDao.sendTransfer(userDao.findByUsername(senderName), userDao.findByUsername(userDao.findByUserId(transfer.getReceiverId()).getUsername()), transfer.getTransferAmount());
        // boolean transfer2 = jdbcTransferDao.sendTransfer(userDao.findByUsername(senderName), userDao.findByUsername(receiverName), sentAmount);
        return transfer2;
    }

    @RequestMapping(path = "/{transferId}", method = RequestMethod.GET)
    public Transfer getTransfer(@Valid Principal principal, int transferId){
        String name = principal.getName();
        Transfer transfer = jdbcTransferDao.getTransfer(userDao.findByUsername(name), transferId);
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer Not Found");
        }
        else {
            return transfer;
        }
    }
}
