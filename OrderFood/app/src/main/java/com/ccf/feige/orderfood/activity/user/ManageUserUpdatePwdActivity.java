package com.ccf.feige.orderfood.activity.user;

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

public class ManageUserUpdatePwdActivity extends AppCompatActivity {

    // 声明所有输入框控件（新增原密码输入框）
    private EditText etOldPwd; // 原密码
    private EditText etNewPwd; // 新密码
    private EditText etConfirmNewPwd; // 确认新密码
    private String currentAccount; // 当前登录普通用户账号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_update_pwd);

        // 1. 初始化Toolbar，优化导航返回逻辑
        initToolbar();

        // 2. 绑定控件并获取当前登录账号
        initView();

        // 3. 绑定按钮点击事件，实现完整密码修改逻辑（含原密码验证）
        initUpdatePwdListener();
    }

    /**
     * 初始化Toolbar，设置导航图标点击事件（符合Android交互规范，实现返回功能）
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.user_manage_updateBusiness_pwd_bar);
        // 替换原整个Toolbar点击，改为导航图标点击（更合理）
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 方式1：直接关闭当前Activity，返回上一级ManageUserActivity（承载用户对应Fragment）
                finish();

                // 方式2：沿用原有跳转逻辑，跳转后关闭当前Activity（二选一即可）
                // Intent intent = new Intent(ManageUserUpdatePwdActivity.this, ManageUserActivity.class);
                // intent.putExtra("sta", "1");
                // startActivity(intent);
                // finish();
            }
        });
    }

    /**
     * 绑定所有输入框控件，获取当前登录普通用户账号
     */
    private void initView() {
        // 绑定新增的原密码输入框（与布局中的id对应）
        etOldPwd = findViewById(R.id.user_manage_updateBusiness_pwd_oldPwd);
        // 绑定原有新密码、确认密码输入框
        etNewPwd = findViewById(R.id.user_manage_updateBusiness_pwd_pwd);
        etConfirmNewPwd = findViewById(R.id.user_manage_updateBusiness_pwd_confirmPwd);

        // 获取当前登录普通用户账号（沿用项目原有工具类方法）
        currentAccount = Tools.getOnAccount(this);
    }

    /**
     * 初始化修改密码按钮点击事件，实现完整校验逻辑（适配普通用户）
     */
    private void initUpdatePwdListener() {
        Button btnUpdatePwd = findViewById(R.id.user_manage_updateBusiness_pwd_update);
        btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 获取所有输入框内容（去除前后空格，避免空格导致校验错误）
                String oldPwdInput = etOldPwd.getText().toString().trim();
                String newPwdInput = etNewPwd.getText().toString().trim();
                String confirmNewPwdInput = etConfirmNewPwd.getText().toString().trim();

                // 2. 分步校验输入合法性（按「原密码→新密码→确认密码」顺序）
                if (!validateOldPwd(oldPwdInput)) return;
                if (!validateNewPwd(newPwdInput)) return;
                if (!validateConfirmPwd(newPwdInput, confirmNewPwdInput)) return;

                // 3. 所有校验通过，执行普通用户密码更新操作
                executeUserPwdUpdate(newPwdInput);
            }
        });
    }

    /**
     * 校验普通用户原密码：非空 + 与数据库中存储的原密码一致
     * @param oldPwdInput 用户输入的原密码
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateOldPwd(String oldPwdInput) {
        // 步骤1：校验原密码非空
        if (oldPwdInput.isEmpty()) {
            etOldPwd.setError("请输入原密码");
            etOldPwd.requestFocus();
            return false;
        }

        // 步骤2：调用AdminDao已有的getCommonUserPwd方法，获取普通用户数据库中原密码
        String dbOldPwd = AdminDao.getCommonUserPwd(currentAccount);

        // 步骤3：校验原密码是否正确（处理查询返回null的情况，避免空指针）
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

        return true;
    }

    /**
     * 校验普通用户新密码：非空（可扩展密码复杂度）
     * @param newPwdInput 用户输入的新密码
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateNewPwd(String newPwdInput) {
        if (newPwdInput.isEmpty()) {
            etNewPwd.setError("请输入新密码");
            etNewPwd.requestFocus();
            return false;
        }

        // 可选扩展：新密码长度校验（如≥6位）
        // if (newPwdInput.length() < 6) {
        //     etNewPwd.setError("新密码长度不能少于6位");
        //     etNewPwd.requestFocus();
        //     return false;
        // }

        return true;
    }

    /**
     * 校验普通用户确认新密码：非空 + 与新密码一致
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

        return true;
    }

    /**
     * 执行普通用户密码更新操作，成功后返回对应Fragment
     * @param newPwd 校验通过的新密码
     */
    private void executeUserPwdUpdate(String newPwd) {
        // 调用AdminDao已有的updateCommentUserPwd方法（沿用你项目中的方法名，注意拼写）
        int updateResult = AdminDao.updateCommentUserPwd(currentAccount, newPwd);

        if (updateResult == 1) {
            Toast.makeText(ManageUserUpdatePwdActivity.this, "更改密码成功", Toast.LENGTH_SHORT).show();
            // 核心：关闭当前Activity，返回承载用户对应Fragment（如ManageMyFragment）的ManageUserActivity
            finish();
        } else {
            Toast.makeText(ManageUserUpdatePwdActivity.this, "更改密码失败", Toast.LENGTH_SHORT).show();
        }
    }
}