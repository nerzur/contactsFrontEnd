package com.nerzur.demos.contacts.front.utils;

import com.google.gson.Gson;
import com.nerzur.demos.contacts.front.ui.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorSeverity {
    String summary;
    String content;

    public static ErrorSeverity ConvertToErrorMessage(String error) {
        try {
            ErrorMessage errorMessage = new Gson().fromJson(error, ErrorMessage.class);
            return ErrorSeverity.builder()
                    .summary(errorMessage.getStatus() + " error")
                    .content(errorMessage.getError())
                    .build();
        }
        catch (Exception ex){
            return ErrorSeverity.builder()
                    .summary("410")
                    .content("An error has been occurred")
                    .build();
        }
    }
}
