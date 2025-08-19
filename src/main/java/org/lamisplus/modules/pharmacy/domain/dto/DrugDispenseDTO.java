package org.lamisplus.modules.pharmacy.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugDispenseDTO {
    private Long id;
    private String uuid;
    private Long drugOrderId;
    private Long patientId;
    private String medicationName;
    private String brandName;
    private String manufacturer;
    private String batchNumber;
    private LocalDate expiryDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime encounterDate;

    @NotNull(message = "Quantity dispensed is required")
    private Integer quantityDispensed;
    private String quantityUnit;
    private String formulation;
    private String strength;
    private LocalDateTime dateTimeDispensed;
    private Long dispensedBy;
    private String dispenserName;
    private Boolean substitutionMade;
    private String substitutionReason;

    private String dispensingNotes;

    private Boolean isRefill;
    private Integer refillNumber;
}
