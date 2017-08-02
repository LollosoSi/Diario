package utenti.diario;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import java.util.Random;

import utenti.diario.utilities.internet.InternetCheck;
import utenti.diario.utilities.internet.InternetManager;

public class inizio extends Activity implements InternetCheck {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizio);

        SetRandomHint();
        findViewById(R.id.hinttextview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetRandomHint();
            }
        });

        StartConnectionCheck();

    }

    void SetRandomHint(){
        Random rnd = new Random();
        ((TextView) findViewById(R.id.hinttextview)).setTextColor(Color.argb(255, rnd.nextInt(256-150)+150, rnd.nextInt(256-150)+150, rnd.nextInt(256-150)+150));
        switch (rnd.nextInt(5)) {
            case 0:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint0));
                break;
            case 1:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint1));
                break;
            case 2:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint2));
                break;
            case 3:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint3));
                break;
            case 4:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint4));
                break;
        }
    }

    void StartConnectionCheck(){

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new InternetManager().execute(inizio.this,null,null);
            }
        });


    }

    @Override
    public void OnConnectionChecked(boolean isOnline) {

        /** Method called by interface */

        if(isOnline){
            // Connected!
        }else{
            // Not connected, Updating UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.notconnectedlayout);
                }
            });
        }
    }


}
