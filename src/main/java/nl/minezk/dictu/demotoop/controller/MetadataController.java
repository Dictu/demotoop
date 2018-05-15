package nl.minezk.dictu.demotoop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.minezk.dictu.demotoop.model.ToopException;
import nl.minezk.dictu.demotoop.service.SAMLService;

@Controller
public class MetadataController {

	@Autowired
	private SAMLService samlService;
	
	@Autowired
	private String demotoopIssuerURL;
	
	@Autowired
	private String demotoopAssertionConsumerServiceURL;
	
	@GetMapping(value={"/metadata"}, produces = "text/xml")
    public @ResponseBody String metadata() throws ToopException {
		return samlService.getSPMetadata(demotoopIssuerURL, demotoopAssertionConsumerServiceURL);
	}
}
