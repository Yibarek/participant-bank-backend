package org.multimedia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.multimedia.dto.OperationSuccessResponse;
import org.multimedia.dto.ParticipantBankDto;
import org.multimedia.dto.ParticipantBankListResponse;
import org.multimedia.dto.RestResponse;
import org.multimedia.model.ParticipantBank;
import org.multimedia.service.ParticipantBankService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/acs-dev/api/institutions", "/api/v1/participants"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Participant Banks", description = "CRUD operations for managing financial institution and participant bank profiles")
public class ParticipantBankController {

    private final ParticipantBankService participantBankService;

    @Operation(
            summary = "Add Participant Bank",
            description = "Creates a new participant bank record in the database using a JSON payload."
    )
    @RequestBody(
            description = "Participant bank creation JSON payload",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ParticipantBankDto.class),
                    examples = @ExampleObject(
                            name = "Add Bank Example",
                            value = "{\"name\": \"Commercial Bank of Ethiopia\", \"bic\": \"CBETETAA\", \"shortName\": \"CBE\", \"address\": \"Sudan Street, Addis Ababa, Ethiopia\", \"email\": \"contact@cbe.com.et\", \"phoneNumber\": \"+251 11 123 4567\", \"status\": \"ACTIVE\"}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant bank added successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationSuccessResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": \"successfully Added Commercial Bank of Ethiopia\"}"
                            )
                    )
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> addInstitution(@Valid @org.springframework.web.bind.annotation.RequestBody ParticipantBankDto dto) {
        log.info("Received request to create participant bank: {}", dto.getName());
        ParticipantBank created = participantBankService.createBank(dto);
        return RestResponse.success("successfully Added " + created.getName(), "institution", created);
    }

    @Operation(
            summary = "Update Participant Bank",
            description = "Updates an existing participant bank profile by its ID using a JSON payload."
    )
    @RequestBody(
            description = "Participant bank update JSON payload (must include id or id supplied in path)",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ParticipantBankDto.class),
                    examples = @ExampleObject(
                            name = "Update Bank Example",
                            value = "{\"id\": 1, \"name\": \"Commercial Bank of Ethiopia\", \"bic\": \"CBETETAA\", \"shortName\": \"CBE\", \"address\": \"Sudan Street, Addis Ababa, Ethiopia\", \"email\": \"contact@cbe.com.et\", \"phoneNumber\": \"+251 11 123 4567\", \"status\": \"ACTIVE\"}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant bank updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationSuccessResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": \"successfully Updated Commercial Bank of Ethiopia\"}"
                            )
                    )
            )
    })
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> updateInstitutionPost(@Valid @org.springframework.web.bind.annotation.RequestBody ParticipantBankDto dto) {
        log.info("Received request to update participant bank: {}", dto.getName());
        ParticipantBank updated = participantBankService.updateBank(dto.getId(), dto);
        return RestResponse.success("successfully Updated " + updated.getName(), "institution", updated);
    }

    @Operation(
            summary = "Update Participant Bank by ID (RESTful PUT)",
            description = "Updates an existing participant bank by path variable ID."
    )
    @RequestBody(
            description = "Participant bank update JSON payload",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ParticipantBankDto.class),
                    examples = @ExampleObject(
                            name = "Update Bank Example",
                            value = "{\"name\": \"Commercial Bank of Ethiopia\", \"bic\": \"CBETETAA\", \"shortName\": \"CBE\", \"address\": \"Sudan Street, Addis Ababa, Ethiopia\", \"email\": \"contact@cbe.com.et\", \"phoneNumber\": \"+251 11 123 4567\", \"status\": \"ACTIVE\"}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant bank updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationSuccessResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": \"successfully Updated Commercial Bank of Ethiopia\"}"
                            )
                    )
            )
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> updateInstitutionPut(
            @Parameter(description = "Participant bank ID", example = "1") @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody ParticipantBankDto dto) {
        log.info("Received RESTful update request for bank ID: {}", id);
        ParticipantBank updated = participantBankService.updateBank(id, dto);
        return RestResponse.success("successfully Updated " + updated.getName(), "institution", updated);
    }

    @Operation(
            summary = "Get Participant Bank by ID",
            description = "Retrieves complete participant bank profile by its numeric ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant bank retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ParticipantBank.class),
                            examples = @ExampleObject(
                                    name = "Bank Detail Example",
                                    value = "{\"id\": 1, \"name\": \"Commercial Bank of Ethiopia\", \"bic\": \"CBETETAA\", \"shortName\": \"CBE\", \"address\": \"Sudan Street, Addis Ababa, Ethiopia\", \"email\": \"contact@cbe.com.et\", \"phoneNumber\": \"+251 11 123 4567\", \"logoPath\": null, \"status\": \"ACTIVE\", \"createdBy\": 1, \"createdDate\": \"2026-10-19T10:00:00\", \"modifiedBy\": 1, \"modifiedDate\": \"2026-10-19T10:00:00\"}"
                            )
                    )
            )
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantBank> getInstitutionById(
            @Parameter(description = "Participant bank ID", example = "1") @PathVariable Long id) {
        log.info("Fetching participant bank by ID: {}", id);
        ParticipantBank bank = participantBankService.getBankById(id);
        return ResponseEntity.ok(bank);
    }

    @Operation(
            summary = "Get Participant Bank by BIC",
            description = "Retrieves participant bank profile details using the BIC code."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant bank retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ParticipantBank.class),
                            examples = @ExampleObject(
                                    name = "Bank Detail Example",
                                    value = "{\"id\": 1, \"name\": \"Commercial Bank of Ethiopia\", \"bic\": \"CBETETAA\", \"shortName\": \"CBE\", \"address\": \"Sudan Street, Addis Ababa, Ethiopia\", \"email\": \"contact@cbe.com.et\", \"phoneNumber\": \"+251 11 123 4567\", \"logoPath\": null, \"status\": \"ACTIVE\"}"
                            )
                    )
            )
    })
    @GetMapping(value = {"/bic/{bic}", "/details/{bic}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantBank> getInstitutionByBic(
            @Parameter(description = "Bank Identifier Code (BIC)", example = "CBETETAA") @PathVariable String bic) {
        log.info("Fetching participant bank by BIC: {}", bic);
        ParticipantBank bank = participantBankService.getBankByBic(bic);
        return ResponseEntity.ok(bank);
    }

    @Operation(
            summary = "List All Participant Banks",
            description = "Returns all registered participant banks."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant banks retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ParticipantBankListResponse.class),
                            examples = @ExampleObject(
                                    name = "List Example",
                                    value = "{\"success\": \"successfully Loaded\", \"institutions\": [{\"id\": 1, \"name\": \"Commercial Bank of Ethiopia\", \"bic\": \"CBETETAA\", \"shortName\": \"CBE\", \"address\": \"Sudan Street, Addis Ababa, Ethiopia\", \"email\": \"contact@cbe.com.et\", \"phoneNumber\": \"+251 11 123 4567\", \"status\": \"ACTIVE\"}]}"
                            )
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> findAll() {
        log.info("Listing all participant banks");
        List<ParticipantBank> banks = participantBankService.getAllBanks();
        return RestResponse.success("successfully Loaded", "institutions", banks);
    }

    @Operation(
            summary = "Search Participant Banks",
            description = "Searches participant banks by matching keyword against name, BIC, or short name."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ParticipantBankListResponse.class),
                            examples = @ExampleObject(
                                    name = "Search Example",
                                    value = "{\"success\": \"searched successfully\", \"institutions\": [{\"id\": 1, \"name\": \"Commercial Bank of Ethiopia\", \"bic\": \"CBETETAA\", \"shortName\": \"CBE\", \"status\": \"ACTIVE\"}]}"
                            )
                    )
            )
    })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> search(
            @Parameter(description = "Search query keyword (matches name, BIC, or short name)", example = "CBE")
            @RequestParam(value = "query", required = false, defaultValue = "") String query) {
        log.info("Searching participant banks with keyword: {}", query);
        List<ParticipantBank> banks = participantBankService.searchBanks(query);
        return RestResponse.success("searched successfully", "institutions", banks);
    }

    @Operation(
            summary = "Delete Participant Bank",
            description = "Deletes a participant bank by its numeric ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant bank deleted successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationSuccessResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": \"successfully Deleted\"}"
                            )
                    )
            )
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> remove(
            @Parameter(description = "Participant bank ID to delete", example = "1") @PathVariable Long id) {
        log.info("Deleting participant bank ID: {}", id);
        participantBankService.deleteBank(id);
        return RestResponse.success("successfully Deleted");
    }

    @Operation(
            summary = "Upload Participant Bank Logo",
            description = "Uploads a logo image file (PNG, JPG, SVG, WebP) for a participant bank identified by ID, stores it, and updates its profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logo uploaded successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationSuccessResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": \"successfully Uploaded Logo for Commercial Bank of Ethiopia\", \"logoPath\": \"/uploads/logos/CBETETAA_logo.png\"}"
                            )
                    )
            )
    })
    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> uploadLogo(
            @Parameter(description = "Participant bank ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Logo image file (PNG, JPG, SVG, WebP)") @RequestParam("logo") org.springframework.web.multipart.MultipartFile logo) {
        log.info("Received request to upload logo for bank ID: {}", id);
        ParticipantBank updated = participantBankService.updateBankLogo(id, logo);
        return RestResponse.success("successfully Uploaded Logo for " + updated.getName(), "institution", updated);
    }
}
