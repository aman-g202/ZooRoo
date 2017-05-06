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

import com.zonalrooms.corporation.zooroo.Network.Registration;
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

public class SignupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputName, inputMobile, inputEmail, inputPassword, inputRepassword;
    private TextInputLayout inputLayoutName, inputLayoutMobile, inputLayoutEmail, inputLayoutPassword, inputLayoutRepassword;
    private TextView textViewLogin;
    private Button btnsignup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signuplayout);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutRepassword = (TextInputLayout) findViewById(R.id.input_layout_repassword);
        inputName = (EditText) findViewById(R.id.editTextname);
        inputMobile = (EditText) findViewById(R.id.editTextmobile);
        inputEmail = (EditText) findViewById(R.id.editTextemail);
        inputPassword = (EditText) findViewById(R.id.editTextpassword);
        inputRepassword = (EditText) findViewById(R.id.editTextrepassword);
        textViewLogin = (TextView) findViewById(R.id.textviewaccountlogin);
        btnsignup = (Button) findViewById(R.id.finalsignupbutton);

        inputName.addTextChangedListener(new SignupActivity.MyTextWatcher(inputName));
        inputMobile.addTextChangedListener(new SignupActivity.MyTextWatcher(inputMobile));
        inputEmail.addTextChangedListener(new SignupActivity.MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new SignupActivity.MyTextWatcher(inputPassword));
        inputRepassword.addTextChangedListener(new SignupActivity.MyTextWatcher(inputRepassword));

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
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

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateMobile()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if (!validateRepassword()) {
            return;
        }

        String name = inputName.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String repassword = inputRepassword.getText().toString().trim();

        Retrofit registration = new Retrofit.Builder()
                                .baseUrl("http://192.168.1.9:100/")
                                .client(getUnsafeOkHttpClient())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
        Registration service = registration.create(Registration.class);
        Call<Users> usersCall = service.insertUser(name,mobile,email,password,repassword);
        usersCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                String output = response.body().getResult();
                Toast.makeText(SignupActivity.this,output,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.d("failure",t.getMessage());
            }
        });
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobile() {
        String mobile = inputMobile.getText().toString().trim();
        if (mobile.isEmpty() || !(mobile.length() == 10)) {
            inputLayoutMobile.setError(getString(R.string.err_msg_number));
            requestFocus(inputMobile);
            return false;
        }
        else {
            inputLayoutMobile.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        String password = inputPassword.getText().toString().trim();
        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        }
        else if (!(password.length() > 4)){
            inputLayoutPassword.setError(getString(R.string.err_msg_lengthpassword));
            requestFocus(inputPassword);
            return false;
        }
        else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateRepassword() {
        if (inputRepassword.getText().toString().trim().isEmpty()) {
            inputLayoutRepassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputRepassword);
            return false;
        }
        else if (!inputPassword.getText().toString().trim().equals(inputRepassword.getText().toString().trim())){
            inputLayoutRepassword.setError(getString(R.string.err_msg_checkpassword));
            requestFocus(inputRepassword);
            return false;
        }
        else {
            inputLayoutRepassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                case R.id.editTextname:
                    validateName();
                    break;
                case R.id.editTextmobile:
                    validateMobile();
                    break;
                case R.id.editTextemail:
                    validateEmail();
                    break;
                case R.id.editTextpassword:
                    validatePassword();
                    break;
                case R.id.editTextrepassword:
                    validateRepassword();
                    break;
            }
        }
    }
}
