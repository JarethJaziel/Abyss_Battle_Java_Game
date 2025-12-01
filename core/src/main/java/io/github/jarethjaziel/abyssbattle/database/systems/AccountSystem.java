package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.util.Constants;

public class AccountSystem {

    private DatabaseManager dbManager;

    public AccountSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }



    public void registrarNuevoUsuario(String username, String password) throws SQLException {
        User nuevoUsuario = new User();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setNewPassword(password); // Recuerda hashear esto en la vida real
        dbManager.getUserDao().create(nuevoUsuario);

        asignarYEquiparSkin(nuevoUsuario, Constants.DEFAULT_TROOP_SKIN_ID);

        asignarYEquiparSkin(nuevoUsuario, Constants.DEFAULT_CANNON_SKIN_ID);
    }

    private void asignarYEquiparSkin(User usuario, int skinId) throws SQLException {
        Dao<Skin, Integer> skinDao = dbManager.getSkinDao();
        Dao<UserSkin, Integer> userSkinDao = dbManager.getUserSkinDao();
        Dao<UserLoadout, Integer> loadoutDao = dbManager.getUserLoadoutDao();

        Skin skinBasica = skinDao.queryForId(skinId);
        if (skinBasica == null) throw new RuntimeException("Error: Skin default ID " + skinId + " no existe en la DB.");

        UserSkin ownership = new UserSkin();
        ownership.setUser(usuario);
        ownership.setSkin(skinBasica);
        userSkinDao.create(ownership);

        UserLoadout loadout = new UserLoadout(usuario, skinBasica);
        loadoutDao.create(loadout);
    }

}
