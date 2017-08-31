package utenti.diario.utilities.usermanagement;

import android.content.Context;
import android.support.annotation.Nullable;

/**
 * Created by SosiForWork on 30/08/2017.
 */

public interface UserManagerInterface {
    void userExist(boolean exist, @Nullable String alias);
    void isBanned(boolean isbanned);
    Context getContext();
}
