package com.springrestmvcproject.spring6restmvc.model;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class CustomerDTO {

    private UUID id;
    private String version;

    @NotEmpty
    @NotNull
    private String customerName;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;




}
