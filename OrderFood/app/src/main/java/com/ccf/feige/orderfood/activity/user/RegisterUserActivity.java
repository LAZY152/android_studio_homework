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
 * 4. 图片保存/数据库操作异步化，避免ANR（Application Not Responding）
 * 5. 注册成功后延迟返回登录页，提升用户体验
 * 6. 移除冗余代码，定义常量，提升可维护性
 * 修复：未完成注册退出后再次进入崩溃问题（统一退出清理、任务取消、脏数据清理）
 * 新增：密码确认验证功能（与商家注册保持一致）
 */
public class RegisterUserActivity extends AppCompatActivity {
    // ===================== 常量定义区（避免魔法值，便于统一维护和修改） =====================
    /** 图片选择的MIME类型，指定仅选择图片文件 */
    private static final String IMAGE_MIME_TYPE = "image/*";
    /** 注册成功的返回标记（与AdminDao.saveCommonUser返回值对应） */
    private static final int REGISTER_SUCCESS = 1;
    /** 注册成功后延迟返回的时间（毫秒），半秒延迟提升用户感知体验 */
    private static final long DELAY_MILLIS = 500;
    /** 异步任务被取消的标记，用于区分任务失败和任务取消 */
    private static final int TASK_CANCELLED = -2;
    /** 密码最小长度限制，统一用户和商家注册的密码规则 */
    private static final int PWD_MIN_LENGTH = 6;

    // ===================== 成员变量声明区（全局可访问，统一管理核心数据和组件） =====================
    /** 相册图片选择器，替代传统的startActivityForResult，兼容AndroidX */
    private ActivityResultLauncher<String> getContentLauncher;
    /** 用户选择的头像Uri，用于后续图片保存和展示 */
    private Uri avatarUri; // 语义更清晰，替代原有的模糊命名
    /** 用户选择的性别，默认值为"男" */
    private String userSex;
    /** 用户注册异步任务实例，用于管理任务的创建、执行和取消 */
    private UserRegisterAsyncTask mUserRegisterTask;

    // ===================== 视图组件声明区（所有页面控件统一声明，便于初始化和管理） =====================
    /** 头像展示ImageView */
    private ImageView ivUserAvatar;
    /** 账号输入框 */
    private EditText etUserAccount;
    /** 密码输入框 */
    private EditText etUserPwd;
    // 【新增点1】声明确认密码输入框组件（与布局id对应，建议为register_user_pwd_confirm）
    private EditText etUserPwdConfirm;
    /** 用户名（昵称）输入框 */
    private EditText etUserName;
    /** 性别选择单选按钮组 */
    private RadioGroup rgUserSex;
    /** 男性单选按钮 */
    private RadioButton rbUserMale;
    /** 女性单选按钮 */
    private RadioButton rbUserFemale;
    /** 住址输入框 */
    private EditText etUserAddress;
    /** 手机号码输入框 */
    private EditText etUserPhone;

    // ===================== 生命周期方法（页面创建到销毁的核心流程） =====================
    /**
     * 页面创建时执行，初始化页面所有资源和逻辑
     * @param savedInstanceState 页面重建时的保存状态，此处未使用
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载页面布局文件
        setContentView(R.layout.activity_register_user);

        // 分步初始化，解耦各模块逻辑，便于维护和排查问题
        initToolbar(); // 初始化顶部导航栏
        initViews(); // 初始化所有视图组件
        initImagePicker(); // 初始化相册图片选择器
        initSexSelect(); // 初始化性别选择逻辑
        initRegisterButton(); // 初始化注册按钮点击事件
    }

    // ===================== 初始化方法区（分步初始化，职责单一） =====================
    /**
     * 初始化Toolbar，实现返回上一级页面的功能
     * 替代传统的ActionBar，更灵活的页面导航栏配置
     */
    private void initToolbar() {
        // 绑定布局中的Toolbar控件
        Toolbar toolbar = findViewById(R.id.register_user_toolbar);
        // 将Toolbar设置为当前页面的ActionBar
        setSupportActionBar(toolbar);
        // 设置Toolbar左侧返回按钮的点击事件，点击时执行统一的清理退出逻辑
        toolbar.setNavigationOnClickListener(v -> cleanAndFinish());
    }

    /**
     * 初始化所有视图组件，集中管理findViewById，避免代码分散
     * 同时初始化组件的默认状态和基础点击事件
     */
    private void initViews() {
        // 绑定头像组件
        ivUserAvatar = findViewById(R.id.register_user_tx);
        // 绑定表单输入组件
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

        // 初始化头像Tag（自定义标记，用于判断是否选择了自定义头像）
        // 替代传统的像素对比方式，高效且无崩溃风险，R.id.tag_avatar_selected为自定义Tag id
        ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
        // 头像点击事件：绑定打开相册的逻辑
        ivUserAvatar.setOnClickListener(v -> openGallery());

        // 初始化性别（修正原逻辑矛盾，男性默认选中，对应性别为“男”，保持界面和数据一致）
        userSex = "男";
        rbUserMale.setChecked(true);
    }

    /**
     * 初始化图片选择器（ActivityResultLauncher）
     * 兼容Android 11及以上版本，替代废弃的startActivityForResult和onActivityResult
     */
    private void initImagePicker() {
        // 注册图片选择结果回调，指定选择内容为“文件”，回调返回Uri类型
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), // 选择手机中的文件/内容
                new ActivityResultCallback<Uri>() { // 选择结果的回调接口
                    /**
                     * 相册选择完成后的回调方法
                     * @param result 选择的图片Uri，未选择时为null
                     */
                    @Override
                    public void onActivityResult(Uri result) {
                        // 安全判断：如果页面正在销毁或已销毁，直接返回，避免操作无效组件
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        // 处理选择结果
                        if (result != null) {
                            // 保存选择的图片Uri
                            avatarUri = result;
                            // 展示选择的图片到头像ImageView
                            ivUserAvatar.setImageURI(avatarUri);
                            // 更新Tag标记，标记为已选择自定义头像
                            ivUserAvatar.setTag(R.id.tag_avatar_selected, true);
                        } else {
                            // 未选择图片，给出提示并重置Tag标记
                            showToast("未选择头像");
                            ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
                        }
                    }
                }
        );
    }

    /**
     * 初始化性别选择逻辑，修正初始值矛盾，添加选中状态监听
     * 实现单选按钮组的实时状态更新，避免仅在注册时判断性别
     */
    private void initSexSelect() {
        // 为单选按钮组设置选中状态改变监听器
        rgUserSex.setOnCheckedChangeListener((group, checkedId) -> {
            // 安全判断：避免单选按钮组为null导致的空指针异常
            if (group == null) {
                return;
            }
            // 根据选中的按钮ID更新性别数据
            if (checkedId == R.id.register_user_nan) {
                // 选中男性，更新性别为“男”
                userSex = "男";
            } else if (checkedId == R.id.register_user_nv) {
                // 选中女性，更新性别为“女”
                userSex = "女";
            }
        });
    }

    /**
     * 初始化注册按钮点击事件，绑定用户注册的核心流程
     */
    private void initRegisterButton() {
        // 绑定注册按钮控件
        Button btnRegister = findViewById(R.id.register_user_zcyh);
        // 设置按钮点击事件，执行用户注册流程
        btnRegister.setOnClickListener(v -> performUserRegister());
    }

    // ===================== 核心业务逻辑方法区（用户注册的核心流程） =====================
    /**
     * 执行用户注册流程：表单验证 -> 异步保存图片和数据
     * 职责单一，仅负责串联注册流程，不处理具体的验证和存储逻辑
     */
    private void performUserRegister() {
        // 获取表单输入值，调用trim()去除首尾空格，避免无效的空白输入
        String userAccount = etUserAccount.getText().toString().trim();
        String userPwd = etUserPwd.getText().toString().trim();
        // 【新增点3】获取确认密码输入值
        String userPwdConfirm = etUserPwdConfirm.getText().toString().trim();
        String userName = etUserName.getText().toString().trim();
        String userAddress = etUserAddress.getText().toString().trim();
        String userPhone = etUserPhone.getText().toString().trim();

        // 【修改点1】传递确认密码到表单验证方法，进行一致性校验
        // 表单验证不通过，直接返回，终止注册流程
        if (!validateForm(userAccount, userPwd, userPwdConfirm, userName, userAddress, userPhone)) {
            return;
        }

        // 取消已有未完成的异步任务，避免多个任务同时执行导致数据混乱
        if (mUserRegisterTask != null && !mUserRegisterTask.isCancelled()) {
            mUserRegisterTask.cancel(true);
        }
        // 创建新的异步任务，传入表单数据，执行耗时的图片保存和数据库存储
        mUserRegisterTask = new UserRegisterAsyncTask(
                userAccount, userPwd, userName, userSex, userAddress, userPhone
        );
        // 执行异步任务
        mUserRegisterTask.execute();
    }

    /**
     * 表单合法性验证（封装后，简化冗余逻辑，增加密码确认校验）
     * 所有表单验证规则集中在此方法，便于维护和修改
     * @param account 账号
     * @param pwd 密码
     * @param pwdConfirm 确认密码
     * @param name 用户名（昵称）
     * @param address 住址
     * @param phone 手机号码
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

        // 2. 非空验证：逐一校验必填项是否为空
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

        // 3. 长度校验：校验密码和手机号码的格式合法性
        if (pwd.length() < PWD_MIN_LENGTH) {
            showToast("密码长度不能少于" + PWD_MIN_LENGTH + "位");
            return false;
        }
        // 【新增点5】确认密码长度校验（与原密码保持一致规则）
        if (pwdConfirm.length() < PWD_MIN_LENGTH) {
            showToast("确认密码长度不能少于" + PWD_MIN_LENGTH + "位");
            return false;
        }
        if (phone.length() != 11) { // 简单的手机号长度校验，可扩展为正则表达式校验
            showToast("请输入有效的11位手机号码");
            return false;
        }

        // 【新增点6】核心逻辑 - 两次密码一致性校验
        if (!pwd.equals(pwdConfirm)) {
            showToast("两次输入的密码不一致，请重新输入");
            return false;
        }

        // 所有验证规则通过，返回true
        return true;
    }

    // ===================== 内部异步任务类（处理耗时操作，避免主线程阻塞） =====================
    /**
     * Java原生异步任务：处理图片保存+数据库用户注册（避免主线程IO阻塞导致ANR）
     * 继承AsyncTask，泛型参数说明：
     * 第一个参数：doInBackground的输入参数类型（此处为Void，无输入参数）
     * 第二个参数：onProgressUpdate的输入参数类型（此处未使用进度更新，为Void）
     * 第三个参数：doInBackground的返回值类型，也是onPostExecute的输入参数类型（此处为Integer，标记执行结果）
     */
    private class UserRegisterAsyncTask extends AsyncTask<Void, Void, Integer> {
        // 表单数据缓存，避免异步任务执行过程中表单数据被修改
        private String account, pwd, name, sex, address, phone;
        // 异常信息缓存，用于传递异步任务中的错误信息到主线程
        private String errorMsg;

        /**
         * 构造方法，初始化表单数据
         * @param account 账号
         * @param pwd 密码
         * @param name 用户名（昵称）
         * @param sex 性别
         * @param address 住址
         * @param phone 手机号码
         */
        public UserRegisterAsyncTask(String account, String pwd, String name, String sex, String address, String phone) {
            this.account = account;
            this.pwd = pwd;
            this.name = name;
            this.sex = sex;
            this.address = address;
            this.phone = phone;
        }

        /**
         * 子线程执行耗时操作（图片保存+数据库操作）
         * 此方法运行在子线程，不能更新UI
         * @param voids 无输入参数
         * @return 执行结果标记（REGISTER_SUCCESS/TASK_CANCELLED/-1/其他）
         */
        @Override
        protected Integer doInBackground(Void... voids) {
            // 任务被取消，直接返回取消标记
            if (isCancelled()) {
                return TASK_CANCELLED;
            }
            try {
                // 1. 保存头像图片（添加取消判断，避免无效操作）
                // 获取图片保存的文件名（由FileImgUntil工具类生成）
                String imgPath = FileImgUntil.getImgName();
                // 头像Uri不为空且任务未被取消时，执行图片保存
                if (avatarUri != null && !isCancelled()) {
                    FileImgUntil.saveImageBitmapToFileImg(avatarUri, RegisterUserActivity.this, imgPath);
                }

                // 2. 调用Dao保存用户数据到数据库（添加取消判断）
                if (isCancelled()) {
                    return TASK_CANCELLED;
                }
                // 调用AdminDao的静态方法，保存用户数据到数据库，返回执行结果
                return AdminDao.saveCommonUser(account, pwd, name, sex, address, phone, imgPath);
            } catch (Exception e) {
                // 捕获异常，打印异常堆栈，缓存错误信息
                e.printStackTrace();
                errorMsg = "注册失败，发生未知错误";
                return -1; // 失败标记
            }
        }

        /**
         * 主线程更新UI（Toast提示+延迟返回登录页）
         * 此方法运行在主线程，可更新UI，接收doInBackground的返回结果
         * @param result 子线程执行结果标记
         */
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            // 安全判断：页面正在销毁或已销毁，直接返回
            if (isFinishing() || isDestroyed()) {
                return;
            }
            // 任务被取消则不处理UI，直接置空任务引用
            if (result == TASK_CANCELLED) {
                mUserRegisterTask = null;
                return;
            }
            // 根据执行结果处理不同的UI逻辑
            if (result == REGISTER_SUCCESS) {
                // 注册成功，给出提示
                showToast("注册用户成功");
                // 延迟半秒后返回用户登录页，提升用户体验
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 明确跳转到登录界面（推荐，避免上一级不是登录页的问题）
                        Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
                        startActivity(intent);
                        // 注册完成后调用统一清理退出方法，保持逻辑一致
                        cleanAndFinish();
                    }
                }, DELAY_MILLIS);
            } else if (result == -1) {
                // 未知异常导致注册失败，展示错误信息
                showToast(errorMsg);
            } else {
                // 账号冲突等其他原因导致注册失败
                showToast("注册用户失败，账号冲突");
            }
            // 任务执行完成，置空任务引用，帮助GC回收
            mUserRegisterTask = null;
        }
    }

    // ===================== 辅助方法区（工具类方法，职责单一，便于复用） =====================
    /**
     * 打开相册选择图片（移除无用参数，简化调用）
     */
    private void openGallery() {
        // 安全判断：图片选择器未初始化，直接返回
        if (getContentLauncher == null) {
            return;
        }
        // 启动相册选择器，指定选择的文件类型为图片
        getContentLauncher.launch(IMAGE_MIME_TYPE);
    }

    /**
     * 判断是否为默认头像（通过Tag标记，替代像素对比，无崩溃风险且高效）
     * @return true-默认头像（未选择自定义头像），false-已选择自定义头像
     */
    private boolean isDefaultAvatar() {
        // 安全判断：头像ImageView未初始化，返回true（视为未选择头像）
        if (ivUserAvatar == null) {
            return true;
        }
        // 获取头像ImageView的Tag标记
        Object tag = ivUserAvatar.getTag(R.id.tag_avatar_selected);
        // 容错处理：避免Tag为null导致的类型转换异常
        if (tag == null) {
            // 重置Tag标记为false，返回true（视为未选择头像）
            ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
            return true;
        }
        // 转换Tag类型为Boolean，返回取反结果（Tag为false时，视为默认头像）
        return !(Boolean) tag;
    }

    /**
     * 安全获取Drawable对应的Bitmap（解决ClassCastException，支持所有Drawable类型）
     * 避免直接转换Drawable为BitmapDrawable导致的类型转换异常
     * @param drawable 待转换的Drawable
     * @return 转换后的Bitmap，避免空指针
     */
    private Bitmap getSafeBitmapFromDrawable(Drawable drawable) {
        // Drawable为null，返回一个空的Bitmap
        if (drawable == null) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        // 处理BitmapDrawable类型（直接获取内部的Bitmap）
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap != null ? bitmap : Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        // 处理VectorDrawable、ColorDrawable等其他类型，通过Canvas绘制转换为Bitmap
        try {
            // 获取Drawable的固有宽高，无固有宽高时使用默认值200
            int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 200;
            int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 200;
            // 创建空白Bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 创建Canvas，关联空白Bitmap
            Canvas canvas = new Canvas(bitmap);
            // 设置Drawable的绘制边界
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            // 将Drawable绘制到Canvas上，从而转换为Bitmap
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            // 捕获绘制异常，打印堆栈，返回空Bitmap
            e.printStackTrace();
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
    }

    /**
     * 封装Toast提示，避免重复代码，提升可维护性
     * 增加页面状态判断，避免在页面销毁时弹出Toast导致的崩溃
     * @param msg 提示文本
     */
    private void showToast(String msg) {
        // 安全判断：页面正在销毁或已销毁，不弹出Toast
        if (isFinishing() || isDestroyed()) {
            return;
        }
        // 弹出短时长Toast提示
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // ===================== 资源清理和退出方法区（解决内存泄漏和崩溃问题） =====================
    /**
     * 封装统一的清理资源并退出方法（核心修复：解决未完成注册退出后再次进入崩溃问题）
     * 按顺序清理资源，避免遗漏导致的内存泄漏或无效操作
     */
    private void cleanAndFinish() {
        // 步骤1：取消未完成的注册异步任务，避免子线程操作已销毁的Activity
        if (mUserRegisterTask != null && !mUserRegisterTask.isCancelled()) {
            mUserRegisterTask.cancel(true);
            mUserRegisterTask = null;
        }

        // 步骤2：清理所有EditText的脏数据，避免再次进入时残留上一次的输入内容
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

    /**
     * 辅助方法：清理所有输入框内容
     */
    private void clearAllEditTexts() {
        if (etUserAccount != null) etUserAccount.setText("");
        if (etUserPwd != null) etUserPwd.setText("");
        // 【新增点7】清理确认密码输入框内容
        if (etUserPwdConfirm != null) etUserPwdConfirm.setText("");
        if (etUserName != null) etUserName.setText("");
        if (etUserAddress != null) etUserAddress.setText("");
        if (etUserPhone != null) etUserPhone.setText("");
    }

    /**
     * 辅助方法：重置头像状态，释放图片资源
     */
    private void resetAvatarState() {
        // 置空头像Uri
        avatarUri = null;
        if (ivUserAvatar != null) {
            // 清空头像展示，替换为透明背景（可替换为项目默认用户头像）
            ivUserAvatar.setImageResource(android.R.color.transparent);
            // 重置Tag标记，恢复为未选择头像状态
            ivUserAvatar.setTag(R.id.tag_avatar_selected, false);
            // 释放图片Bitmap资源，避免OOM（Out Of Memory）
            Drawable drawable = ivUserAvatar.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    /**
     * 辅助方法：重置性别状态，恢复默认值
     */
    private void resetSexState() {
        // 恢复性别默认值为“男”
        userSex = "男";
        if (rbUserMale != null) {
            rbUserMale.setChecked(true);
        }
        if (rbUserFemale != null) {
            rbUserFemale.setChecked(false);
        }
    }

    /**
     * 辅助方法：置空组件引用，帮助GC回收
     */
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

    // ===================== 重写系统方法（处理页面导航和销毁） =====================
    /**
     * 重写物理返回键事件，调用统一清理退出方法
     * 保证用户点击物理返回键时，也能执行完整的资源清理逻辑
     */
    @Override
    public void onBackPressed() {
        cleanAndFinish();
    }

    /**
     * 页面销毁时释放资源，避免内存泄漏和OOM
     * 双重保障：在页面销毁时再次强化资源释放，避免遗漏
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