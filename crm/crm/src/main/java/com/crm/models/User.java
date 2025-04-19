package com.crm.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z\\s]+$",
            message = "Name can only contain letters and spaces"
    )
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email address")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9_.-]*@[a-zA-Z]+\\.[a-zA-Z]{2,}$",
            message = "Username must be a valid email address and should not start with a number or contain invalid domains"
    )
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Location is required")
    private String location;

    
    @NotBlank(message = "Type is required")
    @Pattern(
            regexp = "COMPANY|FRANCHISOR|SUPER_ADMIN",
            message = "Type is not correct. Allowed values are: FRANCHISOR, COMPANY, SUPER_ADMIN"
    )
    @Column(nullable = false)
    private String type;

    private Integer status;

    @ManyToOne
    private User created;

    private LocalDate createdDate;

    @ManyToOne
    private User modified;

    private LocalDate modifiedDate;
}
