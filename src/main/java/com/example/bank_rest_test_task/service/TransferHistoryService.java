package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.entity.TransferHistory;
import com.example.bank_rest_test_task.repository.TransferHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Сервис истории переводов.
 *
 * Хранит и предоставляет постраничный доступ к операциям пользователя.
 */
@Service
public class TransferHistoryService {
    private final TransferHistoryRepository transferHistoryRepository;

    /**
     * @param transferHistoryRepository интерфейс для работы с JPA сущностями в базе данных
     */
    public TransferHistoryService(TransferHistoryRepository transferHistoryRepository) {
        this.transferHistoryRepository = transferHistoryRepository;
    }

    /**
     * Сохраняет историю транзакции
     *
     * @param transferHistory данные транзакции
     */
    public void saveTransferHistory(TransferHistory transferHistory) {
        transferHistoryRepository.save(transferHistory);
    }

    /**
     * Получение всей истории транзакций одного пользователя
     *
     * @param username имя пользователя
     * @param pageable объект постраничного запроса
     * @return историю транзакций разделенную на страницы
     */
    public Page<TransferHistory> getTransferHistoryByUsernamePaginated(String username, Pageable pageable) {
        return transferHistoryRepository.findByUser_Username(username, pageable);
    }
}
