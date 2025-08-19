package org.lamisplus.modules.pharmacy.domain.projections;



import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DrugDispenseProjection {
    Long getId();
    String getUuid();
    String getMedicationName();
    String getBrandName();
    String getManufacturer();
    String getBatchNumber();
    LocalDate getExpiryDate();
    Integer getQuantityDispensed();
    String getQuantityUnit();
    String getFormulation();
    String getStrength();
    LocalDateTime getDateTimeDispensed();
    Long getDispensedBy();
    String getDispenserName();
    Boolean getSubstitutionMade();
    String getSubstitutionReason();
    String getDispensingNotes();
    Boolean getIsRefill();
    Integer getRefillNumber();
}
