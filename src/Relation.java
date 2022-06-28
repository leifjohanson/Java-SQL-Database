import java.util.LinkedList;

public class Relation {
    private String name; /* name of the relation */
	private LinkedList<Attribute> schema;	/* Schema of the relation */
	private LinkedList<Tuple> tuples;	/* Tuples stored on the relation */
	final int paddingWidth = 2;
	private boolean temp = false;

	public Relation(String name, LinkedList<Attribute> schema)
	{
		this.name = name;
		this.schema = schema;

		this.tuples = new LinkedList<>();
	}

	public Relation(String name, LinkedList<Attribute> schema, boolean temp)
	{
		this.name = name;
		this.schema = schema;
		this.temp = temp;

		this.tuples = new LinkedList<>();
	}

	public String getName()
	{
		return this.name;
	}

	public LinkedList<Attribute> getSchema()
	{
		return this.schema;
	}

	public LinkedList<Tuple> getTuples() {
		return this.tuples;
	}

	public void setTuples (LinkedList<Tuple> tuples) {
		this.tuples = tuples;
	}

	public boolean isTemp()
	{
		return this.temp;
	}

	/* Formats and prints the relation's name, schema, and tuples */
	public void print() {
		
		Attribute currAtt;
		int totalLen;
		int innerLength = 0;
		int numOfVertBars = -1;  

		for (int i = 0; i < schema.size(); ++i)
		{
			currAtt = schema.get(i);

			innerLength += determineLength(currAtt);
			innerLength += paddingWidth;
			numOfVertBars++; 
		}

		totalLen = innerLength + numOfVertBars + paddingWidth;

		printTitle(this.name, totalLen);
		printSchema(totalLen);
		printTuples(totalLen);
	}

	/* Adds the specified tuple to the relation */
    public void insert(Tuple tuple) {
		tuples.add(tuple);
    }

	/* Remove all tuples from the relation */
	public void delete() {
		tuples = new LinkedList<>();
	}

	public void removeElement(String eleName)
	{
		for (int i = 0; i < tuples.size(); ++i)
		{
			if (tuples.get(i).getValue("RELATION").equals(eleName))
			{
				tuples.remove(i);
				return;
			}
		}
	}

	private void printStars(int nums)
	{
		for (int i = 0; i < nums; ++i)
		{
			System.out.print("*");
		}
		System.out.println();
	}

	private void printLines(int nums)
	{
		for (int i = 0; i < nums; ++i)
		{
			System.out.print("-");
		}
		System.out.println();
	}

	private int determineLength(Attribute attr)
	{
		int attrNameLen = attr.getName().length();
		int attrLen = attr.getLength();

		if (attrNameLen > attrLen) 
		{
			return attrNameLen;
		} 
		else 
		{
			return attrLen;
		}
	}

	private void printTitle(String title, int totalLen)
	{
		printStars(totalLen);

		String printString = "| %-" + Integer.toString(totalLen-4) + "s |\n";
		System.out.printf(printString, title);

		printLines(totalLen);
	}

	private void printSchema(int totalLen)
	{
		Attribute currAttr;
		String printString;
		int length;

		System.out.print("|");

		for (int i = 0; i < schema.size(); ++i)
		{
			currAttr = schema.get(i);
			length = determineLength(currAttr);
			printString = " %-" + Integer.toString(length) + "s |";

			System.out.printf(printString, currAttr.getName());
		}
		
		System.out.println();
		printLines(totalLen);
	}

	private void printTuples(int totalLen)
	{
		Tuple currTup;
		String currVal;
		String printString;
		int length;

		for (int i = 0; i < tuples.size(); ++i)
		{
			currTup = tuples.get(i);
			System.out.print("|");

			for (int j = 0; j < schema.size(); ++j)
			{
				currVal = currTup.getValue(schema.get(j).getName());
				length = determineLength(schema.get(j));
				printString = " %-" + Integer.toString(length) + "s |";

				System.out.printf(printString, currVal);
			}

			System.out.println();
		}

		printStars(totalLen);
	}

}
