package experimentrunner.model.experimentrunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.json.simple.JSONObject;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.ranges.VariableRange;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ExperimentSetUtils;
import experimentrunner.modules.netlogo.NetLogoExperimentsRunner;

public class WithinSubjectIteratingExperimentsRunner implements ExperimentRunner{
	
	private final ExperimentVariableNetwork vars;
	private final IterableExperimentRunner runner;
	private final Variable v;
	private final VariableRange r;
	
	private final Map<ExperimentSetup, ExperimentOutput> cache
	= new HashMap<ExperimentSetup, ExperimentOutput>(); 
	private WithinSubjectIteratingExperimentsRunner(
			ExperimentVariableNetwork network,
			IterableExperimentRunner runner,
			Variable v,
			VariableRange r
			)
	{
		this.vars = network;
		this.runner = runner;
		this.v = v;
		this.r = r;
	}
	

	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		if(cache.containsKey(t))return cache.get(t);
		
		List<ExperimentSetup> setups = ExperimentSetUtils.getAllSetupsSortedByIterationsOf(t,v,r);
		for(ExperimentSetup es:setups)
		{
			cache.put(es, runner.apply(es));
		}
		
		return cache.get(t);
	}



	public static WithinSubjectIteratingExperimentsRunner 
	newInstance(ExperimentVariableNetwork vars2, IterableExperimentRunner runner,
			Variable variableToIterate, VariableRange rangetoIterate) {
		return new WithinSubjectIteratingExperimentsRunner(
				vars2, 
				runner, 
				variableToIterate, 
				rangetoIterate);
	}


	public void terminate() {
		runner.terminate();
	}
	

}
