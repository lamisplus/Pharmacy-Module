package org.lamisplus.modules.pharmacy.domain.projections;

import org.lamisplus.modules.pharmacy.domain.entity.DispensingStatus;

import java.time.LocalDateTime;

public interface DispensationQueueProjection {
    Long getDrugOrderId();
    LocalDateTime getPrescriptionDate();
    String getPatientFirstName();
    String getPatientLastName();
    String getPatientHospitalNumber();
    String getMedicationName();
    String getStrength();
    String getDosageAmount();
    String getFrequency();
    Integer getQuantityPrescribed();
    String getQuantityUnit();
    String getPrescriberName();
    DispensingStatus getDispensingStatus();
    LocalDateTime getLastDispensedDate();
    Integer getTotalQuantityDispensed();
    String getLastDispenserName();
    String getSpecialInstructions();
}