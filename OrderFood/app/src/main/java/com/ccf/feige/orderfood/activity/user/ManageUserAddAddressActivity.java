package com.ccf.feige.orderfood.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.AddressBean;
import com.ccf.feige.orderfood.dao.AddressDao;
import com.ccf.feige.orderfood.until.Tools;

/**
 * 用户地址管理-添加新地址页面
 * 功能：接收用户输入的收货人姓名、收货地址、联系电话，完成新收货地址的添加操作
 */
public class ManageUserAddAddressActivity extends AppCompatActivity {

    /**
     * 页面创建时的初始化方法，完成布局加载、控件绑定、事件监听设置
     * @param savedInstanceState 保存的页面状态数据（本次未使用）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前页面的布局文件
        setContentView(R.layout.activity_manage_user_add_address);
        //加载上个界面传过来的数据（注：当前代码未实现具体的传参接收逻辑，仅保留注释说明）

        // 绑定页面顶部的Toolbar控件，作为页面导航栏
        Toolbar toolbar=this.findViewById(R.id.user_manage_addAddress_bar);
        // 将Toolbar设置为当前页面的ActionBar（替代系统默认导航栏）
        setSupportActionBar(toolbar);
        // 给Toolbar的返回按钮设置点击事件监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 优化：toolbar返回也使用finish()，和按钮返回行为一致，避免创建新实例
                finish();
//                原逻辑：Intent intent=new Intent(ManageUserAddAddressActivity.this, ManageUserAddressActivity.class);
//                startActivity(intent);
            }
        });

        // 绑定"收货人姓名"输入框控件
        EditText nameT=findViewById(R.id.user_manage_addAddress_name);
        // 调用工具类方法，获取当前登录账号的ID（用于关联收货地址所属用户）
        String id= Tools.getOnAccount(this);

        // 绑定"收货地址"输入框控件
        EditText addressT=findViewById(R.id.user_manage_addAddress_address);
        // 绑定"联系电话"输入框控件
        EditText phoneT=findViewById(R.id.user_manage_addAddress_phone);

        // 绑定"添加地址"按钮控件
        Button button=findViewById(R.id.user_manage_addAddress_add);
        // 给"添加地址"按钮设置点击事件监听
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取姓名输入框的文本内容（去除前后空格）
                String name=nameT.getText().toString();
                // 获取地址输入框的文本内容（去除前后空格）
                String address=addressT.getText().toString();
                // 获取电话输入框的文本内容（去除前后空格）
                String phone=phoneT.getText().toString();

                // 表单非空验证：依次检查姓名、地址、电话是否为空
                if(name.isEmpty()){
                    // 姓名为空时，弹出提示吐司
                    Toast.makeText(ManageUserAddAddressActivity.this, "请输入收货名称", Toast.LENGTH_SHORT).show();
                }else if(address.isEmpty()){
                    // 地址为空时，弹出提示吐司
                    Toast.makeText(ManageUserAddAddressActivity.this, "请输入收货地址", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    // 电话为空时，弹出提示吐司
                    Toast.makeText(ManageUserAddAddressActivity.this, "请输入收货联系方式", Toast.LENGTH_SHORT).show();
                }else{
                    // 表单验证通过，调用地址数据库访问层方法，执行添加地址操作
                    // 参数：当前登录用户ID、收货人姓名、收货地址、联系电话
                    int a= AddressDao.addAddress(id,name,address,phone);
                    // 判断添加操作结果（1代表添加成功，其他值代表添加失败）
                    if(a==1){
                        // 添加成功，弹出提示吐司
                        Toast.makeText(ManageUserAddAddressActivity.this, "添加成功", Toast.LENGTH_SHORT).show();

                        // 关键修改：添加成功后，关闭当前Activity，返回上一个ManageUserAddressActivity
                        finish();
                    }else{
                        // 添加失败，弹出提示吐司
                        Toast.makeText(ManageUserAddAddressActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}