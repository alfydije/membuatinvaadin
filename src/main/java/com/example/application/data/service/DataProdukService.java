package com.example.application.data.service;

import com.example.application.data.entity.DataProduk;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DataProdukService {

    private final DataProdukRepository repository;

    public DataProdukService(DataProdukRepository repository) {
        this.repository = repository;
    }

    public Optional<DataProduk> get(Long id) {
        return repository.findById(id);
    }

    public DataProduk update(DataProduk entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<DataProduk> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<DataProduk> list(Pageable pageable, Specification<DataProduk> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
