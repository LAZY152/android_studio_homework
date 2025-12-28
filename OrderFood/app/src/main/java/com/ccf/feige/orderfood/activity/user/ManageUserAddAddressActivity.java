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

public class ManageUserAddAddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_add_address);
        //加载上个界面传过来的数据

        Toolbar toolbar=this.findViewById(R.id.user_manage_addAddress_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 优化：toolbar返回也使用finish()，和按钮返回行为一致，避免创建新实例
                finish();
//                原逻辑：Intent intent=new Intent(ManageUserAddAddressActivity.this, ManageUserAddressActivity.class);
//                startActivity(intent);
            }
        });

        EditText nameT=findViewById(R.id.user_manage_addAddress_name);
        String id= Tools.getOnAccount(this);

        EditText addressT=findViewById(R.id.user_manage_addAddress_address);
        EditText phoneT=findViewById(R.id.user_manage_addAddress_phone);

        Button button=findViewById(R.id.user_manage_addAddress_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameT.getText().toString();
                String address=addressT.getText().toString();
                String phone=phoneT.getText().toString();

                if(name.isEmpty()){
                    Toast.makeText(ManageUserAddAddressActivity.this, "请输入收货名称", Toast.LENGTH_SHORT).show();
                }else if(address.isEmpty()){
                    Toast.makeText(ManageUserAddAddressActivity.this, "请输入收货地址", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    Toast.makeText(ManageUserAddAddressActivity.this, "请输入收货联系方式", Toast.LENGTH_SHORT).show();
                }else{
                    int a= AddressDao.addAddress(id,name,address,phone);
                    if(a==1){
                        Toast.makeText(ManageUserAddAddressActivity.this, "添加成功", Toast.LENGTH_SHORT).show();

                        // 关键修改：添加成功后，关闭当前Activity，返回上一个ManageUserAddressActivity
                        finish();
                    }else{
                        Toast.makeText(ManageUserAddAddressActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}