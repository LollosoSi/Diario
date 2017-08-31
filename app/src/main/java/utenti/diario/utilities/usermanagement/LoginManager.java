package utenti.diario.utilities.usermanagement;

import android.content.Context;

import java.util.Objects;

import utenti.diario.utilities.exceptions.GlobalContextNotDeclared;

/** Created by LollosoSi */

public class LoginManager {

    /*     Keep in mind
        In sharedpreferences there are the exact tags:

        File: "credentials"

        "autologin"  - Boolean
          "name"     - String
        "password"   - String
         "class"     - String
        "institute"  - String
     */

    Context GlobalContext = null;

    public boolean isAutoLoginEnabled(Context ctx){
        if(ctx==null) {ctx = getGlobalContext();}
        Boolean areCredentialsStored = (!Objects.equals(ctx.getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("name", ""), "") &&
                                        !Objects.equals(ctx.getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("class", ""), "") &&
                                        !Objects.equals(ctx.getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("institute", ""), "")); // Password is not required

        return areCredentialsStored && ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).getBoolean("autologin",false);
    }

    // Not encrypted for now, this is a simple getter
    public String getName(Context ctx){
        if(ctx==null) {ctx = getGlobalContext();}
        return ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).getString("name","");
    }
    // Not encrypted for now, this is a simple getter
    public String getPassword(Context ctx){
        if(ctx==null) {ctx = getGlobalContext();}
        return ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).getString("password","");
    }

    public String getClass(Context ctx){
        if(ctx==null) {ctx = getGlobalContext();}
        return ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).getString("class","");
    }
    public String getInstitute(Context ctx){
        if(ctx==null) {ctx = getGlobalContext();}
        return ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).getString("institute","");
    }

    public void RemoveAutoLogin(Context ctx) {
        if(ctx==null) {ctx = getGlobalContext();}
        ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).edit().clear().commit();
    }

    public void SaveCredentials(Context ctx, String name, String password, String institute, String Class){
        if(ctx==null) {ctx = getGlobalContext();}

        ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).edit().putString("name",name).commit();
        if(password!=null) {
            ctx.getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().putString("password",password).commit();
        }
        ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).edit().putString("class", Class).commit();
        ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).edit().putString("institute", institute).commit();
        ctx.getSharedPreferences("credentials",Context.MODE_PRIVATE).edit().putBoolean("autologin",true).commit();
    }

    public void StoreContext(Context ctx){
    GlobalContext=ctx;
    }

    Context getGlobalContext(){
        if (GlobalContext==null){try {throw new GlobalContextNotDeclared();} catch (GlobalContextNotDeclared globalContextNotDeclared) {globalContextNotDeclared.printStackTrace();}}
        return GlobalContext;
    }
}
