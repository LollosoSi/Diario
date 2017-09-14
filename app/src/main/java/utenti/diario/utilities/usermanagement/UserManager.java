package utenti.diario.utilities.usermanagement;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import utenti.diario.BuildConfig;
import utenti.diario.container.Container;
import utenti.diario.security.Encryption;
import utenti.diario.utilities.arraylist.ArrayListManager;



public class UserManager implements UserManagerInterface {

    final public int PERMISSIONS_USER = 0;
    final public int PERMISSIONS_WRITER = 1;
    final public int PERMISSIONS_ADMIN = 2;

    final public static int LOGIN_SUCCEEDED = 0;
    final public static int REASON_FAIL_PASSWORD_CHECK = 1;
    final public static int REASON_USER_NOT_EXIST = 2;
    final public static int REASON_CLASS_FULL = 3;
    final public static int REASON_BANNED = 4;
    final public static int REGISTRATION_SUCCEDED = 5;
    final public static int REASON_IS_ALIAS = 6;

    private String Banreason="";
    private long BanExpiry = 0;


    void Registration(final String name, String password, final String Class, final String institute, String imei){

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        String CurrentTime = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+", "+c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1) +"-"+c.get(Calendar.YEAR);

            /*Store hashed password*/  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("password").setValue(new Encryption().Hash256(password));
            /*Store hashed Imei*/  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("id").setValue(new Encryption().Hash256(imei));
            /*Set permissions*/
        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("permissions").setValue(new ArrayList<String>(Arrays.asList("user.read")));
            /*Set using version */  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("version").setValue(BuildConfig.VERSION_CODE);
            /*Set Last login time*/  Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("lastLogin").setValue(CurrentTime);

        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("userslist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ArrayList<String> users = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                    users.add(name);
                    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("userslist").setValue(users);
                } else {
                   ArrayList<String> users = new ArrayList<String>(Arrays.asList(name));
                    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("userslist").setValue(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public LoginInterface latestli;
    String institute;
    String Class;
    String imei;
    String name;
    String password;
    boolean StoreInfo;
    boolean RegisterIfNotExist;
    Context ctx;
    public void Login(final LoginInterface li,
                      final String name, final String password, final String Class, final String institute,
                      final Boolean StoreInfo /* Used for "remember me" feature */,
                      String IMEI /* Used for checking phone ban */,
                      final Context ctx,
                      boolean RegisterIfNotExist){

        this.RegisterIfNotExist = RegisterIfNotExist;
        this.StoreInfo = StoreInfo;
        latestli = li;
        this.imei = IMEI;
        this.name=name;
        this.password=password;
        this.ctx=ctx;
        this.institute=institute;
        this.Class=Class;

        // 2 Methods chain

        isBanned(IMEI,Container.getInstance().databaseManager.getDatabase(),ctx,this);


    }

    // Check if user exist. USES UMI
    public void userExist(String institute, String Class, final String name, final UserManagerInterface umi) {


       if(institute != null && Class != null && name != null ) {

           Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("userslist").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                   if (dataSnapshot.exists()) {

                       ArrayList<String> users = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                       boolean exist = false;
                       boolean hasAlias = false;
                       String Alias = "";

                       for (int i = 0; i < users.size(); i++) {

                           if (users.get(i).equalsIgnoreCase(name)) {
                               exist = true;
                               if (!Objects.equals(users.get(i), name)) {
                                   hasAlias = true;
                                   Alias = users.get(i);
                               }
                           }
                       }

                       if (exist) {

                           if (hasAlias) {
                               umi.userExist(true, Alias);
                           } else {
                               umi.userExist(true, "");
                           }
                       } else {
                           umi.userExist(false, "");
                       }

                   } else {
                       umi.userExist(false, "");
                   }


               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
       }else{
           if (institute == null) {
               Toast.makeText(umi.getContext(), "institute null", Toast.LENGTH_LONG).show();
           }
           if (name == null) {
               Toast.makeText(umi.getContext(), "name null", Toast.LENGTH_LONG).show();
           }
           if (Class == null) {
               Toast.makeText(umi.getContext(), "Class null", Toast.LENGTH_LONG).show();
           }

       }

    }

    // Check if user is banned. USES UMI
    void isBanned(final String Imei, final DatabaseReference dr, Context ctx, final UserManagerInterface umi){

        dr.child("BannedUsers").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> bannedList;

                if (dataSnapshot.exists()) {
                    bannedList = new ArrayList<String>(Arrays.asList(dataSnapshot.getValue().toString().replace(" ", "").replace("[", "").replace("]", "").split(",")));
                }else{
                    bannedList = new ArrayList<String>(Arrays.asList("dummy", "0123", "BanReason"));
                }

                String EncryptedImei = new Encryption().Hash256(Imei);

                Boolean banned = false;

                for (int i = 0; i < bannedList.size(); i++){
                    banned = bannedList.get(i).equals(EncryptedImei);
                    if(!banned){
                        i += 2;
                    }else{
                        BanExpiry= Long.parseLong(String.valueOf(bannedList.get(i+1)));
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


                umi.isBanned(banned);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public boolean verifyPassword(String Encrypted, String attempt){return (Encrypted.equals(new Encryption().Hash256(attempt)));}

    @Override
    public void userExist(boolean exist, String alias) {

        // do not proceed if alias is found
        if (!Objects.equals(alias, "")) {
            latestli.onAliasFound(alias);
            latestli.onLoginResult(false, REASON_IS_ALIAS);

        } else {

            if (!exist){

                if (!RegisterIfNotExist) {

                    latestli.onLoginResult(false, REASON_USER_NOT_EXIST);
                } else {
            // Check maximum users

                    Container.getInstance().databaseManager.getDatabase().child("Institutes").child(Container.getInstance().GlobalInstitute).child(Container.getInstance().GlobalClass).child("utenza").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int max = 0;
                    int actualusers = 0;

                    if(dataSnapshot.exists()){
                        ArrayList<String> usersnum = new ArrayListManager().ConvertedArrayFromFirebase(dataSnapshot.getValue().toString());
                        actualusers = Integer.parseInt(usersnum.get(0));
                        max = Integer.parseInt(usersnum.get(1));
                    }

                    if (actualusers < max) {
                        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(Container.getInstance().GlobalInstitute).child(Container.getInstance().GlobalClass).child("utenza").setValue(new ArrayList<>(Arrays.asList((actualusers + 1), max)));

                        Registration(name,password,Class,institute,imei);
                        if (StoreInfo){
                            new LoginManager().SaveCredentials(ctx,name,password,institute,Class);
                        }
                        latestli.onLoginResult(true,REGISTRATION_SUCCEDED);

                    }else{
                        latestli.onLoginResult(false,REASON_CLASS_FULL);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            }
        }else {

            Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        if(verifyPassword(dataSnapshot.getValue().toString(),password)){

                            // Setting last login date
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            String CurrentTime = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+", "+c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1) +"-"+c.get(Calendar.YEAR);
                            Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("lastLogin").setValue(CurrentTime);

                            /*Set using version */
                            Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("version").setValue(BuildConfig.VERSION_CODE);

                            if (StoreInfo){
                                new LoginManager().SaveCredentials(ctx,name,password,institute,Class);
                            }
                            latestli.onLoginResult(true, LOGIN_SUCCEEDED);
                        }else{
                            latestli.onLoginResult(false, REASON_FAIL_PASSWORD_CHECK);
                        }

                    }else{
                        // Setting last login date
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        String CurrentTime = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+", "+c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1) +"-"+c.get(Calendar.YEAR);
                        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("lastLogin").setValue(CurrentTime);

                        /*Set using version */
                        Container.getInstance().databaseManager.getDatabase().child("Institutes").child(institute).child(Class).child("users").child(name).child("version").setValue(BuildConfig.VERSION_CODE);

                        if (StoreInfo){
                            new LoginManager().SaveCredentials(ctx,name,null,institute,Class);
                        }

                        latestli.onLoginResult(true, LOGIN_SUCCEEDED);}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        }
    }

    // First step to login
    @Override
    public void isBanned(boolean isbanned) {

        if (isbanned){
            // Banned! Notify via LI to update UI
            latestli.onLoginResult(false,REASON_BANNED);
            latestli.onBannedResult(BanExpiry,Banreason);
        }else{

            // Not banned, proceed
            userExist(UserManager.this.institute,UserManager.this.Class,UserManager.this.name,UserManager.this);
        }
    }


    @Override
    public Context getContext() {
        return ctx;
    }
}
