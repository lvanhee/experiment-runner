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
	private final String preSetupCommand;
	
	public NetlogoProgramManager(Path file, String preSetupCommand,
			String setup, String go) {
		this.preSetupCommand = preSetupCommand;
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
		workspace.command(preSetupCommand);
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

	public static NetlogoProgramManager newInstance(Path path, String preSetupCommand, String string, String string2) {
		return new NetlogoProgramManager(path, preSetupCommand, string, string2);
	}
	public static NetlogoProgramManager newInstance(Path path) {
		return new NetlogoProgramManager(path, "", "", "");
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
