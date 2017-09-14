package utenti.diario.container;

import android.graphics.Bitmap;

import java.util.ArrayList;

import utenti.diario.regole.permissions.PermissionInternalFragment;
import utenti.diario.utilities.database.DatabaseManager;

/**
 * Created by LollosoSi on 01/08/2017.
 */

public class Container {

    private static Container ourInstance = new Container();

    public static Container getInstance() {
        return ourInstance;
    }

    // Public database manager, database reference for the entire project
    public DatabaseManager databaseManager=null;

    // Used in UserManager for banlist
    public ArrayList<String> temparraylist=new ArrayList<>();

    // Global arrays for spinners (Login screen only)
    public ArrayList<String> tempinstituteslist=null;
    public ArrayList<String> tempclasseslist=null;

    // Globals for Selection (Login screen only)
    public String GlobalInstitute = "";
    public String GlobalClass = "";

    // Globals for class size
    public int maxclassusers = 0;
    public int classusers = 0;

    // Complete permissions list
    public ArrayList<String> permissionslist = null;
    public ArrayList<PermissionInternalFragment> internalFragments = new ArrayList<>();
    public ArrayList<String> parents = new ArrayList<>();
    public ArrayList<PermissionInternalFragment> userFragments = new ArrayList<>();

    // Orario
    public ArrayList<String> orario = null;

    // Days in list
    public ArrayList<String> Days = null;
}
