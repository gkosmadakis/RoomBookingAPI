package com.example.scannerapi.repository.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.scannerapi.model.Users;

public interface UsersDemoRepository extends JpaRepository<Users, Long> {

	Users findByUsername(String username);
}
