package experimentrunner.model.experiment.values;

public class IntValue implements Value {
	
	private final int val;
	private IntValue(int val)
	{
		this.val = val;
	}
	
	public static IntValue newInstance(int val)
	{
		return new IntValue(val);
	}
	
	public boolean equals(Object o)
	{
		return ((IntValue)o).val == val;
	}
	
	public int hashCode() {return val;}
	public int getValue() {
		return val;
	}
	
	public String toString()
	{
		return val+"";
	}
	
	

}
