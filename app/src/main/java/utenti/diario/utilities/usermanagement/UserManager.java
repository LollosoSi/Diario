package utenti.diario.utilities.usermanagement;

import android.content.Context;
import android.os.SystemClock;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import utenti.diario.BuildConfig;
import utenti.diario.container.Container;
import utenti.diario.security.Encryption;
import utenti.diario.utilities.database.DatabaseManager;



public class UserManager {

    final public int PERMISSIONS_USER = 0;
    final public int PERMISSIONS_WRITER = 1;
    final public int PERMISSIONS_ADMIN = 2;

    final public static int LOGIN_SUCCEEDED = 0;
    final public static int REASON_FAIL_PASSWORD_CHECK = 1;
    final public static int REASON_USER_NOT_EXIST = 2;
    final public static int REASON_BANNED = 4;

    private String Banreason="";
    private int BanExpiry = 0;

    public boolean Registration(final String name, String password, final String Class, final String institute,String imei){
        /** This method registers a new user and initializes all data */

        if(userExist(institute,Class,name)){
            return false;
        }else{
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            String CurrentTime = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+", "+c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1) +"-"+c.get(Calendar.YEAR);

            /*Store hashed password*/  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("password").setValue(new Encryption().Hash256(password));
            /*Store hashed Imei*/  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("id").setValue(new Encryption().Hash256(imei));
            /*Set permissions*/    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("permissions").setValue(new ArrayList<String>(Arrays.asList("user_normal","user_read")));
            /*Set using version */  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("version").setValue(BuildConfig.VERSION_CODE);
            /*Set Last login time*/  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("lastLogin").setValue(CurrentTime);

return true;
        }

    }

    public void Login(final LoginInterface li,
                      final String name, final String password, final String Class, final String institute,
                      final Boolean StoreInfo /* Used for "remember me" feature */,
                      String IMEI /* Used for checking phone ban */,
                      final Context ctx){

        if(!isBanned(IMEI,new Container().databaseManager.getDatabase(),ctx)){
            // Not banned, proceed


                    if(!userExist(institute,Class,name)){li.onLoginResult(false, REASON_USER_NOT_EXIST);}else{

                        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    if(verifyPassword(dataSnapshot.getValue().toString(),password)){
                                        if (StoreInfo){
                                            new LoginManager().SaveCredentials(ctx,name,password,institute,Class);
                                        }
                                        li.onLoginResult(true, LOGIN_SUCCEEDED);
                                    }else{
                                        li.onLoginResult(false, REASON_FAIL_PASSWORD_CHECK);
                                    }

                                }else{
                                    // Setting last login date
                                    Calendar c = Calendar.getInstance();
                                    c.setTimeInMillis(System.currentTimeMillis());
                                    String CurrentTime = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+", "+c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1) +"-"+c.get(Calendar.YEAR);
                                    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("lastLogin").setValue(CurrentTime);

                                    if (StoreInfo){
                                        new LoginManager().SaveCredentials(ctx,name,null,institute,Class);
                                    }

                                    li.onLoginResult(true, LOGIN_SUCCEEDED);}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


        }else{
            // Banned! Notify via LI to update UI
            li.onLoginResult(false,REASON_BANNED);
            li.onBannedResult(BanExpiry,Banreason);
        }
    }

    boolean userExist(String institute, String Class, String name){
        final boolean[] found = {false};
        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                found[0] = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return found[0];
    }

    boolean isBanned(String Imei, DatabaseReference dr, Context ctx){
        String EncryptedImei = new Encryption().Hash256(Imei);
        ArrayList<String> bannedList = getBannedUsersList(dr);
        Boolean banned = false;

        for (int i = 0; i < bannedList.size(); i++){
            banned = bannedList.get(i).equals(EncryptedImei);
            if(!banned){
                i += 2;
            }else{
                BanExpiry= Integer.parseInt(bannedList.get(i+1));
                Banreason=bannedList.get(i+2);
               if(BanExpiry > System.currentTimeMillis()){
                   // Ban not expired, report in UI

               } else {
                   // Ban expired, act as nothing happened
                   banned=false;

               }
               i=bannedList.size();
            }
        }

        return banned;
    }

    // Gets Users banned list full with IMEI(Encrypted), expiry date and reason
    ArrayList<String> getBannedUsersList(DatabaseReference dr){
        dr.child("BannedUsers").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    Container.getInstance().temparraylist = new ArrayList<String>(Arrays.asList(dataSnapshot.getValue().toString().replace(" ","").replace("[","").replace("]","").split(",")));

                }else{
                    Container.getInstance().temparraylist = new ArrayList<String>(Arrays.asList("dummy","0123","BanReason"));}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return Container.getInstance().temparraylist;
    }

    public boolean verifyPassword(String Encrypted, String attempt){return (Encrypted.equals(new Encryption().Hash256(attempt)));}
}
