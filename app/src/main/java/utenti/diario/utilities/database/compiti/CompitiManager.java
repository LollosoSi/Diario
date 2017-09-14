package utenti.diario.utilities.database.compiti;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import utenti.diario.container.Container;
import utenti.diario.regole.SpecialSymbols;
import utenti.diario.utilities.arraylist.ArrayListManager;
import utenti.diario.utilities.usermanagement.LoginManager;

/**
 * Created by SosiForWork on 01/09/2017.
 */

public class CompitiManager {

    ArrayList<String> Materie = null;
    ArrayList<String> Compiti = null;
    ArrayList<String> Authors = null;
    ArrayList<String> immagini = null;
    ArrayList<String> immaginiID = null;

    CompitiInterface globalci;

    boolean oneTimeCall = false;

    public void getCompiti(String formattedDate, CompitiInterface ci) {
        // Setup variables
        DatabaseReference dr = Container.getInstance().databaseManager.getDatabase();
        LoginManager lm = new LoginManager();
        lm.StoreContext(ci.getContext());
        globalci = ci;

        oneTimeCall = true;

        // Call getters
        CallGetters(lm, dr, formattedDate.replace("/", "-"));


    }

    void CallGetters(LoginManager lm, DatabaseReference dr, String formattedDate) {
        getAssegnamenti(lm, dr, formattedDate);
        getImages(lm, dr, formattedDate);
        getAuthors(lm, dr, formattedDate);
    }

    // TODO: Refer to SpecialSymbols for divider key and space key
    // Gets Materie e Compiti
    void getAssegnamenti(LoginManager lm, DatabaseReference dr, final String date) {

        dr.child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("compiti").child(date).child("assegnamenti").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> temptotallist = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());

                    ArrayList<String> tempmaterie = new ArrayList<String>();
                    ArrayList<String> tempcompiti = new ArrayList<String>();


                    ArrayList<String> tempunified = new ArrayList<String>();

                    for (int i = 0; i < temptotallist.size(); i++) {

                        tempunified = new ArrayList<String>(Arrays.asList((temptotallist.get(i)).split(SpecialSymbols.divider_key)));


                        tempmaterie.add(tempunified.get(0).replace(SpecialSymbols.space_key, " "));
                        tempcompiti.add(tempunified.get(1).replace(SpecialSymbols.space_key, " "));

                    }

                    Materie = tempmaterie;
                    Compiti = tempcompiti;


                } else {
                    Materie = null;
                    Compiti = null;

                }

                // Check if asyncronous actions are done
                checkDataAndCallInterface();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void getAuthors(LoginManager lm, DatabaseReference dr, final String date) {

        dr.child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("compiti").child(date).child("scritto_da").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> temptotallist = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());

                    ArrayList<String> tempauthors = new ArrayList<String>();

                    for (int i = 0; i < temptotallist.size(); i++) {

                        tempauthors.add(temptotallist.get(i).replace(SpecialSymbols.space_key, " "));

                    }

                    Authors = tempauthors;


                } else {
                    Authors = null;
                }

                // Check if asyncronous actions are done
                checkDataAndCallInterface();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void getImages(LoginManager lm, DatabaseReference dr, String date) {

        dr.child("Institutes").child(lm.getInstitute(null)).child(lm.getClass(null)).child("compiti").child(date).child("immagini").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> temptotallist = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());

                    ArrayList<String> tempid = new ArrayList<String>();
                    ArrayList<String> tempdidascalia = new ArrayList<String>();


                    ArrayList<String> tempunified = new ArrayList<String>();

                    for (int i = 0; i < temptotallist.size(); i++) {

                        tempunified = new ArrayList<String>(Arrays.asList(temptotallist.get(i).split(SpecialSymbols.divider_key)));

                        tempdidascalia.add(tempunified.get(0).replace(SpecialSymbols.space_key, " "));
                        tempid.add(tempunified.get(1).replace(SpecialSymbols.space_key, " "));
                    }

                    immagini = tempdidascalia;
                    immaginiID = tempid;

                } else {
                    immagini = new ArrayList<String>();
                    immaginiID = new ArrayList<String>();
                }

                // Check if asyncronous actions are done
                checkDataAndCallInterface();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void checkDataAndCallInterface() {
        // This could be called multiple times
        if (oneTimeCall) {
            // Pass data to interface if homework is detected, or notify empty space in database
            if (Compiti != null && Materie != null && Authors != null && immagini != null && immaginiID != null) {
                oneTimeCall = false;
                Toast.makeText(globalci.getContext(), "Interface called", Toast.LENGTH_LONG).show();
                globalci.onCompitiGot(Materie, Compiti, Authors, immagini, immaginiID);
            } else if (Compiti == null && Materie == null) {
                oneTimeCall = false;
                globalci.onCompitiNotGot();
            }
        }
    }


}
