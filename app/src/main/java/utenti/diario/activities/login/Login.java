package utenti.diario.activities.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import utenti.diario.R;
import utenti.diario.activities.home.Home;
import utenti.diario.activities.login.ban.BannedActivity;
import utenti.diario.container.Container;
import utenti.diario.utilities.arraylist.ArrayListManager;
import utenti.diario.utilities.database.DatabaseManager;
import utenti.diario.utilities.usermanagement.LoginInterface;
import utenti.diario.utilities.usermanagement.LoginManager;
import utenti.diario.utilities.usermanagement.UserManager;
import utenti.diario.utilities.usermanagement.UserManagerInterface;

public class Login extends Activity implements LoginInterface, UserManagerInterface {

    ProgressDialog pd = null;

    Container global = Container.getInstance();

    // Initialize global database reference
    DatabaseReference dr = global.databaseManager.getDatabase();
    LoginManager lm;

    // Boolean for name and password checking (used for AND port enabling login button)
    boolean nameinput_sufficient = false;
    boolean passwordinput_sufficient = false;

    // Boolean for prevent multiple setup calls
    boolean OnceSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Random set welcome2_variant string ( 50% chance )
        if (new Random().nextInt(1) == 1){
            ((TextView)findViewById(R.id.didascaliatextview)).setText(getString(R.string.welcome2_variant));
        }

        //Initialize global login manager
        lm = new LoginManager();
        lm.StoreContext(this);

        // If feature "remember me" is enabled do auto login
        if (lm.isAutoLoginEnabled(null)) {
            // Put the user in waiting state
            callprogressdialoglogin();

            // Graphic effect: Put name in nameinput end fake password in passwordinput
            ((EditText) findViewById(R.id.nameinput)).setText(lm.getName(null));
            ((EditText) findViewById(R.id.passwordinput)).setText("abcdefg");

            // Store global institute to not call spinner when institutes are loaded
            global.GlobalInstitute = lm.getInstitute(null);

            // Login
            new UserManager().Login(this, lm.getName(null), lm.getPassword(null), lm.getClass(null), lm.getInstitute(null), false, ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId(), this, false);

        }else{
            secondarySetup();
        }




    }

    // setup called once per session only if autologin fails or user is not registered
    void secondarySetup () {
        // Multi calling protection
        if (!OnceSetup) { OnceSetup=true;  // With this the method won't be executed anymore

            // Clear password text
            ((EditText) findViewById(R.id.passwordinput)).setText("");

            //Get institutes list
            dr.child("Institutes").child("list").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Adapter initialization
                    ArrayAdapter<String> adpt = new ArrayAdapter<String>(Login.this, android.R.layout.simple_list_item_1);
                    global.tempinstituteslist = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());

                    // Temporary array for menu style
                    // Keep in mind for real selection index is index-1
                    ArrayList<String> spinner = new ArrayList<String>();
                    spinner.add(getString(R.string.choose_your_institute));
                    spinner.addAll(global.tempinstituteslist);

                    // Add in adapter
                    adpt.addAll(spinner);

                    // Set styled adapter in spinner
                    ((Spinner) findViewById(R.id.institutes_spinner)).setAdapter(adpt);


                    // If global institute is not set this means user hasn't logged in yet, so request to choose institute
                    if (Objects.equals(global.GlobalInstitute, "")) {
                        ((Spinner) findViewById(R.id.institutes_spinner)).performClick();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Get ready to catch institute selection -> then launch class selection
            ((Spinner) findViewById(R.id.institutes_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    // Clear user globals to avoid members number issues
                    Container.getInstance().maxclassusers = 0;
                    Container.getInstance().classusers = 0;

                    // Clear class capacity text since is reset
                    ((TextView) findViewById(R.id.class_capacity_txt)).setText("");

                    // Recall back spinner if index 0 is chosen (Choose your institute menu voice is selected)
                    // Or set global institute and search for classes
                    if (i == 0) {

                        global.GlobalInstitute = "";
                        ((Spinner) findViewById(R.id.institutes_spinner)).performClick();
                    } else {
                        // call waiting dialog
                        pd = ProgressDialog.show(Login.this, "", getString(R.string.please_wait), true);

                        global.GlobalInstitute = global.tempinstituteslist.get(i - 1);
                        dr.child("Institutes").child(global.GlobalInstitute).child("classi").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Adapter initialization
                                ArrayAdapter<String> adpt = new ArrayAdapter<String>(Login.this, android.R.layout.simple_list_item_1);
                                global.tempclasseslist = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());

                                // Temporary array for menu style
                                // Keep in mind for real selection index is index-1
                                ArrayList<String> spinner = new ArrayList<String>();
                                spinner.add(getString(R.string.choose_your_class));
                                spinner.addAll(global.tempclasseslist);

                                // Add in adapter
                                adpt.addAll(spinner);

                                // dismiss waiting dialog
                                pd.dismiss();

                                // Set styled adapter in spinner
                                ((Spinner) findViewById(R.id.classes_spinner)).setAdapter(adpt);

                                ((Spinner) findViewById(R.id.classes_spinner)).performClick();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                }

                public void onNothingSelected(AdapterView<?> parent) {
                    ((Spinner) findViewById(R.id.institutes_spinner)).performClick();
                }

            });

            // Get ready to catch class selection -> then get class capacity
            ((Spinner) findViewById(R.id.classes_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i == 0) {
                        global.GlobalClass = "";
                        ((Spinner) findViewById(R.id.classes_spinner)).performClick();
                    } else {
                        global.GlobalClass = global.tempclasseslist.get(i - 1);

                        // Check maximum users
                        dr.child("Institutes").child(global.GlobalInstitute).child(global.GlobalClass).child("utenza").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int maxusers = 0;
                                int users = 0;

                                if (dataSnapshot.exists()) {
                                    ArrayList<String> usersnum = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                                    users = Integer.parseInt(usersnum.get(0));
                                    maxusers = Integer.parseInt(usersnum.get(1));
                                }

                                // If users is not retrieved it's value is 0, so show it
                                // If value is retrieved check if class is full
                                if (maxusers != 0) {

                                    if (users <= maxusers) {
                                        ((TextView) findViewById(R.id.class_capacity_txt)).setText(getString(R.string.class_capacity) + ": "
                                                + users +
                                                "/" +
                                                maxusers);
                                    } else {
                                        ((TextView) findViewById(R.id.class_capacity_txt)).setText(getString(R.string.class_full));
                                    }

                                } else {
                                    ((TextView) findViewById(R.id.class_capacity_txt)).setText(getString(R.string.class_capacity) + ": "
                                            + users +
                                            "/" +
                                            maxusers);
                                }

                                // Set users in global
                                Container.getInstance().maxclassusers = maxusers;
                                Container.getInstance().classusers = users;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    ((Spinner) findViewById(R.id.classes_spinner)).performClick();
                }
            });

            // Disable password to force user to put name in first
            setPasswordEditTextDisabledandlistener();

            // Check rules then login/register
            findViewById(R.id.login_bt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if everything is ok (automatic notification to user)
                    if (rule1() && rule2() && rule3() && rule4()) {

                        // Login
                        new UserManager().Login(
                                Login.this,
                                ((EditText) findViewById(R.id.nameinput)).getText().toString(),
                                ((EditText) findViewById(R.id.passwordinput)).getText().toString(),
                                global.GlobalClass,
                                global.GlobalInstitute,
                                true,
                                ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId(),
                                Login.this,
                                false);


                    }
                }
            });
        }
    }

    // Disable password to force user to put name in first
    void setPasswordEditTextDisabledandlistener() {
        findViewById(R.id.passwordinput).setEnabled(false);
        findViewById(R.id.passwordinput).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Password edittext gained focus
                if (b) {
                    // Institute and class are selected
                    if (!Objects.equals(global.GlobalInstitute, "") && !Objects.equals(global.GlobalClass, "")) {
                        new UserManager().userExist(global.GlobalInstitute, global.GlobalClass, ((EditText) findViewById(R.id.nameinput)).getText().toString(),Login.this);


                    }
                }
            }
        });

        // Check for text changes and remove illegal charaters, update password availability and modify its hint
        final int[] beforelength = {0};
        ((EditText) findViewById(R.id.nameinput)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforelength[0] = ((EditText) findViewById(R.id.nameinput)).getText().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                verifyAndEnableLoginButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {



                if (beforelength[0] < editable.length()) {

                    char lastinsert = editable.charAt(editable.length() - 1);

                    if (!charallowed(lastinsert)) {
                        //Temporary save text for remove illegal char
                        String modify = ((EditText) findViewById(R.id.nameinput)).getText().toString();
                        // Remove last char
                        if (((EditText) findViewById(R.id.nameinput)).getText().length() != 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(((EditText) findViewById(R.id.nameinput)).getText());
                            sb.deleteCharAt(((EditText) findViewById(R.id.nameinput)).getText().length() - 1);
                            ((EditText) findViewById(R.id.nameinput)).setText(sb.toString());
                            ((EditText) findViewById(R.id.nameinput)).setSelection(((EditText) findViewById(R.id.nameinput)).getText().length());
                        } else {
                            ((EditText) findViewById(R.id.nameinput)).setText("");
                        }
                    }
                }
            }
        });

        ((EditText) findViewById(R.id.passwordinput)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verifyAndEnableLoginButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // Verify if char is in list (alphabet)
    boolean charallowed(char c){
        char[] alphabet = {'a','b','c','d','e','è','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        boolean found = false;

        for (int i = 0; i<alphabet.length; i++){
            if (String.valueOf(c).equalsIgnoreCase(String.valueOf(alphabet[i]))) {
                found=true;
            }
        }

        return found;
    }

    // Enable login button if rules are respected
    void verifyAndEnableLoginButton() {
        // Enables login button if name and password are ready
        ((Button) findViewById(R.id.login_bt)).setEnabled(
                ((EditText) findViewById(R.id.nameinput)).getText().toString().length() >= 6 &&
                        ((EditText) findViewById(R.id.passwordinput)).getText().toString().length() >= 4
        );

        // Enable passwordinput if name satisfies rules
        if(((EditText) findViewById(R.id.nameinput)).getText().toString().length() >= 6) {
            findViewById(R.id.passwordinput).setEnabled(true);
            ((EditText)findViewById(R.id.passwordinput)).setHint(getString(R.string.password));

        }  // Check if nameinput is empty, restore initial state
        else if (((EditText) findViewById(R.id.nameinput)).getText().toString().length() == 0){
            findViewById(R.id.passwordinput).setEnabled(false);
            ((EditText)findViewById(R.id.passwordinput)).setHint(getString(R.string.password));

        } else {
            findViewById(R.id.passwordinput).setEnabled(false);
            ((EditText)findViewById(R.id.passwordinput)).setHint(getString(R.string.add_chars)+ ": " + (6-((EditText) findViewById(R.id.nameinput)).getText().toString().length()));
        }
    }

    // Method for opening progress dialog to hang user while logging in
    void callprogressdialoglogin() {
        pd = ProgressDialog.show(Login.this, getString(R.string.please_wait), getString(R.string.logging_in), true);
    }

    // Rule 1: Name must be 6+ chars long
    boolean rule1 (){
        if (((EditText) findViewById(R.id.nameinput)).getText().toString().length() >= 6){
            return true;
        }else{
            GenericAdvice("Name not ok");
            return false;
        }
    }

    // Rule 2: Password must be 4+ chars long
    boolean rule2 (){
        if (((EditText) findViewById(R.id.passwordinput)).getText().toString().length() >= 4){
            return true;
        }else{
            GenericAdvice("Password not ok");
            return false;
        }
    }

    // Rule 3: Institute and class must be set
    // Note: Automatic call spinner
    boolean rule3 (){
        if (!Objects.equals(global.GlobalClass, "") && !Objects.equals(global.GlobalInstitute, "")){
            return true;
        }else{
            if(!Objects.equals(global.GlobalInstitute, "")) {
                GenericAdvice(getString(R.string.choose_your_institute));
            }
            if(!Objects.equals(global.GlobalClass, "")) {

                GenericAdvice(getString(R.string.choose_your_class));
            }
            ((Spinner)findViewById(R.id.institutes_spinner)).performClick();
            return false;
        }
    }

    // Rule 4: class must not be full
    // If login button contains "Login" this means is recognized as registered user, so no need to check
    boolean rule4(){
        if (!Objects.equals(((Button) findViewById(R.id.login_bt)).getText().toString(), getString(R.string.login))) {
            if (global.classusers <= global.maxclassusers) {
                return true;
            } else {
                GenericAdvice(getString(R.string.class_full));
                return false;
            }
        }else {return true;}
    }

    // Called when UserManager makes login
    @Override
    public void onLoginResult(boolean success, int reason) {
        if (pd.isShowing()) {
            pd.dismiss();
        }

        switch (reason) {
            case UserManager.LOGIN_SUCCEEDED:
                GenericAdvice(getString(R.string.welcome_back));
                startActivity(new Intent(this, Home.class));
                break;

            case UserManager.REGISTRATION_SUCCEDED:
                //GenericAdvice("Welcome to your new Diary");
                startActivity(new Intent(this, Home.class));
                break;

            case UserManager.REASON_FAIL_PASSWORD_CHECK:
                GenericAdvice(getString(R.string.wrong_password));
                break;

            case UserManager.REASON_CLASS_FULL:
                GenericAdvice(getString(R.string.class_full));
                break;

            case UserManager.REASON_USER_NOT_EXIST:
                // Protection for register dialog, can't call it if secondarySetup() isn't called
                // Because of lack of data
                // If oncesetup is false, show GenericAdvice
                if(OnceSetup) {
                    // Registration dialog
                    AlertDialog ad = new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_abstract_user_flat_2)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.new_user_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    // User want to sign up, call eula dialog

                                    final Dialog d = new Dialog(Login.this);
                                    d.setContentView(R.layout.suredialog);
                                    d.setCancelable(false);

                                    ((Switch) d.findViewById(R.id.swacpt)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            ((Button) d.findViewById(R.id.accept)).setEnabled(isChecked);
                                        }
                                    });
                                    ((Button) d.findViewById(R.id.refuse)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            d.dismiss();
                                        }
                                    });
                                    ((Button) d.findViewById(R.id.accept)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            d.dismiss();
                                            // Login and register then
                                            new UserManager().Login(
                                                    Login.this,
                                                    ((EditText) findViewById(R.id.nameinput)).getText().toString(),
                                                    ((EditText) findViewById(R.id.passwordinput)).getText().toString(),
                                                    global.GlobalClass,
                                                    global.GlobalInstitute,
                                                    true,
                                                    ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId(),
                                                    Login.this,
                                                    true);

                                        }
                                    });

                                    d.show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User doesn't want to sign up. Close dialog.
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }else{
                    GenericAdvice(getString(R.string.user_not_exist));
                }
                secondarySetup();
                break;

            case UserManager.REASON_BANNED:
                // Use other caller method for setup and more info
                // GenericAdvice("Banned");
                break;
        }

    }

    // Shows a toast or snackbar based on availability
    void GenericAdvice (final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Avoid app crash if focus is null
                if(getCurrentFocus()!=null) {
                    Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Login.this,message,Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    // Called when UserManager recognises a banned device, with more informations than onLoginResult
    @Override
    public void onBannedResult(long expiry, String Reason) {
        Intent bannedintent = new Intent(this, BannedActivity.class);
        bannedintent.putExtra("expiry", String.valueOf(expiry));
        bannedintent.putExtra("reason", Reason);
        startActivity(bannedintent);
        finish();
    }

    @Override
    public void userExist(boolean exist) {
        if (exist){
            ((Button) findViewById(R.id.login_bt)).setText(getString(R.string.login));
        } else {
            ((Button) findViewById(R.id.login_bt)).setText(getString(R.string.register));
        }
    }

    // Not used but required for standard interface
    @Override
    public void isBanned(boolean isbanned) {

    }

    @Override
    public Context getContext() {
        return this;
    }
}
