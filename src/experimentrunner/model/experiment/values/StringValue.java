package experimentrunner.model.experiment.values;

public class StringValue implements Value {
	
	private final String s;

	private StringValue(String s) {
		this.s = s;
	}
	
	public int hashCode() {return s.hashCode();}
	public boolean equals(Object o) { return ((StringValue)o).s.equals(s);};
	public String toString() {return s;};

	public static Value newInstance(String s) {
		return new StringValue(s);
	}
}
