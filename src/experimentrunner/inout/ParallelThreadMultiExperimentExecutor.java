package experimentrunner.inout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import experimentrunner.inout.record.FileBaseDatabase;
import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experimentrunner.ExperimentRunner;

public class ParallelThreadMultiExperimentExecutor implements MultiExperimentExecutor{

	private final Supplier<ExperimentRunner>er;
	private final boolean verbose;
	private final boolean parallelOps;
	private final FileBaseDatabase db;
	ConcurrentLinkedQueue<ExperimentRunner> runners = new ConcurrentLinkedQueue<ExperimentRunner>();
	public ParallelThreadMultiExperimentExecutor(Supplier<ExperimentRunner> er,boolean verbose, boolean parallelOps,
			FileBaseDatabase fileBaseDatabase) {
		this.er = er;
		this.verbose = verbose;
		this.parallelOps = parallelOps;
		db = fileBaseDatabase;
	}

	@Override
	public Map<ExperimentSetup, ExperimentOutput> apply(Set<ExperimentSetup> t) {
		if(parallelOps)
			return
				t.parallelStream()
				.collect(Collectors.toMap(Function.identity(), x->apply(x)));
		else
			return
				t.stream()
				.collect(Collectors.toMap(Function.identity(), x->apply(x)));
	}

	public static MultiExperimentExecutor newInstance(Supplier<ExperimentRunner> er, 
			boolean verbose, boolean parallelOps, FileBaseDatabase fileBaseDatabase) {
		return new ParallelThreadMultiExperimentExecutor(er, verbose,parallelOps,
				fileBaseDatabase);
	}
	
	 

//	private final Map<ExperimentSetup, ExperimentOutput>cache 
//	= new HashMap<ExperimentSetup, ExperimentOutput>();
	@Override
	public ExperimentOutput apply(ExperimentSetup t) {
		
		if(db.hasAlreadyBeenProcessed(t))
		{
			if(verbose)
				System.out.println(t+":"+db.getResult(t));
			return db.getResult(t);
		}
		ExperimentRunner runner = null;
		//	synchronized (runners) {
		while(runner==null)
		{
			if(runners.isEmpty())
			{
				runners.add(er.get());
						System.out.println("Created runner");
			}
			runner = runners.poll();
		}

		ExperimentOutput res =runner.apply(t); 
		if(verbose)
			System.out.println(t+":"+res);
		//synchronized (runners) {
		runners.add(runner);
		//	System.out.println("Releasing a runner:"+runners.size());
		//}
		db.add(t,res);
		return res;
	}

}
