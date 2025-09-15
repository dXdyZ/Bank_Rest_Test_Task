package com.example.bank_rest_test_task.controller.documentation;

import com.example.bank_rest_test_task.dto.AuthRequestDto;
import com.example.bank_rest_test_task.dto.ErrorResponse;
import com.example.bank_rest_test_task.dto.JwtTokenDto;
import com.example.bank_rest_test_task.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public interface AuthControllerDocs {

    @Operation(summary = "User login", description = "Authenticate user and get JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenDto.class))),
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
                                                "message": "Username must not be empty"
                                            }
                                        ]
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Invalid username or password",
                                        "code": 401
                                    }""")))
    })
    ResponseEntity<JwtTokenDto> login(@Valid @RequestBody AuthRequestDto authRequestDto);

    @Operation(summary = "Refresh access token", description = "Get a new access token using a refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully refreshed token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Invalid refresh token",
                                        "code": 400
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Refresh token expired or invalid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Refresh token expired or invalid",
                                        "code": 401
                                    }""")))
    })
    ResponseEntity<JwtTokenDto> updateAccessToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest);
}
