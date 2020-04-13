package experimentrunner.model.experiment.ranges;

import java.util.List;
import java.util.Set;

import experimentrunner.model.experiment.values.Value;

public interface VariableRange {

	static VariableRange parse(String string) {
		if(string.startsWith("[") && string.endsWith("]"))
			return NumericVariableRange.parse(string);
		if(string.startsWith("{"))
			return EnumVariableRange.parse(string);
		throw new Error();
	}

	List<Value> getValues();

	Value aRandomValue();

}
