package dimasg.drawi;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dimasg.drawi.schemes.Meta;
import dimasg.drawi.schemes.NiceAsyncTask;
import dimasg.drawi.tasks.InitDataTask;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private MainActivity activity;
    private ListView listApps;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        listApps = findViewById(R.id.listApps);
        pm = getPackageManager();

        String category = getIntent().getStringExtra("category");
        if (category == null) {
            showText("Hello world!");
        } else {
            showApps(category);
        }
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
    }

    private void showText(String text) {
        List<String> labels = new ArrayList<>();
        List<ResolveInfo> infos = new ArrayList();
        List<Bitmap> icons = new ArrayList();

        labels.add(text);
        infos.add(null);
        icons.add(null);

        ListAppAdapter adapter = new ListAppAdapter(activity, pm, labels, infos, icons);
        listApps.setAdapter(adapter);
    }

    private void showApps(final String category) {
        final InitDataTask task = new InitDataTask(this);
        task.setPackageManager(pm);
        task.setCategory(category);
        task.setCallback(new NiceAsyncTask.Callback<Void>() {
            @Override
            public void callback(Void result, Exception error) {
                if (error != null) {
                    showText("Error");
                    Log.i(TAG, Log.getStackTraceString(error));
                } else {
                    List<String> labels = new ArrayList();
                    List<ResolveInfo> infos = new ArrayList();
                    List<Bitmap> icons = new ArrayList();
                    for (Map.Entry<String, String> sortedPair : task.sortedMap.entrySet()) {
                        String key = sortedPair.getValue();
                        Meta meta = task.metaMap.get(key);
                        labels.add(meta.label);
                        infos.add(task.infoMap.get(key));
                        icons.add(BitmapFactory.decodeByteArray(meta.icon, 0, meta.icon.length));
                    }

                    if (labels.size() == 0) {
                        showText("No " + category + " apps");
                    } else {
                        ListAppAdapter adapter = new ListAppAdapter(activity, pm, labels, infos, icons);
                        listApps.setAdapter(adapter);
                    }
                }
            }
        });

        showText("Loading...");
        task.execute();
    }
}
