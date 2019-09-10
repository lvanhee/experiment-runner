package experiments.processing;

import java.util.HashMap;
import java.util.Map;

public class ProcessingUtils {

	public static Map<String, String> parseMap(String input) {
		Map<String, String> res = new HashMap<String, String>();
		input = input.replaceAll("\\}", "").replaceAll(" ", "").replaceAll("\\{", "");
		for(String s: input.substring(0, input.length()).split(","))
			res.put(s.split("=")[0].substring(0),s.split("=")[1]);
		return res;
	}

}
