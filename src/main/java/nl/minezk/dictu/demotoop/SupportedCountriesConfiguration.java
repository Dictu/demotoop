package nl.minezk.dictu.demotoop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nl.minezk.dictu.demotoop.model.Country;

@Configuration
public class SupportedCountriesConfiguration {

	/**
	 * Returns a list of supported {@link Country} elements.
	 * @return supportedCountries
	 */
	@Bean
	public List<Country> supportedCountries() {
		List<Country> countries = Collections.synchronizedList(new ArrayList<>());
		countries.add(Country.builder().countryCode("AA").countryName("Atlantis").build());
		countries.add(Country.builder().countryCode("SE").countryName("Sweden").build());
		countries.add(Country.builder().countryCode("NO").countryName("Norway").build());
		countries.add(Country.builder().countryCode("NL").countryName("The Netherlands").build());
		return countries;
	}
}
