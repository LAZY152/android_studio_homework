package com.ccf.feige.orderfood.activity.man;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.until.Tools;

public class ManageManUpdatePwdActivity extends AppCompatActivity {

    // 声明所有输入框控件
    private EditText etOldPwd; // 原密码
    private EditText etNewPwd; // 新密码
    private EditText etConfirmNewPwd; // 确认新密码
    private String currentAccount; // 当前登录商家账号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_man_update_pwd);

        // 1. 初始化Toolbar并设置导航返回逻辑（点击返回箭头关闭当前Activity，返回ManageMyFragment）
        initToolbar();

        // 2. 绑定控件并获取当前登录账号
        initView();

        // 3. 绑定按钮点击事件，实现密码修改完整逻辑（含原密码验证）
        initUpdatePwdListener();
    }

    /**
     * 初始化Toolbar，设置导航图标点击事件（返回上一级，即承载ManageMyFragment的Activity）
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.man_manage_updateBusiness_pwd_bar);
        // 注意：给Toolbar的导航图标设置点击事件（而非整个Toolbar），符合Android交互规范
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭当前修改密码Activity，自动返回上一级Activity（该Activity承载ManageMyFragment）
                finish();
            }
        });
    }

    /**
     * 绑定所有输入框控件，获取当前登录商家账号
     */
    private void initView() {
        // 绑定新增的原密码输入框（与布局中的id对应）
        etOldPwd = findViewById(R.id.man_manage_updateBusiness_pwd_oldPwd);
        // 绑定原有新密码、确认密码输入框
        etNewPwd = findViewById(R.id.man_manage_updateBusiness_pwd_pwd);
        etConfirmNewPwd = findViewById(R.id.man_manage_updateBusiness_pwd_confirmPwd);

        // 获取当前登录账号（沿用项目原有工具类方法）
        currentAccount = Tools.getOnAccount(this);
    }

    /**
     * 初始化修改密码按钮点击事件，实现完整校验逻辑
     */
    private void initUpdatePwdListener() {
        Button btnUpdatePwd = findViewById(R.id.man_manage_updateBusiness_pwd_update);
        btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 获取所有输入框内容（去除前后空格，避免空格导致的校验错误）
                String oldPwdInput = etOldPwd.getText().toString().trim();
                String newPwdInput = etNewPwd.getText().toString().trim();
                String confirmNewPwdInput = etConfirmNewPwd.getText().toString().trim();

                // 2. 分步校验输入合法性（按「原密码→新密码→确认密码」顺序，符合用户操作习惯）
                if (!validateOldPwd(oldPwdInput)) return;
                if (!validateNewPwd(newPwdInput)) return;
                if (!validateConfirmPwd(newPwdInput, confirmNewPwdInput)) return;

                // 3. 所有校验通过，执行密码更新操作
                executePwdUpdate(newPwdInput);
            }
        });
    }

    /**
     * 校验原密码：非空 + 与数据库中存储的原密码一致
     * @param oldPwdInput 用户输入的原密码
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateOldPwd(String oldPwdInput) {
        // 步骤1：校验原密码非空
        if (oldPwdInput.isEmpty()) {
            etOldPwd.setError("请输入原密码");
            etOldPwd.requestFocus(); // 聚焦到原密码输入框，方便用户立即补填
            return false;
        }

        // 步骤2：调用AdminDao已有的getBusinessUserPwd方法，获取数据库中存储的原密码
        String dbOldPwd = AdminDao.getBusinessUserPwd(currentAccount);

        // 步骤3：校验原密码是否正确（处理数据库查询返回null的情况，避免空指针）
        if (dbOldPwd == null) {
            etOldPwd.setError("账号不存在或已注销");
            etOldPwd.requestFocus();
            return false;
        }
        if (!dbOldPwd.equals(oldPwdInput)) {
            etOldPwd.setError("原密码输入错误，请重新输入");
            etOldPwd.requestFocus();
            return false;
        }

        // 原密码校验通过
        return true;
    }

    /**
     * 校验新密码：非空（可扩展：密码长度、复杂度等）
     * @param newPwdInput 用户输入的新密码
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateNewPwd(String newPwdInput) {
        if (newPwdInput.isEmpty()) {
            etNewPwd.setError("请输入新密码");
            etNewPwd.requestFocus();
            return false;
        }

        // 可选扩展：添加密码复杂度校验（如长度≥6位、包含字母+数字等）
        // if (newPwdInput.length() < 6) {
        //     etNewPwd.setError("新密码长度不能少于6位");
        //     etNewPwd.requestFocus();
        //     return false;
        // }

        // 新密码校验通过
        return true;
    }

    /**
     * 校验确认新密码：非空 + 与新密码一致
     * @param newPwdInput 新密码
     * @param confirmNewPwdInput 确认新密码
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateConfirmPwd(String newPwdInput, String confirmNewPwdInput) {
        if (confirmNewPwdInput.isEmpty()) {
            etConfirmNewPwd.setError("请输入确认密码");
            etConfirmNewPwd.requestFocus();
            return false;
        }

        if (!confirmNewPwdInput.equals(newPwdInput)) {
            etConfirmNewPwd.setError("两次输入的新密码不一致，请重新输入");
            etConfirmNewPwd.requestFocus();
            return false;
        }

        // 确认密码校验通过
        return true;
    }

    /**
     * 执行密码更新操作，更新成功后返回ManageMyFragment
     * @param newPwd 校验通过的新密码
     */
    private void executePwdUpdate(String newPwd) {
        // 调用AdminDao已有的updateBusinessUserPwd方法，更新数据库中的密码
        int updateResult = AdminDao.updateBusinessUserPwd(currentAccount, newPwd);

        if (updateResult == 1) {
            // 密码更新成功
            Toast.makeText(ManageManUpdatePwdActivity.this, "更改密码成功", Toast.LENGTH_SHORT).show();

            // 核心：关闭当前Activity，返回承载ManageMyFragment的上一级Activity
            // 此时上一级Activity会自动显示之前的ManageMyFragment，实现“返回”效果
            finish();
        } else {
            // 密码更新失败
            Toast.makeText(ManageManUpdatePwdActivity.this, "更改密码失败", Toast.LENGTH_SHORT).show();
        }
    }
}