package experimentrunner.mains;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import experimentrunner.inout.MultiExperimentExecutor;
import experimentrunner.inout.OutputGenerator;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class FileBasedMain {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
	//	try {
		if(args.length<1) {
			System.err.println("Indicate the name of the json file to process as a first parameter");
		}
		File f = new File (args[0]);
		JSONParser parser = new JSONParser();
		JSONObject head = (JSONObject) parser.parse(new FileReader(f)); 
		
		ExperimentVariableNetwork network = 
				ExperimentVariableNetwork.parse((JSONObject)head.get("variables"));
		
		MultiExperimentExecutor mee = 
				MultiExperimentExecutor.parse((JSONObject)head.get("executor"), network);
		
		OutputGenerator.parse(network,mee,(JSONObject)head.get("output"));
		
		System.out.println("Task completed");
		System.exit(0);
		}
	/*	catch(Exception e)
		{
			e.printStackTrace();
			if(e.getLocalizedMessage().startsWith("Can't find extension:"))
				System.out.println("Extension folder not found, need to add as VM parameter: -Dnetlogo.extensions.dir=\"paths/to/NetLogo 6.X.X/extensions\"");
			throw new Error();
		}*/
		/*try {
			

			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
			JSONObject jsonObject = (JSONObject) obj;

			// A JSON array. JSONObject supports java.util.List interface.
			JSONArray companyList = (JSONArray) jsonObject.get("Company List");

			// An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
			// Iterators differ from enumerations in two ways:
			// 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
			// 2. Method names have been improved.
			Iterator<JSONObject> iterator = companyList.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
