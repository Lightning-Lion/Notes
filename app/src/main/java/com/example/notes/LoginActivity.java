package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Database database = new Database(this);

        EditText usernameInput = findViewById(R.id.et_username);
        EditText passwordInput = findViewById(R.id.et_password);
        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> {
            if (database.loginOrRegister(usernameInput.getText().toString(), passwordInput.getText().toString())) {
                Intent intent = new Intent(LoginActivity.this, FolderListActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
            }
        });
    }
}