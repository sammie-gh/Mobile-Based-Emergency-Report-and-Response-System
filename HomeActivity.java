package sammie.com.truecrime;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import sammie.com.truecrime.Common.Common;
import sammie.com.truecrime.Model.EmergencyRequest;
import sammie.com.truecrime.Model.Users;
 public class HomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final int REQUEST_CODE = 99;
    TextView txt_latt, txt_longi;
    LocationManager lManager;
    SupportMapFragment frag;
    GoogleMap gMap;
    SimpleDateFormat simpleDateFormat;
    String FirstNumber, SecondNumber, ThirdNumber, FourthNumber, FifthNumber;
    String lat, lng;
    String FirstMail, SecondMail, ThirdMail, FourthMail, FifthMail;
    FirebaseDatabase database;
    DatabaseReference usersDb, users_request, users;
    FirebaseAuth mAuth;
    AlertDialog dialog;
    String fName, emerg_one, emerg_two, emerg_three;
    ImageButton btn_video_record, btn_sendMessage, btn_call, btn_direction, btn_send_mail;
    String emergency_one;
    String emergency_two;
    String emergency_three;
    String curentUserName;
    String date;
    BottomSheetDialog bottomSheetDialog;
    String isSubscribe_splash;


    //  private static final int VIDEO_CAPTURE = 101;
    //  private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //init Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        txt_latt = findViewById(R.id.txt_latt);
        txt_longi = findViewById(R.id.txt_longi);

        btn_call = findViewById(R.id.btn_call);
        btn_direction = findViewById(R.id.btn_direction);
        btn_send_mail = findViewById(R.id.btn_send_mail);
        btn_video_record = findViewById(R.id.btn_video_record);
        btn_sendMessage = findViewById(R.id.btn_sendMessage);

        dialog = new SpotsDialog.Builder().setContext(HomeActivity.this)
                .setCancelable(false).build();
        //get time
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = simpleDateFormat.format(new Date());


        usersDb = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
        users_request = database.getReference("Emergency Request");
        users = database.getReference("Users");


        checkForSmsPermission();

        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Users user = dataSnapshot.getValue(Users.class);

                if (!dataSnapshot.exists()) {
                    showUpdateDialog(mAuth.getCurrentUser().getPhoneNumber());
                    Toast.makeText(HomeActivity.this, "User database info null", Toast.LENGTH_SHORT).show();
                } else

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        emerg_one = user.getEmerg_contact_one();
                        emerg_two = user.getEmerg_contact_two();
                        emerg_three = user.getEmerg_contact_three();
                        curentUserName = user.getFulName();

                        //set Function sendText message
                        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.show();
                                lat = txt_latt.getText().toString();
                                lng = txt_longi.getText().toString();

                                sendSMSMessage(lat, lng, curentUserName, emerg_one, emerg_two, emerg_three);

//                            Intent intent = getIntent();
//                            String userInput = "122323,12344221,1323442";
//                            String numbers[] = userInput.split(", *");
//                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//                            smsIntent.setType("vnd.android-dir/mms-sms");
////                            smsIntent.putExtra("address", "987385438; 750313; 971855;84393");
//                            smsIntent.putExtra("address",  user.getEmerg_contact_one() +";" +user.getEmerg_contact_two() +";" +user.getEmerg_contact_three());
//
//                            Log.e("Phone Numbers", "" + FirstNumber + "," + SecondNumber + "," + ThirdNumber + "," + FourthNumber + "," + FifthNumber);
//                            smsIntent.putExtra("sms_body", "Hey! I am safe now, My location is  " + lat + "  " + lng);
//                            startActivity(smsIntent);

                                //send to databas
                            }
                        });

                        //emailshare
                        btn_send_mail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.show();
                                sendemail(lat, lng, curentUserName);

                            }
                        });
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "he read failed:" + databaseError.getCode(), Toast.LENGTH_SHORT).show();

            }
        });


//
//        FirstMail = getIntent().getStringExtra("FirstMail");
//        SecondMail = getIntent().getStringExtra("SecondMail");
//        ThirdMail = getIntent().getStringExtra("ThirdMail");
//        FourthMail = getIntent().getStringExtra("FourthMail");
//        FifthMail = getIntent().getStringExtra("FifthMail");
//
//        Log.e("Email Addresses", "" + FirstMail + "," + SecondMail + "," + ThirdMail + "," + FourthMail + "," + FifthMail);
//
//        FirstNumber = getIntent().getStringExtra("First Number");
//        SecondNumber = getIntent().getStringExtra("Second Number");
//        ThirdNumber = getIntent().getStringExtra("Third Number");
//        FourthNumber = getIntent().getStringExtra("Fourth Number");
//        FifthNumber = getIntent().getStringExtra("Fifth Number");


        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("Internet Connection Required")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        frag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lati = location.getLatitude();
                double longi = location.getLongitude();
                txt_latt.setText("Latitude :" + lati);
                txt_longi.setText("Longitude :" + longi);
                Log.e("My Location Data", "" + lati + "," + longi);

                lManager.removeUpdates(this);

                MarkerOptions options = new MarkerOptions();
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.place_holder));
                options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                gMap.addMarker(options);
                gMap.getUiSettings().setZoomControlsEnabled(true);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12f));

                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gMap.getUiSettings().setMapToolbarEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        frag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
            }
        });
    }

    private void showUpdateDialog(final String phoneNumber) {

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setTitle("One last step 😁 !");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_info, null);


        final Button btn_update = sheetView.findViewById(R.id.btn_update);
        final TextInputEditText edt_name = sheetView.findViewById(R.id.edt_name);
        final TextInputEditText edt_contact_one = sheetView.findViewById(R.id.edt_contact_one);
        final TextInputEditText edt_contact_two = sheetView.findViewById(R.id.edt_contact_two);
        final TextInputEditText edt_contact_three = sheetView.findViewById(R.id.edt_contact_three);
        final TextInputEditText edt_gender = sheetView.findViewById(R.id.edt_gender);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }

                String fName = edt_name.getText().toString();
                String contact_2 = edt_contact_two.getText().toString();
                String contact_3 = edt_contact_three.getText().toString();
                String contact_1 = edt_contact_one.getText().toString();
                String gender = edt_gender.getText().toString();

                if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(contact_1) || TextUtils.isEmpty(contact_2)
                        || TextUtils.isEmpty(contact_3) || TextUtils.isEmpty(gender)) {
                    Toast.makeText(getApplicationContext(), "Please Check fields", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    Users user = new Users();
                    user.setFulName(edt_name.getText().toString());
                    user.setEmerg_contact_one(edt_contact_one.getText().toString());
                    user.setEmerg_contact_two(edt_contact_two.getText().toString());
                    user.setEmerg_contact_three(edt_contact_three.getText().toString());
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
                                    bottomSheetDialog.dismiss();
                                    Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                                    startActivity(homeIntent);
                                    finish();
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            bottomSheetDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });


        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void sendemail(String lat, String lng, String curentUserName) {


        Intent intentemail = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        /* FirstMail = intentemail.getStringExtra("FirstMail");
         SecondMail = intentemail.getStringExtra("SecondMail");
        ThirdMail = intentemail.getStringExtra("ThirdEmail");
          FourthMail = intentemail.getStringExtra("FourthMail");
          FifthMail = intentemail.getStringExtra("FifthMail");  */
        Log.e("Mail Data", "" + FirstMail + "" + SecondMail + "" + ThirdMail + "" + FourthMail + "" + FifthMail);
        //  String[] to = {FirstMail, SecondMail, ThirdMail, FourthMail, FifthMail};

        intentemail.setType("text/plain");

        intentemail.putExtra(Intent.EXTRA_EMAIL, new String[]{FirstMail, SecondMail, ThirdMail, FourthMail, FifthMail});
        //    intentemail.putExtra(Intent.EXTRA_EMAIL,""+FirstMail+""+SecondMail+""+ThirdMail+""+FourthMail+""+FifthMail);
        intentemail.putExtra(Intent.EXTRA_SUBJECT, "Please help me ");
        intentemail.putExtra(Intent.EXTRA_TEXT, "I am Unsafe.My Location is " + lat + "  ,  " + lng);
        startActivity(intentemail);

        try {
            startActivity(Intent.createChooser(intentemail, "Choose an email client from..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(HomeActivity.this, "No email client installed.", Toast.LENGTH_LONG).show();
        }

        //write to database
        EmergencyRequest request = new EmergencyRequest();

        request.setLat(lat);
        request.setLng(lng);
        request.getUsername(curentUserName);
        request.setTimeStamp(date);

        users_request.child(curentUserName + "Hail through MAIL")
                .setValue(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("onSuccessSmS", "onSuccess: ");
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    private void sendSMSMessage(String lat, String lng, String usersname, String one, String two, String three) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(one, null, "HELLOO ! I am not safe I'm in DANGER, My location is " + lat + " " + lng, null, null);

        //second number
        SmsManager smsManager1 = SmsManager.getDefault();
        smsManager1.sendTextMessage(two, null, "HELLOO ! I am not safe I'm in DANGER, My location is " + lat + " " + lng, null, null);

        //third number
        SmsManager smsManager2 = SmsManager.getDefault();
        smsManager2.sendTextMessage(three, null, "HELLOO ! I am not safe I'm in DANGER, My location is " + lat + " " + lng, null, null);

        Toast.makeText(getApplicationContext(), "SMS sent." + one + two + three, Toast.LENGTH_LONG).show();
        Log.i("SMS_sent", "sendSMSMessage: " + "SMS sent." + this.emerg_one + this.emerg_two + this.emerg_three);

        //write to database
        EmergencyRequest request = new EmergencyRequest();

        request.setLat(lat);
        request.setLng(lng);
        request.getUsername(usersname);
        request.setMessage(smsManager.toString());
        request.setTimeStamp(date);


        users_request.child(usersname + " Hail Through SMS")
                .setValue(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        Log.i("onSuccessSmS", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            Log.i("SMS_SEND_PERM", "Permission already granted. Enable the SMS button");

            enableSmsButton();
        }
    }

    private void enableSmsButton() {
        btn_sendMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableSmsButton();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS failed,Permission denied.", Toast.LENGTH_LONG).show();
                    disableSmsButton();

                }
            }
        }

    }

    private void disableSmsButton() {
        btn_sendMessage.setVisibility(View.INVISIBLE);
    }


    public void call(View v) {

        final CharSequence[] options = {"Call Family Members", "Call Police", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Emergency Call To!");
        builder.setIcon(R.drawable.siren);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Call Specific Contact")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE);

                } else if (options[item].equals("Contact Security firm")) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:111"));

                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    startActivity(callIntent);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void message(View v) {

        lat = txt_latt.getText().toString();
        lng = txt_longi.getText().toString();
        Intent intent = getIntent();
        String userInput = "122323,12344221,1323442";
        String numbers[] = userInput.split(", *");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");

//        smsIntent.putExtra("address", "" + FirstNumber + "," + SecondNumber + "," + ThirdNumber + "," + FourthNumber + "," + FifthNumber);
        smsIntent.putExtra("address", "987385438; 750313; 971855;84393");

        Log.e("Phone Numbers", "" + FirstNumber + "," + SecondNumber + "," + ThirdNumber + "," + FourthNumber + "," + FifthNumber);
        smsIntent.putExtra("sms_body", "Hey! I am safe now, My location is  " + lat + "  " + lng);
        startActivity(smsIntent);


    }


    public void disrection(View v) {
        lat = txt_latt.getText().toString();
        lng = txt_longi.getText().toString();
        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=police");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        Log.e("Location Data", "" + lat + "" + lng);
        startActivity(mapIntent);

       /* Uri gmmIntentUri = Uri.parse("geo:0,0?q=");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent); */
    }

    public void tips(View v) {
        startActivity(new Intent(getApplicationContext(), TipsActivity.class));
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
            case R.id.about_us:
                startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
                finish();
                return (true);
            case R.id.logout:
                mAuth.signOut();
                finish();
                return true;
            case R.id.settings:
                showSettingsNotificationDialog();
                return true;
            case R.id.change:
                Toast.makeText(this, "to be implemented", Toast.LENGTH_SHORT).show();
                return true;


        }
        return (super.onOptionsItemSelected(item));
    }


    private void showSettingsNotificationDialog() {

        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("SETTINGS");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_settings = inflater.inflate(R.layout.settings_layout, null);
        final CheckBox ckb_subscribe_new = layout_settings.findViewById(R.id.ckb_sub_new);

        //  remember state of check
        Paper.init(this);
        isSubscribe_splash = Paper.book().read("sub_splash");


        if (isSubscribe_splash == null || TextUtils.isEmpty(isSubscribe_splash) || isSubscribe_splash.equals("false"))
            ckb_subscribe_new.setChecked(false);
        else
            ckb_subscribe_new.setChecked(true);


        alertDialog.setView(layout_settings);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (ckb_subscribe_new.isChecked()) {

                    //write value
                    Paper.book().write("sub_splash", "true");
                    Toast.makeText(HomeActivity.this, "enable", Toast.LENGTH_SHORT).show();

                } else {

                    //write value
                    Paper.book().write("sub_splash", "false");
                    Toast.makeText(HomeActivity.this, "disable", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void videorecording(View v) {

        Intent i = new Intent(HomeActivity.this, VideoRecordingActivity.class);
        startActivity(i);

   /*       hasCamera();
        File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = Uri.fromFile(mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VIDEO_CAPTURE) {
            Log.e("Video Capture", "" + requestCode);
            if (resultCode == RESULT_OK) {


              //  Intent intentParent = getIntent();
               // setResult(RESULT_OK, intentParent);
               // Uri uri = data.getData();
               // fileUri = data.getData();
                Log.e("Video Recording", "" + resultCode);

                Toast.makeText(HomeActivity.this,"Video has been saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(HomeActivity.this,"Video recording cancelled.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(HomeActivity.this,"Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else {
            return false;
        }
    }  */
    }
}