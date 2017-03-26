package jp.co.smallit.freg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.core.Mat;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.utils.Converters;
import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jp.co.smallit.freg.activity.FaceDetectGrayActivity;
import jp.co.smallit.freg.activity.FaceDetectRGBActivity;
import jp.co.smallit.freg.activity.PhotoDetectActivity;
import jp.co.smallit.freg.R;
import jp.co.smallit.freg.adapter.ImagePreviewAdapter;
import jp.co.smallit.freg.utils.ColorBlobDetector;

public class MainActivity extends AppCompatActivity {

    /* --- 追加 (共有ライブラリをロードする) --- */
    static {
        System.loadLibrary("opencv_java3");
    }

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_HANDLE_CAMERA_PERM_RGB = 1;
    private static final int RC_HANDLE_CAMERA_PERM_GRAY = 2;

    private Context mContext;
    private RecyclerView recyclerView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private ArrayList<Bitmap> facesBitmap;
    private ImageView testImage;

    private ImageView matchedImage;
    private TextView txt_confidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        testImage = (ImageView)findViewById(R.id.test_image);
        matchedImage =(ImageView)findViewById(R.id.matched_image);
        txt_confidence = (TextView)findViewById(R.id.confidence);
        //train face input
        Button btnTrain = (Button) findViewById(R.id.btnTrain);
        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mContext, FaceDetectRGBActivity.class);
                    intent.putExtra("mode",true);
                    startActivityForResult(intent,0);
                } else {
                    requestCameraPermission(RC_HANDLE_CAMERA_PERM_RGB);
                }
            }
        });

        //train face clear
        Button btnClearTrain = (Button) findViewById(R.id.clearTrain);
        btnClearTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFiles(true);
            }
        });
        //train face list
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        facesBitmap = new ArrayList<>();
        imagePreviewAdapter = new ImagePreviewAdapter(MainActivity.this, facesBitmap, new ImagePreviewAdapter.ViewHolder.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                imagePreviewAdapter.setCheck(position);
                imagePreviewAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(imagePreviewAdapter);
        refreshTrainFace();
        refreshTestFace();
        // test face input
        Button btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mContext, FaceDetectRGBActivity.class);
                    intent.putExtra("mode",false);
                    startActivityForResult(intent,1);
                } else {
                    requestCameraPermission(RC_HANDLE_CAMERA_PERM_RGB);
                }
            }
        });

        Button btnFind = (Button)findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });
    }

    public void find(){
        List<String> trainList = readFiles(true);
        List<String> testList = readFiles(false);
        FaceRecognizer rec = Face.createLBPHFaceRecognizer();
        if (testList.size() > 0) {

            String testPath = testList.get(0);
            Mat srcMat = Imgcodecs.imread(testPath, Imgcodecs.IMREAD_GRAYSCALE);

            List<Mat> mats = new ArrayList<>();
            List<Integer> labels = new ArrayList<Integer>();
            for (int i = 0; i < trainList.size(); i++) {
                String filePath = trainList.get(i);
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                Integer label = Integer.valueOf(fileName.substring(11, 12));
                String labelInfo = fileName;
                labels.add(label);
                Mat mat = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE);
                Log.e(TAG, "label:" + label + "  labelInfo:" + labelInfo);
                mats.add(mat);
                rec.setLabelInfo(label, labelInfo);
            }
            Mat labelMat = Converters.vector_int_to_Mat(labels);
            rec.train(mats, labelMat);
            int[] labelr = new int[trainList.size()];
            double[] confidence = new double[trainList.size()];

            rec.predict(srcMat,labelr,confidence);
            for(int i=0;i<trainList.size();i++) {
                Log.i(TAG,"predict index :" + i + " label " + labels.get(i) + "  confidence :"+confidence[i]);
            }

            String matchedFilePath = Environment.getExternalStorageDirectory().toString()
                    + "/freg_train_"+String.valueOf(labelr[0])+ ".jpg";
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(matchedFilePath,bmOptions);
            matchedImage.setImageBitmap(bitmap);
            txt_confidence.setText("確度:"+String.valueOf(confidence[0]));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult " + requestCode);
        switch (requestCode) {
            case (0):
                refreshTrainFace();
                break;
            case (1):
                refreshTestFace();
                break;
            default:
                break;
        }
    }

    public void resetFiles(boolean isTrain){
        List<String> trainFaceList = readFiles(true);
        for(int i=0;i<trainFaceList.size();i++){
            File f = new File(trainFaceList.get(i));
            if (f.exists())f.delete();
        }
        imagePreviewAdapter.clearAll();
    }

    public List<String> readFiles(boolean isTrain)
    {
        List<String> trainList = new ArrayList<>();
        File f = new File(Environment.getExternalStorageDirectory().toString());
        File[] files=f.listFiles();
        for(int i=0; i<files.length; i++)
        {
            File file = files[i];
            String filePath = file.getPath();
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            if(filePath.endsWith(".jpg")) {
                if (isTrain) {
                    if (fileName.startsWith("freg_train_")){
                        trainList.add(filePath);
                    }
                } else {
                    if (fileName.startsWith("freg_test")){
                        trainList.add(filePath);
                    }
                }
            }
        }
        return trainList;
    }

    public void refreshTrainFace(){
        imagePreviewAdapter.clearAll();
        List<String> trainFaceList = readFiles(true);
        for(int i=0;i<trainFaceList.size();i++){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(trainFaceList.get(i),bmOptions);
            imagePreviewAdapter.add(bitmap);
        }
        imagePreviewAdapter.notifyDataSetChanged();
    }

    public void refreshTestFace(){
        testImage.setImageResource(0);
        List<String> trainFaceList = readFiles(false);
        for(int i=0;i<trainFaceList.size();i++){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(trainFaceList.get(i),bmOptions);
            testImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        /* --- OpenCV Managerの呼び出しをコメントアウト --- */
/*      if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else */
        {
            //Log.d(TAG, "OpenCV library found inside package. Using it!");
            //mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void requestCameraPermission(final int RC_HANDLE_CAMERA_PERM) {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == RC_HANDLE_CAMERA_PERM_RGB) {
            Intent intent = new Intent(mContext, FaceDetectRGBActivity.class);
            startActivity(intent);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == RC_HANDLE_CAMERA_PERM_GRAY) {
            Intent intent = new Intent(mContext, FaceDetectGrayActivity.class);
            startActivity(intent);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
    }

}
