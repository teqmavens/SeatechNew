package teq.development.seatech.ServerCall;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vibrantappz on 12/18/2017.
 */

public class apiManager {


    private static Retrofit retrofit = null;
    private static Retrofit retrofitadmin = null;
    private static String BaseApiAddress = "http://tqmstaging.com/seatech/users/";
    private static String BaseApiAddressadmin = "http://tqmstaging.com/seatech/admin/users/";

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

}
