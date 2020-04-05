package experimentrunner.modules.netlogo;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nlogo.headless.HeadlessWorkspace;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentOutputImpl;
import experimentrunner.model.experiment.values.NumericValue;
import experimentrunner.model.experiment.values.StringValue;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;

public class NetlogoProgramManager
{
	private final HeadlessWorkspace workspace;
	private final String goCommand;
	private final String setupCommand;
	
	public NetlogoProgramManager(Path file, String setup, String go) {
		workspace = HeadlessWorkspace.newInstance();
		this.setupCommand = setup;
		this.goCommand = go;
		try {
			workspace.open(
					file.toString(),true);
		}
		catch(java.io.IOException ex) {
			ex.printStackTrace();
		};
	}
	
	public void setup(Map<Variable,Value> setupAllocations)
	{
		
		for(Variable v:setupAllocations.keySet())
		{
			
			Value val = setupAllocations.get(v);
			if(v.getName().equals("ticks")) {
				continue;
			}
			if(val instanceof NumericValue)
			{
				workspace.command("set "+v+" "+val);
				continue;
			}
			if(val instanceof StringValue)
			{
				workspace.command("set "+v+" \""+val+"\"");
				continue;
			}
			throw new Error();
		}
		workspace.command(setupCommand);
	}

	public static NetlogoProgramManager newInstance(Path path, String string, String string2) {
		return new NetlogoProgramManager(path, string, string2);
	}

	public int getCurrentTick() {
		Object ret = workspace.report("ticks");
		
		return (int)Math.round(Double.parseDouble(""+ret));
	}

	public ExperimentOutput getResult(Set<Variable> outputVariables) {
		  Map<Variable, Value> res= new HashMap<>();
		    for(Variable s: outputVariables)
		    	res.put(s, 	Variable.parse(""+workspace.report(s.toString())));
		    
		    
		    return ExperimentOutputImpl.newInstance(res);
	}

	public void go() {
		workspace.command(goCommand);
	}

}
