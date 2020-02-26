package com.example.mortgagecalculatorpro;
import ca.roumani.i2c.MPro;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener , SensorEventListener {

    MPro mp;
    private TextToSpeech txttospeech;
    private SensorManager motionManager;
    private Sensor motion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        mp = new MPro();

        this.txttospeech = new TextToSpeech(this,this);

        motionManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        motion = motionManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        motionManager.registerListener(this, motion, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onInit(int initStatus){
        this.txttospeech.setLanguage(Locale.US);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1){
    }

    public void onSensorChanged(SensorEvent event){

        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(Math.pow(ax,2)+Math.pow(ay,2)+Math.pow(az,2));
        if(a>20){
            ((EditText)findViewById(R.id.pBox)).setText("");
            ((EditText)findViewById(R.id.aBox)).setText("");
            ((EditText)findViewById(R.id.iBox)).setText("");
            ((TextView)findViewById(R.id.display)).setText("");
        }
    }



    public void pressedButton(View v) {
        try{
            mp.setPrinciple(((EditText)findViewById(R.id.pBox)).getText().toString());
            mp.setAmortization(((EditText)findViewById(R.id.aBox)).getText().toString());
            mp.setInterest(((EditText)findViewById(R.id.iBox)).getText().toString());
            System.out.println(mp.computePayment("%,.2f"));
            System.out.println(mp.outstandingAfter(2,"%,16.0f"));

            String s = "Monthly Payment : " + mp.computePayment("%,.2f");
            String n;
            n = s;
            n = n + " \n\n By making this payment monthly for 20 years, the mortgage will be paid in full." +
                    " If you terminate it on the nth anniversary";


            for (int i = 0 ; i <=5 ; i++){
                n = n + "\n\n" + String.format("%8d",i) + mp.outstandingAfter(i,"%,16.0f");
            }

            for (int k = 10 ; k <=20 ; k+=5){
                n = n + "\n\n" + String.format("%8d",k)+ mp.outstandingAfter(k,"%,16.0f");
            }
            n = n + "\n";
            ((TextView) findViewById(R.id.display)).setText(n);


            txttospeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }

        catch (Exception e){
            Toast label = Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }



    }


}
