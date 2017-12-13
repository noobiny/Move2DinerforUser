package com.example.a0b.move2dinerforuser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


import java.util.regex.Pattern;

public class ActivityJoinMemberShip extends AppCompatActivity {
    private TextInputEditText edit_regi_email;
    private TextInputEditText edit_regi_password;
    private EditText edit_regi_nickname;
    private Button btn_regi_confirm;
    private TextInputLayout passwordcheck, emailcheck;
    private Pattern patternEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_membership);

        initView();
        mAuth = FirebaseAuth.getInstance();

        edit_regi_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                CharSequence s = ((EditText) v).getText();
                if (hasFocus == false) {
                    if (!patternEmail.matcher(s).find() || s.length() == 0) {
                        edit_regi_email.setError("이메일을 입력하세요(이메일 형식)");
                    }
                } else {
                    edit_regi_email.setError(null);
                }
            }
        });


        edit_regi_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                CharSequence s = ((EditText) v).getText();

                if (hasFocus == false) {
                    if (s.length() >= 0 && s.length() < 6)
                        edit_regi_password.setError("패스워드를 확인 하세요(6자 이상)");
                } else {
                    edit_regi_password.setError(null);
                }
            }
        });

        //이벤트 연결
        btn_regi_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edit_regi_email.getText().toString();
                String pw = edit_regi_password.getText().toString();
                String nick = edit_regi_nickname.getText().toString();
                if (TextUtils.isEmpty(email) || edit_regi_email.getError() != null) {
                    edit_regi_email.setError("이메일을 입력하세요(이메일 형식)");
                    return;
                }
                if (TextUtils.isEmpty(pw) || edit_regi_password.getError() != null) {
                    edit_regi_password.setError("패스워드를 확인 하세요(6자 이상)");
                    return;
                }
                if (TextUtils.isEmpty(nick)) {
                    edit_regi_nickname.setError("닉네임을 입력하세요");
                    return;
                }

                BaseApplication.getInstance().progressON(ActivityJoinMemberShip.this, "계정 생성중");
                createUser(email, pw, nick);
            }
        });
        findViewById(R.id.btn_regi_confirm_Cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        emailcheck = (TextInputLayout) findViewById(R.id.emailcheck);
        passwordcheck = (TextInputLayout) findViewById(R.id.passwordcheck);
        edit_regi_email = (TextInputEditText) findViewById(R.id.edit_regi_email);
        edit_regi_password = (TextInputEditText) findViewById(R.id.edit_regi_password);
        edit_regi_nickname = (EditText) findViewById(R.id.edit_regi_nickname);
        btn_regi_confirm = (Button) findViewById(R.id.btn_regi_confirm);

        emailcheck.setErrorEnabled(true);
        passwordcheck.setCounterEnabled(true);
        passwordcheck.setCounterMaxLength(15);
        passwordcheck.setErrorEnabled(true);
    }

    //이메일 회원가입 처리
    private void createUser(final String email, final String password, final String nickname) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();

                    //닉네임 설정
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nickname).build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                    }
                                }
                            });

                    loginUser(email, password);
                } else {
                    // If sign in fails, display a message to the user.
                    BaseApplication.getInstance().progressOFF();
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    BaseApplication.getInstance().progressOFF();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(ActivityJoinMemberShip.this, user.getDisplayName() + "님 반갑습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityJoinMemberShip.this, ActivityMain.class);
                    startActivity(intent);
                } else {
                    BaseApplication.getInstance().progressOFF();
                    // If sign in fails, display a message to the user.
                    Toast.makeText(ActivityJoinMemberShip.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}