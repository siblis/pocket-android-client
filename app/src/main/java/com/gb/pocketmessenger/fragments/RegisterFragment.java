package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.Correct;

import java.util.concurrent.ExecutionException;

import static com.gb.pocketmessenger.Constants.CURRENT_SERVER;

public class RegisterFragment extends Fragment {

    private EditText loginEditText;
    private TextView serverResponse;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button registerButton;
    private Button cancelButton;
    private TextView loginText;
    private TextView passwordText;
    private TextView emailText;
    private int loginIndex = 0;
    private int emailIndex = 0;
    private int pswdIndex = 0;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        loginText = view.findViewById(R.id.login_text);
        passwordText = view.findViewById(R.id.password_text);
        emailText = view.findViewById(R.id.email_text);
        serverResponse = view.findViewById(R.id.server_response);
        loginEditText = view.findViewById(R.id.login_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        emailEditText = view.findViewById(R.id.email_edittext);
        registerButton = view.findViewById(R.id.register_ok_button);
        cancelButton = view.findViewById(R.id.register_cancel_button);

        loginEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (Correct.containsWhiteSpace(loginEditText.getText().toString()) || loginEditText.getText().toString().length() == 0)
                    loginIndex = 0;
                else loginIndex = 1;

                switch (loginIndex) {
                    case 0:
                        loginText.setTextColor(getResources().getColor(R.color.color0));
                        loginText.setText("invalid login");
                        break;
                    case 1:
                        loginText.setTextColor(getResources().getColor(R.color.color4));
                        loginText.setText("valid login");
                        break;
                    default:
                        break;
                }

                Log.d("tar", "Login: " + loginIndex);

                return false;
            }
        });

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                pswdIndex = Correct.pswCheck(passwordEditText.getText().toString());
                switch (pswdIndex) {
                    case 0:
                        passwordText.setTextColor(getResources().getColor(R.color.color0));
                        passwordText.setText("restricted password");
                        break;
                    case 1:
                        passwordText.setTextColor(getResources().getColor(R.color.color1));
                        passwordText.setText("very weak");
                        break;
                    case 2:
                        passwordText.setTextColor(getResources().getColor(R.color.color2));
                        passwordText.setText("weak");
                        break;
                    case 3:
                        passwordText.setTextColor(getResources().getColor(R.color.color3));
                        passwordText.setText("normal");
                        break;
                    case 4:
                        passwordText.setTextColor(getResources().getColor(R.color.color4));
                        passwordText.setText("strong");
                        break;
                    default:
                        break;
                }

                Log.d("tar", "Password: " + pswdIndex);
                return false;
            }
        });

        emailEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (Correct.isValidEmail(emailEditText.getText().toString())) emailIndex = 1;
                else emailIndex = 0;

                switch (emailIndex) {
                    case 0:
                        emailText.setTextColor(getResources().getColor(R.color.color0));
                        emailText.setText("invalid email");
                        break;
                    case 1:
                        emailText.setTextColor(getResources().getColor(R.color.color4));
                        emailText.setText("valid email");
                        break;
                    default:
                        break;
                }

                Log.d("tar", "Email: " + emailIndex);

                return false;
            }
        });

        registerButton.setOnClickListener(v ->
        {
            if (loginIndex == 1 && pswdIndex != 0 && emailIndex == 1) {
                Toast.makeText(getContext(), "Registration...", Toast.LENGTH_SHORT).show();
                sendRegisterData(loginEditText.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            } else Toast.makeText(getContext(), "Registation Error!", Toast.LENGTH_SHORT).show();


        });
        return view;
    }

    private void sendRegisterData(String login, String email, String password) {
        User newUser = new User(login, password);
        newUser.seteMail(email);
        ConnectionToServer connection = new ConnectionToServer("REGISTER", newUser);
        connection.execute(CURRENT_SERVER);
        try {
            serverResponse.setText(connection.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
