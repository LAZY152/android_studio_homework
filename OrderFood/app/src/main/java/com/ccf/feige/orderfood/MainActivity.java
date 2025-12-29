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

/**
 * 程序主入口 - 登录界面Activity
 * 负责处理商家/用户的登录验证、注册跳转，以及程序退出时的数据清理
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Activity创建生命周期方法，完成界面初始化、控件绑定、事件监听设置
     * @param savedInstanceState 保存的Activity状态数据（当前界面无状态恢复需求，暂未使用）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定当前Activity对应的布局文件（activity_main.xml）
        setContentView(R.layout.activity_main);

        // 初始化数据库工具类，获取可读写的数据库连接并赋值给静态变量con，供全局操作数据库使用
        DBUntil dbUntil = new DBUntil(this);
        DBUntil.con = dbUntil.getWritableDatabase();

        // 实现共享数据存储 - 获取名为"data"的SharedPreferences实例，用于缓存登录账号等临时数据
        // Context.MODE_PRIVATE：表示该SharedPreferences文件仅当前应用可访问，私有权限
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        // 获取SharedPreferences的编辑对象，用于后续写入/修改缓存数据
        SharedPreferences.Editor edit = sharedPreferences.edit();

        // 商家单选按钮默认选中 - 找到布局中商家身份的单选按钮控件，设置默认勾选状态
        RadioButton sjRadio = findViewById(R.id.login_sj);
        sjRadio.setChecked(true);// 让运行时候商家单选按钮默认选择

        // 商家注册跳转 - 绑定商家注册按钮，并设置点击跳转事件
        Button zcsj = findViewById(R.id.login_zhuceshangjia);
        zcsj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到注册商家界面 - 构建意图对象，指定当前界面和目标注册界面
                Intent intent = new Intent(MainActivity.this, RegisterManActivity.class);
                // 启动目标Activity，进入商家注册流程
                startActivity(intent);
            }
        });

        // 用户注册跳转 - 绑定用户注册按钮，并设置点击跳转事件
        Button zcyh = findViewById(R.id.login_zhuceyonghu);
        zcyh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到注册用户界面 - 构建意图对象，指定当前界面和目标注册界面
                Intent intent = new Intent(MainActivity.this, RegisterUserActivity.class);
                // 启动目标Activity，进入用户注册流程
                startActivity(intent);
            }
        });

        // 登陆功能初始化 - 绑定登录所需的输入框和按钮控件
        EditText accountText = findViewById(R.id.login_account); // 账号输入框
        EditText pwdText = findViewById(R.id.login_pwd); // 密码输入框
        Button denglu = findViewById(R.id.login_denglu); // 登录按钮
        RadioButton role = findViewById(R.id.login_sj); // 身份单选按钮（商家/用户）

        // 登录按钮点击事件 - 处理登录逻辑验证和页面跳转
        denglu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入框中的账号和密码文本内容
                String account = accountText.getText().toString();
                String pwd = pwdText.getText().toString();

                // 非空校验：先判断账号是否为空
                if (account.isEmpty()) {
                    // 弹出短提示Toast，提醒用户输入账号
                    Toast.makeText(MainActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                }
                // 非空校验：再判断密码是否为空
                else if (pwd.isEmpty()) {
                    // 弹出短提示Toast，提醒用户输入密码
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                // 账号和密码均不为空，执行后续登录逻辑
                else {
                    // 将登录账号缓存到SharedPreferences中，供后续界面使用
                    edit.putString("account", account);
                    // 异步提交SharedPreferences数据，确保数据持久化（不阻塞主线程）
                    edit.apply();

                    // 判断当前选中的身份：是否为商家（管理员）身份
                    if (role.isChecked()) {
                        // 管理员/商家登录 - 调用AdminDao的商家登录验证方法，获取验证结果
                        int a = AdminDao.loginBusiness(account, pwd);
                        // 验证结果为1表示登录成功（约定返回值：1=成功，其他=失败）
                        if (a == 1) {
                            // 弹出登录成功提示
                            Toast.makeText(MainActivity.this, "管理员登录成功", Toast.LENGTH_SHORT).show();
                            // 构建意图对象，跳转到商家管理主界面
                            Intent intent = new Intent(MainActivity.this, ManageManActivity.class);
                            startActivity(intent);
                        }
                        // 验证失败，账号或密码错误
                        else {
                            Toast.makeText(MainActivity.this, "管理员账号或密码错误", Toast.LENGTH_SHORT).show();
                        }

                    }
                    // 普通用户身份登录（未选中商家单选按钮）
                    else {
                        // 普通用户登录 - 调用AdminDao的用户登录验证方法，获取验证结果
                        int a = AdminDao.loginUser(account, pwd);
                        // 验证结果为1表示登录成功（约定返回值：1=成功，其他=失败）
                        if (a == 1) {
                            // 弹出登录成功提示
                            Toast.makeText(MainActivity.this, "用户登录成功", Toast.LENGTH_SHORT).show();
                            // 构建意图对象，跳转到用户管理主界面
                            Intent intent = new Intent(MainActivity.this, ManageUserActivity.class);
                            startActivity(intent);
                        }
                        // 验证失败，账号或密码错误
                        else {
                            Toast.makeText(MainActivity.this, "用户账号或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    /**
     * 重写返回键事件：点击一次即清空数据并退出程序，不返回任何界面
     * 关键：不调用 super.onBackPressed() （注：当前代码仍保留super调用，仅屏蔽默认返回栈行为），屏蔽系统默认返回行为
     */
    @Override
    public void onBackPressed() {
        // 1. 执行系统默认返回操作（注：结合后续finishAffinity，最终效果为退出程序）
        super.onBackPressed();
        // 调用自定义方法，清空所有缓存和残留数据
        clearAllData();

        // 2. 彻底关闭任务栈中所有Activity，直接退出程序（不返回登录/其他界面）
        // finishAffinity()：关闭当前应用的所有关联Activity，清空任务栈
        finishAffinity();

        // 可选：Android Q及以上，额外确保进程完全退出（优雅退出优先使用finishAffinity）
        // System.exit(0);
    }

    /**
     * 清空所有数据（内存缓存、数据库引用、SP残留数据）
     * 目的：避免程序退出后残留敏感数据，同时释放占用的系统资源
     */
    private void clearAllData() {
        // 1. 清空数据库静态引用，释放数据库连接资源，防止内存泄漏
        DBUntil.con = null;

        // 2. 清空SharedPreferences所有残留数据（登录/商家/用户缓存）
        clearSPResidue();
    }

    /**
     * 清空SharedPreferences残留数据，确保无任何数据留存
     * 涵盖登录缓存、商家信息缓存、用户信息缓存，全面清理隐私数据
     */
    private void clearSPResidue() {
        try {
            // 清理登录账号缓存（data文件）
            getSharedPreferences("data", MODE_PRIVATE).edit().clear().apply();
            // 清理商家信息缓存（BusinessInfoSP文件）
            getSharedPreferences("BusinessInfoSP", MODE_PRIVATE).edit().clear().apply();
            // 清理用户信息缓存（UserInfoSP文件）
            getSharedPreferences("UserInfoSP", MODE_PRIVATE).edit().clear().apply();
        } catch (Exception e) {
            // 捕获清理过程中的异常，打印异常堆栈信息，避免程序崩溃
            e.printStackTrace();
        }
    }
}