package yash.com.miniproject.dherya.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.dialogs.CaptureOcrTextDialogFragment;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.enums.Currency;
import yash.com.miniproject.dherya.enums.ExpenseCategory;
import yash.com.miniproject.dherya.enums.ExpenseType;
import yash.com.miniproject.dherya.exceptions.CouldNotInsertDataException;
import yash.com.miniproject.dherya.model.Expense;
import yash.com.miniproject.dherya.ocr.OcrDetectorProcessor;
import yash.com.miniproject.dherya.ocr.OcrGraphic;
import yash.com.miniproject.dherya.ocr.camera.CameraSource;
import yash.com.miniproject.dherya.ocr.camera.CameraSourcePreview;
import yash.com.miniproject.dherya.ocr.camera.GraphicOverlay;


public final class OcrCreateExpenseActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, CaptureOcrTextDialogFragment.CaptureOcrTextDialogListener {
    private static final String TAG = "OcrCreateExpenseAct";

    public static final String TAG_EXTRA_PERIOD_ID = "tagExtraPeriodId";
    public static final String TAG_EXTRA_CURRENCY = "tagExtraCurrency";

    private static final int RC_HANDLE_GMS = 9001;

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private static int STATUS_BAR_HEIGHT_OFFSET;
    private static int RESIZER_MARGIN = 50;

    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mOcrCameraPreview;
    private GraphicOverlay<OcrGraphic> mOcrGraphicOverlay;
    private OcrDetectorProcessor mDetectorProcessor;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private Toolbar mToolbar;
    private TextView mTxtDetectedText;
    private ImageView mBtnCapture;
    private EditText mTxtAmount;
    private EditText mTxtDescription;
    private Spinner mExpenseCategory;
    private Spinner mExpenseType;
    private Button mCreate;
    private Button mCancel;

    private FrameLayout mOcrContainer;
    private LinearLayout mOcrWindowContainer;
    private View mOcrWindow;
    private ImageView mOcrWindowResizer;
    private Point resizerCenterOffset;
    private Point resizerMinPosition;
    private Point resizerMaxPosition;
    private Point containerCenter;

    ExpenseManagerDAO mDao;
    List<ExpenseCategory> expenseCategories;
    List<ExpenseType> expenseTypes;
    Currency mCurrency = null;
    int mCreditPeriodId = -1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ocr_create_expense);

        loadDao();

        mCurrency = (Currency) getIntent().getSerializableExtra(TAG_EXTRA_CURRENCY);
        mCreditPeriodId =  getIntent().getIntExtra(TAG_EXTRA_PERIOD_ID, -1);

        if(mCreditPeriodId == -1 || mCurrency == null) {
            Toast.makeText(this, "Error, wrong arguments passed. Finishing activity", Toast.LENGTH_SHORT).show();
            finish();
        }

        mToolbar = (Toolbar) findViewById(R.id.create_expense_ocr_toolbar);
        mTxtDetectedText = (TextView) findViewById(R.id.create_expense_ocr_txt_detected);
        mBtnCapture = (ImageView) findViewById(R.id.create_expense_ocr_btn_capture);
        mTxtAmount = (EditText) findViewById(R.id.create_expense_ocr_amount);
        mTxtDescription = (EditText) findViewById(R.id.create_expense_ocr_description);
        mExpenseCategory = (Spinner) findViewById(R.id.create_expense_ocr_category);
        mExpenseType = (Spinner) findViewById(R.id.create_expense_ocr_type);
        mCreate = (Button) findViewById(R.id.create_expense_ocr_btn_create);
        mCancel = (Button) findViewById(R.id.create_expense_ocr_btn_cancel);
        mOcrContainer = (FrameLayout) findViewById(R.id.create_expense_ocr_container);
        mOcrWindowContainer = (LinearLayout) findViewById(R.id.create_expense_ocr_window_container);
        mOcrWindow = findViewById(R.id.create_expense_ocr_window);
        mOcrCameraPreview = (CameraSourcePreview) findViewById(R.id.create_expense_ocr_camera_preview);
        mOcrGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.create_expense_ocr_graphic_overlay);
        mOcrWindowResizer = (ImageView) findViewById(R.id.create_expense_ocr_window_resizer);

        boolean autoFocus = true;
        boolean useFlash = false;
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        setUpToolbar();
        setUpSpinners();
        setUpShowcase();

        mBtnCapture.setOnClickListener(this);
        mCreate.setOnClickListener(this);
        mCancel.setOnClickListener(this);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        calculateStatusBarHeightOffset();
        setupOcrWindowResizer();

        mDetectorProcessor.setOcrWindowBoundingBox(calculateViewBoundingBox(mOcrWindow));
        mDetectorProcessor.setOcrWindowContainerBoundingBox(calculateViewBoundingBox(mOcrWindowContainer));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDao() {
        if(mDao == null)
            mDao = new ExpenseManagerDAO(getApplicationContext());
    }

    private void setUpToolbar() {
        mToolbar.setTitle(getResources().getString(R.string.activity_create_expense_ocr_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        setSupportActionBar(mToolbar);
    }

    private void setUpSpinners() {

        expenseCategories = new ArrayList<>(Arrays.asList(ExpenseCategory.values()));
        ArrayAdapter expenseCategoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, expenseCategories);
        expenseCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mExpenseCategory.setAdapter(expenseCategoryAdapter);

        expenseTypes = new ArrayList<>(Arrays.asList(ExpenseType.values()));
        ArrayAdapter expenseTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, expenseTypes);
        expenseTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mExpenseType.setAdapter(expenseTypeAdapter);
    }

    private void setUpShowcase() {

        final Activity ACTIVITY = this;

        new ShowcaseView.Builder(ACTIVITY)
                .setTarget(new ViewTarget(mOcrWindowResizer))
                .setContentTitle(getResources().getText(R.string.activity_ocr_create_showcase_resizer_title))
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentText(getResources().getText(R.string.activity_ocr_create_showcase_resizer_content))
                .hideOnTouchOutside()
                .singleShot(1)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        new ShowcaseView.Builder(ACTIVITY)
                                .setTarget(new ViewTarget(mBtnCapture))
                                .setContentTitle(getResources().getText(R.string.activity_ocr_create_showcase_capture_title))
                                .withMaterialShowcase()
                                .setStyle(R.style.CustomShowcaseTheme)
                                .setContentText(getResources().getText(R.string.activity_ocr_create_showcase_capture_content))
                                .hideOnTouchOutside()
                                .singleShot(2)
                                .build();
                    }

                })
                .build();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int X = (int) motionEvent.getRawX();
        int Y = (int) motionEvent.getRawY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                X = (X < resizerMinPosition.x ? resizerMinPosition.x : X);
                X = (X > resizerMaxPosition.x ? resizerMaxPosition.x : X);
                Y = (Y < (resizerMinPosition.y + STATUS_BAR_HEIGHT_OFFSET) ? (resizerMinPosition.y + STATUS_BAR_HEIGHT_OFFSET) : Y);
                Y = (Y > (resizerMaxPosition.y + STATUS_BAR_HEIGHT_OFFSET) ? (resizerMaxPosition.y + STATUS_BAR_HEIGHT_OFFSET) : Y);

                mOcrWindowResizer.setX(X - resizerCenterOffset.x);
                mOcrWindowResizer.setY(Y - resizerCenterOffset.y - STATUS_BAR_HEIGHT_OFFSET);

                mOcrWindow.setLeft(containerCenter.x - (X - containerCenter.x));
                mOcrWindow.setTop(containerCenter.y + STATUS_BAR_HEIGHT_OFFSET - (Y - containerCenter.y));
                mOcrWindow.setRight(X);
                mOcrWindow.setBottom(Y - STATUS_BAR_HEIGHT_OFFSET);

                break;

            case MotionEvent.ACTION_UP:
                mDetectorProcessor.setOcrWindowBoundingBox(calculateViewBoundingBox(mOcrWindow));
                break;
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int X = (int) e.getRawX();
        int Y = (int) e.getRawY();
        Log.w(TAG, "TOUCH X="+X+" Y="+Y);

        return super.onTouchEvent(e);
    }


    private void calculateStatusBarHeightOffset() {

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int a = dm.heightPixels;
        int b =  mOcrContainer.getMeasuredHeight();

        STATUS_BAR_HEIGHT_OFFSET = (dm.heightPixels - mOcrContainer.getMeasuredHeight());
        Log.d (TAG, "STATUSBAR OFFSET = " + STATUS_BAR_HEIGHT_OFFSET);
    }

    private void setupOcrWindowResizer() {
        resizerCenterOffset = new Point();
        resizerCenterOffset.x = mOcrWindowResizer.getMeasuredWidth()/2;
        resizerCenterOffset.y = mOcrWindowResizer.getMeasuredHeight()/2;
        mOcrWindowResizer.setOnTouchListener(this);


        mOcrWindowResizer.setX(mOcrWindow.getRight() - resizerCenterOffset.x);
        mOcrWindowResizer.setY(mOcrWindow.getBottom() - resizerCenterOffset.y);

        Point containerBRCorner = new Point();
        containerCenter = new Point();
        containerBRCorner.x = mOcrWindowContainer.getRight();
        containerBRCorner.y = mOcrWindowContainer.getBottom();
        containerCenter.x = mOcrWindowContainer.getLeft() + (mOcrWindowContainer.getWidth() / 2);
        containerCenter.y = mOcrWindowContainer.getTop() + (mOcrWindowContainer.getHeight() / 2);

        resizerMinPosition = new Point(containerCenter.x + RESIZER_MARGIN, containerCenter.y + RESIZER_MARGIN);
        resizerMaxPosition = new Point(containerBRCorner.x - RESIZER_MARGIN, containerBRCorner.y - RESIZER_MARGIN);
    }

    private Rect calculateViewBoundingBox(View view){

        int[] loc = new int[2];
        view.getLocationInWindow(loc);
        loc[1] += STATUS_BAR_HEIGHT_OFFSET/4;
        Rect boundingBox = new Rect(loc[0], loc[1], loc[0] + view.getWidth(), loc[1] + view.getHeight());
        return boundingBox;
    }



    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mOcrGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        mDetectorProcessor = new OcrDetectorProcessor(mOcrGraphicOverlay, this);
        textRecognizer.setProcessor(mDetectorProcessor);

        if(!textRecognizer.isOperational()) {
            Log.w(TAG, "TextRecognizer dependencies are not yet available");



            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if(hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_SHORT).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }


        mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(15.0f)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOcrCameraPreview != null) {
            mOcrCameraPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOcrCameraPreview != null) {
            mOcrCameraPreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");

            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,true);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    @SuppressLint("MissingPermission")
    private void startCameraSource() throws SecurityException {

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mOcrCameraPreview.start(mCameraSource, mOcrGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.create_expense_ocr_btn_capture:
                handleTextCapture();
                break;
            case R.id.create_expense_ocr_btn_create:
                handleNewExpenseCreation();
                break;
            case R.id.create_expense_ocr_btn_cancel:
                finish();
                break;
        }
    }

    private void handleTextCapture() {
        String text = mTxtDetectedText.getText().toString();
        if(text.isEmpty()) {
            Toast.makeText(this, "No text has been detected", Toast.LENGTH_SHORT).show();
            return;
        }

        View v = getCurrentFocus();
        Object asd = v.getClass();
        if(v == null || v.getClass() != AppCompatEditText.class ) {
            Toast.makeText(this, "Tap a text field to set its text", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            TextView tv = (TextView) v;

            switch (tv.getId()) {
                case R.id.create_expense_ocr_amount:
                    text = text.replaceAll("[^\\d.]", "");
                    tv.setText(text);
                    mTxtDescription.requestFocus();
                    break;
                case R.id.create_expense_ocr_description:
                    tv.setText(text);
                    break;

            }


        } catch (Exception e) {
            Toast.makeText(this, "Problem Capturing text!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNewExpenseCreation() {

        String amount = mTxtAmount.getText().toString();
        String description = mTxtDescription.getText().toString();
        if(amount.equals("") || new BigDecimal(amount).compareTo(new BigDecimal(0)) == 0) {
            Toast.makeText(this, getResources().getString(R.string.dialog_create_expense_error_bad_amount), Toast.LENGTH_SHORT).show();
            return;
        }

        ExpenseCategory expenseCategory = expenseCategories.get(mExpenseCategory.getSelectedItemPosition());
        ExpenseType expenseType = expenseTypes.get(mExpenseType.getSelectedItemPosition());

            try {
                Expense expense = new Expense(description, null, null, new BigDecimal(amount),
                        mCurrency, Calendar.getInstance(), expenseCategory, expenseType);
                mDao.insertExpense(mCreditPeriodId, expense);
            } catch (CouldNotInsertDataException e) {
                Toast.makeText(this, "There was a problem inserting the Expense", Toast.LENGTH_SHORT).show();
            }

        finish();

    }



    public void setNewDetectedText(final String newDetectedText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtDetectedText.setText(newDetectedText);
            }
        });
    }

    @Override
    public void onFinishCaptureOcrTextDialog(String selectedText) {
        Toast.makeText(this, "onFinishCaptureOcrTextDialog called", Toast.LENGTH_SHORT).show();
    }


    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }


        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (mCameraSource != null) {
                mCameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }
}
