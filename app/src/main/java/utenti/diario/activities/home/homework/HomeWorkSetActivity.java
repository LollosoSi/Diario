package utenti.diario.activities.home.homework;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;

import utenti.diario.R;
import utenti.diario.activities.home.Home;
import utenti.diario.activities.login.Login;
import utenti.diario.container.Container;
import utenti.diario.utilities.database.compiti.CompitiInterface;
import utenti.diario.utilities.database.compiti.CompitiManager;
import utenti.diario.utilities.usermanagement.LoginManager;

public class HomeWorkSetActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, CompitiInterface {

    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_set);

        // Enable back button on ActionBar
        getSupportActionBar().setHomeButtonEnabled(true);

        findViewById(R.id.textView_set_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HomeWorkSetActivity.this, HomeWorkSetActivity.this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

        findViewById(R.id.done_button).setVisibility(View.GONE);

        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LoginManager lm = new LoginManager();
                lm.StoreContext(HomeWorkSetActivity.this);

                // Process data and clear
                ha.saveAndClear();

                // Get arrays
                ArrayList<String> assignmentsToSave = ha.getSavedArray();
                final ArrayList<String> authorsToSave = ha.getSavedAuthArray();

                if (assignmentsToSave == null) {
                    // Clear data on database
                    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("compiti").child(formatteddate).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });

                } else if (assignmentsToSave.size() == 0) {
                    // Clear data on database
                    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("compiti").child(formatteddate).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                } else {
                    // Set assignments array in database normally
                    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("compiti").child(formatteddate).child("assegnamenti").setValue(assignmentsToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Container.getInstance().databaseManager.getDatabase().child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("compiti").child(formatteddate).child("scritto_da").setValue(authorsToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finish();
                                }
                            });
                        }
                    });
                }


            }
        });
    }

    HomeworkAdapter ha = new HomeworkAdapter();
    int giorno;
    String formatteddate;

    // Back button handling
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // User has picked a date
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        callCompiti(year, month, day);
        pd = ProgressDialog.show(this, "", getString(R.string.please_wait), true);
    }

    void callCompiti(int year, int month, int day) {

        Calendar lastCalendar;
        lastCalendar = Calendar.getInstance();
        lastCalendar.set(Calendar.YEAR, year);
        lastCalendar.set(Calendar.MONTH, month);
        lastCalendar.set(Calendar.DAY_OF_MONTH, day);

        giorno = lastCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        formatteddate = day + "-" + (month + 1) + "-" + year;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        new CompitiManager().getCompiti(day + "-" + (month + 1) + "-" + year, this);

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onCompitiGot(ArrayList<String> Materie, ArrayList<String> Compiti, ArrayList<String> Authors, ArrayList<String> immagini, ArrayList<String> immaginiID) {
        ha.setup(this, Materie, Compiti, Authors, giorno);

        pd.dismiss();
        findViewById(R.id.done_button).setVisibility(View.VISIBLE);

        ((ListView) findViewById(R.id.listview_set)).setAdapter(ha);
    }

    @Override
    public void onCompitiNotGot() {
        ha.setup(this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), giorno);

        pd.dismiss();
        findViewById(R.id.done_button).setVisibility(View.VISIBLE);

        ((ListView) findViewById(R.id.listview_set)).setAdapter(ha);
    }
}
