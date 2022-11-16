package com.example.ws8;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;


public class Logo extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private TextView attempts;
    private TextView numberOfAttempts;
    private TextView loginLocked;
    private ImageButton reg1;

    int numberOfRemainingLoginAttempts = 3;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);

        reg1 = (ImageButton) findViewById(R.id.reg);

        loginLocked = (TextView) findViewById(R.id.login_locked);
        attempts = (TextView) findViewById(R.id.namber_of_attempts);
        numberOfAttempts = (TextView) findViewById(R.id.namber_of_attempts);

        numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://app.swaggerhub.com/apis-docs/k5422/smart/1.0.0-oas3#/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);

        findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User u = new User();

                EditText email = findViewById(R.id.email);
                EditText password = findViewById(R.id.password);

                TextView t = findViewById(R.id.text);

                u.setEmail(email.getText().toString());
                u.setPassword(password.getText().toString());

                Call<UserData> login = api.login(u);

                login.enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call, Response<UserData> response) {
                        Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_LONG).show();
                        t.setText(response.body().toString());

                        Toast.makeText(getApplicationContext(), "Вход выполнен!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Logo.this,MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
                        isAllFieldsChecked = CheckAllFields();
                        Toast.makeText(getApplicationContext(), "Данные неверны!", Toast.LENGTH_SHORT).show();
                        numberOfRemainingLoginAttempts--;

                        // Делаем видимыми текстовые поля, указывающие на количество оставшихся попыток:
                        attempts.setVisibility(View.VISIBLE);
                        numberOfAttempts.setVisibility(View.VISIBLE);
                        numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));

                        // Когда выполнено 3 безуспешных попытки залогиниться,
                        // делаем видимым текстовое поле с надписью, что все пропало и выставляем
                        // кнопке настройку невозможности нажатия setEnabled(false):
                        if (numberOfRemainingLoginAttempts == 0) {
                            reg1.setEnabled(false);
                            loginLocked.setVisibility(View.VISIBLE);
                            loginLocked.setBackgroundColor(Color.RED);
                            loginLocked.setText("Вход заблокирован!");
                        }
                    }
                });
            }
        });
    }


    public void save(View view) {
        Intent mainIntent = new Intent(Logo.this, MainActivity.class);
        Logo.this.startActivity(mainIntent);
        Logo.this.finish();

    }

    public interface API {
        @POST("user/login")
        Call<UserData> login(@Body User user);
    }

    public static class User {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public class UserData {
        private String id;
        private String email;
        private String nickName;
        private String avatar;
        private String token;

        public String toString() {
            return id + "; " + email + "; " + nickName + "; " + avatar + "; " + token;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    private boolean CheckAllFields() {
        if (Email.length() == 0) {
            Email.setError("Введите Email");
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            FragmentManager manager = getSupportFragmentManager();
            //myDialogFragment.show(manager, "dialog");

            FragmentTransaction transaction = manager.beginTransaction();
            myDialogFragment.show(transaction, "dialog");
            return false;
        }
        if (Password.length() == 0) {
            Password.setError("Введите Password");
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            FragmentManager manager = getSupportFragmentManager();
            //myDialogFragment.show(manager, "dialog");

            FragmentTransaction transaction = manager.beginTransaction();
            myDialogFragment.show(transaction, "dialog");
            return false;
        }
        return true ;
    }
}