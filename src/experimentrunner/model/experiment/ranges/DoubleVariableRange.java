package experimentrunner.model.experiment.ranges;

import java.util.ArrayList;
import java.util.List;

import experimentrunner.model.experiment.values.DoubleValue;
import experimentrunner.model.experiment.values.Value;

public class DoubleVariableRange implements VariableRange{
	
	private final double min;
	private final double step;
	private final double max;
	
	private DoubleVariableRange(double min, double step, double max) {
		this.min = min;
		this.step = step;
		this.max = max;
	}

	public static DoubleVariableRange newInstance(double min, double step, double max) {
		return new DoubleVariableRange(min, step,max);
	}

	public List<Value> getValues() {
		List<Value> values = new ArrayList<>();
		for(double i = min; i <= max; i += step)
		{
			values.add(DoubleValue.newInstance(i));
		}
		return values;
	}
	
	public String toString()
	{
		return "["+min+","+step+","+max+"]";
	}
	
	public boolean equals(Object o)
	{
		return getValues().equals(((VariableRange)o).getValues());
	}

	public static DoubleVariableRange parse(String string) {
		String tmp = string.substring(string.indexOf("[")+1, string.indexOf("]"));
		String[]split = tmp.split(" ");
		return newInstance(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
	}

}
