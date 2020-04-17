package experimentrunner.model.experiment.values;

public interface NumericValue extends Value {

	public static double toDouble(Value z) {
		if(z instanceof IntValue)
			return ((IntValue)z).getValue();
		if(z instanceof DoubleValue)
			return ((DoubleValue)z).getValue();
		throw new Error();
	}

}
