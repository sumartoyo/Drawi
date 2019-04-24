package dimasg.drawi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAppAdapter extends ArrayAdapter<String> {

    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private PackageManager pm;
    private List<ResolveInfo> infos;
    private List<Bitmap> icons;

    public ListAppAdapter(Activity activity, PackageManager pm, List<String> labels, List<ResolveInfo> infos, List<Bitmap> icons) {
        super(activity, R.layout.row_app, labels);
        this.activity = activity;
        this.pm = pm;
        this.infos = infos;
        this.icons = icons;
    }

    @Override
    public View getView(int index, View view, ViewGroup group) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.row_app, null, true);
        String label = getItem(index);
        final ResolveInfo info = infos.get(index);
        Bitmap icon = icons.get(index);

        TextView text = row.findViewById(R.id.textApp);
        text.setText(label == null ? "[LABEL]" : label);

        if (icon != null) {
            ImageView image = row.findViewById(R.id.imageApp);
            image.setImageBitmap(icon);
        }

        if (info != null) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = pm.getLaunchIntentForPackage(info.activityInfo.packageName);
                    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.getApplicationContext().startActivity(intent);
                    activity.finishAndRemoveTask();
                }
            });
        }

        return row;
    }
}
