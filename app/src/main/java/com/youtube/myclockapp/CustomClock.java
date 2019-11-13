package com.youtube.myclockapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class CustomClock extends View {

    private int height;
    private int width;


    private Canvas canvas;
    private Paint paint;
    private Bitmap backgroundBitmap;

    private static int MAX_WIDTH;
    private static String CUSTOM_FONT_NAME;
    private int numeralSpacing = 0;
    private int padding = 0;
    private int handTruncation, hourHandTruncation = 0;
    private int radius = 0;
    Rect rect = new Rect();


    private void drawHand(Canvas canvas, double loc, boolean isHour) {
        Paint paint = new Paint();
        paint.setStrokeWidth(12);
        paint.setColor(Color.RED);
        double angle = Math.PI * loc / 30 - Math.PI / 2;
        int handRadius = isHour ? radius - handTruncation - hourHandTruncation : radius - handTruncation;
        canvas.drawLine(width / 2, height / 2,
                (float) (width / 2 + Math.cos(angle) * handRadius),
                (float) (height / 2 + Math.sin(angle) * handRadius),
                paint);
    }



    private void drawCenter(Canvas canvas) {

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2 , 12, paint);

    }

    // kraj njegovih


    // Konstruktori klase - programabilno
    public CustomClock(Context context) {
        super(context);
        init(context, null);
    }

    // Konstruktor za deklarativno - iz layout fajla
    public CustomClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    // za inicijalizaciju
    private void init(Context context, AttributeSet attrs) {
        // inicijalizacija objekata CustomView elementa
        paint = new Paint();
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.clock);

        // za citanje custom attrs
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomClock);

            MAX_WIDTH = typedArray.getInt(R.styleable.CustomClock_maxWidth, 100);
            CUSTOM_FONT_NAME = typedArray.getString(R.styleable.CustomClock_customFontName);

            // VEOMA JE BITNO RECIKLIRATI
            typedArray.recycle();  // nakon što ovo uradimo nije moguće čitati custom atribute
        }

    }

    private static final int BASE_WIDTH = 300;
    private static final int BASE_HEIGHT = 300;

    /******************* za mjerenje ********/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // kod za interpretiranje vrijednosti
        int w;
        int h;
        if (widthMode == MeasureSpec.EXACTLY) {   // znači da je roditelj proslijedio tačnu širinu
            w = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {  // NE VIŠE OD VRIJEDNOSTI KOJU SAM TI PROSLIJEDIO
            w = Math.min(BASE_WIDTH, widthSize);
        } else {
            w = BASE_WIDTH;
        }


        //ZA VISINU
        if (heightMode == MeasureSpec.EXACTLY) {   // znači da je roditelj proslijedio tačnu širinu
            h = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {  // NE VIŠE OD VRIJEDNOSTI KOJU SAM TI PROSLIJEDIO
            h = Math.min(BASE_HEIGHT, heightSize);
        } else {
            h = BASE_WIDTH;
        }



        /********** Neophodno osigurati da visina i širina budu jednake!*****/
        int smaller = Math.min(w, h);       // da sam odabrao veću vrijednost, možda element ne bi bio adekvatno smješten unutar displeja!
        w = smaller;
        h = smaller;
        
        
        // za padding 
        if (!areAllEqual(getPaddingTop(), getPaddingBottom(), getPaddingLeft(), getPaddingRight())){

            w = smaller - (getPaddingTop() + getPaddingBottom());
            h = smaller - (getPaddingRight() + getPaddingLeft());

        }
        


        setMeasuredDimension(w, h);  // ne pozivanjem ove metode dobit ćemo izuzetak!!!



    }


    /******************* za mjerenje ********/


    //
    private float centX;
    private float centY;

    private Rect lineBounds = new Rect();

    private static final float VERTICAL_CLOCK_CENTER = 0.19f;

    // za dolazak do centra

    private void centerLine(){

        centY = backgroundBounds.top + (backgroundBounds.height() * VERTICAL_CLOCK_CENTER);   // UDALJENOST VRHA OD CENTRA
        centX = backgroundBounds.left + backgroundBounds.width()/2;  // osigurali da će biti centrirane kazaljke

    }


    // za poziciju pozadine
    Rect backgroundBounds = new Rect();

    // za postavljanje veličine i pozicije sadržaja
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // za postavljanje veličine i pozicije sadržaja

        width = w;
        height = h;

        // utvrditi poziciju crtanja slike

        backgroundBounds.left = getPaddingLeft();
        backgroundBounds.top = getPaddingTop();
        backgroundBounds.right = w - getPaddingRight();
        backgroundBounds.bottom = h - getPaddingBottom();

        //lokacija na kojoj ćemo crtaiti liniju
        centerLine();


    }


    /******************** Logika za crtanje *********/

    @Override
    protected void onDraw(Canvas canvas) {

        // clock
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 70;
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        handTruncation = min / 20;
        hourHandTruncation = min / 7;
        paint = new Paint();

        // draw center
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2 , 12, paint);

        // draw Hands

            Calendar c = Calendar.getInstance();
            float hour = c.get(Calendar.HOUR_OF_DAY);
            hour = hour > 12 ? hour - 12 : hour;
            drawHand(canvas, (hour + c.get(Calendar.MINUTE) / 60) * 5f, true);
            drawHand(canvas, c.get(Calendar.MINUTE), false);
            drawHand(canvas, c.get(Calendar.SECOND), false);



        postInvalidateDelayed(500);
        invalidate();


        canvas.drawBitmap(backgroundBitmap,null, backgroundBounds, null);



    }

    /******************** Logika za crtanje *********/




    public boolean areAllEqual (int... values){
        if (values.length == 0){
            return true;
        }
        
        int checkValue = values[0];
        for (int i = 1; i < values.length; i++) {

            if (values[i] != checkValue){
                return false;
            }
        }

        return true;

    }




}
