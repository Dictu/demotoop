package nl.minezk.dictu.demotoop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import nl.minezk.dictu.demotoop.model.Card;
import nl.minezk.dictu.demotoop.model.User;

@Configuration
public class DemoToopConfiguration {
	
	@Value("${toop.node.consumer.url}")
	public String toopNodeConsumerUrl;
	
	@Bean
	public List<Card> cards() {
		List<Card> cards = Collections.synchronizedList(new ArrayList<>());
		cards.add(Card.builder().image("/demotoop/css/eIDAS Login eID-Small.png")
				.link("/demotoop/login")
				.name("EU Login")
				.title_en("Log in to the TOOP demo portal with your national eID (eIDAS)")
				.title_nl("Log in voor de TOOP demo portal met je nationale eID (eIDAS)")
				.build());
		return cards;
	}
	
	@Bean
	public Map<String, User> fakeUsers() {
		Map<String, User> users = Collections.synchronizedMap(new HashMap<>());
		users.put("NL", User.builder().countryCode("NL").dateOfBirth("01-01-1980").firstName("Bob").lastName("de Bouwer").userName("bob").id("NL/NL/123").build());
		users.put("NO", User.builder().countryCode("NO").dateOfBirth("01-01-1980").firstName("Ole").lastName("Olsen").userName("ole").id("NO/NL/456").build());
		users.put("SE", User.builder().countryCode("SE").dateOfBirth("01-01-1980").firstName("Johan").lastName("Johansson").userName("johan").id("SE/NL/789").build());
		Map<String, User> userMap = Collections.unmodifiableMap(users);
		return userMap;
	}
	
	@Bean
	public String toopNodeConsumerUrl() {
		return toopNodeConsumerUrl;
	}
	
	//language file beans. from http://www.baeldung.com/spring-boot-internationalization
	@Bean
	public LocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(Locale.ENGLISH);
	    return slr;
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	    lci.setParamName("lang");
	    return lci;
	}

	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(localeChangeInterceptor());
	}
}
