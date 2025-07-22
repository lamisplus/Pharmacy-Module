package org.lamisplus.modules.pharmacy.controller;

import org.lamisplus.modules.pharmacy.domain.dto.*;
import org.lamisplus.modules.pharmacy.domain.entity.DispensingStatus;
import org.lamisplus.modules.pharmacy.domain.projections.DispensationQueueProjection;
import org.lamisplus.modules.pharmacy.domain.projections.MedicationHistoryProjection;
import org.lamisplus.modules.pharmacy.service.DrugOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/drug-orders")
public class DrugOrderController {

    private static final Logger log = LoggerFactory.getLogger(DrugOrderController.class);

    private final DrugOrderService drugOrderService;

    @Autowired
    public DrugOrderController(DrugOrderService drugOrderService) {
        this.drugOrderService = drugOrderService;
    }

    @PostMapping("/order")
    public ResponseEntity<DrugOrderDTO> createDrugOrder(@Valid @RequestBody DrugOrderDTO dto) {
        log.info("Creating drug order for patient: {}", dto.getPatientId());
        DrugOrderDTO created = drugOrderService.createDrugOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DrugOrderDTO> updateDrugOrder(@PathVariable Long id, @Valid @RequestBody DrugOrderDTO dto) {
        log.info("Updating drug order: {}", id);
        DrugOrderDTO updated = drugOrderService.updateDrugOrder(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all-drugOrder")
    public ResponseEntity<Page<DrugOrderDTO>> getAllDrugOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "prescriptionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<DrugOrderDTO> orders = drugOrderService.getAllDrugOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrugOrderDTO> getDrugOrder(@PathVariable Long id) {
        DrugOrderDTO drugOrder = drugOrderService.getDrugOrderById(id);
        return ResponseEntity.ok(drugOrder);
    }

    @GetMapping("/get-patient-drugOrder/{patientId}")
    public ResponseEntity<List<DrugOrderDTO>> getPatientDrugOrders(@PathVariable Long patientId) {
        List<DrugOrderDTO> orders = drugOrderService.getPatientDrugOrders(patientId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<DrugOrderDTO>> getPendingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("prescriptionDate").descending());
        Page<DrugOrderDTO> orders = drugOrderService.getPendingDrugOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<MedicationHistoryProjection>> getMedicationHistory(@PathVariable Long patientId) {
        List<MedicationHistoryProjection> history = drugOrderService.getMedicationHistory(patientId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/dispensation-queue")
    public ResponseEntity<Page<DispensationQueueProjection>> getDispensationQueue(
            @RequestParam(required = false) List<DispensingStatus> statuses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (statuses == null || statuses.isEmpty()) {
            statuses = Arrays.asList(DispensingStatus.PENDING, DispensingStatus.PARTIALLY_DISPENSED);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<DispensationQueueProjection> queue = drugOrderService.getDispensationQueue(statuses, pageable);
        return ResponseEntity.ok(queue);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDrugOrder(@PathVariable Long id) {
        drugOrderService.deleteDrugOrder(id);
        return ResponseEntity.ok("Record has been deleted successfully");
    }
}