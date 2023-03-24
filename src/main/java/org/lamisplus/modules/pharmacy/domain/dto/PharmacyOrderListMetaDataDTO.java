package org.lamisplus.modules.pharmacy.domain.dto;

import lombok.Data;

import java.util.List;
@Data
public class PharmacyOrderListMetaDataDTO {
    private  long totalRecords;
    private Integer totalPages;
    private Integer pageSize;
    private Integer currentPage;
    private List<PatientDrugOrderDTO> records;
}
