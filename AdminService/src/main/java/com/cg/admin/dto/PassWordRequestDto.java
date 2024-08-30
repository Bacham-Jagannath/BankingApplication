package com.cg.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PassWordRequestDto {

    private Long id;
    private String password;
    @JsonIgnore
    private CustomerDto customerInfo;

}
