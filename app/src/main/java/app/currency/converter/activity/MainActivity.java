package app.currency.converter.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.google.gson.JsonObject;

import app.currency.converter.R;
import app.currency.converter.retrofit.RetrofitBuilder;
import app.currency.converter.retrofit.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity  extends AppCompatActivity {
    // Variables to be used
    Button button;
    EditText currencyToBeConverted;
    EditText currencyConverted;
    Spinner convertToDropdown;
    Spinner convertFromDropdown;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RetrofitInterface retrofitInterface = RetrofitBuilder.getRetrofitInstance().create(RetrofitInterface.class);
        //Initialization
        currencyConverted = (EditText) findViewById(R.id.currency_converted);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        currencyToBeConverted = (EditText) findViewById(R.id.currency_to_be_converted);
        convertToDropdown = (Spinner) findViewById(R.id.convert_to);
        convertFromDropdown = (Spinner) findViewById(R.id.convert_from);
        button = (Button) findViewById(R.id.button);

        //Adding Functionality
        Call<Map<String, String>> call = retrofitInterface.getCurrencies();
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the currency codes
                    Map<String, String> currencies = response.body();
                    List<String> currencyList = new ArrayList<>(currencies.keySet());

                    // Create an ArrayAdapter to populate the Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, currencyList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Set the adapter for the Spinner
                    convertToDropdown.setAdapter(adapter);
                    convertFromDropdown.setAdapter(adapter);
                } else {
                    Log.e("API Error", "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("API Error", "Failed to fetch data", t);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Using the RetrofitInterface to get currency rates
                progressBar.setVisibility(View.VISIBLE);
                Call<JsonObject> call = retrofitInterface.getExchangeCurrency(convertFromDropdown.getSelectedItem().toString().toUpperCase());
                call.enqueue(new Callback<JsonObject>() {
                    // onResponse controls the logic and calculation for the exchange rates
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        progressBar.setVisibility(View.GONE);
                        JsonObject res = response.body();
                        if(res !=  null && res.getAsJsonObject("rates") != null){
                            JsonObject rates = res.getAsJsonObject("rates");
                            double currency = Double.valueOf(currencyToBeConverted.getText().toString());
                            double multiplier = Double.valueOf(rates.get(convertToDropdown.getSelectedItem().toString().toUpperCase()).toString());
                            double result = currency * multiplier;
                            currencyConverted.setText(String.valueOf(result));
                        }else {
                            currencyConverted.setText(String.valueOf(Double.valueOf(currencyToBeConverted.getText().toString())));
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });



    }
}
