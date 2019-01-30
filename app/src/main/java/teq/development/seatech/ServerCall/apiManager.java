package teq.development.seatech.ServerCall;



import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vibrantappz on 12/18/2017.
 */

public class apiManager {
//http://tqmstaging.com
//132.148.241.93/seatech
    private static Retrofit retrofit = null;
    private static Retrofit retrofitadmin,retrofitjobs ,retrofitmain= null;
    private static String MainBaseApiURL = "http://132.148.241.93/seatech/";
    private static String BaseApiAddress = "http://132.148.241.93/seatech/users/";
    private static String BaseApiAddressadmin = "http://132.148.241.93/seatech/admin/users/";
    private static String BaseApiAddressjobs= "http://132.148.241.93/seatech/jobs/";

    public static Retrofit getApiManager() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BaseApiAddress)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getApiManagerAdmin() {

      //  if (retrofitadmin == null) {
            retrofitadmin = new Retrofit.Builder()
                    .baseUrl(BaseApiAddressadmin)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        //}
        return retrofitadmin;
    }

    public static Retrofit getApiAddressjobs() {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(1, TimeUnit.MINUTES)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(15, TimeUnit.SECONDS)
//                .build();
        //  if (retrofitadmin == null) {
     //   if (retrofitjobs == null) {
            retrofitjobs = new Retrofit.Builder()
                    .baseUrl(BaseApiAddressjobs)
                    .addConverterFactory(GsonConverterFactory.create())
                   // .client(okHttpClient)
                    .build();
     //   }
        //}
        return retrofitjobs;
    }

    public static Retrofit getApiManagerMain() {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(1, TimeUnit.MINUTES)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(15, TimeUnit.SECONDS)
//                .build();
        if (retrofitmain == null) {
            retrofitmain = new Retrofit.Builder()
                    .baseUrl(MainBaseApiURL)
                    .addConverterFactory(GsonConverterFactory.create())
                   // .client(okHttpClient)
                    .build();
        }
        return retrofitmain;
    }

    public static Retrofit getApiManagerMainRx() {

        if (retrofitmain == null) {
            retrofitmain = new Retrofit.Builder()
                    .baseUrl(MainBaseApiURL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitmain;
    }

}
