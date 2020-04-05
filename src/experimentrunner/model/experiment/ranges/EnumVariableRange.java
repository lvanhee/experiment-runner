package experimentrunner.model.experiment.ranges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import experimentrunner.model.experiment.values.Value;

public class EnumVariableRange implements VariableRange {

	private final List<Value> values;
	public EnumVariableRange(List<Value> values) {
		this.values = values;
	}

	public static VariableRange parse(String string) {
		string = string.substring(1, string.length()-1);
		String[] split = string.split(" ");
		List<Value>	values = Arrays.asList(split)
				.stream()
				.map(x->Value.parse(x))
				.collect(Collectors.toList());
		return new EnumVariableRange(values);
	}

	@Override
	public List<Value> getValues() {
		return values;
	}
	
	public String toString() {return values.toString();}

}
