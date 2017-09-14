package utenti.diario.activities.home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import utenti.diario.R;
import utenti.diario.activities.administration.AdminAreaMain;
import utenti.diario.activities.home.homework.HomeWorkSetActivity;
import utenti.diario.activities.login.Login;
import utenti.diario.inizio;
import utenti.diario.regole.permissions.PermissionsInterface;
import utenti.diario.regole.permissions.PermissionsManager;
import utenti.diario.utilities.database.cards.CardsAdapter;
import utenti.diario.utilities.database.compiti.CompitiInterface;
import utenti.diario.utilities.database.compiti.CompitiManager;
import utenti.diario.utilities.orario.OrarioInterface;
import utenti.diario.utilities.orario.OrarioManager;
import utenti.diario.utilities.usermanagement.LoginManager;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PermissionsInterface, CompitiInterface, DatePickerDialog.OnDateSetListener, OrarioInterface {


    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Save date and display again on screen rotation/other activity kills
        if (lastCalendar != null) {
            outState.putString("date", String.valueOf(lastCalendar.getTimeInMillis()));
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {

        // Restore states
        String data = savedState.getString("date", "no");
        if (!Objects.equals(data, "no")) {
            Calendar t = Calendar.getInstance();
            t.setTimeInMillis(Long.parseLong(data));
            callCompiti(t.get(Calendar.YEAR), t.get(Calendar.MONTH), t.get(Calendar.DAY_OF_MONTH));
        }


        super.onRestoreInstanceState(savedState);
    }


    PermissionsManager pm = new PermissionsManager();

    void UpdateDrawerMenu() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.action_admin_area).setVisible(pm.hasPermission("admin.access_area"));
            menu.findItem(R.id.action_write).setVisible(pm.hasPermission("user.write"));
            menu.findItem(R.id.action_read).setVisible(pm.hasPermission("user.read"));

            menu.findItem(R.id.action_write).setEnabled(new OrarioManager().isOrarioAvailable());
            navigationView.setNavigationItemSelectedListener(this);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.accent_background);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Home.this.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(this.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Color statusbar
        setStatusBarGradiant(this);

        setContentView(R.layout.activity_home);


        // Set up custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.homebar);
        setSupportActionBar(toolbar);
        toolbar.setBackground(this.getResources().getDrawable(R.drawable.accent_background));

        // Set up navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set up drawer item click & set textview texts with info
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.navTitle)).setText(new LoginManager().getName(this));
        ((TextView) header.findViewById(R.id.navinstitute)).setText(new LoginManager().getInstitute(this));
        ((TextView) header.findViewById(R.id.navclass)).setText(new LoginManager().getClass(this));

        pm.ParsePermissions(this);

        // Get orario from database
        new OrarioManager().DownloadOrario(this, this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_read:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                if (lastCalendar != null) {
                    c = lastCalendar;
                }

                // Call dialog with calendar to get right day
                // Then in callback request homework
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this, Home.this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;

            case R.id.action_write:
                startActivity(new Intent(this, HomeWorkSetActivity.class));
                break;

            case R.id.action_admin_area:
                startActivity(new Intent(this, AdminAreaMain.class));
                break;

            case R.id.action_logout:
                new LoginManager().RemoveAutoLogin(this);

                startActivity(new Intent(this, inizio.class));
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Used by 2 interfaces
    @Override
    public Context getContext() {
        return this;
    }

    CardsAdapter ca = new CardsAdapter(this);

    @Override
    public void onCompitiGot(ArrayList<String> Materie, ArrayList<String> Compiti, ArrayList<String> Authors, ArrayList<String> immagini, ArrayList<String> immaginiID) {

        pd.dismiss();

        ((ListView) findViewById(R.id.lista_cards)).setVisibility(View.VISIBLE);
        ca.setCards(Materie, Compiti, lastCalendar);
        ((ListView) findViewById(R.id.lista_cards)).setAdapter(ca);
        ca.notifyDataSetChanged();

        // Set images list or hide
        if (immagini.size() != 0) {

        } else {
            findViewById(R.id.lista_immagini).setVisibility(View.GONE);
        }
        // Set authors colored list or hide
        if (Authors.size() != 0) {
            TextView authorsText = (TextView) findViewById(R.id.textview_autori);
            authorsText.setText(getString(R.string.modified_by) + ": ");
            for (int a = 0; a < Authors.size(); a++) {
                AppendColoredAuthorsInTextView(authorsText, Authors.get(a));
            }
        }

    }

    @Override
    public void onCompitiNotGot() {
        pd.dismiss();

        ((TextView) findViewById(R.id.textview_data)).setText(((TextView) findViewById(R.id.textview_data)).getText() + "\n" + getString(R.string.nothing_for_today));
        ca.clearCards();
    }

    @Override
    public void onPermissionsParsed() {
        UpdateDrawerMenu();
    }

    Calendar lastCalendar = null;

    ProgressDialog pd;

    // User has picked a date
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // call waiting dialog
        pd = ProgressDialog.show(Home.this, "", getString(R.string.please_wait), true);
        callCompiti(year, month, day);
    }

    void callCompiti(int year, int month, int day) {

        lastCalendar = Calendar.getInstance();
        lastCalendar.set(Calendar.YEAR, year);
        lastCalendar.set(Calendar.MONTH, month);
        lastCalendar.set(Calendar.DAY_OF_MONTH, day);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        // Set textview with date
        if (c.get(Calendar.YEAR) != lastCalendar.get(Calendar.YEAR)) {
            ((TextView) findViewById(R.id.textview_data)).setText(getString(R.string.watching) + ": " + ((lastCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (lastCalendar.get(Calendar.DAY_OF_MONTH) + 1) + "/" + lastCalendar.get(Calendar.YEAR))));

        } else {
            ((TextView) findViewById(R.id.textview_data)).setText(getString(R.string.watching) + ": " + ((lastCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (lastCalendar.get(Calendar.MONTH) + 1))));

        }

        new CompitiManager().getCompiti(day + "-" + (month + 1) + "-" + year, this);

    }

    // Notify orario is downloaded to enable WRITE section
    @Override
    public void onOrarioDownloaded() {
        UpdateDrawerMenu();
    }


    void AppendColoredAuthorsInTextView(TextView tv, String author) {

        Spannable word = new SpannableString(author);

        word.setSpan(new ForegroundColorSpan(randomColor()), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.append(word);
        tv.append(" ");

    }

    int randomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256 - 150) + 150, rnd.nextInt(256 - 150) + 150, rnd.nextInt(256 - 150) + 150);
    }
}
