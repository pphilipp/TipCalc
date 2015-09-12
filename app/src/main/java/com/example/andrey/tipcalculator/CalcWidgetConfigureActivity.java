package com.example.andrey.tipcalculator;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * The configuration screen for the {@link CalcWidget CalcWidget} AppWidget.
 */
public class CalcWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    private static final String PREFS_NAME = "com.example.andrey.tipcalculator.CalcWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private double customPercent = 0.3; // initial custom tip percentage
    SeekBar seekBar = null;
    TextView configActivityPercentage = null;

    public CalcWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.calc_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        //Init customise element for widget.
        seekBar = (SeekBar) findViewById(R.id.config_activity_seekBar);
        seekBar.setOnSeekBarChangeListener(customSeekBarListener);
        configActivityPercentage = (TextView) findViewById(R.id.config_activity_percentage);
//        updateCustom(); //update the custom tip TextViews  <<<<BUG on the 30%>>>

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        configActivityPercentage.setText(loadTitlePref(CalcWidgetConfigureActivity.this, mAppWidgetId));
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = CalcWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = configActivityPercentage.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            CalcWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };





    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }







    private void updateCustom() {
        // show customPercent in percentCustomTextView formatted as %
        configActivityPercentage.setText(percentFormat.format(customPercent));
    }

    // called when the user changes the position of SeekBar
    private SeekBar.OnSeekBarChangeListener customSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        // update customPercent, then call updateCustom
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // sets customPercent to position of the SeekBar's thumb
            customPercent = progress / 100.0;
            updateCustom(); // update the custom tip TextViews
        } // end method onProgressChanged

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {} // end method onStartTrackingTouch

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {} // end method onStopTrackingTouch
    }; // end OnSeekBarChangeListener

}

