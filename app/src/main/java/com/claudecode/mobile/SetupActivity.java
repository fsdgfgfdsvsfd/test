package com.claudecode.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetupActivity extends AppCompatActivity {

    private EditText editApiUrl;
    private EditText editApiKey;
    private Button btnLoadModels;
    private Spinner spinnerModels;
    private Button btnSelectFolder;
    private TextView tvFolderPath;
    private Button btnStartChat;
    
    private OkHttpClient client;
    private ArrayAdapter<String> modelAdapter;
    private List<String> modelNames = new ArrayList<>();
    private String selectedModel = "";
    private Uri selectedFolderUri = null;
    
    private SharedPreferences prefs;
    
    private final ActivityResultLauncher<Intent> folderPickerLauncher = 
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedFolderUri = result.getData().getData();
                if (selectedFolderUri != null) {
                    tvFolderPath.setText(selectedFolderUri.getPath());
                    prefs.edit().putString("folder_uri", selectedFolderUri.toString()).apply();
                    checkReadyToStart();
                }
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        
        prefs = getSharedPreferences("claude_code_prefs", MODE_PRIVATE);
        client = new OkHttpClient();
        
        // Load saved settings
        String savedUrl = prefs.getString("api_url", "https://api.ollama.cloud");
        String savedKey = prefs.getString("api_key", "");
        String savedFolder = prefs.getString("folder_uri", "");
        
        editApiUrl = findViewById(R.id.editApiUrl);
        editApiKey = findViewById(R.id.editApiKey);
        btnLoadModels = findViewById(R.id.btnLoadModels);
        spinnerModels = findViewById(R.id.spinnerModels);
        btnSelectFolder = findViewById(R.id.btnSelectFolder);
        tvFolderPath = findViewById(R.id.tvFolderPath);
        btnStartChat = findViewById(R.id.btnStartChat);
        
        editApiUrl.setText(savedUrl);
        editApiKey.setText(savedKey);
        
        if (!savedFolder.isEmpty()) {
            selectedFolderUri = Uri.parse(savedFolder);
            tvFolderPath.setText("Carpeta seleccionada");
        }
        
        modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelNames);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModels.setAdapter(modelAdapter);
        
        spinnerModels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < modelNames.size()) {
                    selectedModel = modelNames.get(position);
                    prefs.edit().putString("selected_model", selectedModel).apply();
                    checkReadyToStart();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        btnLoadModels.setOnClickListener(v -> loadModels());
        btnSelectFolder.setOnClickListener(v -> selectFolder());
        btnStartChat.setOnClickListener(v -> startChat());
    }
    
    private void loadModels() {
        String apiUrl = editApiUrl.getText().toString().trim();
        String apiKey = editApiKey.getText().toString().trim();
        
        if (apiUrl.isEmpty()) {
            Toast.makeText(this, "Introduce la URL de la API", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Save settings
        prefs.edit()
            .putString("api_url", apiUrl)
            .putString("api_key", apiKey)
            .apply();
        
        btnLoadModels.setEnabled(false);
        btnLoadModels.setText(R.string.loading);
        
        // Fetch models from cloud API
        String url = apiUrl.endsWith("/") ? apiUrl + "api/tags" : apiUrl + "/api/tags";
        
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (!apiKey.isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
        }
        
        Request request = requestBuilder.build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(SetupActivity.this, 
                        "Error al cargar modelos: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                    btnLoadModels.setEnabled(true);
                    btnLoadModels.setText(R.string.load_models);
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray modelsArray = json.optJSONArray("models");
                    
                    modelNames.clear();
                    if (modelsArray != null) {
                        for (int i = 0; i < modelsArray.length(); i++) {
                            JSONObject model = modelsArray.getJSONObject(i);
                            String name = model.optString("name", "Unknown");
                            modelNames.add(name);
                        }
                    }
                    
                    runOnUiThread(() -> {
                        modelAdapter.notifyDataSetChanged();
                        if (!modelNames.isEmpty()) {
                            spinnerModels.setSelection(0);
                            Toast.makeText(SetupActivity.this, 
                                modelNames.size() + " modelos cargados", 
                                Toast.LENGTH_SHORT).show();
                            checkReadyToStart();
                        } else {
                            Toast.makeText(SetupActivity.this, 
                                "No se encontraron modelos", 
                                Toast.LENGTH_SHORT).show();
                        }
                        btnLoadModels.setEnabled(true);
                        btnLoadModels.setText(R.string.load_models);
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(SetupActivity.this, 
                            "Error al procesar respuesta: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                        btnLoadModels.setEnabled(true);
                        btnLoadModels.setText(R.string.load_models);
                    });
                }
            }
        });
    }
    
    private void selectFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                       Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                       Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        folderPickerLauncher.launch(intent);
    }
    
    private void checkReadyToStart() {
        boolean ready = !selectedModel.isEmpty() && selectedFolderUri != null;
        btnStartChat.setEnabled(ready);
    }
    
    private void startChat() {
        if (selectedModel.isEmpty() || selectedFolderUri == null) {
            Toast.makeText(this, "Selecciona un modelo y una carpeta", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
