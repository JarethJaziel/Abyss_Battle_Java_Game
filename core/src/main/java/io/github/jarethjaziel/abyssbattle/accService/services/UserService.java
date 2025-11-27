package io.github.jarethjaziel.abyssbattle.accService.services;

import io.github.jarethjaziel.abyssbattle.accService.entities.User;
import io.github.jarethjaziel.abyssbattle.accService.repos.UserRepository;

public class UserService {

    private UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void registerUser(String username, String password) {
        User existing = userRepo.findByUsername(username);
        if (existing != null)
            throw new RuntimeException("Usuario ya existe.");

        User newUser = new User(
            0,
            username,
            Integer.toHexString(password.hashCode()),
            java.time.LocalDateTime.now()
        );

        userRepo.createUser(newUser);
    }

    public void loginUser(int id, String newPassword) {
        User u = userRepo.listUsers()
            .stream()
            .filter(x -> x.getId() == id)
            .findFirst()
            .orElse(null);

        if (u != null) {
            u.setPasswordHash(Integer.toHexString(newPassword.hashCode()));
            userRepo.updateUser(u);
        }
    }
}
