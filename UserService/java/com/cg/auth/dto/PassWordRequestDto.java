package com.cg.auth.dto;

import com.cg.auth.entity.PassWordChangeRequest;
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

    public  PassWordRequestDto(PassWordChangeRequest passWordRequest){
        this.id=passWordRequest.getId();
        this.password = null;
        this.customerInfo = new CustomerDto(passWordRequest.getCustomer().getPanNumber());
    }

}
