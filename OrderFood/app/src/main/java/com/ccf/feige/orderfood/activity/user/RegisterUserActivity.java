package com.ccf.feige.orderfood.activity.user;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.RegisterManActivity;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.until.FileImgUntil;

/**
 * 用户注册页面（优化版）
 * 优化点：
 * 1. 解决类型转换、空指针崩溃风险
 * 2. 优化性别逻辑，修正初始值矛盾
 * 3. 表单验证封装，简化冗余代码
 * 4. 图片保存/数据库操作异步化，避免ANR
 * 5. 注册成功后延迟返回登录页，提升用户体验
 * 6. 移除冗余代码，定义常量，提升可维护性
 * 修复：未完成注册退出后再次进入崩溃问题（统一退出清理、任务取消、脏数据清理）
 * 新增：密码确认验证功能（与商家注册保持一致）
 */
public class RegisterUserActivity extends AppCompatActivity {
    // 常量定义（避免魔法值，便于维护）
    private static final String IMAGE_MIME_TYPE = "image/*";
    private static final int REGISTER_SUCCESS = 1;
    private static final long DELAY_MILLIS = 500; // 注册成功后延迟返回时间（半秒）
    private static final int TASK_CANCELLED = -2; // 异步任务被取消的标记
    private static final int PWD_MIN_LENGTH = 6; // 密码最小长度（统一规则）

    // 组件声明
    private ActivityResultLauncher<String> getContentLauncher;
    private Uri avatarUri; // 头像Uri，语义更清晰
    private String userSex; // 用户性别
    private UserRegisterAsyncTask mUserRegisterTask;

    // 视图组件
    private ImageView ivUserAvatar;
    private EditText etUserAccount;
    private EditText etUserPwd;
    // 【新增点1】声明确认密码输入框组件（与布局id对应，建议为register_user_pwd_confirm）
    private EditText etUserPwdConfirm;
    private EditText etUserName;
    private RadioGroup rgUserSex;
    private RadioButton rbUserMale;
    private RadioButton rbUserFemale;
    private EditText etUserAddress;
    private EditText etUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // 初始化Toolbar（返回功能）
        initToolbar();
        // 初始化视图组件
        initViews();
        // 初始化图片选择器
        initImagePicker();
        // 初始化性别选择逻辑
        initSexSelect();
        // 初始化注册按钮点击事件
        initRegisterButton();
    }

    /**
     * 初始化Toolbar，实现返回功能
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.register_user_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> cleanAndFinish());
    }

    /**
     * 初始化所有视图组件，集中管理findViewById
     */
    private void initViews() {
        // 头像组件
        ivUserAvatar = findViewById(R.id.register_user_tx);
        // 表单输入组件
        etUserAccount = findViewById(R.id.register_user_account);
        etUserPwd = findViewById(R.id.register_user_pwd);
        // 【新增点2】初始化确认密码输入框（与布局中新增的id对应）
        etUserPwdConfirm = findViewById(R.id.register_user_pwd_confirm);
        etUserName = findViewById(R.id.register_user_name);
        rgUserSex = findViewById(R.id.register_user_sex_group);
        rbUserMale = findViewById(R.id.register_user_nan);
        rbUserFemale = findViewById(R.id.register_user_nv);
        etUserAddress = findViewById(R.id.register_user_address);
        etUserPhone = findViewById(R.id.register_user_phone);

        // 初始化头像Tag（标记是否选择自定义头像，避免像素对比）
        ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
        // 头像点击事件：打开相册选择图片
        ivUserAvatar.setOnClickListener(v -> openGallery());

        // 初始化性别（修正原逻辑矛盾，男性默认选中，对应性别为“男”）
        userSex = "男";
        rbUserMale.setChecked(true);
    }

    /**
     * 初始化图片选择器（ActivityResultLauncher）
     */
    private void initImagePicker() {
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        if (result != null) {
                            avatarUri = result;
                            ivUserAvatar.setImageURI(avatarUri);
                            ivUserAvatar.setTag(R.id.tag_avatar_selected, true);
                        } else {
                            showToast("未选择头像");
                            ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
                        }
                    }
                }
        );
    }

    /**
     * 初始化性别选择逻辑，修正初始值矛盾，添加选中监听
     */
    private void initSexSelect() {
        // 单选按钮组监听，实时更新性别，避免仅在注册时判断
        rgUserSex.setOnCheckedChangeListener((group, checkedId) -> {
            if (group == null) {
                return;
            }
            if (checkedId == R.id.register_user_nan) {
                userSex = "男";
            } else if (checkedId == R.id.register_user_nv) {
                userSex = "女";
            }
        });
    }

    /**
     * 初始化注册按钮点击事件，包含表单验证和异步注册逻辑
     */
    private void initRegisterButton() {
        Button btnRegister = findViewById(R.id.register_user_zcyh);
        btnRegister.setOnClickListener(v -> performUserRegister());
    }

    /**
     * 执行用户注册流程：表单验证 -> 异步保存图片和数据
     */
    private void performUserRegister() {
        // 获取表单输入值，去除首尾空格，避免无效输入
        String userAccount = etUserAccount.getText().toString().trim();
        String userPwd = etUserPwd.getText().toString().trim();
        // 【新增点3】获取确认密码输入值
        String userPwdConfirm = etUserPwdConfirm.getText().toString().trim();
        String userName = etUserName.getText().toString().trim();
        String userAddress = etUserAddress.getText().toString().trim();
        String userPhone = etUserPhone.getText().toString().trim();

        // 【修改点1】传递确认密码到表单验证方法
        if (!validateForm(userAccount, userPwd, userPwdConfirm, userName, userAddress, userPhone)) {
            return;
        }

        // 取消已有未完成任务，再创建新任务
        if (mUserRegisterTask != null && !mUserRegisterTask.isCancelled()) {
            mUserRegisterTask.cancel(true);
        }
        // 异步执行图片保存和数据库注册（避免主线程阻塞）
        mUserRegisterTask = new UserRegisterAsyncTask(
                userAccount, userPwd, userName, userSex, userAddress, userPhone
        );
        mUserRegisterTask.execute();
    }

    /**
     * 表单合法性验证（封装后，简化冗余逻辑，增加密码确认校验）
     * @return true-验证通过，false-验证失败
     */
    // 【修改点2】增加确认密码参数（userPwdConfirm）
    private boolean validateForm(String account, String pwd, String pwdConfirm,
                                 String name, String address, String phone) {
        // 1. 验证头像是否已选择（通过Tag标记，高效无风险）
        if (isDefaultAvatar()) {
            showToast("请点击图片添加用户头像");
            return false;
        }

        // 2. 非空验证
        if (TextUtils.isEmpty(account)) {
            showToast("请输入用户账号");
            return false;
        }
        if (TextUtils.isEmpty(pwd)) {
            showToast("请输入用户密码");
            return false;
        }
        // 【新增点4】确认密码非空验证
        if (TextUtils.isEmpty(pwdConfirm)) {
            showToast("请确认用户密码");
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            showToast("请输入用户昵称");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            showToast("请输入用户住址");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            showToast("请输入用户联系方式");
            return false;
        }

        // 3. 长度校验
        if (pwd.length() < PWD_MIN_LENGTH) {
            showToast("密码长度不能少于" + PWD_MIN_LENGTH + "位");
            return false;
        }
        // 【新增点5】确认密码长度校验（与原密码保持一致规则）
        if (pwdConfirm.length() < PWD_MIN_LENGTH) {
            showToast("确认密码长度不能少于" + PWD_MIN_LENGTH + "位");
            return false;
        }
        if (phone.length() != 11) { // 简单的手机号长度校验
            showToast("请输入有效的11位手机号码");
            return false;
        }

        // 【新增点6】核心逻辑 - 两次密码一致性校验
        if (!pwd.equals(pwdConfirm)) {
            showToast("两次输入的密码不一致，请重新输入");
            return false;
        }

        return true;
    }

    /**
     * Java原生异步任务：处理图片保存+数据库用户注册（避免主线程IO阻塞）
     */
    private class UserRegisterAsyncTask extends AsyncTask<Void, Void, Integer> {
        private String account, pwd, name, sex, address, phone;
        private String errorMsg;

        public UserRegisterAsyncTask(String account, String pwd, String name, String sex, String address, String phone) {
            this.account = account;
            this.pwd = pwd;
            this.name = name;
            this.sex = sex;
            this.address = address;
            this.phone = phone;
        }

        // 子线程执行耗时操作（图片保存+数据库操作）
        @Override
        protected Integer doInBackground(Void... voids) {
            if (isCancelled()) {
                return TASK_CANCELLED;
            }
            try {
                // 1. 保存头像图片（添加取消判断，避免无效操作）
                String imgPath = FileImgUntil.getImgName();
                if (avatarUri != null && !isCancelled()) {
                    FileImgUntil.saveImageBitmapToFileImg(avatarUri, RegisterUserActivity.this, imgPath);
                }

                // 2. 调用Dao保存用户数据到数据库（添加取消判断）
                if (isCancelled()) {
                    return TASK_CANCELLED;
                }
                return AdminDao.saveCommonUser(account, pwd, name, sex, address, phone, imgPath);
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = "注册失败，发生未知错误";
                return -1; // 失败标记
            }
        }

        // 主线程更新UI（Toast提示+延迟返回登录页）
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (isFinishing() || isDestroyed()) {
                return;
            }
            // 任务被取消则不处理UI
            if (result == TASK_CANCELLED) {
                mUserRegisterTask = null;
                return;
            }
            if (result == REGISTER_SUCCESS) {
                showToast("注册用户成功");
                // 延迟半秒后返回用户登录页，提升用户体验
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 方式2：明确跳转到登录界面（推荐，避免上一级不是登录页的问题）
                        Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
                        startActivity(intent);
                        // 注册完成后调用统一清理退出方法，保持逻辑一致
                        cleanAndFinish();
                    }
                }, DELAY_MILLIS);
            } else if (result == -1) {
                showToast(errorMsg);
            } else {
                showToast("注册用户失败，账号冲突");
            }
            // 任务执行完成，置空任务引用
            mUserRegisterTask = null;
        }
    }

    /**
     * 打开相册选择图片（移除无用参数，简化调用）
     */
    private void openGallery() {
        if (getContentLauncher == null) {
            return;
        }
        getContentLauncher.launch(IMAGE_MIME_TYPE);
    }

    /**
     * 判断是否为默认头像（通过Tag标记，替代像素对比，无崩溃风险且高效）
     * @return true-默认头像（未选择），false-已选择自定义头像
     */
    private boolean isDefaultAvatar() {
        if (ivUserAvatar == null) {
            return true;
        }
        Object tag = ivUserAvatar.getTag(R.id.tag_avatar_selected);
        // 容错处理：避免Tag为null导致的类型转换异常
        if (tag == null) {
            ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
            return true;
        }
        return !(Boolean) tag;
    }

    /**
     * 安全获取Drawable对应的Bitmap（解决ClassCastException，支持所有Drawable类型）
     * @param drawable 待转换的Drawable
     * @return 转换后的Bitmap，避免空指针
     */
    private Bitmap getSafeBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        // 处理BitmapDrawable类型
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap != null ? bitmap : Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        // 处理VectorDrawable、ColorDrawable等其他类型，转换为Bitmap
        try {
            int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 200;
            int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 200;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
    }

    /**
     * 封装Toast提示，避免重复代码，提升可维护性
     * @param msg 提示文本
     */
    private void showToast(String msg) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 封装统一的清理资源并退出方法（核心修复）
     */
    private void cleanAndFinish() {
        // 步骤1：取消未完成的注册异步任务，避免子线程操作已销毁的Activity
        if (mUserRegisterTask != null && !mUserRegisterTask.isCancelled()) {
            mUserRegisterTask.cancel(true);
            mUserRegisterTask = null;
        }

        // 步骤2：清理所有EditText的脏数据，避免再次进入时残留
        clearAllEditTexts();

        // 步骤3：重置头像状态，释放图片资源
        resetAvatarState();

        // 步骤4：重置性别状态，恢复默认值
        resetSexState();

        // 步骤5：置空组件引用，帮助GC回收，避免内存泄漏
        unbindViews();

        // 步骤6：正常关闭当前页面
        finish();
    }

    // 辅助方法：清理所有输入框内容
    private void clearAllEditTexts() {
        if (etUserAccount != null) etUserAccount.setText("");
        if (etUserPwd != null) etUserPwd.setText("");
        // 【新增点7】清理确认密码输入框内容
        if (etUserPwdConfirm != null) etUserPwdConfirm.setText("");
        if (etUserName != null) etUserName.setText("");
        if (etUserAddress != null) etUserAddress.setText("");
        if (etUserPhone != null) etUserPhone.setText("");
    }

    // 辅助方法：重置头像状态
    private void resetAvatarState() {
        avatarUri = null;
        if (ivUserAvatar != null) {
            ivUserAvatar.setImageResource(android.R.color.transparent); // 清空图片，可替换为你的默认用户头像
            ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
            // 释放图片Bitmap
            Drawable drawable = ivUserAvatar.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    // 辅助方法：重置性别状态，恢复默认值
    private void resetSexState() {
        userSex = "男";
        if (rbUserMale != null) {
            rbUserMale.setChecked(true);
        }
        if (rbUserFemale != null) {
            rbUserFemale.setChecked(false);
        }
    }

    // 辅助方法：置空组件引用
    private void unbindViews() {
        ivUserAvatar = null;
        etUserAccount = null;
        etUserPwd = null;
        // 【新增点8】置空确认密码输入框引用
        etUserPwdConfirm = null;
        etUserName = null;
        rgUserSex = null;
        rbUserMale = null;
        rbUserFemale = null;
        etUserAddress = null;
        etUserPhone = null;
        getContentLauncher = null;
    }

    /**
     * 重写物理返回键事件，调用统一清理退出方法
     */
    @Override
    public void onBackPressed() {
        cleanAndFinish();
    }

    /**
     * 页面销毁时释放资源，避免内存泄漏和OOM
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 再次强化资源释放，双重保障
        // 取消未完成任务
        if (mUserRegisterTask != null && !mUserRegisterTask.isCancelled()) {
            mUserRegisterTask.cancel(true);
            mUserRegisterTask = null;
        }
        // 释放Bitmap资源
        Drawable drawable = ivUserAvatar != null ? ivUserAvatar.getDrawable() : null;
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        // 清空成员变量，解除引用
        avatarUri = null;
        unbindViews();
    }
}