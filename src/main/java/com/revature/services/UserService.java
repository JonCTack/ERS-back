package com.revature.services;

import com.revature.daos.UserDAO;
import com.revature.models.User;
import com.revature.models.dtos.IncomingUserDTO;
import com.revature.models.dtos.OutgoingUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Register User
    public User addUser(IncomingUserDTO userDTO) throws IllegalArgumentException{
        // Data processing and error handling

        // Check for valid inputs
        if (userDTO.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First Name cannot be empty");
        }

        if (userDTO.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last Name cannot be empty");
        }

        if (userDTO.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Checks pass, create user and return user
        User u = new User(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getRole());

        return userDAO.save(u);
    }

    // Get All Users
    public List<OutgoingUserDTO> getAllUsers() {
        // Get all users using findAll()
        List<User> userList = userDAO.findAll();

        // Create an arrayList to hold outgoing users using a DTO to give only required data (no username/password)
        List<OutgoingUserDTO> outgoingUserList = new ArrayList<>();
        for (User u : userList) {
            OutgoingUserDTO outUser = new OutgoingUserDTO(u.getUserId(), u.getFirstName(), u.getLastName());
            outgoingUserList.add(outUser);
        }
        return outgoingUserList;
    }

    // Delete User
    public String deleteUser(int userId) {
        // Check if user exists
        Optional<User> oUser = userDAO.findById(userId);

        // If user doesn't exist, return error
        if (oUser.isEmpty()) {
            throw new NoSuchElementException("User doesn't exist");
        }

        // User exists! Delete user
        userDAO.deleteById(userId);
        return oUser.get().getUsername() + " was deleted";
    }

    public Optional<User> findById(int userId) {
        // Look for user in database by id
        return userDAO.findById(userId);
    }

    public String updateRole(User user) {
        userDAO.save(user);
        return user.getFirstName() + " " + user.getLastName() + " is now a " + user.getRole();
    }

    // Login User

    public Optional<User> loginUser(IncomingUserDTO userDTO) throws IllegalArgumentException {
        // Check for valid inputs
        if (userDTO.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Find user by username/password combo
        Optional<User> u = userDAO.findByUsernameAndPassword(userDTO.getUsername(), userDTO.getPassword());
        if (u.isEmpty()) {
            throw new IllegalArgumentException("Username and Password combo does not exist");
        }

        // Return user
        return u;
    }
}
