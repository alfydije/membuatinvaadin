package com.example.application.data.service;

import com.example.application.data.entity.ContactUs;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ContactUsService {

    private final ContactUsRepository repository;

    public ContactUsService(ContactUsRepository repository) {
        this.repository = repository;
    }

    public Optional<ContactUs> get(Long id) {
        return repository.findById(id);
    }

    public ContactUs update(ContactUs entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<ContactUs> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ContactUs> list(Pageable pageable, Specification<ContactUs> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
