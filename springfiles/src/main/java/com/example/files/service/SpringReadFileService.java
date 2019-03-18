package com.example.files.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.files.entities.User;

public interface SpringReadFileService {

	List<User> findAll();

	boolean saveDataFromUploadFile(MultipartFile file);

}
