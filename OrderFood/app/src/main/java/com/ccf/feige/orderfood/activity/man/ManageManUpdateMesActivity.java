package com.ccf.feige.orderfood.activity.man;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.UserBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.until.FileImgUntil;
import com.ccf.feige.orderfood.until.Tools;

/**
 * 实现修改商家信息
 */
public class ManageManUpdateMesActivity extends AppCompatActivity {

    private EditText mBusinessName;
    private EditText mBusinessDes;
    private EditText mBusinessType;
    private ImageView mBusinessTx;
    private Button mUpdateButton;

    private Uri url;
    private ActivityResultLauncher<String> getContentLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_man_update_mes);


        Toolbar toolbar = findViewById(R.id.man_manage_updateBusiness_bar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageManUpdateMesActivity.this,ManageManActivity.class);
                intent.putExtra("sta","1");
                startActivity(intent);
            }
        });

        //加载这个商家的个人信息
        UserBean user = AdminDao.getBusinessUser(Tools.getOnAccount(this));

        mBusinessName = findViewById(R.id.man_manage_updateBusiness_name);
        mBusinessName.setText(user.getsName());


        mBusinessDes = findViewById(R.id.man_manage_updateBusiness_des);
        mBusinessDes.setText(user.getsDescribe());//描述

        mBusinessType = findViewById(R.id.man_manage_updateBusiness_type);
        mBusinessType.setText(user.getsType());//类型

        mBusinessTx = findViewById(R.id.register_man_tx);
        Bitmap bitmap = BitmapFactory.decodeFile(user.getsImg());
        mBusinessTx.setImageBitmap(bitmap);//加载头像
        mBusinessTx.setOnClickListener(new View.OnClickListener() {//加载图片
            @Override
            public void onClick(View v) {
                getContentLauncher.launch("image/*");
            }
        });
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    mBusinessTx.setImageURI(result);
                    url = result;

                } else {
                    Toast.makeText(ManageManUpdateMesActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });



        mUpdateButton = findViewById(R.id.man_manage_updateBusiness_update);


        //将商品原有的图片设置成默认图片
        Drawable drawable = mBusinessTx.getDrawable();//获取当前标签的图片
        Bitmap defaultDrawable = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件


        //点击修改有对应的提示
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mBusinessName.getText().toString().trim();
                String description = mBusinessDes.getText().toString().trim();
                String type = mBusinessType.getText().toString().trim();

                // 首先检查店铺名称
                if (name.isEmpty()) {
                    mBusinessName.setError("店铺名称不能为空");
                    mBusinessName.requestFocus(); // 将焦点设置到该EditText上，方便用户立即输入

                } else  if (description.isEmpty()) { // 然后检查店铺描述
                    mBusinessDes.setError("店铺描述不能为空");
                    mBusinessDes.requestFocus();

                } else if (type.isEmpty()) { // 最后检查店铺类型
                    mBusinessType.setError("店铺类型不能为空");
                    mBusinessType.requestFocus();

                }else{


                    Drawable drawable =  mBusinessTx.getDrawable();//获取当前标签的图片
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件

                    String path = FileImgUntil.getImgName();//获取一个存储图片的路径名字
                    if (bitmap.sameAs(defaultDrawable)) {//判断是不是默认的图片
                        path=user.getsImg();
                    }else{
                        FileImgUntil.saveImageBitmapToFileImg(url, ManageManUpdateMesActivity.this, path);//保存图片
                    }

                    int a=AdminDao.updateBusinessUser(user.getsId(),name,description,type,path);
                    if(a==1){
                        Toast.makeText(ManageManUpdateMesActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ManageManUpdateMesActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });

    }


}