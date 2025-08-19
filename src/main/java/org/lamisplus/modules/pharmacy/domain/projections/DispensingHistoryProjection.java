package org.lamisplus.modules.pharmacy.domain.projections;

import java.time.LocalDateTime;

public interface DispensingHistoryProjection {
    LocalDateTime getDateTimeDispensed();
    String getPatientFirstName();
    String getPatientLastName();
    String getPatientHospitalNumber();
    String getMedicationName();
    String getBrandName();
    String getStrength();
    String getDosageAmount();
    String getFrequency();
    Integer getQuantityDispensed();
    String getQuantityUnit();
    String getDispenserName();
    LocalDateTime getPrescriptionDate();
    String getPrescriberName();
    String getPrescriptionType();
    String getDispensingNotes();
    Boolean getIsRefill();
}
