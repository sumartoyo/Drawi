package dimasg.drawi.schemes;

import android.content.Context;
import android.os.AsyncTask;

public class NiceAsyncTask<S, T> extends AsyncTask<S, Void, T> {

    protected Context context;
    protected Callback<T> callback;
    protected Exception error;

    public NiceAsyncTask(Context context, Callback<T> callback) {
        super();
        this.context = context;
        this.callback = callback;
    }

    public NiceAsyncTask(Context context) {
        this(context, new Callback<T>() {
            @Override
            public void callback(T result, Exception error) {
            }
        });
    }

    public void setCallback(Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    protected T doInBackground(S... params) {
        try {
            error = null;
            if (params.length == 0) {
                return run(null);
            } else {
                return run(params[0]);
            }
        } catch (Exception error) {
            this.error = error;
            return null;
        }
    }

    protected T run(S param) throws Exception {
        return null;
    }

    @Override
    protected void onPostExecute(T result) {
        callback.callback(result, error);
    }

    public interface Callback<T> {
        void callback(T result, Exception error);
    }
}
