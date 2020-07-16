package com.example.android.med_ai;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 11);
        alarmStartTime.set(Calendar.MINUTE, 15);
        alarmStartTime.set(Calendar.SECOND, 0);

        Intent intent = new Intent(MainActivity.this,Notification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        //Creating reference for the parent view
        RelativeLayout parentView = (RelativeLayout) findViewById(R.id.parentView);
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
            }
        });

        // Creating the retrofit builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://hackathon-med-ai.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // mesh e7na ele bnktb el body bta3 el interface retrofit ele btmlah zy fe tsunami alert app keda, doInBackGround, AsyncTask, etc
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                EditText etUserName = findViewById(R.id.nameEditText);
                String userName = etUserName.getText().toString();
                EditText etPassword = findViewById(R.id.passEditText);
                String password = etPassword.getText().toString();
                if(TextUtils.isEmpty(userName))
                {
                    etUserName.setError("Email field can't be empty");
                }
                else
                {
                    if(TextUtils.isEmpty(password))
                        etPassword.setError("Password field can't be empty");
                    else
                    {
                        currentUser = new User(userName,password);
                        Call<Response> call = apiInterface.storeUser(currentUser);
                        call.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                if(response.body() != null)
                                {
                                    Intent i1 = new Intent(MainActivity.this, app.class);
                                    startActivity(i1);
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                                    etPassword.setText("");
                                }
                            }

                            @Override
                            public void onFailure(Call<Response> call, Throwable t) {
                                Toast.makeText(MainActivity.this ,"Network or Server Error", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "MedAiReminderChannel";
            String description = "Channel for MedAi Reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyMedAi", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
