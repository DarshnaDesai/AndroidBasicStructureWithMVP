package com.basicstructurewithmvp.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.basicstructurewithmvp.BuildConfig;
import com.basicstructurewithmvp.R;
import com.basicstructurewithmvp.baseclasses.LifeCycleCallBackManager;
import com.basicstructurewithmvp.models.FileUri;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static com.basicstructurewithmvp.utils.AppUtils.createImageFile;
import static com.basicstructurewithmvp.utils.AppUtils.createVideoFile;


/**
 * Created by Darshna Desai
 */

public class FilePickUtils implements LifeCycleCallBackManager {

    public static final int VIDEO_LIMIT_FROM_CAMERA = 30;
    public static final int VIDEO_QUALITY_FROM_CAMERA = 1;//Low Quality 0 High Quality 1

    public static final long VIDEO_MAX_SIZE_LENGTH = 26214400;

    private static final int CAMERA_MEDIA = 10;
    private static final int GALLERY_MEDIA = 11;

    private static final int STORAGE_PERMISSION_IMAGE = 111;
    private static final int STORAGE_PERMISSION_CAMERA = 112;
    private static final int CAMERA_PERMISSION = 115;
    private static final int CAMERA_BUT_STORAGE_PERMISSION = 116;
    public static final int FILE_TYPE_IMAGE = 232;
    public static final int FILE_TYPE_VIDEO = 233;
    public static final int FILE_TYPE_DOCUMENT = 234;
    public static final int FILE_TYPE_IMAGE_VIDEO = 235;

    private OnFileChoose mOnFileChoose;
    private Uri imageUrl;
    private int requestCode;
    private Activity activity;
    private Fragment fragment;
    private boolean allowCrop;
    private boolean allowDelete;
    @FILETYPE
    private int fileType;
    private List<String> fileUrls = new ArrayList<>();

    public FilePickUtils(Activity activity, OnFileChoose mOnFileChoose) {
        super();
        this.activity = activity;
        this.mOnFileChoose = mOnFileChoose;
    }

    public FilePickUtils(Fragment fragment, OnFileChoose mOnFileChoose) {
        super();
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.mOnFileChoose = mOnFileChoose;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public LifeCycleCallBackManager getCallBackManager() {
        return this;
    }

    public void requestVideoFromGallery(int requestCode) {
        setFileType(FILE_TYPE_VIDEO);
        this.requestCode = requestCode;
        boolean hasStoragePermission = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission) {
            selectVideoFromGallery();
        } else {
            requestPermissionForExternalStorage();
        }
    }

    public void requestImageVideoFromGallery(int requestCode) {
        setFileType(FILE_TYPE_IMAGE_VIDEO);
        this.requestCode = requestCode;
        boolean hasStoragePermission = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission) {
            selectImageVideoFromGallery();
        } else {
            requestPermissionForExternalStorage();
        }
    }

    private void selectVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_MEDIA);
    }

    public void requestVideoFromCamera(int requestCode) {

        setFileType(FILE_TYPE_VIDEO);
        this.requestCode = requestCode;

        boolean hasCameraPermission = checkPermission(Manifest.permission.CAMERA);
        boolean hasStoragePermission = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasCameraPermission && hasStoragePermission) {
            selectVideoFromCamera();
        } else if (!hasCameraPermission && !hasStoragePermission) {
            requestPermissionForCameraStorage();
        } else if (!hasCameraPermission) {
            requestPermissionForCamera();
        } else {
            requestPermissionForCameraButStorage();
        }
    }

    private void selectVideoFromCamera() {

        File photoFile;
        FileUri fileUri = createVideoFile("VIDEO");
        photoFile = fileUri.getFile();
        imageUrl = fileUri.getImageUrl();

        if (photoFile != null) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", fileUri.getFile());
            //intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, VIDEO_LIMIT_FROM_CAMERA);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, VIDEO_QUALITY_FROM_CAMERA);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, VIDEO_MAX_SIZE_LENGTH);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            List<ResolveInfo> resInfoList =
                    activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(intent, CAMERA_MEDIA);
        }
    }

    public void requestImageGallery(int requestCode, boolean allowCrop) {
        setFileType(FILE_TYPE_IMAGE);
        this.requestCode = requestCode;
        this.allowCrop = allowCrop;
        boolean hasStoragePermission = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission) {
            selectImageFromGallery();
        } else {
            requestPermissionForExternalStorage();
        }
    }

    private void selectImageFromGallery() {
        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("image/*");
        startActivityForResult(pictureActionIntent, GALLERY_MEDIA);
    }

    private void selectImageVideoFromGallery() {
        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("image/* video/*");
        startActivityForResult(pictureActionIntent, GALLERY_MEDIA);
    }

    public void requestImageCamera(int requestCode, boolean allowCrop) {
        setFileType(FILE_TYPE_IMAGE);
        this.requestCode = requestCode;
        this.allowCrop = allowCrop;
        boolean hasCameraPermission = checkPermission(Manifest.permission.CAMERA);
        boolean hasStoragePermission = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasCameraPermission && hasStoragePermission) {
            selectImageFromCamera();
        } else if (!hasCameraPermission && !hasStoragePermission) {
            requestPermissionForCameraStorage();
        } else if (!hasCameraPermission) {
            requestPermissionForCamera();
        } else {
            requestPermissionForCameraButStorage();
        }
    }

    private void selectImageFromCamera() {
        File photoFile;

        FileUri fileUri = createImageFile("CAMERA");
        photoFile = fileUri.getFile();
        imageUrl = fileUri.getImageUrl();

        if (photoFile != null) {
      /*Uri photoURI = FileProvider.getUriForFile(activity,
          BuildConfig.APPLICATION_ID + ".provider",
          photoFile);*/
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", fileUri.getFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            List<ResolveInfo> resInfoList =
                    activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(intent, CAMERA_MEDIA);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionForCamera() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        requestPermissionWithRationale(permissions, CAMERA_PERMISSION, "Camera");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionForCameraButStorage() {
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissionWithRationale(permissions, CAMERA_BUT_STORAGE_PERMISSION, "Storage");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionForExternalStorage() {
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissionWithRationale(permissions, STORAGE_PERMISSION_IMAGE, "Storage");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionForCameraStorage() {
        final String[] permissions = new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
        };
        requestPermissionWithRationale(permissions, STORAGE_PERMISSION_CAMERA, "Camera & Storage");
    }

    //Activity and Fragment Base Methods
    private void startActivityForResult(Intent intent, int requestCode) {
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    private boolean checkPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissionWithRationale(final String permissions[], final int requestCode, String rationaleDialogText) {
        boolean showRationale = false;
        for (String permission : permissions) {
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                showRationale = true;
            }
        }

        if (showRationale) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(activity).setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermissions(permissions, requestCode);
                        }
                    }).setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setMessage("Allow " + activity.getString(R.string.app_name) + " to access " + rationaleDialogText + "?");
            builder.create().show();
        } else {
            requestPermissions(permissions, requestCode);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions(String[] permissions, int requestCode) {
        if (fragment != null) {
            fragment.requestPermissions(permissions, requestCode);
        } else if (activity != null) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_IMAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onStoragePermissionGrantedGallery();
        } else if (requestCode == STORAGE_PERMISSION_CAMERA
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            onStoragePermissionGrantedCamera();
        } else if (requestCode == CAMERA_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        } else if (requestCode == CAMERA_BUT_STORAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraButStoragePermissionGranted();
        }
    }

    private void onStoragePermissionGrantedGallery() {
        if (fileType == FILE_TYPE_IMAGE) {
            selectImageFromGallery();
        } else if (fileType == FILE_TYPE_VIDEO) {
            selectVideoFromGallery();
        } else {
            selectImageVideoFromGallery();
        }
    }

    private void onStoragePermissionGrantedCamera() {
        if (fileType == FILE_TYPE_IMAGE) {
            selectImageFromCamera();
        } else {
            selectVideoFromCamera();
        }
    }

    private void onCameraPermissionGranted() {
        if (fileType == FILE_TYPE_IMAGE) {
            selectImageFromCamera();
        } else {
            selectVideoFromCamera();
        }
    }

    private void onCameraButStoragePermissionGranted() {
        if (fileType == FILE_TYPE_IMAGE) {
            selectImageFromCamera();
        } else {
            selectVideoFromCamera();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean hasStoragePermission = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (resultCode == Activity.RESULT_OK && hasStoragePermission) {

            switch (requestCode) {
                case GALLERY_MEDIA:
                    onGalleryResult(data);
                    break;
                case CAMERA_MEDIA:
                    onCameraResult(data);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    performImageProcessing(resultUri.getPath());
                    break;
            }
        }
    }

    private void onCameraResult(Intent data) {
        if (fileType == FILE_TYPE_IMAGE) {
            Uri uri = imageUrl;
            if (allowCrop) {
                performCrop(uri);
            } else {
                performImageProcessing(uri.getPath());
            }
        } else if (fileType == FILE_TYPE_VIDEO) {
            onFileChoose(imageUrl.getPath(), FILE_TYPE_VIDEO);
        }
    }

    private void onGalleryResult(Intent data) {

        if (fileType == FILE_TYPE_IMAGE) {
            if (allowCrop) {
                performCrop(data.getData());
            } else {
      /*onFileChoose(getRealPathFromURI(data.getData().toString()));*/
                performImageProcessing(data.getData().toString());
            }
        } else if (fileType == FILE_TYPE_VIDEO) {
            onFileChoose(getRealPathFromURI(data.getData().toString()), FILE_TYPE_VIDEO);
        } else if (fileType == FILE_TYPE_IMAGE_VIDEO) {
            ContentResolver cr = activity.getContentResolver();
            String mime = cr.getType(data.getData());
            if (mime != null && mime.startsWith("image/")) {
                AppUtils.logE("IMAGE FILE");
                if (allowCrop) {
                    performCrop(data.getData());
                } else {
                    performImageProcessing(data.getData().toString());
                }
            } else if (mime != null && mime.startsWith("video/")) {
                AppUtils.logE("VIDEO FILE");
                onFileChoose(getRealPathFromURI(data.getData().toString()), FILE_TYPE_VIDEO);
            }
        }
    }

    public void onDestroy() {
        activity = null;
        fragment = null;
        mOnFileChoose = null;
        //To delete Files on exit
        if (fileUrls != null && !fileUrls.isEmpty() && allowDelete) {
            for (String fileUrl : fileUrls) {
                File file = new File(fileUrl);
                if (file.exists()) {
                    boolean isDelete = file.delete();
                }
            }
        }
    }

    @Override
    public void onStartActivity() {

    }

    //This method is for compress image
    private void performImageProcessing(final String imageUrl) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(compressImage(imageUrl));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                onFileChoose(s, FILE_TYPE_IMAGE);
            }
        });
    }


    private String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //		by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //		you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        //		max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 616.0f;
        float maxWidth = 816.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //		width and height values are set maintaining the aspect ratio of the image
        Log.d("IMAGE", "actualHeight=" + actualHeight + "actualWidth=" + actualWidth + "");
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //		setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //		inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //		this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //			load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out;
        String filename = createImageFile("").getFile().getAbsolutePath();
        try {
            out = new FileOutputStream(filename);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        refreshMediaProvider(filePath);
        Cursor cursor = activity.getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return activity.getApplicationContext().getContentResolver().insert(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Force a refresh of media content provider for specific item
     *
     * @param fileName
     */
    private void refreshMediaProvider(String fileName) {
        MediaScannerConnection scanner = null;
        try {
            scanner = new MediaScannerConnection(activity, null);
            scanner.connect();
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
            if (scanner.isConnected()) {
                scanner.scanFile(fileName, null);
            }
        } catch (Exception e) {
        } finally {
            if (scanner != null) {
                scanner.disconnect();
            }
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private void onFileChoose(String uri, int type) {
        if (mOnFileChoose != null) {
            mOnFileChoose.onFileChoose(uri, requestCode, type);
        }
    }

    public void performCrop(Uri uri) {
        FileUri cropFile = createImageFile("CROP");
        if (fragment != null) {
            CropImage.activity(uri)
                    .setOutputUri(cropFile.getImageUrl())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(activity, fragment);
        } else {
            CropImage.activity(uri)
                    .setOutputUri(cropFile.getImageUrl())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(activity);
        }
    }

    public
    @FILETYPE
    int getFileType() {
        return fileType;
    }

    public void setFileType(@FILETYPE int fileType) {
        this.fileType = fileType;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FILE_TYPE_IMAGE, FILE_TYPE_VIDEO, FILE_TYPE_DOCUMENT, FILE_TYPE_IMAGE_VIDEO})
    public @interface FILETYPE {
    }

    public interface OnFileChoose {
        void onFileChoose(String fileUri, int requestCode, int type);
    }

}
