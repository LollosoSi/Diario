package utenti.diario.regole.permissions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import utenti.diario.container.Container;
import utenti.diario.utilities.arraylist.ArrayListManager;
import utenti.diario.utilities.exceptions.MhanzException;
import utenti.diario.utilities.usermanagement.LoginManager;

/**
 * Created by LollosoSi on 01/09/2017.
 */

public class PermissionsManager {

    public void ParsePermissions(final PermissionsInterface pi) {
        // Set database
        DatabaseReference dr = Container.getInstance().databaseManager.getDatabase();

        // Get data
        LoginManager lm = new LoginManager();
        lm.StoreContext(pi.getContext());

        // Setup variables
        String name = lm.getName(null);
        String institute = lm.getInstitute(null);
        String Class = lm.getClass(null);

        //Permission handler
        final Permissions perm = new Permissions();

        // Get permissions array
        dr.child("Institutes").child(institute).child(Class).child("users").child(name).child("permissions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> rawperms = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                    ArrayList<String> fullperms = new ArrayList<String>();

                    for (int i = 0; i < rawperms.size(); i++) {

                        if (rawperms.get(i).equals("*.*")) {
                            i = rawperms.size();
                            perm.addAllPermissions();
                        } else {
                            perm.addPermission(new ArrayList<String>(Arrays.asList(rawperms.get(i).split("\\."))));
                        }
                    }

                    pi.onPermissionsParsed();
                } else {
                    try {
                        throw new MhanzException("Permissions doesn't exist");
                    } catch (MhanzException e) {
                        e.printStackTrace();
                    }

                    Container.getInstance().permissionslist = new ArrayList<String>();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Checks permission for client classes, gets one string but REMEMBER: ONLY ONE . IS PERMITTED PER STRING
    public boolean hasPermission(String permission) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(permission.split("\\.")));
        return new Permissions().hasPermission(temp.get(0), temp.get(1));
    }

}
