package io.github.jarethjaziel.abyssbattle.accService.repos;

import io.github.jarethjaziel.abyssbattle.accService.entities.State;

public interface StateRepository {

    State findByUserId(int userId);

    boolean createStats(State stats);

    boolean updateStats(State stats);

    boolean deleteStatsByUserId(int userId);
}
