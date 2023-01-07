package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
public class ContactUs extends AbstractEntity {

    private String nama;
    @Email
    private String email;
    private String subject;
    private String pesan;

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getPesan() {
        return pesan;
    }
    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

}
