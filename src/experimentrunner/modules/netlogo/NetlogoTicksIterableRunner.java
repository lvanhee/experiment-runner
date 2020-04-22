package experimentrunner.modules.netlogo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.values.IntValue;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentrunner.IterableExperimentRunner;
import experimentrunner.model.experimentrunner.NetlogoExperimentRunner;

public class NetlogoTicksIterableRunner implements NetlogoExperimentRunner, IterableExperimentRunner {
	
	private static final Variable TICKS_VARIABLE =	VariableImpl.newInstance("ticks");
	
	private final NetlogoProgramManager nlmp;
	private ExperimentSetup previous = null;
	
	private final Set<Variable> outputVariables;
	private final Set<Variable> preSetupVariables;
		private final Set<Variable> postSetupVariables;

	private NetlogoTicksIterableRunner(NetlogoProgramManager nlmp,
			Set<Variable> outputVariables,
			Set<Variable> preSetupVariables,
			Set<Variable> postSetupVariables)
	{
		this.nlmp = nlmp;		
		this.outputVariables = outputVariables;
		this.preSetupVariables = preSetupVariables;
		this.postSetupVariables = postSetupVariables;
	}
	public static NetlogoTicksIterableRunner newInstance(
			JSONObject jsonObject,
			Path file,
			Set<Variable> outputVariables) {
		NetlogoProgramManager nlmp = NetlogoProgramManager.newInstance(
				file,
				"",
				((String)jsonObject.get("setup")),
				((String)jsonObject.get("go"))
				);
		
		
		return new NetlogoTicksIterableRunner(nlmp, outputVariables,
				Variable.parseSet((String)jsonObject.get("presetup-variables")),
				Variable.parseSet((String)jsonObject.get("postsetup-variables"))
				);
	}
	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		System.out.println("Running:"+t);
		if(previous==null || ! ExperimentSetup.isAllEqualExceptFor(previous,t,TICKS_VARIABLE))
		{
			Map<Variable, Value> presetupAllocation = t.getVariableAllocation()
					.keySet().stream()
					.filter(x->preSetupVariables.contains(x))
					.collect(Collectors.toMap(Function.identity(),
							x->t.getVariableAllocation().get(x)));
			Map<Variable, Value> postSetupAllocation = t.getVariableAllocation()
					.keySet().stream()
					.filter(x->postSetupVariables.contains(x))
					.collect(Collectors.toMap(Function.identity(),
							x->t.getVariableAllocation().get(x)));
			Set<Variable> allVariables = new HashSet<Variable>();
			allVariables.addAll(presetupAllocation.keySet());
			allVariables.addAll(postSetupAllocation.keySet());
			if(t.getVariableAllocation().containsKey(VariableImpl.newInstance("ticks")))
				allVariables.add(VariableImpl.newInstance("ticks"));
			if(!allVariables.equals(t.getVariableAllocation().keySet()))
				throw new Error();
			nlmp.setup(presetupAllocation, postSetupAllocation);
		}
		
		final int requestedTick = ((IntValue)t.getVariableAllocation().get(TICKS_VARIABLE)).getValue();
		
		if(requestedTick < nlmp.getCurrentTick())throw new Error();
		
		if(t.getVariableAllocation().containsKey(TICKS_VARIABLE))
			while(nlmp.getCurrentTick() <
					requestedTick)
				nlmp.go();
		previous = t;
		return nlmp.getResult(outputVariables);
	}
	@Override
	public void iterate() {
		throw new Error();
	}
	@Override
	public void terminate() {
		nlmp.terminate();
	}

}
