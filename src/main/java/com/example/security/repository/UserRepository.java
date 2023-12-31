package com.example.security.repository;

import com.example.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

// CRUD 함수를 이미 JpaRepository가 들고 있음
// @Respository 어노테이션이 없어도 IoC에 등록이 된다.
public interface UserRepository extends JpaRepository<User, Integer> {

    // SELECT * FROM user WHERE username = ?1
    User findByUsername(String username);
}
