package io.github.jarethjaziel.abyssbattle.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para hashear y verificar contraseñas de forma segura.
 * Usa SHA-256 con salt aleatorio.
 */
public class PasswordUtils {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    public static String hashPassword(String password) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hashear contraseña con salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Convertir a Base64 para almacenar
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            // Retornar en formato "salt:hash"
            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña: " + e.getMessage());
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash almacenado.
     *
     * @param password Contraseña en texto plano
     * @param storedHash Hash almacenado en formato "salt:hash"
     * @return true si coinciden, false si no
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Separar salt y hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

            // Hashear la contraseña proporcionada con el mismo salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] actualHash = md.digest(password.getBytes());

            // Comparar byte a byte
            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al verificar contraseña: " + e.getMessage());
        }
    }
}
