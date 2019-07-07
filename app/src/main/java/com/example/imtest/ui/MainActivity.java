package com.example.imtest.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imtest.BaseApplication;
import com.example.imtest.R;
import com.example.imtest.net.IHttpRequest;
import com.example.imtest.net.RetrofitFactory;
import com.example.imtest.net.StandardResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public Toolbar toolbar;


    FragmentManager fragmentManager = getSupportFragmentManager();

    ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.add_friend:

                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.container_add_friend,null);
                        final EditText editText = view.findViewById(R.id.ed_add_friend);
                        new AlertDialog.Builder(MainActivity.this)
                                .setView(view)
                                .setTitle("Add new friend")
                                .setCancelable(false)
                                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Retrofit retrofit = RetrofitFactory.getInstance();
                                        IHttpRequest request = retrofit.create(IHttpRequest.class);
                                        Disposable d =
                                        request.addFriend(BaseApplication.getLoginUser(),editText.getText().toString())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<StandardResponse>() {
                                                    @Override
                                                    public void accept(final StandardResponse standardResponse) throws Exception {
                                                        Toast.makeText(MainActivity.this, standardResponse.message, Toast.LENGTH_SHORT).show();

                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(Throwable throwable) throws Exception {
                                                        Log.e(TAG, "accept: ",throwable);
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Cancel",null)
                                .show();
                        break;
                }
                return false;
            }
        });

        listFragment = ListFragment.newInstance("test");
        fragmentManager.beginTransaction().add(R.id.main_layout, listFragment,"parent").commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.function_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //transaction.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_right_out);

        Fragment parent = fragmentManager.findFragmentByTag("parent");
        if(parent!=null){
            transaction.show(parent);
        }

        Fragment child = fragmentManager.findFragmentByTag("child");
        if(child!=null) {
            toolbar.setTitle("IMTest");
            transaction.remove(child);
        }else{
            finish();
        }


        transaction.commit();


    }
}


