import java.util.LinkedList;
import java.util.stream.Collectors;

public class ProjectParser {

    String relName;
    String newRelName;
    SurlyDatabase db;
    private LinkedList<String> attrList;
    
    public ProjectParser(String[] cmdParamsArr, String newRelName, SurlyDatabase db)
    {
        this.relName = cmdParamsArr[cmdParamsArr.length-1];
        this.newRelName = newRelName;
        this.db = db;

        this.attrList = new LinkedList<>();

        for (int i = 0; i < cmdParamsArr.length - 2; ++i)
            this.attrList.add(cmdParamsArr[i]);

        if (this.attrList.size() != 1)
        {
            for (int i = 0; i < this.attrList.size()-1; ++i)
                attrList.set(i,attrList.get(i).substring(0, attrList.get(i).length()-1));
        }
    }

    public void projectRelation()
    {
        Relation rel = db.getRelation(this.relName);
        Relation newRelTest = db.getRelation(this.newRelName);

        // check to maintain rules of temp relation
        if (newRelTest != null)
        {
            if (newRelTest.isTemp())
                db.destroyRelation(this.newRelName);
            else 
                return;
        }

        LinkedList<Attribute> schema = rel.getSchema();
        LinkedList<Tuple> tuples = rel.getTuples();
        int[] ordering = new int[attrList.size()]; 

        LinkedList<Attribute> newSchema = new LinkedList<>();
        LinkedList<Tuple> newTuples = new LinkedList<>();

        int j = 0;

        // get ordered indexes of schema where columns will be projected
        for (int i = 0; i < schema.size(); ++i)
        {
            if (attrList.contains(schema.get(i).getName()))
            {
                ordering[j] = i;
                j++;
            }
        }

        // use ordered indexes to make new schema with only values from projection
        for (int i = 0; i < ordering.length; ++i)
        {
            newSchema.add(schema.get(ordering[i]));
        }

        LinkedList<AttributeValue> tempVals;
        LinkedList<AttributeValue> currTup;

        // use ordered indexes to refactor tuples to only contain columns from projection
        for (int i = 0; i < tuples.size(); ++i)
        {
            currTup = tuples.get(i).getValues();
            tempVals = new LinkedList<>();

            for (j = 0; j < ordering.length; ++j)
                tempVals.add(currTup.get(ordering[j]));

            newTuples.add(new Tuple(tempVals));
        }

        // Check for distinct values
        newTuples = newTuples.stream().distinct().collect(Collectors.toCollection(LinkedList::new));

        // Generate new relation
        Relation newRel = new Relation(this.newRelName, newSchema, true);
        newRel.setTuples(newTuples);

        db.createRelation(newRel);
    }

}
