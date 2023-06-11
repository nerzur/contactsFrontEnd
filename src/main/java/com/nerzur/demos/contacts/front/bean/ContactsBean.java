package com.nerzur.demos.contacts.front.bean;

import com.nerzur.demos.contacts.front.entity.Address;
import com.nerzur.demos.contacts.front.entity.PhoneNumber;
import com.nerzur.demos.contacts.front.rest.ContactRest;
import com.nerzur.demos.contacts.front.entity.Contact;
import com.nerzur.demos.contacts.front.ui.ContactRequest;
import com.nerzur.demos.contacts.front.utils.ErrorSeverity;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.CroppedImage;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Data
public class ContactsBean implements Serializable {

    private List<Contact> contactList;
    private ContactRest contactRest = new ContactRest();
    private ContactRequest contactSelected;

    private String firstName;
    private String secondName;
    private Date dateBirth;
    private List<String> addressList;
    private List<String> phoneNumberList;

    private UploadedFile originalImageFile;
    private String base64File;

    public void init() {
        cleanGlobalVariables();
        try {
            contactList = contactRest.findAllContacts();
        } catch (Exception e) {
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
            PrimeFaces.current().ajax().update("form:messages");
        }
    }

    private void cleanGlobalVariables() {
        contactList = new ArrayList<>();
        contactSelected = ContactRequest.builder().build();
    }

    private void cleanVariables() {
        firstName = "";
        secondName = "";
        dateBirth = null;
        phoneNumberList.clear();
        addressList.clear();
        base64File = "";
        originalImageFile = null;

    }

    public void updateSelectedContact(Contact contact) {
        try {
            contactSelected = contactRest.findAllContactDataByContact(contact.getId());
        } catch (Exception e) {
            e.printStackTrace();
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
            PrimeFaces.current().ajax().update("form:messages");
        }
    }

    public void deleteContact() {
        try {
            contactRest.deleteFullContact(contactSelected);
            init();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation Successfully", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-contact");
        } catch (Exception e) {
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-contact");
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        this.originalImageFile = null;
        UploadedFile file = event.getFile();
        if (file != null && file.getContent() != null && file.getContent().length > 0 && file.getFileName() != null) {
            this.originalImageFile = file;
            byte[] encodedBytes = Base64.encodeBase64(originalImageFile.getContent());
            base64File = new String(encodedBytes);
            FacesMessage msg = new FacesMessage("Successful", this.originalImageFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void saveContact() {
        if (!base64File.contains("data:image/png;base64,"))
            base64File = "data:image/png;base64," + base64File;
        Contact contact = Contact.builder()
                .firstName(firstName)
                .secondName(secondName)
                .birthDate(new java.sql.Date(dateBirth.getTime()))
                .personalPhoto(base64File)
                .build();
        List<Address> addressesList = new ArrayList<>();
        List<PhoneNumber> phoneNumbersList = new ArrayList<>();

        addressList.forEach(address -> {
            addressesList.add(Address.builder().address(address).build());
        });

        phoneNumberList.forEach(phoneNumber -> {
            phoneNumbersList.add(PhoneNumber.builder().contact(contact).phoneNumber(phoneNumber).build());
        });

        ContactRequest contactRequest = ContactRequest.builder()
                .contact(contact)
                .phoneNumbers(phoneNumbersList)
                .addresses(addressesList)
                .build();

        boolean flag = false;
        ErrorSeverity errorSeverity = null;
        try {
            flag = contactRest.createFullContact(contactRequest);
            if (!flag) {
                errorSeverity = ErrorSeverity.builder()
                        .summary("400")
                        .content("An Error has Been Occurred")
                        .build();
            }
        } catch (Exception e) {
            errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
        }

        if (flag) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation Successfully", ""));
            PrimeFaces.current().executeScript("PF('addContactDialog').hide()");
            PrimeFaces.current().ajax().update("form:messages", "form:dt-contact");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorSeverity.getSummary(), errorSeverity.getContent()));
        }
        init();

    }
}
