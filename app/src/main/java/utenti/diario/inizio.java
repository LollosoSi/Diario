package utenti.diario;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import utenti.diario.activities.login.Login;
import utenti.diario.utilities.database.DatabaseManager;
import utenti.diario.utilities.exceptions.GlobalContextNotDeclared;
import utenti.diario.utilities.internet.InternetCheck;
import utenti.diario.utilities.internet.InternetDataElement;
import utenti.diario.utilities.internet.InternetManager;
import utenti.diario.utilities.usermanagement.LoginInterface;
import utenti.diario.utilities.usermanagement.LoginManager;
import utenti.diario.utilities.usermanagement.UserManager;

public class inizio extends Activity implements InternetCheck {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /** Called when app is opened */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizio);

        setTheme(R.style.MaterialLight);

        new DatabaseManager();  // Let database manager store in Container by initializing

        SetRandomHint();  // Set hint in loading page
        // On hint click set another random
        findViewById(R.id.hinttextview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {SetRandomHint();}
        });

        StartConnectionCheck(); // Check if internet is available (Result given by interface)


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
                new InternetManager().execute(new InternetDataElement(inizio.this,0),null,null);
            }
        });


    }

    @Override
    public void OnConnectionChecked(boolean isOnline, int RequestID) {

        /** Method called by interface */

        if(isOnline) {
            // Connected!
if(checkPermission(Manifest.permission.READ_PHONE_STATE)){
    GoToLoginActivity();
}else{
    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.READ_PHONE_STATE)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

        AlertDialog ad =new AlertDialog.Builder(this)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage("Please grant permission to continue")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(inizio.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                0);
                    }
                }).show();
    } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                0);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }

}


        }else{
            // Not connected, Updating UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.notconnectedlayout);
                    findViewById(R.id.offlinemodebt).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(inizio.this,"WIP",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    GoToLoginActivity();

                } else {
                    // permission denied, boo!
                    AlertDialog ad =new AlertDialog.Builder(this)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage("Please grant permission to continue")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(inizio.this,
                                            new String[]{Manifest.permission.READ_PHONE_STATE},
                                            0);
                                }
                            }).show();
                }
            }
        }
    }

    void GoToLoginActivity(){
        startActivity(new Intent(this, Login.class));
        finish();
    }


boolean checkPermission(String perm){return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));}
}
