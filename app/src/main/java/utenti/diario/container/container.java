package utenti.diario.container;

import utenti.diario.utilities.database.DatabaseManager;

/**
 * Created by SosiForWork on 01/08/2017.
 */

public class Container {

    private static Container ourInstance = new Container();

    public static Container getInstance() {
        return ourInstance;
    }

    public DatabaseManager databaseManager=null;

}
