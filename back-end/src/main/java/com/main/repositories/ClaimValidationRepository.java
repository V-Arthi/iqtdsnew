package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.ClaimValidation;

public interface ClaimValidationRepository extends JpaRepository<ClaimValidation, Long> {

}
