package com.prashantlabs.invoicing.web;

import com.prashantlabs.invoicing.core.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/invoices")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {
    private final InvoiceService svc;
    private final Path storageDir;

    public InvoiceController(
            InvoiceService svc,
            @Value("${app.invoices.dir}") String storageDir
    ) {
        this.svc = svc;
        this.storageDir = Paths.get(storageDir);
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Map<String, String>> generate(@RequestBody @Valid InvoiceRequest req) throws Exception {
        Path pdf = svc.generate(req);
        svc.emailInvoice(req.customerEmail(), pdf);
        return ApiResponse.ok("Invoice generated & emailed", Map.of("file", pdf.toString()));
    }

   /* @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String file) throws Exception {
        var bytes = java.nio.file.Files.readAllBytes(Path.of(file));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf").body(bytes);
    }*/
   @Operation(
           summary = "Download a PDF invoice",
           responses = {
                   @io.swagger.v3.oas.annotations.responses.ApiResponse(
                           responseCode = "200",
                           description = "Invoice PDF",
                           content = @Content(
                                   mediaType = "application/octet-stream",
                                   schema = @Schema(type = "string", format = "binary")
                           )
                   )
           }
   )
   @GetMapping(value = "/download/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
   public ResponseEntity<byte[]> download(@PathVariable String fileName) throws IOException {
       Path filePath = storageDir.resolve(fileName).normalize();
       if (!Files.exists(filePath)) {
           return ResponseEntity.notFound().build();
       }
       byte[] bytes = Files.readAllBytes(filePath);
       return ResponseEntity.ok()
               .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
               .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE) // actual type for browsers
               .body(bytes);
   }


}
