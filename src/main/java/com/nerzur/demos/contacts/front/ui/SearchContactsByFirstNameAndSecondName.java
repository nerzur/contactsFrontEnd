package com.nerzur.demos.contacts.front.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactsByFirstNameAndSecondName {

    String firstName;
    String secondName;
}
