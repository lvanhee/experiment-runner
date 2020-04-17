package experimentrunner.inout.plotting;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import experimentrunner.model.experiment.data.DataPoint;
import experimentrunner.model.experiment.values.DoubleValue;
import experimentrunner.model.experiment.values.NumericValue;
import experimentrunner.model.experiment.values.Value;
import experimentrunner.model.experiment.variables.Variable;
import experimentrunner.model.experimentexecutor.ExperimentLinearScheduler;
import experimentrunner.model.experimentexecutor.ProcessingUtils;


public class ResultPlotter extends ApplicationFrame {

/**
* A demonstration application showing an XY series containing a null value.
*
* @param title  the frame title.
 * @param data 
 * @param yVar 
 * @param xVar 
*/
public ResultPlotter(final String title, XYSeriesCollection data, Variable xVar, Variable yVar) {
   super(title);
   final JFreeChart chart = ChartFactory.createXYLineChart(
       title,
       xVar.getName(), 
       yVar.getName(),
       data,
       PlotOrientation.VERTICAL,
       true,
       true,
       false
   );

   final ChartPanel chartPanel = new ChartPanel(chart);
   chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
   setContentPane(chartPanel);
   
   /*try {

	    OutputStream out = new FileOutputStream(chartName);
	    ChartUtilities.writeChartAsPNG(out,
	            aJFreeChart,
	            aChartPanel.getWidth(),
	            aChartPanel.getHeight());

	} catch (IOException ex) {
	    logger.error(ex);
	}*/
}

private static ResultPlotter demo = null;
public static void drawSeries(final XYSeriesCollection data,
		Variable xVar, 
		Variable yVar,
		String title) {
	/*if(demo != null)
		demo.dispatchEvent(new WindowEvent(demo, WindowEvent.WINDOW_CLOSING));*/
  demo = new ResultPlotter(title,data,xVar, yVar);
   demo.pack();
   RefineryUtilities.centerFrameOnScreen(demo);
   demo.setVisible(true);
}

public static void plot(Set<DataPoint> points, Variable lines, Variable x, Variable y, 
		String title) {
	Map<Value, Set<DataPoint>> splittedExperiments = 
			ProcessingUtils.splitByValuesOf(points, lines);

	final XYSeriesCollection data = new XYSeriesCollection();

	for(Value s: splittedExperiments.keySet()) {
		Set<DataPoint> elements = splittedExperiments.get(s);
		List<Point2D.Double> l = ProcessingUtils
				.experimentsToPoints(elements, x,y);
		
		final XYSeries series = new XYSeries((Comparable) s);
		for(Point2D.Double p: l)
			series.add(p.getX(), p.getY());
		data.addSeries(series);
	}
	drawSeries(data,x, y,title);

}

}