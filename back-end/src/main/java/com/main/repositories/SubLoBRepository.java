package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.models.SubLoB;

@Repository
public interface SubLoBRepository extends JpaRepository<SubLoB, Long> {

}
