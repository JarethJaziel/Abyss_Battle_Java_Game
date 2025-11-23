package io.github.jarethjaziel.abyssbattle.accService.services;

import io.github.jarethjaziel.abyssbattle.accService.entities.State;
import io.github.jarethjaziel.abyssbattle.accService.repos.StateRepository;

public class StateService {

    private StateRepository stateRepo;

    public StateService(StateRepository repo) {
        this.stateRepo = repo;
    }

    public void recordMatchResult(int userId, int result) {
        State st = stateRepo.findByUserId(userId);

        if (st == null) {
            st = new State(userId, 0, 0, 0, 0, 0, 0, java.time.LocalDateTime.now());
            stateRepo.createStats(st);
        }

        st.setPlayed(st.getPlayed() + 1);

        if (result == 1) st.setWon(st.getWon() + 1);
        if (result == -1) st.setLost(st.getLost() + 1);

        st.setLastPlayed(java.time.LocalDateTime.now());

        stateRepo.updateStats(st);
    }

    public State getStateForUser(int id) {
        return stateRepo.findByUserId(id);
    }
}
