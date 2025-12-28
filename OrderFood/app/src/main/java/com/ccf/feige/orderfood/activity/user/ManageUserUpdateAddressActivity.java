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

public class ManageUserUpdateAddressActivity extends AppCompatActivity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_update_address);

        //加载上个界面传过来的数据
        Toolbar toolbar=this.findViewById(R.id.user_manage_updateAddress_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关键优化1：Toolbar导航返回改为finish()，统一返回行为，避免创建新实例
                finish();
//                原逻辑：Intent intent=new Intent(ManageUserUpdateAddressActivity.this, ManageUserAddressActivity.class);
//                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        AddressBean address =(AddressBean) intent.getSerializableExtra("address");
        id=address.getsId();

        EditText nameT=findViewById(R.id.user_manage_updateAddress_name);
        nameT.setText(address.getsUserName());

        EditText addressT=findViewById(R.id.user_manage_updateAddress_address);
        addressT.setText(address.getsUserAddress());
        EditText phoneT=findViewById(R.id.user_manage_updateAddress_phone);
        phoneT.setText(address.getsUserPhone());

        Button button=findViewById(R.id.user_manage_updateAddress_up);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameT.getText().toString();
                String address=addressT.getText().toString();
                String phone=phoneT.getText().toString();

                if(name.isEmpty()){
                    Toast.makeText(ManageUserUpdateAddressActivity.this, "请输入收货名称", Toast.LENGTH_SHORT).show();
                }else if(address.isEmpty()){
                    Toast.makeText(ManageUserUpdateAddressActivity.this, "请输入收货地址", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    Toast.makeText(ManageUserUpdateAddressActivity.this, "请输入收货联系方式", Toast.LENGTH_SHORT).show();
                }else{
                    int a= AddressDao.updateAddress(id,name,address,phone);
                    if(a==1){
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "更改成功", Toast.LENGTH_SHORT).show();

                        // 关键修改2：更新成功后，调用finish()返回上一级页面
                        finish();
                    }else{
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_address_del_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int a=item.getItemId();
        if(a==R.id.user_manage_delAddress) {
            // 创建AlertDialog.Builder对象
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("信息");
            builder.setMessage("你确定删除收货地址!");
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
                    int a = AddressDao.deleteAddressById(id);
                    if (a == 1) {
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                        // 关键优化3：删除成功后也改为finish()，统一返回逻辑
                        finish();
//                        原逻辑：Intent intent=new Intent(ManageUserUpdateAddressActivity.this, ManageUserAddressActivity.class);
//                        startActivity(intent);
                    } else {
                        Toast.makeText(ManageUserUpdateAddressActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}