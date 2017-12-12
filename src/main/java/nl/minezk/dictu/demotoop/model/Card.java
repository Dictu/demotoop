package nl.minezk.dictu.demotoop.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Card item on the index page.
 *
 */
@Builder(toBuilder=true)
public class Card {
	
	@NonNull
	@Getter
	private String name;
	@NonNull
	@Getter
    private String image;
	@NonNull
	@Getter
    private String link;
	@NonNull
	@Getter
    private String title_en;
	@NonNull
	@Getter
    private String title_nl;

}
