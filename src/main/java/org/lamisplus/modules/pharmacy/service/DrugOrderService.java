package org.lamisplus.modules.pharmacy.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.base.domain.dto.PageDTO;
import org.lamisplus.modules.patient.domain.dto.PersonResponseDto;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.service.PersonService;
import org.lamisplus.modules.pharmacy.domain.dto.*;
import org.lamisplus.modules.pharmacy.domain.entity.DrugDispense;
import org.lamisplus.modules.pharmacy.domain.entity.DrugOrder;
import org.lamisplus.modules.pharmacy.domain.mapper.DrugOrderMapper;
import org.lamisplus.modules.pharmacy.repository.DrugDispenseRepository;
import org.lamisplus.modules.pharmacy.repository.DrugOrderRepository;
import org.lamisplus.modules.pharmacy.util.JsonNodeTransformer;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
@RequiredArgsConstructor
public class DrugOrderService {
    private static final int ARCHIVED = 1;
    private static final int UN_ARCHIVED = 0;
    private final DrugOrderRepository drugOrderRepository;
    private final DrugOrderMapper drugOrderMapper;
    private final PersonService personService;
    private final DrugDispenseRepository drugDispenseRepository;
    private final JsonNodeTransformer jsonNodeTransformer;

    public List<DrugOrderDTO> getAllDrugOrders(Long patientId) {
        List<DrugOrderDTO> drugOrdersDTOS;
        if(patientId == 0) {
            drugOrdersDTOS = drugOrderRepository.findAllByArchived(UN_ARCHIVED)
                    .stream()
                    .map(this::transformDrugOrder)
                    .collect(Collectors.toList());
        } else {
            if(!personService.isPersonExist(patientId)){
                throw new org.lamisplus.modules.base.controller.apierror
                        .EntityNotFoundException(DrugOrder.class, "patientId", "" + patientId);
            }
            drugOrdersDTOS = drugOrderRepository.findAllByPatientIdAndArchived(patientId, UN_ARCHIVED)
                    .stream()
                    .map(this::transformDrugOrder)
                    .collect(Collectors.toList());
        }
        return drugOrdersDTOS;

    }

    private  DrugOrderDTO transformDrugOrder(DrugOrder drugOrder){

        if (drugOrder.getDrugDispensesById() == null) {
            drugOrder.setStatus(0);
        } else {
            drugOrder.setDateTimeDispensed(drugOrder.getDrugDispensesById().getDateTimeDispensed());
            drugOrder.setStatus(1);
        }
        return drugOrderMapper.toDrugOrderDTO(drugOrder);

    }

    public List<DrugOrder> save(DrugOrderDTOS drugOrderDTOS) {
        List<DrugOrderDTO> drugOrderDTOList = new ArrayList<>();
        String drugPrescribedGroupId = UUID.randomUUID().toString();

        drugOrderDTOS.getDrugOrders().forEach(drugOrderDTO -> {
            if (drugOrderDTO.getPatientId() == null) throw new EntityNotFoundException(DrugOrder.class, "id", "id");
            if(!personService.isPersonExist(drugOrderDTO.getPatientId())) {
                throw new EntityNotFoundException(Person.class, "patientId", " " + drugOrderDTO.getPatientId());
            }
            drugOrderDTO.setId(null);
            drugOrderDTO.setPrescriptionGroupId(drugPrescribedGroupId);
            drugOrderDTO.setUuid(UUID.randomUUID().toString());
            drugOrderDTOList.add(drugOrderDTO);
        });
        return drugOrderRepository.saveAll(drugOrderMapper.toDrugOrderList(drugOrderDTOList));
    }

    public DrugOrderDTO getDrugOrder(Long id) {
        DrugOrder drugOrder = drugOrderRepository
                .findByIdAndArchived(id, UN_ARCHIVED)
                .orElseThrow(() -> new EntityNotFoundException(DrugOrder.class, "Id", id + ""));
        return drugOrderMapper.toDrugOrderDTO(drugOrder);
    }

    public DrugOrder update(Long id, DrugOrderDTO drugOrderDTO) {
        DrugOrder current_order = drugOrderRepository.findByIdAndArchived(id, UN_ARCHIVED)
                .orElseThrow(() -> new EntityNotFoundException(DrugOrder.class, "Id", id + ""));
        drugOrderDTO.setId(id);
        DrugOrder drugOrder = drugOrderMapper.toDrugOrder(drugOrderDTO);
        drugOrder.setPrescriptionGroupId(current_order.getPrescriptionGroupId());
        drugOrder.setPatientId(current_order.getPatientId());
        return drugOrderRepository.save(drugOrder);
    }

    public Integer delete(Long id) {
        DrugOrder drugOrder = drugOrderRepository
                .findByIdAndArchived(id, UN_ARCHIVED)
                .orElseThrow(() -> new EntityNotFoundException(DrugOrder.class, "Id", id + ""));

        drugOrder.setArchived(ARCHIVED);
        DrugDispense drugDispense = drugOrder.getDrugDispensesById();
        drugDispense.setArchived(ARCHIVED);

        drugOrderRepository.save(drugOrder);
        drugDispenseRepository.save(drugDispense);
        return drugOrder.getArchived();
    }

    public PharmacyOrderListMetaDataDTO getAllDrugOrdersForPatients(String searchParam, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Page<DrugOrder> orders = drugOrderRepository.findAllDrugOrderGroupByPrescriptionGroupIdOrderById(paging);

        if (orders.hasContent()) {
            PageDTO pageDTO = this.generatePagination(orders);
            PharmacyOrderListMetaDataDTO orderListMetaDataDto = new PharmacyOrderListMetaDataDTO();
            orderListMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
            orderListMetaDataDto.setPageSize(pageDTO.getPageSize());
            orderListMetaDataDto.setTotalPages(pageDTO.getTotalPages());
            orderListMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
            orderListMetaDataDto.setRecords(getPatientDrugOrders(new ArrayList<>(orders.getContent()).stream()
                    .sorted(Comparator.comparingLong(DrugOrder::getId).reversed())
                    .collect(groupingBy(DrugOrder::getPrescriptionGroupId))));
            return orderListMetaDataDto;
        }

        return new PharmacyOrderListMetaDataDTO();
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

    public List<PatientDrugOrderDTO> getAllDrugOrdersForAPatient(Long patientId) {
        Map<String, List<DrugOrder>> orders = drugOrderRepository
                .findAllByPatientIdGroupByPrescriptionGroupIdOrderById(patientId)
                .stream()
                .sorted(Comparator.comparingLong(DrugOrder::getId).reversed())
                .collect(groupingBy(DrugOrder::getPrescriptionGroupId));

        return getPatientDrugOrders(orders);
    }

    private List<PatientDrugOrderDTO> getPatientDrugOrders(Map<String, List<DrugOrder>> drugOrderMap) {
        List<PatientDrugOrderDTO> patientDrugOrderDTOS = new ArrayList<>();
        drugOrderMap.forEach((k, v) -> {
            PatientDrugOrderDTO patientDrugOrderDTO = new PatientDrugOrderDTO();

            patientDrugOrderDTO.setDrugOrders(v.stream()
                    .map(drugOrder -> {
                        try {
                            if (patientDrugOrderDTO.getPatientHospitalNumber() == null) {
                                PersonResponseDto personResponseDTO = personService.getPersonById(drugOrder.getPatientId());
                                patientDrugOrderDTO.setPatientDob(personResponseDTO.getDateOfBirth());
                                patientDrugOrderDTO.setPatientId(drugOrder.getPatientId());
                                patientDrugOrderDTO.setPatientFirstName(personResponseDTO.getFirstName());
                                patientDrugOrderDTO.setPatientLastName(personResponseDTO.getSurname());
                                patientDrugOrderDTO.setPatientHospitalNumber
                                        (jsonNodeTransformer.getNodeValue(personResponseDTO.getIdentifier(), "identifier", "value", true));

                                patientDrugOrderDTO.setPatientAddress
                                        (jsonNodeTransformer.getNodeValue(personResponseDTO.getAddress(), "address", "city", true));

                                patientDrugOrderDTO.setPatientGender
                                        (jsonNodeTransformer.getNodeValue(personResponseDTO.getGender(), null, "display", false));

                                patientDrugOrderDTO.setPatientPhoneNumber(jsonNodeTransformer.getNodeValue(personResponseDTO.getContactPoint(),
                                        "contactPoint", "value", true));
                            }
                            patientDrugOrderDTO.setPatientId(drugOrder.getPatientId());
                            if (drugOrder.getDrugDispensesById() == null) {
                                drugOrder.setStatus(0);
                            } else {
                                drugOrder.setDateTimeDispensed(drugOrder.getDrugDispensesById().getDateTimeDispensed());
                                drugOrder.setStatus(1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return drugOrder;
                    }).sorted(Comparator.comparingLong(DrugOrder::getId).reversed())
                    .collect(Collectors.toList()));
            patientDrugOrderDTOS.add(patientDrugOrderDTO);
        });
        return patientDrugOrderDTOS;
    }

    public List<DrugOrderDTO> getAllDrugOrdersByVisitId(Long id) {
        return drugOrderRepository.findAllByArchivedAndVisitId(UN_ARCHIVED, id)
                .stream()
                .map(this::transformDrugOrder)
                .collect(Collectors.toList());
    }
}
