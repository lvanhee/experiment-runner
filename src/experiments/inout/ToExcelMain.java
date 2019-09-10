package experiments.inout;

import java.util.Set;

import experiments.inout.FileReadWriter.FileFormat;
import experiments.inout.FileReadWriter.FileOperation;
import experiments.model.DataPoint;
import experiments.model.Experiment;

public class ToExcelMain {
	
	public static void main(String[] args)
	{
		Set<DataPoint> dataPoints =
				FileReadWriter.loadDataPoints(args[0]);
		
		FileReadWriter.saveAs(
				dataPoints, 
				args[1], 
				FileFormat.EXCEL, 
				FileOperation.REPLACE_ALL);
	}

}
