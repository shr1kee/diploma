import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Acer on 17.04.2018.
 */
public class XYLineChart extends JFrame {
    ArrayList<Double> x;
    ArrayList<Double> y;
    public XYLineChart(ArrayList<Double> x,ArrayList<Double> y) {
        super("Зависимость D От показателя Гельдера");
        this.x=x;
        this.y=y;
        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Object 1");
        for (int i = 0; i <x.size() ; i++) {
            series1.add(x.get(i),y.get(i));
        }
        dataset.addSeries(series1);
        return dataset;
    }
    private JPanel createChartPanel() {
        String title = "Зависимость D от показателя Гельдера";
        String xAxisLabel = "Показатель Гельдера";
        String yAxisLabel = "Размерность";
        XYDataset dataset = createDataset();
        boolean showLegend = false;
        boolean createURL = false;
        boolean createTooltip = false;
        JFreeChart chart = ChartFactory.createXYLineChart(title,xAxisLabel,yAxisLabel,dataset, PlotOrientation.VERTICAL,showLegend,createTooltip,createURL);
        return new ChartPanel(chart);
    }
}