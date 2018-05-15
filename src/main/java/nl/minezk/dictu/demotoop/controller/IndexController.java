package nl.minezk.dictu.demotoop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import nl.minezk.dictu.demotoop.model.Card;

@Controller
public class IndexController {
	@Autowired
	private List<Card> cards;
	
	@GetMapping(value={"/"})
    public ModelAndView getIndex() {
		ModelMap map = new ModelMap();
		map.addAttribute("cards", cards);
		return new ModelAndView("index", map);
	}
	
	
}
