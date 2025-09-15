package com.example.bank_rest_test_task.controller.documentation;

import com.example.bank_rest_test_task.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User Management", description = "Endpoints for administrators to manage users")
public interface UserControllerDocs {

    @Operation(summary = "Register a new user", description = "Registers a new user with the USER role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Validation failed",
                                        "code": 400,
                                        "validationErrors": [
                                            {
                                                "field": "username",
                                                "message": "Username must be specified"
                                            }
                                        ]
                                    }"""))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "User by name: new_user already exists",
                                        "code": 409
                                    }""")))
    })
    void registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto);

    @Operation(summary = "Get user by username", description = "Retrieves user details by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "User by name: john_doe not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<UserDto> getUserByUsername(@Size(min = 3, max = 50,
            message = "Username must be between 3 and 100 characters long") @PathVariable String username);

    @Operation(summary = "Delete user by username", description = "Deletes a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "User by name: john_doe not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<?> deleteUserByUsername(@Size(min = 3, max = 50,
            message = "Username must be between 3 and 100 characters long") @PathVariable String username);

    @Operation(summary = "Delete user by ID", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "User by id: 1 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<?> deleteUserById(@Positive(message = "Id must not be less than zero") @PathVariable Long id);

    @Operation(summary = "Update username", description = "Updates the username of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Validation failed",
                                        "code": 400,
                                        "validationErrors": [
                                            {
                                                "field": "newUsername",
                                                "message": "New username must not be empty"
                                            }
                                        ]
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "User by id: 1 not found",
                                        "code": 404
                                    }"""))),
            @ApiResponse(responseCode = "409", description = "Username already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "User by name: existing_user already exists",
                                        "code": 409
                                    }""")))
    })
    ResponseEntity<UserDto> updateUsername(@Positive(message = "Id must not be less than zero") @PathVariable Long userId,
                                           @Valid @RequestBody UsernameUpdateDto usernameUpdateDto);

    @Operation(summary = "Update user role", description = "Updates the role of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Validation failed",
                                        "code": 400,
                                        "validationErrors": [
                                            {
                                                "field": "role",
                                                "message": "Role name must not be empty"
                                            }
                                        ]
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "User by id: 1 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<UserDto> updateRol(@Positive(message = "Id must not be less than zero") @PathVariable Long userId,
                                      @Valid @RequestBody UserRoleUpdateDto userRoleUpdateDto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping()
    ResponseEntity<PageResponse<UserDto>> getAllUser(@ParameterObject @PageableDefault(size = 6, sort = "id") Pageable pageable,
                                                     @RequestParam(value = "includeCards", defaultValue = "false") Boolean includeCards);

}
