package experimentrunner.model.experiment.values;

public interface Value {

	public static Value parse(String s) {
		if(!isNumeric(s))
			return StringValue.newInstance(s);
		throw new Error();
	}
	
	private static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

}
