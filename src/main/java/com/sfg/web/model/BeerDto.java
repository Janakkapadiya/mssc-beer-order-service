package com.sfg.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
    private UUID id = null;
    private Integer version = null;
    @Null
    @JsonFormat(pattern = "dd-MM-yyyy'T'HH-mm-ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate = null;
    @Null
    @JsonFormat(pattern = "dd-MM-yyyy'T'HH-mm-ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime lastModifiedDate = null;
    private String beerName;
    private String beerStyle;
    private String upc;
    private Integer quantityOnHand;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
}