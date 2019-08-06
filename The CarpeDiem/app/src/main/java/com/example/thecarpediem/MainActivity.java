package com.example.thecarpediem;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPagerBest,viewPager1,viewPager2;
    Adapter adapter;
    AdapterTech adapter1;
    AdapterBest adapter2;
    List<model> models, models1,models2;
    Integer[] colors=null;
    Integer[] colors1=null;
    ImageView imgword, imglive;
    Integer[] colors2=null;
    ArgbEvaluator argbEvaluator=new ArgbEvaluator();
    private DrawerLayout drawer;
    private NavigationView nv;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    InternetConnection ic;
    ProgressBar progressBar;
    ProgressBar progressBarlive;
    ProgressBar progressBarword;

    private FirebaseAuth.AuthStateListener mauthStateListener;
    private String[] imgUrls={
            R.drawable.loadingimg+"",
            R.drawable.loadingimg+"",
            R.drawable.loadingimg+"",
            R.drawable.loadingimg+""
    };
    DatabaseReference refbestofday, reflive,refwordofday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_main);
        setupFirebaseListener();

        checkInternet();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        progressBar=findViewById(R.id.progressbar);
        progressBarword=findViewById(R.id.progressbarword);
        progressBarlive=findViewById(R.id.progressbarlive);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nv = (NavigationView)findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id) {
                    case R.id.writeup:
                        Intent i=new Intent(MainActivity.this,WritingActivity.class);
                        startActivity(i);
                        break;
                    case R.id.favourites:
                        Intent intent1=new Intent(MainActivity.this,Favourites.class);
                        startActivity(intent1);
                        break;
                    case R.id.setting:
                        Intent intent2=new Intent(MainActivity.this,Settings.class);
                        startActivity(intent2);
                        break;
                    case R.id.about:
                        Intent in=new Intent(MainActivity.this,About.class);
                        startActivity(in);
                        break;
                    case R.id.team:
                        Intent inte=new Intent(MainActivity.this,OurTeam.class);
                        startActivity(inte);
                        break;
                    case R.id.contact:
                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto", "reachtco@gmail.com", null));

                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding The CarpeDiem Android app");
                            startActivity(Intent.createChooser(emailIntent, null));
                        } catch (Exception e)
                        {
                            Toast.makeText(MainActivity.this,"Necessary packages, not available on your device! " +
                                    "Kindly contact us directly at \"reachtco@gmail.com\"",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.share:

                       try {
                           Intent intent = new Intent(Intent.ACTION_SEND);

                           intent.setType("text/plain");
                           intent.putExtra(Intent.EXTRA_SUBJECT, "The CarpeDiem");
                           intent.putExtra(Intent.EXTRA_TEXT, "Install *The CarpeDiem* App now! https://github.com/sarthaksarm/TheCarpeDiem"); //give article's or app's link here
                           startActivity(Intent.createChooser(intent, "Share!"));
                       }
                       catch (Exception e)
                       {
                           Toast.makeText(MainActivity.this,"Necessary packages, not available on your device! " +
                                   "Kindly contact us directly at \"reachtco@gmail.com\"",Toast.LENGTH_LONG).show();
                       }
                        break;
                    case R.id.exit:
                        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Are you sure want to exit?");
                        builder.setCancelable(false);

                        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Signing out",Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                finishAffinity();
                            }
                        });

                        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        });
                        AlertDialog alertdialog=builder.create();
                        alertdialog.show();

                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        BottomNavigationView bottomnav=findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(navListener);

        imgword=(ImageView)findViewById(R.id.wordimg);
        imglive=(ImageView)findViewById(R.id.liveimg);

        refbestofday= FirebaseDatabase.getInstance().getReference("bestofday").child("imgs");
        reflive=FirebaseDatabase.getInstance().getReference("live");
        refwordofday=FirebaseDatabase.getInstance().getReference("wordoftheday");

        loadliveimg(reflive);
        loadwordimg(refwordofday);

       // imgUrls=loadimgs(refbestofday, imgUrls);

        refbestofday.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot x:dataSnapshot.getChildren())
                {
                    if(i==4)
                    {
                        break;
                    }
                    imgUrls[i]=x.getValue().toString();
                    i=i+1;
                }
                ViewPagerAdapter adapterBest=new ViewPagerAdapter(MainActivity.this,imgUrls);
                progressBar.setVisibility(View.INVISIBLE);
                viewPagerBest.setAdapter(adapterBest);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        models1=new ArrayList<>();
        models1.add(new model(R.drawable.inspiration,"Inspiration","Grow Inspired"));
        models1.add(new model(R.drawable.love,"Love","A bliss feeling"));
        models1.add(new model(R.drawable.sad,"Sad","Life shows its ways"));
        models1.add(new model(R.drawable.science,"Science Fiction","Innovate with science"));

        adapter=new Adapter(models,this);
        adapter1=new AdapterTech(models1,this);
        adapter2=new AdapterBest(models2,this);

        viewPagerBest =findViewById(R.id.viewPagerbest);

        sliderDotspanel=findViewById(R.id.SliderDots);
        dotscount = 5;
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPagerBest.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager1=findViewById(R.id.viewPager1);
        viewPager1.setAdapter(adapter1);

        viewPager1.setPadding(50,0,50,0);

        Integer[] colors_temp1={
                getResources().getColor(R.color.Black),
                getResources().getColor(R.color.Black),
                getResources().getColor(R.color.Black),
                getResources().getColor(R.color.Black),
        };

        colors1=colors_temp1;
        viewPager1.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (i<(adapter1.getCount()-1)&&i<(colors1.length-1)){
                    viewPager1.setBackgroundColor(
                            (Integer)argbEvaluator.evaluate(
                                    v,
                                    colors1[i],
                                    colors1[i+1]
                            )
                    );
                }
                else {
                    viewPager1.setBackgroundColor(colors1[colors1.length-1]);
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    public void loadliveimg(DatabaseReference reflive)
    {
        reflive.child("img").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).placeholder(R.drawable.loadingimg).into(imglive);
                progressBarlive.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void loadwordimg(DatabaseReference refwordofday)
    {
        refwordofday.child("img").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).placeholder(R.drawable.loadingimg).into(imgword);
                progressBarword.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void checkInternet()
    {
        ConnectivityManager cm= (ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= cm.getActiveNetworkInfo();

        boolean isConnected = networkInfo!=null && networkInfo.isConnectedOrConnecting();
        if(!isConnected)
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("NO Internet! Check Internet connection and try again.");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Reload",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(MainActivity.this,MainActivity.class));
                        }
                    });

            builder1.setNegativeButton(
                    "Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this,"Leaving off!",Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

//    public String[] loadimgs(DatabaseReference refbestofday, final String[] imgUrls)
//    {
//        refbestofday.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int i=0;
//                for(DataSnapshot x:dataSnapshot.getChildren())
//                {
//                    if(i==4)
//                    {
//                        break;
//                    }
//                    imgUrls[i]=x.getValue().toString();
//                    i=i+1;
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        if(imgUrls[0].equals(""))
//        {
//
//            loadimgs(refbestofday,imgUrls);
//        }
//
//
//        return imgUrls;
//
//    }
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Are you sure want to Exit?");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Sure",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this,"Have a good day!",Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
       }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.writingact:
                            Intent i=new Intent(MainActivity.this,WritingActivity.class);
                            startActivity(i);
                            break;

                        case R.id.favouritecollec:
                            Intent in=new Intent(MainActivity.this,Favourites.class);
                            startActivity(in);
                            break;

                        case R.id.notification:
                            Intent inte=new Intent(MainActivity.this,Notification.class);
                            startActivity(inte);
                            break;
                    }
                    return true;
                }
            };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                Toast.makeText(getApplicationContext(),"Signing out",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                return true;

            case R.id.About:
                Intent in=new Intent(MainActivity.this,About.class);
                startActivity(in);
                return true;

            case R.id.Share:

                try {
                    Toast.makeText(MainActivity.this,"Spread it wider!",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "The CarpeDiem");
                    intent.putExtra(Intent.EXTRA_TEXT, "Install *The CarpeDiem* App now! https://github.com/sarthaksarm/TheCarpeDiem"); //give article's or app's link here
                    startActivity(Intent.createChooser(intent, "Share!"));
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Necessary packages, not available on your device! " +
                            "Kindly contact us directly at \"reachtco@gmail.com\"",Toast.LENGTH_LONG).show();
                }

                return true;

            case R.id.contactus:
                try {
                    Toast.makeText(MainActivity.this,"Write to us directly!",Toast.LENGTH_SHORT).show();
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "reachtco@gmail.com", null));

                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding The CarpeDiem Android app");
                    startActivity(Intent.createChooser(emailIntent, null));
                }

                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Necessary packages, not available on your device! " +
                            "Kindly contact us directly at \"reachtco@gmail.com\"",Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.exit:
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure want to exit?");
                builder.setCancelable(false);

                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Signing out",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        finishAffinity();
                    }
                });

                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                AlertDialog alertdialog=builder.create();
                alertdialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupFirebaseListener(){

        mauthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    //signed-in
                    Log.d("AccountManager","onAuthStateChanged: signed_in: "+user.getUid());

                }
                else{
                    //signout
                    Intent i=new Intent(MainActivity.this, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(mauthStateListener!=null)
            FirebaseAuth.getInstance().removeAuthStateListener(mauthStateListener);
    }
}
