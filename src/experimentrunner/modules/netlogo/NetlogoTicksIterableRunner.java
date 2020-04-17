package experimentrunner.modules.netlogo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.json.simple.JSONObject;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.values.IntValue;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experiment.variables.VariableImpl;
import experimentrunner.model.experimentrunner.IterableExperimentRunner;
import experimentrunner.model.experimentrunner.NetlogoExperimentRunner;

public class NetlogoTicksIterableRunner implements NetlogoExperimentRunner, IterableExperimentRunner {
	
	private static final Variable TICKS_VARIABLE =	VariableImpl.newInstance("ticks");
	
	private final NetlogoProgramManager nlmp;
	private ExperimentSetup previous = null;
	
	private final Set<Variable> outputVariables;

	private NetlogoTicksIterableRunner(NetlogoProgramManager nlmp, Set<Variable> outputVariables)
	{
		this.nlmp = nlmp;		
		this.outputVariables = outputVariables;
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
		return new NetlogoTicksIterableRunner(nlmp, outputVariables);
	}
	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		System.out.println("Running:"+t);
		if(previous==null || ! ExperimentSetup.isAllEqualExceptFor(previous,t,TICKS_VARIABLE))
		{
		nlmp.setup(t.getVariableAllocation());
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

}
