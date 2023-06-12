package com.nerzur.demos.contacts.front.bean;

import com.nerzur.demos.contacts.front.entity.Address;
import com.nerzur.demos.contacts.front.entity.PhoneNumber;
import com.nerzur.demos.contacts.front.rest.ContactRest;
import com.nerzur.demos.contacts.front.entity.Contact;
import com.nerzur.demos.contacts.front.ui.ContactRequest;
import com.nerzur.demos.contacts.front.ui.SearchContactsByDates;
import com.nerzur.demos.contacts.front.ui.SearchContactsByFirstNameAndSecondName;
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

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Data
public class ContactsBean implements Serializable {

    private List<Contact> contactList = new ArrayList<>();
    private ContactRest contactRest = new ContactRest();
    private static ContactRequest contactSelected;

    private static String firstName;
    private static String secondName;
    private static Date dateBirth;
    private static List<String> addressList = new ArrayList<>();
    private static List<String> phoneNumberList = new ArrayList<>();
    private String name;

    private Date startDate;
    private Date endDate;

    private UploadedFile originalImageFile;
    private static String base64File;

    public void init() {
        cleanGlobalVariables();
        name = "";
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

    public void cleanVariables() {
        firstName = "";
        secondName = "";
        dateBirth = new Date();
        phoneNumberList = new ArrayList<>();
        addressList = new ArrayList<>();
        base64File = "";
        originalImageFile = null;
        startDate = null;
        endDate = null;
    }

    public void updateSelectedContact(Contact contact) {
        try {
            contactSelected = contactRest.findAllContactDataByContact(contact.getId());
            cleanVariables();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
            PrimeFaces.current().ajax().update("form:messages");
        }
    }

    public void updateSelectedContactToEdit(Contact contact) {
        updateSelectedContact(contact);
        firstName = contact.getFirstName();
        secondName = contact.getSecondName();
        dateBirth = contact.getBirthDate();
        base64File = contact.getPersonalPhoto();

        addressList = new ArrayList<>();
        phoneNumberList = new ArrayList<>();

        contactSelected.getAddresses().forEach(address -> {
            addressList.add(address.getAddress());
        });
        contactSelected.getPhoneNumbers().forEach(phoneNumber -> {
            phoneNumberList.add(phoneNumber.getPhoneNumber());
        });
        PrimeFaces.current().ajax().update("dialogs, form1:edit-contact-content");
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
        } else base64File = "";
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
        cleanVariables();

    }

    public void editContact() {
        if (!base64File.contains("data:image/png;base64,"))
            base64File = "data:image/png;base64," + base64File;
        Contact contact = contactSelected.getContact();
        contact.setFirstName(firstName);
        contact.setSecondName(secondName);
        contact.setBirthDate(new java.sql.Date(dateBirth.getTime()));
        contact.setPersonalPhoto(base64File);

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
            flag = contactRest.editFullContact(contactRequest);
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
            PrimeFaces.current().executeScript("PF('editContactDialog').hide()");
            PrimeFaces.current().ajax().update("form:messages", "form:dt-contact");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorSeverity.getSummary(), errorSeverity.getContent()));
        }
        init();
        cleanVariables();
    }

    public void search() {
        cleanGlobalVariables();
        try {
            contactList = contactRest.searchContactsByFirstNameAndSecondName(SearchContactsByFirstNameAndSecondName.builder()
                    .firstName(name)
                    .secondName(name)
                    .build());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", contactList.size() + " items have been found matching that search criteria"));
            PrimeFaces.current().ajax().update("form:messages, form:dt-contact");
        } catch (Exception e) {
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
            PrimeFaces.current().ajax().update("form:messages, form:dt-contact");
        }
        name = "";
    }

    public void searchByDates() {
        if(endDate.before(startDate)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR ", "The firstDate and / or endDate is invalid."));
            PrimeFaces.current().ajax().update("form:messages");
            return;
        }
        cleanGlobalVariables();
        try {
            contactList = contactRest.searchContactsByDates(SearchContactsByDates.builder()
                    .startDate(new java.sql.Date(startDate.getTime()))
                    .endDate(new java.sql.Date(endDate.getTime()))
                    .build());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", contactList.size() + " items have been found matching that search criteria"));
            PrimeFaces.current().ajax().update("form:messages, form:dt-contact");
            PrimeFaces.current().executeScript("PF('searchContactDialog').hide()");

        } catch (Exception e) {
            ErrorSeverity errorSeverity = ErrorSeverity.ConvertToErrorMessage(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + errorSeverity.getSummary(), errorSeverity.getContent()));
            PrimeFaces.current().ajax().update("form:messages, form:dt-contact");
        }
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        ContactsBean.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        ContactsBean.secondName = secondName;
    }

    public Date getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(Date dateBirth) {
        ContactsBean.dateBirth = dateBirth;
    }

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        ContactsBean.addressList = addressList;
    }

    public List<String> getPhoneNumberList() {
        return phoneNumberList;
    }

    public void setPhoneNumberList(List<String> phoneNumberList) {
        ContactsBean.phoneNumberList = phoneNumberList;
    }

    public String getBase64File() {
        return base64File;
    }

    public void setBase64File(String base64File) {
        ContactsBean.base64File = base64File;
    }

    public ContactRequest getContactSelected() {
        return contactSelected;
    }

    public void setContactSelected(ContactRequest contactSelected) {
        ContactsBean.contactSelected = contactSelected;
    }
}
