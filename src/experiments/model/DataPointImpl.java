package experiments.model;


import experiments.executor.ExperimentRunner;

public class DataPointImpl implements DataPoint{
	private final Experiment experiment;
	private final ExperimentOutputImpl output;
	
	public DataPointImpl(Experiment experiment, ExperimentOutputImpl output) {
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

}
