package e.sanjay.kangaroorooms;

import java.util.zip.Checksum;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    //this is the URL of the paytm folder that we added in the server
    //make sure you are using your ip else it will not work
    //String BASE_URL = "http://192.168.43.199/paytm/";//doubtError2
    String BASE_URL = "https://kangarooroomspaytmchecksum.000webhostapp.com/paytm/";

    @FormUrlEncoded
    @POST("generateChecksum.php")
    Call<Checksum> getChecksum(
            @Field("MID") String mId,
            @Field("ORDER_ID") String orderId,
            @Field("CUST_ID") String custId,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId,
            @Field("CHANNEL_ID") String channelId,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl
    );

}