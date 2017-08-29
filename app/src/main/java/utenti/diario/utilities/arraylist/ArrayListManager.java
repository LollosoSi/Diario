package utenti.diario.utilities.arraylist;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by LollosoSi on 01/08/2017.
 */

public class ArrayListManager {

    public ArrayList<String> ConvertedArrayFromFirebase(String data){
        return new ArrayList<>(Arrays.asList(data.replace("[","").replace("]","").replace(" ","").replace("§"," ").split(",")));
    }
}
