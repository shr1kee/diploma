import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
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
import java.util.List;

/**
 * Created by Acer on 17.04.2018.
 */
public class XYLineChart extends JFrame {
    static List<Double> x;
    static List<Double> y;
    static double[] xd;
    static double[] yd;
    static List<Double> yi;
    static List<Double> xi = new ArrayList<>();
    static double step = 0.05;
    public XYLineChart(ArrayList<Double> x, java.util.List<Double> y) {
//        super("Зависимость D От показателя Гельдера");
        super("Зависимость lnI от lnR");
        this.x=x;
        this.y=y;
        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    public static ArrayList<Double> linearInterp(double[] x, double[] y, double min,double max) {
        SplineInterpolator li = new SplineInterpolator(); // or other interpolator
        PolynomialSplineFunction psf = li.interpolate(x, y);
        ArrayList<Double> yi  = new ArrayList<>();
        for (double i =min ; i < max; i+=step) {
            yi.add(psf.value(i));
        }
        return yi;
    }
    public static void bubbleSort(){
    /*Внешний цикл каждый раз сокращает фрагмент массива,
      так как внутренний цикл каждый раз ставит в конец
      фрагмента максимальный элемент*/
        for(int i = xd.length-1 ; i > 0 ; i--){
            for(int j = 0 ; j < i ; j++){
            /*Сравниваем элементы попарно,
              если они имеют неправильный порядок,
              то меняем местами*/
            if( xd[j] > xd[j+1] ){
                double tmpx = xd[j];
                xd[j] = xd[j+1];
                xd[j+1] = tmpx;
                double tmpy = yd[j];
                yd[j] = yd[j+1];
                yd[j+1] = tmpy;
            }
        }
    }
}
    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Object 1");
        xd =new double[x.size()];
        yd = new double[y.size()];
        double minX =1000;
        double maxX = 0;
        for (int i = 0; i <x.size() ; i++) {
            xd[i]=x.get(i);
            yd[i] = y.get(i);
            if(x.get(i)>maxX)
                maxX = x.get(i);
            if(x.get(i)<minX)
                minX = x.get(i);
        }
        bubbleSort();
        for (double i = minX; i <maxX ; i+=step) {
            xi.add(i);
        }
        yi= linearInterp(xd,yd,minX,maxX);
        for (int i = 0; i <xi.size() ; i++) {
            series1.add(xi.get(i),yi.get(i));
        }
//        for (int i = 0; i <x.size() ; i++) {
//            series1.add(x.get(i),y.get(i));
//        }
        dataset.addSeries(series1);
        return dataset;
    }
    private JPanel createChartPanel() {
        String title = "Зависимость D от показателя Гельдера";
        String xAxisLabel = "показатель Гельдера";
        String yAxisLabel = "фрактальная размерность";
        XYDataset dataset = createDataset();
        boolean showLegend = false;
        boolean createURL = false;
        boolean createTooltip = false;
        JFreeChart chart = ChartFactory.createXYLineChart(title,xAxisLabel,yAxisLabel,dataset, PlotOrientation.VERTICAL,showLegend,createTooltip,createURL);
        return new ChartPanel(chart);
    }
}