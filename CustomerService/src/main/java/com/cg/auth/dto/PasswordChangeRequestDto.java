package com.cg.auth.dto;

import com.cg.auth.entity.PasswordChangeRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

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

    public PasswordChangeRequestDto(PasswordChangeRequest passWordRequest){
        this.id=passWordRequest.getId();
        this.password = null;
        this.status=passWordRequest.getStatus();
        this.panNumber=passWordRequest.getPanNumber();
        this.createdOn=passWordRequest.getCreatedOn();
        this.updatedBy=passWordRequest.getUpdatedBy();
        this.createdBy=passWordRequest.getCreatedBy();
        this.updatedOn=passWordRequest.getUpdatedOn();
    }
    public String getStatus(){
        return status!=null && !status.isBlank()?status.toUpperCase():status;
    }

    public String getPanNumber(){
        return panNumber!=null && !panNumber.isBlank()?panNumber.toUpperCase():panNumber;
    }

}
