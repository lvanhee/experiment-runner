package experimentrunner.model.experiment.variables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableImpl implements Variable {
	
	private final String name;

	public VariableImpl(String name2) {
		this.name = name2;
	}

	public String getName() {
		return name;
	}

	public String toString()
	{
		return name;
	}

	public static VariableImpl newInstance(String name) {
		return new VariableImpl(name);
	}
	
	public boolean equals(Object o) {return ((VariableImpl)o).name.equals(name);
	}
	public int hashCode()
	{
		return name.hashCode();
	}

}
