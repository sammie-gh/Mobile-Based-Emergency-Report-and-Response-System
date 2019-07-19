package sammie.com.truecrime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WelcomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;


    String email;
    TextView tv;
    EditText et1, et2, et3, et4, et5;
    String first_no, second_no, third_no, fourth_no, fivth_no;
    EditText etmail_1, etmail_2, etmail_3, etmail_4, etmail_5;
    String mail_1, mail_2, mail_3, mail_4, mail_5;
    String regEx = "^(([w-]+.)+[w-]+|([a-zA-Z]{1}|[w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]).([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]).([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[w-]+.)+[a-zA-Z]{2,4})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);








        et1 = (EditText) findViewById(R.id.number_1);
        et2 = (EditText) findViewById(R.id.number_2);
        et3 = (EditText) findViewById(R.id.number_3);
        et4 = (EditText) findViewById(R.id.number_4);
        et5 = (EditText) findViewById(R.id.number_5);

        etmail_1 = (EditText) findViewById(R.id.emergency_gmail_1);
        etmail_2 = (EditText) findViewById(R.id.emergency_gmail_2);
        etmail_3 = (EditText) findViewById(R.id.emergency_gmail_3);
        etmail_4 = (EditText) findViewById(R.id.emergency_gmail_4);
        etmail_5 = (EditText) findViewById(R.id.emergency_gmail_5);


        tv = (TextView) findViewById(R.id.tv3);
//        Intent i = getIntent();
//        Bundle b = i.getExtras();
//        email = b.getString("k1");
//        tv.setText(email);
    }

    public void next(View v) {

        ValidateEmailAddress(v);

        if (isValidPhone(et1.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Phone number is valid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Phone number is not valid", Toast.LENGTH_SHORT).show();
        }
        first_no = et1.getText().toString().trim();
        second_no = et2.getText().toString().trim();
        third_no = et3.getText().toString().trim();
        fourth_no = et4.getText().toString().trim();
        fivth_no = et5.getText().toString().trim();

        mail_1 = etmail_1.getText().toString().trim();
        mail_2 = etmail_2.getText().toString().trim();
        mail_3 = etmail_3.getText().toString().trim();
        mail_4 = etmail_4.getText().toString().trim();
        mail_5 = etmail_5.getText().toString().trim();

        if (!first_no.equals("")) {
            if (!mail_1.equals("")) {
                if (!second_no.equals("")) {
                    if (!mail_2.equals("")) {
                        if (!third_no.equals("")) {
                            if (!mail_3.equals("")) {
                                if (!fourth_no.equals("")) {
                                    if (!mail_4.equals("")) {
                                        if (!fivth_no.equals("")) {
                                            if (!mail_5.equals("")) {
                                                callintent();
                                            } else {
                                                Toast.makeText(this, "Enter Fifth Email", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(this, "Enter Fifth no.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(this, "Enter Fourth Email", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Enter Fourth No.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Enter Third Email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Enter Third No.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Enter Second Email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Enter Second No.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter First Email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter First No.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callintent() {

        Intent i = new Intent(getApplicationContext(), HomeActivity.class);

        i.putExtra("First Number", first_no = et1.getText().toString().trim());
        i.putExtra("Second Number", second_no = et2.getText().toString().trim());
        i.putExtra("Third Number", third_no = et3.getText().toString().trim());
        i.putExtra("Fourth Number", fourth_no = et4.getText().toString().trim());
        i.putExtra("Fifth Number", fivth_no = et5.getText().toString().trim());

        i.putExtra("FirstMail", mail_1);
        i.putExtra("SecondMail", mail_2);
        i.putExtra("ThirdMail", mail_3);
        i.putExtra("FourthMail", mail_4);
        i.putExtra("FifthMail", mail_5);

        startActivity(i);
    }

    public boolean isValidPhone(CharSequence phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    public void ValidateEmailAddress(View v) {

        mail_1 = etmail_1.getText().toString().trim();
        mail_2 = etmail_2.getText().toString().trim();
        mail_3 = etmail_3.getText().toString().trim();
        mail_4 = etmail_4.getText().toString().trim();
        mail_5 = etmail_5.getText().toString().trim();

        Matcher matcherObj_1 = Pattern.compile("").matcher(mail_1);
        Matcher matcherObj_2 = Pattern.compile("").matcher(mail_2);
        Matcher matcherObj_3 = Pattern.compile("").matcher(mail_3);
        Matcher matcherObj_4 = Pattern.compile("").matcher(mail_4);
        Matcher matcherObj_5 = Pattern.compile("").matcher(mail_5);

        if (matcherObj_1.matches()) {
            Toast.makeText(v.getContext(), matcherObj_1 + " is valid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(v.getContext(), matcherObj_1 + " is InValid", Toast.LENGTH_SHORT).show();
        }
        if (matcherObj_2.matches()) {
            Toast.makeText(v.getContext(), matcherObj_2 + " is valid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(v.getContext(), matcherObj_2 + " is Invalid", Toast.LENGTH_SHORT).show();
        }
        if (matcherObj_3.matches()) {
            Toast.makeText(v.getContext(), matcherObj_3 + " is valid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(v.getContext(), matcherObj_3 + " is Invalid", Toast.LENGTH_SHORT).show();
        }
        if (matcherObj_4.matches()) {
            Toast.makeText(v.getContext(), matcherObj_4 + " is valid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(v.getContext(), matcherObj_4 + " is Invalid", Toast.LENGTH_SHORT).show();
        }
        if (matcherObj_5.matches()) {
            Toast.makeText(v.getContext(), matcherObj_5 + " is valid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(v.getContext(), matcherObj_5 + " is Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.about_us:
//                startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
//                finish();
//                return (true);
            case R.id.logout:
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }
}

