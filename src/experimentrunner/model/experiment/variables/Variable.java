package experimentrunner.model.experiment.variables;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import experimentrunner.model.experiment.values.DoubleValue;
import experimentrunner.model.experiment.values.IntValue;
import experimentrunner.model.experiment.values.Value;


public interface Variable {
	String getName();

	static Set<Variable> parseSet(String s) {
		if(s==null) return new HashSet<Variable>();
		Set<Variable> res = new HashSet<>();
		if(!s.contains("{")) res.add(VariableImpl.newInstance(s));
		else throw new Error();
		return res;
	}
	
	
}
