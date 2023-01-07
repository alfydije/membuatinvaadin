package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Checkout extends AbstractEntity {

    private String nama;
    private String alamat;
    private LocalDate tanggalBooking;
    @Lob
    @Column(length = 1000000)
    private byte[] gambar;
    private String order_notes;

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public LocalDate getTanggalBooking() {
        return tanggalBooking;
    }
    public void setTanggalBooking(LocalDate tanggalBooking) {
        this.tanggalBooking = tanggalBooking;
    }
    public byte[] getGambar() {
        return gambar;
    }
    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }
    public String getOrder_notes() {
        return order_notes;
    }
    public void setOrder_notes(String order_notes) {
        this.order_notes = order_notes;
    }

}
