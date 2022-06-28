import java.util.LinkedList;

public class Tuple {
    private LinkedList<AttributeValue> values;  /* Values of each attribute in the tuple */

    public Tuple(LinkedList<AttributeValue> values) {
        this.values = values;
    }

    /* Returns the value of the specified attribute */
    public String getValue(String attributeName) {
        for (int i = 0; i < values.size(); i++) 
        {
            if (values.get(i).getName().equals(attributeName)) 
            {
                return values.get(i).getValue();
            }
        }

        return "ERROR";
    }

    public LinkedList<AttributeValue> getValues()
    {
        return this.values;
    }

    @Override public boolean equals(Object o)
    {
        if (o == this)
            return true;

        if (!(o instanceof Tuple))
            return false;

        Tuple tupObj = (Tuple) o;

        if (tupObj.getValues().size() != this.values.size())
            return false;

        LinkedList<AttributeValue> oAttrs = tupObj.getValues();
        
        for (int i = 0; i < oAttrs.size(); ++i)
        {
            if (!this.values.get(i).getName().equals(oAttrs.get(i).getName()) || !this.values.get(i).getValue().equals(oAttrs.get(i).getValue()))
                return false;
        }

        return true;
    }
}
