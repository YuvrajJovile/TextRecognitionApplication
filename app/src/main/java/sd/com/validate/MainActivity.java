package sd.com.validate;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView surfaceView;
    private TextView textViewData;
    private Button btStartStop, btSearch;
    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;
    private boolean recognotionFlag = false;
    private ImageButton ivCopy, ivClear, ivCapture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Window window = getWindow();
//        WindowManager.LayoutParams winParams = window.getAttributes();
//        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        window.setAttributes(winParams);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        surfaceView = (SurfaceView) findViewById(R.id.sv_surface);
        textViewData = (TextView) findViewById(R.id.tv_data);
        btSearch = (Button) findViewById(R.id.bt_search);
        btStartStop = (Button) findViewById(R.id.bt_start_stop_recognition);
        ivCopy = (ImageButton) findViewById(R.id.iv_copy);
        ivClear = (ImageButton) findViewById(R.id.iv_clear);
        ivCapture = (ImageButton) findViewById(R.id.ib_capture);
        ivCapture.setOnClickListener(this);
        ivCopy.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        btStartStop.setOnClickListener(this);
        btSearch.setOnClickListener(this);
        init();


    }


    private void init() {
        textRecognizer = new TextRecognizer.Builder(MainActivity.this).build();
        if (textRecognizer.isOperational()) {


            cameraSource = new CameraSource.Builder(MainActivity.this, textRecognizer)
                    .setFacing(cameraSource.CAMERA_FACING_BACK)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(3.0f)
                    .setRequestedPreviewSize(1024, 768)
                    .build();


            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

                            return;
                        } else {

                        }

                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });


            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                    if (sparseArray.size() > 0) {
                        textViewData.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < sparseArray.size(); i++) {
                                    TextBlock textBlock = sparseArray.valueAt(i);
                                    stringBuilder.append(textBlock.getValue());
                                }
                                if (recognotionFlag && stringBuilder.toString().trim() != null && !stringBuilder.toString().trim().isEmpty()) {
                                    textViewData.setText(stringBuilder.toString().trim());
                                }

                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_search:
                goToSearch();
                break;
            case R.id.bt_start_stop_recognition:
                handleRecognition();
                break;
            case R.id.iv_copy:
                coppyText();
                break;
            case R.id.iv_clear:
                if (textViewData.getText().toString().trim() != null && !textViewData.getText().toString().trim().isEmpty()) {
                    textViewData.setText(null);
                }
                break;
            case R.id.ib_capture:
                captureText();
                break;
        }
    }

    private void captureText() {

        if (cameraSource != null) {
            cameraSource.stop();
            recognotionFlag = true;
        }


    }

    private void coppyText() {
        if (textViewData.getText().toString().trim() != null && !textViewData.getText().toString().trim().isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", textViewData.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "TextCopied", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToSearch() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, InAppSearchActivity.class);
        bundle.putString("google", textViewData.getText().toString().trim());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void handleRecognition() {
        recognotionFlag = recognotionFlag == false ? true : false;
        if (recognotionFlag) {
            btStartStop.setText("Stop");
                  try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start(surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }

        } else {
            btStartStop.setText("Start");
            cameraSource.stop();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }


}
