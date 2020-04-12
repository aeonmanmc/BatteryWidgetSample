package my.aeonmanmc.batterylev;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.RemoteViews;

public class BatteryLev extends AppWidgetProvider {

    private static final String REQUEST_UPDATE = "requestUpdate";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        updateViews(context, calculateBatteryLevel(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.battery_lev_widget);

        // update all app widgets
        for (int appWidgetId : appWidgetIds) {
            rv.setOnClickPendingIntent(R.id.batteryImage, getPendingSelfIntent(context, REQUEST_UPDATE));
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (REQUEST_UPDATE.equals(intent.getAction())){
            updateViews(context, calculateBatteryLevel(context));
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {

        Intent intent = new Intent(context, BatteryLev.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private int calculateBatteryLevel(Context context) {

        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        return level * 100 / scale;
    }

    private void updateViews(Context context, int batteryLevel) {

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.battery_lev_widget);
        rv.setTextViewText(R.id.batteryText, batteryLevel + "%");

        if(batteryLevel >= 70){
            rv.setImageViewResource(R.id.batteryImage, R.drawable.image100);
        } else if(batteryLevel >= 40){
            rv.setImageViewResource(R.id.batteryImage, R.drawable.image70);
        } else if(batteryLevel >= 10){
            rv.setImageViewResource(R.id.batteryImage, R.drawable.image40);
        } else {
            rv.setImageViewResource(R.id.batteryImage, R.drawable.image10);
        }

        ComponentName componentName = new ComponentName(context, BatteryLev.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(componentName, rv);
    }

}
