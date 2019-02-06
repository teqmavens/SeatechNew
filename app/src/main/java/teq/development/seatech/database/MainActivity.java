package teq.development.seatech.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class MainActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPick;
	Button btnGalleryPickMul;
	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;
	String[] all_path;
	MediaType MEDIA_TYPE_FORM = MediaType.parse("image/png");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		initImageLoader();
		init();
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	private void init() {
		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);

		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);
		btnGalleryPick = (Button) findViewById(R.id.btnGalleryPick);
		btnGalleryPick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
/*
				Intent i = new Intent(Action.ACTION_PICK);
				startActivityForResult(i, 100);*/
				submitImageTask(HandyObject.getPrams(MainActivity.this,AppConstants.LOGIN_SESSIONID));

			}
		});

		btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
		btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
				startActivityForResult(i, 200);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			adapter.clear();

			viewSwitcher.setDisplayedChild(1);
			String single_path = data.getStringExtra("single_path");
			imageLoader.displayImage("file://" + single_path, imgSinglePick);

		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;
				dataT.add(item);
			}
			viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataT,new String[0]);
		}
	}

	private MultipartBody createResponseBody() {

		MultipartBody.Builder builder = new MultipartBody.Builder();

		builder.setType(MultipartBody.FORM)

				.addFormDataPart("created_by", "4")
				.addFormDataPart("job_id", "620181")
				.addFormDataPart("description", "safdfds")
				.build();


		ArrayList<File> imagesData = getImagesData();

		// co.showLoading();
		for (int i = 0; i < imagesData.size(); i++) {
			builder.setType(MultipartBody.FORM)
					.addFormDataPart("images[" + i + "]", "image" + i + ".png",
							RequestBody.create(MEDIA_TYPE_FORM, imagesData.get(i)))
					.build();

		}

		// co.hideLoading();

		return builder.build();
	}

	private ArrayList<File> getImagesData() {
		ArrayList<File> imagesList = new ArrayList<>();



		for (String string : all_path) {
			File file = new File(string);

			Long s1 = file.length() / 1024;
			//  File f = compressImage(file);
			//   Long s2 = f.length() / 1024;

			Log.d("fileSize :- ", "s1 = " + s1.toString() + "\n" +
					"s2 = " + s1.toString());


			imagesList.add(file);
		}


		return imagesList;
	}


	private void submitImageTask(String sessionid) {
		if (all_path.length < 1) {
			HandyObject.showAlert(MainActivity.this, "Please Choose Images Of Product");
			return;
		}

		MultipartBody responseBody = createResponseBody();
		if (HandyObject.checkInternetConnection(MainActivity.this)) {
			HandyObject.showProgressDialog(MainActivity.this);

			HandyObject.getApiManagerMain().addJobImages(responseBody, sessionid).enqueue(new Callback<ResponseBody>() {
				@Override
				public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
					try {
						String rep = response.body().string();
						Log.e("Imageresponse ", rep);
						JSONObject jsonObject = new JSONObject(rep);
						//    co.myToast(jsonObject.getString("alert"));
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
		}

	}
}
