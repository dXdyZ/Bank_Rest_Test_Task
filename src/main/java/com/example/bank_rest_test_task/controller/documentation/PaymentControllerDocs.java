package com.example.bank_rest_test_task.controller.documentation;

import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.dto.ErrorResponse;
import com.example.bank_rest_test_task.dto.PaymentDto;
import org.springframework.security.oauth2.jwt.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Payment Management", description = "Endpoints for money transfers between user's cards")
public interface PaymentControllerDocs {

    @Operation(summary = "Transfer money between cards", description = "Allows a user to transfer money between their own cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient funds",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "Validation Error", value = """
                                            {
                                                "timestamp": "2024-07-30T12:34:56.789Z",
                                                "message": "Validation failed",
                                                "code": 400,
                                                "validationErrors": [
                                                    {
                                                        "field": "amount",
                                                        "message": "Amount must not be less than zero"
                                                    }
                                                ]
                                            }"""),
                                    @ExampleObject(name = "Insufficient Funds", value = """
                                            {
                                                "timestamp": "2024-07-30T12:34:56.789Z",
                                                "message": "There are not enough funds on the card",
                                                "code": 400
                                            }""")
                            })),
            @ApiResponse(responseCode = "403", description = "Card is blocked or expired",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by id: 1 is blocked for operation",
                                        "code": 403
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Card by id: 1 not found for user 1",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<List<CardDto>> transferMoney(@Parameter(hidden = true) Jwt jwt, @Valid @RequestBody PaymentDto paymentDto);
}
