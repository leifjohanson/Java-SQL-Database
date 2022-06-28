import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class SelectParser {
    String[] input;
    SurlyDatabase db;

    Relation origR;
    Relation newR;
    String newRelationName;

    public SelectParser(String[] input, SurlyDatabase db, String newRelationName) {
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

        // generates new temp relation
        this.newR = new Relation(this.newRelationName, this.origR.getSchema(), true);

        LinkedList<Tuple> tuplesOfNewRelation = new LinkedList<>();

        LinkedList<Tuple> oldTuples = this.origR.getTuples();

        // removes all tuples from relation that are in tuplesToAdd
        for(int i = 0; i < oldTuples.size(); i ++) {
            tuplesOfNewRelation.add(oldTuples.get(i));
        }

        this.newR.setTuples(tuplesOfNewRelation);

        this.db.createRelation(this.newR);
    }
}

