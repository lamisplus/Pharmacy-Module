package org.lamisplus.modules.pharmacy.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import java.time.LocalDate;
import java.util.List;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PatientDrugOrderDTO {
    // Patient Information
    private long patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientHospitalNumber;
    private String patientAddress;
    private String patientGender;
    private String patientPhoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate patientDob;

    private String prescriptionGroupId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss")
    private LocalDateTime prescriptionDate;

    private String orderedBy;

    private List<DrugOrder> drugOrders;

    private int totalMedications;
    private int dispensedMedications;
    private int pendingMedications;
    private String prescriptionStatus; // PENDING, PARTIALLY_DISPENSED, FULLY_DISPENSED
}