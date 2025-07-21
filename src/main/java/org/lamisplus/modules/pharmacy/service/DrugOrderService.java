package org.lamisplus.modules.pharmacy.service;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.lamisplus.modules.base.service.UserService;
import org.lamisplus.modules.patient.domain.Patient;
import org.lamisplus.modules.patient.domain.dto.PersonResponseDto;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VisitRepository;
import org.lamisplus.modules.patient.service.PersonService;
import org.lamisplus.modules.pharmacy.controller.DrugOrderController;
import org.lamisplus.modules.pharmacy.domain.dto.*;
import org.lamisplus.modules.pharmacy.domain.entity.DispensingStatus;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.lamisplus.modules.pharmacy.domain.entity.OrderStatus;
import org.lamisplus.modules.pharmacy.domain.mapper.DrugOrderMapper;
import org.lamisplus.modules.pharmacy.domain.projections.DispensationQueueProjection;
import org.lamisplus.modules.pharmacy.domain.projections.MedicationHistoryProjection;
import org.lamisplus.modules.pharmacy.repository.DrugDispenseRepository;
import org.lamisplus.modules.pharmacy.repository.DrugOrderRepository;
import org.lamisplus.modules.pharmacy.util.JsonNodeTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lamisplus.modules.base.domain.entities.User;

@Service
@Slf4j
@RequiredArgsConstructor

public class DrugOrderService {


    private static final Logger log = LoggerFactory.getLogger(DrugOrderService.class);

    private final DrugOrderRepository drugOrderRepository;
    private final DrugDispenseRepository drugDispensingRepository;
    private final PersonService personService;
    private final DrugOrderMapper drugOrderMapper;
    private final PersonRepository personRepository;
    private final VisitRepository visitRepository;
    private UserService userService;

    public DrugOrderDTO createDrugOrder(DrugOrderDTO dto) {
        validatePatientExists(dto.getPatientId());

        DrugOrder saved = saveOrder(dto);
        log.info("Created drug order with ID: {} for patient: {}", saved.getId(), saved.getPatient().getId());

        return drugOrderMapper.toDTO(saved);
    }

    @Transactional
    public DrugOrder saveOrder(DrugOrderDTO dto) {
        DrugOrder drugOrder = drugOrderMapper.toEntity(dto);

        Person patient = personRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));
        drugOrder.setPatient(patient);

        if (dto.getVisitId() != null) {
            Visit visit = visitRepository.findById(dto.getVisitId())
                    .orElseThrow(() -> new EntityNotFoundException("Visit not found with id: " + dto.getVisitId()));
            drugOrder.setVisit(visit);
        }

        drugOrder.setOrderStatus(OrderStatus.ACTIVE);
        drugOrder.setDispensingStatus(DispensingStatus.PENDING);
        return drugOrderRepository.save(drugOrder);
    }

    private void validatePatientExists(Long patientId) {
        if (patientId <= 0) {
            throw new IllegalArgumentException("Invalid patient ID");
        }

        if (!personService.isPersonExist(patientId)) {
            throw new EntityNotFoundException("Patient not found with id: " + patientId);
        }
    }



    public DrugOrderDTO updateDrugOrder(Long id, DrugOrderDTO dto) {
        DrugOrder existingOrder = drugOrderRepository.findByIdAndArchived(id, 0)
                .orElseThrow(() -> new EntityNotFoundException("Drug order not found with id: " + id));

        dto.setId(existingOrder.getId());
        dto.setUuid(existingOrder.getUuid());
        dto.setPrescriptionDate(existingOrder.getPrescriptionDate());

        if (!existingOrder.getPatient().getId().equals(dto.getPatientId())) {
            Person patient = personRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));
            existingOrder.setPatient(patient);
        }

        if (dto.getVisitId() != null) {
            Visit visit = visitRepository.findById(dto.getVisitId())
                    .orElseThrow(() -> new EntityNotFoundException("Visit not found with id: " + dto.getVisitId()));
            existingOrder.setVisit(visit);
        }

        drugOrderMapper.updateEntity(dto, existingOrder);
        DrugOrder updated = drugOrderRepository.save(existingOrder);

        return drugOrderMapper.toDTO(updated);
    }


    public DrugOrderDTO getDrugOrderById(Long id) {
        DrugOrder drugOrder = drugOrderRepository.findByIdAndArchived(id, 0)
                .orElseThrow(() -> new EntityNotFoundException("Drug order not found with id: " + id));

        return drugOrderMapper.toDTO(drugOrder);
    }

    public List<DrugOrderDTO> getPatientDrugOrders(Long patientId) {
        validatePatientExists(patientId);
        List<DrugOrder> orders = drugOrderRepository.findByPatientId(patientId);

        return drugOrderMapper.toDTOList(orders);
    }

    public Page<DrugOrderDTO> getPendingDrugOrders(Pageable pageable) {
        Page<DrugOrder> orders = drugOrderRepository.findByDispensingStatus(DispensingStatus.PENDING, pageable);
        return orders.map(drugOrderMapper::toDTO);
    }

    public void discontinueDrugOrder(Long id, String reason, Long discontinuedBy) {
        DrugOrder drugOrder = drugOrderRepository.findByIdAndArchived(id, 0)
                .orElseThrow(() -> new EntityNotFoundException("Drug order not found with id: " + id));

        drugOrder.setOrderStatus(OrderStatus.DISCONTINUED);


        drugOrderRepository.save(drugOrder);
        log.info("Discontinued drug order: {} for reason: {}", id, reason);
    }

    public Page<DrugOrderDTO> getAllDrugOrders(Pageable pageable) {
        Page<DrugOrder> drugOrders = drugOrderRepository.findByArchived(0, pageable);
        return drugOrders.map(drugOrderMapper::toDTO);
    }

    public void deleteDrugOrder(Long id) {
        DrugOrder drugOrder = drugOrderRepository.findByIdAndArchived(id, 0)
                .orElseThrow(() -> new EntityNotFoundException("Drug order not found with id: " + id));

        drugOrder.setArchived(1);
        drugOrderRepository.save(drugOrder);
       log.info("Archived drug order: {}", id);
    }

    public List<MedicationHistoryProjection> getMedicationHistory(Long patientId) {
        validatePatientExists(patientId);
        return drugOrderRepository.getMedicationHistory(patientId);
    }

    public Page<DispensationQueueProjection> getDispensationQueue(List<DispensingStatus> statuses, Pageable pageable) {
        List<String> statusStrings = new ArrayList<>();
        for (DispensingStatus status : statuses) {
            statusStrings.add(status.name());
        }

        return drugDispensingRepository.getDispensationQueue(statusStrings, pageable);
    }


}