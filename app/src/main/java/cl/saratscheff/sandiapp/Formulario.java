package cl.saratscheff.sandiapp;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Formulario extends AppCompatActivity {

    public final String APP_TAG = "SandiApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "SandiApp.jpg";
    public Bitmap takenImage;
    public EditText titulo;
    public EditText descripcion;
    private Firebase mFire;
    ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
        // Start the image capture intent to take photo
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        ivPreview = (ImageView) findViewById(R.id.imageView);
        ivPreview.setClickable(true);


        Button publicar =  (Button) findViewById(R.id.buttonPublicar);
        Button meme =  (Button) findViewById(R.id.buttonMeme);
       titulo =  (EditText) findViewById(R.id.editText2);
       descripcion =  (EditText) findViewById(R.id.editText3);

        publicar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {

                    if (String.valueOf(titulo.getText()) == "" |String.valueOf(descripcion.getText()) == "") {

                        Toast.makeText(Formulario.this,
                               "Complete los campos Titulo y Descripcion",
                             Toast.LENGTH_LONG).show();
                    }else {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("titulo", String.valueOf(titulo.getText()));
                        returnIntent.putExtra("descripcion", String.valueOf(descripcion.getText()));
                        returnIntent.putExtra("img", photoFileName);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                    }catch (Exception e){

                    }
            }
        });

        meme.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {

                   // Bitmap TakenLight = ScaleImage(takenImage, 1000, 1000);

                    ivPreview.setImageBitmap(MemeBitmap(takenImage));

                }catch (Exception e){

                }
            }
        });

        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }


       // Toast.makeText(this, R.string.TEXT, Toast.LENGTH_SHORT).show();


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                /* GIRAR FOTO */
                //Obtener rotación
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(takenPhotoUri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // Obtener bmp rotado
                takenImage = rotateBitmap(BitmapFactory.decodeFile(takenPhotoUri.getPath()), orientation);
                /* ---FIN--- */
                // La linea siguiente funciona en caso de evitar la rotación: (OLD)
                // takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // Load the taken image into a preview


                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "No se tomo una foto!", Toast.LENGTH_SHORT).show();
                // Terminar el activity (Volver al mapa)
                finish();
            }
        }
    }


    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private String code(Bitmap img)
    {
        try {
            Bitmap bmp = img;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bmp.recycle();
            byte[] byteArray = stream.toByteArray();
            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

            return imageFile;
        }
        catch (Exception e){
            return null;
        }
    }

    private Bitmap decode(String imageFile)
    {
        byte[] imageAsBytes = Base64.decode(imageFile, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return  bmp;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    private Bitmap MemeBitmap(Bitmap bm){
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        try {
            bm1 = bm;

            Bitmap.Config config = bm1.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1, 0, 0, null);

            String captionString = titulo.getText().toString();
            if(captionString != null){

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.BLUE);
                paintText.setTextSize(300);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

                newCanvas.drawText(captionString,
                        takenImage.getWidth()/2 - rectText.width()/2 , rectText.height()+50, paintText);

                Toast.makeText(getApplicationContext(),
                        "Meme: " + captionString,
                        Toast.LENGTH_LONG).show();

                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SandiApp/" + "SandiApp.jpg");
                String path = mediaStorageDir.getAbsolutePath();

                Bitmap img = ScaleImage(newBitmap, 700, 700);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mediaStorageDir);
                    img.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getApplicationContext(),
                        "caption empty!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newBitmap;
    }

    public static Bitmap ScaleImage(Bitmap image, int maxHeight, int maxWidth)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        if (width<maxWidth && height>maxHeight){
            return image;
        };

        double ratioH = (double)maxHeight / image.getHeight();
        double ratioW = (double)maxWidth / image.getWidth();

        double ratio = Math.min(ratioH, ratioW);
        int newWidth = (int)(image.getHeight() * ratio);
        int newHeight = (int)(image.getWidth() * ratio);

        Bitmap newImage = Bitmap.createScaledBitmap(image, newHeight, newWidth, true);
        return newImage;
    }


}
