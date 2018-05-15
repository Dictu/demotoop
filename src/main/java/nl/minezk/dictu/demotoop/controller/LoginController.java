package nl.minezk.dictu.demotoop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import nl.minezk.dictu.demotoop.model.Card;
import nl.minezk.dictu.demotoop.model.Country;
import nl.minezk.dictu.demotoop.model.ToopException;
import nl.minezk.dictu.demotoop.model.User;
import nl.minezk.dictu.demotoop.service.EidasSAMLService;

@Controller
@SessionAttributes("user")
public class LoginController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private List<Card> cards;
	
	@Autowired
	private List<Country> supportedCountries;
	
	@Autowired
	private String eidasNodeServiceProviderURL;
	
	@Autowired
	private String demotoopIssuerURL;
	
	@Autowired
	private String demotoopProviderName;
	
	@Autowired
	private EidasSAMLService samlService;

	@GetMapping(value={"/login"})
    public ModelAndView login() throws ToopException {
		Map<String, String> hashMap = new HashMap<>();
		hashMap.put("SAMLRequest", samlService.base64AuthnRequest(demotoopIssuerURL, eidasNodeServiceProviderURL, demotoopProviderName));
		ModelMap map = new ModelMap();
		map.addAttribute("countries", supportedCountries);
		map.addAttribute("destination", eidasNodeServiceProviderURL);
		map.addAttribute("data", hashMap);
		return new ModelAndView("countrySelect", map);
	}
	
	@GetMapping(value={"/logout"})
    public ModelAndView logout() {
		request.getSession().invalidate();
		ModelMap map = new ModelMap();
		map.addAttribute("cards", cards);
		return new ModelAndView("index", map);
	}
	
	@GetMapping(value={"/fakelogin"})
	public ModelAndView fake() {
		User user = User.builder()
				.countryCode("NL")
				.dateOfBirth("01-01-1980")
				.firstName("Bob")
				.lastName("de Bouwer")
				.id("NL/NL/1234")
				.userName("...")
				.build();
		request.getSession().setAttribute("user", user);
		LOGGER.debug("Sessie set: " + request.getSession().getId());
		ModelMap model = new ModelMap();
		model.addAttribute("user", user);		
		return new ModelAndView("landing", model);
	}
	
	@PostMapping(value={"/acs"})
	public ModelAndView acs(@RequestParam(value = "SAMLResponse") String samlToken) throws ToopException {
		LOGGER.debug("Received SAMLToken: " + samlToken);
		User user = samlService.getUser(samlToken);
		request.getSession().setAttribute("user", user);
		LOGGER.debug("Sessie set: " + request.getSession().getId());
		ModelMap model = new ModelMap();
		model.addAttribute("user", user);
		return new ModelAndView("landing", model);
	}

}
