package utenti.diario.container;

/**
 * Created by SosiForWork on 01/08/2017.
 */

public class Container {

    private static Container ourInstance = new Container();

    public static Container getInstance() {
        return ourInstance;
    }



}
