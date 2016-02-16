package com.example.ethan.inclass04;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    Executor threadpool;
    Button generatePasswordThreadButton;
    Handler handler;
    String finishedPassword;
    static final String GENERATED_PASSWORD = "GeneratedPassword";
    TextView passwordView;
    ProgressDialog progressDialog;
    static final String GENERATING_PASSWORD = "GeneratingPassword";
    CheckBox numBox;
    CheckBox upperBox;
    CheckBox lowerBox;
    CheckBox specialBox;
    Spinner lengthSpinner;
    Button  generatePasswordAsyncButton;

    String asyncPassword;


    int length;
    boolean num;
    boolean upper;
    boolean lower;
    boolean special;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordView = (TextView) findViewById(R.id.passwordText);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Generating Password");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        numBox = (CheckBox) findViewById(R.id.numbers_id);
        upperBox = (CheckBox) findViewById(R.id.uppercase_id);
        lowerBox = (CheckBox) findViewById(R.id.lowercase_id);
        specialBox = (CheckBox) findViewById(R.id.special_id);
        lengthSpinner = (Spinner) findViewById(R.id.spinner);




        threadpool = Executors.newFixedThreadPool(4);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {


                finishedPassword = msg.getData().getString(GENERATED_PASSWORD);

                progressDialog.dismiss();

                passwordView.setTextColor(Color.RED);
                passwordView.setText(finishedPassword);



                return false;
            }
        });



       generatePasswordThreadButton = (Button) findViewById(R.id.gpthread);
        generatePasswordThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                length = lengthSpinner.getSelectedItemPosition();
                num = numBox.isChecked();
                upper = upperBox.isChecked();
                lower = lowerBox.isChecked();
                special = specialBox.isChecked();

                if(num||upper||lower||special) {
                    progressDialog.show();
                    threadpool.execute(new GeneratePasswordThread());
                }
                else
                    Toast.makeText(MainActivity.this, "Must Select At Least One Option", Toast.LENGTH_LONG).show();
            }
        });

        generatePasswordAsyncButton = (Button) findViewById(R.id.gpasynctask);
        generatePasswordAsyncButton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                length = lengthSpinner.getSelectedItemPosition();
                num = numBox.isChecked();
                upper = upperBox.isChecked();
                lower = lowerBox.isChecked();
                special = specialBox.isChecked();


                if(num||upper||lower||special) {
                    GeneratePasswordAsync GpAsnyc = new GeneratePasswordAsync();

                    GpAsnyc.execute();
                }
                else

                    Toast.makeText(MainActivity.this, "Must Select At Least One Option", Toast.LENGTH_LONG).show();



            }

        });



    }

    class GeneratePasswordThread implements Runnable{



        public void run(){


            Bundle bundle;
            Message msg;



            msg = new Message();
            String password = Util.getPassword(length,num,upper,lower,special);
            bundle = new Bundle();

            bundle.putString(GENERATED_PASSWORD,password);
            msg.setData(bundle);

            handler.sendMessage(msg);

        }


    }

    class GeneratePasswordAsync extends AsyncTask<Void, Integer, Void>{

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            passwordView.setTextColor(Color.RED);
            passwordView.setText(asyncPassword);

        }

        @Override
        protected Void doInBackground(Void... params) {


            asyncPassword = Util.getPassword(length,num,upper,lower,special);



            return null;
        }

        @Override
        protected void onPreExecute() {

            progressDialog.show();

        }

        protected void onPostExecute(){



        }

    }
}
