package org.lamisplus.modules.pharmacy.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.lamisplus.modules.base.service.UserService;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.service.PersonService;
import org.lamisplus.modules.pharmacy.controller.DrugDispenseController;
import org.lamisplus.modules.pharmacy.domain.dto.*;
import org.lamisplus.modules.pharmacy.domain.entity.DispensingStatus;
import org.lamisplus.modules.pharmacy.domain.entity.DrugDispense;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.lamisplus.modules.pharmacy.domain.entity.OrderStatus;
import org.lamisplus.modules.pharmacy.domain.mapper.DrugDispenseMapper;
import org.lamisplus.modules.pharmacy.domain.projections.DispensingHistoryProjection;
import org.lamisplus.modules.pharmacy.domain.projections.DrugDispenseProjection;
import org.lamisplus.modules.pharmacy.repository.DrugDispenseRepository;
import org.lamisplus.modules.pharmacy.repository.DrugOrderRepository;
import org.lamisplus.modules.pharmacy.util.JsonNodeTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.lamisplus.modules.base.domain.entities.User;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DrugDispenseService {

    private static final Logger log = LoggerFactory.getLogger(DrugDispenseService.class);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private final DrugDispenseRepository drugDispensingRepository;
    private final DrugOrderRepository drugOrderRepository;
    private final PersonService personService;
    private final DrugDispenseMapper drugDispenseMapper;
    private final UserService userService;
    private PersonRepository personRepository;




    @Transactional
    public DrugDispenseDTO dispenseDrug(DrugDispenseDTO dto) {
        log.info("Starting dispensing process - Order ID: {}, Patient ID: {}",
                dto.getDrugOrderId(), dto.getPatientId());

        DrugOrder drugOrder = drugOrderRepository.findByIdAndArchived(dto.getDrugOrderId(), 0)
                .orElseThrow(() -> new EntityNotFoundException("Drug order not found with id: " + dto.getDrugOrderId()));

        log.info("Drug order found: {}", drugOrder.getMedicationName());

        Person patient = drugOrder.getPatient();
        log.info("Using patient from drug order: {} {} (ID: {})",
                patient.getFirstName(), patient.getSurname(), patient.getId());

        if (!patient.getId().equals(dto.getPatientId())) {
            log.warn("Patient ID mismatch - Order has: {}, DTO has: {}",
                    patient.getId(), dto.getPatientId());
        }


        log.info("Before update - Status: {}, Dispensing: {}", drugOrder.getOrderStatus(), drugOrder.getDispensingStatus());
        drugOrder.setOrderStatus(OrderStatus.COMPLETED);
        drugOrder.setDispensingStatus(DispensingStatus.DISPENSED);
        log.info("After setting - Status: {}, Dispensing: {}", drugOrder.getOrderStatus(), drugOrder.getDispensingStatus());
        DrugOrder savedDrugOrder = drugOrderRepository.saveAndFlush(drugOrder);

        log.info("After save - Status: {}, Dispensing: {}", savedDrugOrder.getOrderStatus(), savedDrugOrder.getDispensingStatus());


        DrugDispense dispensing = drugDispenseMapper.toEntity(dto);
        dispensing.setDrugOrder(savedDrugOrder);
        dispensing.setPatient(patient);
        dispensing.setMedicationName(drugOrder.getMedicationName());
        dispensing.setFormulation(drugOrder.getFormulation());
        dispensing.setStrength(drugOrder.getStrength());
        dispensing.setDateTimeDispensed(LocalDateTime.now());

        DrugDispense saved = drugDispensingRepository.save(dispensing);

        return drugDispenseMapper.toDTO(saved);
    }

    @Transactional
    public DrugDispenseDTO updateDispensing(Long id, DrugDispenseDTO dto) {

        DrugDispense existing = drugDispensingRepository.findByIdAndArchived(id, 0)
                .orElseThrow(() -> new EntityNotFoundException("Dispensing record not found with id: " + id));


        DrugOrder drugOrder = drugOrderRepository.findByIdAndArchived(existing.getDrugOrder().getId(), 0)
                .orElseThrow(() -> new EntityNotFoundException("Drug order not found with id: " + existing.getDrugOrder().getId()));

        Person patient = personRepository.findById(existing.getPatient().getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + existing.getPatient().getId()));


        dto.setId(existing.getId());
        dto.setUuid(existing.getUuid());
        dto.setDrugOrderId(drugOrder.getId());
        dto.setPatientId(patient.getId());
        dto.setDateTimeDispensed(existing.getDateTimeDispensed());

        drugDispenseMapper.updateEntity(dto, existing);

        existing.setDrugOrder(drugOrder);
        existing.setPatient(patient);

        DrugDispense updated = drugDispensingRepository.save(existing);

        return drugDispenseMapper.toDTO(updated);
    }






    public DrugDispenseDTO getDispensingById(Long id) {
        DrugDispense dispensing = drugDispensingRepository.findByIdAndArchived(id, 0)
                .orElseThrow(() -> new EntityNotFoundException("Dispensing record not found with id: " + id));

        return drugDispenseMapper.toDTO(dispensing);
    }

    public List<DrugDispenseDTO> getDispensingByDrugOrder(Long drugOrderId) {
        List<DrugDispense> dispensings = drugDispensingRepository.findByDrugOrderIdAndArchived(drugOrderId, 0);
        return drugDispenseMapper.toDTOList(dispensings);
    }
//@Transactional
public Page<DrugDispenseDTO> getPatientDispensingHistory(Long patientId, Pageable pageable) {
    Page<DrugDispenseProjection> projections = drugDispensingRepository
            .findDispenseHistoryByPatientId(patientId, pageable);

    if (projections.isEmpty()) {
        throw new EntityNotFoundException("No dispensing history found for patient with ID: " + patientId);
    }

    return projections.map(this::mapProjectionToDTO);
}


    private DrugDispenseDTO mapProjectionToDTO(DrugDispenseProjection p) {
        DrugDispenseDTO dto = new DrugDispenseDTO();
        dto.setId(p.getId());
        dto.setUuid(p.getUuid());
        dto.setMedicationName(p.getMedicationName());
        dto.setBrandName(p.getBrandName());
        dto.setManufacturer(p.getManufacturer());
        dto.setBatchNumber(p.getBatchNumber());
        dto.setExpiryDate(p.getExpiryDate());
        dto.setQuantityDispensed(p.getQuantityDispensed());
        dto.setQuantityUnit(p.getQuantityUnit());
        dto.setFormulation(p.getFormulation());
        dto.setStrength(p.getStrength());
        dto.setDateTimeDispensed(p.getDateTimeDispensed());
        dto.setDispensedBy(p.getDispensedBy());
        dto.setDispenserName(p.getDispenserName());
        dto.setSubstitutionMade(p.getSubstitutionMade());
        dto.setSubstitutionReason(p.getSubstitutionReason());
        dto.setDispensingNotes(p.getDispensingNotes());
        dto.setIsRefill(p.getIsRefill());
        dto.setRefillNumber(p.getRefillNumber());
        return dto;
    }


    public Page<DispensingHistoryProjection> getDispensingHistory(Pageable pageable) {

        return drugDispensingRepository.getDispensingHistory( pageable);
    }

    public void deleteDispensing(Long id) {
        DrugDispense dispensing = drugDispensingRepository.findByIdAndArchived(id, 0)
                .orElseThrow(() -> new EntityNotFoundException("Dispensing record not found with id: " + id));

        dispensing.setArchived(1);
        drugDispensingRepository.save(dispensing);


        updateDrugOrderStatusAfterDeletion(dispensing.getDrugOrder().getId());

        log.info("Archived dispensing record: {}", id);
    }

    private void updateDrugOrderDispensingStatus(DrugOrder drugOrder, Integer totalDispensed) {
        DispensingStatus newStatus;


        Integer quantityPrescribed;
        try {
            quantityPrescribed = Integer.valueOf(drugOrder.getQuantityPrescribed());
        } catch (NumberFormatException e) {
           log.error("Invalid quantity prescribed format: {}", drugOrder.getQuantityPrescribed());
            return;
        }

        if (totalDispensed.equals(quantityPrescribed)) {
            newStatus = DispensingStatus.DISPENSED;
        } else if (totalDispensed > 0) {
            newStatus = DispensingStatus.PARTIALLY_DISPENSED;
        } else {
            newStatus = DispensingStatus.PENDING;
        }

        drugOrderRepository.updateDispensingStatus(drugOrder.getId(), newStatus);
    }

    private void updateDrugOrderStatusAfterDeletion(Long drugOrderId) {
        Integer totalDispensed = drugDispensingRepository.getTotalQuantityDispensed(drugOrderId);
        DrugOrder drugOrder = drugOrderRepository.findById(drugOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Drug order not found"));

        updateDrugOrderDispensingStatus(drugOrder, totalDispensed != null ? totalDispensed : 0);
    }

    private void validatePatientExists(Long patientId) {
        if (patientId <= 0) {
            throw new IllegalArgumentException("Invalid patient ID");
        }

        if (!personService.isPersonExist(patientId)) {
            throw new EntityNotFoundException("Patient not found with id: " + patientId);
        }
    }
}