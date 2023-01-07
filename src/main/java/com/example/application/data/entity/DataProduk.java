package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class DataProduk extends AbstractEntity {

    private String nama;
    private String harga;
    private String detail;
    @Lob
    @Column(length = 1000000)
    private byte[] gambar;
    private LocalDate dateOfBirth;
    private String occupation;
    private String role;
    private boolean important;

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getHarga() {
        return harga;
    }
    public void setHarga(String harga) {
        this.harga = harga;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public byte[] getGambar() {
        return gambar;
    }
    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getOccupation() {
        return occupation;
    }
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean isImportant() {
        return important;
    }
    public void setImportant(boolean important) {
        this.important = important;
    }

}
