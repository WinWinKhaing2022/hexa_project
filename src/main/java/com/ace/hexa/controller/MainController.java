package com.ace.hexa.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ace.hexa.dao.NewsDao;
import com.ace.hexa.dao.UserDao;
import com.ace.hexa.dto.interaction.InteractionRequestDto;
import com.ace.hexa.dto.interaction.InteractionResponseDto;
import com.ace.hexa.dto.news.NewsResponseDto;
import com.ace.hexa.dto.user.UserRequestDto;
import com.ace.hexa.dto.user.UserResponseDto;
import com.ace.hexa.model.InteractionBean;
import com.ace.hexa.model.UserBean;

@Controller
@RequestMapping("/hexa")
public class MainController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private NewsDao newsDao;

	@GetMapping("/login")
	public ModelAndView showLogin() {
		return new ModelAndView("login", "bean", new UserBean());
	}

	@PostMapping("/login")
	public String showLogin(@ModelAttribute("bean") UserBean user, HttpSession session, HttpServletRequest request) {
		if (userDao.check(user.getUser_email(), user.getUser_password())) {
			UserResponseDto dto = userDao.selectByEmail(user.getUser_email());
			session.setAttribute("userInfo", dto);
			if (dto.getUser_role() == 1) {
				return "redirect:/hexa/admin/home";
			} else if (dto.getUser_role() == 2) {
				return "redirect:/hexa/admin/home";
			} else {
				return "home";
			}
		}
		request.setAttribute("error", "<h3>email and password do not match !!</h3>");
		return "login";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("userInfo");
		session.invalidate();
		return "redirect:/hexa/login";
	}

	@GetMapping("/register")
	public ModelAndView showRegister() {
		return new ModelAndView("register", "bean", new UserBean());
	}

	@PostMapping("/register")
	public String register(@ModelAttribute("bean") UserBean user, HttpSession session, HttpServletRequest request) {
		if (!userDao.checkByEmail(user.getUser_email())) {
			UserRequestDto dto = new UserRequestDto();
			dto.setUser_name(user.getUser_name());
			dto.setUser_email(user.getUser_email());
			dto.setUser_password(user.getUser_password());
			userDao.insertUser(dto);
			return "redirect:/hexa/login";
		}
		request.setAttribute("error", "<h3>A user with that email already exists !!</h3>");
//		model.addAttribute("error", "<h3>A user with that email already exists !!</h3>");
		return "register";
	}

	@GetMapping("/home")
	public String showNews(ModelMap model) {
		ArrayList<NewsResponseDto> newsDto = newsDao.selectAllNews();
		model.addAttribute("newsList", newsDto);
		return "home";
	}

	@GetMapping("/details/{id}")
	public ModelAndView showDetails(@PathVariable long id, ModelMap model) {
		ArrayList<InteractionResponseDto> interactionDto = newsDao.selectInteractionByNewsId(id);
		NewsResponseDto dto = newsDao.selectNewsById(id);
		model.addAttribute("interactions", interactionDto);
		model.addAttribute("newsDetails", dto);
		return new ModelAndView("details", "bean", new InteractionBean());
	}

	@PostMapping("/details/addComment/{news_id}")
	public String addComment(@PathVariable long news_id, @ModelAttribute("bean") InteractionBean bean) {
		InteractionRequestDto dto = new InteractionRequestDto();
		dto.setNews_id(news_id);
		dto.setUser_id(bean.getUser_id());
		dto.setComments(bean.getComments());
		newsDao.insertComment(dto);
		return "redirect:/hexa/details/" + news_id;
	}

	@GetMapping("/dashboard")
	public String showDashboard() {
		return "adminDashboard";
	}

	@GetMapping("/news_manage")
	public String manageNews() {
		return "news_manage";
	}

	@GetMapping("/users")
	public String manageUsers() {
		return "users";
	}

}