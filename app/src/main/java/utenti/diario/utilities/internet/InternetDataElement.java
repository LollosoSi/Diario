package utenti.diario.utilities.internet;

/**
 * Created by SosiForWork on 03/08/2017.
 */

public class InternetDataElement {

    InternetCheck IC = null;
    int RequestID = 0;

    public InternetDataElement (InternetCheck IC, int RequestID){
        this.IC = IC;
        this.RequestID = RequestID;
    }
}
