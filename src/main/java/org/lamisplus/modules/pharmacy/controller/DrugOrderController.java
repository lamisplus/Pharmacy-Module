package org.lamisplus.modules.pharmacy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.lamisplus.modules.pharmacy.domain.dto.*;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.lamisplus.modules.pharmacy.service.DrugDispenseService;
import org.lamisplus.modules.pharmacy.service.DrugOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/drug-orders")
@Slf4j
@RequiredArgsConstructor
@Validated
public class DrugOrderController {

    private final DrugOrderService drugOrderService;

    @GetMapping
    public ResponseEntity<List<DrugOrderDTO>> getAllDrugOrders(@RequestParam (required = false, defaultValue = "0")Long patientId) {
        if(patientId == null) patientId = 0L;
        return ResponseEntity.ok(drugOrderService.getAllDrugOrders(patientId));
    }

    @GetMapping("/patients")
    public ResponseEntity<PharmacyOrderListMetaDataDTO> getAllDrugOrdersForPatients(@RequestParam(defaultValue = "*") String searchParam,
                                                                                    @RequestParam(defaultValue = "0") Integer pageNo,
                                                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(drugOrderService.getAllDrugOrdersForPatients(searchParam, pageNo, pageSize));
    }

    @GetMapping("/visits/{id}")
    public ResponseEntity<List<DrugOrderDTO>> getAllDrugOrdersByVisitId(@PathVariable Long id) {
        return ResponseEntity.ok(drugOrderService.getAllDrugOrdersByVisitId(id));
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<List<PatientDrugOrderDTO>> getAllDrugOrdersForAPatient(@PathVariable Long id) {
        return ResponseEntity.ok(drugOrderService.getAllDrugOrdersForAPatient(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrugOrderDTO> getDrugOrder(@PathVariable Long id) {
        return ResponseEntity.ok(drugOrderService.getDrugOrder(id));
    }

    @PostMapping
    public ResponseEntity<List<DrugOrder>> save(@RequestBody @Valid DrugOrderDTOS drugOrderDTOS) {
        return ResponseEntity.ok(drugOrderService.save(drugOrderDTOS));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrugOrder> update(@PathVariable Long id, @RequestBody DrugOrderDTO drugOrderDTO) {
        return ResponseEntity.ok(drugOrderService.update(id, drugOrderDTO));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id) {
        return ResponseEntity.ok(drugOrderService.delete(id));
    }
}
