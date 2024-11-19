package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.models.LoB;

@Repository
public interface LoBRepository extends JpaRepository<LoB, Long> {

}
