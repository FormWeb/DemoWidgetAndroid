package com.example.demowidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.RemoteViews
import com.example.demowidget.R

/**
 * Implementation of App Widget functionality.
 */
class CounterWidget : AppWidgetProvider() {
    companion object {
        const val ADD = "ADD"
        const val REMOVE = "REMOVE"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("hello", "hello")


        if (intent != null && context != null) {
            val appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context.packageName, this::class.java.name)
            )

            val sharedPref = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE)
            val counter : Int = sharedPref.getInt(context.getString(R.string.pref_counter), 0)

            when(intent.action) {
                ADD -> {
                    changeCounterValue(context, sharedPref, counter + 1)
                }
                REMOVE -> {
                    changeCounterValue(context, sharedPref, counter - 1)
                }
            }

            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
        }
    }

    private fun changeCounterValue(context: Context, sharedPref: SharedPreferences, newCounter: Int) {
        sharedPref.edit().putInt(context.getString(R.string.pref_counter), newCounter).apply()
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val widgetText = context.getString(R.string.appwidget_text)

    // Get counter via shared pref
    val sharedPref = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE)
    val counter : Int = sharedPref.getInt(context.getString(R.string.pref_counter), 0)

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.counter_widget)

    views.setOnClickPendingIntent(R.id.button_add_widget, getPendingIntentAdd(context))
    views.setOnClickPendingIntent(R.id.button_remove_widget, getPendingIntentRemove(context))
    views.setTextViewText(R.id.counter_widget, counter.toString())

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun getPendingIntentRemove(context: Context): PendingIntent? {
    val intent = Intent(context, CounterWidget::class.java)
    intent.action = CounterWidget.REMOVE
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}

fun getPendingIntentAdd(context: Context): PendingIntent? {
    val intent = Intent(context, CounterWidget::class.java)
    intent.action = CounterWidget.ADD
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}
