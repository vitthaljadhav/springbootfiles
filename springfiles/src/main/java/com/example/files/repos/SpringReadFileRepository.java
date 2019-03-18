package com.example.files.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.files.entities.User;
@Repository
public interface SpringReadFileRepository extends JpaRepository<User, Long> {

}
