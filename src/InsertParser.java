import java.util.LinkedList;
import java.util.Scanner;

public class InsertParser {
    /* Reference to the input string being parsed */
    private String input;

	/* Constructor to initialize the input field */
    public InsertParser(String input) {
        this.input = input;
	}
	
	/* Parses and returns the name of the relation to insert into */
    public String parseRelationName() {
        Scanner scan = new Scanner(this.input); 
        String relName = scan.next();       // first token should contain relation name

        scan.close();

        return relName;
    }
	
	/* Parses and returns the number of attributes to insert */
    public Tuple parseTuple() {
        Scanner scan = new Scanner(this.input);
        String currTok;                                                 // stores current token total string after ("\'")
        String currString = "";                                         // stores total string
        LinkedList<AttributeValue> preTuple = new LinkedList<>();       // stores the linkedlist that will become the tuple
        LinkedList<Attribute> attrVals = LexicalAnalyzer.db.getRelation(scan.next()).getSchema(); // get schema of current relation

        while (scan.hasNext())                      // while more tokens are available
        {
            currTok = scan.next();                  // get next token
            preTuple.add(new AttributeValue());     // add empty attr

            if (currTok.charAt(0) == '\'')          // if we've hit string seq
            {
                currString += currTok.substring(1,currTok.length()) + " ";      // add initial token minus ('\'')
                while (scan.hasNext())                                          // while more tokens are available
                {
                    currTok = scan.next(); // get next

                    if (currTok.charAt(currTok.length() - 1) == '\'')           // if end of string hit
                    {
                        currString += currTok.substring(0,currTok.length()-1);  // add final token without ('\'')

                        break; // exit loop
                    }
                    else        // more or string to add
                    {
                        currString += currTok + " "; // concat to end of string plus space
                    }

                }
                preTuple.getLast().setValue(currString); // get empty tuple element and add total string
            }
            else
            {
                preTuple.getLast().setValue(currTok);
            }
        }

        for (int i = 0; i < attrVals.size(); ++i)
        {
            preTuple.get(i).setName(attrVals.get(i).getName());
        }

        Tuple returnTuple = new Tuple(preTuple);

        scan.close();
        
        return returnTuple;
    }
}

