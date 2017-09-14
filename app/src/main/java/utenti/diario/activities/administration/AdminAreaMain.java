package utenti.diario.activities.administration;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import utenti.diario.R;
import utenti.diario.regole.permissions.PermissionsManager;

public class AdminAreaMain extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_area_main);

        setupActionBar();
    }

    void setupActionBar() {

        ActionBar actionbar = getSupportActionBar();

        // Title & subtitle
        actionbar.setTitle(getString(R.string.admin_area));
        actionbar.setSubtitle(getString(R.string.tools));

        // Back button
        actionbar.setHomeButtonEnabled(true);

        // Show/hide layout based on permission requirements
        showHideContainer(findViewById(R.id.container_user_management), "admin.user_management");
        showHideContainer(findViewById(R.id.container_institutes_management), "admin.institutes_management");

    }

    void showHideContainer(View v, String Permission) {
        if (new PermissionsManager().hasPermission(Permission)) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    // Back button handling
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
