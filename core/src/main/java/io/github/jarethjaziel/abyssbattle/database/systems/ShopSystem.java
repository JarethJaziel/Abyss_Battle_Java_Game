package io.github.jarethjaziel.abyssbattle.database.systems;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager; // Importante para transacciones seguras

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.util.PurchaseResult;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class ShopSystem {

    private final DatabaseManager dbManager;
    private final UserInventorySystem inventorySystem;
    private Dao<Skin, Integer> skinDao;
    private Dao<User, Integer> userDao;

    public ShopSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.inventorySystem = new UserInventorySystem(dbManager);
        this.skinDao = dbManager.getSkinDao();
        this.userDao = dbManager.getUserDao();
    }

    /**
     * Intenta comprar una skin para un usuario.
     * Maneja automáticamente el descuento de monedas y la asignación del item.
     * 
     * @param user   El usuario que compra (debe tener el objeto actualizado con sus
     *               monedas)
     * @param skinId El ID de la skin que quiere comprar
     * @return PurchaseResult indicando el resultado
     */
    public PurchaseResult buySkin(User user, int skinId) {
        try {
            Skin skinToBuy = skinDao.queryForId(skinId);
            if (skinToBuy == null) return PurchaseResult.SKIN_NOT_FOUND;

            // Delegamos la verificación al experto (InventorySystem)
            if (inventorySystem.doesUserOwnSkin(user, skinId)) {
                return PurchaseResult.ALREADY_OWNED;
            }

            if (user.getCoins() < skinToBuy.getPrice()) {
                return PurchaseResult.INSUFFICIENT_FUNDS;
            }

            // TRANSACCIÓN
            TransactionManager.callInTransaction(dbManager.getConnectionSource(), () -> {
                // 1. Cobrar (Responsabilidad del Shop)
                user.purchase(skinToBuy.getPrice());;
                userDao.update(user);

                // 2. Entregar (Responsabilidad delegada al Inventory)
                inventorySystem.grantSkin(user, skinToBuy);
                
                return null;
            });

            return PurchaseResult.SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            return PurchaseResult.ERROR;
        }
    }
    /**
     * Obtiene todas las skins disponibles en el juego
     */
    public List<Skin> getAllSkins() {
        try {
            return skinDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene skins filtradas por tipo (ej: Solo CANNON o solo TROPA)
     */
    public List<Skin> getSkinsByType(SkinType type) {
        try {
            return skinDao.queryForEq("type", type);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}