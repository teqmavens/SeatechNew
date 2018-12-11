package teq.development.seatech.JobDetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.JobDetail.Skeleton.UploadImageSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.Utils.UserPicture;
import teq.development.seatech.database.Action;
import teq.development.seatech.database.CustomGallery;
import teq.development.seatech.database.GalleryAdapter;
import teq.development.seatech.database.MainActivity;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.PopupNeedchangeorderBinding;

import static android.app.Activity.RESULT_OK;

public class UploadImageDialog extends DialogFragment implements View.OnClickListener {

    Dialog dialog;
    int PICK_IMAGE_MULTIPLE = 1;
    List<String> imagesEncodedList;
    MediaType MEDIA_TYPE_FORM = MediaType.parse("image/png");
    private static final int REQUEST_IMAGE_CAPTURE = 5484;
    EditText et_laborperform;
    File imageFile;
    ImageView imgSinglePick;
    ViewSwitcher viewSwitcher;
    ArrayList<Uri> mArrayUri;
    ImageLoader imageLoader;
    String[] all_path = null;
    GridView gridGallery;
    GalleryAdapter adapter;
    public static String jobid;
    private SQLiteDatabase database;
    String insertedTime;
    Gson gson;

    static UploadImageDialog newInstance(String num) {
        UploadImageDialog f = new UploadImageDialog();
        Bundle args = new Bundle();
        jobid = num;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_uploadimage, container, false);
        initImageLoader();
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 240;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void initViews(View rootView) {
        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        Button submit = (Button) rootView.findViewById(R.id.submit);
        ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        TextView options_gallery = (TextView) rootView.findViewById(R.id.options_gallery);
        TextView options_camera = (TextView) rootView.findViewById(R.id.options_camera);
        et_laborperform = (EditText) rootView.findViewById(R.id.et_laborperform);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher);
        imgSinglePick = (ImageView) rootView.findViewById(R.id.imgSinglePick);
        viewSwitcher.setVisibility(View.GONE);
       // viewSwitcher.setDisplayedChild(1);
        gridGallery = (GridView) rootView.findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getActivity(), imageLoader);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);
        submit.setOnClickListener(this);
        cross.setOnClickListener(this);
        options_gallery.setOnClickListener(this);
        options_camera.setOnClickListener(this);
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void onClickCross() {
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                submitImageTask(HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
                break;
            case R.id.cross:
                dialog.dismiss();
                break;
            case R.id.options_camera:
                dispatchTakePictureIntent();
                break;
            case R.id.options_gallery:
                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 200);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            all_path = data.getStringArrayExtra("all_path");
            ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
            for (String string : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = string;
                dataT.add(item);
            }
            viewSwitcher.setVisibility(View.VISIBLE);
            viewSwitcher.setDisplayedChild(0);
         //   viewSwitcher.setDisplayedChild(1);
            adapter.addAll(dataT);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                Log.e("imageUri below:", "" + Uri.fromFile(imageFile));
                try {
                    all_path = new String[]{imageFile.getAbsolutePath()};
                    viewSwitcher.setVisibility(View.VISIBLE);
                    viewSwitcher.setDisplayedChild(1);
                    imageLoader.displayImage("file://" + all_path[0], imgSinglePick);
                } catch (Exception e) {
                }
            } else {
                Uri fileUri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider", imageFile);
                Log.i("imageUri:", "" + fileUri);
                all_path = new String[]{imageFile.getAbsolutePath()};
                viewSwitcher.setVisibility(View.VISIBLE);
                viewSwitcher.setDisplayedChild(1);
                imageLoader.displayImage("file://" + all_path[0], imgSinglePick);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private MultipartBody createResponseBody() {
        ArrayList<File> imagesData = getImagesData();
        insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        UploadImageSkeleton ske = new UploadImageSkeleton();
        ske.setCreated_by(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID));
        ske.setJob_id(jobid);
        ske.setDescription(et_laborperform.getText().toString());
        ArrayList<UploadImageSkeleton> arrayList = new ArrayList<>();
        arrayList.add(ske);
        gson = new Gson();
        String uploaded_data = gson.toJson(arrayList);
        insertIntoDB(insertedTime, arrayList, imagesData);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("uploaded_data", uploaded_data)
                /*.addFormDataPart("created_by", HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID))
                .addFormDataPart("job_id", jobid)
                .addFormDataPart("description", et_laborperform.getText().toString())*/
                .build();
        for (int i = 0; i < imagesData.size(); i++) {
            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("img_" + jobid + "[" + i + "]", "image" + i + ".png",
                            RequestBody.create(MEDIA_TYPE_FORM, imagesData.get(i)))
                    .build();
        }
        return builder.build();

    }

    private ArrayList<File> getImagesData() {
        ArrayList<File> imagesList = new ArrayList<>();
        for (String string : all_path) {
            File file = new File(string);
            Long s1 = file.length() / 1024;
            Log.d("fileSize :- ", "s1 = " + s1.toString() + "\n" +
                    "s2 = " + s1.toString());
            imagesList.add(file);
        }
        return imagesList;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);
        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public String getPath(Uri Uri, String id) {
        File imageFile;
        try {
            //    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri);
            Bitmap bitmap = new UserPicture(Uri, getActivity().getContentResolver()).getBitmap();
            //   addphoto_img.setImageBitmap(bitmap);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + File.separator + "SeatechImage");
            f.mkdir();
            imageFile = new File(f, "image_" + id + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return imageFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private void submitImageTask(String sessionid) {
        if (all_path == null) {
            HandyObject.showAlert(getActivity(), "Please Choose Images");
            return;
        } else if (all_path.length < 1) {
            HandyObject.showAlert(getActivity(), "Please Choose Images");
            // dialog.dismiss();
            return;
        }
        if (et_laborperform.getText().toString().length() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
            //dialog.dismiss();
            return;
        }

        MultipartBody responseBody = createResponseBody();
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.showProgressDialog(getActivity());
            final Context context = getActivity();
            HandyObject.getApiManagerMain().addJobImages(responseBody, sessionid).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String rep = response.body().string();
                        Log.e("Imageresponse ", rep);
                        JSONObject jsonObject = new JSONObject(rep);
                        Gson gson = new Gson();
                        Log.e("null", "null");
                        if (jsonObject.getString("status").toLowerCase().equals("success")) {
                            HandyObject.showAlert(context, jsonObject.getString("message"));
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            ArrayList<String> arrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jobjInside = jsonArray.getJSONObject(i);
                                String jobid = jobjInside.getString("job_id");

                                JSONArray jArray_upldImages = jobjInside.getJSONArray("uploads");
                                ArrayList<String> arraylistupldImages = new ArrayList<>();
                                for (int k = 0; k < jArray_upldImages.length(); k++) {
                                    arraylistupldImages.add(jArray_upldImages.getString(k));
                                }

                                Intent intent = new Intent("updateImage");
                                intent.putExtra("UpdatedImages_Jobid", jobid);
                                intent.putStringArrayListExtra("updateImageArray", arraylistupldImages);
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                                String images = gson.toJson(arraylistupldImages);
                                ContentValues cv = new ContentValues();
                                cv.put(ParseOpenHelper.JOBSTECHUPLOADEDIMAGESCURRDAY, images);
                                database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                        new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});
                            }

                            //Delete related row from database
                            database.delete(ParseOpenHelper.TABLE_UPLOADIMAGES, ParseOpenHelper.UPLOADIMAGESJOBID + " =? AND " + ParseOpenHelper.UPLOADIMAGESCREATEDAT + " = ?",
                                    new String[]{jobid, insertedTime});

                        } else if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                            HandyObject.clearpref(getActivity());
                            HandyObject.deleteAllDatabase(getActivity());
                            App.appInstance.stopTimer();
                            Intent intent_reg = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent_reg);
                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                        } else {
                            HandyObject.showAlert(context, jsonObject.getString("message"));
                        }
                        //}
                        dialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        HandyObject.stopProgressDialog();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    // co.myToast(t.getMessage());
                    HandyObject.stopProgressDialog();
                }
            });
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
            dialog.dismiss();
        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri fileUri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider", photoFile);
                //    activity.setCapturedImageURI(fileUri);
                //    activity.setCurrentPhotoPath(photoFile.getAbsolutePath());
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.setClipData(ClipData.newRawUri("", fileUri));
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_SEATECH" + timeStamp + "_";
        String dir = "";
        File file = null;
        try {
            dir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Seatech/";
            file = new File(dir);
            file.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageFile = new File(file, imageFileName + ".png");
        return imageFile;
    }

    private void insertIntoDB(String createdat, ArrayList<UploadImageSkeleton> arrayList, ArrayList<File> file) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.UPLOADIMAGESCREATEDBY, arrayList.get(0).getCreated_by());
        cv.put(ParseOpenHelper.UPLOADIMAGESJOBID, arrayList.get(0).getJob_id());
        cv.put(ParseOpenHelper.UPLOADIMAGESDESCR, arrayList.get(0).getDescription());
        cv.put(ParseOpenHelper.UPLOADIMAGESCREATEDAT, createdat);
        cv.put(ParseOpenHelper.UPLOADIMAGESALLIMAGE, gson.toJson(file));
        long idd = database.insert(ParseOpenHelper.TABLE_UPLOADIMAGES, null, cv);
    }
}

