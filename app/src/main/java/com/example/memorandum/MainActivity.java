package com.example.memorandum;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.memorandum.adapter.AdapterRemember;
import com.example.memorandum.common.MyDbHelper;
import com.example.memorandum.enity.Remember;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button buttonAdd;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
        btnAddClick();
        recyDisplay();
    }

    private void recyDisplay() {
        List<Remember> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from tb_remember",null);
        while (cursor.moveToNext()){
            String myTitle = cursor.getString(cursor.getColumnIndex("title"));
            String myContent = cursor.getString(cursor.getColumnIndex("content"));
            String myImgPath = cursor.getString(cursor.getColumnIndex("imgPath"));
            String myTime = cursor.getString(cursor.getColumnIndex("mtime"));
            Remember remember = new Remember(myTitle,myContent,myImgPath,myTime);
            list.add(remember);
        }
        cursor.close();
        AdapterRemember adapterRemember = new AdapterRemember(MainActivity.this,list);
        StaggeredGridLayoutManager st = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(st);
        recyclerView.setAdapter(adapterRemember);
    }

    private void btnAddClick() {
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddContentActivity.class));
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_viewContent);
        buttonAdd = findViewById(R.id.button_add);
        myDbHelper = new MyDbHelper(MainActivity.this);
        db = myDbHelper.getWritableDatabase();
    }
}