package com.hkucs.receipt;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.axes.Linear;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Orientation;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {
    AnyChartView anyChartView;
    AnyChartView anyChartView2;
    ArrayList<Record> RecordList = new ArrayList<>();
    TextView textview2;
    TextView textview3;
    TextView textview4;
    TextView textview5;
    TextView textview6;
    TextView textview7;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container,false);
        textview2 = view.findViewById(R.id.textView2);
        textview3 = view.findViewById(R.id.textView3);
        textview4 = view.findViewById(R.id.textView4);
        textview5 = view.findViewById(R.id.textView5);
        textview6 = view.findViewById(R.id.textView6);
        textview7 = view.findViewById(R.id.textView7);
        DatabaseHelper db = new DatabaseHelper(this.getActivity());
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor c = database.rawQuery("SELECT * FROM Receipt_table", null);
        c.moveToFirst();
        while(!c.isAfterLast() ) {
            Record r = new Record();
            r.ID = c.getString(0);
            r.name = c.getString(1);
            r.date = c.getString(2);
            r.price = c.getString(3);
            r.Image = c.getBlob(4);
            r.warranty = c.getString(5);
            r.category = c.getString(6);
            LocalDate purchaseDate;
            purchaseDate = LocalDate.parse(r.date);
            Date todayDate = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String todayString = formatter.format(todayDate);
            LocalDate today = LocalDate.parse(todayString);
            String[] warrantyRaw = r.warranty.split(" ");
            int warrantyDayLeft = 0;
            switch (warrantyRaw[1]){
                case "Day":
                    warrantyDayLeft = Integer.parseInt(warrantyRaw[0]);
                    break;
                case "Year":
                    warrantyDayLeft = Integer.parseInt(warrantyRaw[0])*365;
                    break;

                case "Month":
                    warrantyDayLeft = Integer.parseInt(warrantyRaw[0])*30;
                    break;
            }
            LocalDate warrantyDue = purchaseDate.plusDays(warrantyDayLeft);
            r.spinner = Long.toString(ChronoUnit.DAYS.between(today, warrantyDue));
            RecordList.add(r);
            c.moveToNext();
        }
        anyChartView = (AnyChartView) view.findViewById(R.id.any_chart_view);
        //anyChartView2 = (AnyChartView) view.findViewById(R.id.any_chart_view2);
        setupPieChart();
        setupBarChart();
        return view;

    }

    public void setupPieChart(){
        Pie pie = AnyChart.pie();
        ArrayList<String> categoryList = new ArrayList<>();
        ArrayList<Integer> costList = new ArrayList<>();
        List<DataEntry> data = new ArrayList<>();

        pie.title("Expense tracking");

        for (int i = 0; i < RecordList.size(); i++){
            Record record = RecordList.get(i);
            int index = categoryList.indexOf(record.category);
            if (index == -1){
                categoryList.add(record.category);
                costList.add(Integer.parseInt(record.price));
            }else{
                int currentCost = costList.get(index);
                int newCost = currentCost + Integer.parseInt(record.price);
                costList.set(index, newCost);
            }
        }
        for (int i = 0; i < categoryList.size(); i++){
            data.add(new ValueDataEntry(categoryList.get(i), costList.get(i) ));
        }
        pie.data(data);
        anyChartView.setChart(pie);
    }


    public void setupBarChart(){

        ArrayList<StringIntTuple> warrantyList = new ArrayList<StringIntTuple>();

        for (int i = 0; i < RecordList.size(); i++){
            Record record = RecordList.get(i);
            int warranty = Integer.parseInt(record.spinner);
            if (warrantyList.size() < 3){
                warrantyList.add(new StringIntTuple(warranty, record.name));
            }else{
                StringIntTuple newTuple = new StringIntTuple(warranty, record.name);
                if (newTuple.compare(warrantyList.get(2))){
                    warrantyList.set(2, newTuple);
                }
            }
            Collections.sort(warrantyList, new StringIntTupleIntComparator());

        }
        //Cartesian barChart = AnyChart.bar();
        List<DataEntry> data = new ArrayList<>();
        for (StringIntTuple tuple : warrantyList){
            data.add(new ValueDataEntry(tuple.getStringValue(), tuple.getIntValue()));
            Log.e("setupBarChart: ", data.toString() );
        }

        try {
            StringIntTuple tuple1 = warrantyList.get(0);
            textview2.setText(tuple1.stringValue);
            textview3.setText(Integer.toString(tuple1.intValue) + "days");

            StringIntTuple tuple2 = warrantyList.get(1);
            textview4.setText(tuple2.stringValue);
            textview5.setText(Integer.toString(tuple2.intValue) + "days");

            StringIntTuple tuple3 = warrantyList.get(2);
            textview6.setText(tuple3.stringValue);
            textview7.setText(Integer.toString(tuple3.intValue) + "days");
        } catch (Exception e) {
            e.printStackTrace();
        }


//        data.add(new ValueDataEntry("Rouge", 80540));
//        data.add(new ValueDataEntry("Foundation", 94190));
//        data.add(new ValueDataEntry("Mascara", 102610));
//        data.add(new ValueDataEntry("Lip gloss", 110430));
//        data.add(new ValueDataEntry("Lipstick", 128000));
//        data.add(new ValueDataEntry("Nail polish", 143760));
//        data.add(new ValueDataEntry("Eyebrow pencil", 170670));
//        data.add(new ValueDataEntry("Eyeliner", 213210));
//        data.add(new ValueDataEntry("Eyeshadows", 249980));
//
//        barChart.animation(true);
//
//        barChart.yAxis(0d).title("days");
//        Linear xAxis1 = barChart.xAxis(1d);
//        xAxis1.enabled(true);
//        xAxis1.orientation(Orientation.RIGHT);
//
//        barChart.bar(data);
//
//
//
//        anyChartView2.setChart(barChart);
    }

}

