package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.CardCreateDto;
import com.example.bank_rest_test_task.dto.CardSearchFilter;
import com.example.bank_rest_test_task.entity.Card;
import com.example.bank_rest_test_task.entity.StatusCard;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.exception.CardDuplicateException;
import com.example.bank_rest_test_task.exception.CardNotFoundException;
import com.example.bank_rest_test_task.exception.UserNotFoundException;
import com.example.bank_rest_test_task.repository.CardRepository;
import com.example.bank_rest_test_task.repository.CardSpecification;
import com.example.bank_rest_test_task.util.CardFormattedService;
import com.example.bank_rest_test_task.util.CryptoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис управления картами.
 *
 * Операции:
 * - создание (с проверкой дубликатов и шифрованием номера);
 * - поиск и получение по номеру, id, пользователю;
 * - обновление статуса;
 * - удаление.
 *
 * Номер хранится в зашифрованной форме; для поиска используется хэш (HMAC).
 */
@Service
public class CardService {
    private final CardRepository cardRepository;
    private final CryptoService cryptoService;
    private final UserService userService;


    /**
     * @param cardRepository репозиторий для работы с картами
     * @param cryptoService сервис для хеширования
     * @param userService сервис для работы с пользователями
     */
    public CardService(CardRepository cardRepository, CryptoService cryptoService, UserService userService) {
        this.cardRepository = cardRepository;
        this.cryptoService = cryptoService;
        this.userService = userService;
    }


    /**
     * Создает и сохраняет карту для указанного пользователя.
     *
     * Логика:
     * 1. Находит пользователя по {@code userId} используя метод {@link UserService#findUserById(Long)}
     * 2. Вычисляет поисковой Hash номера с помощью метода {@link CryptoService#calculationCardHash(String)}
     * 3. Проверяет что такой хэш еще не существует с помощью метода {@link CardRepository#existsBySearchHash(String)}
     * 4. Шифрует номер карты с помощью метода {@link CryptoService#encrypt(String)} и сохраняет карту {@link CardRepository#save(Object)} со статусом {@code ACTIVE}
     *
     * @param cardCreateDto DTO с данными карты и id пользователя
     * @throws UserNotFoundException если пользователя с передаваемым id не существует
     * @throws CardDuplicateException если карта (по вычисленному поисковому хэшу) уже существует
     */
    @Transactional
    public void createCard(CardCreateDto cardCreateDto) throws UserNotFoundException {
        User user = userService.findUserById(cardCreateDto.userId());
        if (cardRepository.existsBySearchHash(cryptoService.calculationCardHash(cardCreateDto.cardNumber()))) {
            throw new CardDuplicateException("Card by number: %s already exists".formatted(cardCreateDto.cardNumber()));
        }
        Card card = Card.builder()
                .encryptNumber(cryptoService.encrypt(cardCreateDto.cardNumber()))
                .user(user)
                .validityPeriod(cardCreateDto.validityPeriod())
                .searchHash(cryptoService.calculationCardHash(cardCreateDto.cardNumber()))
                .statusCard(StatusCard.ACTIVE)
                .first8(CardFormattedService.getFirst8Number(cardCreateDto.cardNumber()))
                .last4(CardFormattedService.getLast4Number(cardCreateDto.cardNumber()))
                .build();
        cardRepository.save(card);
    }

    /**
     * Проверяет существование карты по ее номеру.
     *
     * Логика:
     * 1. Высчитывает Hash для поиска карты по номеру с помощью метода {@link CryptoService#calculationCardHash(String)}
     * 2. Отправляет высчитанный Hash в репозиторий
     * 3. Возвращает результат работы метода репозитория
     *
     * @param cardNumber сырой номер карты
     * @return результат в булевом значении
     */
    public Boolean existsCardByNumber(String cardNumber) {
        return cardRepository.existsBySearchHash(cryptoService.calculationCardHash(cardNumber));
    }

    /**
     * Получение всех карт пользователя с пагинацией
     *
     * @param userId пользователя к картам которого выполняется поиск
     * @param pageable объект постраничного запроса
     * @return карты разделенный на страницы
     */
    public Page<Card> getUserCardsPaginated(Long userId, Pageable pageable) {
        return cardRepository.findByUser_Id(userId, pageable);
    }

    /**
     * Получение указанной карты по id у указанного пользователя
     *
     * @param cardId искомая карта
     * @param userId пользователя которому должна принадлежать карта
     * @return данные карты
     * @throws CardNotFoundException если карты с таким id не существует
     */
    public Card findCardByUserIdAndCardId(Long cardId, Long userId) {
        return cardRepository.findByIdAndUser_id(cardId, userId).orElseThrow(
                () -> new CardNotFoundException("Card by id: %s not found for user %s".formatted(cardId, userId))
        );
    }

    /**
     * Получение указанной карты по номеру у указанного пользователя
     *
     * Логика:
     * 1. По номеру карты высчитывается Hash с помощью метода {@link CryptoService#calculationCardHash(String)}
     * 2. Выполняется запрос с для получения карты с таким же хэшом с помощью метода {@link CardRepository#findByEncryptNumberAndUser_Id(String, Long)}
     *
     * @param userId пользователь которому должна принадлежать карта
     * @param cardNumber номера карты
     * @return данные карты
     * @throws CardNotFoundException если карты с таким номером не существует
     */
    public Card findCardByCardNumberAndUserId(Long userId, String cardNumber) {
        return cardRepository.findByEncryptNumberAndUser_Id(cryptoService.calculationCardHash(cardNumber), userId)
                .orElseThrow(() -> new CardNotFoundException("Card: %s not found for user %s".formatted(
                        CardFormattedService.formatedMaskedCard(cardNumber), userId)));
    }

    /**
     * Получение карты по номеру
     *
     * Логика:
     * 1. По номеру карты высчитывается Hash с помощью метода {@link CryptoService#calculationCardHash(String)}
     * 2. Выполняется запрос с для получения карты с таким же хэшом с помощью метода {@link CardRepository#findBySearchHash(String)}
     *
     * @param cardNumber номер искомой карты
     * @return данные карты
     * @throws CardNotFoundException если карты с таким хэшом не существует
     */
    public Card findCardByNumber(String cardNumber) {
        return cardRepository.findBySearchHash(cryptoService.calculationCardHash(cardNumber))
                .orElseThrow(() -> new CardNotFoundException("Card by: %s not found"
                        .formatted(CardFormattedService.formatedMaskedCard(cardNumber))));
    }

    /**
     * Сохраняет карту
     *
     * @param card данные карты в роли объекта {@link Card}
     */
    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    /**
     * Поиск карты по id
     *
     * @param id искомой карты
     * @return данные карты
     */
    public Card findCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException("Card by id: %s not found".formatted(id)));
    }

    /**
     * Обновление статуса карты по id
     *
     * @param cardId обновляемой карты
     * @param newStatusCard новый статус карты
     * @return обновленные данные карты
     * @throws CardNotFoundException если карты с данным id не существует
     */
    @Transactional
    public Card updateCardStatus(Long cardId, StatusCard newStatusCard) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card by id: %s not found".formatted(cardId)));
        card.setStatusCard(newStatusCard);
        return cardRepository.save(card);
    }

    /**
     * Удаление карты по id
     *
     * @param cardId удаляемой карты
     * @throws CardNotFoundException если карты с данным id не существует
     */
    @Transactional
    public void deleteCardById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card by id: %s not found".formatted(cardId)));
        cardRepository.delete(card);
    }

    /**
     * Удаление карты по номеру
     *
     * @param cardNumber номер карты
     * @throws CardNotFoundException если карты с данными хэшом не существует
     */
    @Transactional
    public void deleteCardByCardNumber(String cardNumber) {
        Card card = cardRepository.findBySearchHash(cryptoService.calculationCardHash(cardNumber))
                .orElseThrow(() -> new CardNotFoundException("Card by id: %s not found".formatted(cardNumber)));
        cardRepository.delete(card);
    }

    /**
     * Получение всех карт пользователя по имени с пагинацией
     *
     * @param username пользователя к картам которого выполняется поиск
     * @param pageable объект постраничного запроса
     * @return карты разделенный на страницы
     */
    public Page<Card> findCardsByUsername(String username, Pageable pageable) {
        return cardRepository.findByUser_Username(username, pageable);
    }

    /**
     * Получение всех карт с пагинацией
     *
     * @param pageable объект постраничного запроса
     * @return карты разделенный на страницы
     */
    public Page<Card> findAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    //TODO написать тесты
    /**
     * Поиск карты по фильтрам
     *
     * @param filter данные для фильтрации в виде объекта {@link CardSearchFilter}
     * @param pageable объект пагинации
     * @return результат поиска
     */
    public Page<Card> searchCard(CardSearchFilter filter, Pageable pageable) {
        return cardRepository.findAll(CardSpecification.withFilter(filter, cryptoService), pageable);
    }
}









