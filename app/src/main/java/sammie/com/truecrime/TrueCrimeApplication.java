package sammie.com.truecrime;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TrueCrimeApplication extends Application {

    private static TrueCrimeApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
