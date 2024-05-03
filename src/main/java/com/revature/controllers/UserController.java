package com.revature.controllers;

import com.revature.models.User;
import com.revature.models.dtos.IncomingUserDTO;
import com.revature.models.dtos.OutgoingUserDTO;
import com.revature.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create new user, POST
    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody IncomingUserDTO userDTO) {
        // Don't have to be logged in, so no session check

        // Service throws error, so try catch
        try {
            // Create a user using adduser()
            userService.addUser(userDTO);

            // Return with successful user if no error thrown
            return ResponseEntity.ok(userDTO.getUsername() + " was created.");
        } catch (IllegalArgumentException e) {

            // Return error if error was thrown
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Get All Users, type ? to return a list or error
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpSession session) {

        // Check session
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You must be logged in to do this");
        }

        // Check if user is a manager
        if (!session.getAttribute("role").equals("MANAGER")) {
            return ResponseEntity.status(401).body("You must be a manager to do this");
        }

        // Manager is logged in

        // Shouldn't throw an error since we're just grabbing a list.
        // If there isn't a list, it'll return an empty list
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Delete User, DELETE
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId, HttpSession session) {
        // Check session

        // Invalid login session
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You must be logged in to do this");
        }

        // Login is successful

        // Check if user is a manager
        if (!session.getAttribute("role").equals("MANAGER")) {
            return ResponseEntity.status(401).body("You must be a manager to do this");
        }

        // Manager is logged in

        // Service throws error, so try catch
        try {
            // deleteUser returns a sting of deleted user, so we can return that string
            return ResponseEntity.ok(userService.deleteUser(userId));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Update an employee's status to manager
    // TODO: Not sure if we need to pass MANAGER or automatically assume MANAGER for role
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateRole(@PathVariable int userId, @RequestBody IncomingUserDTO role, HttpSession session) {
        // Check session

        // Invalid login session
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("You must be logged in to do this");
        }

        // Check if user is a manager
        if (!session.getAttribute("role").equals("MANAGER")) {
            return ResponseEntity.status(401).body("You must be a manager to do this");
        }

        // Manager is logged in

        // Find user by id
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Could not find user with ID of: " + userId);
        }

        // If user is found, set role
        User u = optionalUser.get();
        u.setRole(role.getRole());

        return ResponseEntity.accepted().body(userService.updateRole(u));
    }

    // Login User
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody IncomingUserDTO userDTO, HttpSession session) {

        try {
            Optional<User> oUser = userService.loginUser(userDTO);

            // We already did this, but we'll do it again for fun
            if (oUser.isEmpty()) {
                return ResponseEntity.status(401).body("Login failed");
            }

            User u = oUser.get();
            session.setAttribute("userId", u.getUserId());
            session.setAttribute("firstName", u.getFirstName());
            session.setAttribute("lastName", u.getLastName());
            session.setAttribute("role", u.getRole());

            return ResponseEntity.ok(new OutgoingUserDTO(
                    u.getUserId(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getRole()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }



    }

    /*
    // Get user
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {
        User u = userDAO.findById(userId);
        if (u == null) {
            return ResponseEntity.status(404).body("Could not find user with Id of: " + userId);
        }

        return ResponseEntity.ok(u);
    }
    */
}
