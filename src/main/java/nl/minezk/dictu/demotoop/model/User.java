package nl.minezk.dictu.demotoop.model;

import eu.toop.node.model.ChamberOfCommerceDataSet;
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
	
	//The company the user represents
	private ChamberOfCommerceDataSet dataSet;
}
