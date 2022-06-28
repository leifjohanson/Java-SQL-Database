import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

// The term 'condition' used in comments in this file represent strings that look like 'CNUM = CSCI145' or 'ROOM = AW205'
public class SelectWhereParser {

    String[] input;
    SurlyDatabase db;

    Relation origR;
    Relation newR;
    String newRelationName;

    public SelectWhereParser(String[] input, SurlyDatabase db, String newRelationName) {
        this.input = input;
        this.db = db;
        // sets the relation for this object as the first input line
        this.origR = db.getRelation(input[0]);
        this.newRelationName = newRelationName;
    }

    public void parseAddRelation() {
        // check to maintain rules of temp relation
        Relation prevRel = db.getRelation(newRelationName); // This name may already be a temp rel

        if (prevRel != null)
        {
            if (prevRel.isTemp())
                db.destroyRelation(this.newRelationName);
            else 
                return;
        }

        // gets the string of all conditions after the WHERE
        String cmdString = getCndStrings(input);

        // Splits on the OR values, creates an array of independent statements to be run, ANDs are then split at each index of the original array.
        // For example, A or B and C or D becomes
        // [[A], [B, C], [D]]
        ArrayList<String[]> queriesCnds = parseString(cmdString);

        // Creates an empty array list of each tuple
        ArrayList<Tuple> tuplesToAdd = new ArrayList<>();

        // For each 1D array of conditions in the queriesCmds 2D array
        for(String[] eachCmd : queriesCnds) {
            // adds the tuples returned by each condition to total amount of tuples to be added
            ArrayList<Tuple> tempTuples = runQuery(eachCmd);
            for(int i = 0; i < tempTuples.size(); i ++) {
                tuplesToAdd.add(tempTuples.get(i));
            }
        }

        // generates new temp relation
        newR = new Relation(newRelationName, origR.getSchema(), true);
        LinkedList<Tuple> tuplesOfNewRelation = new LinkedList<>();

        // removes all tuples from relation that are in tuplesToAdd
        for(int i = 0; i < tuplesToAdd.size(); i ++) {
            tuplesOfNewRelation.add(tuplesToAdd.get(i));
        }

        tuplesOfNewRelation = removeDuplicates(tuplesOfNewRelation);
        newR.setTuples(tuplesOfNewRelation);

        db.createRelation(newR);
    }

    // see comment before function call in parseAddRelation()
    private static ArrayList<String[]> parseString(String str) {

        ArrayList<String[]> arrL = new ArrayList<>();
        String[] preParse = str.split(" or ");

        for(String s : preParse) {
            arrL.add(s.split(" and "));
        }

        return arrL;
    }

    // In prose: Takes all tuples that fit first condition in input string and then makes sure out of those tuples that it fits all the other conditions in input string
    public ArrayList<Tuple> runQuery(String[] ss) {
        // creates List of tuples to be added to temporary relation
        ArrayList<Tuple> tempTuples = new ArrayList<>();

        // Lines to 'end comment'; splits condition of the first condition into separate variables
        String firstCmd = ss[0];

        String[] firstSplitCmd = firstCmd.split(" ");

        String firstAttributeName = firstSplitCmd[0];
        String firstOperator = firstSplitCmd[1];
        String firstAttributeValue = firstSplitCmd[2]; // end comment

        LinkedList<Tuple> tuplesInRelation = origR.getTuples();

        // run through all the tuples and add the tuples that fit the first condition
        for(int i = 0; i < tuplesInRelation.size(); i ++) {
            Tuple curTuple = tuplesInRelation.get(i);
            // switch statement to see what the operator is
            switch (firstOperator) {
                case ">":
                    if (Integer.parseInt(curTuple.getValue(firstAttributeName)) > Integer.parseInt(firstAttributeValue)) {
                        tempTuples.add(curTuple);
                    }
                    break;
                case "<":
                    if (Integer.parseInt(curTuple.getValue(firstAttributeName)) < Integer.parseInt(firstAttributeValue)) {
                        tempTuples.add(curTuple);
                    }
                    break;
                // different cases for string and integer
                case "=":
                    if (isNumeric(firstAttributeValue)) {
                        if (Integer.parseInt(curTuple.getValue(firstAttributeName)) == Integer.parseInt(firstAttributeValue)) {
                            tempTuples.add(curTuple);
                        }
                    } else {
                        if (curTuple.getValue(firstAttributeName).equals(firstAttributeValue)) {
                            tempTuples.add(curTuple);
                        }
                    }

                    break;
                // different cases for string and integer
                case "!=":
                    if (isNumeric(firstAttributeValue)) {
                        if (Integer.parseInt(curTuple.getValue(firstAttributeName)) != Integer.parseInt(firstAttributeValue)) {
                            tempTuples.add(curTuple);
                        }
                    } else {
                        if (!curTuple.getValue(firstAttributeName).equals(firstAttributeValue)) {
                            tempTuples.add(curTuple);
                        }
                    }
                    break;
                case ">=":
                    if (Integer.parseInt(curTuple.getValue(firstAttributeName)) >= Integer.parseInt(firstAttributeValue)) {
                        tempTuples.add(curTuple);
                    }
                    break;
                case "<=":
                    if (Integer.parseInt(curTuple.getValue(firstAttributeName)) <= Integer.parseInt(firstAttributeValue)) {
                        tempTuples.add(curTuple);
                    }
                    break;
                default:
                    System.out.println("NOT A VALID OPERATOR");
            }
        }

        // run through the rest of the conditions in the input array
        for(int i = 1; i < ss.length; i++) {

            String[] splitCmd = ss[i].split(" ");
            String attributeName = splitCmd[0];
            String operator = splitCmd[1];
            String attributeValue = splitCmd[2];

            // run through all the tuples that fit the first condition (0 index), and then remove them from the returned tuple list if they don't fit all the requirements
            for(int j = 0; j < tempTuples.size(); j ++) {
                Tuple curTuple = tempTuples.get(j);

                // operations have to be reversed to act as an overlap
                switch (operator) {
                    case ">":
                        if (Integer.parseInt(curTuple.getValue(attributeName)) <= Integer.parseInt(attributeValue)) {
                            tempTuples.set(j, null);
                        }
                        break;
                    case "<":
                        if (Integer.parseInt(curTuple.getValue(attributeName)) >= Integer.parseInt(attributeValue)) {
                            tempTuples.set(j, null);
                        }
                        break;
                    case "=":
                        // cases for both strings and integers
                        if(isNumeric(attributeValue)) {
                            if (Integer.parseInt(curTuple.getValue(attributeName)) != Integer.parseInt(attributeValue)) {
                                tempTuples.set(j, null);
                            }
                        } else {
                            if (!curTuple.getValue(attributeName).equals(attributeValue)) {
                                tempTuples.set(j, null);
                            }
                        }
                        break;
                    case "!=":
                        // cases for both strings and integers
                        if(isNumeric(attributeValue)) {
                            if (Integer.parseInt(curTuple.getValue(attributeName)) == Integer.parseInt(attributeValue)) {
                                tempTuples.set(j, null);
                            }
                        } else {
                            if (curTuple.getValue(attributeName).equals(attributeValue)) {
                                tempTuples.set(j, null);
                            }
                        }
                        break;
                    case ">=":
                        if (Integer.parseInt(curTuple.getValue(attributeName)) < Integer.parseInt(attributeValue)) {
                            tempTuples.set(j, null);
                        }
                        break;
                    case "<=":
                        if (Integer.parseInt(curTuple.getValue(attributeName)) > Integer.parseInt(attributeValue)) {
                            tempTuples.set(j, null);
                        }
                        break;
                    default:
                        System.out.println("INVALID OPERATOR");
                }
            }
        }

        ArrayList<Tuple> output = new ArrayList<>();

        // return all the elements in the original tempTuples that weren't set to null during the previous for loop
        for(int i = 0; i < tempTuples.size(); i ++) {
            if(tempTuples.get(i) != null) {
                output.add(tempTuples.get(i));
            }
        }

        // returns all the elements that fit each set of statements separated by an or
        return output;
    }

    // see comment on function call in parseAddRelation()
    private static String getCndStrings(String[] str) {
        String output = "";
        str = Arrays.copyOfRange(str, 2, str.length);
        for(String s : str) {
            output += s + " ";
        }

        return output.trim();
    }

    // checks if a string can be converted to an integer
    private static boolean isNumeric(String numAsString) {
        try {
            Integer.parseInt(numAsString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static LinkedList<Tuple> removeDuplicates(LinkedList<Tuple> tuples) {
        ArrayList<Tuple> tempTuples = new ArrayList<>();
        for(int i = 0; i < tuples.size(); i ++) {
            Tuple curTuple = tuples.get(i);
            if(!tempTuples.contains(curTuple)) {
                tempTuples.add(curTuple);
            } else {
                tuples.remove(i);
            }
        }

        return tuples;
    }
}
