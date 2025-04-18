package com.dinhngoctranduy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resume_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skills;
    private String education;
    private String address;
    private int yearsOfExperience;
    private String certificates;
    private Integer score;

    @OneToOne
    @JoinColumn(name = "resume_id", unique = true)
    private Resume resume;
}