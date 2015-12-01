package cl.saratscheff.sandiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private PieChart pieChart;
    private ArrayList<String> xVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pieChart = (PieChart) findViewById(R.id.pie_chart);


        // Datos de ejemplo

        // Crear listas de Entries
        ArrayList<Entry> valsComp = new ArrayList<Entry>();

        // Rellenar con Entries
        valsComp.add(new Entry(104, 0));
        valsComp.add(new Entry(45, 1));
        valsComp.add(new Entry(123, 2));
        valsComp.add(new Entry(245, 3));

        PieDataSet pieDataSet = new PieDataSet(valsComp, "");
        pieDataSet.setSelectionShift(12f);

        xVals = new ArrayList<String>();
        xVals.add("Las Condes"); xVals.add("Vitacura"); xVals.add("La Reina"); xVals.add("La Pintana");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        pieDataSet.setColors(colors);

        PieData data = new PieData(xVals, pieDataSet);
        data.setValueTextSize(12f);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Seleccione comuna \n para ver N° de casos");
        pieChart.setDescription("");
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        pieChart.invalidate();

        pieChart.setOnChartValueSelectedListener(this);

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null) {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return;
        }
        System.out.println("Entry: " + e.toString() + "|| dataSetIndex: " + dataSetIndex + "|| HIghlight: " + h);
        pieChart.setCenterText(xVals.get(e.getXIndex()) + "\n" + "Casos: " + Math.round(e.getVal()));
    }

    @Override
     public void onNothingSelected() {
        System.out.println("Nothing Selected...");
    }
}
