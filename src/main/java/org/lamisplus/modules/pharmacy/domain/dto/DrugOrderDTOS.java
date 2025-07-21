package org.lamisplus.modules.pharmacy.domain.dto;

import lombok.Data;
import javax.validation.Valid;
import java.util.List;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class DrugOrderDTOS {
    @Valid
    @NotEmpty(message = "Drug orders cannot be empty")
    private List<DrugOrderDTO> drugOrders;
}
