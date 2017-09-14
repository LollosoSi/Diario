package utenti.diario.utilities.database.cards;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import utenti.diario.R;
import utenti.diario.utilities.orario.OrarioManager;

/**
 * Created by SosiForWork on 01/09/2017.
 */

public class CardsAdapter extends BaseAdapter {

    ArrayList<CardItem> carditems = new ArrayList<>();
    Calendar thisday;

    public CardsAdapter(Context ctx) {

        this.ctx = ctx;


    }

    public void setCards(ArrayList<String> Materie, ArrayList<String> Compiti, Calendar selectedDay) {
        thisday = selectedDay;
        clearCards();

        // Todo: Setup cards with card layout
        for (int i = 0; i < Materie.size(); i++) {
            CardItem ci = new CardItem();
            ci.Materia = Materie.get(i);
            ci.Compiti = Compiti.get(i);
            carditems.add(ci);
        }

    }

    public void clearCards() {
        carditems = new ArrayList<>();
        notifyDataSetChanged();
    }

    Context ctx;


    @Override
    public int getCount() {
        return carditems.size();
    }

    @Override
    public Object getItem(int i) {
        return carditems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).hashCode();
    }


    int Activecolor = Color.WHITE;
    int inactivecolor = Color.parseColor("#a0a0a0");
    int AdviceColor = Color.RED;

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.layout_card_assignment, null);

        final IDidItManager idi = new IDidItManager(ctx);
        final CardItem ci = carditems.get(i);
        OrarioManager om = new OrarioManager();

        ((TextView) view.findViewById(R.id.materiaView)).setText(ci.Materia);
        ((TextView) view.findViewById(R.id.compitiView)).setText(ci.Compiti);


        if (idi.getValue(ci.Data, ci.Materia, ci.Compiti)) {
            ((TextView) view.findViewById(R.id.materiaView)).setTextColor(inactivecolor);
            ((TextView) view.findViewById(R.id.compitiView)).setTextColor(inactivecolor);
            ((TextView) view.findViewById(R.id.textview_ora)).setTextColor(inactivecolor);
        } else {
            ((TextView) view.findViewById(R.id.materiaView)).setTextColor(Activecolor);
            ((TextView) view.findViewById(R.id.compitiView)).setTextColor(Activecolor);
            ((TextView) view.findViewById(R.id.textview_ora)).setTextColor(Activecolor);
        }

        // If isn't an advice
        if (!Objects.equals(ci.Materia, ctx.getString(R.string.avviso))) {

            // Restore state if present
            ((CheckBox) view.findViewById(R.id.checkbox_made_it)).setChecked(idi.getValue(ci.Data, ci.Materia, ci.Compiti));
            ((CheckBox) view.findViewById(R.id.checkbox_made_it)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // Haptic feedback
                    compoundButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                    // Save state
                    idi.ChangeValue(ci.Data, ci.Materia, ci.Compiti, b);
                    notifyDataSetChanged();
                }
            });

            // Set orario in card
            if (om.isOrarioAvailable()) {
                ((TextView) view.findViewById(R.id.textview_ora)).setText(om.getHourInDay(thisday.get(Calendar.DAY_OF_WEEK) - 1, ci.Materia) + "° " + ctx.getString(R.string.hour));
            } else {
                ((TextView) view.findViewById(R.id.textview_ora)).setText("N/A");
            }

        } else {
            ((CheckBox) view.findViewById(R.id.checkbox_made_it)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.textview_ora)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.materiaView)).setTextColor(AdviceColor);
        }

        return view;
    }
}
