package nl.minezk.dictu.demotoop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import nl.minezk.dictu.demotoop.model.Card;
import nl.minezk.dictu.demotoop.model.Company;

@Configuration
public class DemoToopConfiguration {
	
	@Bean
	public List<Card> cards() {
		List<Card> cards = Collections.synchronizedList(new ArrayList<>());
		cards.add(Card.builder().image("/demotoop/css/eIDAS Login eID-Small.png").link("/demotoop/login").name("Demo Login").title_en("Demo Login").title_nl("Demo Login").build());
		return cards;
	}
	
	@Bean
	public List<Company> companies() {
		List<Company> result = Collections.synchronizedList(new ArrayList<>());
		result.add(Company.builder().name("ABC").build());
		return result;
	}
	
	@Bean
	public String toopNodeConsumerUrl() {
		return "http://localhost:8081/toopnode/consumer/provide";
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
