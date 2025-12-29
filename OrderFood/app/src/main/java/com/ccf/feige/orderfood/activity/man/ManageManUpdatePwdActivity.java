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

/**
 * 商家管理端 - 密码修改页面Activity
 * 功能：实现商家登录后修改自身登录密码，包含原密码验证、新密码校验、密码更新入库等完整流程
 */
public class ManageManUpdatePwdActivity extends AppCompatActivity {

    // 声明所有输入框控件
    private EditText etOldPwd; // 原密码输入框
    private EditText etNewPwd; // 新密码输入框
    private EditText etConfirmNewPwd; // 确认新密码输入框
    private String currentAccount; // 当前登录商家账号（用于关联数据库中的用户信息）

    /**
     * Activity生命周期创建方法
     * 页面初始化入口，完成Toolbar、控件、点击事件的初始化工作
     * @param savedInstanceState 保存的Activity状态数据（本次页面未使用）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定当前Activity对应的布局文件
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
     * 遵循Android交互规范，仅给导航图标设置点击事件，而非整个Toolbar
     */
    private void initToolbar() {
        // 从布局中获取Toolbar控件实例
        Toolbar toolbar = findViewById(R.id.man_manage_updateBusiness_pwd_bar);
        // 给Toolbar的导航返回图标设置点击监听器
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭当前修改密码Activity，自动返回上一级承载ManageMyFragment的Activity
                // 上一级Activity会保留之前的页面状态，无需额外传递数据
                finish();
            }
        });
    }

    /**
     * 绑定所有输入框控件，获取当前登录商家账号
     * 完成控件与布局id的映射，以及当前登录账号的获取（沿用项目现有工具类）
     */
    private void initView() {
        // 绑定新增的原密码输入框（与布局中的id一一对应，避免控件找不到异常）
        etOldPwd = findViewById(R.id.man_manage_updateBusiness_pwd_oldPwd);
        // 绑定原有新密码、确认密码输入框（复用项目已有布局控件）
        etNewPwd = findViewById(R.id.man_manage_updateBusiness_pwd_pwd);
        etConfirmNewPwd = findViewById(R.id.man_manage_updateBusiness_pwd_confirmPwd);

        // 获取当前登录账号（调用项目工具类Tools的静态方法，从本地存储中获取已登录账号）
        currentAccount = Tools.getOnAccount(this);
    }

    /**
     * 初始化修改密码按钮点击事件，实现完整校验逻辑
     * 点击修改按钮后，按流程执行输入内容获取、分步校验、密码更新操作
     */
    private void initUpdatePwdListener() {
        // 从布局中获取修改密码按钮控件实例
        Button btnUpdatePwd = findViewById(R.id.man_manage_updateBusiness_pwd_update);
        // 给修改密码按钮设置点击监听器
        btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 获取所有输入框内容（调用trim()去除前后空格，避免空格导致的校验误判）
                String oldPwdInput = etOldPwd.getText().toString().trim();
                String newPwdInput = etNewPwd.getText().toString().trim();
                String confirmNewPwdInput = etConfirmNewPwd.getText().toString().trim();

                // 2. 分步校验输入合法性（按「原密码→新密码→确认密码」顺序，符合用户操作习惯，校验失败则直接返回）
                if (!validateOldPwd(oldPwdInput)) return;
                if (!validateNewPwd(newPwdInput)) return;
                if (!validateConfirmPwd(newPwdInput, confirmNewPwdInput)) return;

                // 3. 所有校验通过，执行密码更新操作（传入合规的新密码）
                executePwdUpdate(newPwdInput);
            }
        });
    }

    /**
     * 校验原密码：非空 + 与数据库中存储的原密码一致
     * 分步校验，给出精准错误提示，并聚焦到对应输入框，提升用户体验
     * @param oldPwdInput 用户输入的原密码
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateOldPwd(String oldPwdInput) {
        // 步骤1：校验原密码非空，避免空输入提交
        if (oldPwdInput.isEmpty()) {
            etOldPwd.setError("请输入原密码");
            etOldPwd.requestFocus(); // 聚焦到原密码输入框，方便用户立即补填
            return false;
        }

        // 步骤2：调用AdminDao已有的getBusinessUserPwd方法，从数据库中查询当前账号对应的原密码
        String dbOldPwd = AdminDao.getBusinessUserPwd(currentAccount);

        // 步骤3：校验原密码是否正确（处理数据库查询返回null的情况，避免空指针异常）
        if (dbOldPwd == null) {
            etOldPwd.setError("账号不存在或已注销");
            etOldPwd.requestFocus();
            return false;
        }
        // 字符串内容比对，判断输入的原密码与数据库存储的密码是否一致
        if (!dbOldPwd.equals(oldPwdInput)) {
            etOldPwd.setError("原密码输入错误，请重新输入");
            etOldPwd.requestFocus();
            return false;
        }

        // 原密码各项校验均通过
        return true;
    }

    /**
     * 校验新密码：非空（可扩展：密码长度、复杂度等）
     * 预留扩展接口，方便后续升级密码安全要求
     * @param newPwdInput 用户输入的新密码
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateNewPwd(String newPwdInput) {
        // 校验新密码非空，避免空密码提交
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
     * 防止用户输入新密码时手滑出错，保证两次输入的新密码一致
     * @param newPwdInput 新密码（已通过校验的合规新密码）
     * @param confirmNewPwdInput 确认新密码（用户二次输入的新密码）
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateConfirmPwd(String newPwdInput, String confirmNewPwdInput) {
        // 校验确认密码非空
        if (confirmNewPwdInput.isEmpty()) {
            etConfirmNewPwd.setError("请输入确认密码");
            etConfirmNewPwd.requestFocus();
            return false;
        }

        // 比对新密码与确认密码是否一致，不一致则给出错误提示
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
     * 调用数据访问层方法完成数据库更新，根据更新结果给出用户提示
     * @param newPwd 校验通过的新密码（已确保合规、无空格）
     */
    private void executePwdUpdate(String newPwd) {
        // 调用AdminDao已有的updateBusinessUserPwd方法，更新数据库中当前账号对应的密码
        // 返回值int类型：1表示更新成功，其他值表示更新失败
        int updateResult = AdminDao.updateBusinessUserPwd(currentAccount, newPwd);

        if (updateResult == 1) {
            // 密码更新成功，给出友好提示
            Toast.makeText(ManageManUpdatePwdActivity.this, "更改密码成功", Toast.LENGTH_SHORT).show();

            // 核心：关闭当前Activity，返回承载ManageMyFragment的上一级Activity
            // 此时上一级Activity会自动显示之前的ManageMyFragment，实现无缝“返回”效果，无需额外跳转
            finish();
        } else {
            // 密码更新失败（如数据库异常、账号不存在等），给出错误提示
            Toast.makeText(ManageManUpdatePwdActivity.this, "更改密码失败", Toast.LENGTH_SHORT).show();
        }
    }
}