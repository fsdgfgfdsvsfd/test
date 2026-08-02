package com.claudecode.mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private Button btnSend;
    
    private OkHttpClient client;
    private Handler mainHandler;
    private SharedPreferences prefs;
    
    private String apiUrl;
    private String apiKey;
    private String selectedModel;
    
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        prefs = getSharedPreferences("claude_code_prefs", MODE_PRIVATE);
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Load settings
        apiUrl = prefs.getString("api_url", "https://api.ollama.cloud");
        apiKey = prefs.getString("api_key", "");
        selectedModel = prefs.getString("selected_model", "claude-sonnet-4-20250514");
        
        recyclerMessages = findViewById(R.id.recyclerMessages);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        
        adapter = new MessageAdapter(messages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerMessages.setAdapter(adapter);
        
        // Add welcome message
        addMessage("assistant", "¡Hola! Soy tu asistente de programación Claude Code. ¿En qué puedo ayudarte hoy?");
        
        btnSend.setOnClickListener(v -> sendMessage());
        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }
    
    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;
        
        addMessage("user", text);
        editMessage.setText("");
        
        // Build conversation history for context
        JSONArray conversationHistory = new JSONArray();
        for (Message msg : messages) {
            try {
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.role);
                msgObj.put("content", msg.content);
                conversationHistory.put(msgObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Send to Ollama API
        sendToOllama(conversationHistory);
    }
    
    private void sendToOllama(JSONArray conversationHistory) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", selectedModel);
            requestBody.put("messages", conversationHistory);
            requestBody.put("stream", false);
            
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            
            String url = apiUrl.endsWith("/") ? apiUrl + "api/chat" : apiUrl + "/api/chat";
            
            Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
            
            if (!apiKey.isEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
            }
            
            Request request = requestBuilder.build();
            
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> {
                        addMessage("assistant", "Error: " + e.getMessage());
                    });
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        JSONObject messageObj = json.optJSONObject("message");
                        
                        String assistantResponse = "";
                        if (messageObj != null) {
                            assistantResponse = messageObj.optString("content", "Sin respuesta");
                        }
                        
                        final String finalResponse = assistantResponse;
                        mainHandler.post(() -> {
                            addMessage("assistant", finalResponse);
                        });
                    } catch (Exception e) {
                        mainHandler.post(() -> {
                            addMessage("assistant", "Error al procesar respuesta: " + e.getMessage());
                        });
                    }
                }
            });
        } catch (Exception e) {
            addMessage("assistant", "Error: " + e.getMessage());
        }
    }
    
    private void addMessage(String role, String content) {
        messages.add(new Message(role, content));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerMessages.scrollToPosition(messages.size() - 1);
    }
    
    static class Message {
        String role;
        String content;
        
        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        private List<Message> messages;
        
        MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Message message = messages.get(position);
            
            if ("user".equals(message.role)) {
                holder.userMessageLayout.setVisibility(View.VISIBLE);
                holder.assistantMessageLayout.setVisibility(View.GONE);
                holder.tvUserMessage.setText(message.content);
            } else {
                holder.userMessageLayout.setVisibility(View.GONE);
                holder.assistantMessageLayout.setVisibility(View.VISIBLE);
                holder.tvAssistantMessage.setText(message.content);
            }
        }
        
        @Override
        public int getItemCount() {
            return messages.size();
        }
        
        static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout userMessageLayout;
            LinearLayout assistantMessageLayout;
            TextView tvUserMessage;
            TextView tvAssistantMessage;
            
            ViewHolder(View itemView) {
                super(itemView);
                userMessageLayout = itemView.findViewById(R.id.userMessageLayout);
                assistantMessageLayout = itemView.findViewById(R.id.assistantMessageLayout);
                tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
                tvAssistantMessage = itemView.findViewById(R.id.tvAssistantMessage);
            }
        }
    }
}
