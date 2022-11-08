package com.example.memorandum;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.memorandum.common.MyDbHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static android.os.Environment.getExternalStorageDirectory;

public class AddContentActivity extends AppCompatActivity {
    public static final int TAKE_Camera = 0;
    public static final int TAKE_PHOTO = 1;
    private EditText editTitle;
    private EditText editContent;
    private Button buttonCamera, buttonPhoto, buttonSave;
    private String tmpPath;
    private Uri imgUri;
    private ImageView imageView;
    private File file;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        initView();
        btnOnClick();
    }

    private void initView() {
        editTitle = findViewById(R.id.edit_title);
        editContent = findViewById(R.id.edit_content);
        buttonCamera = findViewById(R.id.button_camera);
        buttonPhoto = findViewById(R.id.button_photo);
        buttonSave = findViewById(R.id.button_save);
        imageView = findViewById(R.id.image_pre_img);
        myDbHelper = new MyDbHelper(AddContentActivity.this);
        db = myDbHelper.getWritableDatabase();
    }


    private void btnOnClick() {
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
                tmpPath =getExternalCacheDir()+ now + ".jpg";
                file = new File(tmpPath);
                try {
                    if (file.exists()) {          //  检查与File对象相连接的文件和目录是否存在于磁盘中
                        file.delete();           //  删除与File对象相连接的文件和目录
                    }
                    file.createNewFile();        //  如果与File对象相连接的文件不存在，则创建一个空文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {       //  如果运行设备的系统版本高于 Android7.0
                    /**
                     *          将File对象转换成一个封装过的Uri对象
                     *          第一个参数：  要求传入Context参数
                     *          第二个参数：  可以是任意唯一的字符串
                     *          第三个参数：  我们刚刚创建的File对象
                     */
                    imgUri = FileProvider.getUriForFile(AddContentActivity.this, "com.example.memorandum.AddContentActivity", file);
                } else {                  //  如果运行设备的系统版本低于 Android7.0
                    //  将File对象转换成Uri对象，这个Uri对象表示着 str + ".jpg" 这张图片的本地真实路径
                    imgUri = Uri.fromFile(file);
                }
                Log.e("TAG",""+imgUri);
                System.out.println(""+imgUri);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //  指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                /**
                 *      在通过startActivityForResult()，来启动活动，因此拍完照后会有结果返回到 onActivityResult()方法中
                 */
                startActivityForResult(intent, TAKE_Camera);
            }
        });
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(Intent.ACTION_PICK);
                //指定获取的是图片
                intent.setType("image/*");
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put("title",editTitle.getText().toString());
                contentValues.put("content",editContent.getText().toString());
                contentValues.put("imgPath",tmpPath);
                contentValues.put("mtime",time);
               //插入
                db.insert("tb_remember",null,contentValues);
                Toast.makeText(AddContentActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
               //页面跳转
                Intent intent = new Intent(AddContentActivity.this,MainActivity.class);
                startActivity(intent);
                //关闭页面
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_Camera:
                if (resultCode == RESULT_OK) {       //  当拍照成功后，会返回一个返回码，这个值为 -1 — RESULT_OK
                    try {
                        //  根据Uri找到这张照片的资源位置，将它解析成Bitmap对象，然后将把它设置到imageView中显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_PHOTO:
                Uri url = data.getData();
                if(url !=null){
                    imageView.setImageURI(url);
                }
                break;
        }
    }
}