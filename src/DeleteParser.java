
public class DeleteParser {
    /* Reference to the input string being parsed */
    private String input;

    /* Constructor to initialize the input field */
    public DeleteParser(String input) {
		this.input = input;
	}

    /* Parses and returns the name of the relation for delete */
    public String parseRelationName() {
        return this.input;
    }
}
