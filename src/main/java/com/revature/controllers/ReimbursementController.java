package com.revature.controllers;

import com.revature.daos.ReimbursementDAO;
import com.revature.daos.UserDAO;
import com.revature.models.Reimbursement;
import com.revature.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reimbursements")
public class ReimbursementController {

    private ReimbursementDAO reimbDAO;
    private UserDAO userDAO;

    @Autowired
    public ReimbursementController(ReimbursementDAO reimbDao, UserDAO userDAO) {
        this.reimbDAO = reimbDAO;
        this.userDAO = userDAO;
    }

    // Create new reimb
    @PostMapping
    public ResponseEntity<Object> addReimb(Reimbursement reimb) {
        Reimbursement r = reimbDAO.save(reimb);

        if(r == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(r);
    }

    // TODO: Check if this is possible in one or reuse getAllReimbs/getAllPendingReimbs
    // Get ALL reimb (for userID) && (manager) ==== Need to check if user is MANAGER
    @GetMapping
    public ResponseEntity<List<Reimbursement>> getAllReimbs(User u) {

        // If reimb list is empty
        List<Reimbursement> reimbList = reimbDAO.findAll();
        if (reimbList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // If user is a MANAGER
        if (u.getRole().equals("MANAGER")) {
            return ResponseEntity.ok(reimbList);
        }

        // If user is NOT a MANAGER
        List<Reimbursement> userReimbs = new ArrayList<>();
        for (Reimbursement reimb : reimbList) {
            if (reimb.getUserId() == u.getUserId()) {
                userReimbs.add(reimb);
            }
        }
        return ResponseEntity.ok(userReimbs);
    }

    // Get ALL reimb (status == PENDING) (manager)
    @GetMapping
    public ResponseEntity<List<Reimbursement>> getAllPendingReimbs(User u) {
        // If reimb list is empty
        List<Reimbursement> reimbList = reimbDAO.findAll();
        if (reimbList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Get list of all PENDING reimbs
        List<Reimbursement> allPendingReimbsList = new ArrayList<>();
        for (Reimbursement reimb : reimbList) {
            if (reimb.getStatus().equals("PENDING")) {
                allPendingReimbsList.add(reimb);
            }
        }

        // If user is a MANAGER
        if (u.getRole().equals("MANAGER")) {
            return ResponseEntity.ok(allPendingReimbsList);
        }

        // If user is NOT a MANAGER
        List<Reimbursement> userReimbs = new ArrayList<>();
        for (Reimbursement reimb : allPendingReimbsList) {
            if (reimb.getUserId() == u.getUserId()) {
                userReimbs.add(reimb);
            }
        }
        return ResponseEntity.ok(userReimbs);
    }

    // TODO: Check if this is possible to reuse or combine == update/resolve
    // Update desc of reimb
    @PatchMapping("/{reimbId")
    public ResponseEntity<Object> updateReimb(@RequestBody String desc, @PathVariable int reimbId) {
        Reimbursement r = reimbDAO.findById(reimbId);
        if (r == null) {
            return ResponseEntity.status(404).body("Could not find reimbursement with ID of: " + reimbId);
        }

        r.setDescription(desc);
        reimbDAO.save(r);
        return ResponseEntity.accepted().body(r);
    }

    // Resolve a reimb (from PENDING to APPROVED/DENIED)
    @PatchMapping("/{reimbId}")
    public ResponseEntity<Object> resolveReimb(@RequestBody String status, @PathVariable int reimbId) {
        Reimbursement r = reimbDAO.findById(reimbId);
        if (r == null) {
            return ResponseEntity.status(404).body("Could not find reimbursement with ID of: " + reimbId);
        }

        r.setStatus(status);
        reimbDAO.save(r);
        return ResponseEntity.accepted().body(r);
    }
}
