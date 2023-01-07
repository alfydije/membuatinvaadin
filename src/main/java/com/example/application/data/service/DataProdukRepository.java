package com.example.application.data.service;

import com.example.application.data.entity.DataProduk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataProdukRepository extends JpaRepository<DataProduk, Long>, JpaSpecificationExecutor<DataProduk> {

}
