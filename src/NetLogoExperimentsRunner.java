import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.codec.binary.StringUtils;
import org.nlogo.headless.HeadlessWorkspace;

public class NetLogoExperimentsRunner {
	
	private final HeadlessWorkspace workspace;
	
	private final List<String> outputToMonitor;
	

	private NetLogoExperimentsRunner(String string,//, List<String> inputParameters, 
			List<String> outputToMonitor
			) {
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

	public static NetLogoExperimentsRunner newInstance(String string,//, List<String> inputParameters, List<String> resultParameters
			List<String> outputToMonitor
			) {
		return new NetLogoExperimentsRunner(string,
				//,inputParameters, resultParameters
				outputToMonitor
				);
	}

	public Function<String, ExperimentSetup> getExperimentsParser() {
		return x->ExperimentSetup.parse(x.substring(0, x.indexOf(';')));
	}

	public Function<String, ExperimentResult> getExperimentResultsParser() {
		return x->ExperimentResult.parse(x.substring(x.indexOf(';')+1));
	}

	public ExperimentResult run(ExperimentSetup e) {
		for(String param:e.getParameters())
		{
			if(isNumeric(e.getValueFor(param)))
				workspace.command("set "+param+" "+e.getValueFor(param));
			else
				workspace.command("set "+param+" \""+e.getValueFor(param)+"\"");
				
		}
		workspace.command("setup");
	    workspace.command("repeat 500 [ go ]");
	    
	    Map<String, String> res= new HashMap<>();
	    for(String s: outputToMonitor)
	    	res.put(s, 	""+workspace.report(s));
	    
	    return ExperimentResult.newInstance(res);
		
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

}
