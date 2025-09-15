package com.example.bank_rest_test_task.util;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * Сервис для шифрования/дешифрования номера банковской карты и вычисления хеша номера карты
 *
 * Использует класс {@link StringEncryptor} из библиотеки jasypt
 */
@Service
public class CryptoService {
    private final StringEncryptor encryptor;
    private final String hashKey;

    /**
     * @param encryptor шифратор строк
     * @param hashKey секрет для SHA-265 (из property)
     */
    public CryptoService(StringEncryptor encryptor, @Value("${payment.card.tokenization.hash-key}") String hashKey) {
        this.encryptor = encryptor;
        this.hashKey = hashKey;
    }

    /**
     * Шифрует номер карты.
     *
     * @param rawData сырой номер карты получаемый от пользователя
     * @return зашифрованный номер карты
     */
    public String encrypt(String rawData) {
        return encryptor.encrypt(rawData);
    }

    /**
     * Расшифровывает номер карты, полученный методом {@link #encrypt(String)}
     *
     * @param encryptData зашифрованный номер карты
     * @return исходная строка
     * @throws RuntimeException если расшифровка не удалась
     */
    public String decrypt(String encryptData) {
        return encryptor.decrypt(encryptData);
    }

    /**
     * Возвращается Hash код передаваемого номера карты закодированный в base64
     *
     * @param number номер карты
     * @return Base64-строка хэша
     * @throws IllegalArgumentException если ключ HMAC некорректный
     */
    public String calculationCardHash(String number) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(hashKey.getBytes(), "HmacSHA256"));
            byte[] hash = hmac.doFinal(number.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid HMAC key", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
