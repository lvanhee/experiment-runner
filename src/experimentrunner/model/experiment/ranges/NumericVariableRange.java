package experimentrunner.model.experiment.ranges;

public interface NumericVariableRange extends VariableRange {

	public static VariableRange parse(String string) {
		String tmp = string.replaceAll("\\[", "").replaceAll("\\]",""); 
		String split[] = tmp.split(" ");
		if(isInteger(split[0])&&isInteger(split[1])&&isInteger(split[2]))
		{
			return IntVariableRange.newInstance(Integer.parseInt(split[0]),
					Integer.parseInt(split[1]),
					Integer.parseInt(split[2]));
		}
		return DoubleVariableRange.newInstance(
				Double.parseDouble(split[0]),
				Double.parseDouble(split[1]),
				Double.parseDouble(split[2]));
				
	}
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}

}
