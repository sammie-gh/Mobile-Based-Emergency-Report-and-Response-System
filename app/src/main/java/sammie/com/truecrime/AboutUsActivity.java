package sammie.com.truecrime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
    }

//    public void opensite(View v){
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nareshit.com/"));
//        startActivity(browserIntent);
//    }
}
