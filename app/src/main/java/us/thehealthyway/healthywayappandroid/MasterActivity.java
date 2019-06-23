package us.thehealthyway.healthywayappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static us.thehealthyway.healthywayappandroid.AppData.DEBUG;
import static us.thehealthyway.healthywayappandroid.AppData.TAG;

public class MasterActivity extends AppCompatActivity
    implements  TabJournal.OnFragmentInteractionListener,
                TabWeightChart.OnFragmentInteractionListener,
                TabSocialMedia.OnFragmentInteractionListener,
                TabGroceryList.OnFragmentInteractionListener,
                TabDailyTotals.OnFragmentInteractionListener,
                TabSettings.OnFragmentInteractionListener,
                TabChangePassword.OnFragmentInteractionListener


{
    ArrayList<Fragment> fragmentChain = new ArrayList<>();
    private Fragment oldFragment = new Fragment();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.journal_selection:
                    fragment = new TabJournal();
                    loadFragment(fragment);
                    return true;
                case R.id.weight_chart_selection:
                    fragment = new TabWeightChart();
                    loadFragment(fragment);
                    return true;
                case R.id.social_media_selection:
                    fragment = new TabSocialMedia();
                    loadFragment(fragment);
                    return true;
                case R.id.grocery_list_selection:
                    fragment = new TabGroceryList();
                    loadFragment(fragment);
                    return true;
                case R.id.daily_totals_selection:
                    if (fragmentChain.size() > 0) {
                        fragmentChain.clear();
                    }
                    // load fragment chain (tab and sub-tabs for Daily Totals
                    fragment = new TabDailyTotals();
                    fragmentChain.add(fragment);
                    loadFragment(fragment);
                    oldFragment = fragment;
                    fragment = new TabSettings();
                    fragmentChain.add(fragment);
                    fragment = new TabChangePassword();
                    fragmentChain.add(fragment);
                    return true;
                default:
                    Log.e(AppData.TAG, "onNavigationItemSelected: bad fragment id");
                    return true; // for now just return. Determine how to handle situation

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new TabJournal());

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof TabJournal) {
            TabJournal tabJournalFragment = (TabJournal) fragment;
            tabJournalFragment.setOnFragmentInteractionListener(this);
        }
    }



    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onFragmentInteraction(HW_Enumerations.TabNames tabIndex, HW_Enumerations.SubTabNames subTabIndex) {

        if (DEBUG) Log.d(TAG, "onFragmentInteraction: ");
        FragmentTransaction ft;
        Fragment newFragment = new Fragment();
        switch (tabIndex) {
            case TAB_JOURNAL:
            case TAB_WEIGHT_GRAPH:
            case TAB_SOCIAL_MEDIA:
            case TAB_GROCERY_LIST:
                return;
            case TAB_DAILY_TOTALS:
                switch (subTabIndex) {
                    case SUB_TAB_LEVEL_0:
                        newFragment = fragmentChain.get(subTabIndex.ordinal());
                        break;
                    case SUB_TAB_LEVEL_1:
                        newFragment = fragmentChain.get(subTabIndex.ordinal());
                        break;
                    case SUB_TAB_LEVEL_2:
                        newFragment = fragmentChain.get(subTabIndex.ordinal());
                        break;
                    default:
                        Log.e(TAG, "onFragmentInteraction: MasterActivity, bad sub-tab index" );
                        return;
                }
                break;
            case TAB_EXIT:
                signoffClient();
                break;
        }
        loadFragment(newFragment);
        ft = getSupportFragmentManager().beginTransaction();
        ft.hide(oldFragment);
        ft.show(newFragment);
        oldFragment= newFragment;
        ft.commit();
    }



    void signoffClient(){
        // add Firebase signoff code
        if (DEBUG) {
            Log.d(TAG, "signoffStaff: add signoff code here");
        }
        // route to login view
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_MASTER.getName(),
                HealthyWayAppActivities.LOGIN_ACTIVITY);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }



    public  static Intent makeIntent(Context context){
        return new Intent(context, MasterActivity.class);
    }


}
