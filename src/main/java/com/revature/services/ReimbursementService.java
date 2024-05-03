package com.revature.services;

import com.revature.daos.ReimbursementDAO;
import com.revature.daos.UserDAO;
import com.revature.models.Reimbursement;
import com.revature.models.User;
import com.revature.models.dtos.IncomingReimbDTO;
import com.revature.models.dtos.OutgoingReimbDTO;
import com.revature.models.dtos.OutgoingUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReimbursementService {

    private ReimbursementDAO reimbDAO;
    private UserDAO userDAO;

    @Autowired
    public ReimbursementService(ReimbursementDAO reimbDAO, UserDAO userDAO) {
        this.reimbDAO = reimbDAO;
        this.userDAO = userDAO;
    }

    // Create new reimb
    public Reimbursement addReimb(IncomingReimbDTO reimbDTO) throws IllegalArgumentException {
        // Check for valid inputs
        if (reimbDTO.getDescription().isBlank()) {
            throw new IllegalArgumentException("Reimbursement description cannot be empty");
        }

        if (reimbDTO.getAmount() == 0) {
            throw new IllegalArgumentException("Reimbursement amount cannot be empty");
        }

        // Don't think we need to check for user id since we grab it from session

        // Grab user from userId within reimbDTO
        Optional<User> u = userDAO.findById(reimbDTO.getUserId());
        if (u.isEmpty()) {
            throw new IllegalArgumentException("Should always return a user.  We're currently logged in as said user");
        }

        // Create reimbursement
        Reimbursement r = new Reimbursement(
                reimbDTO.getDescription(),
                reimbDTO.getAmount(),
                reimbDTO.getStatus(),
                u.get()
        );

        return reimbDAO.save(r);
    }

    // Get all reimbs
    @GetMapping
    public List<OutgoingReimbDTO> getAllReimbs(String role, int userId) {
        // Get all reimbs
        List<Reimbursement> reimbs;
        if(role.equals("MANAGER")) {
            reimbs = reimbDAO.findAll();
        }
        else {
            reimbs = reimbDAO.findByUserUserId(userId);
        }

        // New List to hold DTOs
        List<OutgoingReimbDTO> outgoingDTOs = new ArrayList<>();

        // Too much information in these, so we should put into DTO
        for (Reimbursement reimb : reimbs) {
            OutgoingReimbDTO outReimb = new OutgoingReimbDTO(
                    reimb.getReimbId(),
                    reimb.getDescription(),
                    reimb.getAmount(),
                    reimb.getStatus(),
                    reimb.getUser().getUserId()
            );

            // Add newly created DTOs to array list
            outgoingDTOs.add(outReimb);
        }

        // return array list
        return outgoingDTOs;
    }

    // Get all pending reimbs
    @GetMapping
    public List<OutgoingReimbDTO> getAllStatusReimbs(String role, int userId, String status) {
        // Get all pending reimbs
        List<Reimbursement> reimbs;
        if(role.equals("MANAGER")) {
            reimbs = reimbDAO.findByStatus(status);
        }
        else {
            reimbs = reimbDAO.findByStatusAndUserUserId(status, userId);
        }

        // New List to hold DTOs
        List<OutgoingReimbDTO> outgoingDTOs = new ArrayList<>();

        // Too much information in these, so we should put into DTO
        for (Reimbursement reimb : reimbs) {
            OutgoingReimbDTO outReimb = new OutgoingReimbDTO(
                    reimb.getReimbId(),
                    reimb.getDescription(),
                    reimb.getAmount(),
                    reimb.getStatus(),
                    reimb.getUser().getUserId()
            );

            // Add newly created DTOs to array list
            outgoingDTOs.add(outReimb);
        }

        // return array list
        return outgoingDTOs;
    }

    // Find reimb by ID
    public Optional<Reimbursement> findById(int reimbId) {
        return reimbDAO.findById(reimbId);
    }

    // Resolve a reimbursement
    public String resolveReimb(Reimbursement r) throws IllegalArgumentException {

        // Shouldn't ever throw this
        if (r == null) {
            throw new IllegalArgumentException("Reimbursement by that ID does not exist");
        }

        // Reimb has been found, user is confirmed manager.  Resolve
        reimbDAO.save(r);
        return "Reimbursement " + r.getReimbId() + " has been resolved to " + r.getStatus();
    }

    public String updateReimb(Reimbursement r) {

        // Shouldn't ever throw this
        if (r == null) {
            throw new IllegalArgumentException("Reimbursement by that ID does not exist");
        }

        reimbDAO.save(r);
        return "Reimbursement description has been updated to " + r.getDescription();
    }

}
