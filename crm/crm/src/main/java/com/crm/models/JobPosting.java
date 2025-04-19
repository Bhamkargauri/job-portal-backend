package com.crm.models;

import java.time.LocalDate;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jobpostings")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class JobPosting {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String companyName;
	private String email;
	private String jobTitle;
	private int noOfPosition;
	private int positionPerDepartment;
	private String contactDetails;
	private String experience;
	private String hours;
	private String shift;
	private String location;
	private long minSalary;
	private long maxSalary;
	private LocalDate deadline;
	private String jobType;
	@Lob
	@JsonIgnore
	private byte[] doc;
    private String docOriginalFileName;
    private String docContentType; // Stores MIME type (e.g., application/pdf, application/vnd.openxmlformats-officedocument.wordprocessingml.document)
   
	private Integer status;
	@ManyToOne
	private User created;
	@ManyToOne
	private User modified;
	private LocalDate createdDate;
	private LocalDate modifiedDate;
}
