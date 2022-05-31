package yash.com.miniproject.dherya.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.utils.ImageUtils;


public class ImageCropperActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int IMAGE_COMPRESSION_PERCENTAGE = 30;
    public static final String IMAGE_PATH = "image_path";



    CropImageView cropImageView;



    String imagePath;
    Bitmap originalImage = null;
    Bitmap croppedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);

        try {
            imagePath = getIntent().getStringExtra(IMAGE_PATH);
            originalImage = BitmapFactory.decodeFile(imagePath);
        } catch (Exception e) {
            originalImage = null;
        }

        if (originalImage == null) {
            Toast.makeText(this, "No image to crop!", Toast.LENGTH_SHORT).show();
            finish();
        }


        cropImageView = (CropImageView) findViewById(R.id.image_cropper_crop_image_view);
        cropImageView.setImageBitmap(originalImage);
        cropImageView.setGuidelines(2);

        ImageButton cropButton = (ImageButton) findViewById(R.id.image_cropper_crop_button);
        ImageButton rotateButton = (ImageButton) findViewById(R.id.image_cropper_rotate_button);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.image_cropper_cancel_button);

        cropButton.setOnClickListener(this);
        rotateButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        switch (i) {
            case R.id.image_cropper_crop_button:
                handleImageCrop();
                break;
            case R.id.image_cropper_rotate_button:
                cropImageView.rotateImage(90);
                break;
            case R.id.image_cropper_cancel_button:
                finish();
                break;
        }
    }

    private void handleImageCrop() {


        croppedImage = cropImageView.getCroppedImage();


        croppedImage = ImageUtils.scaleBitmap(croppedImage, 1920);


        byte[] out = ImageUtils.toCompressedByteArray(croppedImage, IMAGE_COMPRESSION_PERCENTAGE);


        try {
            File image = new File(imagePath);
            FileOutputStream fos = new FileOutputStream(image);
            fos.write(out);
            fos.close();
        }catch (IOException e) {
            Toast.makeText(this, "There was a problem saving the cropped image!", Toast.LENGTH_SHORT).show();
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }



}
