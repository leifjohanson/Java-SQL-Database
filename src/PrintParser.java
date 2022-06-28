import java.util.LinkedList;
import java.util.Scanner;

public class PrintParser {
    /* Reference to the input string being parsed */
    private String input;

	/* Constructor to initialize the input field */
    public PrintParser(String input) {
		this.input = input;
	}
	
	/* Parses and returns the names the relations to print */
    public String[] parseRelationNames() {
        Scanner scan = new Scanner(this.input);
        LinkedList<String> relNames = new LinkedList<>();
        String currTok;                                                 // stores current token

        while (scan.hasNext())                                          // while more tokens are available 
        {
            currTok = scan.next();          
            if (currTok.charAt(currTok.length() - 1) == ',')            // if syntax is correct and tokens are separated by (",")
            {
                currTok = currTok.substring(0, currTok.length()-1);     // remove comma
            }
            relNames.add(currTok);                                      // add it to list
        }

        scan.close();

        return relNames.toArray(new String[relNames.size()]);           // convert list to array to return
    }
}
