package com.nerzur.demos.contacts.front.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactAdrressesPk implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long contactId;

    private Long addressId;
}
