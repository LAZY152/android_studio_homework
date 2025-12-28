package com.ccf.feige.orderfood.activity.man;
/**
 * 这个是数据添加书屋的界面
 */

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.dao.FoodDao;
import com.ccf.feige.orderfood.until.FileImgUntil;

public class ManageManAddFoodActivity extends AppCompatActivity {
    Uri uri;

    private ActivityResultLauncher<String> getContentLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_man_add_food);




        
        //实现数据账号共享
        SharedPreferences sharedPreferences=getSharedPreferences("data",Context.MODE_PRIVATE);
        String businessId=sharedPreferences.getString("account","root");//如果这个值没有添加则使用默认的




        ImageView img = findViewById(R.id.man_manage_addFood_img);
        getContentLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result!=null){
                    img.setImageURI(result);
                    uri =result;

                }else{
                    Toast.makeText(ManageManAddFoodActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(v);
            }
        });


        //获取某人头像
        Drawable defaultDrawable= ContextCompat.getDrawable(this,R.drawable.upimg);

        Toolbar toolbar=this.findViewById(R.id.man_manage_addFood_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageManAddFoodActivity.this,ManageManActivity.class);
                startActivity(intent);
            }
        });


        EditText nameText = findViewById(R.id.man_manage_addFood_name);
        EditText priceText = findViewById(R.id.man_manage_addFood_price);
        EditText desText = findViewById(R.id.man_manage_addFood_des);
        Button btnAddProduct = findViewById(R.id.man_manage_addFood_addBut);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameText.getText().toString();
                String price=priceText.getText().toString();
                String des=desText.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(ManageManAddFoodActivity.this, "请输入商品名称", Toast.LENGTH_SHORT).show();
                }else if(price.isEmpty()){
                    Toast.makeText(ManageManAddFoodActivity.this, "请输入商品价格", Toast.LENGTH_SHORT).show();
                }else if(des.isEmpty()){
                    Toast.makeText(ManageManAddFoodActivity.this, "请输入商品描述", Toast.LENGTH_SHORT).show();
                }else{


                    Drawable drawable=img.getDrawable();//获取当前标签的图片
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件
                    Bitmap bitmapDef = ((BitmapDrawable) defaultDrawable).getBitmap();//获取这个图片的二进制文件
                    if(bitmap.sameAs(bitmapDef)){//判断是不是默认的图片
                        Toast.makeText(ManageManAddFoodActivity.this, "请点击图片进行添加商品", Toast.LENGTH_SHORT).show();
                    }else{

                        String path= FileImgUntil.getImgName();//获取一个存储图片的路径名字
                        FileImgUntil.saveImageBitmapToFileImg(uri,ManageManAddFoodActivity.this,path);//保存图片
                        //准备就绪，协议添加dao
                        int a=FoodDao.addFood(businessId,name,des,price,path);
                        if(a==1){
                            Toast.makeText(ManageManAddFoodActivity.this, "添加商品成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ManageManAddFoodActivity.this, "添加商品失败", Toast.LENGTH_SHORT).show();
                        }

                    }


                }

            }
        });


    }

    private void openGallery(View v){
        getContentLauncher.launch("image/*");
    }
}