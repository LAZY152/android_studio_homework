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
    // 根视图（修正拼写错误，规范命名）
    private View rootView;
    // 用户信息相关控件（规范命名，语义清晰）
    private ImageView ivUserAvatar;
    private TextView tvUserAccount;
    private TextView tvUserName;
    private TextView tvUserAddress;
    // 功能入口相关控件
    private TextView tvExitSystem; // 退出账号（原退出系统）
    private TextView tvLogout; // 注销账号
    private TextView tvChangePassword;
    private TextView tvChangeInfo;
    private TextView tvViewOrder;
    private TextView tvManageAddress;

    // 当前登录账号（全局保存，用于注销验证和删除数据）
    private String currentAccount;
    // 当前用户信息（全局保存，避免重复查询）
    private UserCommonBean currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局，初始化根视图
        rootView = inflater.inflate(R.layout.fragment_manage_user_my, container, false);

        // 初始化所有控件（集中管理，避免分散findViewById）
        initViews();

        // 加载并展示用户信息
        loadUserInfo();

        // 初始化所有控件的点击事件
        initViewClickEvents();

        return rootView;
    }

    /**
     * 初始化所有视图控件，规范命名，便于维护
     */
    private void initViews() {
        // 用户信息控件
        ivUserAvatar = rootView.findViewById(R.id.user_manage_user_my_tx);
        tvUserAccount = rootView.findViewById(R.id.user_manage_user_my_account);
        tvUserName = rootView.findViewById(R.id.user_manage_user_my_name);
        tvUserAddress = rootView.findViewById(R.id.user_manage_user_my_address);

        // 功能入口控件
        tvExitSystem = rootView.findViewById(R.id.user_manage_user_my_exit);
        tvLogout = rootView.findViewById(R.id.user_manage_user_my_zx);
        tvChangePassword = rootView.findViewById(R.id.user_manage_user_my_changePwd);
        tvChangeInfo = rootView.findViewById(R.id.user_manage_user_my_changeMes);
        tvViewOrder = rootView.findViewById(R.id.user_manage_user_my_order);
        tvManageAddress = rootView.findViewById(R.id.user_manage_user_my_res_address);
    }

    /**
     * 加载用户信息（含容错处理，避免空指针崩溃）
     */
    private void loadUserInfo() {
        // 1. 获取当前登录账号（判空，避免无效查询）
        currentAccount = Tools.getOnAccount(getContext());
        if (currentAccount == null || currentAccount.trim().isEmpty()) {
            showToast("未获取到登录账号，请重新登录");
            return;
        }

        // 2. 查询用户信息（判空，避免查询结果为null导致崩溃）
        currentUser = AdminDao.getCommonUser(currentAccount);
        if (currentUser == null) {
            showToast("用户信息查询失败，请重新登录");
            return;
        }

        // 3. 展示用户账号与基本信息
        tvUserAccount.setText(currentAccount);
        tvUserName.setText(currentUser.getsName() != null ? currentUser.getsName() : "未知用户");
        tvUserAddress.setText("住址:" + (currentUser.getsAddress() != null ? currentUser.getsAddress() : "未填写"));

        // 4. 加载并展示用户头像（含容错与压缩，避免OOM和崩溃）
        loadUserAvatar(currentUser.getsImg());
    }

    /**
     * 加载用户头像（含文件判空、图片压缩、容错处理）
     * @param imgPath 头像文件路径
     */
    private void loadUserAvatar(String imgPath) {
        // 判空：路径为null/空或文件不存在
        if (imgPath == null || imgPath.trim().isEmpty() || !new File(imgPath).exists()) {
            // 设置默认头像，避免ImageView为空
            ivUserAvatar.setImageResource(R.drawable.upimg);
            return;
        }

        // 图片压缩：避免大图片导致OOM
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取图片尺寸，不加载到内存
        BitmapFactory.decodeFile(imgPath, options);

        // 计算压缩比例（宽高均不超过500px，可根据需求调整）
        int reqWidth = 500;
        int reqHeight = 500;
        int inSampleSize = 1;
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        // 加载压缩后的图片
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // 指定图片配置，减少内存占用

        Bitmap avatarBitmap = BitmapFactory.decodeFile(imgPath, options);
        // 判空：图片加载失败则设置默认头像
        ivUserAvatar.setImageBitmap(avatarBitmap != null ? avatarBitmap : BitmapFactory.decodeResource(getResources(), R.drawable.upimg));
    }

    /**
     * 初始化所有控件的点击事件（集中管理，逻辑清晰）
     */
    private void initViewClickEvents() {
        // 1. 退出账号（修改：返回登录界面）
        tvExitSystem.setOnClickListener(v -> exitAccount());

        // 2. 注销账号（修改：弹出密码验证弹窗）
        tvLogout.setOnClickListener(v -> showLogoutPwdDialog());

        // 3. 修改密码
        tvChangePassword.setOnClickListener(v -> jumpToActivity(ManageUserUpdatePwdActivity.class));

        // 4. 修改个人信息
        tvChangeInfo.setOnClickListener(v -> jumpToActivity(ManageUserUpdateMesActivity.class));

        // 5. 查看订单
        tvViewOrder.setOnClickListener(v -> showUserOrder());

        // 6. 管理收货地址
        tvManageAddress.setOnClickListener(v -> jumpToActivity(ManageUserAddressActivity.class));
    }

    // ---------------------- 核心修改1：退出账号返回登录界面 ----------------------
    /**
     * 退出账号：关闭当前用户中心，跳转到登录界面
     */
    private void exitAccount() {
        if (getContext() == null) return;
        // 1. 跳转到用户登录界面
        Intent loginIntent = new Intent(getContext(), MainActivity.class);
        // 标记：清除返回栈，避免用户返回当前页面
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);

        // 2. 关闭当前宿主Activity（ManageUserActivity）
        if (getActivity() != null) {
            getActivity().finish();
        }

        showToast("已退出账号");
    }

    // ---------------------- 核心修改2：注销账号密码验证弹窗 ----------------------
    /**
     * 显示注销账号密码验证弹窗
     */
    private void showLogoutPwdDialog() {
        if (currentUser == null || getContext() == null) {
            showToast("用户信息异常，无法注销");
            return;
        }

        // 1. 加载弹窗布局
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_user_logout, null);

        // 2. 获取弹窗控件
        EditText etLogoutPwd = dialogView.findViewById(R.id.et_logout_pwd);
        Button btnCancel = dialogView.findViewById(R.id.btn_logout_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_logout_confirm);

        // 3. 构建弹窗
        AlertDialog logoutDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false) // 点击外部不关闭弹窗
                .create();

        // 4. 取消按钮点击事件
        btnCancel.setOnClickListener(v -> logoutDialog.dismiss());

        // 5. 确认注销按钮点击事件
        btnConfirm.setOnClickListener(v -> {
            // 5.1 获取输入的密码并判空
            String inputPwd = etLogoutPwd.getText().toString().trim();
            if (inputPwd.isEmpty()) {
                showToast("请输入账号密码");
                return;
            }

            // 5.2 获取数据库中保存的用户密码
            String userPwd = AdminDao.getCommonUserPwd(currentAccount);
            if (userPwd == null) {
                showToast("用户密码查询失败");
                logoutDialog.dismiss();
                return;
            }

            // 5.3 验证密码是否一致
            if (!inputPwd.equals(userPwd)) {
                showToast("密码输入错误，请重新输入");
                etLogoutPwd.setText(""); // 清空输入框
                return;
            }

            // 5.4 密码验证通过，删除数据库用户数据
            boolean isDeleteSuccess = AdminDao.deleteCommonUser(currentAccount);
            if (isDeleteSuccess) {
                showToast("账号注销成功");
                // 5.5 跳转到登录界面，清除返回栈
                Intent loginIntent = new Intent(getContext(), MainActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);

                // 5.6 关闭当前弹窗和宿主Activity
                logoutDialog.dismiss();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                showToast("账号注销失败，请重试");
                logoutDialog.dismiss();
            }
        });

        // 6. 显示弹窗
        logoutDialog.show();
    }

    /**
     * 展示用户订单（调用宿主Activity的方法，含空指针容错）
     */
    private void showUserOrder() {
        if (getActivity() instanceof ManageUserActivity) {
            ManageUserActivity hostActivity = (ManageUserActivity) getActivity();
            hostActivity.showOrder();
        } else {
            showToast("无法查看订单，请重试");
        }
    }

    /**
     * 通用页面跳转方法（封装Intent，减少冗余代码，含上下文容错）
     * @param targetActivity 目标Activity的Class
     */
    private void jumpToActivity(Class<?> targetActivity) {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), targetActivity);
        startActivity(intent);
    }

    /**
     * 封装Toast提示（含上下文容错，减少冗余代码）
     * @param msg 提示文本
     */
    private void showToast(String msg) {
        if (getContext() == null) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Fragment销毁时释放资源，避免内存泄漏
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 回收头像Bitmap资源，避免OOM
        if (ivUserAvatar != null) {
            ivUserAvatar.setImageBitmap(null);
        }
        // 清空控件与全局变量引用，解除内存绑定
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