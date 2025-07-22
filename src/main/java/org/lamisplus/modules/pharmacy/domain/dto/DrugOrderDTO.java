package org.lamisplus.modules.pharmacy.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lamisplus.modules.pharmacy.domain.entity.DispensingStatus;
import org.lamisplus.modules.pharmacy.domain.entity.OrderStatus;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugOrderDTO {
    private Long id;
    private String uuid;
    private Long patientId;

    private Long visitId;

    @NotBlank(message = "Medication name is required")
    private String medicationName;
    private String drugBrandName;

    private String formulation;
    private String strength;
    private String dosageAmount;
    private String routeOfAdmin;
    private String frequency;
    private String timingInstructions;
    private String duration;
    private String durationUnit;
    private String quantityPrescribed;
    private String quantityUnit;
    private Integer refillsAllowed;
    private Integer refillsRemaining;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime encounterDate;

    private String prescriptionType;
    private String notes;

    private Long prescribedBy;
    private String prescriberName;
    private LocalDateTime prescriptionDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private OrderStatus orderStatus;
    private DispensingStatus dispensingStatus;

}
