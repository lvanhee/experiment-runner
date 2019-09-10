package experiments.model;


import java.util.Set;
import java.util.stream.Collectors;

import experiments.model.experimentRunner.ExperimentRunner;

public class DataPointImpl implements DataPoint{
	private final Experiment experiment;
	private final ExperimentOutput output;
	
	public DataPointImpl(Experiment experiment, ExperimentOutput output) {
		this.experiment = experiment;
		this.output = output;
	}

	public static DataPointImpl parse(String x) {
		String start = x.substring(1, x.indexOf("}"));
		String end = x.substring(x.indexOf("{",1), x.length()-1);
		return DataPointImpl.newInstance(ExperimentImpl.parseString(start),
				ExperimentOutputImpl.parse(end));
	}

	private static DataPointImpl newInstance(Experiment experiment, ExperimentOutputImpl output) {
		return new DataPointImpl(experiment, output);
	}

	@Override
	public Experiment getExperiment() {
		return experiment;
	}

	@Override
	public ExperimentOutput getExperimentOutput() {
		return output;
	}
	
	public String toString()
	{
		return experiment.toString()+":"+output.toString();
	}

	public static DataPoint newInstance(Experiment expe, ExperimentOutput out) {
		return new DataPointImpl(expe, out);
	}

	public static Set<DataPoint> from(ExperimentSeries bestSoFar, ExperimentRunner er) {
		return bestSoFar.getSetups().stream()
		.map(x->DataPointImpl.newInstance(x,er.apply(x)))
		.collect(Collectors.toSet());
	}

}
