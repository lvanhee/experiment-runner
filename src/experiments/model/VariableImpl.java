package experiments.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableImpl implements Variable {
	
	private final String name;
	private final List<Object> values;

	public VariableImpl(String name2, List<Object> values2) {
		this.name = name2;
		this.values = values2;
	}

	public static VariableImpl newInstance(String name, double min, double step, double max) {
		List<Object> values = new ArrayList<>();
		for(double i = min; i <= max; i += step)
			values.add(""+i);
		return new VariableImpl(name, values);
	}

	public String getName() {
		return name;
	}

	public List<Object> getValues() {
		return values;
	}
	
	public String toString()
	{
		return name+":"+values;
	}

	public static VariableImpl newInstance(String string, List<Object> asList) {
		return new VariableImpl(string, asList);
	}

	public static VariableImpl newInstance(String name, Object value) {
		return newInstance(name, Arrays.asList(value));
	}

}
