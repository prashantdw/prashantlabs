package com.prashantlabs.invoicing.web;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceRequest(@NotBlank String invoiceNumber, @NotBlank String customerEmail,
                             @NotBlank String customerName, @NotNull List<InvoiceLine> lines,
                             @Positive BigDecimal taxPercent) {
}
