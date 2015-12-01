package cl.saratscheff.sandiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

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
import java.util.Collections;
import java.util.Random;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private PieChart pieChart;
    private String centerText;
    private ArrayList<String> xValsSelected;
    private ArrayList<String> xVals1;
    private ArrayList<String> xVals2;
    private ArrayList<String> xVals3;
    private ArrayList<String> xVals4;
    private PieDataSet pieDataSet1;
    private PieDataSet pieDataSet2;
    private PieDataSet pieDataSet3;
    private PieDataSet pieDataSet4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pieChart = (PieChart) findViewById(R.id.pie_chart);
        RadioButton rb1 = (RadioButton) findViewById(R.id.radio1);
        RadioButton rb2 = (RadioButton) findViewById(R.id.radio2);
        RadioButton rb3 = (RadioButton) findViewById(R.id.radio3);
        RadioButton rb4 = (RadioButton) findViewById(R.id.radio4);

        // Datos de ejemplo

        // Crear listas de Entries
        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp3 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp4 = new ArrayList<Entry>();

        // Rellenar con Entries de ejemplo!
        valsComp1.add(new Entry(104, 0));
        valsComp1.add(new Entry(45, 1));
        valsComp1.add(new Entry(123, 2));
        valsComp1.add(new Entry(245, 3));
        valsComp2.add(new Entry(134, 0));
        valsComp2.add(new Entry(256, 1));
        valsComp2.add(new Entry(87, 2));
        valsComp2.add(new Entry(13, 3));
        valsComp3.add(new Entry(324, 0));
        valsComp3.add(new Entry(167, 1));
        valsComp3.add(new Entry(23, 2));
        valsComp3.add(new Entry(6, 3));
        valsComp4.add(new Entry(67, 0));
        valsComp4.add(new Entry(74, 1));
        valsComp4.add(new Entry(13, 2));
        valsComp4.add(new Entry(37, 3));

        pieDataSet1 = new PieDataSet(valsComp1, "");
        pieDataSet2 = new PieDataSet(valsComp2, "");
        pieDataSet3 = new PieDataSet(valsComp3, "");
        pieDataSet4 = new PieDataSet(valsComp4, "");

        xVals1 = new ArrayList<String>();
        xVals1.add("Las Condes"); xVals1.add("Vitacura"); xVals1.add("La Reina"); xVals1.add("La Pintana");
        xVals2 = new ArrayList<String>();
        xVals2.add("Robo Autos"); xVals2.add("Transporte público"); xVals2.add("Mascotas perdidas"); xVals2.add("Otros");
        xVals3 = new ArrayList<String>();
        xVals3.add("2015"); xVals3.add("2014"); xVals3.add("2013"); xVals3.add("2012");
        xVals4 = new ArrayList<String>();
        xVals4.add("Septiembre"); xVals4.add("Octubre"); xVals4.add("Noviembre"); xVals4.add("Diciembre");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        long seed = System.nanoTime(); Random rnd = new Random(seed); Collections.shuffle(colors, rnd); // Shuffle lista de colores
        pieDataSet1.setColors(colors);
        Collections.shuffle(colors, rnd); // Shuffle lista de colores
        pieDataSet2.setColors(colors);
        Collections.shuffle(colors, rnd); // Shuffle lista de colores
        pieDataSet3.setColors(colors);
        Collections.shuffle(colors, rnd); // Shuffle lista de colores
        pieDataSet4.setColors(colors);

        PieData data = new PieData(xVals1, pieDataSet1); xValsSelected=xVals1;
        data.setValueTextSize(12f);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        centerText = "Seleccione comuna \n para ver N° de casos";
        pieChart.setCenterText(centerText);
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
        pieChart.setCenterText(xValsSelected.get(e.getXIndex()) + "\n" + "Casos: " + Math.round(e.getVal()));
    }

    @Override
     public void onNothingSelected() {
        pieChart.setCenterText(centerText);
    }

    public void onRadioButtonClicked(View view) {
        RadioButton rb = (RadioButton) view;

        boolean checked = rb.isChecked();
        switch(rb.getId()) {
            case R.id.radio1:
                if (checked) {
                    PieData data = new PieData(xVals1, pieDataSet1); xValsSelected=xVals1;
                    data.setValueTextSize(12f);
                    data.setValueFormatter(new PercentFormatter());
                    pieChart.setData(data);
                    centerText = "Seleccione comuna \n para ver N° de casos";
                    pieChart.setCenterText(centerText);
                    pieChart.animateY(300, Easing.EasingOption.EaseInOutQuad);
                    pieChart.highlightValue(-1, -1); // Deseleccionar items
                    break;
                }
            case R.id.radio2:
                if (checked) {
                    PieData data = new PieData(xVals2, pieDataSet2); xValsSelected=xVals2;
                    data.setValueTextSize(12f);
                    data.setValueFormatter(new PercentFormatter());
                    pieChart.setData(data);
                    centerText = "Seleccione Categoría \n para ver N° de casos";
                    pieChart.setCenterText(centerText);
                    pieChart.animateY(300, Easing.EasingOption.EaseInOutQuad);
                    pieChart.highlightValue(-1, -1); // Deseleccionar items
                    break;
                }
            case R.id.radio3:
                if (checked) {
                    PieData data = new PieData(xVals3, pieDataSet3); xValsSelected=xVals3;
                    data.setValueTextSize(12f);
                    data.setValueFormatter(new PercentFormatter());
                    pieChart.setData(data);
                    centerText = "Seleccione Año \n para ver N° de casos";
                    pieChart.setCenterText(centerText);
                    pieChart.animateY(300, Easing.EasingOption.EaseInOutQuad);
                    pieChart.highlightValue(-1, -1); // Deseleccionar items
                    break;
                }
            case R.id.radio4:
                if (checked) {
                    PieData data = new PieData(xVals4, pieDataSet4); xValsSelected=xVals4;
                    data.setValueTextSize(12f);
                    data.setValueFormatter(new PercentFormatter());
                    pieChart.setData(data);
                    centerText = "Seleccione Mes \n para ver N° de casos";
                    pieChart.setCenterText(centerText);
                    pieChart.animateY(300, Easing.EasingOption.EaseInOutQuad);
                    pieChart.highlightValue(-1, -1); // Deseleccionar items
                    break;
                }
        }
    }
}
