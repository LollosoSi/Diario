package utenti.diario.activities.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import utenti.diario.R;
import utenti.diario.container.Container;
import utenti.diario.utilities.arraylist.ArrayListManager;
import utenti.diario.utilities.database.DatabaseManager;
import utenti.diario.utilities.usermanagement.LoginInterface;
import utenti.diario.utilities.usermanagement.LoginManager;
import utenti.diario.utilities.usermanagement.UserManager;

public class Login extends Activity implements LoginInterface {

    ProgressDialog pd = null;

    Container global=Container.getInstance();

    // Initialize global database reference
    DatabaseReference dr = global.databaseManager.getDatabase();
    LoginManager lm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);



        //Initialize global login manager
        lm = new LoginManager();
        lm.StoreContext(this);



        if (lm.isAutoLoginEnabled(null)) {
            callprogressdialoglogin();
            new UserManager().Login(this, lm.getName(null),lm.getPassword(null),lm.getClass(null),lm.getInstitute(null), false, ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId(),this );

        }else{



            ((Spinner)findViewById(R.id.institutes_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        // Recall back spinner if index 0 is chosen (Choose your institute menu voice is selected)
                        // Or set global institute and search for classes
                        if(i == 0){
                            global.GlobalInstitute = "";
                            ((Spinner)findViewById(R.id.institutes_spinner)).performClick();
                        }else{
                            // call waiting dialog
                            pd=ProgressDialog.show(Login.this, "", getString(R.string.please_wait), true);

                            global.GlobalInstitute = global.tempinstituteslist.get(i-1);
                            dr.child("Institutes").child(global.GlobalInstitute).child("classi").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Adapter initialization
                                    ArrayAdapter<String> adpt=new ArrayAdapter<String>(Login.this,android.R.layout.simple_list_item_1);
                                    global.tempclasseslist = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());

                                    // Temporary array for menu style
                                    // Keep in mind for real selection index is index-1
                                    ArrayList<String> spinner =  new ArrayList<String>();
                                    spinner.add(getString(R.string.choose_your_class));
                                    spinner.addAll(global.tempclasseslist);

                                    // Add in adapter
                                    adpt.addAll(spinner);

                                    // dismiss waiting dialog
                                    pd.dismiss();

                                    // Set styled adapter in spinner
                                    ((Spinner)findViewById(R.id.classes_spinner)).setAdapter(adpt);

                                    ((Spinner)findViewById(R.id.classes_spinner)).performClick();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }


                }

                public void onNothingSelected(AdapterView<?> parent) {
                    ((Spinner)findViewById(R.id.institutes_spinner)).performClick();
                }

            });



                }

        //Get institutes list
        dr.child("Institutes").child("list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Adapter initialization
                ArrayAdapter<String> adpt=new ArrayAdapter<String>(Login.this,android.R.layout.simple_list_item_1);
                global.tempinstituteslist = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());

                // Temporary array for menu style
                // Keep in mind for real selection index is index-1
                ArrayList<String> spinner =  new ArrayList<String>();
                spinner.add(getString(R.string.choose_your_institute));
                spinner.addAll(global.tempinstituteslist);

                // Add in adapter
                adpt.addAll(spinner);

                // Set styled adapter in spinner
                ((Spinner)findViewById(R.id.institutes_spinner)).setAdapter(adpt);



                // If global institute is not set this means user hasn't logged in yet, so request to choose institute
                if(Objects.equals(global.GlobalInstitute, "")){((Spinner)findViewById(R.id.institutes_spinner)).performClick();}


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ((Spinner)findViewById(R.id.classes_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0) {
                    global.GlobalClass = "";
                    ((Spinner) findViewById(R.id.classes_spinner)).performClick();
                }else {
                    Toast.makeText(Login.this,"Chosed "+global.tempclasseslist.get(i-1),Toast.LENGTH_LONG).show();
                    global.GlobalClass = global.tempclasseslist.get(i-1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ((Spinner) findViewById(R.id.classes_spinner)).performClick();
            }
        });
        setPasswordEditTextDisabledandlistener();

        findViewById(R.id.nameinput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.passwordinput).setEnabled(true);
            }
        });
    }

    void setPasswordEditTextDisabledandlistener(){
        findViewById(R.id.passwordinput).setEnabled(false);
        findViewById(R.id.passwordinput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
Toast.makeText(Login.this,"Global institute="+global.GlobalInstitute+" \nGlobal class="+global.GlobalClass,Toast.LENGTH_LONG).show();

                if(!Objects.equals(global.GlobalInstitute, "") && !Objects.equals(global.GlobalClass, "")){

                    if(new UserManager().userExist(global.GlobalInstitute,global.GlobalClass,((EditText)findViewById(R.id.nameinput)).getText().toString())){
                        ((Button)findViewById(R.id.login_bt)).setText(getString(R.string.login));
                    }else{
                        ((Button)findViewById(R.id.login_bt)).setText(getString(R.string.register));
                    }

                }

            }
        });
    }

    // Method for opening progress dialog to hang user while logging in
    void callprogressdialoglogin(){pd=ProgressDialog.show(Login.this, getString(R.string.please_wait), getString(R.string.logging_in), true);}


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
