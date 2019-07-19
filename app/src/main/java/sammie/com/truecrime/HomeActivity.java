package sammie.com.truecrime;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;
import sammie.com.truecrime.Model.EmergencyRequest;
import sammie.com.truecrime.Model.Users;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String TAG = "HomeActivity";
    private String number = "";
    private final int REQUEST_CODE = 99;
    TextView txt_latt, txt_longi, location_name;
    private GoogleMap mMap;
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
    String user_house_address;
    String user_town;
    String date;
    BottomSheetDialog bottomSheetDialog;
    String isSubscribe_splash;
    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    String address;
    LatLng myCoordinates;
    private Marker marker;
    private ToggleButton toggleButton;
    //  private static final int VIDEO_CAPTURE = 101;
    //  private Uri fileUri;
    private ShakeOptions shakeOptions;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //init Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        txt_latt = findViewById(R.id.txt_latt);
        txt_longi = findViewById(R.id.txt_longi);
        location_name = findViewById(R.id.location_name);

        btn_call = findViewById(R.id.btn_call);
        btn_direction = findViewById(R.id.btn_direction);
        toggleButton = findViewById(R.id.toggleButton);
        btn_send_mail = findViewById(R.id.btn_send_mail);
        btn_video_record = findViewById(R.id.btn_video_record);
        btn_sendMessage = findViewById(R.id.btn_sendMessage);

        //set Shake function here
        shakeOptions = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(5)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(shakeOptions).start(this, new ShakeCallback() {
            @Override
            public void onShake() {
                Log.d(TAG, "onShake " + shakeOptions.getShakeCounts());
                toggleButton.setChecked(true);
            }
        });

        //        this.shakeDetector = new ShakeDetector(options).start(this);
        //        startService(new Intent(getApplicationContext(), ShakeSensorService.class));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String calledFrom = bundle.getString("CALLED_FROM");
            if (calledFrom != null && calledFrom.equals("ShakeSensorService")) {

                Toast.makeText(this, "shaked ", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(true);
            }
        }


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lat = txt_latt.getText().toString();
                    lng = txt_longi.getText().toString();
                    // The toggle is enabled
                    toggleButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_technology));
                    Toast message = Toast.makeText(getApplicationContext(), "emergency shake detected sending Hail", Toast.LENGTH_SHORT);
                    sendSMSMessage(lat, lng, curentUserName, emerg_one, emerg_two, emerg_three);
                    message.setGravity(Gravity.CENTER, message.getXOffset() / 4,
                            message.getYOffset() / 4);
                    message.show();
//                    shakeDetector.startService(getBaseContext());


                } else {
                    // The toggle is disabled
                    toggleButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_technology_off));
                    Toast message = Toast.makeText(getApplicationContext(), "emergency shake Off", Toast.LENGTH_SHORT);
                    message.setGravity(Gravity.CENTER, message.getYOffset() / 4,
                            message.getXOffset() / 4);
                    message.show();
                    shakeDetector.stopShakeDetector(getBaseContext());


                }
            }
        });


        dialog = new SpotsDialog.Builder().setContext(HomeActivity.this)
                .setCancelable(false).build();
        //get time
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = simpleDateFormat.format(new Date());

        usersDb = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
        users_request = database.getReference("Emergency Request");
        users = database.getReference("Users");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location mCurrentLocation = locationResult.getLastLocation();
                LatLng myCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                String cityName = getCityName(myCoordinates);
                location_name.setText(address);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 13.0f));
                if (marker == null) {
                    marker = mMap.addMarker(new MarkerOptions().position(myCoordinates));
                } else
                    marker.setPosition(myCoordinates);
            }
        };


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("mylog", "Not granted");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else
                requestLocation();
        } else
            requestLocation();

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
                        user_house_address = user.getHouseNumber();
                        user_town = user.getAddress();

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

                        //email hail fxn
                        btn_send_mail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.show();
                                lat = txt_latt.getText().toString();
                                lng = txt_longi.getText().toString();
                                sendemail(lat, lng, curentUserName, user_house_address, user_town);

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


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lati = location.getLatitude();
                double longi = location.getLongitude();
                txt_latt.setText("Latitude :" + lati);
                txt_longi.setText("Longitude :" + longi);

                Log.e("My Location Data", "" + lati + "," + longi);

                locationManager.removeUpdates(this);

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

    }

    @Override
    protected void onDestroy() {
        toggleButton.setChecked(false);
        shakeDetector.destroy(getBaseContext());
        super.onDestroy();
    }

    private void showUpdateDialog(final String phoneNumber) {
        final Users user = new Users();
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setTitle("One last step 😁 !");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_info, null);


        final Button btn_update = sheetView.findViewById(R.id.btn_update);
        final TextInputEditText edt_name = sheetView.findViewById(R.id.edt_name);
        final TextInputEditText edt_contact_one = sheetView.findViewById(R.id.edt_contact_one);
        final TextInputEditText edt_contact_two = sheetView.findViewById(R.id.edt_contact_two);
        final TextInputEditText edt_contact_three = sheetView.findViewById(R.id.edt_contact_three);
        final TextInputEditText edt_gender = sheetView.findViewById(R.id.edt_gender);
        final TextInputEditText edt_house_number = sheetView.findViewById(R.id.edt_house_number);
        final TextInputEditText edt_address = sheetView.findViewById(R.id.edt_address);

        //get new values
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Users user = dataSnapshot.getValue(Users.class);

                if (!dataSnapshot.exists()) {

                } else
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        emerg_one = user.getEmerg_contact_one();
                        emerg_two = user.getEmerg_contact_two();
                        emerg_three = user.getEmerg_contact_three();
                        curentUserName = user.getFulName();

                        edt_gender.setText(user.getGender());
                        edt_address.setText(user.getAddress());
                        edt_name.setText(user.getFulName());
                        edt_house_number.setText(user.getHouseNumber());
                        edt_contact_one.setText(user.getEmerg_contact_one());
                        edt_contact_two.setText(user.getEmerg_contact_two());
                        edt_contact_three.setText(user.getEmerg_contact_three());


                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "read failed:" + databaseError.getCode(), Toast.LENGTH_SHORT).show();

            }
        });


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
                String res_address = edt_address.getText().toString();
                String house_number = edt_house_number.getText().toString();

                if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(contact_1) || TextUtils.isEmpty(contact_2)
                        || TextUtils.isEmpty(contact_3) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(res_address)
                        || TextUtils.isEmpty(house_number)) {
                    Toast.makeText(getApplicationContext(), "Please Check fields", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {

                    user.setFulName(edt_name.getText().toString());
                    user.setEmerg_contact_one(edt_contact_one.getText().toString());
                    user.setEmerg_contact_two(edt_contact_two.getText().toString());
                    user.setEmerg_contact_three(edt_contact_three.getText().toString());
                    user.setGender(edt_gender.getText().toString());
                    user.setCurrentId(mAuth.getCurrentUser().getUid());
                    user.setUserPhoneNumber(mAuth.getCurrentUser().getPhoneNumber());
                    user.setHouseNumber(edt_house_number.getText().toString());
                    user.setAddress(edt_address.getText().toString());
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

    private void sendemail(String lat, String lng, String curentUserName, String user_house_address, String user_town) {
        Toast.makeText(HomeActivity.this, lat + lng, Toast.LENGTH_LONG).show();

        Intent intentemail = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));

        Log.e("Mail Data", "" + FirstMail + "" + SecondMail + "" + ThirdMail + "" + FourthMail + "" + FifthMail);
        //  String[] to = {FirstMail, SecondMail, ThirdMail, FourthMail, FifthMail};

        intentemail.setType("text/plain");

        intentemail.putExtra(Intent.EXTRA_EMAIL, new String[]{FirstMail, SecondMail, ThirdMail, FourthMail, FifthMail});
        //    intentemail.putExtra(Intent.EXTRA_EMAIL,""+FirstMail+""+SecondMail+""+ThirdMail+""+FourthMail+""+FifthMail);
        intentemail.putExtra(Intent.EXTRA_SUBJECT, "Please help me ");
        intentemail.putExtra(Intent.EXTRA_TEXT, "This is "
                + curentUserName + "from " + user_town + " House No:" + user_house_address + "." +
                " I am Unsafe.My Current Location is " + lat + "  ,  " + lng + " " + address);
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

        Toast.makeText(HomeActivity.this, lat + lng, Toast.LENGTH_LONG).show();


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(one, null, "This is "
                + curentUserName + "from " + user_town + " House No:" + user_house_address + "." +
                " I am Unsafe.My Current Location is " + lat + "  ,  " + lng + " " + address, null, null);

        //second number
        SmsManager smsManager1 = SmsManager.getDefault();
        smsManager1.sendTextMessage(two, null, "This is "
                + curentUserName + "from " + user_town + " House No:" + user_house_address + "." +
                " I am Unsafe.My Current Location is " + lat + "  ,  " + lng + " " + address, null, null);

        //third number
        SmsManager smsManager2 = SmsManager.getDefault();
        smsManager2.sendTextMessage(three, null, "This is "
                + curentUserName + "from " + user_town + " House No:" + user_house_address + "." +
                " I am Unsafe.My Current Location is " + lat + "  ,  " + lng + " " + address, null, null);

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

        final CharSequence[] options = {"Call Family Members", "Call Police", "Call Ambulance", "Call Fire Service", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Emergency Call To!");
        builder.setIcon(R.drawable.siren);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Call Family Members")) {
                    CallSpecificNumber();

                } else if (options[item].equals("Call Police")) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:191"));

                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    startActivity(callIntent);
                } else if (options[item].equals("Call Ambulance")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:193"));
                    startActivity(callIntent);
                } else if (options[item].equals("Call Fire Service")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:192"));
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


    public void direction(View v) {
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
    public void tip() {
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
            case R.id.menu_tips:
//                startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
                tip();
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
                showUpdateDialog(mAuth.getCurrentUser().getPhoneNumber());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String numberPicked = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            number = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:" + number);

                            Toast.makeText(this, "calling " + numberPicked, Toast.LENGTH_LONG).show();
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            startActivity(callIntent);

                        }


                    }
                }
                break;
        }


    }


    private void CallSpecificNumber() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContact, REQUEST_CODE);


    }


    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("mylog1", "In Requesting Location");
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= 1000 * 2) {
            myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            String cityName = getCityName(myCoordinates);

            Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Log.d("mylog2", "Last location too old getting new location! device offline");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getLocality();
            Log.d("mylog3", "Complete Address: " + addresses.toString());
            Log.d("mylog4", "Address: " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCity;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}