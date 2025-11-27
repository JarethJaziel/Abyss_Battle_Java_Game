package io.github.jarethjaziel.abyssbattle.accService.repos;

import java.util.List;
import io.github.jarethjaziel.abyssbattle.accService.entities.User;

public interface UserRepository {

    User findByUsername(String username);

    boolean createUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(String username);

    List<User> listUsers();
}
