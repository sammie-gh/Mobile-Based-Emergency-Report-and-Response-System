package sammie.com.truecrime.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShakeReceiver extends BroadcastReceiver {
    private  final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && intent.getAction().equals("shake.detector")) {
//            Toast.makeText(context, "device shake", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "device shaked");
        }
    }
}
