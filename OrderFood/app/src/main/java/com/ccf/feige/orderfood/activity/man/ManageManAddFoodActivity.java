package com.ccf.feige.orderfood.activity.man;

/**
 * 这个是数据添加食物的界面（管理员端添加商品的页面）
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

/**
 * 管理员端商品添加页面
 * 负责提供商品名称、价格、描述、图片的录入界面，并将商品信息持久化到数据库
 */
public class ManageManAddFoodActivity extends AppCompatActivity {
    // 选中的图片Uri，用于存储从相册选择的图片路径
    Uri uri;

    // 相册选择结果启动器，用于接收从相册选择图片后的回调结果
    private ActivityResultLauncher<String> getContentLauncher;

    /**
     * 页面创建生命周期方法，初始化页面布局、控件和事件监听
     * @param savedInstanceState 保存的页面状态数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前页面的布局文件
        setContentView(R.layout.activity_manage_man_add_food);

        // 实现数据账号共享（获取当前登录的商家/管理员账号ID）
        // 获取名为"data"的SharedPreferences存储，私有模式仅当前应用可访问
        SharedPreferences sharedPreferences=getSharedPreferences("data",Context.MODE_PRIVATE);
        // 从SharedPreferences中获取"account"对应的账号，默认值为"root"
        String businessId=sharedPreferences.getString("account","root");//如果这个值没有添加则使用默认的

        // 初始化商品图片展示控件，用于预览选择的商品图片
        ImageView img = findViewById(R.id.man_manage_addFood_img);

        // 注册相册选择结果回调，处理从相册选择图片后的逻辑
        getContentLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            /**
             * 相册选择图片结果回调
             * @param result 选中图片的Uri，未选择时为null
             */
            @Override
            public void onActivityResult(Uri result) {
                // 判断是否成功选中图片
                if(result!=null){
                    // 给ImageView设置选中的图片，实现预览效果
                    img.setImageURI(result);
                    // 保存选中图片的Uri，用于后续图片保存操作
                    uri =result;
                }else{
                    // 未选择图片时给出提示
                    Toast.makeText(ManageManAddFoodActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 给商品图片控件设置点击事件，点击后打开相册选择图片
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(v);
            }
        });

        // 获取默认上传图片的Drawable（未选择商品图片时的占位图）
        Drawable defaultDrawable= ContextCompat.getDrawable(this,R.drawable.upimg);

        // 初始化页面顶部导航栏
        Toolbar toolbar=this.findViewById(R.id.man_manage_addFood_bar);
        // 给导航栏设置返回点击事件，返回管理员主页面
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 构建跳转意图，从当前添加页面跳转到管理员主页面
                Intent intent=new Intent(ManageManAddFoodActivity.this,ManageManActivity.class);
                // 执行页面跳转
                startActivity(intent);
            }
        });

        // 初始化商品名称输入框
        EditText nameText = findViewById(R.id.man_manage_addFood_name);
        // 初始化商品价格输入框
        EditText priceText = findViewById(R.id.man_manage_addFood_price);
        // 初始化商品描述输入框
        EditText desText = findViewById(R.id.man_manage_addFood_des);
        // 初始化添加商品按钮
        Button btnAddProduct = findViewById(R.id.man_manage_addFood_addBut);

        // 给添加商品按钮设置点击事件，处理商品信息提交逻辑
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入框中的商品名称
                String name=nameText.getText().toString();
                // 获取输入框中的商品价格
                String price=priceText.getText().toString();
                // 获取输入框中的商品描述
                String des=desText.getText().toString();

                // 输入项非空校验
                if(name.isEmpty()){
                    Toast.makeText(ManageManAddFoodActivity.this, "请输入商品名称", Toast.LENGTH_SHORT).show();
                }else if(price.isEmpty()){
                    Toast.makeText(ManageManAddFoodActivity.this, "请输入商品价格", Toast.LENGTH_SHORT).show();
                }else if(des.isEmpty()){
                    Toast.makeText(ManageManAddFoodActivity.this, "请输入商品描述", Toast.LENGTH_SHORT).show();
                }else{
                    // 获取图片控件当前显示的图片（用户选择的图片或默认占位图）
                    Drawable drawable=img.getDrawable();//获取当前标签的图片
                    // 将Drawable转换为Bitmap，用于后续和默认图片对比
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件
                    // 将默认占位图转换为Bitmap，用于对比判断
                    Bitmap bitmapDef = ((BitmapDrawable) defaultDrawable).getBitmap();//获取这个图片的二进制文件

                    // 判断是不是默认的图片（用户是否未选择商品图片）
                    if(bitmap.sameAs(bitmapDef)){
                        Toast.makeText(ManageManAddFoodActivity.this, "请点击图片进行添加商品", Toast.LENGTH_SHORT).show();
                    }else{
                        // 获取一个唯一的图片存储路径/文件名（用于区分不同商品的图片）
                        String path= FileImgUntil.getImgName();//获取一个存储图片的路径名字
                        // 将选中的图片保存到本地文件中
                        FileImgUntil.saveImageBitmapToFileImg(uri,ManageManAddFoodActivity.this,path);//保存图片

                        // 准备就绪，调用Dao层方法添加商品信息到数据库
                        int a=FoodDao.addFood(businessId,name,des,price,path);

                        // 根据Dao层返回结果判断添加是否成功，并给出对应提示
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

    /**
     * 打开手机相册，选择商品图片
     * @param v 点击事件源控件（此处为商品图片ImageView）
     */
    private void openGallery(View v){
        // 启动相册选择，限定选择类型为所有图片格式
        getContentLauncher.launch("image/*");
    }
}