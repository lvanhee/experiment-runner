package experimentrunner.modules.netlogo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import org.json.simple.JSONObject;
import org.nlogo.headless.HeadlessWorkspace;

import experimentrunner.inout.FileReadWriter;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentOutputImpl;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class NetLogoExperimentsRunner implements ExperimentRunner{
	

	private final Optional<String> terminationCommand;
	private final Set<Variable> outputToMonitor;

	private NetLogoExperimentsRunner(String fileName,//, List<String> inputParameters, 
			Set<Variable> outputToMonitor,
			Optional<String> terminationCommand
			) {
		this.terminationCommand = terminationCommand;
		this.outputToMonitor = outputToMonitor;
	}

	public static NetLogoExperimentsRunner newInstance(
			String fileName,//, List<String> inputParameters, List<String> resultParameters
			Set<Variable> outputToMonitor,
			Optional<String> tc
			) {
		return new NetLogoExperimentsRunner(fileName,
				//,inputParameters, resultParameters
				outputToMonitor,tc
				);
	}

	public Function<String, ExperimentSetup> getExperimentsParser() {
		throw new Error();
		//return x->FileReadWriter.parseMap(x.substring(0, x.indexOf(';')));
	}

	public Function<String, ExperimentSetup> getExperimentResultsParser() {
		throw new Error();
		//return x->ExperimentResult.parse(x.substring(x.indexOf(';')+1));
	}

	public ExperimentOutput apply(ExperimentSetup e) {
		/*System.out.print("Running:"+e+" "+getDate());
		
		
		if(terminationCommand.isPresent())
	    while(!workspace.report(terminationCommand.get()).toString().replaceAll(" ", "").equals("true"))
	    {
	    	workspace.command("go");
	    }
	    
	  
	    System.out.println(result);
	    return result;*/
		throw new Error();
		
	}
	
	private String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static boolean isNumeric(Object str)  
	{  
		if(str instanceof String)
		{
			try  
			{  
				double d = Double.parseDouble((String) str);  
			}  
			catch(NumberFormatException nfe)  
			{  
				return false;  
			}  
			return true;  
		}else throw new Error();
	}

	public static ExperimentRunner parse(JSONObject jsonObject) {
		throw new Error();
	}

}
