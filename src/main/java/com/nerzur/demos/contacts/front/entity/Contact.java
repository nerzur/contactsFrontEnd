package com.nerzur.demos.contacts.front.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "tbl_contact")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contact implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 256)
    String firstName;

    @NotNull
    @Column(length = 256)
    String secondName;

    @NotNull
    @Temporal(TemporalType.DATE)
    Date birthDate;

    String personalPhoto;
}
