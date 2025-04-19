package com.crm.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "candidates")
public class Candidate {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z\s]+$",
            message = "Name can only contain letters and spaces"
    )
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Column(nullable = false)
    private String name;
    @NotBlank(message = "email can not be null")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Qualification is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z\s,.()-]+$",
            message = "Qualification can only contain letters, spaces, commas, periods, parentheses, and hyphens"
    )
    @Size(max = 150, message = "Qualification cannot exceed 150 characters")
    @Column(nullable = false)
    private String qualification;
    
    @NotBlank(message = "Job post is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z\s]+$",
            message = "Job post can only contain letters and spaces"
    )
    @Size(max = 100, message = "Job post cannot exceed 100 characters")
    @Column(nullable = false)
    private String jobPost;
    
    @NotBlank(message = "Experience is required")
    @Pattern(
        regexp = "^[a-zA-Z0-9 ]+$",
        message = "Experience can only contain letters, numbers, and spaces"
    )
    @Column(nullable = false)
    private String experience;
    
    @NotBlank(message = "Address is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z0-9\s,.-]+$",
            message = "Address can only contain letters, numbers, spaces, commas, periods, and hyphens"
    )
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(nullable = false)
    private String address;
    
    @NotBlank(message = "Phone number is mandatory")
    @Pattern(
            regexp = "^\\d{10}$",
            message = "Phone number must be exactly 10 digits"
    )
    @Column(nullable = false, unique = true)
    private String phoneNo;
    
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer placementStatus = 0; // 0=default, 1=Screening, 2=Mock Inprogress, 3=Mock Pass, 4=Interview Schedule, 5=Interview Pass, 6=Interview Failed
    
    
    @Lob
    @JsonIgnore
    private byte[] resume;
    private String resumeOriginalFileName;
    private String resumeContentType; // Stores MIME type (e.g., application/pdf, application/vnd.openxmlformats-officedocument.wordprocessingml.document)
    private Integer status;
    @ManyToOne
//    @ManyToOne
    private User created;
    @ManyToOne
    private User modified;
    private LocalDate createdDate;
    private LocalDate modifiedDate;
//	public Candidate(String name, String email, String qualification,
//			String jobPost, String experience, String address, String phoneNo) {
//		super();
//		this.name = name;
//		this.email = email;
//		this.qualification = qualification;
//		this.jobPost = jobPost;
//		this.experience = experience;
//		this.address = address;
//		this.phoneNo = phoneNo;
//	}
    
}
