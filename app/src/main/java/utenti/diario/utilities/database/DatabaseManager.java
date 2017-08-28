package utenti.diario.utilities.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import utenti.diario.container.Container;

/**
 * Created by SosiForWork on 01/08/2017.
 */

public class DatabaseManager {

    DatabaseReference DR = null;

    public DatabaseManager(){
        // Initializing

        DR = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getClassPath(String institute, String Class){
        DatabaseReference i = DR.child("Istituti").child(institute).child(cla);
        return i;
    }
}
