package com.cg.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordChangeRequestDto {

    private Long id;
    private String password;
    @JsonIgnore
    private CustomerDto customerInfo;
    private String panNumber;
    private String status;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
    public String getStatus(){
        return status!=null && !status.isBlank()?status.toUpperCase():status;
    }

    public String getPanNumber(){
        return panNumber!=null && !panNumber.isBlank()?panNumber.toUpperCase():panNumber;
    }

}
