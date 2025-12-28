package com.ccf.feige.orderfood.activity.user;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.listen.StartListen;
import com.ccf.feige.orderfood.dao.CommentDao;
import com.ccf.feige.orderfood.dao.OrderDao;
import com.ccf.feige.orderfood.until.FileImgUntil;
import com.ccf.feige.orderfood.until.Tools;

/**
 * 订单评论页面（修复数据缺失+评论成功更新订单状态4）
 */
public class ManageUserCommentActivity extends AppCompatActivity {
    // 控件成员变量
    private Toolbar toolbar;
    private EditText etCommentContent;
    private ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;
    private TextView tvScoreDesc;
    private ImageView ivCommentImg;
    private Button btnTakePhoto, btnPickAlbum, btnSubmitComment;

    // 业务参数成员变量（核心：确保非空）
    private String account; // 当前登录用户账号
    private String orderId; // 订单ID（必传，用于更新状态4）
    private String businessId; // 商家ID（必传，用于关联评论）

    // 拍照/相册回调
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    // 评分描述常量（与原有逻辑一致）
    private static final String[] SCORE_DESC_ARRAY = {"非常差", "差", "一般", "满意", "非常满意"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_comment);

        // 1. 初始化控件
        initView();

        // 2. 初始化Toolbar返回事件
        initToolbar();

        // 3. 初始化拍照/相册回调
        initActivityResultLauncher();

        // 4. 初始化控件事件
        initViewListener();

        // 5. 解析并校验参数（核心：解决数据缺失问题）
        parseAndCheckParams();
    }

    /**
     * 初始化控件（与用户原有布局id保持一致）
     */
    private void initView() {
        toolbar = findViewById(R.id.user_manage_comment_bar);
        etCommentContent = findViewById(R.id.user_manage_comment_text);
        ivStar1 = findViewById(R.id.user_comment_one);
        ivStar2 = findViewById(R.id.user_comment_two);
        ivStar3 = findViewById(R.id.user_comment_three);
        ivStar4 = findViewById(R.id.user_comment_four);
        ivStar5 = findViewById(R.id.user_comment_five);
        tvScoreDesc = findViewById(R.id.user_comment_con);
        ivCommentImg = findViewById(R.id.user_manage_comment_img);
        btnTakePhoto = findViewById(R.id.user_manage_comment_pz);
        btnPickAlbum = findViewById(R.id.user_manage_comment_xc);
        btnSubmitComment = findViewById(R.id.user_manage_comment_fbpl);
    }

    /**
     * 初始化Toolbar返回事件
     */
    private void initToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * 初始化拍照/相册回调（拆分回调，避免逻辑混淆）
     */
    private void initActivityResultLauncher() {
        // 拍照回调
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    ivCommentImg.setImageBitmap(bitmap);
                }
            }
        });

        // 相册回调
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri selectImgUri = result.getData().getData();
                ivCommentImg.setImageURI(selectImgUri);
            }
        });
    }

    /**
     * 初始化控件事件（保留原有逻辑）
     */
    private void initViewListener() {
        // 评分星星点击事件（保留原有监听器）
        ivStar1.setOnClickListener(new StartListen(this));
        ivStar2.setOnClickListener(new StartListen(this));
        ivStar3.setOnClickListener(new StartListen(this));
        ivStar4.setOnClickListener(new StartListen(this));
        ivStar5.setOnClickListener(new StartListen(this));

        // 拍照按钮事件
        btnTakePhoto.setOnClickListener(v -> paiZhao());

        // 相册按钮事件
        btnPickAlbum.setOnClickListener(v -> xiangCe());

        // 提交评论按钮事件（核心：修复数据缺失+更新订单状态4）
        btnSubmitComment.setOnClickListener(v -> submitComment());
    }

    /**
     * 解析并校验跳转参数（核心：解决“数据缺失无法评论”）
     */
    private void parseAndCheckParams() {
        // 获取当前登录用户账号
        account = Tools.getOnAccount(this);

        // 解析跳转参数
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("orderId");
            businessId = intent.getStringExtra("businessId");
        }

        // 校验关键参数（非空判断）
        boolean isParamValid = true;
        if (account == null || account.trim().isEmpty()) {
            Toast.makeText(this, "用户未登录，无法评论", Toast.LENGTH_SHORT).show();
            isParamValid = false;
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, "订单ID缺失，无法评论", Toast.LENGTH_SHORT).show();
            isParamValid = false;
        }
        if (businessId == null || businessId.trim().isEmpty()) {
            Toast.makeText(this, "商家信息缺失，无法评论", Toast.LENGTH_SHORT).show();
            isParamValid = false;
        }

        // 参数无效时，禁用提交按钮
        btnSubmitComment.setEnabled(isParamValid);
    }

    /**
     * 提交评论（核心：插入评论+更新订单状态4）
     */
    private void submitComment() {
        // 1. 获取并校验评论内容
        String commentContent = etCommentContent.getText().toString().trim();
        if (commentContent.isEmpty()) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 获取并校验评分
        String scoreDesc = tvScoreDesc.getText() == null ? "" : tvScoreDesc.getText().toString().trim();
        if (scoreDesc.isEmpty()) {
            Toast.makeText(this, "请选择评分", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 转换评分为数字（1-5分）
        int score = 1;
        for (int i = 0; i < SCORE_DESC_ARRAY.length; i++) {
            if (SCORE_DESC_ARRAY[i].equals(scoreDesc)) {
                score = i + 1;
                break;
            }
        }

        // 4. 处理评论图片
        String imgPath = "";
        Drawable drawable = ivCommentImg.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            imgPath = FileImgUntil.getImgName();
            FileImgUntil.saveSystemImgToPath(bitmap, imgPath);
        }

        // 5. 插入评论到数据库
        int insertResult = CommentDao.insertComment(account, businessId, commentContent, String.valueOf(score), imgPath);

        // 6. 评论成功后，更新订单状态为4（完成且被评论）
        boolean updateOrderSuccess = false;
        if (insertResult == 1) {
            updateOrderSuccess = OrderDao.updateOrderStatusToCommented(orderId) == 1;
        }

        // 7. 结果提示与页面关闭
        if (insertResult == 1 && updateOrderSuccess) {
            Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
            finish(); // 关闭评论页，返回订单列表
        } else {
            Toast.makeText(this, "评论失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拍照功能
     */
    private void paiZhao() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(picIntent);
        } else {
            Toast.makeText(this, "未找到相机应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 相册选择功能
     */
    private void xiangCe() {
        Intent picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (picIntent.resolveActivity(getPackageManager()) != null) {
            galleryLauncher.launch(picIntent);
        } else {
            Toast.makeText(this, "未找到相册应用", Toast.LENGTH_SHORT).show();
        }
    }
}