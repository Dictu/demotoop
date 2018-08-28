package nl.minezk.dictu.demotoop.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import eu.toop.node.model.ChamberOfCommerceDataSet;
import nl.minezk.dictu.demotoop.model.Company;
import nl.minezk.dictu.demotoop.model.ToopException;
import nl.minezk.dictu.demotoop.model.User;
import nl.minezk.dictu.demotoop.service.ToopNodeClientService;

@Controller
@SessionAttributes({"company", "user", "dataset"})
public class LandingController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LandingController.class);

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
		User user = getUser();
		user.setDataSet(null); //since we are starting all over we need to remowe the old set.
		model.addAttribute("user", user);
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
		String companyName="";
		if (null != user.getDataSet()) {
			companyName = user.getDataSet().getCompanyCode();
		}
		else {
			companyName = getCompany().getName(); //must come from landing4 page
		}
		ChamberOfCommerceDataSet dataSet = toopNodeService.getCoCData(user.getCountryCode(), companyName);
		request.getSession().setAttribute("dataset", dataSet);
		ModelMap model = new ModelMap();
		model.addAttribute("user", user);
		model.addAttribute("dataset", dataSet);
		return new ModelAndView("landing5", model);
	}

	@PostMapping("/enter_company")
	public ModelAndView enterCompany(@ModelAttribute Company company) throws ToopException {
		ModelMap model = new ModelMap();
		model.addAttribute("user", getUser());
		request.getSession().setAttribute("company", company);
		return new ModelAndView("landing3", model);
	}
	
	@GetMapping(value={"/landing6"})
    public ModelAndView landing6() throws ToopException  {
		ModelMap model = new ModelMap();
		User user = getUser();
		ChamberOfCommerceDataSet dataSet = getDataSet();
		user.setDataSet(dataSet);
		model.addAttribute("user", user);
		model.addAttribute("dataset", dataSet);
		return new ModelAndView("landing6", model);
	}
	
	private User getUser() throws ToopException {
		User user = (User) request.getSession().getAttribute("user");
		LOGGER.debug("Sessie get: " + request.getSession().getId());
		if (null != user) {
			return user;
		}
		else {
			throw new ToopException("Not logged in!");
		}
	}
	
	private ChamberOfCommerceDataSet getDataSet() throws ToopException {
		ChamberOfCommerceDataSet dataSet = (ChamberOfCommerceDataSet) request.getSession().getAttribute("dataset");
		return dataSet;
	}
	
	private Company getCompany() throws ToopException {
		Company company = ((Company)request.getSession().getAttribute("company"));
		return company;
	}
	
}
