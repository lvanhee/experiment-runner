package experimentrunner.inout;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import experimentrunner.inout.FileReadWriter.FileFormat;
import experimentrunner.inout.FileReadWriter.FileOperation;
import experimentrunner.inout.explore.Search;
import experimentrunner.inout.explore.SearchOutput;
import experimentrunner.inout.model.ExperimentGoal;
import experimentrunner.inout.model.ExperimentGoal.ExperimentalGoalType;
import experimentrunner.inout.plotting.ResultPlotter;
import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.DataPointImpl;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentexecutor.ExperimentSetUtils;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public interface OutputGenerator {
	static void parse(ExperimentVariableNetwork vars, 
			MultiExperimentExecutor mee,
			JSONObject object) {
		
		
		ExperimentGoal eg = ExperimentGoal.parse(object.get("goal"));
		
		if(eg instanceof MaximizeVariableExperimentGoal)
		{
			SearchOutput so = SearchOutput.parse(object);
			outputMaximization(so, (MaximizeVariableExperimentGoal)eg, object,
					vars,mee);
		}
		else {
			FileFormat ot  = FileFormat.parse((String)object.get("type"));
			Path outputFolder = Paths.get((String)object.get("folder"));
			Set<ExperimentSetup> allSetups =  
					ExperimentSetUtils.getAllSetups(vars);

			Variable sortBy = VariableImpl.newInstance((String)object.get("sort-by"));
			Set<Variable> s = Variable.parseSet((String)object.get("merge-by"));

			Map<ExperimentSetup, Set<ExperimentSetup>> mergeSetupAltogether = 
					ExperimentSetUtils.getMergedSetupBy(allSetups, s);

			for(ExperimentSetup es: mergeSetupAltogether.keySet())
			{
				experimentAndSave(
						Paths.get(outputFolder.toString()+"/"+
								toFileFormat(""+es)+".csv"),
						mergeSetupAltogether.get(es),
						object,
						mee,
						ot, 
						vars);
			}
		}
		//throw new Error();
	}
	public static void outputMaximization(
			SearchOutput so, MaximizeVariableExperimentGoal eg, JSONObject object,
			ExperimentVariableNetwork network, MultiExperimentExecutor mee) {
		Search.performSearch( so,eg, object, network,
				mee);
	}
	static void experimentAndSave(Path p, Set<ExperimentSetup> es, JSONObject object, 
			ExperimentRunner computed,FileFormat ot,
			ExperimentVariableNetwork vars) {
		
		if(ot == FileFormat.CSV)
		{
			Set<DataPoint> points = 
					es.stream()
					.map((ExperimentSetup x)->DataPointImpl.newInstance(x, computed.apply(x)))
					.collect(Collectors.toSet());
			Variable sortBy = VariableImpl.newInstance((String)object.get("sort-by"));
			FileReadWriter.saveAs(points, p, ot, FileOperation.REPLACE_ALL,Optional.of(sortBy));
		return;
		}
		
		
		if(ot == FileFormat.TIKZ || ot == FileFormat.JFREECHART)
		{
			JSONArray plots = ((JSONArray) object.get("plot"));
			for(Object o: plots)
			{
				JSONObject plotInfo = (JSONObject)o;
				Variable xAxis = VariableImpl.newInstance((String)plotInfo.get("x"));
				Variable yAxis = VariableImpl.newInstance((String)plotInfo.get("y"));
				Variable lines = VariableImpl.newInstance((String)plotInfo.get("lines")); 

				Set<DataPoint> points = 
						es.stream().map(x->DataPointImpl.newInstance(x, computed.apply(x)))
						.collect(Collectors.toSet());
				if(ot==FileFormat.TIKZ)
					ToTikzPlot.exportToTikz(points, xAxis, yAxis, lines, vars);
				else if(ot== FileFormat.JFREECHART)
					ResultPlotter.plot(points, lines, xAxis, yAxis,"");
			}
			return;
		}
		throw new Error();
	}
	static String toFileFormat(String string) {
		String res = string.replaceAll("\\{", "").replaceAll("\\}", ""); 
		return res;
	}

}
