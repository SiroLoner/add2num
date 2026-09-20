package com.caesar.add2num.web.api;

import com.caesar.add2num.core.SumResult;
import com.caesar.add2num.web.service.CalculationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A small JSON API over the same calculation, so the feature is usable by something other than the
 * browser form: a script, a scheduled job, or another service.
 *
 * <p>Versioned in the path from the first release. Adding {@code /v1} later, once clients exist, is
 * a breaking change; adding it now costs nothing.
 *
 * <pre>{@code
 * curl -s localhost:8080/api/v1/sum \
 *   -H 'Content-Type: application/json' \
 *   -d '{"first":"1234","second":"897","maxSteps":10}'
 * }</pre>
 */
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class SumApiController {

    private final CalculationService calculationService;

    public SumApiController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping(path = "/sum", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SumResponse sum(@Valid @RequestBody SumRequest request) {
        SumResult result = (request.maxSteps() == null)
                ? calculationService.add(request.first(), request.second())
                : calculationService.add(request.first(), request.second(), request.maxSteps());

        return SumResponse.from(result);
    }
}
