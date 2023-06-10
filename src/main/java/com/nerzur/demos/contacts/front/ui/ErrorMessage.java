package com.nerzur.demos.contacts.front.ui;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage implements Serializable {
    private Timestamp timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
