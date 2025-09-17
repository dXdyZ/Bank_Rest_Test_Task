package com.example.bank_rest_test_task.controller.documentation;

import com.example.bank_rest_test_task.dto.*;
import com.example.bank_rest_test_task.entity.BlockRequestStatus;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;
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
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;

@Tag(name = "Card Block Management", description = "Endpoints for managing card block requests")
public interface CardBlockControllerDocs {

    @Operation(summary = "Create a card block request", description = "Allows a user to request blocking their own card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Block request created successfully"),
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
                                                "field": "reason",
                                                "message": "Reason should not be empty"
                                            }
                                        ]
                                    }"""))),
            @ApiResponse(responseCode = "403", description = "Card is already blocked",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "You cannot block an inactive card",
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
    void createBlockRequest(@Parameter(hidden = true) Jwt jwt, @Valid @RequestBody CreateCardBlockRequestDto cardBlockDto);

    @Operation(summary = "Process a card block request", description = "Allows an administrator to approve or reject a block request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Block request processed successfully"),
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
                                                "field": "status",
                                                "message": "Status cannot be null"
                                            }
                                        ]
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "Block request not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Request to block by id: 1 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<?> processBlockRequest(@Valid @RequestBody ProcessBlockRequestDto requestDto);


    @Operation(summary = "Find block requests processed by an admin", description = "Retrieves a paginated list of block requests processed by a specific administrator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found block requests",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<PageResponse<CardBlockRequestDto>> findCardBlockRequestByProcessed(
            @PathVariable Long adminId,
            @ParameterObject @PageableDefault(size = 6, sort = "createAt") Pageable pageable
    );

    @Operation(summary = "Find block request by ID", description = "Retrieves a specific block request by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found block request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardBlockRequestDto.class))),
            @ApiResponse(responseCode = "404", description = "Block request not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2024-07-30T12:34:56.789Z",
                                        "message": "Request for blocking by id: 1 not found",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<CardBlockRequestDto> findCardBlockRequestById(@Positive(message = "Id must be greater than zero")
                                                                  @PathVariable Long id);


    @Operation(summary = "Filter card block requests", description = "Allows an administrator to filter block requests by various criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found block requests",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<PageResponse<CardBlockRequestDto>> searchCardBlockRequest(
            @Valid @RequestBody CardBlockRequestFilter filter,
            @ParameterObject @PageableDefault(size = 6, sort = "createAr") Pageable pageable);
}
