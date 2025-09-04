package com.prashantlabs.documents.web;

import com.prashantlabs.documents.core.DocumentStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/bills")
@SecurityRequirement(name = "bearerAuth")
public class BillController {
    private final DocumentStorageService svc;

    public BillController(DocumentStorageService s) {
        this.svc = s;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a bill",
            description = "Uploads a bill document and returns its storage path")
    public ApiResponse<Map<String, String>> upload(
            @Parameter(
                    description = "Bill document (PDF or image)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file
    ) {
        String path = svc.save(file);
        return ApiResponse.ok("Uploaded", Map.of("path", path));
    }
}
