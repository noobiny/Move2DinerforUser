package com.example.a0b.move2dinerforuser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class ActivityLoginForm extends AppCompatActivity {

    private EditText edit_email, edit_password;
    private Button btn_login, btn_regi_member;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_regi_member = (Button) findViewById(R.id.btn_regi_member);

        mAuth = FirebaseAuth.getInstance();

        //로그인 버튼 이벤트 연결
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edit_email.getText().toString();
                String pw = edit_password.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    edit_email.setError("이메일을 입력하세요");
                    return;
                }
                if (TextUtils.isEmpty(pw)) {
                    edit_password.setError("패스워드를 입력하세요");
                    return;
                }

                BaseApplication.getInstance().progressON(ActivityLoginForm.this, "로그인 하고 있습니다");
                loginUser(email, pw);
            }
        });

        //회원가입 버튼 이벤트 연결
        btn_regi_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLoginForm.this, ActivityJoinMemberShip.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(ActivityLoginForm.this, user.getDisplayName() + "님 반갑습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityLoginForm.this, ActivityMain.class);
                    startActivity(intent);
                    BaseApplication.getInstance().progressOFF();
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(ActivityLoginForm.this, "이메일과 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    BaseApplication.getInstance().progressOFF();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
