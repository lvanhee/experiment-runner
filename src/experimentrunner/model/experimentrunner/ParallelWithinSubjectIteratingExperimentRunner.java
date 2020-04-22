package experimentrunner.model.experimentrunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import experimentrunner.inout.record.FileBaseDatabase;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.ExperimentVariableNetwork;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ExperimentSetUtils;
import experimentrunner.model.experimentexecutor.ProcessingUtils;

public class ParallelWithinSubjectIteratingExperimentRunner implements ExperimentRunner
{
	private final FileBaseDatabase dataBase;
	public ParallelWithinSubjectIteratingExperimentRunner(
			ExperimentVariableNetwork vars, 
			Variable variableToIterateOn,
			Supplier<WithinSubjectIteratingExperimentsRunner> supplier,
			FileBaseDatabase fileBaseDatabase) {
		Set<ExperimentSetup> allSetups =
				ExperimentSetUtils.getAllSetups(vars);
		this.dataBase = fileBaseDatabase;
		
		Set<Variable>nonIterationVariables = 
				vars.getInputVariables()
				.stream().filter(x->!x.equals(variableToIterateOn))
				.collect(Collectors.toSet());
		
		 Map<ExperimentSetup, Set<ExperimentSetup>> merged =
				 ExperimentSetUtils.getMergedSetupBy(allSetups, nonIterationVariables);
		 
		 ConcurrentLinkedQueue<WithinSubjectIteratingExperimentsRunner> runners 
		 = new ConcurrentLinkedQueue<WithinSubjectIteratingExperimentsRunner>();

		 merged.keySet()
		 .parallelStream()
		 .forEach(x->
		 {
			 WithinSubjectIteratingExperimentsRunner runner = null;
			 while(runner==null)
			 {
				 if(runners.isEmpty())
				 {
					 runners.add(supplier.get());
					 System.out.println("Created runner");
				 }
				 runner = runners.poll();
			 }
			
			 Set<ExperimentSetup> iteratedExperiments = 
					 merged.get(x);
			 for(ExperimentSetup es: iteratedExperiments)
				 if(!dataBase.hasAlreadyBeenProcessed(es))
					 dataBase.add(es, runner.apply(es));
			 runners.add(runner);
		 });
		 runners.parallelStream().forEach(x->x.terminate());
	}

	public static ExperimentRunner newInstance(ExperimentVariableNetwork vars,
			Variable variableToIterateOn,
			Supplier<WithinSubjectIteratingExperimentsRunner> supplier, FileBaseDatabase fileBaseDatabase) {
		return new ParallelWithinSubjectIteratingExperimentRunner(vars, variableToIterateOn, supplier, fileBaseDatabase);
	}

	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		return dataBase.getResult(t);
	}

}
