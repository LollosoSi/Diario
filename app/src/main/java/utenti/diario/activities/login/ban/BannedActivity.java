package utenti.diario.activities.login.ban;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

import utenti.diario.R;

public class BannedActivity extends Activity {

    /**               Ban Codes
     *         no_reason == You're banned (Simple)
     *     bad_behaviour == You're banned for having a bad behaviour
     *     multi_account == You're banned for having more than 1 account in the same class
     *   account_sharing == You're banned for sharing your account
     *  account_stealing == You're banned for using other's account
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bannedlayout);

        if(getActionBar()!=null){
            getActionBar().hide();
        }

        // Get reason and expiry from intent, if not passed, close app
        if(getIntent()!=null){
            String Reason = getIntent().getStringExtra("reason");
            long expiry = Long.parseLong(getIntent().getStringExtra("expiry"));

            // Create date string
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(expiry);
            String Formatteddate = +c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1) +"-"+c.get(Calendar.YEAR)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);

            // Set date string in ban expiry
            ((TextView)findViewById(R.id.banexpiry)).setText(getString(R.string.ban_expires_in)+": \n"+Formatteddate);

            // Get ban reason and convert in local language
            // If not found show original text NOTE: Intended behaviour!
            switch (Reason){
                case "no_reason":
                    setTextReason(getString(R.string.ban_no_reason));
                    break;
                case "bad_behaviour":
                    setTextReason(getString(R.string.ban_bad_behaviour));
                    break;
                case "account_sharing":
                    setTextReason(getString(R.string.ban_account_sharing));
                    break;
                case "account_stealing":
                    setTextReason(getString(R.string.ban_account_stealing));
                    break;
                case "multi_account":
                    setTextReason(getString(R.string.ban_multi_account));
                    break;

                default:
                    setTextReason(Reason);
                    break;
            }

        }else{
            // Close app, no intent
            System.exit(0);
        }
    }

    void setTextReason(String reason){
        ((TextView)findViewById(R.id.bannedtexteason)).setText(reason);
    }
}
