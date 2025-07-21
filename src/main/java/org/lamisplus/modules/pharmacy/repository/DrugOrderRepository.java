package org.lamisplus.modules.pharmacy.repository;

import org.lamisplus.modules.pharmacy.domain.entity.DispensingStatus;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.lamisplus.modules.pharmacy.domain.entity.OrderStatus;
import org.lamisplus.modules.pharmacy.domain.projections.MedicationHistoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrugOrderRepository extends JpaRepository<DrugOrder, Long> {

    Optional<DrugOrder> findByIdAndArchived(Long id, Integer archived);

    Optional<DrugOrder> findByUuidAndArchived(String uuid, Integer archived);

    @Query("SELECT do FROM DrugOrder do WHERE do.patient.id = :patientId AND do.archived = 0 ORDER BY do.prescriptionDate DESC")
    List<DrugOrder> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT do FROM DrugOrder do WHERE do.patient.id = :patientId AND do.orderStatus = :status AND do.archived = 0")
    List<DrugOrder> findByPatientIdAndOrderStatus(@Param("patientId") Long patientId, @Param("status") OrderStatus status);

    @Query("SELECT do FROM DrugOrder do WHERE do.dispensingStatus = :status AND do.archived = 0 ORDER BY do.prescriptionDate DESC")
    Page<DrugOrder> findByDispensingStatus(@Param("status") DispensingStatus status, Pageable pageable);

    @Query(value = "SELECT " +
            "dr.prescription_date as prescriptionDate, " +
            "dr.medication_name as medicationName, " +
            "dr.strength as strength, " +
            "dr.dosage_amount as dosageAmount, " +
            "dr.frequency as frequency, " +
            "dr.prescriber_name as prescriberName, " +
            "CASE WHEN dd.id IS NOT NULL THEN true ELSE false END as isDispensed, " +
            "dd.date_time_dispensed as dispensedDate, " +
            "dd.quantity_dispensed as quantityDispensed, " +
            "dd.quantity_unit as quantityUnit, " +
            "dd.dispenser_name as dispenserName, " +
            "dr.prescription_type as prescriptionType, " +
            "dr.notes AS notes "+

            "FROM drug_order dr " +
            "LEFT JOIN drug_dispense dd ON dr.id = dd.drug_order_id AND dd.archived = 0 " +
            "WHERE dr.patient_id = :patientId AND dr.archived = 0 " +
            "ORDER BY dr.prescription_date DESC",
            nativeQuery = true)
    List<MedicationHistoryProjection> getMedicationHistory(@Param("patientId") Long patientId);

    @Query("SELECT do FROM DrugOrder do WHERE do.archived = :archived ORDER BY do.prescriptionDate DESC")
    Page<DrugOrder> findByArchived(@Param("archived") Integer archived, Pageable pageable);

    @Modifying
    @Query("UPDATE DrugOrder do SET do.dispensingStatus = :status WHERE do.id = :orderId")
    void updateDispensingStatus(@Param("orderId") Long orderId, @Param("status") DispensingStatus status);
}
