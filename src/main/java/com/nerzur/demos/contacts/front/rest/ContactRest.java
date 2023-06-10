package com.nerzur.demos.contacts.front.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.nerzur.demos.contacts.front.entity.Contact;
import com.nerzur.demos.contacts.front.ui.ContactRequest;
import com.nerzur.demos.contacts.front.utils.JSONUtils;

import java.net.http.HttpRequest;
import java.util.List;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ContactRest {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private final String serviceURL = "http://localhost:8091/contact";

    private final Gson gson = new Gson();

    public List<Contact> findAllContacts() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<Contact> contactList = null;
        HttpResponse<String> httpResponse = response.join();
        if (httpResponse.statusCode() == 200) {
            contactList = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<Contact>>() {
                    });
        } else {

            throw new Exception(httpResponse.body());
        }
        return contactList;
    }

    public ContactRequest findAllContactDataByContact(Long contactId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL + "/findAllDataByContact/" + contactId)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        ContactRequest contactRestDb = null;
        HttpResponse<String> httpResponse = response.join();
        if (httpResponse.statusCode() == 200) {
            contactRestDb = JSONUtils.covertFromJsonToObject(response.get().body(), ContactRequest.class);
        } else {
            throw new Exception(httpResponse.body());
        }
        return contactRestDb;
    }


}
