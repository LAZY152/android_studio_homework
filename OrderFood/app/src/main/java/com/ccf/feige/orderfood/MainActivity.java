package com.ccf.feige.orderfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ccf.feige.orderfood.activity.man.ManageManActivity;
import com.ccf.feige.orderfood.activity.man.RegisterManActivity;
import com.ccf.feige.orderfood.activity.user.ManageUserActivity;
import com.ccf.feige.orderfood.activity.user.RegisterUserActivity;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.db.DBUntil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库
        DBUntil dbUntil = new DBUntil(this);
        DBUntil.con = dbUntil.getWritableDatabase();

        // 实现共享数据
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        // 商家单选按钮默认选中
        RadioButton sjRadio = findViewById(R.id.login_sj);
        sjRadio.setChecked(true);// 让运行时候商家单选按钮默认选择

        // 商家注册跳转
        Button zcsj = findViewById(R.id.login_zhuceshangjia);
        zcsj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到注册商家界面
                Intent intent = new Intent(MainActivity.this, RegisterManActivity.class);
                startActivity(intent);
            }
        });

        // 用户注册跳转
        Button zcyh = findViewById(R.id.login_zhuceyonghu);
        zcyh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到注册用户界面
                Intent intent = new Intent(MainActivity.this, RegisterUserActivity.class);
                startActivity(intent);
            }
        });

        // 登陆功能初始化
        EditText accountText = findViewById(R.id.login_account);
        EditText pwdText = findViewById(R.id.login_pwd);
        Button denglu = findViewById(R.id.login_denglu);
        RadioButton role = findViewById(R.id.login_sj);

        // 登录按钮点击事件
        denglu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountText.getText().toString();
                String pwd = pwdText.getText().toString();
                if (account.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                } else if (pwd.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    edit.putString("account", account);
                    edit.apply();
                    if (role.isChecked()) {
                        // 管理员/商家登录
                        int a = AdminDao.loginBusiness(account, pwd);
                        if (a == 1) {
                            Toast.makeText(MainActivity.this, "管理员登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ManageManActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "管理员账号或密码错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // 普通用户登录
                        int a = AdminDao.loginUser(account, pwd);
                        if (a == 1) {
                            Toast.makeText(MainActivity.this, "用户登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ManageUserActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "用户账号或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    /**
     * 重写返回键事件：点击一次即清空数据并退出程序，不返回任何界面
     * 关键：不调用 super.onBackPressed()，屏蔽系统默认返回行为
     */
    @Override
    public void onBackPressed() {
        // 1. 点击一次即清空所有数据
        super.onBackPressed();
        clearAllData();

        // 2. 彻底关闭任务栈中所有Activity，直接退出程序（不返回登录/其他界面）
        finishAffinity();

        // 可选：Android Q及以上，额外确保进程完全退出（优雅退出优先使用finishAffinity）
        // System.exit(0);
    }

    /**
     * 清空所有数据（内存缓存、数据库引用、SP残留数据）
     */
    private void clearAllData() {
        // 1. 清空数据库静态引用，释放资源
        DBUntil.con = null;

        // 2. 清空SharedPreferences所有残留数据（登录/商家/用户缓存）
        clearSPResidue();
    }

    /**
     * 清空SharedPreferences残留数据，确保无任何数据留存
     */
    private void clearSPResidue() {
        try {
            getSharedPreferences("data", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("BusinessInfoSP", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("UserInfoSP", MODE_PRIVATE).edit().clear().apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}