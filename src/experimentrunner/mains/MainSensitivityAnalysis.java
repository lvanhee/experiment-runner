package experimentrunner.mains;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import experimentrunner.model.experiment.data.ExperimentOutput;
import experimentrunner.model.experiment.data.ExperimentSetup;
import experimentrunner.model.experimentexecutor.ExperimentBatchRunner;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;
import experimentrunner.modules.netlogo.Pair;

public class MainSensitivityAnalysis {
	
	public static final int NUMBER_REPETITIONS = 10;
	
	private static enum Experiments{
		COMPLEX_DO_IT,
		ROBUSTNESS_DO_IT,
		INTENSIVE_DO_IT,
		INTENSIVE_DO_IT_TOGETHER,
		COMPLEX_DO_IT_TOGETHER,
		ROBUSTNESS_DO_IT_TOGETHER,
		CULTURAL_CLASH_COMPLEX_DO_IT,
		CULTURAL_CLASH_ROBUSTNESS_DO_IT,
		CULTURAL_CLASH_INTENSIVE_DO_IT,
		INTENSIVE_RULE_DO_IT,
		COMPLEX_RULE_DO_IT,
		ROBUSTNESS_RULE_DO_IT;

		public boolean isIntensive() {
			return this == INTENSIVE_DO_IT || 
					this == Experiments.INTENSIVE_DO_IT_TOGETHER ||
					this == CULTURAL_CLASH_INTENSIVE_DO_IT ||
					this == INTENSIVE_RULE_DO_IT;
		}

		public boolean isClashing() {
			return this == CULTURAL_CLASH_COMPLEX_DO_IT 
					|| this == CULTURAL_CLASH_INTENSIVE_DO_IT 
					|| this == CULTURAL_CLASH_ROBUSTNESS_DO_IT;
		}

		public boolean isComplex() {
			return this == COMPLEX_DO_IT || 
					this == Experiments.COMPLEX_DO_IT_TOGETHER ||
					this == CULTURAL_CLASH_COMPLEX_DO_IT ||
					this == COMPLEX_RULE_DO_IT;
		}

		public boolean isDOIT() {
			return this == COMPLEX_DO_IT || this == Experiments.ROBUSTNESS_DO_IT || this == INTENSIVE_DO_IT
					|| this == CULTURAL_CLASH_COMPLEX_DO_IT 
					|| this == CULTURAL_CLASH_INTENSIVE_DO_IT 
					|| this == CULTURAL_CLASH_ROBUSTNESS_DO_IT;
		}

		public boolean ISDOITTogether() {
			return
					this == INTENSIVE_DO_IT_TOGETHER ||
					this == COMPLEX_DO_IT_TOGETHER ||
					this == ROBUSTNESS_DO_IT_TOGETHER;
		}

		public boolean IsRuleDoIt() {
			return
					this == INTENSIVE_RULE_DO_IT ||
					this == COMPLEX_RULE_DO_IT ||
					this == ROBUSTNESS_RULE_DO_IT;
		}

		public boolean isBursting() {
			return this == ROBUSTNESS_DO_IT
					|| this == ROBUSTNESS_DO_IT_TOGETHER
					|| this == CULTURAL_CLASH_ROBUSTNESS_DO_IT 
					|| this == Experiments.ROBUSTNESS_RULE_DO_IT;
		}
	}
		public Set<ExperimentSetup> getAllSensitivityParametersToBeTested() {
			/*Set<Pair<String>>res = new HashSet<>();
			res.addAll(
					Arrays.asList(
							new Pair<String>("#subordinates", "9"),
							new Pair<String>("#subordinates", "11")
							));
			ExperimentLinearScheduler es = getExperimentSeries(this);
			int btd = Integer.parseInt(
					es.getAllSetups().get(0)
					.getVariableAllocation().get("base-task-duration").toString());
			
			int btdMin = btd*9/10;
			int btdMax = btd*11/10;
			
			if(btd == btdMax || ((float)btd)*1.1f >btdMax) btdMax++;
			
			res.add(new Pair<String>("base-task-duration", ""+btdMin));
			res.add(new Pair<String>("base-task-duration", ""+btdMax));
			
			int baseTimeBeforeDeadline = Integer.parseInt(es.getAllSetups()
					.get(0).getVariableAllocation().get("nb-of-steps-before-deadline").toString());
			
			int baseTimeBeforeDeadlineMin = baseTimeBeforeDeadline*9/10;
			int baseTimeBeforeDeadlineMax = baseTimeBeforeDeadline*11/10;
			if(baseTimeBeforeDeadline == baseTimeBeforeDeadlineMax || ((float)baseTimeBeforeDeadline)*1.1f >baseTimeBeforeDeadlineMax) baseTimeBeforeDeadlineMax++;
			
			res.add(new Pair<String>("nb-of-steps-before-deadline", ""+baseTimeBeforeDeadlineMin));
			res.add(new Pair<String>("nb-of-steps-before-deadline", ""+baseTimeBeforeDeadlineMax));

			
			
			return res;
			throw new Error();
			
			
		}*/
		throw new Error();
		
	}

	public static void main(String[] args) throws IOException
	{
		/*
	
		NetLogoExperimentsRunner nler = NetLogoExperimentsRunner.newInstance(
				"/export/home/vanhee/Dropbox/Boulot/Recherche/Ecrits/Papiers/2018 JASSS/simulation/culture_coordination.nlogo",
				Arrays.asList("ratio-failures"),
				Optional.empty()
				);
		File outputFile = getOutputFile();
		
		for(Experiments expe: Experiments.values())
		{
			System.out.println("\n"+expe);
			ExperimentLinearScheduler es = getExperimentSeries(expe);
			for(Pair<String>p: expe.getAllSensitivityParametersToBeTested())
			{
				
			double divergence = ExperimentBatchRunner.getAverageDivergence(
					es, 
					p.left(),
					p.right(), "ratio-failures",nler);
			System.out.println("Average divergence when "+p.left()+":"+
			es.getAllSetups().get(0).getVariableAllocation().get(p.left())+"->"+
			p.right()+"->"+
			" for "+expe +":"+divergence);
			}
		}
		System.exit(0);*/
		throw new Error();
		
	}

	private static ExperimentLinearScheduler getExperimentSeries(Experiments expe) {
	/*	Map<String, List<Object>> possibleParameterValues = new HashMap<>();
		
		possibleParameterValues.put("leader-value-system", 
				Arrays.asList("PDI- MAS+","PDI- MAS-","PDI+ MAS+","PDI+ MAS-"));
		
		if(expe.isClashing())
		possibleParameterValues.put("subordinate-value-system", 
				Arrays.asList("PDI- MAS+","PDI- MAS-","PDI+ MAS+","PDI+ MAS-"));
		else
			if(expe.isClashing())
				possibleParameterValues.put("subordinate-value-system", 
						Arrays.asList("","PDI- MAS-","PDI+ MAS+","PDI+ MAS-"));
				else
					possibleParameterValues.put("subordinate-value-system", 
							Arrays.asList("PDI- MAS+"));
		
		LinkedList<Object> seeds = new LinkedList<>();
		for(int i = 0; i < NUMBER_REPETITIONS ; i++)
			seeds.add(""+i);
		possibleParameterValues.put("seed", 
				seeds);
		
		if(expe.isClashing())
			possibleParameterValues.put("shared-vs?", 
					Arrays.asList("0"));
		else
			possibleParameterValues.put("shared-vs?", 
					Arrays.asList("1"));
		
		switch(expe)
		{
		case INTENSIVE_DO_IT : case INTENSIVE_RULE_DO_IT:case CULTURAL_CLASH_INTENSIVE_DO_IT:
			possibleParameterValues.put("base-task-duration", 
					Arrays.asList("10")); break;
		case COMPLEX_DO_IT: case ROBUSTNESS_DO_IT:case COMPLEX_RULE_DO_IT: case ROBUSTNESS_RULE_DO_IT:
		case CULTURAL_CLASH_COMPLEX_DO_IT: case CULTURAL_CLASH_ROBUSTNESS_DO_IT:
			possibleParameterValues.put("base-task-duration", 
				Arrays.asList("5")); break;
		case INTENSIVE_DO_IT_TOGETHER: possibleParameterValues.put("base-task-duration", 
				Arrays.asList("75")); break;
		case COMPLEX_DO_IT_TOGETHER: possibleParameterValues.put("base-task-duration", 
				Arrays.asList("50"));break;
		case ROBUSTNESS_DO_IT_TOGETHER: possibleParameterValues.put("base-task-duration", 
				Arrays.asList("25"));break;
		default: throw new Error();
		}
		
		switch(expe)
		{
		case INTENSIVE_DO_IT : case INTENSIVE_RULE_DO_IT:case CULTURAL_CLASH_INTENSIVE_DO_IT:
			possibleParameterValues.put("nb-of-steps-before-deadline", 
					Arrays.asList("12")); break;
		case COMPLEX_DO_IT : case COMPLEX_RULE_DO_IT:case CULTURAL_CLASH_COMPLEX_DO_IT:
		case ROBUSTNESS_DO_IT : case ROBUSTNESS_RULE_DO_IT:case CULTURAL_CLASH_ROBUSTNESS_DO_IT:
			possibleParameterValues.put("nb-of-steps-before-deadline", 
					Arrays.asList("15")); break;
		case INTENSIVE_DO_IT_TOGETHER: case ROBUSTNESS_DO_IT_TOGETHER:
			possibleParameterValues.put("nb-of-steps-before-deadline", 
					Arrays.asList("35")); break;
		case COMPLEX_DO_IT_TOGETHER:
			possibleParameterValues.put("nb-of-steps-before-deadline", 
					Arrays.asList("50")); break;
					
		default: throw new Error();
					
		}	
		
		
		
		if(expe.isComplex())
		{
			List<Object> l = new ArrayList<>();
			for(float f = 0; f <= 1 ; f+=0.05)
				l.add(""+f);
			possibleParameterValues.put("environmental-complexity", 
					l);
		}
		else
			possibleParameterValues.put("environmental-complexity", 
					Arrays.asList("0"));


		if(expe.isDOIT())
			possibleParameterValues.put("organizational-structure", 
					Arrays.asList("DO-IT"));
		else if(expe.ISDOITTogether())
			possibleParameterValues.put("organizational-structure", 
					Arrays.asList("DO-IT-Together"));
		else if(expe.IsRuleDoIt())
			possibleParameterValues.put("organizational-structure", 
					Arrays.asList("Rule-DO-IT"));
		else throw new Error();

		if(expe.ISDOITTogether())
		{
			if(expe.isComplex())
			{possibleParameterValues.put("environment", 
					Arrays.asList("cooperative-complex"));
			}
			else if(expe.isBursting())
			{
				possibleParameterValues.put("environment", 
						Arrays.asList("cooperative-bursts"));
			}
			else if(expe.isIntensive())
			{
				possibleParameterValues.put("environment", 
						Arrays.asList("cooperative-intensive"));
			}
			else throw new Error();
		}
		else 
			if(expe.isComplex())
			{possibleParameterValues.put("environment", 
				Arrays.asList("complex"));
			}
		else if(expe.isBursting())
		{
			possibleParameterValues.put("environment", 
					Arrays.asList("bursts"));
		}
		else if(expe.isIntensive())
		{
			possibleParameterValues.put("environment", 
					Arrays.asList("intensive"));
		}
		else throw new Error();
		
		possibleParameterValues.put("#splits-creation", 
				Arrays.asList(""+5));
		possibleParameterValues.put("#split-tasks" , 
				Arrays.asList("5"));
		
		if(expe.isBursting())
		{
			List<Object> possibleBursts = new ArrayList<>();
			for(int i = 1 ;  i < 25 ; i++)
			{
				possibleBursts.add(""+i);
			}
			possibleParameterValues.put("#tasks-burst", possibleBursts);
		}
		else
		possibleParameterValues.put("#tasks-burst", 
				Arrays.asList("-1"));
		
		
		possibleParameterValues.put("time-to-split-tasks", 
				Arrays.asList("5"));
		
		if(expe.isIntensive())
		{
			List<Object> possibleVariations = new ArrayList<>();
			for(int i = 1 ;  i < 25 ; i++)
			{
				possibleVariations.add(""+i);
			}
			possibleParameterValues.put("task-duration-variation", possibleVariations);
		}
		else
			possibleParameterValues.put("task-duration-variation", 
				Arrays.asList("0"));
		
		possibleParameterValues.put("#subordinates", 
				Arrays.asList("10"));
	
		
		
		return ExperimentLinearScheduler.newInstance(possibleParameterValues);*/
		throw new Error();
	}

	private static File getOutputFile() {
		return new File("output/output.txt");
	}
	
}

