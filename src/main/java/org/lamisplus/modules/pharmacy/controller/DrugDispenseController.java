package org.lamisplus.modules.pharmacy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.pharmacy.domain.dto.*;
import org.lamisplus.modules.pharmacy.domain.entity.DrugDispense;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.lamisplus.modules.pharmacy.domain.projections.DispensingHistoryProjection;
import org.lamisplus.modules.pharmacy.service.DrugDispenseService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/drug-dispensing")
public class DrugDispenseController {

    private static final Logger log = LoggerFactory.getLogger(DrugDispenseController.class);

    private final DrugDispenseService drugDispensingService;

    @Autowired
    public DrugDispenseController(DrugDispenseService drugDispensingService) {
        this.drugDispensingService = drugDispensingService;
    }

    @PostMapping("/dispense")
    public ResponseEntity<DrugDispenseDTO> dispenseDrug(@Valid @RequestBody DrugDispenseDTO dto) {
        log.info("Dispensing drug for order: {}", dto.getDrugOrderId());
        DrugDispenseDTO dispensed = drugDispensingService.dispenseDrug(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dispensed);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrugDispenseDTO> updateDispensing(@PathVariable Long id, @Valid @RequestBody DrugDispenseDTO dto) {
        log.info("Updating dispensing record: {}", id);
        DrugDispenseDTO updated = drugDispensingService.updateDispensing(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrugDispenseDTO> getDispensing(@PathVariable Long id) {
        DrugDispenseDTO dispensing = drugDispensingService.getDispensingById(id);
        return ResponseEntity.ok(dispensing);
    }

    @GetMapping("/order/{drugOrderId}")
    public ResponseEntity<List<DrugDispenseDTO>> getDispensingByOrder(@PathVariable Long drugOrderId) {
        List<DrugDispenseDTO> dispensings = drugDispensingService.getDispensingByDrugOrder(drugOrderId);
        return ResponseEntity.ok(dispensings);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DrugDispenseDTO>> getPatientDispensingHistory(@PathVariable Long patientId) {
        List<DrugDispenseDTO> history = drugDispensingService.getPatientDispensingHistory(patientId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history")
    public ResponseEntity<Page<DispensingHistoryProjection>> getDispensingHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateTimeDispensed").descending());
        Page<DispensingHistoryProjection> history = drugDispensingService.getDispensingHistory(startDate, endDate, pageable);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispensing(@PathVariable Long id) {
        drugDispensingService.deleteDispensing(id);
        return ResponseEntity.noContent().build();
    }
}