package com.ccf.feige.orderfood.activity.user;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import android.widget.RadioButton;
import android.widget.Toast;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.ManageManActivity;
import com.ccf.feige.orderfood.activity.man.ManageManUpdateMesActivity;
import com.ccf.feige.orderfood.activity.man.ManageManUpdatePwdActivity;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.until.FileImgUntil;
import com.ccf.feige.orderfood.until.Tools;

public class ManageUserUpdateMesActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> getContentLauncher;

    private Uri url;
    String sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_update_mes);
        //实现一个返回的功能
        Toolbar toolbar = findViewById(R.id.user_manage_updateMes_bar);
        setSupportActionBar(toolbar);
        //返回有两种，采用跳转和管理
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageUserUpdateMesActivity.this, ManageUserActivity.class);
                intent.putExtra("sta","1");
                startActivity(intent);
            }
        });

        UserCommonBean user = AdminDao.getCommonUser(Tools.getOnAccount(this));


        ImageView imgText=findViewById(R.id.user_manage_updateMes_tx);
        imgText.setImageBitmap(BitmapFactory.decodeFile(user.getsImg()));

        imgText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentLauncher.launch("image/*");
            }
        });
        getContentLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    imgText.setImageURI(result);
                    url = result;

                } else {
                    Toast.makeText(ManageUserUpdateMesActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //获取某人头像
        //将商品原有的图片设置成默认图片
        Drawable drawable =  imgText.getDrawable();//获取当前标签的图片
        Bitmap defaultDrawable = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件

        EditText nameText=findViewById(R.id.user_manage_updateMes_name);
        nameText.setText(user.getsName());
        sex="女";
        RadioButton man=findViewById(R.id.user_manage_updateMes_nan);
        RadioButton nv=findViewById(R.id.user_manage_updateMes_nv);


        sex=user.getsSex();
        if(user.getsSex().equals("男")){
            man.setChecked(true);
        }else{
            nv.setChecked(true);
        }


        EditText addressText=findViewById(R.id.user_manage_updateMes_address);//地址
        addressText.setText(user.getsAddress());
        EditText phoneText=findViewById(R.id.user_manage_updateMes_phone);
        phoneText.setText(user.getsPhone());
        String id= Tools.getOnAccount(this);
        Button reg= findViewById(R.id.user_manage_updateMes_xggrxx);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断密码是否为空，进行实现注册功能
                //需要判断这个人是否选择了头像

                String name=nameText.getText().toString();//变化
                String address=addressText.getText().toString();//变化
                String phone=phoneText.getText().toString();//变化

                Drawable drawable=imgText.getDrawable();//获取当前标签的图片
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件
                Bitmap bitmapDef = defaultDrawable;//获取这个图片的二进制文件
                if(name.isEmpty()){
                    Toast.makeText(ManageUserUpdateMesActivity.this, "请输入用户昵称", Toast.LENGTH_SHORT).show();
                }else if( address.isEmpty()){
                    Toast.makeText(ManageUserUpdateMesActivity.this, "请输入住址", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    Toast.makeText(ManageUserUpdateMesActivity.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
                }else{

                    //可以向数据库进行保存数据
                    ///storage/emulated/0/Pictures/111.png
                    //bitmap  我需要将他保存到另一个一个文件当中，并且把保存路径输入进去

                    //得到一个图名字 root/图片文件+"/a.png
                    String path= FileImgUntil.getImgName();//获取一个存储图片的路径名字

                    if (bitmap.sameAs(defaultDrawable)) {//判断是不是默认的图片
                        path=user.getsImg();
                    }else{
                        FileImgUntil.saveImageBitmapToFileImg(url, ManageUserUpdateMesActivity.this, path);//保存图片
                    }

                    if(man.isChecked()){
                        sex="男";
                    }else{
                        sex="女";
                    }

                    int a= AdminDao.updateCommonUser(id,name,sex,address,phone,path);

                    if(a==1){
                        Toast.makeText(ManageUserUpdateMesActivity.this, "更改个人信息成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ManageUserUpdateMesActivity.this, "更改个人信息失败", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        //实现选择图片
        imgText.setOnClickListener(new View.OnClickListener() {//实现点击头像进行上传头像
            @Override
            public void onClick(View v) {
                //打开文件选择器
                openGallery(v);
            }
        });



    }

    /**
     * 打开文件选择器
     * @param v
     */

    private void openGallery(View v){
        getContentLauncher.launch("image/*");
    }
}