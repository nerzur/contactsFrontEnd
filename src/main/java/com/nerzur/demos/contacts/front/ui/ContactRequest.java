package com.nerzur.demos.contacts.front.ui;

import com.nerzur.demos.contacts.front.entity.Address;
import com.nerzur.demos.contacts.front.entity.Contact;
import com.nerzur.demos.contacts.front.entity.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactRequest {

    Contact contact;
    List<Address> addresses;
    List<PhoneNumber> phoneNumbers;
}
