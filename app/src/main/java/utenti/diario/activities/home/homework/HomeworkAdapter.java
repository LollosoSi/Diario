package utenti.diario.activities.home.homework;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import utenti.diario.R;
import utenti.diario.container.Container;
import utenti.diario.regole.SpecialSymbols;
import utenti.diario.utilities.orario.OrarioManager;
import utenti.diario.utilities.usermanagement.LoginManager;

/**
 * Created by SosiForWork on 02/09/2017.
 */

public class HomeworkAdapter extends BaseAdapter {

    ArrayList<Assignment> assignments = new ArrayList<>();
    ArrayList<String> materie = new ArrayList<>();

    ArrayList<String> autori = new ArrayList<>();

    Context ctx;
    int day;

    public void setup(Context ctx, ArrayList<String> Materie, ArrayList<String> Compiti, ArrayList<String> autori, int Day_Of_Week_int) {
        assignments = new ArrayList<>();
        day = Day_Of_Week_int;
        this.ctx = ctx;
        materie = Materie;
        this.autori = autori;
        for (int i = 0; i < Materie.size(); i++) {
            Assignment a = new Assignment();
            a.Materia = Materie.get(i);
            a.Contenuto = Compiti.get(i);
            assignments.add(a);
        }
        Assignment b = new Assignment();
        b.isHolder = true;
        assignments.add(b);

    }


    @Override
    public int getCount() {
        return assignments.size();
    }

    @Override
    public Object getItem(int i) {
        return assignments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).hashCode();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_homework_assigment, null);


       /* view.findViewById(R.id.button_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(((EditText) view.findViewById(R.id.ContentEditText)).getText().toString(), "")){
                    assignments.remove(getItem(i));
                    notifyDataSetChanged();
                }else{

                }
            }
        });*/

        if (i == getCount() - 1) {
            if (!((Assignment) getItem(i)).isHolder) {
                Assignment a = new Assignment();
                a.isHolder = true;
                assignments.add(a);
                notifyDataSetChanged();
            }
        }

        if (!((Assignment) getItem(i)).isHolder) {
            ((TextView) view.findViewById(R.id.textView_Materia)).setText(assignments.get(i).Materia);
            ((EditText) view.findViewById(R.id.ContentEditText)).setText(assignments.get(i).Contenuto);
            final boolean[] called = {false};
            ((EditText) view.findViewById(R.id.ContentEditText)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!called[0]) {
                        //((EditText) assignments.get(i).v.findViewById(R.id.ContentEditText)).setText(((EditText) assignments.get(i).v.findViewById(R.id.ContentEditText)).getText().toString().replace(SpecialSymbols.divider_key, "").replace(SpecialSymbols.space_key, ""));


                    } else {
                        called[0] = !called[0];
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    assignments.get(i).Contenuto = ((EditText) assignments.get(i).v.findViewById(R.id.ContentEditText)).getText().toString();
                }
            });

            view.findViewById(R.id.spinner_Materie).setVisibility(View.GONE);
            view.findViewById(R.id.textView_Materia).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ContentEditText).setVisibility(View.VISIBLE);
        } else {
            OrarioManager om = new OrarioManager();
            if (om.isOrarioAvailable()) {
                ArrayList<String> orario = new ArrayList<>();

                orario.add(ctx.getString(R.string.select_materia));

                orario.addAll(om.getGiorno(day));
                orario.add(ctx.getString(R.string.avviso));

                exactorario = new ArrayList<>();
                exactorario.addAll(om.getGiorno(day));
                exactorario.add(ctx.getString(R.string.avviso));

                for (int o = 0; o < materie.size(); o++) {
                    exactorario.remove(materie.get(o));
                    orario.remove(materie.get(o));
                }


                ArrayAdapter<String> ar = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1);
                ar.addAll(orario);
                ((Spinner) view.findViewById(R.id.spinner_Materie)).setAdapter(ar);

                final int[] check = {0};
                ((Spinner) view.findViewById(R.id.spinner_Materie)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int selection, long l) {
                        if (check[0] > 0) {
                            if (selection != 0) {
                                ((Assignment) getItem(i)).isHolder = false;
                                ((Assignment) getItem(i)).Contenuto = "";
                                ((Assignment) getItem(i)).Materia = exactorario.get(selection - 1);
                                materie.add(exactorario.get(selection - 1));
                                notifyDataSetChanged();
                            }
                        } else {
                            check[0] += 1;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                view.findViewById(R.id.spinner_Materie).setVisibility(View.VISIBLE);
                view.findViewById(R.id.textView_Materia).setVisibility(View.GONE);
                view.findViewById(R.id.ContentEditText).setVisibility(View.GONE);

            }
        }
        assignments.get(i).v = view;
        return assignments.get(i).v;
    }

    ArrayList<String> exactorario = new ArrayList<>();


    ArrayList<String> saved_compiti = null;
    ArrayList<String> saved_auth = null;

    public void saveAndClear() {
        LoginManager lm = new LoginManager();

        // Reset
        saved_compiti = null;
        saved_auth = null;

        // Update authors list
        if (autori.contains(lm.getName(ctx))) {
            autori.remove(lm.getName(ctx));
            autori.add(lm.getName(ctx));
        }

        // Remove holder (empty) object in list
        // If not done this causes exception in for loop
        if (assignments.get(assignments.size() - 1).isHolder) {
            assignments.remove(assignments.size() - 1);
        }

        // Assignment array to upload. Includes Subject + "Divider key" + Content
        ArrayList<String> compitiUpload = new ArrayList<>();

        // Process assignments
        for (int i = 0; i < assignments.size(); i++) {

            if (Objects.equals(assignments.get(i).Contenuto, "")) {
                // Do not add empty assignments

            } else {
                assignments.get(i).Autori.add(lm.getName(ctx));
                String auth = "";
                for (int o = 0; o < assignments.get(i).Autori.size(); o++) {
                    auth = auth + assignments.get(i).Autori.get(o);
                    if (o != assignments.get(i).Autori.size() - 1) {
                        auth = auth + SpecialSymbols.space_key;
                    }
                }
                compitiUpload.add(assignments.get(i).Materia + SpecialSymbols.divider_key + assignments.get(i).Contenuto.replace(SpecialSymbols.divider_key, "").replace(SpecialSymbols.space_key, "").replace(" ", SpecialSymbols.space_key));
            }

        }


        // Save processed arrays
        saved_compiti = compitiUpload;
        saved_auth = autori;
    }

    // Returns "compitiUpload" in saveAndClear()
    public ArrayList<String> getSavedArray() {
        return saved_compiti;
    }

    // Returns latest modifications authors made in saveAndClear()
    public ArrayList<String> getSavedAuthArray() {
        return saved_auth;
    }
}
