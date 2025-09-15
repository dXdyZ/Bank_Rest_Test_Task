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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Admin Card Management", description = "Endpoints for administrators to manage bank cards")
public interface AdminCardControllerDocs {

    @Operation(summary = "Create a new card for a user", description = "Creates a new bank card for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
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
                                                "field": "cardNumber",
                                                "message": "Incorrect card number format"
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
            @ApiResponse(responseCode = "409", description = "Card already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by number: 4000123456789010 already exists",
                                        "code": 409
                                    }""")))
    })
    void createCard(@Valid @RequestBody CardCreateDto cardCreateDto);

    @Operation(summary = "Get card by ID", description = "Retrieves card details by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by id: 1 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<CardDto> getCardById(@Positive(message = "Id must not be less than zero") @PathVariable Long id);

    @Operation(summary = "Get all cards for a user", description = "Retrieves a paginated list of all cards for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards found",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<PageResponse<CardDto>> getUserCards(@Positive(message = "Id must not be less than zero") @PathVariable Long id,
                                               @ParameterObject @PageableDefault(size = 6, sort = "validityPeriod") Pageable pageable);

    @Operation(summary = "Get card by card number", description = "Retrieves card details by its number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by: **** **** **** 9010 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<CardDto> getCardByCardNumber(@CreditCardNumber @RequestParam("number") String number);

    @Operation(summary = "Update card status", description = "Updates the status of a card (e.g., ACTIVE, BLOCKED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by id: 1 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<CardDto> updateStatusCard(@Positive(message = "Id must not be less than zero") @PathVariable Long cardId,
                                             @RequestBody UpdateStatusCardDto cardDto);

    @Operation(summary = "Delete card by ID", description = "Deletes a card by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by id: 1 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<?> deleteCardById(@Positive(message = "Id must not be less than zero") @PathVariable Long id);

    @Operation(summary = "Delete card by card number", description = "Deletes a card by its number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by number: 4000123456789010 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<?> deleteCardByCardNumber(@CreditCardNumber @RequestParam("number") String number);


    @Operation(summary = "Get card by username", description = "Get all the user's cards by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card found",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<PageResponse<CardDto>> getCardsByUserName(@NotBlank(message = "Username must be not empty") @RequestParam String username, @ParameterObject @PageableDefault(size = 6) Pageable pageable);


    @Operation(summary = "Get card", description = "Get all cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card found",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<PageResponse<CardDto>> getAllCards(@ParameterObject @PageableDefault(size = 6, sort = "validityPeriod") Pageable pageable);
}
