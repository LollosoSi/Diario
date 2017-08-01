package utenti.diario.utilities.log;

/**
 * Created by LollosoSi on 01/08/2017.
 * Here we keep the method to call for debug logs, only for debug
 */
import android.util.Log;

import utenti.diario.regole.Informations;

public class LogManager {

    Informations info = null;

    final public int LogFlagInfo = 0;
    final public int LogFlagAlert = 1;

    public LogManager(){
        // Init of class
        info=new Informations();
    }
    public void Log(String TAG, String Message, int LogFlag){

        if ( TAG == null ) {
            // Assignment if null
            TAG = "StandardDebugAlert";
        }

        if ( Message == null ){
            // Assigment if null
            Message = "No message for this tag";
        }

        if(LogFlag == Integer.parseInt(null) ||(LogFlag!=LogFlagAlert&&LogFlag!=LogFlagInfo)){
            // Checking if no flag or wrong is passed
            LogFlag=LogFlagInfo;
            Log(null,"The following log has wrong LogFlag",LogFlagAlert);
        }

        if (info.isDebugActive){
            //Logging in Logcat
            switch (LogFlag) {
                case LogFlagInfo:
                    android.util.Log.i(TAG,Message);
                    break;
                case LogFlagAlert:
                    android.util.Log.e(TAG,Message);
                    break;
            }
        }
    }

/** End Of Class */
}
