package com.nerzur.demos.contacts.front.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactsByDates {

    Date startDate;
    Date endDate;
}
