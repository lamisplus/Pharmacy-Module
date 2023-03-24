package org.lamisplus.modules.pharmacy.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.base.controller.apierror.RecordExistException;
import org.lamisplus.modules.base.domain.dto.PageDTO;
import org.lamisplus.modules.patient.domain.dto.PersonResponseDto;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.service.PersonService;
import org.lamisplus.modules.pharmacy.domain.dto.*;
import org.lamisplus.modules.pharmacy.domain.entity.DrugDispense;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.lamisplus.modules.pharmacy.domain.mapper.DrugDispenseMapper;
import org.lamisplus.modules.pharmacy.repository.DrugDispenseRepository;
import org.lamisplus.modules.pharmacy.util.JsonNodeTransformer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
@RequiredArgsConstructor
public class DrugDispenseService {
    private static final int ARCHIVED = 1;
    private static final int UN_ARCHIVED = 0;
    private final DrugDispenseRepository drugDispenseRepository;
    private final DrugDispenseMapper drugDispenseMapper;
    private final PersonService personService;
    //private final JsonNodeTransformer jsonNodeTransformer;
    private final PatientDetailService patientDetailService;


    public PharmacyDispenseListMetaDataDTO getAllDrugDispense(String searchParam, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Page<DrugDispense> orders = drugDispenseRepository.findAllByArchived(UN_ARCHIVED, paging);

        if (orders.hasContent()) {
            PageDTO pageDTO = this.generatePagination(orders);
            PharmacyDispenseListMetaDataDTO dispenseListMetaDataDTO = new PharmacyDispenseListMetaDataDTO();
            dispenseListMetaDataDTO.setTotalRecords(pageDTO.getTotalRecords());
            dispenseListMetaDataDTO.setPageSize(pageDTO.getPageSize());
            dispenseListMetaDataDTO.setTotalPages(pageDTO.getTotalPages());
            dispenseListMetaDataDTO.setCurrentPage(pageDTO.getPageNumber());
            dispenseListMetaDataDTO.setRecords(drugDispenseMapper.toDrugDispenseDTOList(new ArrayList<>(orders.getContent()).stream()
                    .sorted(Comparator.comparingLong(DrugDispense::getId).reversed()).collect(Collectors.toList())));

            return dispenseListMetaDataDTO;
        }

        return new PharmacyDispenseListMetaDataDTO();
    }

    public PageDTO generatePagination(Page page) {
        long totalRecords = page.getTotalElements();
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        int totalPages = page.getTotalPages();
        return PageDTO.builder().totalRecords(totalRecords)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(totalPages).build();
    }

    public List<DrugDispense> save(DrugDispenseDTOS drugDispenseDTOS) {
        //Optional<Drug> DrugOptional = drugDispenseRepository.findByDrugNameAndPatientIdAndDrugOrderId(drugOrder.getName(), UN_ARCHIVED);
        //if (DrugOptional.isPresent()) throw new RecordExistException(Drug.class, "Name", drugOrder.getName());
        List<DrugDispense> drugDispenseList = new ArrayList<>();
        drugDispenseDTOS.getDrugDispenses().forEach(drugDispense -> {
            if(!personService.isPersonExist(drugDispense.getPatientId())){
                throw new EntityNotFoundException(Person.class, "patientId", "" + drugDispense.getPatientId());
            }
            if(drugDispense.getDrugOrderId() == null){
                throw new EntityNotFoundException(DrugOrder.class, "DrugOrderId", "DrugOrderId");
            } else {
                drugDispenseRepository
                        .findByDrugOrderId(drugDispense.getDrugOrderId())
                        .ifPresent( drugDispense1 -> {
                throw new RecordExistException(DrugDispense.class, "Drug " + drugDispense.getDrugName()+ " has already been dispensed with order", " "+drugDispense.getDrugOrderId());
            });
            }
            drugDispenseList.add(drugDispense);
        });
        return drugDispenseRepository.saveAll(drugDispenseList);
    }

    public DrugDispenseDTO getDrugDispense(Long id) {
        DrugDispense drugDispense = drugDispenseRepository
                .findByIdAndArchived(id, UN_ARCHIVED)
                .orElseThrow(() -> new EntityNotFoundException(DrugDispense.class, "Id", id + ""));
        return drugDispenseMapper.toDrugDispenseDTO(drugDispense);
    }

    public DrugDispense update(Long id, DrugDispenseDTO drugDispenseDTO) {
        drugDispenseRepository.findByIdAndArchived(id, UN_ARCHIVED).orElseThrow(() -> new EntityNotFoundException(DrugDispense.class, "Id", id + ""));

        drugDispenseDTO.setId(id);
        DrugDispense drugDispense = drugDispenseMapper.toDrugDispense(drugDispenseDTO);
        return drugDispenseRepository.save(drugDispense);
    }

    public Integer delete(Long id) {
        DrugDispense drugDispense = drugDispenseRepository
                .findByIdAndArchived(id, UN_ARCHIVED)
                .orElseThrow(() -> new EntityNotFoundException(DrugDispense.class, "Id", id + ""));

        drugDispense.setArchived(ARCHIVED);
        //TODO: Also archive the drug dispense
        drugDispenseRepository.save(drugDispense);
        return drugDispense.getArchived();
    }

    public List<PatientDrugDispenseDTO> getAllDrugDispenseForAPatient(Long patientId) {
        Map<Long, List<DrugDispense>> drugDispenseMap = drugDispenseRepository
                .findAllByPatientIdGroupByIdOrderById(patientId)
                .stream()
                .sorted(Comparator.comparingLong(DrugDispense::getDrugOrderId).reversed())
                .collect(groupingBy(DrugDispense::getDrugOrderId));

        return getPatientDrugOrders(drugDispenseMap, patientId);
    }

    public List<PatientDrugDispenseDTO> getAllDrugDispenseForAPatientByDrugOrderId(Long patientId, Long drugOrderId) {
        Map<Long, List<DrugDispense>> drugDispenseMap = drugDispenseRepository
                .findAllDrugDispenseForAPatientByDrugOrderId(patientId, drugOrderId)
                .stream()
                .sorted(Comparator.comparingLong(DrugDispense::getDrugOrderId).reversed())
                .collect(groupingBy(DrugDispense::getDrugOrderId));

        return getPatientDrugOrders(drugDispenseMap, patientId);
    }

    private List<PatientDrugDispenseDTO> getPatientDrugOrders(Map<Long, List<DrugDispense>> drugDispenseMap, Long patientId){
        List<PatientDrugDispenseDTO> patientDrugDispenseDTOS = new ArrayList<>();
        if(patientId != null){
            //PatientDrugDispenseDTO patientDrugDispenseDTO = (PatientDrugDispenseDTO) patientDetailService.setDTO(patientId);
            patientDrugDispenseDTOS.add((PatientDrugDispenseDTO) patientDetailService.setDTO(patientId));
        }
        drugDispenseMap.forEach((k,v) ->{
            final PatientDrugDispenseDTO[] patientDrugDispenseDTO = {new PatientDrugDispenseDTO()};
            patientDrugDispenseDTO[0].setDrugDispenses(v.stream()
                    .map(drugDispense -> {
                        if (patientDrugDispenseDTO[0].getPatientId() == null) {
                            patientDrugDispenseDTO[0] = (PatientDrugDispenseDTO) patientDetailService.setDTO(drugDispense.getPatientId());
                        }
                        return drugDispense;
                    })
                    .sorted(Comparator.comparingLong(DrugDispense::getId).reversed())
                    .collect(Collectors.toList()));
            patientDrugDispenseDTOS.add(patientDrugDispenseDTO[0]);
        });


        return patientDrugDispenseDTOS;
    }

    public List<PatientDrugDispenseDTO> getAllDrugDispenseByDrugOrderId(Long drugOrderId) {
        Map<Long, List<DrugDispense>> drugDispenseMap = drugDispenseRepository
                .findAllDrugDispenseByDrugOrderId(drugOrderId)
                .stream()
                .sorted(Comparator.comparingLong(DrugDispense::getDrugOrderId).reversed())
                .collect(groupingBy(DrugDispense::getDrugOrderId));
        return getPatientDrugOrders(drugDispenseMap, null);
    }

    /*private PatientDrugDispenseDTO setDTO(Long patientId){
        PatientDrugDispenseDTO patientDrugDispenseDTO = new PatientDrugDispenseDTO();
        PersonResponseDto personResponseDTO = personService.getPersonById(patientId);
        try {
            patientDrugDispenseDTO.setPatientId(patientId);
            patientDrugDispenseDTO.setPatientDob(personResponseDTO.getDateOfBirth());
            patientDrugDispenseDTO.setPatientLastName(personResponseDTO.getSurname());
            patientDrugDispenseDTO.setPatientFirstName(personResponseDTO.getFirstName());
            patientDrugDispenseDTO
                    .setPatientHospitalNumber(jsonNodeTransformer
                            .getNodeValue(personResponseDTO
                                    .getIdentifier(), "identifier", "value", true));

            patientDrugDispenseDTO
                    .setPatientAddress(jsonNodeTransformer
                            .getNodeValue(personResponseDTO
                                    .getAddress(), "address", "city", true));

            patientDrugDispenseDTO
                    .setPatientGender(jsonNodeTransformer
                            .getNodeValue(personResponseDTO
                                    .getGender(), null, "display", false));

            patientDrugDispenseDTO
                    .setPatientPhoneNumber(jsonNodeTransformer
                            .getNodeValue(personResponseDTO
                                            .getContactPoint(),
                    "contactPoint", "value", true));
        }catch (Exception e){
            e.printStackTrace();
        }

        return patientDrugDispenseDTO;

    }*/
}
