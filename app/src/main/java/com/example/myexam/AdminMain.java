package com.example.myexam;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Transformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.LabelFormatter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminMain extends AppCompatActivity {
    //private static Tag="AdminMain";
    private int[] yDataMeeting = new int[2];
    private String[] xDataMeeting = {"active", "not active"};

    private int[] yDataParent = new int[2];
    private String[] xDataParent = {"new", "old"};

    private int[] yDataKid = new int[2];
    private String[] xDataKid = {"new", "old"};


    private BarChart barChart;

    private int[] yDataTotal = {210, 140, 70, 80, 50};
    private String[] xDataTotal = {"Sport", "Space", "Art", "Math", "Nature"};

    private PieChart pieChartHours, pieChartParents, pieChartKids, pieChartTotal;
    private TextView numberOfHours, numberOfParents, numberOfKids;
    private Button yearlyBtn, monthlyBtn, weeklyBtn;
    private int timecontrol = 2;
    private BottomNavigationView navigationView;

    private HashMap<String, Integer> categoryKids = new HashMap<String, Integer>();
    private HashMap<String, HashMap<Integer, Integer>> barDataMap = new HashMap<String, HashMap<Integer, Integer>>();
    ///////////
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9091/")
            // when sending data in json format we have to add Gson converter factory
            .addConverterFactory(GsonConverterFactory.create())
            // and build our retrofit builder.
            .build();
    // create an instance for our retrofit api class.
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

    ////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);
///////////////////////

        yearlyBtn = findViewById(R.id.yearlyBtn);
        monthlyBtn = findViewById(R.id.monthlyBtn);
        weeklyBtn = findViewById(R.id.weeklyBtn);
        barChart = findViewById(R.id.barChartAdmin);
        monthlyBtn.setBackgroundColor(Color.parseColor("#5377ee"));
        navigationView = findViewById(R.id.navibarAdminMain);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here

        final Fragment fragment2 = new LeadersFragment();
        final Fragment fragment3 = new CoursesFragment();
        final Fragment fragment4 = new CategoryFragment();


        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                        switch (item.getItemId()) {
                            case R.id.bottomNavigationUserMenuId:
//                                Intent intent=new Intent(AdminMain.this, AdminMain.class);
//
//                                startActivity(intent);
                                Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("MY_FRAGMENT");
                                if (myFragment != null && myFragment.isVisible()) {
                                    fragmentManager.beginTransaction().remove(myFragment).commitAllowingStateLoss();
                                }

                                break;
                            case R.id.bottomNavigationLeadersMenuId:
                                fragmentManager.beginTransaction().replace(R.id.adminlayout, fragment2, "MY_FRAGMENT")
                                        .commitAllowingStateLoss();


                                break;
                            case R.id.bottomNavigationCategorytMenuId:
                                fragmentManager.beginTransaction().replace(R.id.adminlayout, fragment4, "MY_FRAGMENT")
                                        .commitAllowingStateLoss();
                                break;
                            case R.id.bottomNavigationCoursesMenuId:
                                fragmentManager.beginTransaction().replace(R.id.adminlayout, fragment3, "MY_FRAGMENT")
                                        .commitAllowingStateLoss();

                                break;
                            case R.id.bottomNavigationMoreMenuId:
//                                fragmentManager.beginTransaction().replace(R.id.adminlayout, fragment5, "MY_FRAGMENT")
//                                        .commitAllowingStateLoss();
                                PopupMenu popup = new PopupMenu(AdminMain.this, findViewById(R.id.bottomNavigationMoreMenuId));
                                MenuInflater inflater = popup.getMenuInflater();
                                inflater.inflate(R.menu.mymenu, popup.getMenu());
                                popup.show();

                                break;

                        }
                        return true;
                    }
                });

        /////////////////////////

        yearlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearlyBtn.setBackgroundResource(R.drawable.myrect);
                monthlyBtn.setBackgroundResource(R.drawable.myrect8);
                weeklyBtn.setBackgroundResource(R.drawable.myrect5);
                timecontrol = 3;
                initBarChart();
                showBarChart();
                buildchart();

            }

        });

        monthlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timecontrol = 2;
                yearlyBtn.setBackgroundResource(R.drawable.myrect4);
                monthlyBtn.setBackgroundColor(Color.parseColor("#5377ee"));
                weeklyBtn.setBackgroundResource(R.drawable.myrect5);
                initBarChart();
                showBarChart();
                buildchart();

            }
        });


        weeklyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timecontrol = 1;
                yearlyBtn.setBackgroundResource(R.drawable.myrect4);
                weeklyBtn.setBackgroundResource(R.drawable.myrect7);
                monthlyBtn.setBackgroundResource(R.drawable.myrect8);
                initBarChart();
                showBarChart();
                buildchart();

            }
        });
        initBarChart();
        showBarChart();
        buildchart();
    }
        void buildchart () {



            Call<HashMap<String, HashMap<Integer, Integer>>> callBar = retrofitAPI.getTrend(timecontrol);

            callBar.enqueue(new Callback<HashMap<String, HashMap<Integer, Integer>>>() {
                @Override
                public void onResponse(Call<HashMap<String, HashMap<Integer, Integer>>> call,
                                       Response<HashMap<String, HashMap<Integer, Integer>>> response) {
                    barDataMap = response.body();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    for (int i = 0; i < 5; i++) {
                        switch (timecontrol) {
                            case 1:
                                for (int j = 1; j <= 7; j++) {
                                    editor.putInt(xDataTotal[i] + j, barDataMap.get(xDataTotal[i]).get(j));
                                }
                                break;
                            case 2:
                                for (int j = 1; j <= 4; j++) {
                                    editor.putInt(xDataTotal[i] + j, barDataMap.get(xDataTotal[i]).get(j));
                                }
                                break;
                            case 3:
                                for (int j = 1; j <= 12; j++) {
                                    editor.putInt(xDataTotal[i] + j, barDataMap.get(xDataTotal[i]).get(j));
                                }

                        }
                    }

                    editor.commit();

                }

                @Override
                public void onFailure(Call<HashMap<String, HashMap<Integer, Integer>>> call, Throwable t) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    for (int i = 0; i < yDataTotal.length; i++) {
                        switch (timecontrol) {

                            case 1:
                                for (int j = 0; j < 7; j++) {

                                    editor.putInt(xDataTotal[i] + j, i + j * 2);
                                }
                                break;
                            case 2:
                                for (int j = 0; j < 4; j++) {
                                    editor.putInt(xDataTotal[i] + j, i + j);
                                }
                                break;
                            case 3:
                                for (int j = 0; j < 12; j++) {
                                    editor.putInt(xDataTotal[i] + j, i * 2 + j);
                                }

                        }
                    }
                    editor.commit();
                }
            });
            Call<int[]> callHour = retrofitAPI.getMeetingsStat(timecontrol);

            callHour.enqueue(new Callback<int[]>() {
                @Override
                public void onResponse(Call<int[]> call, Response<int[]> response) {
                    yDataMeeting = response.body();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("hours", yDataMeeting[0]);
                    editor.putInt("hoursPercentage", yDataMeeting[1]);
                    editor.commit();

                }

                @Override
                public void onFailure(Call<int[]> call, Throwable t) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("hours", 10);
                    editor.putInt("hoursPercentage", 40);
                    editor.commit();
                }
            });

            Call<int[]> callParent = retrofitAPI.getParentsStat(timecontrol);
            callParent.enqueue(new Callback<int[]>() {
                @Override
                public void onResponse(Call<int[]> call, Response<int[]> response) {
                    yDataParent = response.body();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("ParentsNumber", yDataParent[0]);
                    editor.putInt("ParentsPercentage", yDataParent[1]);
                    editor.commit();

                }

                @Override
                public void onFailure(Call<int[]> call, Throwable t) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("ParentsNumber", 20);
                    editor.putInt("ParentsPercentage", 80);
                    editor.commit();
                }
            });

            Call<int[]> callKid = retrofitAPI.getKidsStat(timecontrol);
            callKid.enqueue(new Callback<int[]>() {
                @Override
                public void onResponse(Call<int[]> call, Response<int[]> response) {
                    yDataKid = response.body();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("KidsNumber", yDataKid[0]);
                    editor.putInt("KidsPercentage", yDataKid[1]);
                    editor.commit();

                }

                @Override
                public void onFailure(Call<int[]> call, Throwable t) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("KidsNumber", 23);
                    editor.putInt("KidsPercentage", 65);
                    editor.commit();
                }
            });

            Call<HashMap<String, Integer>> callCatInfo = retrofitAPI.getKidsInCategories(timecontrol);
            callCatInfo.enqueue(new Callback<HashMap<String, Integer>>() {

                @Override

                public void onResponse(Call<HashMap<String, Integer>> call, Response<HashMap<String, Integer>> response) {

                    categoryKids = response.body();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
//                for (String s : categoryKids.keySet()) {
//                    editor.putInt(s, categoryKids.get(s));
//                }
                    for (int i = 0; i < yDataTotal.length; i++) {
                        editor.putInt(xDataTotal[i], categoryKids.get(xDataTotal[i]));
                    }
                    editor.commit();
                }

                @Override
                public void onFailure(Call<HashMap<String, Integer>> call, Throwable t) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();


                    for (int i = 0; i < yDataTotal.length; i++) {
                        editor.putInt(xDataTotal[i], (i + 1) * 3);
                    }
                    editor.commit();
                }
            });

            ////////////////////////////
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();

            yDataMeeting[0] = pref.getInt("hours", 0); // getting
            yDataMeeting[1] = pref.getInt("hoursPercentage", 0); // getting
            editor.remove("hours");
            editor.remove("hoursPercentage");

            yDataParent[0] = pref.getInt("ParentsNumber", 0); // getting
            yDataParent[1] = pref.getInt("ParentsPercentage", 0); // getting
            editor.remove("ParentsNumber");
            editor.remove("ParentsPercentage");

            yDataKid[0] = pref.getInt("KidsNumber", 0); // getting
            yDataKid[1] = pref.getInt("KidsPercentage", 0); // getting
            editor.remove("KidsNumber");
            editor.remove("KidsPercentage");

//        int i = 0;
//        for (String s : xDataTotal) {

//            categoryKids.put(s,pref.getInt(s, 0));
//            i++;
//        }
            for (int i = 0; i < yDataTotal.length; i++) {
                yDataTotal[i] = pref.getInt(xDataTotal[i], 0);
            }

            numberOfHours = findViewById(R.id.numberOfHours);
            numberOfHours.setText(String.valueOf(yDataMeeting[1]) + "%");

            numberOfParents = findViewById(R.id.numberOfNewParents);

            numberOfParents.setText(String.valueOf(yDataParent[1]) + "%");

            numberOfKids = findViewById(R.id.numberOfNewKids);
            numberOfKids.setText(String.valueOf(yDataKid[1]) + "%");


            barChart = findViewById(R.id.barChartAdmin);


            //////////////////////////////
            pieChartHours = findViewById(R.id.idPieChartHours);
            pieChartParents = findViewById(R.id.idPieChartParent);
            pieChartKids = findViewById(R.id.idPieChartKids);
            pieChartTotal = findViewById(R.id.idPieChartTotal);
            // pieChart.setDescription("Sales by employee (In Thousands $) ");
            pieChartHours.setRotationEnabled(false);
            pieChartHours.setUsePercentValues(false);
            //pieChart.setHoleColor(Color.BLUE);
          pieChartHours.setHoleRadius(80f);  //pieChart.setCenterTextColor(Color.BLACK);

            pieChartHours.setTransparentCircleAlpha(0);
            pieChartHours.setCenterText(String.valueOf(yDataMeeting[0]));//number of hours
            pieChartHours.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            pieChartHours.setCenterTextSize(20);
            pieChartHours.getDescription().setEnabled(false);
            pieChartHours.setHighlightPerTapEnabled(false);
//////////////////////////////////

            // pieChart.setDescription("Sales by employee (In Thousands $) ");
            pieChartParents.setRotationEnabled(false);
            pieChartParents.setUsePercentValues(false);
            //pieChart.setHoleColor(Color.BLUE);
            //pieChart.setCenterTextColor(Color.BLACK);
            pieChartParents.setHoleRadius(80f);
            pieChartParents.setTransparentCircleAlpha(0);
            pieChartParents.setCenterText(String.valueOf(yDataParent[0]));
            pieChartParents.setCenterTextSize(20);
            pieChartParents.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            pieChartParents.getDescription().setEnabled(false);
            pieChartParents.setHighlightPerTapEnabled(false);
            /////////////////////////////////////////////////


            pieChartKids.setRotationEnabled(false);
            pieChartKids.setUsePercentValues(false);
            //pieChart.setHoleColor(Color.BLUE);
            //pieChart.setCenterTextColor(Color.BLACK);
            pieChartKids.setHoleRadius(80f);
            pieChartKids.setTransparentCircleAlpha(0);
            pieChartKids.setCenterText(String.valueOf(yDataKid[0]));
            pieChartKids.setCenterTextSize(20);
            pieChartKids.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            pieChartKids.getDescription().setEnabled(false);
            pieChartKids.setHighlightPerTapEnabled(false);
            //////////////////////////////////////////////

            pieChartTotal.setRotationEnabled(false);
            pieChartTotal.setUsePercentValues(false);
            pieChartTotal.setHoleRadius(0f);
            pieChartTotal.setTransparentCircleAlpha(0);
            //pieChartTotal.setCenterText("250");
            //pieChartTotal.setCenterTextSize(10);
            pieChartTotal.getDescription().setEnabled(false);
            pieChartTotal.setHighlightPerTapEnabled(false);
            addDataSetHours();
            addDataSetTotal();
            addDataSetParents();
            addDataSetKids();
        }


        private void addDataSetHours () {

            ArrayList<PieEntry> yEntrys = new ArrayList<>();
            ArrayList<String> xEntrys = new ArrayList<>();


            for (int i = 0; i < xDataMeeting.length; i++) {
                xEntrys.add(xDataMeeting[i]);
            }
            yEntrys.add(new PieEntry(yDataMeeting[1], 0));
            yEntrys.add(new PieEntry(100 - yDataMeeting[1], 1));

            //create the data set
            PieDataSet pieDataSet = new PieDataSet(yEntrys, "         Activities In Hour");
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);


            //add colors to dataset
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#5377ee"));
            colors.add(Color.parseColor("#d0dbff"));

            colors.add(Color.RED);
            colors.add(Color.GREEN);
            colors.add(Color.CYAN);

            colors.add(Color.YELLOW);
            colors.add(Color.MAGENTA);

            pieDataSet.setColors(colors);
            pieDataSet.setDrawValues(false);
            pieDataSet.setSliceSpace(0f);
            pieChartHours.setDrawSliceText(false);
            //add legend to chart
            Legend legend = pieChartHours.getLegend();
            legend.setForm(Legend.LegendForm.NONE);
            // legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            // legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            //create pie data object
            PieData pieData = new PieData(pieDataSet);
            pieChartHours.setData(pieData);

            pieChartHours.invalidate();
            //pieChart.getLegend().setEnabled(false);


            ////////////////////////////////

        }

        private void addDataSetTotal () {
            ArrayList<PieEntry> yEntrysTotal = new ArrayList<>();
            ArrayList<String> xEntrysTotal = new ArrayList<>();
//        if (categoryKids.isEmpty()) {
//            for (int i = 0; i < yDataTotal.length; i++) {
//                yEntrysTotal.add(new PieEntry(yDataTotal[i], i));
//            }
//            for (int i = 0; i < xDataTotal.length; i++) {
//                xEntrysTotal.add(xDataTotal[i]);
//            }
//        }
//        else {
//            for (int i = 0; i < xDataTotal.length; i++) {
//                xEntrysTotal.add(xDataTotal[i]);
//                yEntrysTotal.add(new PieEntry(categoryKids.get(xDataTotal[i]), i));
//            }
//        }
            for (int i = 0; i < yDataTotal.length; i++) {
                if (yDataTotal[i] != 0) {
                    yEntrysTotal.add(new PieEntry(yDataTotal[i]));
                    xEntrysTotal.add(xDataTotal[i]);
                }
            }


            PieDataSet pieDataSetTotal = new PieDataSet(yEntrysTotal, "     Total Per Category");
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#5377ee"));
            colors.add(Color.parseColor("#edc655"));


            colors.add(Color.parseColor("#0091ff"));
            colors.add(Color.parseColor("#d0dbff"));
            colors.add(R.color.white);
            colors.add(Color.GRAY);
            colors.add(Color.YELLOW);


            pieDataSetTotal.setSliceSpace(2);
            pieDataSetTotal.setValueTextSize(12);
            pieDataSetTotal.setColors(colors);
            pieDataSetTotal.setDrawValues(true);
            pieDataSetTotal.setSliceSpace(0f);
            // pieChartTotal.setDrawSliceText(false);
            //add legend to chart

            pieDataSetTotal.setValueTextColor(Color.WHITE);
            pieDataSetTotal.setColors(colors);
            pieDataSetTotal.setDrawValues(true);
            pieDataSetTotal.setSliceSpace(0f);
            pieChartTotal.setDrawSliceText(true);
            ValueFormatter vf = new ValueFormatter() { //value format here, here is the overridden method
                @Override
                public String getFormattedValue(float value) {
                    return "" + (int) value;
                }
            };
            pieDataSetTotal.setValueFormatter(vf);
            Legend legendTotal = pieChartTotal.getLegend();
            legendTotal.setForm(Legend.LegendForm.NONE);

            // legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            legendTotal.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legendTotal.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            // legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legendTotal.setDrawInside(false);
            //create pie data object
            PieData pieDataTotal = new PieData(pieDataSetTotal);
            pieChartTotal.setData(pieDataTotal);

            pieChartTotal.invalidate();
        }

        private void addDataSetParents () {

            ArrayList<PieEntry> yEntrys = new ArrayList<>();
            ArrayList<String> xEntrys = new ArrayList<>();


            for (int i = 0; i < xDataParent.length; i++) {
                xEntrys.add(xDataParent[i]);
            }
            yEntrys.add(new PieEntry(yDataParent[1], 0));
            yEntrys.add(new PieEntry(100 - yDataParent[1], 1));


            //create the data set
            PieDataSet pieDataSet = new PieDataSet(yEntrys, "               New Parents");
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);


            //add colors to dataset
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#edc655"));
            colors.add(Color.parseColor("#d0dbff"));

            colors.add(Color.RED);
            colors.add(Color.GREEN);
            colors.add(Color.CYAN);

            colors.add(Color.YELLOW);
            colors.add(Color.MAGENTA);

            pieDataSet.setColors(colors);
            pieDataSet.setDrawValues(false);
            pieDataSet.setSliceSpace(0f);
            pieChartParents.setDrawSliceText(false);
            //add legend to chart
            Legend legend = pieChartParents.getLegend();
            legend.setForm(Legend.LegendForm.NONE);
            // legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            // legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            //create pie data object
            PieData pieData = new PieData(pieDataSet);
            pieChartParents.setData(pieData);


            pieChartParents.invalidate();
            //pieChart.getLegend().setEnabled(false);


            ////////////////////////////////

        }

        private void addDataSetKids () {

            ArrayList<PieEntry> yEntrys = new ArrayList<>();
            ArrayList<String> xEntrys = new ArrayList<>();


            for (int i = 0; i < xDataKid.length; i++) {
                xEntrys.add(xDataKid[i]);
            }
            yEntrys.add(new PieEntry(yDataKid[1], 0));
            yEntrys.add(new PieEntry(100 - yDataKid[1], 1));


            //create the data set
            PieDataSet pieDataSet = new PieDataSet(yEntrys, "                  New Children");
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);


            //add colors to dataset
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#0091ff"));
            colors.add(Color.parseColor("#d0dbff"));


            colors.add(Color.GREEN);
            colors.add(Color.CYAN);

            colors.add(Color.YELLOW);
            colors.add(Color.MAGENTA);

            pieDataSet.setColors(colors);
            pieDataSet.setDrawValues(false);
            pieDataSet.setSliceSpace(0f);
            pieChartKids.setDrawSliceText(false);
            //add legend to chart
            Legend legend = pieChartKids.getLegend();
            legend.setForm(Legend.LegendForm.NONE);
            // legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            // legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            //create pie data object
            PieData pieData = new PieData(pieDataSet);
            pieChartKids.setData(pieData);

            pieChartKids.invalidate();
            //pieChart.getLegend().setEnabled(false);


            ////////////////////////////////

        }

        //function responsible for capturing data input and output the bar chart with the data.
        private void showBarChart () {

            int barnum = 1;
            ArrayList<Double> valueList = new ArrayList<Double>();
            ArrayList<Double> valueList2 = new ArrayList<Double>();
            ArrayList<Double> valueList3 = new ArrayList<Double>();
            ArrayList<Double> valueList4 = new ArrayList<Double>();
            ArrayList<Double> valueList5 = new ArrayList<Double>();


            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<BarEntry> entries2 = new ArrayList<>();
            ArrayList<BarEntry> entries3 = new ArrayList<>();
            ArrayList<BarEntry> entries4 = new ArrayList<>();
            ArrayList<BarEntry> entries5 = new ArrayList<>();


            //input data
            switch (timecontrol) {
                case 1:
                    barnum = 7;
                    break;
                case 2:
                    barnum = 4;
                    break;
                case 3:
                    barnum = 12;
                    break;

            }
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyKIDIPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();

            for (int i = 1; i <= barnum; i++) {
                valueList.add(Double.valueOf(pref.getInt(xDataTotal[0] + i, 0)));
                editor.remove(xDataTotal[0] + i);

            }

            for (int i = 1; i <= barnum; i++) {
                valueList2.add(Double.valueOf(pref.getInt(xDataTotal[1] + i, 0)));
                editor.remove(xDataTotal[1] + i);

            }
            for (int i = 1; i <= barnum; i++) {
                valueList3.add(Double.valueOf(pref.getInt(xDataTotal[2] + i, 0)));
                editor.remove(xDataTotal[2] + i);

            }
            for (int i = 1; i <= barnum; i++) {
                valueList4.add(Double.valueOf(pref.getInt(xDataTotal[3] + i, 0)));
                editor.remove(xDataTotal[3] + i);

            }
            for (int i = 1; i <= barnum; i++) {
                valueList5.add(Double.valueOf(pref.getInt(xDataTotal[4] + i, 0)));
                editor.remove(xDataTotal[4] + i);

            }


            //fit the data into a bar
            for (int i = 0; i < valueList.size(); i++) {
                BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
                entries.add(barEntry);

                BarEntry barEntry2 = new BarEntry(i, valueList2.get(i).floatValue());
                entries2.add(barEntry2);

                BarEntry barEntry3 = new BarEntry(i, valueList3.get(i).floatValue());
                entries3.add(barEntry3);

                BarEntry barEntry4 = new BarEntry(i, valueList4.get(i).floatValue());
                entries4.add(barEntry4);

                BarEntry barEntry5 = new BarEntry(i, valueList5.get(i).floatValue());
                entries5.add(barEntry5);
            }

            BarDataSet barDataSet = new BarDataSet(entries, xDataTotal[0]);
            barDataSet.setColors(Color.parseColor("#5377ee"));
            BarDataSet barDataSet2 = new BarDataSet(entries2, xDataTotal[1]);
            barDataSet2.setColors(Color.parseColor("#edc655"));
            BarDataSet barDataSet3 = new BarDataSet(entries3, xDataTotal[2]);
            barDataSet3.setColors(Color.parseColor("#0091ff"));
            BarDataSet barDataSet4 = new BarDataSet(entries4, xDataTotal[3]);
            barDataSet4.setColors(Color.parseColor("#d0dbff"));
            BarDataSet barDataSet5 = new BarDataSet(entries5, xDataTotal[4]);
            barDataSet5.setColors(R.color.white);


            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(barDataSet);
            dataSets.add(barDataSet2);
            dataSets.add(barDataSet3);
            dataSets.add(barDataSet4);
            dataSets.add(barDataSet5);


            BarData data = new BarData(dataSets);
            ;


            barChart.setData(data);
            barChart.setScaleEnabled(false);


            data.setBarWidth(0.1f);
            barChart.setVisibleXRangeMaximum(5f);
            barChart.groupBars(-0.45f, 0.5f, 0f);

            barChart.invalidate();
//        BarData data = new BarData(barDataSet);
//        barChart.setData(data);
//        barChart.invalidate();
            initBarDataSet(barDataSet);
            initBarDataSet(barDataSet2);
            initBarDataSet(barDataSet3);
            initBarDataSet(barDataSet4);
            initBarDataSet(barDataSet5);

        }

        //function  responsible for the appearance of the bar chart
        private void initBarDataSet (BarDataSet barDataSet){
            //Changing the color of the bar
//        barDataSet.setColor(Color.parseColor("#304567"));
            //Setting the size of the form in the legend
            barDataSet.setFormSize(15f);
            //showing the value of the bar, default true if not set
            barDataSet.setDrawValues(false);
            //setting the text size of the value of the bar
            barDataSet.setValueTextSize(12f);
            barDataSet.setDrawValues(false);

        }


        //function  responsible for the appearance of the bar chart
        private void initBarChart () {
            barChart.setFitBars(true);
            barChart.fitScreen();
            barChart.invalidate();
            String[] annualLabels = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug"
                    , "sep", "oct", "nov", "dec"};
            String[] monthlyLabels = new String[]{"week  1", "week 2", "week  3", "week  4"};
            String[] weeklyLabels = new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
            //hiding the grey background of the chart, default false if not set
            barChart.setDrawGridBackground(false);
            //remove the bar shadow, default false if not set
            barChart.setDrawBarShadow(false);
            //remove border of the chart, default false if not set
            barChart.setDrawBorders(false);

            barChart.setDragEnabled(true);
            barChart.setOnClickListener(null);
            //remove the description label text located at the lower right corner
            Description description = new Description();
            description.setEnabled(false);
            barChart.setDescription(description);


            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry entry, Highlight highlight) {
                    barChart.getOnTouchListener().setLastHighlighted(null);
                    barChart.highlightValues(null);
                    //don't highlight when clicked
                }

                @Override
                public void onNothingSelected() {

                }
            });


            //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
            barChart.animateY(1000);
            //setting animation for x-axis, the bar will pop up separately within the time we set
            barChart.animateX(1000);

            XAxis xAxis = barChart.getXAxis();
            //change the position of x-axis to the bottom
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //set the horizontal distance of the grid line
            xAxis.setGranularity(1f);
            //hiding the x-axis line, default true if not set
            xAxis.setDrawAxisLine(false);

            //hiding the vertical grid lines, default true if not set
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = barChart.getAxisLeft();
            //hiding the left y-axis line, default true if not set
            leftAxis.setDrawAxisLine(false);

            YAxis rightAxis = barChart.getAxisRight();
            //hiding the right y-axis line, default true if not set
            rightAxis.setDrawAxisLine(false);

            rightAxis.setDrawLabels(false);
            switch (timecontrol) {
                case 3:
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(annualLabels));
                    break;
                case 2:
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(monthlyLabels));
                    break;
                case 1:
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(weeklyLabels));
                    break;

            }

            Legend legend = barChart.getLegend();
            //setting the shape of the legend form to line, default square shape
            legend.setForm(Legend.LegendForm.LINE);
            //setting the text size of the legend
            legend.setTextSize(11f);
            //setting the alignment of legend toward the chart
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            //setting the stacking direction of legend
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            //setting the location of legend outside the chart, default false if not set
            legend.setDrawInside(false);
            legend.setEnabled(true);
            // setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            //  setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            //  barChart.setExtraOffsets(5f,5f,5f,15f);

            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            CustomBarChartRender barChartRender = new CustomBarChartRender(barChart, barChart.getAnimator(), barChart.getViewPortHandler());
            barChartRender.setRadius(12);
            barChart.setRenderer(barChartRender);
        }


    }
