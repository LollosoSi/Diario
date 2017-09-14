package utenti.diario.utilities.database.cards;

import android.content.Context;

/**
 * Created by SosiForWork on 02/09/2017.
 */

public class IDidItManager {

    Context ctx;
    public static final String SHARED_PREFS_FILE = "made_work";

    public IDidItManager(Context ctx) {
        this.ctx = ctx;
    }

    boolean getValue(String date, String Materia, String Content) {
        return ctx.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE).getBoolean(date + Materia + Content, false);
    }

    void ChangeValue(String date, String Materia, String Content, boolean state) {
        ctx.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE).edit().putBoolean(date + Materia + Content, state).commit();
    }

}
