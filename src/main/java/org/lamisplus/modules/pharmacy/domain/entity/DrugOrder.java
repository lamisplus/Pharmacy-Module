package org.lamisplus.modules.pharmacy.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.lamisplus.modules.base.domain.entities.Audit;
import org.lamisplus.modules.base.security.SecurityUtils;
import org.lamisplus.modules.patient.domain.Patient;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
})
@Entity
@Table(name = "drug_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "archived = 0")

public class DrugOrder extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String uuid;
//
//    @NotNull
//    @Column(name = "patient_id", nullable = false)
//    private Long patientId;

//    @NotNull
//    @Column(name = "encounter_id", nullable = false)
//    private Long encounterId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Person patient;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;


    @NotBlank
    @Column(name = "medication_name", nullable = false)
    private String medicationName;


    @Column(name = "formulation")
    private String formulation; // Tablet, Capsule, Syrup, Injection

    @Column(name = "strength")
    private String strength; // 500mg, 125mg/5mL

    @Column(name = "dosage_amount")
    private String dosageAmount; // 1 tablet, 5mL, 2 puffs

    @Column(name = "route_of_admin")
    private String routeOfAdmin; // Oral, IV, IM, SC, Topical, Inhalation

    @Column(name = "frequency")
    private String frequency; // Once daily, BID, TID, QID, Q4H, Q6H, Q8H, Q12H

    @Column(name = "timing_instructions")
    private String timingInstructions; // Before meals, After meals, With meals, At bedtime

    @Column(name = "duration")
    private String duration;

    @Column(name = "duration_unit")
    private String durationUnit;

    @Column(name = "quantity_prescribed")
    private String quantityPrescribed;


    @Column(name = "refills_allowed")
    private Integer refillsAllowed = 0;

    @Column(name = "refills_remaining")
    private Integer refillsRemaining = 0;



    @Column(name = "prescription_type")
    private String prescriptionType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;


    @Column(name = "prescribed_by")
    private Long prescribedBy;

    @Column(name = "prescriber_name")
    private String prescriberName ;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "prescription_date", nullable = false)
    private LocalDateTime prescriptionDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.ACTIVE;

    @Column(name = "dispensing_status")
    @Enumerated(EnumType.STRING)
    private DispensingStatus dispensingStatus = DispensingStatus.PENDING;




    @Column(name = "archived")
    private Integer archived = 0;

    @Column(name = "organisation_unit_id")
    private Long organisationUnitId;



    @PrePersist
    public void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
        if (this.prescriptionDate == null) {
            this.prescriptionDate = LocalDateTime.now();
        }
        if (this.refillsRemaining == null && this.refillsAllowed != null) {
            this.refillsRemaining = this.refillsAllowed;
        }

        if (this.archived == null) {
            this.archived = 0;
        }

        if (this.prescribedBy == null || this.prescriberName == null) {
            SecurityUtils.getCurrentUserLogin().ifPresent(email -> {
                this.prescriberName = email;
            });
        }
    }
}
