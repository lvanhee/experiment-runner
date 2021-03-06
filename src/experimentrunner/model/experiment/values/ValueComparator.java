package experimentrunner.model.experiment.values;

import java.util.Comparator;

public enum ValueComparator implements Comparator<Value> {INSTANCE;

	@Override
	public int compare(Value o1, Value o2) {
		if(o1 instanceof DoubleValue && o2 instanceof DoubleValue)
			return Double.compare(((DoubleValue)o1).getValue(),
					((DoubleValue)o2).getValue());
		if(o1 instanceof IntValue && o2 instanceof IntValue)
			return Integer.compare(((IntValue)o1).getValue(),
					((IntValue)o2).getValue());
		throw new Error();
		
	}

}
