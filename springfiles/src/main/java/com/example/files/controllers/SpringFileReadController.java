package com.example.files.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.files.entities.User;
import com.example.files.service.SpringReadFileService;

@Controller
public class SpringFileReadController {

	@Autowired
	private SpringReadFileService fileService;

	@GetMapping(value="/")
	public String home(Model model) {
		model.addAttribute("user", new User());
		List<User> users = fileService.findAll();
        model.addAttribute("users",users);
		return "view/users";
	}
	
	@PostMapping(value="/fileupload")
	public String fileUpload(@ModelAttribute User user,RedirectAttributes redirectAttributes) {
		boolean isFlag=fileService.saveDataFromUploadFile(user.getFile());
		if(isFlag) {
			redirectAttributes.addFlashAttribute("successmessage","File upload Successfully");
		}else {
			redirectAttributes.addFlashAttribute("errormessage","File upload not done, please try again");
		}
		return "redirect:/";
	}

}
