package sammie.com.truecrime;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;
import sammie.com.truecrime.Model.Users;

public class UserDetailsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference users;
    FirebaseAuth mAuth;
    AlertDialog dialog;
    private EditText edt_name, edt_gender, edt_contact_3, edt_contact_two, edt_contact_one;
    private Button btn_continue;
    ConstraintLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sdetails);

        //init Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        edt_name = findViewById(R.id.edt_name);
        edt_contact_one = findViewById(R.id.edt_contact_one);
        edt_contact_two = findViewById(R.id.edt_contact_two);
        edt_contact_3 = findViewById(R.id.edt_contact_3);
        edt_gender = findViewById(R.id.edt_gender);
        linearLayout = findViewById(R.id.linearRoot);

        dialog = new SpotsDialog.Builder().setContext(UserDetailsActivity.this)
                .setCancelable(false).build();

        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setText("UPDATE");

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName = edt_name.getText().toString();
                String contact_3 = edt_contact_3.getText().toString();
                String contact_2 = edt_contact_two.getText().toString();
                String contact_1 = edt_contact_one.getText().toString();
                String gender = edt_gender.getText().toString();

                if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(contact_1) || TextUtils.isEmpty(contact_2)
                        || TextUtils.isEmpty(contact_3) || TextUtils.isEmpty(gender)) {
                    Toast.makeText(getApplicationContext(), "Please Check fields", Toast.LENGTH_SHORT).show();

                } else {
                    dialog.show();

                    Users user = new Users();
                    user.setFulName(edt_name.getText().toString());
                    user.setEmerg_contact_one(edt_contact_one.getText().toString());
                    user.setEmerg_contact_two(edt_contact_two.getText().toString());
                    user.setEmerg_contact_three(edt_contact_3.getText().toString());
                    user.setGender(edt_gender.getText().toString());
                    user.setCurrentId(mAuth.getCurrentUser().getUid());
                    user.setUserPhoneNumber(mAuth.getCurrentUser().getPhoneNumber());


                    users.child(mAuth.getCurrentUser().getUid())
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                    Intent homeIntent = new Intent(UserDetailsActivity.this, HomeActivity.class);
                                    startActivity(homeIntent);
                                    finish();
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            Toast.makeText(UserDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


    }

    public void onBackPressed() {

        Snackbar.make(linearLayout, "Please fill in details", Snackbar.LENGTH_LONG)
                .show();
    }

    public void Skip(View view) {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
    }
}
