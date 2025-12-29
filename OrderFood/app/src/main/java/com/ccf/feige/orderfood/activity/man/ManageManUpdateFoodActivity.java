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

/**
 * 管理员商品修改页面Activity
 * 功能：展示商品原有信息、支持修改商品名称/价格/描述/图片、支持删除商品
 */
public class ManageManUpdateFoodActivity extends AppCompatActivity {
    // 选中图片的Uri对象，用于存储从相册选择的图片路径
    Uri uri;
    // 商品ID，用于标识当前要修改/删除的商品
    String foodId;

    // 相册选择结果启动器，用于接收从相册选择图片后的回调结果
    private ActivityResultLauncher<String> getContentLauncher;

    /**
     * 页面创建时的初始化方法
     * @param savedInstanceState 保存的页面状态数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前页面的布局文件
        setContentView(R.layout.activity_manage_man_update_food);

        // 获取从上一个页面传递过来的Intent对象，提取封装的FoodBean商品对象
        Intent intent = getIntent();
        FoodBean food = (FoodBean) intent.getSerializableExtra("food");
        // 从商品对象中获取商品ID并赋值
        foodId=food.getFoodId();

        // 绑定页面中的商品图片ImageView控件
        ImageView img = findViewById(R.id.man_manage_updateFood_img);
        // 根据商品对象中存储的图片路径，解码生成Bitmap位图对象
        Bitmap bitmap = BitmapFactory.decodeFile(food.getFoodImg());
        // 将解码后的位图设置到ImageView中，展示商品原有图片
        img.setImageBitmap(bitmap);

        // 注册相册选择结果回调，获取选择的图片Uri
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            /**
             * 相册选择完成后的回调方法
             * @param result 选择图片返回的Uri对象，未选择则为null
             */
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    // 若选择了图片，将Uri设置到ImageView展示图片
                    img.setImageURI(result);
                    // 保存选中图片的Uri供后续使用
                    uri = result;
                } else {
                    // 若未选择图片，弹出提示吐司
                    Toast.makeText(ManageManUpdateFoodActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 为商品图片ImageView设置点击事件监听器
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击图片时调用打开相册方法
                openGallery(v);
            }
        });

        // 将商品原有的图片设置成默认图片（用于后续判断是否修改了图片）
        Drawable drawable = img.getDrawable();//获取当前ImageView的图片Drawable对象
        Bitmap defaultDrawable = ((BitmapDrawable) drawable).getBitmap();//将Drawable转换为Bitmap位图，作为默认图片参考

        // 绑定页面中的Toolbar控件
        Toolbar toolbar = this.findViewById(R.id.man_manage_updateFood_bar);
        // 将Toolbar设置为当前页面的ActionBar
        setSupportActionBar(toolbar);
        // 为Toolbar设置返回按钮点击事件监听器
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回按钮，跳转到管理员主页面
                Intent intent = new Intent(ManageManUpdateFoodActivity.this, ManageManActivity.class);
                startActivity(intent);
            }
        });

        // 绑定商品名称输入框控件，并设置原有商品名称
        EditText nameText = findViewById(R.id.man_manage_updateFood_name);
        nameText.setText(food.getFoodName());

        // 绑定商品价格输入框控件，并设置原有商品价格
        EditText priceText = findViewById(R.id.man_manage_updateFood_price);
        priceText.setText(food.getFoodPrice());

        // 绑定商品描述输入框控件，并设置原有商品描述
        EditText desText = findViewById(R.id.man_manage_updateFood_des);
        desText.setText(food.getFoodDes());

        // 绑定修改商品按钮控件，并设置点击事件监听器
        Button btnAddProduct = findViewById(R.id.man_manage_updateFood_addBut);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入框中填写的新商品名称
                String name = nameText.getText().toString();
                // 获取输入框中填写的新商品价格
                String price = priceText.getText().toString();
                // 获取输入框中填写的新商品描述
                String des = desText.getText().toString();

                // 输入内容合法性校验
                if (name.isEmpty()) {
                    Toast.makeText(ManageManUpdateFoodActivity.this, "请输入商品名称", Toast.LENGTH_SHORT).show();
                } else if (price.isEmpty()) {
                    Toast.makeText(ManageManUpdateFoodActivity.this, "请输入商品价格", Toast.LENGTH_SHORT).show();
                } else if (des.isEmpty()) {
                    Toast.makeText(ManageManUpdateFoodActivity.this, "请输入商品描述", Toast.LENGTH_SHORT).show();
                } else {
                    // 所有输入项均合法，开始处理图片和修改逻辑

                    // 获取当前ImageView中的图片Drawable对象
                    Drawable drawable = img.getDrawable();//获取当前标签的图片
                    // 将Drawable转换为Bitmap位图对象
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();//获取这个图片的二进制文件

                    // 获取一个新的图片存储路径名称（用于保存新选择的图片）
                    String path = FileImgUntil.getImgName();//获取一个存储图片的路径名字
                    // 判断当前图片是否与默认图片（商品原有图片）一致
                    if (bitmap.sameAs(defaultDrawable)) {//判断是不是默认的图片
                        // 若未修改图片，直接使用商品原有图片路径
                        path=food.getFoodImg();
                    }else{
                        // 若修改了图片，将新选择的图片保存到指定路径
                        FileImgUntil.saveImageBitmapToFileImg(uri, ManageManUpdateFoodActivity.this, path);//保存图片
                    }

                    // 准备就绪，调用FoodDao的修改方法更新商品信息
                    int a = FoodDao.updateFood(food.getFoodId(), name, des, price, path);
                    // 根据修改结果弹出对应提示
                    if (a == 1) {
                        Toast.makeText(ManageManUpdateFoodActivity.this, "修改商品成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageManUpdateFoodActivity.this, "修改商品失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 打开手机相册的方法
     * @param v 点击事件源控件
     */
    private void openGallery(View v){
        // 启动相册选择，指定选择类型为图片
        getContentLauncher.launch("image/*");
    }

    /**
     * 加载页面菜单的方法
     * @param menu 菜单对象
     * @return 布尔值，标识是否成功加载菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载对应的菜单布局文件到当前页面
        getMenuInflater().inflate(R.menu.man_manage_food_del_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单选项点击事件处理方法
     * @param item 被点击的菜单选项
     * @return 布尔值，标识是否处理了该菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 获取被点击菜单的ID
        int a=item.getItemId();
        // 判断是否为删除商品菜单选项
        if(a==R.id.man_manage_food_del){
            // 创建AlertDialog.Builder对象，用于构建确认删除对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 设置对话框标题
            builder.setTitle("删除商品");
            // 设置对话框提示内容
            builder.setMessage("你确定删除该商品么!");
            // 设置对话框是否可以通过点击外部取消
            builder.setCancelable(false);
            // 设置对话框取消按钮
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击取消，关闭对话框
                    dialog.dismiss();
                }
            });
            // 设置对话框确认按钮
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 调用FoodDao的删除方法，根据商品ID删除商品
                    int a=FoodDao.delFoodById(foodId);
                    // 根据删除结果弹出对应提示并跳转页面
                    if(a==1){
                        Toast.makeText(ManageManUpdateFoodActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        // 删除成功，跳转到管理员主页面
                        Intent intent = new Intent(ManageManUpdateFoodActivity.this, ManageManActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ManageManUpdateFoodActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                    // 关闭对话框
                    dialog.dismiss();
                }
            });
            // 创建并显示对话框
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}