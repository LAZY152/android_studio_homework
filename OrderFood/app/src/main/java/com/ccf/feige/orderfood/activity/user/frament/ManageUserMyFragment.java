package com.ccf.feige.orderfood.activity.user.frament;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.fragment.app.Fragment;

import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.ManageUserActivity;
import com.ccf.feige.orderfood.activity.user.ManageUserAddressActivity;
import com.ccf.feige.orderfood.activity.user.ManageUserUpdateMesActivity;
import com.ccf.feige.orderfood.activity.user.ManageUserUpdatePwdActivity;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.until.Tools;

import java.io.File;

/**
 * 用户个人中心碎片
 * 功能：加载用户信息、头像展示、功能跳转（修改密码/信息、地址管理等）、退出/注销账号
 */
public class ManageUserMyFragment extends Fragment {
    // 根视图（修正拼写错误，规范命名）：承载Fragment所有布局控件的根View，避免重复inflater布局
    private View rootView;
    // 用户信息相关控件（规范命名，语义清晰）：用于展示用户核心个人信息
    private ImageView ivUserAvatar; // 用户头像展示控件
    private TextView tvUserAccount; // 用户账号展示控件
    private TextView tvUserName;   // 用户姓名展示控件
    private TextView tvUserAddress; // 用户住址展示控件
    // 功能入口相关控件：用于触发用户个人中心的各类操作
    private TextView tvExitSystem; // 退出账号（原退出系统）：仅退出当前登录状态，保留账号数据
    private TextView tvLogout;     // 注销账号：永久删除账号及相关数据，不可恢复
    private TextView tvChangePassword; // 修改密码入口：跳转至密码修改页面
    private TextView tvChangeInfo;     // 修改个人信息入口：跳转至用户信息编辑页面
    private TextView tvViewOrder;      // 查看订单入口：调用宿主Activity展示用户订单列表
    private TextView tvManageAddress;  // 地址管理入口：跳转至收货地址编辑/管理页面

    // 当前登录账号（全局保存，用于注销验证和删除数据）：贯穿整个Fragment生命周期，避免重复获取
    private String currentAccount;
    // 当前用户信息（全局保存，避免重复查询）：缓存用户完整信息，减少数据库查询次数
    private UserCommonBean currentUser;

    /**
     * Fragment生命周期方法：创建视图并初始化核心逻辑
     * 执行流程：加载布局 → 初始化控件 → 加载用户信息 → 绑定点击事件
     * @param inflater  布局填充器，用于将xml布局转换为View对象
     * @param container 父容器View，用于承载Fragment的布局（Fragment不会直接添加到该容器，仅用于获取布局参数）
     * @param savedInstanceState  保存状态的Bundle，用于恢复Fragment销毁前的状态（此处未使用）
     * @return  Fragment的根视图rootView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局，初始化根视图：将fragment_manage_user_my.xml布局填充为View对象，不附加到父容器（避免重复添加）
        rootView = inflater.inflate(R.layout.fragment_manage_user_my, container, false);

        // 初始化所有控件（集中管理，避免分散findViewById）：统一查找并绑定布局中的控件，提高代码可维护性
        initViews();

        // 加载并展示用户信息：从本地获取登录账号，查询数据库用户信息，最终展示到界面
        loadUserInfo();

        // 初始化所有控件的点击事件（集中管理，逻辑清晰）：统一绑定控件点击监听，便于后续修改和维护
        initViewClickEvents();

        return rootView;
    }

    /**
     * 初始化所有视图控件，规范命名，便于维护
     * 功能：通过rootView查找布局中所有需要操作的控件，完成控件与成员变量的绑定
     * 说明：按控件功能分类（用户信息控件、功能入口控件），提高代码可读性
     */
    private void initViews() {
        // 用户信息控件：绑定展示用户基础信息的控件
        ivUserAvatar = rootView.findViewById(R.id.user_manage_user_my_tx);
        tvUserAccount = rootView.findViewById(R.id.user_manage_user_my_account);
        tvUserName = rootView.findViewById(R.id.user_manage_user_my_name);
        tvUserAddress = rootView.findViewById(R.id.user_manage_user_my_address);

        // 功能入口控件：绑定触发各类操作的功能控件
        tvExitSystem = rootView.findViewById(R.id.user_manage_user_my_exit);
        tvLogout = rootView.findViewById(R.id.user_manage_user_my_zx);
        tvChangePassword = rootView.findViewById(R.id.user_manage_user_my_changePwd);
        tvChangeInfo = rootView.findViewById(R.id.user_manage_user_my_changeMes);
        tvViewOrder = rootView.findViewById(R.id.user_manage_user_my_order);
        tvManageAddress = rootView.findViewById(R.id.user_manage_user_my_res_address);
    }

    /**
     * 加载用户信息（含容错处理，避免空指针崩溃）
     * 执行流程：获取登录账号 → 查询用户完整信息 → 展示用户基础信息 → 加载用户头像
     * 容错说明：每一步都做判空处理，避免因账号丢失、查询失败导致应用崩溃
     */
    private void loadUserInfo() {
        // 1. 获取当前登录账号（判空，避免无效查询）：通过Tools工具类从本地获取已登录的用户账号
        currentAccount = Tools.getOnAccount(getContext());
        if (currentAccount == null || currentAccount.trim().isEmpty()) {
            showToast("未获取到登录账号，请重新登录");
            return;
        }

        // 2. 查询用户信息（判空，避免查询结果为null导致崩溃）：通过AdminDao数据库工具类查询用户完整信息
        currentUser = AdminDao.getCommonUser(currentAccount);
        if (currentUser == null) {
            showToast("用户信息查询失败，请重新登录");
            return;
        }

        // 3. 展示用户账号与基本信息：为控件设置对应数据，含空值兜底，避免展示null
        tvUserAccount.setText(currentAccount); // 展示用户账号（无需兜底，已做判空）
        tvUserName.setText(currentUser.getsName() != null ? currentUser.getsName() : "未知用户"); // 姓名兜底：未知用户
        tvUserAddress.setText("住址:" + (currentUser.getsAddress() != null ? currentUser.getsAddress() : "未填写")); // 住址兜底：未填写

        // 4. 加载并展示用户头像（含容错）：调用专门的头像加载方法，处理图片加载的各类异常
        loadUserAvatar(currentUser.getsImg());
    }

    /**
     * 加载用户头像（含文件判空、图片压缩、容错处理）
     * 核心功能：安全加载用户本地头像文件，避免OOM和空视图问题
     * @param imgPath 头像文件路径：从用户信息中获取的头像本地存储路径
     */
    private void loadUserAvatar(String imgPath) {
        // 判空：路径为null/空或文件不存在 → 说明用户未设置头像或头像文件损坏
        if (imgPath == null || imgPath.trim().isEmpty() || !new File(imgPath).exists()) {
            // 设置默认头像，避免ImageView为空：使用应用内置的默认头像资源填充
            ivUserAvatar.setImageResource(R.drawable.upimg);
            return;
        }

        // 图片压缩：避免大图片导致OOM（内存溢出）→ 分两步进行图片压缩
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 第一步：仅获取图片尺寸，不加载到内存，避免占用大量内存
        BitmapFactory.decodeFile(imgPath, options); // 解析图片文件，获取图片宽高信息存入options

        // 计算压缩比例（宽高均不超过500px，可根据需求调整）：按2的幂次缩放，保证图片不失真
        int reqWidth = 500; // 目标宽度：500px
        int reqHeight = 500; // 目标高度：500px
        int inSampleSize = 1; // 压缩比例初始值：1（不压缩）
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // 循环计算最优压缩比例：保证压缩后的图片宽高均不小于目标宽高
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        // 加载压缩后的图片：第二步，根据计算出的压缩比例加载图片到内存
        options.inJustDecodeBounds = false; // 关闭仅获取尺寸模式，开始加载图片
        options.inSampleSize = inSampleSize; // 设置压缩比例
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // 指定图片配置，减少内存占用（每个像素占4字节，兼顾清晰度和内存）

        Bitmap avatarBitmap = BitmapFactory.decodeFile(imgPath, options); // 解析并加载压缩后的图片
        // 判空：图片加载失败则设置默认头像 → 避免因图片损坏导致ImageView无内容
        ivUserAvatar.setImageBitmap(avatarBitmap != null ? avatarBitmap : BitmapFactory.decodeResource(getResources(), R.drawable.upimg));
    }

    /**
     * 初始化所有控件的点击事件（集中管理，逻辑清晰）
     * 功能：为所有功能控件绑定点击监听，指定对应的处理方法
     * 说明：按功能分类绑定，便于后续查找和修改对应功能的点击逻辑
     */
    private void initViewClickEvents() {
        // 1. 退出账号（修改：返回登录界面）：绑定退出账号的点击事件，调用exitAccount()方法
        tvExitSystem.setOnClickListener(v -> exitAccount());

        // 2. 注销账号（修改：弹出密码验证弹窗）：绑定注销账号的点击事件，调用showLogoutPwdDialog()方法
        tvLogout.setOnClickListener(v -> showLogoutPwdDialog());

        // 3. 修改密码：绑定修改密码的点击事件，跳转至ManageUserUpdatePwdActivity
        tvChangePassword.setOnClickListener(v -> jumpToActivity(ManageUserUpdatePwdActivity.class));

        // 4. 修改个人信息：绑定修改信息的点击事件，跳转至ManageUserUpdateMesActivity
        tvChangeInfo.setOnClickListener(v -> jumpToActivity(ManageUserUpdateMesActivity.class));

        // 5. 查看订单：绑定查看订单的点击事件，调用showUserOrder()方法
        tvViewOrder.setOnClickListener(v -> showUserOrder());

        // 6. 管理收货地址：绑定地址管理的点击事件，跳转至ManageUserAddressActivity
        tvManageAddress.setOnClickListener(v -> jumpToActivity(ManageUserAddressActivity.class));
    }

    // ---------------------- 核心修改1：退出账号返回登录界面 ----------------------
    /**
     * 退出账号：关闭当前用户中心，跳转到登录界面
     * 核心逻辑：清除返回栈 + 关闭当前宿主Activity + 提示退出成功
     * 说明：退出账号仅清除登录状态，不删除用户任何数据，用户可再次使用该账号登录
     */
    private void exitAccount() {
        if (getContext() == null) return; // 上下文判空，避免空指针
        // 1. 跳转到用户登录界面（MainActivity）
        Intent loginIntent = new Intent(getContext(), MainActivity.class);
        // 标记：清除返回栈，避免用户返回当前页面 → FLAG_ACTIVITY_CLEAR_TOP清除目标Activity之上的所有栈内页面
        // FLAG_ACTIVITY_NEW_TASK创建新任务栈，保证登录界面为栈顶唯一页面
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);

        // 2. 关闭当前宿主Activity（ManageUserActivity）：退出用户中心页面，释放资源
        if (getActivity() != null) {
            getActivity().finish();
        }

        // 3. 提示用户：已成功退出账号
        showToast("已退出账号");
    }

    // ---------------------- 核心修改2：注销账号密码验证弹窗 ----------------------
    /**
     * 显示注销账号密码验证弹窗
     * 核心功能：通过弹窗验证用户密码，验证通过后永久删除用户账号数据
     * 执行流程：加载弹窗布局 → 绑定弹窗控件 → 构建弹窗 → 绑定弹窗按钮事件 → 显示弹窗
     * 安全说明：弹窗不可取消（点击外部不关闭），避免误操作，保证注销流程的严谨性
     */
    private void showLogoutPwdDialog() {
        // 前置判空：用户信息或上下文异常，直接提示无法注销
        if (currentUser == null || getContext() == null) {
            showToast("用户信息异常，无法注销");
            return;
        }

        // 1. 加载弹窗布局：将dialog_user_logout.xml弹窗布局填充为View对象
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_user_logout, null);

        // 2. 获取弹窗控件：查找弹窗布局中的输入框和按钮
        EditText etLogoutPwd = dialogView.findViewById(R.id.et_logout_pwd); // 密码输入框：用于输入用户账号密码
        Button btnCancel = dialogView.findViewById(R.id.btn_logout_cancel); // 取消按钮：放弃注销，关闭弹窗
        Button btnConfirm = dialogView.findViewById(R.id.btn_logout_confirm); // 确认按钮：验证密码并执行注销

        // 3. 构建弹窗：使用AlertDialog.Builder构建弹窗，设置弹窗内容和不可取消属性
        AlertDialog logoutDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView) // 设置弹窗布局
                .setCancelable(false) // 点击外部不关闭弹窗，保证注销流程不被中断
                .create();

        // 4. 取消按钮点击事件：仅关闭弹窗，不执行任何其他操作
        btnCancel.setOnClickListener(v -> logoutDialog.dismiss());

        // 5. 确认注销按钮点击事件：核心注销逻辑，分步执行密码验证和账号删除
        btnConfirm.setOnClickListener(v -> {
            // 5.1 获取输入的密码并判空：去除首尾空格，避免空输入
            String inputPwd = etLogoutPwd.getText().toString().trim();
            if (inputPwd.isEmpty()) {
                showToast("请输入账号密码");
                return;
            }

            // 5.2 获取数据库中保存的用户密码：从数据库查询该账号的原始密码，用于验证
            String userPwd = AdminDao.getCommonUserPwd(currentAccount);
            if (userPwd == null) {
                showToast("用户密码查询失败");
                logoutDialog.dismiss(); // 关闭弹窗，终止注销流程
                return;
            }

            // 5.3 验证密码是否一致：输入密码与数据库原始密码比对，不一致则提示错误
            if (!inputPwd.equals(userPwd)) {
                showToast("密码输入错误，请重新输入");
                etLogoutPwd.setText(""); // 清空输入框，方便用户重新输入
                return;
            }

            // 5.4 密码验证通过，删除数据库用户数据：调用AdminDao删除该账号的所有相关数据
            boolean isDeleteSuccess = AdminDao.deleteCommonUser(currentAccount);
            if (isDeleteSuccess) {
                showToast("账号注销成功");
                // 5.5 跳转到登录界面，清除返回栈：与退出账号逻辑一致，保证无法返回已注销的账号页面
                Intent loginIntent = new Intent(getContext(), MainActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);

                // 5.6 关闭当前弹窗和宿主Activity：释放资源，完成注销流程
                logoutDialog.dismiss();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                showToast("账号注销失败，请重试");
                logoutDialog.dismiss(); // 关闭弹窗，终止注销流程
            }
        });

        // 6. 显示弹窗：将构建完成的弹窗展示给用户
        logoutDialog.show();
    }

    /**
     * 展示用户订单（调用宿主Activity的方法，含空指针容错）
     * 核心逻辑：Fragment无法直接处理订单展示，通过类型判断调用宿主Activity（ManageUserActivity）的showOrder()方法
     * 容错说明：判断宿主Activity是否为指定类型，避免类型转换异常
     */
    private void showUserOrder() {
        // 判断当前宿主Activity是否为ManageUserActivity，避免类型转换错误
        if (getActivity() instanceof ManageUserActivity) {
            ManageUserActivity hostActivity = (ManageUserActivity) getActivity();
            hostActivity.showOrder(); // 调用宿主Activity的订单展示方法
        } else {
            showToast("无法查看订单，请重试"); // 宿主Activity异常，提示用户
        }
    }

    /**
     * 通用页面跳转方法（封装Intent，减少冗余代码，含上下文容错）
     * 功能：封装Activity跳转的通用逻辑，避免重复编写Intent创建和启动代码
     * @param targetActivity 目标Activity的Class：指定要跳转的目标页面
     */
    private void jumpToActivity(Class<?> targetActivity) {
        if (getContext() == null) return; // 上下文判空，避免空指针
        Intent intent = new Intent(getContext(), targetActivity); // 创建跳转Intent
        startActivity(intent); // 启动目标Activity
    }

    /**
     * 封装Toast提示（含上下文容错，减少冗余代码）
     * 功能：封装Toast显示的通用逻辑，避免重复编写Toast.makeText和show()代码
     * @param msg 提示文本：要展示给用户的提示信息
     */
    private void showToast(String msg) {
        if (getContext() == null) return; // 上下文判空，避免空指针
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); // 显示短时长Toast提示
    }

    /**
     * Fragment销毁时释放资源，避免内存泄漏
     * 生命周期说明：该方法在Fragment视图销毁时调用，用于清理资源，解除内存引用
     * 核心操作：回收图片资源 + 清空控件和全局变量引用，避免内存无法被GC回收
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView(); // 调用父类方法，保证生命周期流程完整
        // 回收头像Bitmap资源，避免OOM：清空ImageView的Bitmap引用，释放图片内存
        if (ivUserAvatar != null) {
            ivUserAvatar.setImageBitmap(null);
        }
        // 清空控件与全局变量引用，解除内存绑定：将所有成员变量置为null，避免持有视图或数据引用导致内存泄漏
        rootView = null;
        ivUserAvatar = null;
        tvUserAccount = null;
        tvUserName = null;
        tvUserAddress = null;
        tvExitSystem = null;
        tvLogout = null;
        tvChangePassword = null;
        tvChangeInfo = null;
        tvViewOrder = null;
        tvManageAddress = null;
        currentAccount = null;
        currentUser = null;
    }
}