package sammie.com.truecrime;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import io.paperdb.Paper;
import jonathanfinerty.once.Once;
import sammie.com.truecrime.Common.Common;

import static jonathanfinerty.once.Once.beenDone;
import static jonathanfinerty.once.Once.markDone;



public class SplashScreenActivity extends AppCompatActivity {

    final SplashScreenActivity sPlashScreen = this;
    private Thread mSplashThread;
    String isSubscribe ;
    private static final String SHOW_FRESH_INSTALL_DIALOG = "FreshInstallDialogHome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Once.initialise(this);
        Once.markDone("Application Launched");
        Once.clearAll();

        //  remember state of check
        Paper.init(this);
        isSubscribe = Paper.book().read("sub_splash");


        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false")){

            mSplashThread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(2000);
                        }
                    } catch (InterruptedException ex) {
                    }
                    finish();

                    Intent intent = new Intent();
                    intent.setClass(SplashScreenActivity.this, PhoneAuth.class);
                    startActivity(intent);
                }
            };
            mSplashThread.start();
        }
        else {
            Intent intent = new Intent();
            intent.setClass(SplashScreenActivity.this, PhoneAuth.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}