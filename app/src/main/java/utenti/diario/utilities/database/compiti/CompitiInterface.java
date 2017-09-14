package utenti.diario.utilities.database.compiti;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by SosiForWork on 01/09/2017.
 */

public interface CompitiInterface {
    Context getContext();

    void onCompitiGot(ArrayList<String> Materie, ArrayList<String> Compiti, ArrayList<String> Authors, ArrayList<String> immagini, ArrayList<String> immaginiID);

    void onCompitiNotGot();
}
