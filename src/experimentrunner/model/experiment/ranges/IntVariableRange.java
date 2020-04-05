package experimentrunner.model.experiment.ranges;

import java.util.ArrayList;
import java.util.List;

import experimentrunner.model.experiment.values.IntValue;
import experimentrunner.model.experiment.values.Value;


public class IntVariableRange implements NumericVariableRange {
	
	private final int start;
	private final int step;
	private final int end;

	private IntVariableRange(int start, int step, int end) {
		this.start = start;
		this.step = step;
		this.end = end;
	}

	static VariableRange newInstance(int start, int step, int end) {
		return new IntVariableRange(start,step,end);
	}

	@Override
	public List<Value> getValues() {
		List<Value> res = new ArrayList<Value>(); 
		for(int i = start; i <= end ; i+=step)
			res.add(IntValue.newInstance(i));
		return res;
	}
	
	public String toString()
	{
		return "["+start+" "+step+" "+end+"]";
	}

}
