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

/**
 * 用户管理-个人信息修改页面
 * 功能：展示用户原有信息、支持修改昵称/性别/地址/手机号、支持更换头像并保存修改
 */
public class ManageUserUpdateMesActivity extends AppCompatActivity {
    // 图片选择结果启动器，用于注册相册选择图片的回调（AndroidX 推荐替代 startActivityForResult）
    private ActivityResultLauncher<String> getContentLauncher;

    // 选中图片的Uri路径，用于后续图片保存操作
    private Uri url;
    // 用户性别标识
    String sex;

    /**
     * 页面创建生命周期方法，完成页面初始化、控件绑定、数据填充、事件监听设置
     * @param savedInstanceState 保存的页面状态数据（如屏幕旋转时的临时数据）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定当前页面的布局文件（XML）
        setContentView(R.layout.activity_manage_user_update_mes);

        // 1. 初始化顶部工具栏，实现返回功能
        Toolbar toolbar = findViewById(R.id.user_manage_updateMes_bar);
        // 将Toolbar设置为当前页面的ActionBar（替代原生标题栏）
        setSupportActionBar(toolbar);
        // 设置工具栏左侧返回按钮的点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回时，跳转回用户管理主页面，并传递状态参数"1"
                Intent intent=new Intent(ManageUserUpdateMesActivity.this, ManageUserActivity.class);
                intent.putExtra("sta","1");
                startActivity(intent);
            }
        });

        // 2. 获取当前登录用户的信息（通过工具类获取当前账号，再从数据库查询用户详情）
        UserCommonBean user = AdminDao.getCommonUser(Tools.getOnAccount(this));

        // 3. 初始化头像控件，填充用户原有头像
        ImageView imgText=findViewById(R.id.user_manage_updateMes_tx);
        // 通过图片文件路径解码为Bitmap，设置到头像ImageView中
        imgText.setImageBitmap(BitmapFactory.decodeFile(user.getsImg()));

        // 3.1 先为头像设置一次点击事件（后续被重复覆盖，仅保留代码结构）
        imgText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动相册选择，筛选图片类型文件
                getContentLauncher.launch("image/*");
            }
        });

        // 4. 注册相册选择图片的回调监听（AndroidX Activity Result API）
        getContentLauncher=registerForActivityResult(
                // 选择器类型：获取系统内容（此处为相册图片）
                new ActivityResultContracts.GetContent(),
                // 选择结果回调
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        // 判断是否成功选中图片
                        if (result != null) {
                            // 将选中的图片设置到头像ImageView中展示
                            imgText.setImageURI(result);
                            // 保存选中图片的Uri，用于后续图片保存操作
                            url = result;
                        } else {
                            // 未选中图片时，弹出提示吐司
                            Toast.makeText(ManageUserUpdateMesActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // 5. 获取用户原有头像作为默认头像（用于后续判断是否更换了头像）
        // 获取当前头像控件的Drawable对象
        Drawable drawable =  imgText.getDrawable();
        // 将Drawable转换为Bitmap，保存为默认头像（未更换头像时的基准图）
        Bitmap defaultDrawable = ((BitmapDrawable) drawable).getBitmap();

        // 6. 初始化昵称输入框，填充用户原有昵称
        EditText nameText=findViewById(R.id.user_manage_updateMes_name);
        nameText.setText(user.getsName());

        // 7. 初始化性别单选按钮，填充用户原有性别
        // 默认性别设为女
        sex="女";
        // 获取男、女单选按钮控件
        RadioButton man=findViewById(R.id.user_manage_updateMes_nan);
        RadioButton nv=findViewById(R.id.user_manage_updateMes_nv);

        // 从用户信息中获取真实性别，设置对应单选按钮为选中状态
        sex=user.getsSex();
        if(user.getsSex().equals("男")){
            man.setChecked(true);
        }else{
            nv.setChecked(true);
        }

        // 8. 初始化地址输入框，填充用户原有地址
        EditText addressText=findViewById(R.id.user_manage_updateMes_address);
        addressText.setText(user.getsAddress());

        // 9. 初始化手机号输入框，填充用户原有手机号
        EditText phoneText=findViewById(R.id.user_manage_updateMes_phone);
        phoneText.setText(user.getsPhone());

        // 10. 获取当前登录用户的账号（作为修改用户信息的唯一标识）
        String id= Tools.getOnAccount(this);

        // 11. 初始化"修改个人信息"按钮，设置点击事件监听
        Button reg= findViewById(R.id.user_manage_updateMes_xggrxx);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 11.1 获取输入框中的最新数据（用户修改后的数据）
                String name=nameText.getText().toString();// 昵称
                String address=addressText.getText().toString();// 地址
                String phone=phoneText.getText().toString();// 手机号

                // 11.2 获取当前头像和默认头像的Bitmap对象，用于判断是否更换了头像
                Drawable drawable=imgText.getDrawable();// 当前头像控件的Drawable
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();// 当前头像的Bitmap
                Bitmap bitmapDef = defaultDrawable;// 原始默认头像的Bitmap

                // 11.3 输入合法性校验（非空判断）
                if(name.isEmpty()){
                    Toast.makeText(ManageUserUpdateMesActivity.this, "请输入用户昵称", Toast.LENGTH_SHORT).show();
                }else if( address.isEmpty()){
                    Toast.makeText(ManageUserUpdateMesActivity.this, "请输入住址", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    Toast.makeText(ManageUserUpdateMesActivity.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
                }else{
                    // 11.4 头像处理：判断是否更换头像，处理图片保存路径
                    // 获取图片存储的目标路径（通过工具类生成唯一路径）
                    String path= FileImgUntil.getImgName();

                    // 判断当前头像是否与默认头像一致（未更换头像）
                    if (bitmap.sameAs(defaultDrawable)) {
                        // 未更换头像，沿用用户原有头像路径
                        path=user.getsImg();
                    }else{
                        // 已更换头像，将新头像保存到目标路径
                        FileImgUntil.saveImageBitmapToFileImg(url, ManageUserUpdateMesActivity.this, path);
                    }

                    // 11.5 获取当前选中的性别
                    if(man.isChecked()){
                        sex="男";
                    }else{
                        sex="女";
                    }

                    // 11.6 调用数据库DAO方法，更新用户信息
                    int a= AdminDao.updateCommonUser(id,name,sex,address,phone,path);

                    // 11.7 根据更新结果，弹出对应的提示吐司
                    if(a==1){
                        Toast.makeText(ManageUserUpdateMesActivity.this, "更改个人信息成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ManageUserUpdateMesActivity.this, "更改个人信息失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 12. 重新为头像设置点击事件（覆盖之前的点击事件，功能一致：打开相册选择图片）
        imgText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用自定义方法，打开系统相册选择图片
                openGallery(v);
            }
        });
    }

    /**
     * 自定义方法：打开系统相册文件选择器，筛选图片类型文件
     * @param v 点击事件源控件（此处为头像ImageView）
     */
    private void openGallery(View v){
        // 启动图片选择器，指定筛选类型为所有图片（"image/*"）
        getContentLauncher.launch("image/*");
    }
}