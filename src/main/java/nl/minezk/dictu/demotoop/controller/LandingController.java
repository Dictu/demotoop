package nl.minezk.dictu.demotoop.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import nl.minezk.dictu.demotoop.model.Company;
import nl.minezk.dictu.demotoop.model.ToopException;
import nl.minezk.dictu.demotoop.model.User;
import nl.minezk.dictu.demotoop.service.ToopNodeClientService;

@Controller
public class LandingController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LandingController.class);
	
	@Autowired
	private List<Company> companies;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ToopNodeClientService toopNodeService;
	
	@GetMapping(value={"/landing"})
    public ModelAndView landing() throws ToopException {
		ModelMap model = new ModelMap();
		model.addAttribute("user", getUser());
		return new ModelAndView("landing", model);
	}
	
	@GetMapping(value={"/landing2"})
    public ModelAndView landing2() throws ToopException {
		ModelMap model = new ModelMap();
		model.addAttribute("user", getUser());
        model.addAttribute("company", Company.builder().name("").build());
        return new ModelAndView("landing2", model);
    }
	
	@GetMapping(value={"/landing4"})
    public ModelAndView landing4() throws ToopException {
		ModelMap model = new ModelMap();
		model.addAttribute("user", getUser());
		return new ModelAndView("landing4", model);
	}
	
	@PostMapping(value={"/landing5"})
    public ModelAndView landing5() throws IOException, ToopException {
		User user = getUser();
		String companyName = ((Company)request.getSession().getAttribute("company")).getName();
		String result = toopNodeService.getCoCData(user.getCountryCode(), companyName).toString();
		request.getSession().setAttribute("companyResult", result);
		ModelMap model = new ModelMap();
		model.addAttribute("user", user);
		model.addAttribute("companyResult", request.getSession().getAttribute("companyResult"));
		return new ModelAndView("landing5", model);
	}

	@PostMapping("/enter_company")
	public ModelAndView enterCompany(@ModelAttribute Company company) throws ToopException {
		ModelMap model = new ModelMap();
		model.addAttribute("user", getUser());
		request.getSession().setAttribute("company", company);
		if (companies.contains(company)) {
			return new ModelAndView("landing7", model);
		}
		else {
			return new ModelAndView("landing3", model);
		}
	}
	
	@GetMapping(value={"/landing6"})
    public ModelAndView landing6() throws ToopException  {
		ModelMap model = new ModelMap();
		model.addAttribute("user", getUser());
		model.addAttribute("companyResult", request.getSession().getAttribute("companyResult"));
		return new ModelAndView("landing6", model);
	}
	
	@GetMapping(value={"/landing7"})
    public ModelAndView landing7() throws ToopException {
		ModelMap model = new ModelMap();
		model.addAttribute("user", getUser());
		model.addAttribute("companyResult", request.getSession().getAttribute("companyResult"));
		return new ModelAndView("landing7", model);
	}
	
	public User getUser() throws ToopException {
		User user = (User) request.getSession().getAttribute("user");
		LOGGER.debug("Sessie get: " + request.getSession().getId());
		if (null != user) {
			return user;
		}
		else {
			throw new ToopException("Not logged in!");
		}
	}
}
