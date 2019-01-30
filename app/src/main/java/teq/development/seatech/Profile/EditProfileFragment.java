package teq.development.seatech.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.Utils.UserPicture;
import teq.development.seatech.databinding.FrgmEditprofileBinding;

public class EditProfileFragment extends Fragment {

    private static final int REQUEST_CAMERA = 0;
    DashBoardActivity activity;
    FrgmEditprofileBinding binding;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private static final int PICK_IMAGE_FROM_GALLARY = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 5484;
    MediaType MEDIA_TYPE_FORM = MediaType.parse("image/png");
    File imageFile;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_editprofile, container, false);
        check_RequestPermission();
        binding = DataBindingUtil.bind(rootView);
        binding.setEditprofile(this);
        /*Set related profile data from local database*/
        setLocaldata();
        return rootView;
    }

    private void setLocaldata() {
        binding.etFirstname.setText(HandyObject.getPrams(context, AppConstants.LOGINTEQ_FIRSTNAME));
        binding.etMiddlename.setText(HandyObject.getPrams(context, AppConstants.LOGINTEQ_MIDDLENAME));
        binding.etLastname.setText(HandyObject.getPrams(context, AppConstants.LOGINTEQ_LASTNAME));
        binding.etDob.setText(HandyObject.getPrams(context, AppConstants.LOGINTEQ_DOB));
        binding.etPhone.setText(HandyObject.getPrams(context, AppConstants.LOGINTEQ_PHONE));
        binding.etDescription.setText(HandyObject.getPrams(context, AppConstants.LOGINTEQ_DESCRIPTION));
        binding.etRole.setText(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ROLE));
        binding.profileimage.setImageURI(HandyObject.getPrams(context, AppConstants.LOGINTEQ_IMAGE));
        if (HandyObject.getPrams(context, AppConstants.LOGINTEQ_STATUS).equalsIgnoreCase("1")) {
           // binding.checkboxstatus.setChecked(true);
            binding.checkboxstatus.setText("ACTIVE");
        } else {
           // binding.checkboxstatus.setChecked(false);
            binding.checkboxstatus.setText("INACTIVE");
        }

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (view.isShown()) {
                    long selectedMilli = myCalendar.getTimeInMillis();
                    Date datePickerDate = new Date(selectedMilli);

                    SimpleDateFormat datePickerDate_f = new SimpleDateFormat("MM/dd/yyyy");
                    String formattedDate = datePickerDate_f.format(datePickerDate);
                    String formattedDatenew = datePickerDate_f.format(new Date());

                    if (formattedDate.split("/")[1].equalsIgnoreCase(formattedDatenew.split("/")[1])) {
                        updateLabel();
                    }
                    else if (datePickerDate.before(new Date())) {
                        HandyObject.showAlert(getActivity(), "Can't Select Previous date");
                    }
                    else {
                        updateLabel();
                    }
                }

            }
        };
        List<String> list = new ArrayList<String>();
        list.add("Male");
        list.add("Female");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnergender.setAdapter(dataAdapter);
        if (HandyObject.getPrams(context, AppConstants.LOGINTEQ_GENDER).equalsIgnoreCase("M")) {
            binding.spinnergender.setSelection(0);
        } else {
            binding.spinnergender.setSelection(1);
        }
    }

    public void OnClickDOB() {
        // Opening Date Picker
        new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void OnClickCancel() {
        /*Navigate to previous MyProfile screen*/
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void updateLabel() {
        //  String myFormat = "yyyy-MM-dd"; //In which you need put here
        // SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        // binding.etDob.setText(sdf.format(myCalendar.getTime()));
        // String myFormat = "MMM dd yyyy"; //In which you need put here
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        binding.etDob.setText(sdf.format(myCalendar.getTime()));
    }

    public void OnClickEditImage() {
        displayMediaPickerDialog();
    }

    /*Display Dialog to choose image.*/
    private void displayMediaPickerDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog mediaDialog = new Dialog(getActivity());
        mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mediaDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mediaDialog.setContentView(R.layout.dialog_media_picker);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 30, (h / 3) - 100);
        approx_lay.setLayoutParams(params);

        TextView options_camera = (TextView) mediaDialog.findViewById(R.id.options_camera);
        options_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                dispatchTakePictureIntent();
            }
        });
        TextView options_gallery = (TextView) mediaDialog.findViewById(R.id.options_gallery);
        options_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                dispatchGallaryPictureIntent();
            }
        });
        TextView options_cancel = (TextView) mediaDialog.findViewById(R.id.options_cancel);
        options_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
            }
        });
        mediaDialog.show();
    }

    /*Get stored image Path from Uri*/
    public String getPath(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e("imageError", e.getMessage());
        }
        return null;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*Ensure that there's a camera activity to handle the intent*/
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            /*Create the File where the photo should go*/
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
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

    private void dispatchGallaryPictureIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_FROM_GALLARY);
    }

    private File createImageFile() throws IOException {
        /*Create an image file name for image*/
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_FROM_GALLARY) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String imagepath = getPath(selectedImageUri);
                imageFile = new File(imagepath);
                try {
                    /*Set selected image to ImageView*/
                    Bitmap bitmap = new UserPicture(selectedImageUri, context.getContentResolver()).getBitmap();
                    binding.profileimage.setImageBitmap(bitmap);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Check for android version to show image
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                Log.e("imageUri below:", "" + Uri.fromFile(imageFile));
                if(imageFile.length() > 0) {
                    try {
                        /*Set captured image to ImageView*/
                        Bitmap bitmap = new UserPicture(Uri.fromFile(imageFile), context.getContentResolver()).getBitmap();
                        binding.profileimage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                    }
                }
            } else {
                /*Set captured image to ImageView*/
                if(imageFile.length() > 0) {
                    try {
                        Uri fileUri = FileProvider.getUriForFile(getActivity(), "com.example.android.fileprovider", imageFile);
                        Log.i("imageUri:", "" + fileUri);
                        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        binding.profileimage.setImageBitmap(myBitmap);
                    } catch (Exception e){}
                }
            }
        }
    }

    /*Check camera and write data permission*/
    void check_RequestPermission() {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Permission is needed")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                ActivityCompat.requestPermissions((Activity) context,
                                        new String[]{android.Manifest.permission.CAMERA},
                                        REQUEST_CAMERA);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
        } else if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Permission is needed")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        2);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA: {
                /*If request is cancelled, the result arrays are empty.*/
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*permission was granted*/
                    // displayMediaPickerDialog();
                    check_RequestPermission();

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.CAMERA)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Permission is needed to access Scanner")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{android.Manifest.permission.CAMERA},
                                                REQUEST_CAMERA);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        HandyObject.showAlert(getActivity(), "Manually turn on camera permission");
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*permission was granted*/
                    check_RequestPermission();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Permission is needed")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                2);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(getActivity(), "Manually turn on permission", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                return;
            }
        }
    }

    public String parseDateToyyyyMMdd(String time) {
       /* String inputPattern = "MMM dd yyyy";
        String outputPattern = "yyyy-MM-dd";*/
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public void OnClickSave() {
        if (binding.spinnergender.getSelectedItem().toString().equalsIgnoreCase("Male")) {
            UpdateProfileTask(binding.etFirstname.getText().toString(),binding.etMiddlename.getText().toString(),binding.etLastname.getText().toString(),binding.etPhone.getText().toString(), "M", HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID), parseDateToyyyyMMdd(binding.etDob.getText().toString()));
        } else {
            UpdateProfileTask(binding.etFirstname.getText().toString(),binding.etMiddlename.getText().toString(),binding.etLastname.getText().toString(),binding.etPhone.getText().toString(), "F", HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID), parseDateToyyyyMMdd(binding.etDob.getText().toString()));
        }
    }

    /*API fo updating user profile*/
    private void UpdateProfileTask(String firstname,String secondname,String thirdname,String phone, String gender, String sessionid, String dob) {
        if (HandyObject.checkInternetConnection(getActivity())) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("firstname", firstname)
                    .addFormDataPart("middlename", secondname)
                    .addFormDataPart("lastname", thirdname)
                    .addFormDataPart("user_id", md5(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)))
                    .addFormDataPart("parent_id", HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID))
                    .addFormDataPart("description", binding.etDescription.getText().toString())
                    .addFormDataPart("gender", gender)
                    .addFormDataPart("phone", phone)
                    .addFormDataPart("dob", dob)
                    .addFormDataPart("email", HandyObject.getPrams(context, AppConstants.LOGINTEQ_EMAIL));
            if (imageFile != null) {
                if (imageFile.length() > 0) {
                    builder.setType(MultipartBody.FORM).addFormDataPart("image", "images.png", RequestBody.create(MEDIA_TYPE_FORM, imageFile)).build();
                }
            }


      /*  if (imageFile == null) {
            Log.e("FILENULL","FILENULL");
        } else {
            Log.e("FILENOTTT","FILENOTTT");
            builder.setType(MultipartBody.FORM).addFormDataPart("image", "images.png", RequestBody.create(MEDIA_TYPE_FORM, imageFile)).build();
        }*/
            /* builder.setType(MultipartBody.FORM).addFormDataPart("image", "images.png", RequestBody.create(MEDIA_TYPE_FORM, imageFile)).build();*/
            MultipartBody requestBody = builder.build();
            HandyObject.showProgressDialog(getActivity());

            HandyObject.getApiManagerTypeAdmin().updateProfile(requestBody, sessionid).enqueue(
                    new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                JSONObject jsonObject = new JSONObject(jsonResponse);

                                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                    // commonObjects.myToast(getActivity(), getString(R.string.image_load_error));
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                        HandyObject.clearpref(getActivity());
                                        HandyObject.deleteAllDatabase(getActivity());
                                        App.appInstance.stopTimer();
                                        Intent intent_reg = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent_reg);
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                    }
                                }

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
                            Log.e("responseError", t.getMessage());
                            // commonObjects.showHideProgressBar(getActivity());
                            HandyObject.stopProgressDialog();
                        }
                    }
            );
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.check_internet_connection));
        }
    }

    /*Create MD5 for user login id*/
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
