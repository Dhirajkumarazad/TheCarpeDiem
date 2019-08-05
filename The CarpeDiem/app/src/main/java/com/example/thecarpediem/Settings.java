package com.example.thecarpediem;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

public class Settings extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mauthStateListener;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.home:
                            Intent i=new Intent(Settings.this,MainActivity.class);
                            startActivity(i);
                            break;

                        case R.id.writingact:
                            Intent intent=new Intent(Settings.this,WritingActivity.class);
                            startActivity(intent);
                            break;

                        case R.id.favouritecollec:
                            Intent in=new Intent(Settings.this,Favourites.class);
                            startActivity(in);
                            break;

                        case R.id.notification:
                            Intent inte=new Intent(Settings.this,Notification.class);
                            startActivity(inte);
                            break;
                    }
                    return true;
                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupFirebaseListener();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);
    }

    private void setupFirebaseListener() {

        mauthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //signed-in
                    Log.d("AccountManager", "onAuthStateChanged: signed_in: " + user.getUid());

                } else {
                    //signout
                    Intent i = new Intent(Settings.this, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mauthStateListener);
    }
    public void about(View v)
    {
        Intent  i =new Intent(Settings.this,About.class);
        startActivity(i);
    }
    public void termsandcondn(View v)
    {
        Intent  i =new Intent(Settings.this,About.class);
        startActivity(i);
    }
    public void privacypolicy(View v)
    {
        Intent  i =new Intent(Settings.this,About.class);
        startActivity(i);
    }

    public void rateapp(View v)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "The CarpeDiem");
            intent.putExtra(Intent.EXTRA_TEXT, "Install *The CarpeDiem* App now! https://github.com/sarthaksarm/TheCarpeDiem"); //give article's or app's link here
            startActivity(Intent.createChooser(intent, "Share!"));
        }
        catch (Exception e)
        {
            Toast.makeText(Settings.this,"Necessary packages, not available on your device! " +
                    "Kindly contact us directly at \"reachtco@gmail.com\"",Toast.LENGTH_LONG).show();
        }

    }

    public void opengmail(View v)
    {
        try {
            Toast.makeText(Settings.this,"Write to us directly!",Toast.LENGTH_SHORT).show();
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "reachtco@gmail.com", null));

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding The CarpeDiem Android app");
            startActivity(Intent.createChooser(emailIntent, null));
        }

        catch (Exception e)
        {
            Toast.makeText(Settings.this,"Necessary packages, not available on your device! " +
                    "Kindly contact us directly at \"reachtco@gmail.com\"",Toast.LENGTH_LONG).show();
        }
    }

    public void campusambass(View v)
    {
//        Intent  i =new Intent(Settings.this,About.class);
//        startActivity(i);
    }
    public void signout(View v)
    {
        FirebaseAuth.getInstance().signOut();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mauthStateListener!=null)
            FirebaseAuth.getInstance().removeAuthStateListener(mauthStateListener);
    }
}
