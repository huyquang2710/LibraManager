package com.libra.web.controller.admin;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.libra.core.entities.Author;
import com.libra.core.entities.Book;
import com.libra.core.entities.Category;
import com.libra.core.entities.Publisher;
import com.libra.core.services.IAuthorService;
import com.libra.core.services.IBookService;
import com.libra.core.services.ICategoriesService;
import com.libra.core.services.IPublisherService;
import com.libra.core.utils.FileUploadUtil;
import com.libra.exception.ResourceNotFoundException;
import com.libra.web.dto.BookDetailDTO;
import com.libra.web.message.MessageResponse;

@Controller
@RequestMapping("/admin/book")
public class BookController {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IBookService bookService;
	@Autowired
	private IAuthorService authorService;
	@Autowired
	private IPublisherService publisherService;
	@Autowired
	private ICategoriesService categoryService;
	@Autowired
	private ModelMapper modelMapper;
	
	@GetMapping
	public String getAll(Model model) {
		return getDetailByPage(model, 1, null);
	}
	
	// findAll
	@GetMapping("/page/{pageNo}")
	public String getDetailByPage(Model model, @PathVariable("pageNo") int currentPage, HttpSession session) {
		Page<Book> page = bookService.findByPageable(currentPage);
		
		//ki???m tra book c?? r???ng ko
		if(page.isEmpty()) {
			
			return "admin/book/bookPage";
		}
		long totalItems = page.getTotalElements(); // t???ng
		int totalPages = page.getTotalPages();
		
		List<Book> bookList = page.getContent();
		
		model.addAttribute("currentPage", currentPage); // trang hi???n t???i
		model.addAttribute("totalItems", totalItems); // t???ng s??? t??c gi???
		model.addAttribute("totalPages", totalPages);
		
		model.addAttribute("book", bookList);
		model.addAttribute("title", "S??ch");
		return "admin/book/bookPage";
	}
	//findById
	@GetMapping("/findById-detail/{id}")
	public String findByIdDetail(@PathVariable("id") Integer id, Model model, HttpSession session) throws ResourceNotFoundException {
		// ki???m tra id c?? gi?? tr??? ko
		System.out.println("id: " + id);
		
		//l???y danh s??ch t??n t??c gi???
		List<Author> authorList = authorService.findAll();
		//l???y danh s??ch t??n nh?? xu???t b???n
		List<Publisher> publisherList = publisherService.findAll();
		//l???y danh s??ch t??n th??? lo???i
		List<Category> categoryList = categoryService.findAll();
		
		model.addAttribute("author", authorList);
		model.addAttribute("publisher", publisherList);
		model.addAttribute("category", categoryList);
		
		if(id != null) {
			Optional<Book> bookOtp = this.bookService.findById(id);
			Book book = bookOtp.get();
			
			model.addAttribute("book", book);
			model.addAttribute("title", "Chi Ti???t S??ch");
			return "admin/book/bookDetail";
		}
		session.setAttribute("message", new MessageResponse("Kh??ng t??m th???y S??ch!!, vui l??ng th??? l???i!", "danger"));
		return "admin/book/bookPage";
	}
	@GetMapping("/findById-update/{id}")
	public String findByIdUpdate(@PathVariable("id") Integer id, 
								Model model, HttpSession session ) throws ResourceNotFoundException {
		// ki???m tra id c?? gi?? tr??? ko
		System.out.println("id: " + id);
		
		//l???y danh s??ch t??n t??c gi???
		List<Author> authorList = authorService.findAll();
		//l???y danh s??ch t??n nh?? xu???t b???n
		List<Publisher> publisherList = publisherService.findAll();
		//l???y danh s??ch t??n th??? lo???i
		List<Category> categoryList = categoryService.findAll();
		
		if(id != null) {
			Book book = bookService.findById(id).get();
			
			model.addAttribute("author", authorList);
			model.addAttribute("publisher", publisherList);
			model.addAttribute("category", categoryList);
			
			model.addAttribute("book", book);
			model.addAttribute("title", "C???p Nh???t S??ch");
			return "admin/book/bookEdit";
		}
		session.setAttribute("message", new MessageResponse("Kh??ng t??m th???y S??ch!!, vui l??ng th??? l???i!", "danger"));
		return "admin/book/bookPage";
	}
	
	//form add book
	@Transactional(readOnly = true)
	@GetMapping("/new")
	public String bookForm(@ModelAttribute Book book, Model model) {
		//l???y danh s??ch t??n t??c gi???
		List<Author> authorList = authorService.findAll();
		//l???y danh s??ch t??n nh?? xu???t b???n
		List<Publisher> publisherList = publisherService.findAll();
		//l???y danh s??ch t??n th??? lo???i
		List<Category> categoryList = categoryService.findAll();
		
		model.addAttribute("author", authorList);
		model.addAttribute("publisher", publisherList);
		model.addAttribute("category", categoryList);
		
		model.addAttribute("book", book);
		model.addAttribute("title", "Th??m S??ch");
		return "admin/book/bookNew";
	}
	
	//new sach action
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newBook(@Valid @ModelAttribute("book") BookDetailDTO bookDto,
							BindingResult bindingResult,
							@RequestParam("imageFile") MultipartFile multipartFile,
							Model model,
							HttpSession session) {
		try {
			// ki???m tra d??? li???u h???p ll???
			if(bindingResult.hasErrors()) {
				System.out.println("Book: " + bindingResult.toString());
				return "admin/book/bookNew";
			}
			//new anh
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			bookDto.setImage(fileName);

			Book book = new Book();
			
			//chuyen Dto thanh entity
			modelMapper.map(bookDto, book);
			
			this.bookService.save(book);
			
			
			//l??u ???????ng d???n
			String uploadDir = "avatar/book/" + book.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
			model.addAttribute("book", book);
			session.setAttribute("message", new MessageResponse("Th??m S??ch th??nh c??ng!!", "success"));
			return "redirect:/admin/book";
			
		} catch (Exception e) {
			System.out.println("Th??m S??ch th???t b???i!!");
			String errorMessage = e.getMessage();
			session.setAttribute("message", new MessageResponse( "Th??m S??ch Th???t B???i, vui l??ng th??? l???i", "danger"));
			LOGGER.error(errorMessage);
			return "redirect:/admin/book";
		}
	}
	//update
	@PostMapping("/update")
	public String updateBook(@Valid @ModelAttribute("book") Book book ,
			BindingResult bindingResult,
			@RequestParam("imageFile") MultipartFile multipartFile,
			Model model,
			HttpSession session) {
		
		try {
			// Ki???m tra d??? li???u h???p l???
			if(bindingResult.hasErrors()) {
				System.out.println("Book: " + bindingResult.toString());
				return "admin/book/bookEdit";
			}
			//l???y d??? li???u c??
			Book oldBook = bookService.findById(book.getId()).get();
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			if(!multipartFile.isEmpty()) {
				System.out.println("File ???nh t???n t???i !!");
				//x??a ???nh c??
				String deleteFile = "avatar/book/" + book.getId();
				File deleteAction = new File(deleteFile, oldBook.getImage());
				deleteAction.delete();
				
				//th??m ???nh m???i
				book.setImage(fileName);	
				String uploadDir = "avatar/book/" + book.getId();
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			} 
			this.bookService.update(book);
			System.out.println("Th??m S??ch th??nh c??ng!!");
			
			model.addAttribute("book", book);
			session.setAttribute("message", new MessageResponse("C???p nh???t S??ch th??nh c??ng!!", "success"));
			return "redirect:/admin/book";
		} catch (Exception e) {
			System.out.println("C???p nh???t S??ch th???t b???i!!");
			String errorMessage = e.getMessage();
			session.setAttribute("message", new MessageResponse(e.getMessage(), "danger"));
			LOGGER.error(errorMessage);
			return "admin/book/bookEdit";
		}
	}
	@GetMapping("/delete/{id}")
	public String deleteBook(@PathVariable("id") Integer id, HttpSession session) {
		try {
			bookService.delete(id);
			
			session.setAttribute("message", new MessageResponse("X??a Th??nh C??ng!!,!", "success"));
			return "redirect:/admin/book";
		} catch (ResourceNotFoundException e) {
			String errorMessage = e.getMessage();
	        LOGGER.error(errorMessage);
	        session.setAttribute("message", new MessageResponse("X??a Th???t B???i!!,!", "danger"));
	        return "admin/book/bookPage";
		}
	}
}
