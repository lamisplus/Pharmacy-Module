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
import org.lamisplus.modules.patient.domain.entity.Person;

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
@Table(name = "drug_dispense")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "archived = 0")
public class DrugDispense extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String uuid;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_order_id", nullable = false)
    private DrugOrder drugOrder;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Person patient;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "quantity_dispensed", nullable = false)
    private Integer quantityDispensed;

    @Column(name = "quantity_unit")
    private String quantityUnit;

    @Column(name = "formulation")
    private String formulation;

    @Column(name = "strength")
    private String strength;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_time_dispensed", nullable = false)
    private LocalDateTime dateTimeDispensed;

    @Column(name = "dispensed_by")
    private Long dispensedBy;

    @Column(name = "dispenser_name")
    private String dispenserName;

    @Column(name = "substitution_made")
    private Boolean substitutionMade = false;

    @Column(name = "substitution_reason")
    private String substitutionReason;


    @Column(name = "dispensing_notes", columnDefinition = "TEXT")
    private String dispensingNotes;

    @Column(name = "encounter_date")
    private LocalDateTime encounterDate;


    @Column(name = "is_refill")
    private Boolean isRefill = false;

    @Column(name = "refill_number")
    private Integer refillNumber;

    @Column(name = "archived")
    private Integer archived = 0;

    @Column(name = "organisation_unit_id")
    private Long organisationUnitId;



    @PrePersist
    public void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
        if (this.dateTimeDispensed == null) {
            this.dateTimeDispensed = LocalDateTime.now();
        }
        if (this.archived == null) {
            this.archived = 0;
        }
        if (this.dispensedBy == null || this.dispenserName == null) {
            SecurityUtils.getCurrentUserLogin().ifPresent(email -> {
                this.dispenserName = email;
            });
        }
    }
}
