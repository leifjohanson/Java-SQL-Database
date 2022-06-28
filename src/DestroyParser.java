
public class DestroyParser {
    /* Reference to the input string being parsed */
    private String input;

    /* Constructor to initialize the input field */
    public DestroyParser(String input) {
		this.input = input;
	}

    /* Parses and returns the name of the relation to destroy */
    public String parseRelationName() {
        return this.input;
    }
}
