package com.bamboo.bullyalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bamboo.bullyalert.IntentServices.IntentServiceNotification;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.fragment.HistoryDetailFragment;
import com.bamboo.bullyalert.fragment.HistoryFragment;
import com.bamboo.bullyalert.fragment.NotificationDetailFragment;
import com.bamboo.bullyalert.fragment.NotificationFragment;
import com.bamboo.bullyalert.fragment.ProfileDetailFragment;
import com.bamboo.bullyalert.fragment.ProfileFragment;
import com.bamboo.bullyalert.fragment.AddUserFragment;
import com.bamboo.bullyalert.model.History;
import com.bamboo.bullyalert.model.Notification;
import com.bamboo.bullyalert.model.Profile;

import java.util.HashMap;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NotificationFragment.OnListFragmentInteractionListener,
        ProfileFragment.OnListFragmentInteractionListener,
        HistoryFragment.OnListFragmentInteractionListener
{

    AlarmManager mAlarmManager;
    Intent mNotificationIntent;
    PendingIntent mPendingNotificationIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.check_notification);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ViewGroup vGroup = (ViewGroup) navigationView.getHeaderView(0);
        TextView emailTextView = (TextView)vGroup.getChildAt(2);
        emailTextView.setText(UtilityVariables.USER_EMAIL);


        setAlarmNotification();
        Intent intent = getIntent();
        if (intent.hasExtra(UtilityVariables.INTENT_VARIABLE_NOTIFICATIONS))
        {
            HashMap<String,Notification> mNotifications;
            mNotifications = (HashMap<String,Notification>)intent.getSerializableExtra(UtilityVariables.INTENT_VARIABLE_NOTIFICATIONS);
            Fragment fragment = NotificationFragment.newInstance(mNotifications);
            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        else
        {
            Fragment fragment = NotificationFragment.newInstance(null);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }



    private void setAlarmNotification()
    {
        if(UtilityVariables.IS_ALARM_ON == false)
        {
            Log.i(UtilityVariables.tag, ": Inside setAlarm function. alarm is set to true now.");
            mNotificationIntent = new Intent(this, IntentServiceNotification.class);
            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mPendingNotificationIntent = PendingIntent.getService(this, 1, mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //Calendar calendar = Calendar.getInstance();
            //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getService(this, 1, notificationIntent,
                    //PendingIntent.FLAG_UPDATE_CURRENT));

            mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    UtilityVariables.ALARM_INTERVAL, mPendingNotificationIntent);
            //mAlarmManager.set(AlarmManager.RTC_WAKEUP,5000,mPendingNotificationIntent);

            UtilityVariables.IS_ALARM_ON = true;
        }
        else
        {
            Log.i(UtilityVariables.tag, ": Inside setAlarm function. alarm is already set");
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Fragment fragment = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_new_profile) {
            toolbar.setTitle(R.string.add_new_profile);
            fragment = AddUserFragment.newInstance(1);
        } else if (id == R.id.check_notifications) {
            toolbar.setTitle(R.string.check_notification);
            fragment = NotificationFragment.newInstance(null);
        } /*else if (id == R.id.settings) {
            toolbar.setTitle(R.string.settings);
            fragment = SettingFragment.newInstance(1);
        }*/ else if (id == R.id.monitoring_profiles) {
            toolbar.setTitle(R.string.monitoring_profiles);
            fragment = ProfileFragment.newInstance(1);
        } /*else if (id == R.id.history) {
            toolbar.setTitle(R.string.history);
            fragment = HistoryFragment.newInstance(1);
        }*/ else if (id == R.id.logout)
        {
            UtilityVariables.IS_ALARM_ON = false;
            //mPendingNotificationIntent = PendingIntent.getService(this, 1, mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if(this.mAlarmManager != null)
            {
                Log.i(UtilityVariables.tag,"alarm manager isnt null");
                this.mAlarmManager.cancel(mPendingNotificationIntent);
            }
            else
            {
                Log.i(UtilityVariables.tag,"alarm manager is null");
            }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        // Insert the fragment by replacing any existing fragment
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Notification item) {
        Fragment fragment = NotificationDetailFragment.newInstance(item);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(Profile monitoringUserProfile)
    {
        Log.i(UtilityVariables.tag,"hello here it is in the navigation activity: "+monitoringUserProfile.getmUserName());
        Fragment fragment = ProfileDetailFragment.newInstance(monitoringUserProfile.getmUserName());
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(History item) {
        Fragment fragment = HistoryDetailFragment.newInstance(item);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }





}
