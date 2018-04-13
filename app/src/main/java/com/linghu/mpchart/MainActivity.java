package com.linghu.mpchart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.linghu.mpchart.entity.EnrollData;
import com.linghu.mpchart.entity.EnrollDataItem;
import com.linghu.mpchart.utils.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 这是一个MPChartLib库的使用，对于该库已经稍加修改。
 * created by linghu on 2016/4/6
 *
 */
public class MainActivity extends AppCompatActivity {
    private LineChart enrollChart;
    private TextView enrollXTitleTV;
    private TextView enrollYTitleTV;
    private ViewGroup enrollNoDataLay;

    private int highLineColor;
    private int highCircleColor;
    private int lowLineColor;
    private int lowCircleColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enrollChart = (LineChart) findViewById(R.id.enroll_chart);
        enrollXTitleTV = (TextView) findViewById(R.id.biye_xtitle);
        enrollYTitleTV = (TextView) findViewById(R.id.biye_ytitle);
        enrollNoDataLay = (ViewGroup) findViewById(R.id.enroll_nodata_lay);
        initChartView();

        EnrollData datas = new EnrollData();
        datas.picixian = new ArrayList<>();
        datas.zuidifen = new ArrayList<>();
        datas.picixian.add(new EnrollDataItem(2015, 543));
        datas.picixian.add(new EnrollDataItem(2016, 551));
        datas.picixian.add(new EnrollDataItem(2017, 545));
        datas.zuidifen.add(new EnrollDataItem(2015, 697));
        datas.zuidifen.add(new EnrollDataItem(2016, 687));
        datas.zuidifen.add(new EnrollDataItem(2017, 708));

        combineLineChart(datas);
    }

    /**
     * 初始化图表控件
     */
    private void initChartView(){
        enrollChart.setNoDataText(""); //如果没有数据的时候，会显示这个
        enrollChart.setDrawBorders(false); //是否在折线图上添加边框
        enrollChart.getDescription().setEnabled(false); // no description text数据描述
        enrollChart.setDrawGridBackground(false);  // enable/disable grid background,是否显示表格颜色
        enrollChart.setTouchEnabled(false); //enable touch gestures 设置是否可以触摸
        enrollChart.setDragEnabled(false); //enable scaling and dragging 是否可以拖拽
        enrollChart.setScaleEnabled(false); //是否可以缩放
        enrollChart.setPinchZoom(false); // if disabled, scaling can be done on x- and y-axis separately
        enrollChart.setBackgroundColor(Color.parseColor("#ffffff")); // 设置整个图表的背景
        highLineColor = Color.parseColor("#ff4488");
        highCircleColor = Color.parseColor("#ff0044");
        lowLineColor = Color.parseColor("#ffbb00");
        lowCircleColor = Color.parseColor("#ff8800");
    }

    private void initLineAxis(LineChart lineChart, LineData lineData, int xMax, int xMin, int yMax, int yMin) {
        //设置X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.parseColor("#009eff"));
        xAxis.setAxisLineWidth(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴的位置
        //设置数据的显示格式
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return Config.dformater.format(v);
            }
        });

        if (xMax - xMin < 2) {
            xMin = xMax - 2;
        }
        xAxis.setAxisMinimum(xMin - 0.4f);
        xAxis.setAxisMaximum(xMax + 0.4f);
        //根据动态的数据而设置的
        xAxis.setLabelCount(xMax - xMin);

        //设置Y轴
        lineChart.getAxisRight().setEnabled(false); //右边纵坐标轴显示与否
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(false);

        leftAxis.setAxisLineColor(Color.parseColor("#009eff"));
        leftAxis.setAxisLineWidth(1);
        //设置数据的显示格式
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return Config.dformater.format(v);
            }
        });
        int height = yMax - yMin;
        leftAxis.setAxisMinimum(yMin - height * 0.3f);
        leftAxis.setAxisMaximum(yMax + height * 0.3f);
        leftAxis.setLabelCount(0);

        //add data
        lineChart.setData(lineData); // 设置数据

        //设置折线的标题的样式， legend即这个的图表的线指示器
        lineChart.getLegend().setEnabled(true); //false不显示折线的标题
        lineChart.animateX(600); // 立即执行的动画,x轴
    }

    /**
     * 组装往年录取情况的数据， 非浙江省
     */
    private void combineLineChart(EnrollData enrollData) {
        if (enrollData != null && (enrollData.zuidifen != null || enrollData.picixian != null)) {
            int maxValue = -1, minValue = -1;
            int maxYear = -1, minYear = -1;
            // x,y轴的数据
            List<Entry> zuidifenValues = new ArrayList<Entry>();
            if (enrollData.zuidifen != null && enrollData.zuidifen.size() > 0) {
                Collections.sort(enrollData.zuidifen, new Comparator<EnrollDataItem>() {
                    @Override
                    public int compare(EnrollDataItem o1, EnrollDataItem o2) {
                        if (o1.year < o2.year) {
                            return -1;
                        } else if (o1.year == o2.year) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });
                for (int t=0,len=enrollData.zuidifen.size(); t < len; t++) {
                    EnrollDataItem item = enrollData.zuidifen.get(t);
                    zuidifenValues.add(new Entry(item.year, item.value));
                    if (t == 0) {
                        maxValue = item.value;
                        minValue = item.value;
                        maxYear = item.year;
                        minYear = item.year;
                    } else {
                        if (maxValue < item.value) {
                            maxValue = item.value;
                        }
                        if (minValue > item.value) {
                            minValue = item.value;
                        }
                        if (maxYear < item.year) {
                            maxYear = item.year;
                        }
                        if (minYear > item.year) {
                            minYear = item.year;
                        }
                    }
                }
            }

            // x,y轴的数据
            List<Entry> picixianValues = new ArrayList<Entry>();
            if (enrollData.picixian != null && enrollData.picixian.size() > 0) {
                Collections.sort(enrollData.picixian, new Comparator<EnrollDataItem>() {
                    @Override
                    public int compare(EnrollDataItem o1, EnrollDataItem o2) {
                        if (o1.year < o2.year) {
                            return -1;
                        } else if (o1.year == o2.year) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });
                for (int t=0,len=enrollData.picixian.size(); t < len; t++) {
                    EnrollDataItem item = enrollData.picixian.get(t);
                    picixianValues.add(new Entry(item.year, item.value));
                    if(maxValue < 0){
                        maxValue = item.value;
                    }
                    if(minValue < 0){
                        minValue = item.value;
                    }
                    if(maxYear < 0){
                        maxYear = item.year;
                    }
                    if(minYear < 0){
                        minYear = item.year;
                    }

                    if (maxValue < item.value) {
                        maxValue = item.value;
                    }
                    if (minValue > item.value) {
                        minValue = item.value;
                    }
                    if (maxYear < item.year) {
                        maxYear = item.year;
                    }
                    if (minYear > item.year) {
                        minYear = item.year;
                    }
                }
            }

            // y轴的数据集合   用y轴的集合来设置参数
            LineDataSet picixianDataSet = null;
            if (picixianValues.size() > 0) {
                picixianDataSet = new LineDataSet(picixianValues, "批次线");
                picixianDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return Config.dformater.format(value);
                    }
                });
                picixianDataSet.setBgPopStyle(2, Color.parseColor("#0098ff"));
                picixianDataSet.setLineWidth(2f);  //线宽
                picixianDataSet.setCircleRadius(4f);  //圆圈大小
                picixianDataSet.setCircleColorHole(lowCircleColor);
                picixianDataSet.setCircleColor(lowCircleColor);  //圆圈的颜色
                picixianDataSet.setColor(lowLineColor);   //折线的颜色
                picixianDataSet.setValueTextColor(Color.parseColor("#ffffff"));  //显示点值的颜色
                picixianDataSet.setValueTextSize(12); //显示点值的字体大小
                picixianDataSet.setDrawValues(true); //绘制显示点的值
                picixianDataSet.setDrawHighlightIndicators(false);
                picixianDataSet.setMode(LineDataSet.Mode.LINEAR);
            }else{
                picixianDataSet = null;
            }
            // y轴的数据集合  用y轴的集合来设置参数
            LineDataSet zuidifenDataSet = null;
            if (zuidifenValues.size() > 0) {
                zuidifenDataSet = new LineDataSet(zuidifenValues, "最低分");
                zuidifenDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return Config.dformater.format(value);
                    }
                });
                zuidifenDataSet.setBgPopStyle(1, Color.parseColor("#0089ff"));
                zuidifenDataSet.setLineWidth(2f);  //线宽
                zuidifenDataSet.setCircleRadius(4f);  //圆圈大小
                zuidifenDataSet.setCircleColorHole(highCircleColor);  //圆圈的颜色
                zuidifenDataSet.setCircleColor(highCircleColor);  //圆圈的颜色
                zuidifenDataSet.setColor(highLineColor);   //折线的颜色
                zuidifenDataSet.setValueTextColor(Color.parseColor("#ffffff"));  //显示点值的颜色
                zuidifenDataSet.setValueTextSize(12); //显示点值的字体大小
                zuidifenDataSet.setDrawValues(true); //绘制显示点的值
                zuidifenDataSet.setDrawHighlightIndicators(false);
                zuidifenDataSet.setMode(LineDataSet.Mode.LINEAR);
            }else{
                zuidifenDataSet = null;
            }

            LineData scoreLineData = null;
            if (picixianDataSet != null && zuidifenDataSet != null) {
                scoreLineData = new LineData(zuidifenDataSet, picixianDataSet);
            } else if (picixianDataSet != null) {
                scoreLineData = new LineData(picixianDataSet);
            } else if (zuidifenDataSet != null) {
                scoreLineData = new LineData(zuidifenDataSet);
            }else{
                scoreLineData = null;
            }

            if (scoreLineData != null) {
                initLineAxis(enrollChart, scoreLineData, maxYear, minYear, maxValue, minValue);
                enrollNoDataLay.setVisibility(View.GONE);
                enrollChart.setVisibility(View.VISIBLE);
                enrollXTitleTV.setVisibility(View.VISIBLE);
                enrollYTitleTV.setVisibility(View.VISIBLE);
            } else {
                enrollChart.setVisibility(View.GONE);
                enrollXTitleTV.setVisibility(View.GONE);
                enrollYTitleTV.setVisibility(View.GONE);
                enrollNoDataLay.setVisibility(View.VISIBLE);
            }
        } else {
            enrollChart.setVisibility(View.GONE);
            enrollXTitleTV.setVisibility(View.GONE);
            enrollYTitleTV.setVisibility(View.GONE);
            enrollNoDataLay.setVisibility(View.VISIBLE);
        }
    }

}
