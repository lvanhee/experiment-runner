package experimentrunner.inout;

public interface ValueSelection {
	
	public enum SimpleValueSelection implements ValueSelection{ALL_VALUES}

	static ValueSelection parse(Object object) {
		if(object instanceof String)
		{
			String input = (String) object;
			if(input.equals("all-values"))
				return SimpleValueSelection.ALL_VALUES;
		}
		throw new Error();
	}

}
