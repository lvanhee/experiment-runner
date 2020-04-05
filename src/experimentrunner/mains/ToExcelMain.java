package experimentrunner.mains;

import java.util.Set;

import experimentrunner.inout.FileReadWriter;
import experimentrunner.inout.FileReadWriter.FileFormat;
import experimentrunner.inout.FileReadWriter.FileOperation;
import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.data.ExperimentSetup;

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
