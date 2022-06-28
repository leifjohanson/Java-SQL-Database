import java.util.LinkedList;

public class SurlyDatabase {
	/* Collection of relations in the database */
	private LinkedList<Relation> relations;

	public SurlyDatabase()
	{
		relations = new LinkedList<>();
	}

	/* Returns the relation with the specified name */
	public Relation getRelation(String name) {
		
		Relation currRel;
		
		for (int i = 0; i < relations.size(); ++i)
		{
			currRel = relations.get(i);
			if (currRel.getName().equals(name))
			{
				return currRel;
			}
		}
		
		return null;
	}

	/* Removes the relation with the specified name from the database */
    public void destroyRelation(String name) {
		Relation currRel = getRelation(name);
		relations.remove(currRel);
    }

	/* Adds the given relation to the database */
	public void createRelation(Relation relation) {
		relations.add(relation);
	}
}
