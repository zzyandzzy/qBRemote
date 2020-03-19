package xyz.zzyitj.qbremote.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import xyz.zzyitj.qbremote.MainActivity;
import xyz.zzyitj.qbremote.MyApplication;
import xyz.zzyitj.qbremote.R;
import xyz.zzyitj.qbremote.api.AuthService;
import xyz.zzyitj.qbremote.model.Server;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/10 5:31 下午
 * @email zzy.main@gmail.com
 */
public class AddServerActivity extends AppCompatActivity {
    private static final String TAG = AddServerActivity.class.getSimpleName();

    private MyApplication myApplication;

    private EditText serverNameEdit;
    private Spinner protocolSpinner;
    private EditText hostEdit;
    private EditText portEdit;
    //    private EditText rpcUrlEdit;
//    private Button defaultRpcUrlButton;
    private CheckBox signedSslCheckbox;
    private CheckBox authenticationCheckbox;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
        init();
        initViews();
        initDatas();
    }

    @SuppressLint("CheckResult")
    private void initDatas() {
        protocolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String portStr = (String) parent.getItemAtPosition(position);
                portEdit.setText(portStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        defaultRpcUrlButton.setOnClickListener(view -> rpcUrlEdit.setText(Server.API_BASE_URL));
        if (authenticationCheckbox.isChecked()) {
            usernameEdit.setEnabled(true);
            passwordEdit.setEnabled(true);
        }
        authenticationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                usernameEdit.setEnabled(true);
                passwordEdit.setEnabled(true);
            } else {
                usernameEdit.setEnabled(false);
                passwordEdit.setEnabled(false);
            }
        });
        saveButton.setOnClickListener(v -> {
            boolean isCheck = checkRule();
            if (isCheck) {
                Server server = getServer();
                Log.i(TAG, "initDatas: " + server.toString());
                AuthService.test(server)
                        .subscribe(responseBody -> {
                            String body = responseBody.string();
                            Log.i(TAG, "initDatas: " + body);
                            if (body.contains("Ok")) {
                                Toast.makeText(AddServerActivity.this, getString(R.string.server_test_success), Toast.LENGTH_SHORT).show();
                                myApplication.saveServer(server);
                                startActivity(new Intent(AddServerActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(AddServerActivity.this, getString(R.string.server_test_account_error), Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            Toast.makeText(AddServerActivity.this, getString(R.string.server_test_error), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "initDatas: ", throwable);
                        });
            }
        });
    }

    private Server getServer() {
        Server server = new Server();
        server.setName(serverNameEdit.getText().toString());
        server.setHost(hostEdit.getText().toString());
        server.setPort(Integer.parseInt(portEdit.getText().toString()));
//        if (!TextUtils.isEmpty(portEdit.getText())) {
//            server.setRpcUrl(rpcUrlEdit.getText().toString());
//        }
        if (signedSslCheckbox.isChecked()) {
            server.setHttps(true);
        }
        if (authenticationCheckbox.isChecked()) {
            if (!TextUtils.isEmpty(usernameEdit.getText())) {
                server.setUsername(usernameEdit.getText().toString());
            }
            if (!TextUtils.isEmpty(passwordEdit.getText())) {
                server.setPassword(passwordEdit.getText().toString());
            }
        }
        return server;
    }

    private boolean checkRule() {
        if (TextUtils.isEmpty(serverNameEdit.getText())) {
            Toast.makeText(this, getText(R.string.server_name_error_message), Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(hostEdit.getText())) {
                Toast.makeText(this, getText(R.string.host_name_error_message), Toast.LENGTH_SHORT).show();
            } else {
                if (TextUtils.isEmpty(portEdit.getText())) {
                    Toast.makeText(this, getText(R.string.port_number_error_message), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Integer.parseInt(portEdit.getText().toString());
                        return true;
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, getText(R.string.port_number_error_message), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return false;
    }

    private void init() {
        myApplication = new MyApplication(this);
    }

    private void initViews() {
        serverNameEdit = findViewById(R.id.server_name_edit_text);
        protocolSpinner = findViewById(R.id.protocol_spinner);
        hostEdit = findViewById(R.id.host_edit_text);
        portEdit = findViewById(R.id.port_edit_text);
//        rpcUrlEdit = findViewById(R.id.rpc_url_edit_text);
//        defaultRpcUrlButton = findViewById(R.id.default_rpc_url_button);
        signedSslCheckbox = findViewById(R.id.self_signed_ssl_checkbox);
        authenticationCheckbox = findViewById(R.id.authentication_checkbox);
        usernameEdit = findViewById(R.id.user_name_edit_text);
        passwordEdit = findViewById(R.id.password_edit_text);
        saveButton = findViewById(R.id.save_button);
    }
}
