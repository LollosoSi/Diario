package utenti.diario;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class inizio extends Activity {

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


}
