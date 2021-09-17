package com.libra.web.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.libra.core.entities.Book;
import com.libra.core.services.IBookService;
import com.libra.exception.ResourceNotFoundException;
import com.libra.web.message.MessageResponse;

@Controller
@RequestMapping("/book")
public class BookUserController {
	@Autowired
	private IBookService bookService;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping
	public String bookHome(Model model) {
		
		List<Book> bookList = bookService.findAll();
		
		model.addAttribute("book", bookList);
		model.addAttribute("title", "Sách");
		return "book/bookList";
	}
	@GetMapping("/findById/{id}")
	public String findBookById(@PathVariable("id") Integer id, HttpSession session, Model model) {
		if(id != null) {
			Optional<Book> bookOtp;
			try {
				bookOtp = bookService.findById(id);
				Book book = bookOtp.get();
				
				model.addAttribute("book", book);
				model.addAttribute("title", "Chi Tiết Sách");
				return "book/bookDetail";
			} catch (ResourceNotFoundException e) {
				model.addAttribute("message", new MessageResponse("Xảy ra lỗi, Vui lòng thử lại!", "danger"));
				String errorMessage = e.getMessage();
				LOGGER.error(errorMessage);
				return "book/bookList";
			}
			
		}
		session.setAttribute("message", new MessageResponse("Không tìm thấy Sách!!, vui lòng thử lại!", "danger"));
		return "book/bookList";
	}
}
