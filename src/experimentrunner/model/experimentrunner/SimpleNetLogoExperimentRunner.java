package experimentrunner.model.experimentrunner;

import java.nio.file.Path;
import java.util.Set;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.modules.netlogo.NetlogoProgramManager;

public class SimpleNetLogoExperimentRunner implements NetlogoExperimentRunner{
	private final NetlogoProgramManager nlmp;
	private final Set<Variable>outputVariables;
	public SimpleNetLogoExperimentRunner(Path file, Set<Variable>outputVariables, 
			String preSetup
			)
	{
		nlmp = NetlogoProgramManager.newInstance(
				file,
				preSetup, "",""
				);
		this.outputVariables = outputVariables;
	}

	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		/*nlmp.setup(t.getVariableAllocation());
		return nlmp.getResult(outputVariables);*/
		throw new Error();
	}

	public static ExperimentRunner newInstance(Path file, Set<Variable>outputVariables, String preSetup) {
		return new SimpleNetLogoExperimentRunner(file, outputVariables, preSetup);
	}

}
