package experimentrunner.model.experimentrunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import experimentrunner.mains.ExperimentSetUtils;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ProcessingUtils;

public class ParallelWithinSubjectIteratingExperimentRunner implements ExperimentRunner
{
	
	private final Map<ExperimentSetup, ExperimentOutput> cache
	=new ConcurrentHashMap<ExperimentSetup, ExperimentOutput>();

	public ParallelWithinSubjectIteratingExperimentRunner(
			ExperimentVariableNetwork vars, 
			Variable variableToIterateOn,
			Supplier<WithinSubjectIteratingExperimentsRunner> supplier) {
		Set<ExperimentSetup> allSetups =
				ExperimentSetUtils.getAllSetups(vars);
		
		Set<Variable>nonIterationVariables = 
				vars.getInputVariables()
				.stream().filter(x->!x.equals(variableToIterateOn))
				.collect(Collectors.toSet());
		
		 Map<ExperimentSetup, Set<ExperimentSetup>> merged =
				 ExperimentSetUtils.getMergedSetupBy(allSetups, nonIterationVariables);
		 
		 merged.keySet()
		 .parallelStream()
		 .forEach(x->
		 {
			 WithinSubjectIteratingExperimentsRunner runner = supplier.get();
			 for(ExperimentSetup es: merged.get(x))
				 cache.put(es, runner.apply(es));
		 });
	}

	public static ExperimentRunner newInstance(ExperimentVariableNetwork vars,
			Variable variableToIterateOn,
			Supplier<WithinSubjectIteratingExperimentsRunner> supplier) {
		return new ParallelWithinSubjectIteratingExperimentRunner(vars, variableToIterateOn, supplier);
	}

	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		return cache.get(t);
	}

}
