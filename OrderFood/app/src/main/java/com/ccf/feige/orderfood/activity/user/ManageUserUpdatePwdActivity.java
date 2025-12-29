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

/**
 * 普通用户密码修改页面Activity
 * 功能：提供原密码验证、新密码输入与确认，完成普通用户的密码更新操作
 * 依赖：AdminDao（数据访问层）、Tools（工具类）、对应的布局文件
 */
public class ManageUserUpdatePwdActivity extends AppCompatActivity {

    // 声明所有输入框控件（新增原密码输入框）
    private EditText etOldPwd; // 原密码输入框，用于接收用户输入的旧密码
    private EditText etNewPwd; // 新密码输入框，用于接收用户输入的新密码
    private EditText etConfirmNewPwd; // 确认新密码输入框，用于二次验证新密码
    private String currentAccount; // 当前登录普通用户账号，用于关联数据库中的用户信息

    /**
     * Activity生命周期方法：创建Activity时调用
     * 负责初始化页面布局、Toolbar、控件绑定和事件监听
     * @param savedInstanceState 保存的Activity状态数据，用于页面重建时恢复状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前Activity对应的布局文件，建立Java代码与XML布局的关联
        setContentView(R.layout.activity_manage_user_update_pwd);

        // 1. 初始化Toolbar，优化导航返回逻辑（符合Android交互规范）
        initToolbar();

        // 2. 绑定布局中的所有控件，并获取当前登录的普通用户账号
        initView();

        // 3. 绑定修改密码按钮的点击事件，实现完整的密码修改业务逻辑（含原密码验证、新密码校验等）
        initUpdatePwdListener();
    }

    /**
     * 初始化Toolbar，设置导航图标点击事件（符合Android交互规范，实现返回上一级页面功能）
     * 替代原有的整个Toolbar点击逻辑，仅绑定导航图标（左侧返回箭头）的点击事件，交互更合理
     */
    private void initToolbar() {
        // 从布局中获取Toolbar控件实例，与XML中的id对应
        Toolbar toolbar = findViewById(R.id.user_manage_updateBusiness_pwd_bar);
        // 为Toolbar的导航图标设置点击监听器
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 方式1：直接关闭当前Activity，返回上一级ManageUserActivity（承载用户对应Fragment）
                // 优点：简洁高效，保留上一级页面的原有状态
                finish();

                // 方式2：沿用原有跳转逻辑，显式跳转至ManageUserActivity并关闭当前Activity（二选一即可）
                // 备注：该方式会重新创建ManageUserActivity，可能丢失原有页面状态
                // Intent intent = new Intent(ManageUserUpdatePwdActivity.this, ManageUserActivity.class);
                // intent.putExtra("sta", "1"); // 携带页面状态参数，与原有业务逻辑保持一致
                // startActivity(intent); // 启动目标Activity
                // finish(); // 关闭当前Activity，避免返回栈中残留
            }
        });
    }

    /**
     * 绑定所有输入框控件，获取当前登录普通用户账号
     * 完成控件与Java代码的关联，以及用户账号的初始化，为后续密码修改逻辑提供数据支撑
     */
    private void initView() {
        // 绑定新增的原密码输入框（与布局文件中的控件id一一对应，避免找不到控件异常）
        etOldPwd = findViewById(R.id.user_manage_updateBusiness_pwd_oldPwd);
        // 绑定原有新密码输入框（与布局文件中的控件id对应）
        etNewPwd = findViewById(R.id.user_manage_updateBusiness_pwd_pwd);
        // 绑定原有确认新密码输入框（与布局文件中的控件id对应）
        etConfirmNewPwd = findViewById(R.id.user_manage_updateBusiness_pwd_confirmPwd);

        // 获取当前登录普通用户账号（沿用项目原有工具类Tools的方法，保证与项目整体逻辑一致）
        // 从本地存储（如SharedPreferences）中读取已登录的用户账号
        currentAccount = Tools.getOnAccount(this);
    }

    /**
     * 初始化修改密码按钮点击事件，实现完整校验逻辑（适配普通用户）
     * 点击按钮后，将依次执行：输入内容获取→原密码校验→新密码校验→确认密码校验→密码更新
     */
    private void initUpdatePwdListener() {
        // 从布局中获取修改密码按钮控件实例
        Button btnUpdatePwd = findViewById(R.id.user_manage_updateBusiness_pwd_update);
        // 为修改密码按钮设置点击监听器，处理点击事件
        btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 获取所有输入框内容（调用trim()方法去除前后空格，避免因用户误输入空格导致校验错误）
                String oldPwdInput = etOldPwd.getText().toString().trim();
                String newPwdInput = etNewPwd.getText().toString().trim();
                String confirmNewPwdInput = etConfirmNewPwd.getText().toString().trim();

                // 2. 分步校验输入合法性（按「原密码→新密码→确认密码」顺序，校验不通过则直接返回，终止后续流程）
                // 原密码校验：非空 + 与数据库存储一致
                if (!validateOldPwd(oldPwdInput)) return;
                // 新密码校验：非空（可扩展复杂度）
                if (!validateNewPwd(newPwdInput)) return;
                // 确认密码校验：非空 + 与新密码一致
                if (!validateConfirmPwd(newPwdInput, confirmNewPwdInput)) return;

                // 3. 所有校验通过，执行普通用户密码更新操作（传入校验通过的新密码）
                executeUserPwdUpdate(newPwdInput);
            }
        });
    }

    /**
     * 校验普通用户原密码：非空校验 + 与数据库中存储的原密码一致性校验
     * 校验不通过时，为对应输入框设置错误提示并获取焦点，提升用户体验
     * @param oldPwdInput 用户输入的原密码（已去除前后空格）
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateOldPwd(String oldPwdInput) {
        // 步骤1：校验原密码非空，若为空则提示错误并获取焦点
        if (oldPwdInput.isEmpty()) {
            etOldPwd.setError("请输入原密码"); // 为输入框设置错误提示文本
            etOldPwd.requestFocus(); // 让输入框获取焦点，方便用户重新输入
            return false; // 返回false，终止校验流程
        }

        // 步骤2：调用AdminDao已有的getCommonUserPwd方法，根据当前账号从数据库中查询原密码
        // AdminDao：项目数据访问层，负责与数据库进行交互，封装了用户相关的查询和更新方法
        String dbOldPwd = AdminDao.getCommonUserPwd(currentAccount);

        // 步骤3：校验原密码是否正确（先处理查询返回null的情况，避免空指针异常）
        if (dbOldPwd == null) {
            // 数据库中查询不到该账号对应的密码，提示账号异常
            etOldPwd.setError("账号不存在或已注销");
            etOldPwd.requestFocus();
            return false;
        }
        // 对比用户输入的原密码与数据库中存储的原密码，不一致则提示错误
        if (!dbOldPwd.equals(oldPwdInput)) {
            etOldPwd.setError("原密码输入错误，请重新输入");
            etOldPwd.requestFocus();
            return false;
        }

        // 所有原密码校验条件均满足，返回true
        return true;
    }

    /**
     * 校验普通用户新密码：基础非空校验（可根据业务需求扩展密码复杂度校验）
     * 校验不通过时，为对应输入框设置错误提示并获取焦点
     * @param newPwdInput 用户输入的新密码（已去除前后空格）
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateNewPwd(String newPwdInput) {
        // 校验新密码非空，若为空则提示错误并获取焦点
        if (newPwdInput.isEmpty()) {
            etNewPwd.setError("请输入新密码");
            etNewPwd.requestFocus();
            return false;
        }

        // 可选扩展：新密码长度校验（如≥6位），根据项目业务需求开启
        // if (newPwdInput.length() < 6) {
        //     etNewPwd.setError("新密码长度不能少于6位");
        //     etNewPwd.requestFocus();
        //     return false;
        // }

        // 可选扩展：密码复杂度校验（如包含字母、数字、特殊字符），根据项目安全需求开启
        // 新密码校验通过，返回true
        return true;
    }

    /**
     * 校验普通用户确认新密码：非空校验 + 与新密码一致性校验
     * 校验不通过时，为对应输入框设置错误提示并获取焦点
     * @param newPwdInput 已通过校验的新密码
     * @param confirmNewPwdInput 用户输入的确认新密码（已去除前后空格）
     * @return 校验通过返回true，否则返回false
     */
    private boolean validateConfirmPwd(String newPwdInput, String confirmNewPwdInput) {
        // 步骤1：校验确认新密码非空，若为空则提示错误并获取焦点
        if (confirmNewPwdInput.isEmpty()) {
            etConfirmNewPwd.setError("请输入确认密码");
            etConfirmNewPwd.requestFocus();
            return false;
        }

        // 步骤2：校验确认新密码与新密码是否一致，不一致则提示错误
        if (!confirmNewPwdInput.equals(newPwdInput)) {
            etConfirmNewPwd.setError("两次输入的新密码不一致，请重新输入");
            etConfirmNewPwd.requestFocus();
            return false;
        }

        // 确认密码校验通过，返回true
        return true;
    }

    /**
     * 执行普通用户密码更新操作，调用数据访问层方法修改数据库中的用户密码
     * 更新结果反馈给用户，并在成功后返回上一级页面
     * @param newPwd 已通过所有校验的新密码
     */
    private void executeUserPwdUpdate(String newPwd) {
        // 调用AdminDao已有的updateCommentUserPwd方法（注意方法名拼写，与项目原有逻辑保持一致）
        // 传入当前用户账号和新密码，执行数据库密码更新操作，返回更新结果（1=成功，其他=失败）
        int updateResult = AdminDao.updateCommentUserPwd(currentAccount, newPwd);

        // 根据更新结果给用户展示对应的提示信息
        if (updateResult == 1) {
            // 密码更新成功，提示用户并关闭当前Activity，返回上一级页面
            Toast.makeText(ManageUserUpdatePwdActivity.this, "更改密码成功", Toast.LENGTH_SHORT).show();
            // 核心：关闭当前Activity，返回承载用户对应Fragment（如ManageMyFragment）的ManageUserActivity
            // 保留上一级页面的原有状态，提升用户体验
            finish();
        } else {
            // 密码更新失败，提示用户（可根据返回码扩展具体失败原因提示）
            Toast.makeText(ManageUserUpdatePwdActivity.this, "更改密码失败", Toast.LENGTH_SHORT).show();
        }
    }
}