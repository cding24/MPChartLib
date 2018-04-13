
package com.github.mikephil.charting.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.TypedValue;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Superclass of all render classes for the different data types (line, bar, ...).
 * 定义绘制值的实现，可以绘制横竖高亮线的接口等
 *
 * @author Philipp Jahoda
 * modified by linghu for 显示值的背景的上下显示处理
 */
public abstract class DataRenderer extends Renderer {

    /**
     * the animator object used to perform animations on the chart data
     */
    protected ChartAnimator mAnimator;

    /**
     * main paint object used for rendering
     */
    protected Paint mRenderPaint;

    /**
     * paint used for highlighting values
     */
    protected Paint mHighlightPaint;

    protected Paint mDrawPaint;

    /**
     * paint object for drawing values (text representing values of chart entries)
     */
    protected Paint mValuePaint;

    //added by linghu=======================添加值的背景的Paint=====================================
    protected Paint mPointBgPaint;
    //======================end=====================================================================

    public DataRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(viewPortHandler);
        this.mAnimator = animator;

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Style.FILL);

        mDrawPaint = new Paint(Paint.DITHER_FLAG);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(Color.rgb(63, 63, 63));
        mValuePaint.setTextAlign(Align.CENTER);
        mValuePaint.setTextSize(Utils.convertDpToPixel(9f));

        mPointBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointBgPaint.setColor(Color.rgb(63, 63, 63));
        mPointBgPaint.setTextSize(Utils.convertDpToPixel(9f));

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));
    }

    protected boolean isDrawingValuesAllowed(ChartInterface chart) {
        return chart.getData().getEntryCount() < chart.getMaxVisibleCount() * mViewPortHandler.getScaleX();
    }

    //added by linghu========调整绘制值的背景值而添加的如下内容=====================================
    private Context mContext = null;
    private int xOffset1; //42
    private int xOffset2; //40
    private int xOffset3; //30

    private int yOffset1; //35
    private int yOffset2; //10
    private int yOffset3; //28
    private int yOffset4; //70
    private int centerY; //60
    private int center; //10
    public static final int NO_BG_VALUE = 0;
    public static final int TOP_BG_VALUE = 1;
    public static final int BOTTOM_BG_VALUE = 2;
    private int valueBgStyle = TOP_BG_VALUE;
    private int valueBgColor;
    //==================end by linghu===============================================================

    public void setValueBgStyle(int style, int color, Context context){
        valueBgStyle = style;
        valueBgColor = color;
        //added by linghu===========================================================================
        if(mContext == null){
            mContext = context;
            xOffset1 =  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15.3f, mContext.getResources().getDisplayMetrics()); //42
            xOffset2 =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14.5f, mContext.getResources().getDisplayMetrics()); //40
            xOffset3 =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11f, mContext.getResources().getDisplayMetrics()); //30

            yOffset1 =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12.7f, mContext.getResources().getDisplayMetrics()); //35
            yOffset2 =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.7f, mContext.getResources().getDisplayMetrics()); //10
            yOffset3 =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics()); //28
            yOffset4 =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25.5f, mContext.getResources().getDisplayMetrics()); //70
            centerY = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 21.8f, mContext.getResources().getDisplayMetrics()); //60
            center = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.7f, mContext.getResources().getDisplayMetrics()); //10
        }
        //end added by linghu=======================================================================
    }

    /**
     * Returns the Paint object this renderer uses for drawing the values
     * (value-text).
     *
     * @return
     */
    public Paint getPaintValues() {
        return mValuePaint;
    }

    /**
     * Returns the Paint object this renderer uses for drawing highlight
     * indicators.
     *
     * @return
     */
    public Paint getPaintHighlight() {
        return mHighlightPaint;
    }

    /**
     * Returns the Paint object used for rendering.
     *
     * @return
     */
    public Paint getPaintRender() {
        return mRenderPaint;
    }

    /**
     * Applies the required styling (provided by the DataSet) to the value-paint
     * object.
     *
     * @param set
     */
    protected void applyValueTextStyle(IDataSet set) {
        mValuePaint.setTypeface(set.getValueTypeface());
        mValuePaint.setTextSize(set.getValueTextSize());
    }

    /**
     * Initializes the buffers used for rendering with a new size. Since this
     * method performs memory allocations, it should only be called if
     * necessary.
     */
    public abstract void initBuffers();

    /**
     * Draws the actual data in form of lines, bars, ... depending on Renderer subclass.
     *
     * @param c
     */
    public abstract void drawData(Canvas c);

    /**
     * Loops over all Entrys and draws their values.
     *
     * @param c
     */
    public abstract void drawValues(Canvas c);

    /**
     * Draws the value of the given entry by using the provided IValueFormatter.
     * 绘制各个点的值
     *
     * @param c            canvas
     * @param formatter    formatter for custom value-formatting
     * @param value        the value to be drawn
     * @param entry        the entry the value belongs to
     * @param dataSetIndex the index of the DataSet the drawn Entry belongs to
     * @param x            position
     * @param y            position
     * @param color
     */
    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
        mValuePaint.setColor(color);
        //added by linghu===================================================
        mPointBgPaint.setColor(valueBgColor);

        if(valueBgStyle == TOP_BG_VALUE){ //绘制值在绘制的线的上边
            //-20  +20是为了调整气泡的宽度
            //绘制气泡背景
            if(value >= 100){ //值占3位数以上
//                c.drawRoundRect(new RectF(x-42, y-35, x+42, y+10), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset1-20, y-yOffset1, x+xOffset1+20, y+yOffset2), center, center, mPointBgPaint);
            }else if(value >= 10){ //值占2位数
//                c.drawRoundRect(new RectF(x-40, y-35, x+40, y+10), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset2, y-yOffset1, x+xOffset2, y+yOffset2), center, center, mPointBgPaint);
            }else{ //值占1位数
//                c.drawRoundRect(new RectF(x-30, y-35, x+30, y+10), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset3, y-yOffset1, x+xOffset3, y+yOffset2), center, center, mPointBgPaint);
            }
            //绘制文字
            c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, y, mValuePaint);
        }else if(valueBgStyle == BOTTOM_BG_VALUE){ //绘制值在绘制的线的下边
            //绘制气泡背景
            if(value >= 100){
//                c.drawRoundRect(new RectF(x-42, y+28, x+42, y+70), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset1-20, y+yOffset3, x+xOffset1+20, y+yOffset4), center, center, mPointBgPaint);
            }else if(value >= 10){
//                c.drawRoundRect(new RectF(x-40, y+28, x+40, y+70), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset2, y+yOffset3, x+xOffset2, y+yOffset4), center, center, mPointBgPaint);
            }else{
//                c.drawRoundRect(new RectF(x-30, y+28, x+30, y+70), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset3, y+yOffset3, x+xOffset3, y+yOffset4), center, center, mPointBgPaint);
            }
            //绘制文字
//            c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, y+60, mValuePaint);
            c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, y+centerY, mValuePaint);
        }else{ //默认，同TOP_BG_VALUE一样方式
            if(value >= 100){
//                c.drawRoundRect(new RectF(x-42, y-35, x+42, y+10), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset1-20, y-yOffset1, x+xOffset1+20, y+yOffset2), center, center, mPointBgPaint);
            }else if(value >= 10){
//                c.drawRoundRect(new RectF(x-40, y-35, x+40, y+10), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset2, y-yOffset1, x+xOffset2, y+yOffset2), center, center, mPointBgPaint);
            }else{
//                c.drawRoundRect(new RectF(x-30, y-35, x+30, y+10), 10, 10, mPointBgPaint);
                c.drawRoundRect(new RectF(x-xOffset3, y-yOffset1, x+xOffset3, y+yOffset2), center, center, mPointBgPaint);
            }
            //绘制文字
            c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, y, mValuePaint);
        }
    }

    /**
     * Draws any kind of additional information (e.g. line-circles).
     *
     * @param c
     */
    public abstract void drawExtras(Canvas c);

    /**
     * Draws all highlight indicators for the values that are currently highlighted.
     *
     * @param c
     * @param indices the highlighted values
     */
    public abstract void drawHighlighted(Canvas c, Highlight[] indices);

}
