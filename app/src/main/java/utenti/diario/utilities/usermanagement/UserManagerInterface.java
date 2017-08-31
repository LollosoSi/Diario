package utenti.diario.utilities.usermanagement;

import android.content.Context;

/**
 * Created by SosiForWork on 30/08/2017.
 */

public interface UserManagerInterface {
    void userExist (boolean exist);
    void isBanned(boolean isbanned);
    Context getContext();
}
