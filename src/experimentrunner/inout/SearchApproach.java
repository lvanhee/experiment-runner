package experimentrunner.inout;

public interface SearchApproach {
	
	public enum SimpleSearchApproach implements SearchApproach
	{
		ONE_VARIABLE_AT_A_TIME
	}

	static SearchApproach parse(Object object) {
		if(object instanceof String)
		{
			String input = (String) object;
			if(input.equals("one-variable-at-a-time"))
				return SimpleSearchApproach.ONE_VARIABLE_AT_A_TIME;
		}
		throw new Error();
	}

}
