package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

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

    // this method alls a principle user (logged-in user) to send a transfer to another user
    @RequestMapping(method = RequestMethod.POST)
    public Boolean sendTransfer(@Valid Principal senderPrincipal, @RequestBody Transfer transfer) {
        String senderName = senderPrincipal.getName();
        boolean transfer2 = jdbcTransferDao.sendTransfer(userDao.findByUsername(senderName).getId(),
                transfer.getReceiverId(), transfer.getTransferAmount());
        return transfer2;
    }

    // this method gets a specific transfer and must be connected to the principle user (logged-in user)
    @RequestMapping(path = "/{transferId}", method = RequestMethod.GET)
    public Transfer getTransfer(@Valid Principal principal, @PathVariable int transferId){
        String name = principal.getName();
        Transfer transfer = jdbcTransferDao.getTransfer(userDao.findByUsername(name), transferId);
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer Not Found");
        }
        else {
            return transfer;
        }
    }

    // this method gets a list of all transfer associated to the principle user (logged-in user)
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public List<Transfer> getTransferList(@Valid Principal principal) {
        String name = principal.getName();
        List<Transfer> transferList = jdbcTransferDao.seeAllTransfers(userDao.findByUsername(name).getId());
        if (transferList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer List Not Found");
        }
        else {
            return transferList;
        }
    }

    // This method requests a transfer from another user
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public boolean requestTransfer(@Valid Principal principal, @RequestBody Transfer transfer) {
        String senderName = principal.getName();
        boolean transfer2 = jdbcTransferDao.requestTransfer(userDao.findByUsername(senderName).getId(),
                transfer.getReceiverId(), transfer.getTransferAmount());
        return transfer2;
    }

    // This method was working when we had the parameters for the updateRequestMethod as Transfer transfer, but now it is not working when we have it as int transferId
    // commented out option below is the code that was working before but this was not protected by the principal
    @RequestMapping(path = "/pending/{transferId}", method = RequestMethod.PUT)
    public boolean updatedTransferRequestStatus(@Valid Principal principal, @RequestBody Transfer transfer, @PathVariable int transferId) {
        String receiverName = principal.getName();
        boolean transfer2 = jdbcTransferDao.updateRequestTransfer(jdbcTransferDao.findTransferId(userDao.findByUsername(receiverName).getId()), transfer.getTransferStatus());
        //boolean transfer2 = jdbcTransferDao.updateRequestTransfer(userDao.findByUsername(receiverName).getId(), transfer.getTransferStatus());
        //boolean test = jdbcTransferDao.updateRequestTransfer(transfer, transfer.getTransferStatus());
        return transfer2;
    }


}
