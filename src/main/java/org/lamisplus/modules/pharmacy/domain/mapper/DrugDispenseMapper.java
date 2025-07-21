package org.lamisplus.modules.pharmacy.domain.mapper;


import org.lamisplus.modules.pharmacy.domain.dto.DrugDispenseDTO;
import org.lamisplus.modules.pharmacy.domain.entity.DrugDispense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DrugDispenseMapper {


    public DrugDispenseDTO toDTO(DrugDispense entity) {
        if (entity == null) {
            return null;
        }

        DrugDispenseDTO dto = new DrugDispenseDTO();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setPatientId(entity.getPatient().getId());
        dto.setDrugOrderId(entity.getDrugOrder().getId());
        dto.setMedicationName(entity.getMedicationName());
        dto.setBrandName(entity.getBrandName());
        dto.setManufacturer(entity.getManufacturer());
        dto.setBatchNumber(entity.getBatchNumber());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setQuantityDispensed(entity.getQuantityDispensed());
        dto.setQuantityUnit(entity.getQuantityUnit());
        dto.setFormulation(entity.getFormulation());
        dto.setStrength(entity.getStrength());
        dto.setDateTimeDispensed(entity.getDateTimeDispensed());
        dto.setDispensedBy(entity.getDispensedBy());
        dto.setDispenserName(entity.getDispenserName());
        dto.setSubstitutionMade(entity.getSubstitutionMade());
        dto.setSubstitutionReason(entity.getSubstitutionReason());
        dto.setDispensingNotes(entity.getDispensingNotes());
        dto.setIsRefill(entity.getIsRefill());
        dto.setRefillNumber(entity.getRefillNumber());

        return dto;
    }


    public DrugDispense toEntity(DrugDispenseDTO dto) {
        if (dto == null) {
            return null;
        }

        DrugDispense entity = new DrugDispense();
        entity.setId(dto.getId());
        entity.setUuid(dto.getUuid());

        entity.setMedicationName(dto.getMedicationName());
        entity.setBrandName(dto.getBrandName());
        entity.setManufacturer(dto.getManufacturer());
        entity.setBatchNumber(dto.getBatchNumber());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setQuantityDispensed(dto.getQuantityDispensed());
        entity.setQuantityUnit(dto.getQuantityUnit());
        entity.setFormulation(dto.getFormulation());
        entity.setStrength(dto.getStrength());
        entity.setDateTimeDispensed(dto.getDateTimeDispensed());
        entity.setDispensedBy(dto.getDispensedBy());
        entity.setDispenserName(dto.getDispenserName());
        entity.setSubstitutionMade(dto.getSubstitutionMade());
        entity.setSubstitutionReason(dto.getSubstitutionReason());
        entity.setDispensingNotes(dto.getDispensingNotes());
        entity.setIsRefill(dto.getIsRefill());
        entity.setRefillNumber(dto.getRefillNumber());

        return entity;
    }


    public void updateEntity(DrugDispenseDTO dto, DrugDispense entity) {
        if (dto == null || entity == null) {
            return;
        }


        entity.setMedicationName(dto.getMedicationName());
        entity.setBrandName(dto.getBrandName());
        entity.setManufacturer(dto.getManufacturer());
        entity.setBatchNumber(dto.getBatchNumber());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setQuantityDispensed(dto.getQuantityDispensed());
        entity.setQuantityUnit(dto.getQuantityUnit());
        entity.setFormulation(dto.getFormulation());
        entity.setStrength(dto.getStrength());
        entity.setDispensedBy(dto.getDispensedBy());
        entity.setDispenserName(dto.getDispenserName());
        entity.setSubstitutionMade(dto.getSubstitutionMade());
        entity.setSubstitutionReason(dto.getSubstitutionReason());
        entity.setDispensingNotes(dto.getDispensingNotes());
        entity.setIsRefill(dto.getIsRefill());
        entity.setRefillNumber(dto.getRefillNumber());
    }


    public List<DrugDispenseDTO> toDTOList(List<DrugDispense> entities) {
        if (entities == null) {
            return null;
        }

        List<DrugDispenseDTO> dtos = new ArrayList<>();
        for (DrugDispense entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }


    public List<DrugDispense> toEntityList(List<DrugDispenseDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        List<DrugDispense> entities = new ArrayList<>();
        for (DrugDispenseDTO dto : dtos) {
            entities.add(toEntity(dto));
        }
        return entities;
    }
}