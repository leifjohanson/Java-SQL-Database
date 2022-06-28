
public class AttributeValue {
    private String name; 	/* name of the attribute */
	private String value;   /* value of the attribute */

	/* Needs appropriate accessor and mutator methods */

    public void setName(String name) {

        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}
