package com.example.bank_rest_test_task.controller.documentation;

import com.example.bank_rest_test_task.dto.CardDto;
import com.example.bank_rest_test_task.dto.ErrorResponse;
import com.example.bank_rest_test_task.dto.PageResponse;
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
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User Card Management", description = "Endpoints for users to view their own cards")
public interface UserCardControllerDocs {

    @Operation(summary = "Get card by ID", description = "Retrieves a specific card by its ID, if it belongs to the authenticated user")
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
                                        "message": "Card by id: 1 not found for user 1",
                                        "code": 404
                                    }""")))
    })
    ResponseEntity<CardDto> getCardById(@PathVariable Long id, @Parameter(hidden = true) Jwt jwt);

    @Operation(summary = "Get all user cards", description = "Retrieves a paginated list of all cards belonging to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards found",
                    content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<PageResponse<CardDto>> getUserCards(@Parameter(hidden = true) Jwt jwt, @ParameterObject @PageableDefault(size = 6, sort = "balance") Pageable pageable);
}
