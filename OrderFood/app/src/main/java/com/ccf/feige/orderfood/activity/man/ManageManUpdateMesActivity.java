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

    // 店铺名称输入框
    private EditText mBusinessName;
    // 店铺描述输入框
    private EditText mBusinessDes;
    // 店铺类型输入框
    private EditText mBusinessType;
    // 店铺头像展示/选择控件
    private ImageView mBusinessTx;
    // 确认修改按钮
    private Button mUpdateButton;

    // 选中图片的Uri路径，用于后续图片保存操作
    private Uri url;
    // 图片选择器的ActivityResultLauncher，用于替代传统的startActivityForResult
    private ActivityResultLauncher<String> getContentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前Activity对应的布局文件
        setContentView(R.layout.activity_manage_man_update_mes);

        // 初始化顶部工具栏，并设置点击事件
        Toolbar toolbar = findViewById(R.id.man_manage_updateBusiness_bar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击工具栏返回商家管理主页面，并传递状态参数"1"
                Intent intent=new Intent(ManageManUpdateMesActivity.this,ManageManActivity.class);
                intent.putExtra("sta","1");
                startActivity(intent);
            }
        });

        // 加载当前登录商家的个人信息（通过当前账号获取商家信息）
        UserBean user = AdminDao.getBusinessUser(Tools.getOnAccount(this));

        // 初始化店铺名称输入框，并填充原有商家名称
        mBusinessName = findViewById(R.id.man_manage_updateBusiness_name);
        mBusinessName.setText(user.getsName());

        // 初始化店铺描述输入框，并填充原有商家描述
        mBusinessDes = findViewById(R.id.man_manage_updateBusiness_des);
        mBusinessDes.setText(user.getsDescribe());//描述

        // 初始化店铺类型输入框，并填充原有商家类型
        mBusinessType = findViewById(R.id.man_manage_updateBusiness_type);
        mBusinessType.setText(user.getsType());//类型

        // 初始化商家头像控件
        mBusinessTx = findViewById(R.id.register_man_tx);
        // 根据商家信息中的图片路径解码生成Bitmap，并设置到头像控件上
        Bitmap bitmap = BitmapFactory.decodeFile(user.getsImg());
        mBusinessTx.setImageBitmap(bitmap);//加载头像
        // 给头像控件设置点击事件，用于触发图片选择
        mBusinessTx.setOnClickListener(new View.OnClickListener() {//加载图片
            @Override
            public void onClick(View v) {
                // 启动图片选择器，限定选择图片类型
                getContentLauncher.launch("image/*");
            }
        });

        // 注册图片选择的ActivityResultContract，处理图片选择结果
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                // 判断是否成功选择了图片
                if (result != null) {
                    // 将选中的图片设置到头像控件上展示
                    mBusinessTx.setImageURI(result);
                    // 保存选中图片的Uri，用于后续保存操作
                    url = result;
                } else {
                    // 未选择图片时给出提示
                    Toast.makeText(ManageManUpdateMesActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 初始化确认修改按钮
        mUpdateButton = findViewById(R.id.man_manage_updateBusiness_update);

        // 将商品原有的图片设置成默认图片（保存初始头像作为默认对比基准）
        Drawable drawable = mBusinessTx.getDrawable();//获取当前标签的图片
        Bitmap defaultDrawable = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件

        // 给确认修改按钮设置点击事件，处理商家信息修改逻辑
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入框中的店铺名称，去除首尾空格
                String name = mBusinessName.getText().toString().trim();
                // 获取输入框中的店铺描述，去除首尾空格
                String description = mBusinessDes.getText().toString().trim();
                // 获取输入框中的店铺类型，去除首尾空格
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
                    // 所有输入项验证通过，处理头像保存和信息更新逻辑

                    // 获取当前头像控件上的图片
                    Drawable drawable =  mBusinessTx.getDrawable();//获取当前标签的图片
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件

                    // 获取一个新的图片存储路径/文件名
                    String path = FileImgUntil.getImgName();//获取一个存储图片的路径名字
                    // 判断当前头像是否与默认头像（原有头像）一致
                    if (bitmap.sameAs(defaultDrawable)) {//判断是不是默认的图片
                        // 头像未更改，沿用原有图片路径
                        path=user.getsImg();
                    }else{
                        // 头像已更改，将新选中的图片保存到指定路径
                        FileImgUntil.saveImageBitmapToFileImg(url, ManageManUpdateMesActivity.this, path);//保存图片
                    }

                    // 调用Dao层方法更新商家信息，传入商家ID和新的信息参数
                    int a=AdminDao.updateBusinessUser(user.getsId(),name,description,type,path);
                    // 根据更新结果给出对应提示
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