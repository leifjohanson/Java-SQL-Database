import java.util.LinkedList;

public class JoinParser {

    private String rel1;
    private String rel2;
    private String relAttr1;
    private String relAttr2;
    private String newRel;
    private String opp;

    private SurlyDatabase db;

    public JoinParser(String[] strArr, String newRel, SurlyDatabase db)
    {
        this.rel1 = strArr[0].substring(0,strArr[0].length()-1);
        this.rel2 = strArr[1];

        // checks if dot syntax is being used
        String tempRelAttr1 = strArr[3];
        if (tempRelAttr1.contains("."))
            this.relAttr1 = tempRelAttr1.split("\\.")[1];
        else 
            this.relAttr1 = tempRelAttr1;

        String tempRelAttr2 = strArr[5];
        if (tempRelAttr2.contains("."))
            this.relAttr2 = tempRelAttr2.split("\\.")[1];
        else    
            this.relAttr2 = tempRelAttr2;

        this.opp = strArr[4];

        this.newRel = newRel;
        this.db = db;
    }

    public void joinRelations()
    {
        Relation newRelTest = db.getRelation(this.newRel);

        // check to maintain rules of temp relation
        if (newRelTest != null)
        {
            if (newRelTest.isTemp())
                db.destroyRelation(this.newRel);
            else 
                return;
        }

        Relation rel1Ref = this.db.getRelation(this.rel1);
        Relation rel2Ref = this.db.getRelation(this.rel2);

        if (rel1Ref == null)
            return;

        if (rel2Ref == null)
            return;
        
        LinkedList<Attribute> rel1Schema = rel1Ref.getSchema();
        LinkedList<Attribute> rel2Schema = rel2Ref.getSchema();

        int attrIndex1 = -1;
        int attrIndex2 = -1;

        // finds indexes of attributes being compared
        for (int i = 0; i < rel1Schema.size(); ++i)
        {
            if (rel1Schema.get(i).getName().equals(this.relAttr1))
                attrIndex1 = i;
        }

        for (int i = 0; i < rel2Schema.size(); ++i)
        {
            if (rel2Schema.get(i).getName().equals(this.relAttr2))
                attrIndex2 = i;
        }

        if (attrIndex1 < 0 || attrIndex2 < 0)
        {
            db.createRelation(new Relation(this.newRel, new LinkedList<>()));
        }

        if (rel1Schema.get(attrIndex1).getLength() != rel2Schema.get(attrIndex2).getLength())
            return;

        LinkedList<Attribute> newSchema;

        // build a schema depending on if the attrbutes being compared are the same attribute or not
        if (this.relAttr1.equals(this.relAttr2))
        {
            LinkedList<Attribute> temp2Schema = new LinkedList<>(rel2Schema);
            temp2Schema.remove(attrIndex2);
            newSchema = new LinkedList<>(rel1Schema);
            newSchema.addAll(temp2Schema);
        }
        else
        {
            newSchema = new LinkedList<>(rel1Schema);
            newSchema.addAll(rel2Schema);
        }

        // init useful variables
        LinkedList<Tuple> rel1Tups = rel1Ref.getTuples();
        LinkedList<Tuple> rel2Tups = rel2Ref.getTuples(); 

        LinkedList<Tuple> newTups = new LinkedList<>();
        LinkedList<AttributeValue> rel1NewTup = new LinkedList<>();
        LinkedList<AttributeValue> rel2NewTup = new LinkedList<>();
        
        // build the new tuples that make up the temp joined table
        for (int i = 0; i < rel1Tups.size(); ++i)
        {
            rel1NewTup = new LinkedList<>(rel1Tups.get(i).getValues());

            for (int j = 0; j < rel2Tups.size(); ++j)
            {
                rel2NewTup = new LinkedList<>(rel2Tups.get(j).getValues());

                compareAttr(attrIndex1, attrIndex2, rel1NewTup, rel2NewTup, newTups);
            }
        }

        Relation finalRelation = new Relation(this.newRel, newSchema, true);
        finalRelation.setTuples(newTups);

        this.db.createRelation(finalRelation);
    }

    public void compareAttr(int attrIndex1, int attrIndex2, LinkedList<AttributeValue> rel1NewTup, LinkedList<AttributeValue> rel2NewTup, LinkedList<Tuple> newTups)
    {
        boolean flag;
        LinkedList<AttributeValue> newTup;

        switch (this.opp)
        {
            // check which operator is being used
            case "=": 
                if (isNumeric(rel1NewTup.get(attrIndex1).getValue()))
                    flag = Integer.parseInt(rel1NewTup.get(attrIndex1).getValue()) == Integer.parseInt(rel2NewTup.get(attrIndex2).getValue());
                else
                    flag = rel1NewTup.get(attrIndex1).getValue().equals(rel2NewTup.get(attrIndex2).getValue());
                
                break;
            case "!=":
                if (isNumeric(rel1NewTup.get(attrIndex1).getValue()))
                    flag = Integer.parseInt(rel1NewTup.get(attrIndex1).getValue()) != Integer.parseInt(rel2NewTup.get(attrIndex2).getValue());
                else
                    flag = !rel1NewTup.get(attrIndex1).getValue().equals(rel2NewTup.get(attrIndex2).getValue());
                
                break;
            case ">":
                flag = Integer.parseInt(rel1NewTup.get(attrIndex1).getValue()) > Integer.parseInt(rel2NewTup.get(attrIndex2).getValue());
                break;
            case "<":
                flag = Integer.parseInt(rel1NewTup.get(attrIndex1).getValue()) < Integer.parseInt(rel2NewTup.get(attrIndex2).getValue());
                break;
            case ">=":
                flag = Integer.parseInt(rel1NewTup.get(attrIndex1).getValue()) >= Integer.parseInt(rel2NewTup.get(attrIndex2).getValue());
                break;
            case "<=":
                flag = Integer.parseInt(rel1NewTup.get(attrIndex1).getValue()) <= Integer.parseInt(rel2NewTup.get(attrIndex2).getValue());
                break;
            default:
                flag = false;
        }

        if (flag)
                {
                    if (this.relAttr1.equals(this.relAttr2))
                    {
                        rel2NewTup.remove(attrIndex2);
                        newTup = new LinkedList<>();
                        newTup.addAll(rel1NewTup);
                        newTup.addAll(rel2NewTup);
                        newTups.add(new Tuple(newTup));
                    }
                    else
                    {
                        newTup = new LinkedList<>(rel1NewTup);
                        newTup.addAll(rel2NewTup);
                        newTups.add(new Tuple(newTup));
                    }
                }
    }

    private static boolean isNumeric(String numAsString) {
        try {
            Integer.parseInt(numAsString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override public String toString()
    {
        return "[rel1: " + this.rel1 + ", rel2: " + this.rel2 + ", relAttr1: " + this.relAttr1 + ", relAttr2: " + this.relAttr2 + ", opp: " + this.opp + "]";
    }   
}
