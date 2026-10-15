package com.example.service;

import java.util.List;
import com.example.model.User;

public interface UserService {
	User findById(Long id);

	User findByUsername(String username);

	List<User> findAll();

	void changePassword(String oldPassword, String newPassword);
}
