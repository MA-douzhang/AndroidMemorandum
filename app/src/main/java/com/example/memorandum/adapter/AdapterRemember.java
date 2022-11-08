package com.example.memorandum.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.memorandum.R;
import com.example.memorandum.common.MyDbHelper;
import com.example.memorandum.enity.Remember;

import java.util.List;

public class AdapterRemember extends RecyclerView.Adapter<AdapterRemember.ViewHolder>{
    private Context context;
    private List<Remember> remembers;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;
    public AdapterRemember(Context context , List<Remember> list) {
        this.context = context;
        this.remembers = list;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recy_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Remember remember = remembers.get(position);

        holder.textTitle.setText(remember.getTitle());
        holder.textContent.setText(remember.getContent());
        holder.textTime.setText(remember.getTime());
        holder.imageView.setImageURI(Uri.parse(remember.getImgPath()));

        //删除功能
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("确定删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDbHelper = new MyDbHelper(context);
                        db = myDbHelper.getWritableDatabase();
                        db.delete("tb_remember","title=?",new String[]{remembers.get(position).getTitle()});
                        remembers.remove(position);
                        notifyItemRemoved(position);
                        //会话框消失
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return remembers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle,textContent,textTime;
        ImageView imageView;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textContent = itemView.findViewById(R.id.text_content);
            textTime = itemView.findViewById(R.id.text_time);
            imageView = itemView.findViewById(R.id.image_path);
            linearLayout = itemView.findViewById(R.id.line_item);
        }
    }
}
