package com.unal.davsanba.biciparche.Views;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.unal.davsanba.biciparche.Data.ActRefs;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.UserOperationsManager;

/**
 * Created by davsa on 16/11/2016.
 */
public class PreferencesActivity extends PreferenceActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
    {

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference terms = findPreference(getString(R.string.menu_terms_and_conditions));
            terms.setOnPreferenceClickListener(this);
            Preference privac = findPreference(getString(R.string.menu_privacy));
            privac.setOnPreferenceClickListener(this);
            Preference edit = findPreference(getString(R.string.menu_edoit_profile));
            edit.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent i;
            switch (preference.getKey()){
                case "Terminos y condiciones":
                    i = new Intent(preference.getContext(),ReaderActivity.class);
                    i.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_TERMS);
                    startActivity(i);
                    break;
                case "Politica de Privacidad":
                    i = new Intent(preference.getContext(),ReaderActivity.class);
                    i.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_PRIVACY);
                    startActivity(i);
                    break;
                case "Editar mi Perfil":
                    UserOperationsManager uom = new UserOperationsManager(preference.getContext());
                    uom.EditUserData();
                    break;
            }
        return false;
        }
    }

}

