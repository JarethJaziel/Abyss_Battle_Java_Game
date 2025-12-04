package io.github.jarethjaziel.abyssbattle.database.systems;

import com.badlogic.gdx.Gdx;
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

/**
 * Sistema responsable de la lógica de comercio del juego.
 * <p>
 * Gestiona el catálogo de skins, valida las transacciones de compra (fondos suficientes,
 * propiedad previa) y actualiza el saldo del usuario de manera atómica.
 */
public class ShopSystem {

    private static final String TAG = ShopSystem.class.getSimpleName();

    private final DatabaseManager dbManager;
    private final UserInventorySystem inventorySystem;
    private Dao<Skin, Integer> skinDao;
    private Dao<User, Integer> userDao;

    /**
     * Constructor del sistema de tienda.
     * @param dbManager Gestor de base de datos para obtener conexiones y DAOs.
     */
    public ShopSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.inventorySystem = new UserInventorySystem(dbManager);
        this.skinDao = dbManager.getSkinDao();
        this.userDao = dbManager.getUserDao();
    }

    /**
     * Intenta procesar la compra de una skin para un usuario.
     * <p>
     * Realiza las siguientes validaciones y operaciones en una transacción atómica:
     * <ol>
     * <li>Verifica que la skin exista.</li>
     * <li>Verifica que el usuario no la tenga ya.</li>
     * <li>Verifica que el usuario tenga saldo suficiente.</li>
     * <li>Descuenta las monedas.</li>
     * <li>Otorga la propiedad de la skin.</li>
     * </ol>
     * * @param user   El usuario comprador.
     * @param skinId El ID de la skin a comprar.
     * @return {@link PurchaseResult} indicando el éxito o la razón del fallo.
     */
    public PurchaseResult buySkin(User user, int skinId) {
        try {
            Skin skinToBuy = skinDao.queryForId(skinId);

            if (skinToBuy == null) {
                Gdx.app.error(TAG, "Intento de compra fallido: Skin ID " + skinId + " no encontrada.");
                return PurchaseResult.SKIN_NOT_FOUND;
            }

            if (inventorySystem.doesUserOwnSkin(user, skinId)) {
                Gdx.app.log(TAG, "Compra rechazada: El usuario ya posee " + skinToBuy.getName());
                return PurchaseResult.ALREADY_OWNED;
            }

            if (user.getCoins() < skinToBuy.getPrice()) {
                Gdx.app.log(TAG, "Compra rechazada: Fondos insuficientes (" + user.getCoins() + " < " + skinToBuy.getPrice() + ")");
                return PurchaseResult.INSUFFICIENT_FUNDS;
            }

            // TRANSACCIÓN
            TransactionManager.callInTransaction(dbManager.getConnectionSource(), () -> {
                user.purchase(skinToBuy.getPrice());
                userDao.update(user);

                inventorySystem.grantSkin(user, skinToBuy);
                
                return null;
            });

            Gdx.app.log(TAG, "Compra exitosa: " + skinToBuy.getName() + " por " + skinToBuy.getPrice() + " monedas.");
            return PurchaseResult.SUCCESS;

        } catch (Exception e) {
            Gdx.app.error(TAG, "Error crítico durante la transacción de compra", e);
            return PurchaseResult.ERROR;
        }
    }
    /**
     * Obtiene el catálogo completo de skins disponibles.
     * @return Lista de skins o lista vacía en caso de error.
     */
    public List<Skin> getAllSkins() {
        try {
            return skinDao.queryForAll();
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error recuperando catálogo de skins", e);
            return Collections.emptyList();
        }
    }

    /**
     * Filtra el catálogo de skins por tipo (Ej: Solo Tropas).
     * @param type El tipo de skin a buscar.
     * @return Lista de skins filtrada.
     */
    public List<Skin> getSkinsByType(SkinType type) {
        try {
            return skinDao.queryForEq("type", type);
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error filtrando skins por tipo: " + type, e);
            return Collections.emptyList();
        }
    }

}
