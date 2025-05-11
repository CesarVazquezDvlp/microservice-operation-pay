package com.microservice.operation.pay.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.microservice.operation.pay.model.Pago;

@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {
	Optional<Pago> findByFolio(String folio);
}
