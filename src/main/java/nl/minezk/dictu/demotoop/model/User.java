package nl.minezk.dictu.demotoop.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder=true)
public class User {

	private String id;
	private String userName;
	private String firstName;
	private String lastName;
	private String dateOfBirth;
	private String countryCode;
}
