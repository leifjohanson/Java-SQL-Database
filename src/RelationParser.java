import java.util.LinkedList;
import java.util.Scanner;

public class RelationParser {
    /* Reference to the input string being parsed */
    private String input;

	/* Constructor to initialize the input field */
    public RelationParser(String input) {
        this.input = input; 
	}
	
	/* Parses and returns the number of attributes to create */
    public Relation parseRelation() {
        Scanner scan = new Scanner(this.input);
        String currTok;                                 // stores current token
        String relSet = "";                                  // contains values needed for one attribute in schema
        LinkedList<String> relEle = new LinkedList<>(); // stores the elements and their attributes of the schema
        Relation returnRel;                              
        String relName = scan.next();                   // get relation name
        currTok = scan.next();                          // get first attribute

        if (currTok.charAt(0) == '(')                   // test correct relation syntax ("(")
        {
            currTok = currTok.substring(1,currTok.length());    // cut off ("(") and begin getting initial attribute
            relSet += currTok + " ";                            // add name to attribute
            currTok = scan.next();                              // get data type
            relSet += currTok + " ";                            // add data type to attribute 
            currTok = scan.next();                              // get length
            currTok = currTok.substring(0, currTok.length()-1); // remove (",") or (")")
            relSet += currTok;                                  // add length to attribute
            relEle.add(relSet);                                 // add attruibute to attribute list 
            relSet = "";                                        // reset string

            while(scan.hasNext())                       // while more relations are avalible 
            {
                currTok = scan.next();                  // get next name
                relSet += currTok + " ";
                currTok = scan.next();                  // get next type
                relSet += currTok + " ";
                currTok = scan.next();                  // get next length
                relSet += currTok.substring(0, currTok.length()-1); // removed (",") and (")")

                relEle.add(relSet);
                relSet = "";
            }
        }

        returnRel = generateRelation(relName, relEle);

        scan.close();                       // reserve system resources
        
        return returnRel;
    }

    // Generates a new relations using name and list of attributes split into tokens
    private Relation generateRelation(String relName, LinkedList<String> relEle)
    {
        LinkedList<Attribute> attr = new LinkedList<>(); 
        String name;
        String dataType;
        int length;
        Scanner scan;

        for (int i = 0; i < relEle.size(); ++i) // for all attributes
        {
            scan = new Scanner(relEle.get(i));
            name = scan.next();                 // get name
            dataType = scan.next();             // get data type
            length = scan.nextInt();            // get length


            // att to schema
            attr.add(new Attribute(name, dataType, length));
        
            scan.close();
        }

        // return relation, by generating new relation
        Relation returnRel = new Relation(relName, attr);

        return returnRel;
    }
}
