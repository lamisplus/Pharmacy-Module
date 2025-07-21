package org.lamisplus.modules.pharmacy.domain.mapper;

import org.lamisplus.modules.pharmacy.domain.dto.DrugOrderDTO;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DrugOrderMapper {


    public DrugOrderDTO toDTO(DrugOrder entity) {
        if (entity == null) {
            return null;
        }

        DrugOrderDTO dto = new DrugOrderDTO();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
       dto.setPatientId(entity.getPatient().getId());
       dto.setVisitId(entity.getVisit().getId());
        dto.setMedicationName(entity.getMedicationName());
        dto.setFormulation(entity.getFormulation());
        dto.setStrength(entity.getStrength());
        dto.setDosageAmount(entity.getDosageAmount());
        dto.setRouteOfAdmin(entity.getRouteOfAdmin());
        dto.setFrequency(entity.getFrequency());
        dto.setTimingInstructions(entity.getTimingInstructions());
        dto.setDuration(entity.getDuration());
        dto.setDurationUnit(entity.getDurationUnit());
        dto.setQuantityPrescribed(entity.getQuantityPrescribed());
        dto.setRefillsAllowed(entity.getRefillsAllowed());
        dto.setRefillsRemaining(entity.getRefillsRemaining());
        dto.setPrescriptionType(entity.getPrescriptionType());
        dto.setNotes(entity.getNotes());
        dto.setPrescribedBy(entity.getPrescribedBy());
        dto.setPrescriberName(entity.getPrescriberName());
        dto.setPrescriptionDate(entity.getPrescriptionDate());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setOrderStatus(entity.getOrderStatus());
        dto.setDispensingStatus(entity.getDispensingStatus());

        return dto;
    }


    public DrugOrder toEntity(DrugOrderDTO dto) {
        if (dto == null) {
            return null;
        }

        DrugOrder entity = new DrugOrder();
        entity.setId(dto.getId());
        entity.setUuid(dto.getUuid());
       // entity.setPatientId(dto.getPatientId());
      //  entity.setVisitId(dto.getVisitId());
        entity.setMedicationName(dto.getMedicationName());
        entity.setFormulation(dto.getFormulation());
        entity.setStrength(dto.getStrength());
        entity.setDosageAmount(dto.getDosageAmount());
        entity.setRouteOfAdmin(dto.getRouteOfAdmin());
        entity.setFrequency(dto.getFrequency());
        entity.setTimingInstructions(dto.getTimingInstructions());
        entity.setDuration(dto.getDuration());
        entity.setDurationUnit(dto.getDurationUnit());
        entity.setQuantityPrescribed(dto.getQuantityPrescribed());


        entity.setRefillsAllowed(dto.getRefillsAllowed());
        entity.setRefillsRemaining(dto.getRefillsRemaining());
        entity.setPrescriptionType(dto.getPrescriptionType());
        entity.setNotes(dto.getNotes());
        entity.setPrescribedBy(dto.getPrescribedBy());
        entity.setPrescriberName(dto.getPrescriberName());
        entity.setPrescriptionDate(dto.getPrescriptionDate());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());

        entity.setOrderStatus(dto.getOrderStatus());
        entity.setDispensingStatus(dto.getDispensingStatus());

        return entity;
    }

    public void updateEntity(DrugOrderDTO dto, DrugOrder entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Don't update ID, UUID, patientId, and prescriptionDate as per original code
    //    entity.setVisitId(dto.getVisitId());
        entity.setMedicationName(dto.getMedicationName());
        entity.setFormulation(dto.getFormulation());
        entity.setStrength(dto.getStrength());
        entity.setDosageAmount(dto.getDosageAmount());
        entity.setRouteOfAdmin(dto.getRouteOfAdmin());
        entity.setFrequency(dto.getFrequency());
        entity.setTimingInstructions(dto.getTimingInstructions());
        entity.setDuration(dto.getDuration());
        entity.setDurationUnit(dto.getDurationUnit());
        entity.setQuantityPrescribed(dto.getQuantityPrescribed());



        entity.setRefillsAllowed(dto.getRefillsAllowed());
        entity.setRefillsRemaining(dto.getRefillsRemaining());
        entity.setPrescriptionType(dto.getPrescriptionType());
        entity.setNotes(dto.getNotes());
        entity.setPrescribedBy(dto.getPrescribedBy());
        entity.setPrescriberName(dto.getPrescriberName());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());



        entity.setOrderStatus(dto.getOrderStatus());
        entity.setDispensingStatus(dto.getDispensingStatus());
    }

    public List<DrugOrderDTO> toDTOList(List<DrugOrder> entities) {
        if (entities == null) {
            return null;
        }

        List<DrugOrderDTO> dtos = new ArrayList<>();
        for (DrugOrder entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    public List<DrugOrder> toEntityList(List<DrugOrderDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        List<DrugOrder> entities = new ArrayList<>();
        for (DrugOrderDTO dto : dtos) {
            entities.add(toEntity(dto));
        }
        return entities;
    }
}