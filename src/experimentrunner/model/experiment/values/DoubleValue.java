package experimentrunner.model.experiment.values;

public class DoubleValue implements NumericValue {

	private final double val;
	
	private DoubleValue(double d)
	{
		this.val = d;
	}
	
	public static Value newInstance(double d) {
		return new DoubleValue(d);
	}
	
	public String toString()
	{
		return val+"";
	}
	
	public int hashCode()
	{
		return new Double(val).hashCode();
	}
	
	public boolean equals(Object o)
	{
		return ((DoubleValue)o).val == val;
	}

	public double getValue() {
		return val;
	}

}
