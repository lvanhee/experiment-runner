/**
 * @author vanhee
 *
 */
module experiment_runner {	
	requires netlogo;
	requires parboiled;
	requires java.desktop;
	requires jfreechart;
	requires jcommon;
	requires json.simple;
	exports experimentrunner.model.experiment.variables;
	exports experimentrunner.model.experiment.values;
	exports experimentrunner.model.experiment.ranges;
	exports experimentrunner.model.experimentrunner;
	exports experimentrunner.model.experiment.data;
}