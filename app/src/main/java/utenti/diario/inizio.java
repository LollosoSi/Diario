package utenti.diario;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
        switch (rnd.nextInt(3)) {
            case 0:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint0));
                break;
            case 1:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint1));
                break;
            case 2:
                ((TextView) findViewById(R.id.hinttextview)).setText(getString(R.string.hint2));
                break;
        }
    }

    void StartConnectionCheck(){

        new InternetManager().execute(this,null,null);
        Looper.prepare();
    }

    @Override
    public void OnConnectionChecked(boolean isOnline) {
        if(isOnline){
            Toast.makeText(this,"Sei connesso!",Toast.LENGTH_SHORT).show();
        }else{
            setContentView(R.layout.notconnectedlayout);
        }
    }

}
