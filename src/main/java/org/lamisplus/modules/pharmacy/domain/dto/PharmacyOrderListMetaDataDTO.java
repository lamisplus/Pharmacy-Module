package org.lamisplus.modules.pharmacy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyOrderListMetaDataDTO {
    private long totalRecords;
    private int pageSize;
    private int totalPages;
    private int currentPage;
    private List<PatientDrugOrderDTO> records;
}