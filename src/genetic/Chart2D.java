package genetic;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.ArrayList;
import java.util.List;

public class Chart2D {

    private final LineChart<Number, Number> myChart;
    private final double min;
    private final double max;
    private final String f;
    private double[] zList;
    private final static int NBR_ITR = 100;

    public Chart2D(String f, double min, double max) {
        this.min = min;
        this.max = max;
        this.f = f;

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Nombre d'itération");
        //creating the chart
        this.myChart = new LineChart<>(xAxis,yAxis);

        this.myChart.setTitle("Maximisation");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("Z points");
        //populating the series with data
        this.optimisation(this.f, this.min, this.max);

        /*for (int i = 0; i < zList.size(); i++) {
            if (zList.get(i) > zList.get(zList.size() - 1))
                zList.set(i, zList.get(zList.size() - 1));
        }*/
        for (int i = 0; i < zList.length; i+=5) {
            series.getData().add(new XYChart.Data((i+1), zList[i]));
        }


        //Scene scene  = new Scene(lineChart,400,400);
        this.myChart.getData().add(series);
        this.myChart.setAnimated(false);
    }



    public void optimisation(String func, double min, double max) {

        zList = new double[NBR_ITR];
        int index;
        AG ag = new AG(func, min, max);

        Point[] populationInitiale = ag.populationInitial();
        System.out.println("----------------------------------------------------------------");

        for (int i = 0; i < populationInitiale.length; i++) {
            System.out.println(populationInitiale[i].toString());
        }

        System.out.println();

        index = ag.getMaxFitness(populationInitiale);
        System.out.println("Max f(x,y) = " + populationInitiale[index]);

        int compteur = 0;

        do {
            System.out.println("Génération Suivante");
            Point[] suivant = ag.algorithm_genetique(populationInitiale);

            for (int i = 0; i < suivant.length; i++) {
                System.out.println(suivant[i].toString());
            }

            index = ag.getMaxFitness(suivant);
            System.out.println("Max Z = " + suivant[index]);
            zList[compteur] = suivant[index].getZ();
            populationInitiale = suivant;
            compteur++;
        } while(compteur < NBR_ITR);
    }

    public LineChart<Number, Number> getMyChart() {
        return myChart;
    }

}
