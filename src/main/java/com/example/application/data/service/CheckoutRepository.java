package com.example.application.data.service;

import com.example.application.data.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CheckoutRepository extends JpaRepository<Checkout, Long>, JpaSpecificationExecutor<Checkout> {

}
