/**
 * 
 */
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
	
	exports experiments.model;
	exports experiments.inout;
	exports experiments.model.experimentRunner;
	exports experiments.processing;
	exports experiments.model.explorationstrategies;
}