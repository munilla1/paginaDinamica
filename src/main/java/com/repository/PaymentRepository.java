package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
	
	Optional<PaymentEntity> findByPaymentIntentId(String paymentIntentId);

}
