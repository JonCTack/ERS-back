package com.revature.controllers;

import com.revature.daos.UserDAO;
import com.revature.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserDAO userDAO;

    @Autowired
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Create new user
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User u = userDAO.save(user);
        if(u == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.status(201).body(u);
    }

    // Get user
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {
        User u = userDAO.findById(userId);
        if (u == null) {
            return ResponseEntity.status(404).body("Could not find user with Id of: " + userId);
        }

        return ResponseEntity.ok(u);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userDAO.findAll());
    }

    // Delete a user & Delete all related reimbursements
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        User u = userDAO.findById(userId);
        if (u == null) {
            return ResponseEntity.status(404).body("Could not find user with ID of: " + userId);
        }

        userDAO.deleteById(userId);
        return ResponseEntity.accepted().body("User " + u.getFirstName() + " " + u.getLastName() + " and their reimbursements have been removed from the system.");
    }

    // Update an employee's status to manager
    // TODO: Not sure if we need to pass MANAGER or automatically assume MANAGER for role
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateRole(@RequestBody String role, @PathVariable int userId) {
        User u = userDAO.findById(userId);
        if (u == null) {
            return ResponseEntity.status(404).body("Could not find user with ID of: " + userId);
        }

        u.setRole(role);
        userDAO.save(u);
        return ResponseEntity.accepted().body(u);
    }

}
