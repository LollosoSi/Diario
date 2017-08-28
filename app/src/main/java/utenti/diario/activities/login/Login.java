package utenti.diario.activities.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import utenti.diario.R;
import utenti.diario.utilities.usermanagement.LoginInterface;
import utenti.diario.utilities.usermanagement.LoginManager;
import utenti.diario.utilities.usermanagement.UserManager;

public class Login extends Activity implements LoginInterface {

    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.MaterialLight);
        setContentView(R.layout.activity_login);

        getActionBar().hide();
        getWindow().setStatusBarColor(Color.argb(50,0,0,0));

        LoginManager lm = new LoginManager();
        lm.StoreContext(this);

        if (lm.isAutoLoginEnabled(null)) {
            pd=ProgressDialog.show(Login.this, "Please wait", "Logging in..", true);
            new UserManager().Login(this, lm.getName(null),lm.getPassword(null),lm.getClass(null),lm.getInstitute(null), false, ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId(),this );

        }else{

        }

    }


    @Override
    public void onLoginResult(boolean success, int reason) {
    if(pd.isShowing()){ pd.dismiss();}

        switch (reason){
            case UserManager.LOGIN_SUCCEEDED:
                Snackbar.make(getCurrentFocus(),"Welcome back",Snackbar.LENGTH_LONG).show();
                break;

            case UserManager.REASON_FAIL_PASSWORD_CHECK:
                Snackbar.make(getCurrentFocus(),"password not correct",Snackbar.LENGTH_LONG).show();
                break;

            case UserManager.REASON_USER_NOT_EXIST:
                Snackbar.make(getCurrentFocus(),"user not exist",Snackbar.LENGTH_LONG).show();
                break;

            case UserManager.REASON_BANNED:
                Snackbar.make(getCurrentFocus(),"Banned",Snackbar.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    public void onBannedResult(int expiry, String Reason) {

    }

}
