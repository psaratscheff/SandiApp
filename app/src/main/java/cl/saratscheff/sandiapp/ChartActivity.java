package cl.saratscheff.sandiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private PieChart pieChart;
    private ArrayList<String> xVals;
    private Firebase mFire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mFire = new Firebase("https://sizzling-heat-8397.firebaseio.com");




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


    private void loadPins(){
        final Firebase mFireMarkers = mFire.child("markers");

        mFireMarkers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for (DataSnapshot child : snapshot.getChildren()) {

                        boolean[] shouldCreateMark = new boolean[]{false, false, false, false};

                        LatLng loc = null;
                        if(child.child("latitude").exists() && child.child("longitude").exists()){
                            loc = new LatLng(Double.parseDouble(child.child("latitude").getValue().toString()),
                                    Double.parseDouble(child.child("longitude").getValue().toString()));
                            shouldCreateMark[0] = true;
                        }

                        String title = "";
                        String description = "";

                        if(child.child("title").exists()){
                            title = child.child("title").getValue().toString();
                            shouldCreateMark[1] = true;
                        }

                        if(child.child("description").exists()){
                            description = child.child("description").getValue().toString();
                            shouldCreateMark[2] = true;
                        }



                        boolean create = true;

                        for(int i = 0; i<shouldCreateMark.length; i++){
                            if(shouldCreateMark[i] == false)
                                create = false;
                        }

                        if(create){
                            MarkerOptions markerOptions = new MarkerOptions();

                            markerOptions.position(loc);
                            markerOptions.title(child.child("title").getValue().toString());
                            markerOptions.snippet(child.child("description").getValue().toString());
                            if(child.child("creator").exists()){
                                if(child.child("creator").getValue().toString().equals(LoginActivity.userID)){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                }
                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mFireMarkers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}

