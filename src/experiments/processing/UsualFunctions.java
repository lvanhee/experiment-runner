package experiments.processing;

import java.util.Set;
import java.util.function.BiPredicate;

import experiments.model.Experiment;

public class UsualFunctions {
	public static boolean isSimplerInstanceRejected(
			Experiment x,
			Set<Experiment> rejectedInstances,
			BiPredicate<Experiment, Experiment> isSimpler) {
		return rejectedInstances
				.stream()
				.anyMatch(y->isSimpler.test(y,x));			
	}
}
