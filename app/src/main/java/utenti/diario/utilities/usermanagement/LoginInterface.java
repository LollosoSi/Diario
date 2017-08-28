package utenti.diario.utilities.usermanagement;

/**
 * Created by SosiForWork on 28/08/2017.
 */

public interface LoginInterface {
    void onLoginResult(boolean success, int reason);
    void onBannedResult(int expiry, String Reason);
}
