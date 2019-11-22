package netlogorunner;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import org.nlogo.headless.HeadlessWorkspace;

import experiments.inout.FileReadWriter;
import experiments.model.Experiment;
import experiments.model.ExperimentOutput;
import experiments.model.ExperimentOutputImpl;
import experiments.model.experimentRunner.ExperimentRunner;

public class NetLogoExperimentsRunner implements ExperimentRunner{
	
	private final HeadlessWorkspace workspace;
	
	private final List<String> outputToMonitor;
	private final Optional<String> tc;
	

	private NetLogoExperimentsRunner(String string,//, List<String> inputParameters, 
			List<String> outputToMonitor,
			Optional<String> terminationCommand
			) {
		this.tc = terminationCommand;
		workspace = HeadlessWorkspace.newInstance();
		this.outputToMonitor = outputToMonitor; 
		try {
			workspace.open(
					string);
		}
		catch(java.io.IOException ex) {
			ex.printStackTrace();
		};
		}

	public static NetLogoExperimentsRunner newInstance(
			String string,//, List<String> inputParameters, List<String> resultParameters
			List<String> outputToMonitor,
			Optional<String> tc
			) {
		return new NetLogoExperimentsRunner(string,
				//,inputParameters, resultParameters
				outputToMonitor,tc
				);
	}

	public Function<String, Experiment> getExperimentsParser() {
		throw new Error();
		//return x->FileReadWriter.parseMap(x.substring(0, x.indexOf(';')));
	}

	public Function<String, Experiment> getExperimentResultsParser() {
		throw new Error();
		//return x->ExperimentResult.parse(x.substring(x.indexOf(';')+1));
	}

	public ExperimentOutput apply(Experiment e) {
		System.out.print("Running:"+e+" "+getDate());
		for(String param:e.getInputMap().keySet())
		{
			if(isNumeric(e.getInputMap().get(param)))
				workspace.command("set "+param+" "+e.getInputMap().get(param));
			else
				workspace.command("set "+param+" \""+e.getInputMap().get(param)+"\"");
				
		}
		workspace.command("setup");
		
		if(tc.isPresent())
	    while(!workspace.report(tc.get()).toString().replaceAll(" ", "").equals("true"))
	    {
	    	workspace.command("go");
	    }
	    
	    Map<String, String> res= new HashMap<>();
	    for(String s: outputToMonitor)
	    	res.put(s, 	""+workspace.report(s));
	    
	    
	    ExperimentOutputImpl result = ExperimentOutputImpl.newInstance(res);
	    System.out.println(result);
	    return result;
		
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

}
