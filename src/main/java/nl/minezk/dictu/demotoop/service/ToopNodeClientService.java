package nl.minezk.dictu.demotoop.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.toop.node.model.ChamberOfCommerceDataSet;
import eu.toop.node.util.RestClient;

@Component
public class ToopNodeClientService extends RestClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ToopNodeClientService.class);

	@Autowired
	private String toopNodeConsumerUrl;
	
	
	public ChamberOfCommerceDataSet getCoCData(String country, String companyName) throws IOException {
		String url = toopNodeConsumerUrl + "?country=" + country + "&id=" + companyName;
		LOGGER.info("URL: {}", url);
		String json = this.get(url);
		ObjectMapper mapper = new ObjectMapper();
		LOGGER.info("json: {}", json);
		ChamberOfCommerceDataSet set = mapper.readValue(json, ChamberOfCommerceDataSet.class);
		return set;
	}
	
}
