package dimasg.drawi.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dimasg.drawi.schemes.Meta;
import dimasg.drawi.schemes.NiceAsyncTask;

public class InitDataTask extends NiceAsyncTask<Void, Void> {

    private final String TAG = getClass().getSimpleName();
    private PackageManager pm;
    private String category;
    public Map<String, ResolveInfo> infoMap;
    public Map<String, Meta> metaMap;
    public Map<String, String> sortedMap;

    public InitDataTask(Context context) {
        super(context);
        infoMap = new HashMap();
        metaMap = new HashMap();
        sortedMap = new TreeMap();
    }

    public void setPackageManager(PackageManager pm) {
        this.pm = pm;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    protected Void run(Void param) throws Exception {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infoList = pm.queryIntentActivities(mainIntent, 0);

        infoMap = new HashMap();
        for (ResolveInfo info : infoList) {
            if (info.activityInfo.packageName.equals(context.getApplicationContext().getPackageName())) {
                continue;
            }
            infoMap.put(info.activityInfo.packageName + " " + info.activityInfo.name, info);
        }

        loadMetaMap();

        // remove uninstalled
        String[] keys = new String[metaMap.size()];
        keys = metaMap.keySet().toArray(keys);
        for (String key : keys) {
            if (!infoMap.containsKey(key)) {
                metaMap.remove(key);
            }
        }

        for (String key : infoMap.keySet()) {
            ResolveInfo info = infoMap.get(key);
            Long timestamp = new File(info.activityInfo.applicationInfo.sourceDir).lastModified();
            Meta meta = metaMap.containsKey(key) ? metaMap.get(key) : null;
            if (meta != null) {
                if (Long.compare(meta.timestamp, timestamp) == 0) {
                    continue; // app isn't modified, use cache
                }
            }

            if (meta == null) {
                meta = new Meta();
            }
            meta.label = info.loadLabel(pm).toString();

            if (checkIsLabelInCategory(category, meta.label)) {
                meta.timestamp = timestamp;
                Drawable drawable = info.loadIcon(pm);
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                meta.icon = baos.toByteArray();
            }

            metaMap.put(key, meta);
        }

        saveMetaMap();

        sortedMap = new TreeMap();
        for (Map.Entry<String, Meta> metaPair : metaMap.entrySet()) {
            String label = metaPair.getValue().label.toLowerCase();
            if (checkIsLabelInCategory(category, label)) {
                String key = metaPair.getKey();
                sortedMap.put(label + "   " + key, key);
            }
        }

        return null;
    }

    private void loadMetaMap() {
        metaMap = new HashMap();
        FileInputStream fis = null;
        ObjectInputStream iis = null;
        try {
            fis = context.openFileInput("metaMap.bin");
            iis = new ObjectInputStream(fis);
            metaMap = (HashMap<String, Meta>) iis.readObject();
        } catch (Exception error) {
            Log.i(TAG, Log.getStackTraceString(error));
        } finally {
            try { iis.close(); } catch (Exception error) {}
            try { fis.close(); } catch (Exception error) {}
        }
    }

    private void saveMetaMap() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput("metaMap.bin", context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(metaMap);
        } catch (Exception error) {
            Log.i(TAG, Log.getStackTraceString(error));
        } finally {
            try { oos.close(); } catch (Exception error) {}
            try { fos.close(); } catch (Exception error) {}
        }
    }

    public static boolean checkIsLabelInCategory(String category, String label) {
        char prefix = label.toUpperCase().charAt(0);
        if (category == "123") {
            if (prefix < 'A' || prefix > 'Z') {
                return true;
            }
        } else {
            if (prefix >= category.charAt(0) && prefix <= category.charAt(category.length() - 1)) {
                return true;
            }
        }
        return false;
    }
}
