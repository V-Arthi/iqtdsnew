package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.main.models.TestCondition;
import com.main.pojo.TestConditionPOJO;

@Repository
public interface TestConditionRepository extends JpaRepository<TestCondition, Long> {

}
