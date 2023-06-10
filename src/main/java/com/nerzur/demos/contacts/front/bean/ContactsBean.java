package com.nerzur.demos.contacts.front.bean;

import com.google.gson.Gson;
import com.nerzur.demos.contacts.front.rest.ContactRest;
import com.nerzur.demos.contacts.front.entity.Contact;
import com.nerzur.demos.contacts.front.ui.ContactRequest;
import com.nerzur.demos.contacts.front.ui.ErrorMessage;
import com.nerzur.demos.contacts.front.utils.ErrorSeverity;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
@Data
public class ContactsBean implements Serializable {

    private List<Contact> contactList;
    private ContactRest contactRest = new ContactRest();
    private ContactRequest contactSelected;

    public void init() {
        try {
            contactList = contactRest.findAllContacts();
        } catch (Exception e) {
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
        }
    }

    public void updateSelectedContact(Contact contact) {
        try {
            contactSelected = contactRest.findAllContactDataByContact(contact.getId());
        } catch (Exception e) {
            e.printStackTrace();
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
        }
    }
}
