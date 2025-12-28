package com.ccf.feige.orderfood.activity.man;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.ccf.feige.orderfood.R;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.dao.FoodDao;
import com.ccf.feige.orderfood.until.FileImgUntil;

public class ManageManUpdateFoodActivity extends AppCompatActivity {
    Uri uri;
    String foodId;

    private ActivityResultLauncher<String> getContentLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_man_update_food);





        Intent intent = getIntent();
        FoodBean food = (FoodBean) intent.getSerializableExtra("food");
        foodId=food.getFoodId();



        ImageView img = findViewById(R.id.man_manage_updateFood_img);
        Bitmap bitmap = BitmapFactory.decodeFile(food.getFoodImg());
        img.setImageBitmap(bitmap);


        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    img.setImageURI(result);
                    uri = result;

                } else {
                    Toast.makeText(ManageManUpdateFoodActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(v);
            }
        });


        //将商品原有的图片设置成默认图片
        Drawable drawable = img.getDrawable();//获取当前标签的图片
        Bitmap defaultDrawable = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件


        Toolbar toolbar = this.findViewById(R.id.man_manage_updateFood_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageManUpdateFoodActivity.this, ManageManActivity.class);
                startActivity(intent);
            }
        });


        EditText nameText = findViewById(R.id.man_manage_updateFood_name);
        nameText.setText(food.getFoodName());

        EditText priceText = findViewById(R.id.man_manage_updateFood_price);
        priceText.setText(food.getFoodPrice());

        EditText desText = findViewById(R.id.man_manage_updateFood_des);
        desText.setText(food.getFoodDes());

        Button btnAddProduct = findViewById(R.id.man_manage_updateFood_addBut);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String price = priceText.getText().toString();
                String des = desText.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(ManageManUpdateFoodActivity.this, "请输入商品名称", Toast.LENGTH_SHORT).show();
                } else if (price.isEmpty()) {
                    Toast.makeText(ManageManUpdateFoodActivity.this, "请输入商品价格", Toast.LENGTH_SHORT).show();
                } else if (des.isEmpty()) {
                    Toast.makeText(ManageManUpdateFoodActivity.this, "请输入商品描述", Toast.LENGTH_SHORT).show();
                } else {


                    Drawable drawable = img.getDrawable();//获取当前标签的图片
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件

                    String path = FileImgUntil.getImgName();//获取一个存储图片的路径名字
                    if (bitmap.sameAs(defaultDrawable)) {//判断是不是默认的图片
                       path=food.getFoodImg();
                    }else{
                        FileImgUntil.saveImageBitmapToFileImg(uri, ManageManUpdateFoodActivity.this, path);//保存图片
                    }


                    //准备就绪，协议修改dao
                    int a = FoodDao.updateFood(food.getFoodId(), name, des, price, path);
                    if (a == 1) {
                        Toast.makeText(ManageManUpdateFoodActivity.this, "修改商品成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageManUpdateFoodActivity.this, "修改商品失败", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });


    }

    private void openGallery(View v){
        getContentLauncher.launch("image/*");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.man_manage_food_del_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int a=item.getItemId();
        if(a==R.id.man_manage_food_del){
            // 创建AlertDialog.Builder对象
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("删除商品");
            builder.setMessage("你确定删除该商品么!");
            builder.setCancelable(false);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int a=FoodDao.delFoodById(foodId);
                    if(a==1){
                        Toast.makeText(ManageManUpdateFoodActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ManageManUpdateFoodActivity.this, ManageManActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ManageManUpdateFoodActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog=builder.create();
            alertDialog.show();




        }

        return super.onOptionsItemSelected(item);
    }
}