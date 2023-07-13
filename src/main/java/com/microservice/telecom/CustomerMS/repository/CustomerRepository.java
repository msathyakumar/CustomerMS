package com.microservice.telecom.CustomerMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.telecom.CustomerMS.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
