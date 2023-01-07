package com.example.application.data.service;

import com.example.application.data.entity.ContactUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContactUsRepository extends JpaRepository<ContactUs, Long>, JpaSpecificationExecutor<ContactUs> {

}
