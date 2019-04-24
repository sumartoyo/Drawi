package dimasg.drawi;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MainWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        bindOpenActivity(context, views, R.id.button123, "123");
        bindOpenActivity(context, views, R.id.buttonABC, "ABC");
        bindOpenActivity(context, views, R.id.buttonDEF, "DEF");
        bindOpenActivity(context, views, R.id.buttonGHI, "GHI");
        bindOpenActivity(context, views, R.id.buttonJKL, "JKL");
        bindOpenActivity(context, views, R.id.buttonMNO, "MNO");
        bindOpenActivity(context, views, R.id.buttonPQRS, "PQRS");
        bindOpenActivity(context, views, R.id.buttonTUV, "TUV");
        bindOpenActivity(context, views, R.id.buttonWXYZ, "WXYZ");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static private void bindOpenActivity(Context context, RemoteViews views, int buttonId, String category) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("category", category);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(buttonId, pendingIntent);
    }
}

