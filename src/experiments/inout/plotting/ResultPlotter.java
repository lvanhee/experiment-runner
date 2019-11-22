package experiments.inout.plotting;

import java.awt.Point;
import java.awt.event.WindowEvent;
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

import experiments.model.DataPoint;
import experiments.model.Experiment;
import experiments.model.ExperimentSeries;


public class ResultPlotter extends ApplicationFrame {

/**
* A demonstration application showing an XY series containing a null value.
*
* @param title  the frame title.
 * @param data 
*/
public ResultPlotter(final String title, XYSeriesCollection data) {
   super(title);
   
   final JFreeChart chart = ChartFactory.createXYLineChart(
       title,
       "X", 
       "Y", 
       data,
       PlotOrientation.VERTICAL,
       true,
       true,
       false
   );

   final ChartPanel chartPanel = new ChartPanel(chart);
   chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
   setContentPane(chartPanel);

}

private static ResultPlotter demo = null;
public static void drawSeries(final XYSeriesCollection data, String title) {
	/*if(demo != null)
		demo.dispatchEvent(new WindowEvent(demo, WindowEvent.WINDOW_CLOSING));*/
  demo = new ResultPlotter(title,data);
   demo.pack();
   RefineryUtilities.centerFrameOnScreen(demo);
   demo.setVisible(true);
}

public static void plot(Set<DataPoint> points, String lines, String x, String y, 
		String title) {
	Map<Object, Set<DataPoint>> splittedExperiments = 
			Experiment.splitBy(points, lines);

	final XYSeriesCollection data = new XYSeriesCollection();

	for(Object s: splittedExperiments.keySet()) {
		Set<DataPoint> elements = splittedExperiments.get(s);
		Map<Object, Set<String>> pointsNames = DataPoint.toPoints(elements, x,y);
		Map<Double, Double> pointValues = pointsNames.keySet().stream()
				.collect(
						Collectors.toMap(
								z->Double.parseDouble(z.toString()),
								z-> pointsNames
								.get(z)
								.stream()
								.mapToDouble(t->Double.parseDouble(t))
								.average().getAsDouble()));

		List<Point> l = pointValues.keySet().stream().sorted().map(
				z->
				new Point(z.intValue(), pointValues.get(z).intValue())).collect(Collectors.toList());

		final XYSeries series = new XYSeries((Comparable) s);
		for(Point p: l)
			series.add(p.getX(), p.getY());
		data.addSeries(series);
	}
	drawSeries(data,title);

}

}