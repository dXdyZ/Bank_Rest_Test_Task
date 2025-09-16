package com.example.bank_rest_test_task.controller;

import com.example.bank_rest_test_task.controller.documentation.UserControllerDocs;
import com.example.bank_rest_test_task.dto.*;
import com.example.bank_rest_test_task.entity.User;
import com.example.bank_rest_test_task.service.UserService;
import com.example.bank_rest_test_task.util.factory.UserDtoFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/users")
public class UserController implements UserControllerDocs {
    private final UserService userService;
    private final UserDtoFactory userDtoFactory;

    public UserController(UserService userService, UserDtoFactory userDtoFactory) {
        this.userService = userService;
        this.userDtoFactory = userDtoFactory;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto,
                             @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.valueOf(jwt.getSubject());
        userService.registrationUser(userRegisterDto, adminId);
    }

    /**
     * Получение всех пользователй с возможностью выбора получить пользователей с картами или без
     *
     * @param pageable объект пагинации
     * @param includeCards флаг для указания загрузить с картами или без
     * @return преобразованный результат в {@link PageResponse#from(Page)}
     */
    @GetMapping
    public ResponseEntity<PageResponse<UserDto>> getAllUser(@PageableDefault(size = 6, sort = "id") Pageable pageable,
                                                                    @RequestParam(value = "includeCards", defaultValue = "false") Boolean includeCards) {
        Page<User> page = userService.listUsers(pageable, includeCards);

        PageResponse<UserDto> result = PageResponse.from(page.map(u -> {
            if (includeCards) {
                return userDtoFactory.createUserDtoAndCardDtoForAdminWithCards(u);
            } else {
                return userDtoFactory.createUserDtoWithoutCards(u);
            }
        }));

        return ResponseEntity.ok(result);
    }

    //TODO написать swagger документацию и тест
    /**
     * Поиск пользователя по фильтрам
     *
     * @param username имя пользователя; может быть указано неточное
     * @param roleName роль пользователя; указывается в человеческом виде: {@code admin user}
     * @param includeCards флаг для указания загрузить с картами или без
     * @param pageable объект пагинации
     * @return преобразованный результат в {@link PageResponse#from(Page)}
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserDto>> searchUserByFilter(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "includeCards", defaultValue = "false") Boolean includeCards,
            @PageableDefault(size = 6, sort = "id") Pageable pageable) {

        Page<User> page = userService.searchUser(username, roleName, includeCards, pageable);

        PageResponse<UserDto> result = PageResponse.from(page.map(u -> {
            if (includeCards) {
                return userDtoFactory.createUserDtoAndCardDtoForAdminWithCards(u);
            } else {
                return userDtoFactory.createUserDtoWithoutCards(u);
            }
        }));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@Size(min = 3, max = 50,
            message = "Username must be between 3 and 100 characters long") @PathVariable String username) {
        return ResponseEntity.ok(userDtoFactory.createUserDtoAndCardDtoForAdminWithCards(userService.findUserByUsername(username)));
    }

    @DeleteMapping("/by-username/{username}")
    public ResponseEntity<?> deleteUserByUsername(@Size(min = 3, max = 50,
            message = "Username must be between 3 and 100 characters long") @PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@Positive(message = "Id must not be less than zero") @PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/username")
    public ResponseEntity<UserDto> updateUsername(@Positive(message = "Id must not be less than zero") @PathVariable Long userId,
                                                  @Valid @RequestBody UsernameUpdateDto usernameUpdateDto,
                                                  @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                userDtoFactory.createUserDtoWithoutCards(
                        userService.updateUsername(usernameUpdateDto.newUsername(), userId, adminId)
                )
        );
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserDto> updateRol(@Positive(message = "Id must not be less than zero") @PathVariable Long userId,
                                             @Valid @RequestBody UserRoleUpdateDto userRoleUpdateDto,
                                             @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                userDtoFactory.createUserDtoWithoutCards(
                        userService.updateRole(userRoleUpdateDto.role(), userId, adminId)
                )
        );
    }
}
