package com.example.bank_rest_test_task.service;

import com.example.bank_rest_test_task.dto.UserRegisterDto;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.entity.UserRole;
import com.example.bank_rest_test_task.exception.DuplicateUserException;
import com.example.bank_rest_test_task.exception.UserNotFoundException;
import com.example.bank_rest_test_task.repository.UserRepository;
import com.example.bank_rest_test_task.util.LogMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Сервис управления пользователями.
 *
 * Операции:
 * - регистрация (с проверкой уникальности имени и хэшированием пароля);
 * - поиск по имени и id;
 * - обновление имени и роли;
 * - удаление;
 * - постраничный список пользователей.
 *
 * Пароли хранятся только в виде хэша.
 */
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param userRepository интерфейс для работы с JPA сущностями в базе данных
     * @param passwordEncoder интерфейс для хеширования пароль пользователя
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Проверят существование пользователя по имени использую я метод {@link UserRepository#existsByUsername(String)}
     *
     * @param username имя проверяемого пользователя
     * @return результат проверки в булевом значении
     */
    public Boolean existUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Сохраняет пользователя используя метод {@link UserRepository#save(Object)}
     *
     * @param user сохраняемый пользователь
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Поиск пользователя по имени используя метод {@link UserRepository#findByUsername(String)}
     *
     * @param username имя искомого пользователя
     * @return {@link User} искомый пользователь
     * @throws UserNotFoundException если пользователя с таким именем несуществует
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User by name: %s not found".formatted(username))
        );
    }

    /**
     * Получает пользователя по имени с помощью метода {@link UserRepository#findByUsername(String)} и удаляет его с помощью метода {@link UserRepository#delete(Object)}
     *
     * @param username имя удаляемого пользователя
     * @throws UserNotFoundException если пользователь не существует
     */
    @Transactional
    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User by name: %s not found".formatted(username)));
        userRepository.delete(user);
    }

    /**
     * Получает пользователя по id с помощью метода {@link UserRepository#findById(Object)} и удаляет его с помощью метода {@link UserRepository#delete(Object)}
     *
     * @param userId имя удаляемого пользователя
     * @throws UserNotFoundException если пользователь не существует
     */
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by id: %s not found".formatted(userId)));

        userRepository.delete(user);
    }

    /**
     * Получает пользователя по id с помощью метода {@link UserRepository#findById(Object)} далее проверят
     * существует ли пользователь с обновляемым именем с помощью метода {@link UserRepository#existsByUsername(String)}
     * и устанавливает новое имя.
     *
     * @param newUsername новое имя пользователя
     * @param userId id пользователя, которому обновляется имя
     * @param adminId id админа, который назначал роль
     * @return {@link User} обновленные данные пользователя
     * @throws UserNotFoundException если пользователя не существует
     * @throws DuplicateUserException если пользователь с таким именем существует
     */
    @Transactional
    public User updateUsername(String newUsername, Long userId, Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by id: %s not found".formatted(userId)));
        if (userRepository.existsByUsername(newUsername)) {
            throw new DuplicateUserException("User by name: %s already exists".formatted(newUsername));
        }
        user.setUsername(newUsername);

        log.info(LogMarker.AUDIT.getMarker(), "action=UPDATE_USERNAME | result=SUCCESSFULLY | reason=- | adminId={} | userId={} | newUsername={}",
                adminId, user.getId(), newUsername);

        return userRepository.save(user);
    }


    /**
     * Получает пользователя с помощью метода {@link UserRepository#findById(Object)} и устанавливает ему новую роль
     *
     * @param roleName новая роль {@link UserRole#ROLE_USER} или {@link UserRole#ROLE_ADMIN}
     * @param adminId id админа, который назначал роль
     * @param userId id пользователя, которому устанавливается роль
     * @return {@link User} обновленные данные пользователя
     * @throws UserNotFoundException если пользователя не существует
     */
    @Transactional
    public User updateRole(String roleName, Long userId, Long adminId) {
        String modRole = "ROLE_" + roleName.toUpperCase();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by id: %s not found".formatted(userId)));
        user.setRole(UserRole.valueOf(modRole));

        log.info(LogMarker.AUDIT.getMarker(), "action=UPDATE_ROLE | result=SUCCESSFULLY | reason=- | adminId={} | userId={} | role={}",
                adminId, user.getId(), modRole);

        return userRepository.save(user);
    }

    /**
     * Регистрирует нового пользователя
     *
     * @param userRegisterDto данные создаваемого пользователя
     * @param adminId id админа, который назначал роль
     */
    @Transactional
    public void registrationUser(UserRegisterDto userRegisterDto, Long adminId) {
        if (userRepository.existsByUsername(userRegisterDto.username())) {
            throw new DuplicateUserException("User by name: %s already exists".formatted(userRegisterDto.username()));
        }

        User user = userRepository.save(User.builder()
                .username(userRegisterDto.username())
                .password(passwordEncoder.encode(userRegisterDto.password()))
                .role(UserRole.ROLE_USER)
                .build());

        log.info(LogMarker.AUDIT.getMarker(), "action=CREATE_USER | result=SUCCESSFULLY | reason=- | adminId={} | newUserId={}",
                adminId, user.getId());
    }

    /**
     * Поиск пользователя по id
     *
     * @param id искомого пользователя
     * @return {@link User} искомый пользователь
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User by id: %s not found".formatted(id)));
    }

    /**
     * Получение всех пользователей с пагинацией
     *
     * @param pageable объект постраничного запроса
     * @return пользователи разделенные на страницы
     */
    @Transactional(readOnly = true)
    public Page<User> listUsers(Pageable pageable, boolean includeCars) {
        Page<User> page = userRepository.findAll(pageable);

        if (includeCars && !page.isEmpty()) {
            page.getContent().forEach(u -> u.getCards().size());
        }

        return page;
    }

    /**
     * Получение пользователя по имени с подстрочным регистронезависимым поиск и с точным совпадением роли.
     *
     * @param username примерное или точное имя пользователя; регистр не важен
     * @param roleName точное имя пользователя регистр не важен, без приписки ROLE_
     * @param pageable объект пагинации
     * @return постраничный результат поиска
     */
    @Transactional(readOnly = true)
    public Page<User> searchUser(String username, String roleName, boolean includeCars, Pageable pageable) {
        Page<User> page = userRepository.searchUser(username, roleName, pageable);

        if (includeCars && !page.isEmpty()) {
            page.getContent().forEach(u -> u.getCards().size());
        }

        return page;
    }
}
