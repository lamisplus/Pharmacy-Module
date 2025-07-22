package org.lamisplus.modules.pharmacy.domain.projections;

import java.time.LocalDateTime;


public interface MedicationHistoryProjection {
    LocalDateTime getPrescriptionDate();
    String getMedicationName();
    String getStrength();
    String getDosageAmount();
    String getFrequency();
    String getPrescriberName();
    Boolean getIsDispensed();
    LocalDateTime getDispensedDate();
    Integer getQuantityDispensed();
    String getQuantityUnit();
    String getDispenserName();
    String getPrescriptionType();
    String getNotes();
}