package nl.minezk.dictu.demotoop.model;

import lombok.Builder;
import lombok.Data;

/**
 * Card item on the index page.
 *
 */

@Data
@Builder(toBuilder=true)
public class Card {
	
	private String name;
    private String image;
    private String link;
    private String title_en;
    private String title_nl;

}
