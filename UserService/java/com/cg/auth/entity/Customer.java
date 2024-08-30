package com.cg.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Customer")
@Table(name = "customer_table", schema = "auth_db")
public class Customer implements UserDetails {
    @Id
    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private  String lastname;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String  email;

    @Column(name = "role")
    private  String role;

    @Column(name = "mobile_number")
    private Long mobileNum;

    @Column(name = "registration_ReqStatus")
    private String registrationReqStatus;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private  String updatedBy;


    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Account account;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PassWordChangeRequest> passWordRequest;

    // @PastOrPresent(message = "Created date must be in the past or present")
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    //@NotNull
    // @PastOrPresent(message = "Updated date must be in the past or present")
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    //@Column(name = "register_status")
   // private String registerStatus;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return false;
    }
}
