package com.revature.controllers;

import com.revature.models.Reimbursement;
import com.revature.models.dtos.IncomingReimbDTO;
import com.revature.models.dtos.OutgoingReimbDTO;
import com.revature.services.ReimbursementService;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reimbursements")
public class ReimbursementController {

    private ReimbursementService reimbService;

    @Autowired
    public ReimbursementController(ReimbursementService reimbService) {
        this.reimbService = reimbService;
    }

    // Create new reimb
    @PostMapping("/create")
    public ResponseEntity<String> addReimb(@RequestBody IncomingReimbDTO reimbDTO, HttpSession session) {
        // User data will be in session, reimb data in reimb

        // Check for login
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You need to be logged in to do this");
        }

        // Grab current user to attach to reimb TODO: Remember to cast (int)
        reimbDTO.setUserId((int)session.getAttribute("userId"));

        // addReimb throws error, so try catch, return entire Reimb object
        try {
            // Creating a new object to make sure data is saved
            Reimbursement r = reimbService.addReimb(reimbDTO);
            return ResponseEntity.ok("Reimbursement process for " + "$" + r.getAmount() + " has been successfully initialized");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }

    }

    // Get ALL reimb (for userID) && (manager) ==== Need to check if user is MANAGER
    @GetMapping
    public ResponseEntity<?> getAllReimbs(HttpSession session) {

        // Check for login
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You need to be logged in to do this");
        }

        // Get all reimbursements
        List<OutgoingReimbDTO> reimbList = reimbService.getAllReimbs(
                (String)session.getAttribute("role"),
                (int)session.getAttribute("userId"));

        // If reimb list is empty
        if (reimbList.isEmpty()) {
            return ResponseEntity.status(204).body("There are no reimbursements here for you :(");
        }

        return ResponseEntity.ok(reimbList);
    }

    // Get ALL <STATUS> reimbs
    @GetMapping("/status")
    public ResponseEntity<?> getAllStatusReimbs(@RequestBody String status, HttpSession session) {

        // Check for login
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You need to be logged in to do this");
        }

        // Get all reimbursements
        List<OutgoingReimbDTO> reimbList = reimbService.getAllStatusReimbs(
                (String)session.getAttribute("role"),
                (int)session.getAttribute("userId"),
                status);

        // If reimb list is empty
        if (reimbList.isEmpty()) {
            return ResponseEntity.status(204).body("There are no reimbursements here for you :(");
        }

        return ResponseEntity.ok(reimbList);
    }

    // Resolve a reimb (from PENDING to APPROVED/DENIED)
    @PatchMapping("/{reimbId}/resolve")
    public ResponseEntity<Object> resolveReimb(@RequestBody String status, @PathVariable int reimbId, HttpSession session) {

        // Check for login
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You need to be logged in to do this");
        }

        // Check if user is a manager
        if (!session.getAttribute("role").equals("MANAGER")) {
            return ResponseEntity.status(401).body("You must be a manager to do this");
        }

        // If reimb is not found
        Optional<Reimbursement> optionalReimbursement = reimbService.findById(reimbId);
        if (optionalReimbursement.isEmpty()) {
            return ResponseEntity.status(404).body("Could not find reimbursement with ID of: " + reimbId);
        }

        // Reimb is found, set values
        Reimbursement r = optionalReimbursement.get();
        r.setStatus(status);

        return ResponseEntity.ok(reimbService.resolveReimb(r));
    }


    // Update desc of reimb
    @PatchMapping("/{reimbId}/description")
    public ResponseEntity<Object> updateReimb(@RequestBody String desc, @PathVariable int reimbId, HttpSession session) {

        // Check for login
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You need to be logged in to do this");
        }

        // If reimb is not found
        Optional<Reimbursement> optionalReimbursement = reimbService.findById(reimbId);
        if (optionalReimbursement.isEmpty()) {
            return ResponseEntity.status(404).body("Could not find reimbursement with ID of: " + reimbId);
        }

        // Reimb is found, check if user owns
        if (optionalReimbursement.get().getUser().getUserId() != (int)session.getAttribute("userId")) {
            return ResponseEntity.status(401).body("This is not yours to edit");
        }

        // set values
        Reimbursement r = optionalReimbursement.get();
        r.setDescription(desc);

        return ResponseEntity.ok(reimbService.resolveReimb(r));
    }
}
