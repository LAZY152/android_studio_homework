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
import com.ccf.feige.orderfood.activity.man.ManageManActivity;
import com.ccf.feige.orderfood.activity.man.ManageManUpdateFoodActivity;
import com.ccf.feige.orderfood.bean.AddressBean;
import com.ccf.feige.orderfood.dao.AddressDao;
import com.ccf.feige.orderfood.dao.FoodDao;

import java.io.Serializable;

/**
 * 用户收货地址修改/删除页面
 * 功能：接收上一级页面传递的地址信息，提供地址编辑、保存更新、地址删除的功能
 */
public class ManageUserUpdateAddressActivity extends AppCompatActivity {

    // 全局变量：存储收货地址的唯一标识ID，用于后续更新和删除操作的条件查询
    private String id;

    /**
     * 页面创建生命周期方法
     * 负责初始化页面布局、接收上一级传递的数据、绑定控件、设置控件点击事件
     * @param savedInstanceState 保存页面状态的Bundle对象，用于页面重建时恢复数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前页面的布局文件，将XML布局转换为Android界面控件
        setContentView(R.layout.activity_manage_user_update_address);

        //加载上个界面传过来的数据
        // 1. 初始化顶部导航栏Toolbar，并设置为页面的ActionBar
        Toolbar toolbar=this.findViewById(R.id.user_manage_updateAddress_bar);
        setSupportActionBar(toolbar);

        // 2. 设置Toolbar左侧导航按钮（返回箭头）的点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关键优化1：Toolbar导航返回改为finish()，统一返回行为，避免创建新实例
                finish();
//                原逻辑：Intent intent=new Intent(ManageUserUpdateAddressActivity.this, ManageUserAddressActivity.class);
//                startActivity(intent);
            }
        });

        // 3. 获取上一级页面传递过来的Intent对象，提取携带的地址数据
        Intent intent = getIntent();
        // 从Intent中获取序列化的AddressBean对象（收货地址信息），key为"address"
        AddressBean address =(AddressBean) intent.getSerializableExtra("address");
        // 从地址对象中提取地址唯一ID，赋值给全局变量id，供后续更新/删除使用
        id=address.getsId();

        // 4. 初始化页面中的输入框控件，并填充上一级传递过来的地址原始数据
        // 初始化"收货人姓名"输入框，并设置默认值为原始地址的收货人姓名
        EditText nameT=findViewById(R.id.user_manage_updateAddress_name);
        nameT.setText(address.getsUserName());

        // 初始化"收货地址"输入框，并设置默认值为原始地址的详细地址
        EditText addressT=findViewById(R.id.user_manage_updateAddress_address);
        addressT.setText(address.getsUserAddress());

        // 初始化"联系电话"输入框，并设置默认值为原始地址的联系电话
        EditText phoneT=findViewById(R.id.user_manage_updateAddress_phone);
        phoneT.setText(address.getsUserPhone());

        // 5. 初始化"确认更新"按钮，并设置点击事件监听
        Button button=findViewById(R.id.user_manage_updateAddress_up);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 5.1 从三个输入框中获取用户编辑后的最新数据
                String name=nameT.getText().toString();
                String address=addressT.getText().toString();
                String phone=phoneT.getText().toString();

                // 5.2 输入合法性校验，非空判断
                if(name.isEmpty()){
                    // 收货人姓名为空，弹出提示吐司
                    Toast.makeText(ManageUserUpdateAddressActivity.this, "请输入收货名称", Toast.LENGTH_SHORT).show();
                }else if(address.isEmpty()){
                    // 收货地址为空，弹出提示吐司
                    Toast.makeText(ManageUserUpdateAddressActivity.this, "请输入收货地址", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    // 联系电话为空，弹出提示吐司
                    Toast.makeText(ManageUserUpdateAddressActivity.this, "请输入收货联系方式", Toast.LENGTH_SHORT).show();
                }else{
                    // 5.3 输入数据合法，调用AddressDao的更新方法执行地址更新操作
                    // 参数：地址ID（更新条件）、新的收货人姓名、新的收货地址、新的联系电话
                    // 返回值：影响的数据库行数（1表示更新成功，0表示更新失败）
                    int a= AddressDao.updateAddress(id,name,address,phone);
                    if(a==1){
                        // 更新成功，弹出提示吐司
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "更改成功", Toast.LENGTH_SHORT).show();

                        // 关键修改2：更新成功后，调用finish()返回上一级页面
                        finish();
                    }else{
                        // 更新失败，弹出提示吐司
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 初始化页面菜单的方法
     * 用于加载顶部Toolbar右侧的菜单布局
     * @param menu 要填充的菜单对象
     * @return 返回true表示菜单加载成功并显示
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载菜单布局文件（user_address_del_menu）到menu对象中，该菜单包含删除功能
        getMenuInflater().inflate(R.menu.user_address_del_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单选项点击事件处理方法
     * 响应Toolbar右侧菜单的点击操作
     * @param item 被点击的菜单选项对象
     * @return 返回true表示已处理该菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 获取被点击菜单的唯一标识ID
        int a=item.getItemId();
        // 判断是否点击了"删除地址"菜单（标识为R.id.user_manage_delAddress）
        if(a==R.id.user_manage_delAddress) {
            // 1. 创建AlertDialog构建器，用于弹出删除确认对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 设置对话框标题
            builder.setTitle("信息");
            // 设置对话框提示内容，确认用户是否要删除该地址
            builder.setMessage("你确定删除收货地址!");
            // 设置对话框是否可以通过点击外部区域取消，false表示不可以
            builder.setCancelable(false);

            // 2. 设置对话框"取消"按钮
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击取消，关闭对话框，不执行任何操作
                    dialog.dismiss();
                }
            });

            // 3. 设置对话框"确认"按钮
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 3.1 调用AddressDao的删除方法，根据地址ID执行删除操作
                    // 返回值：影响的数据库行数（1表示删除成功，0表示删除失败）
                    int a = AddressDao.deleteAddressById(id);
                    if (a == 1) {
                        // 删除成功，弹出提示吐司
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                        // 关键优化3：删除成功后也改为finish()，统一返回逻辑
                        finish();
//                        原逻辑：Intent intent=new Intent(ManageUserUpdateAddressActivity.this, ManageUserAddressActivity.class);
//                        startActivity(intent);
                    } else {
                        // 删除失败，弹出提示吐司
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                    // 关闭对话框
                    dialog.dismiss();
                }
            });

            // 4. 根据构建器创建AlertDialog实例，并显示对话框
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}