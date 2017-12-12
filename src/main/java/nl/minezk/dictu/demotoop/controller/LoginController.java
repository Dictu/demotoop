package nl.minezk.dictu.demotoop.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import nl.minezk.dictu.demotoop.model.Card;

@Controller
public class LoginController {
	
	@Autowired
	private List<Card> cards;
	
	@Autowired
	private HttpServletRequest request;

	@RequestMapping(value={"/"}, method = {RequestMethod.GET})
    public ModelAndView getIndex() {
		ModelMap map = new ModelMap();
		map.addAttribute("cards", cards);
		return new ModelAndView("index", map);
	}

	@RequestMapping(value={"/login"}, method = {RequestMethod.GET})
    public ModelAndView getLogin() {
		return new ModelAndView("login");
	}
	
	@RequestMapping(value={"/landing"}, method = {RequestMethod.GET})
    public ModelAndView getLanding() {
		String userName = request.getUserPrincipal().getName();
		return new ModelAndView("landing", "username", userName);
	}
}
