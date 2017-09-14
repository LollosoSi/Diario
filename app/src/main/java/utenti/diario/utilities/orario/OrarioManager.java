package utenti.diario.utilities.orario;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import utenti.diario.R;
import utenti.diario.activities.login.Login;
import utenti.diario.container.Container;
import utenti.diario.utilities.arraylist.ArrayListManager;
import utenti.diario.utilities.usermanagement.LoginManager;

/**
 * Created by SosiForWork on 02/09/2017.
 */

public class OrarioManager {

    ArrayList<String> Lunedi = null;
    ArrayList<String> Martedi = null;
    ArrayList<String> Mercoledi = null;
    ArrayList<String> Giovedi = null;
    ArrayList<String> Venerdi = null;
    ArrayList<String> Sabato = null;
    ArrayList<String> Domenica = null;

    LoginManager lm;
    OrarioInterface globalOI;

    public void DownloadOrario(Context ctx, OrarioInterface orarioInterface) {
        lm = new LoginManager();
        lm.StoreContext(ctx);
        globalOI = orarioInterface;

        SetupDaysList(ctx);

        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("orario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Getdata("Domenica");
                    Getdata("Lunedi");
                    Getdata("Martedi");
                    Getdata("Mercoledi");
                    Getdata("Giovedi");
                    Getdata("Venerdi");
                    Getdata("Sabato");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // get orario from database
    void Getdata(final String day) {
        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("orario").child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    switch (day) {
                        case "Lunedi":
                            Lunedi = new ArrayList<String>(new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString()));
                            break;

                        case "Martedi":
                            Martedi = new ArrayList<String>(new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString()));
                            break;

                        case "Mercoledi":
                            Mercoledi = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                            break;

                        case "Giovedi":
                            Giovedi = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                            break;

                        case "Venerdi":
                            Venerdi = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                            break;

                        case "Sabato":
                            Sabato = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                            break;

                        case "Domenica":
                            Domenica = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                            break;
                    }
                } else {
                    switch (day) {
                        case "Lunedi":
                            Lunedi = new ArrayList<String>();
                            break;

                        case "Martedi":
                            Martedi = new ArrayList<String>();
                            break;

                        case "Mercoledi":
                            Mercoledi = new ArrayList<String>();
                            break;

                        case "Giovedi":
                            Giovedi = new ArrayList<String>();
                            break;

                        case "Venerdi":
                            Venerdi = new ArrayList<String>();
                            break;

                        case "Sabato":
                            Sabato = new ArrayList<String>();
                            break;

                        case "Domenica":
                            Domenica = new ArrayList<String>();
                            break;
                    }
                }

                unifyData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void SetupDaysList(Context ctx) {
        Container.getInstance().Days = new ArrayList<String>();

        Container.getInstance().Days.add(ctx.getString(R.string.day_domenica));
        Container.getInstance().Days.add(ctx.getString(R.string.day_lunedi));
        Container.getInstance().Days.add(ctx.getString(R.string.day_martedi));
        Container.getInstance().Days.add(ctx.getString(R.string.day_mercoledi));
        Container.getInstance().Days.add(ctx.getString(R.string.day_giovedi));
        Container.getInstance().Days.add(ctx.getString(R.string.day_venerdi));
        Container.getInstance().Days.add(ctx.getString(R.string.day_sabato));

    }

    public String getDayName(int day) {
        return Container.getInstance().Days.get(day - 1);
    }

    void unifyData() {
        // Wait for all data to set
        if (Lunedi != null && Martedi != null && Mercoledi != null &&
                Giovedi != null && Venerdi != null && Sabato != null &&
                Domenica != null) {

            // NOTE: FIXED ORDER
            Container.getInstance().orario = new ArrayList<>();
            Container.getInstance().orario.add(Domenica.toString());
            Container.getInstance().orario.add(Lunedi.toString());
            Container.getInstance().orario.add(Martedi.toString());
            Container.getInstance().orario.add(Mercoledi.toString());
            Container.getInstance().orario.add(Giovedi.toString());
            Container.getInstance().orario.add(Venerdi.toString());
            Container.getInstance().orario.add(Sabato.toString());

            globalOI.onOrarioDownloaded();

        }


    }

    public ArrayList<String> getGiorno(int giorno) {
        if (giorno < 7 && giorno >= 0) {
            return new ArrayListManager().ConvertedArrayFromFirebase(Container.getInstance().orario.get(giorno));
        } else {
            return new ArrayList<>();
        }
    }

    public int getHourInDay(int Day, String Materia) {
        ArrayList<String> orario = getGiorno(Day);
        int hour = 999;
        for (int i = 0; i < orario.size(); i++) {
            if (orario.get(i).equalsIgnoreCase(Materia)) {
                hour = i + 1;
            }
        }
        return hour;
    }

    public boolean isOrarioAvailable() {
        return (Container.getInstance().orario != null);
    }

    String TAG = "ORARIO";

    public void LogOrario() {
        for (int d = 1; d < 8; d++) {

            Log.d(TAG, getDayName(d));
            Log.d(TAG, "----------");
            for (int i = 0; i < getGiorno(d).size(); i++) {

                Log.d(TAG, getGiorno(d).get(i));

            }


        }
    }
}
