package utenti.diario.utilities.exceptions;

/**
 * Created by SosiForWork on 28/08/2017.
 */

public class MhanzException extends Exception {
    public MhanzException(String message) {
        super(message);
        throw new NullPointerException();
    }

}
