package app.currency.converter.retrofit;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @GET("v4/latest/{currency}")
    Call<JsonObject> getExchangeCurrency(@Path("currency") String currency);

    @GET("https://raw.githubusercontent.com/fawazahmed0/exchange-api/main/other/currencies.json")
    Call<Map<String, String>> getCurrencies();
}
