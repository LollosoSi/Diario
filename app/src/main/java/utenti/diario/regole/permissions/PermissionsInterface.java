package utenti.diario.regole.permissions;

import android.content.Context;

/**
 * Created by SosiForWork on 01/09/2017.
 */

public interface PermissionsInterface {
    Context getContext();

    void onPermissionsParsed();
}
