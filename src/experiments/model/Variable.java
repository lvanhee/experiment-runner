package experiments.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Variable {
	
	private final String name;
	private final List<String> values;

	public Variable(String name2, List<String> values2) {
		this.name = name2;
		this.values = values2;
	}

	public static Variable newInstance(String name, double min, double step, double max) {
		List<String> values = new ArrayList<>();
		for(double i = min; i <= max; i += step)
			values.add(""+i);
		return new Variable(name, values);
	}

	public String getName() {
		return name;
	}

	public List<String> getValues() {
		return values;
	}
	
	public String toString()
	{
		return name+":"+values;
	}

	public static Variable newInstance(String string, List<String> asList) {
		return new Variable(string, asList);
	}

	public static Variable newInstance(String name, String value) {
		return newInstance(name, Arrays.asList(value));
	}

}
