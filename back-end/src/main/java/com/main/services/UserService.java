package com.main.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.models.User;
import com.main.pojo.UserLoginPOJO;
import com.main.repositories.UserRepository;

@RestController
@RequestMapping("/api/user/")
@CrossOrigin(origins = { "http://localhost:3000" })
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/all")
	public List<User> getUsers() {
		return userRepository.findAll();
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> addMapping(@RequestBody User userData) {
		if (userRepository.existsById(userData.getUsername())) {
			return ResponseEntity.badRequest()
					.body(String.format("Username already exists for field '%s'", userData.getUsername()));
		}

		userRepository.save(userData);
		return ResponseEntity.accepted().body(String.format("added '%s'", userData.toString()));
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserLoginPOJO userData) {
		if (userRepository.existsActiveUserById(userData.getUsername())) {
			Optional<User> user = userRepository.findById(userData.getUsername());
			if (user.get().getPassword().equals(userData.getPassword())) {
				return ResponseEntity.ok(user.get().getRole());
			}else {
				return ResponseEntity.badRequest().body("Invalid Password");
			}
		}else {
			return ResponseEntity.badRequest().body(String.format("Username '%s' not found", userData.getUsername()));
		}
	}
	
	
}
