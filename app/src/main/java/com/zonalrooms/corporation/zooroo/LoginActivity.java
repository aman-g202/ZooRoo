package com.zonalrooms.corporation.zooroo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zonalrooms.corporation.zooroo.Network.Login;
import com.zonalrooms.corporation.zooroo.POJO.Users;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DELL on 5/6/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText inputNumber, inputPassword;
    private TextInputLayout inputLayoutNumber, inputLayoutPassword;
    private TextView textViewSignup;
    private Button btnLogIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        inputLayoutNumber = (TextInputLayout) findViewById(R.id.input_layout_number);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputNumber = (EditText) findViewById(R.id.input_number);
        inputPassword = (EditText) findViewById(R.id.input_password);
        textViewSignup = (TextView) findViewById(R.id.textviewaccountsignup);
        btnLogIn = (Button) findViewById(R.id.input_login);

        inputNumber.addTextChangedListener(new MyTextWatcher(inputNumber));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.addInterceptor(logging);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateNumber()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        String mobile = inputNumber.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        Retrofit check = new Retrofit.Builder()
                         .baseUrl("http://192.168.1.9:100/")
                         .client(getUnsafeOkHttpClient())
                         .addConverterFactory(GsonConverterFactory.create())
                         .build();
        Login service = check.create(Login.class);
        Call<Users> usersCall = service.checkUser(mobile,password);
        usersCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                String output = response.body().getResult();
                if (output.equals("Mobile Number or Password may be incorrect")){
                    Toast.makeText(LoginActivity.this,output,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this,output,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,SupermainActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.d("failure",t.getMessage());
            }
        });
    }

    private boolean validateNumber() {
        String number = inputNumber.getText().toString().trim();

        if (number.isEmpty() || !(number.length() == 10)) {
            inputLayoutNumber.setError(getString(R.string.err_msg_number));
            requestFocus(inputNumber);
            return false;
        }
        else {
            inputLayoutNumber.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_number:
                    validateNumber();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}
