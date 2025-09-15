package com.example.bank_rest_test_task.util;

/**
 * Класс маскирующий номер карты
 */
public class CardFormattedService {
    private static final String MASK  = "**** **** **** ";
    private static final int VISIBLE_DIGITS = 4;

    /**
     * Накладывает маску
     *
     * @param cardNumber расшифрованный номер карты
     * @return замаскированный номер карты
     */
    public static String formatedMaskedCard(String cardNumber) {
        return MASK + cardNumber.substring(cardNumber.length() - VISIBLE_DIGITS);
    }

    /**
     * Получает первые 8 цифр карты
     *
     * @param rawCardNumber сырой номер карты полученный во время создания
     * @return первые 8 цифр карты
     */
    public static String getFirst8Number(String rawCardNumber) {
        return rawCardNumber.substring(0, 8);
    }

    /**
     * Получение последних 4 цифр карты
     *
     * @param rawCardNumber сырой номер карты полученный во время создания
     * @return первые 4 цифр карты
     */
    public static String getLast4Number(String rawCardNumber) {
        return rawCardNumber.substring(rawCardNumber.length() - 4);
    }
}
