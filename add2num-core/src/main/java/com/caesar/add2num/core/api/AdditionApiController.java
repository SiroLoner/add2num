package com.caesar.add2num.core.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST adapter located in add2num-core for adding arbitrarily large integers. */
@RestController
@RequestMapping(path = "/api/v1/addition", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Addition", description = "Operations on arbitrarily large decimal integers")
public class AdditionApiController {

    private final AdditionService additionService;

    public AdditionApiController(AdditionService additionService) {
        this.additionService = additionService;
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add two numbers", description = "Adds two non-negative decimal integers using the add2num-core calculation engine.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Numbers added successfully"),
            @ApiResponse(responseCode = "400", description = "Request contains a missing or invalid operand",
                    content = @Content(schema = @Schema(implementation = CoreApiError.class)))
    })
    public AdditionResponse add(@Valid @RequestBody AdditionRequest request) {
        return additionService.add(request.firstNumber(), request.secondNumber());
    }
}