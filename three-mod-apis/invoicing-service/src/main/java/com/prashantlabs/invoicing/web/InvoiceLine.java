package com.prashantlabs.invoicing.web;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record InvoiceLine(@NotBlank String item, @Positive int quantity, @Positive BigDecimal unitPrice) {
}
