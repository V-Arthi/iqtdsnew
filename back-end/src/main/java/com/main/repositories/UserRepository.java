package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.models.User;

public interface UserRepository extends JpaRepository<User, String> {
	@Query("SELECT CASE WHEN count(m)> 0 THEN true ELSE false END  FROM User m WHERE m.username=:username and m.active=1")
	public boolean existsActiveUserById(@Param("username") String username);
}
