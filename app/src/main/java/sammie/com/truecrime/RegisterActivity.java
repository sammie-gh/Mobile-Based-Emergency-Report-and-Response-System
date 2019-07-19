package sammie.com.truecrime;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText et[] = new EditText[6];
    int ids[] = {R.id.reg_name, R.id.reg_age, R.id.reg_cno, R.id.reg_email, R.id.reg_pass, R.id.reg_re_pass};
    String values[] = new String[et.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        for (int i = 0; i < et.length; i++) {
            et[i] = (EditText) findViewById(ids[i]);
        }
    }

    public void registerUser(View v) {
        int i;
        for (i = 0; i < et.length; i++) {
            values[i] = et[i].getText().toString().trim();
            if (values[i].isEmpty()) {
                et[i].requestFocus();
                et[i].setError("Empty");
                break;
            }
        }
        if (i == et.length) {
            if (values[4].equals(values[5])) {
                int age = Integer.parseInt(values[1]);
                long cno = Long.parseLong(values[2]);
                MyDatabase my = new MyDatabase(this);
                SQLiteDatabase db = my.getWritableDatabase();
                String qry = "insert into user_details values ('" + values[0] + "'," + age + "," + cno + ",'" + values[3] + "','" + values[4] + "')";
                db.execSQL(qry);
                Toast.makeText(this, "Details Inserted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                et[5].requestFocus();
                et[5].setError("Not Matching");
            }
        } else {
            Toast.makeText(this, "Enter Data", Toast.LENGTH_SHORT).show();
        }
    }
}
