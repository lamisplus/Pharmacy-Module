package org.lamisplus.modules.pharmacy.repository;

import org.lamisplus.modules.pharmacy.domain.entity.DrugDispense;
import org.lamisplus.modules.pharmacy.domain.projections.DispensationQueueProjection;
import org.lamisplus.modules.pharmacy.domain.projections.DispensingHistoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrugDispenseRepository extends JpaRepository<DrugDispense, Long> {

    Optional<DrugDispense> findByIdAndArchived(Long id, Integer archived);

    List<DrugDispense> findByDrugOrderIdAndArchived(Long drugOrderId, Integer archived);

    List<DrugDispense> findByPatientIdAndArchived(Long patientId, Integer archived);

    @Query("SELECT SUM(dd.quantityDispensed) FROM DrugDispense dd WHERE dd.drugOrder.id = :orderId AND dd.archived = 0")
    Integer getTotalQuantityDispensed(@Param("orderId") Long orderId);
    @Query(value = "SELECT " +
            "drug_ord.id as drugOrderId, " +
            "drug_ord.prescription_date as prescriptionDate, " +
            "p.first_name as patientFirstName, " +
            "p.surname as patientLastName, " +
            "p.hospital_number as patientHospitalNumber, " +
            "drug_ord.medication_name as medicationName, " +
            "drug_ord.strength as strength, " +
            "drug_ord.dosage_amount as dosageAmount, " +
            "drug_ord.frequency as frequency, " +
            "drug_ord.quantity_prescribed as quantityPrescribed, " +
            "drug_ord.quantity_unit as quantityUnit, " +
            "drug_ord.prescriber_name as prescriberName, " +
            "drug_ord.dispensing_status as dispensingStatus, " +
            "MAX(dd.date_time_dispensed) as lastDispensedDate, " +
            "COALESCE(SUM(dd.quantity_dispensed), 0) as totalQuantityDispensed, " +
            "dd2.dispenser_name as lastDispenserName, " +
            "drug_ord.special_instructions as specialInstructions " +
            "FROM drug_order drug_ord " +
            "INNER JOIN patient_person p ON drug_ord.patient_id = p.id " +
            "LEFT JOIN drug_dispense dd ON drug_ord.id = dd.drug_order_id AND dd.archived = 0 " +
            "LEFT JOIN drug_dispense dd2 ON dd2.id = (" +
            "SELECT id FROM drug_dispense " +
            "WHERE drug_order_id = drug_ord.id AND archived = 0 " +
            "ORDER BY date_time_dispensed DESC LIMIT 1" +
            ") " +
            "WHERE drug_ord.dispensing_status IN (:statuses) AND drug_ord.archived = 0 " +
            "GROUP BY drug_ord.id, p.id, dd2.id, dd2.dispenser_name " +
            "ORDER BY drug_ord.prescription_date DESC",
            countQuery = "SELECT COUNT(DISTINCT drug_ord.id) " +
                    "FROM drug_order drug_ord " +
                    "WHERE drug_ord.dispensing_status IN (:statuses) AND drug_ord.archived = 0",
            nativeQuery = true)
    Page<DispensationQueueProjection> getDispensationQueue(@Param("statuses") List<String> statuses, Pageable pageable);

    @Query(value = "SELECT " +
            "dd.date_time_dispensed as dateTimeDispensed, " +
            "p.first_name as patientFirstName, " +
            "p.surname as patientLastName, " +
            "p.hospital_number as patientHospitalNumber, " +
            "dd.medication_name as medicationName, " +
            "dd.brand_name as brandName, " +
            "dd.strength as strength, " +
            "drug_ord.dosage_amount as dosageAmount, " +
            "drug_ord.frequency as frequency, " +
            "dd.quantity_dispensed as quantityDispensed, " +
            "dd.quantity_unit as quantityUnit, " +
            "dd.dispenser_name as dispenserName, " +
            "drug_ord.prescription_date as prescriptionDate, " +
            "drug_ord.prescriber_name as prescriberName, " +
            "drug_ord.prescription_type as prescriptionType, " +
            "dd.dispensing_notes as dispensingNotes, " +
            "dd.is_refill as isRefill " +
            "FROM drug_dispense dd " +
            "INNER JOIN drug_order drug_ord ON dd.drug_order_id = drug_ord.id " +
            "INNER JOIN patient_person p ON dd.patient_id = p.id " +
            "WHERE dd.archived = 0 " +
            "AND (?1 IS NULL OR dd.date_time_dispensed >= CAST(?1 AS TIMESTAMP)) " +
            "AND (?2 IS NULL OR dd.date_time_dispensed <= CAST(?2 AS TIMESTAMP)) " +
            "ORDER BY dd.date_time_dispensed DESC",

            countQuery = "SELECT COUNT(*) FROM drug_dispense dd WHERE dd.archived = 0 " +
                    "AND (?1 IS NULL OR dd.date_time_dispensed >= CAST(?1 AS TIMESTAMP)) " +
                    "AND (?2 IS NULL OR dd.date_time_dispensed <= CAST(?2 AS TIMESTAMP))",
            nativeQuery = true)
    Page<DispensingHistoryProjection> getDispensingHistory(
            String startDate,
            String endDate,
            Pageable pageable);

}
