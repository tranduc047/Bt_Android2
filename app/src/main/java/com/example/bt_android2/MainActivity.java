package com.example.bt_android2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    List<MessageModule> messageList;
    AppCompatButton button, button_back, button_next;
    TextView id,iduser,title,message, txt_dem;
    Gson gson;
    private static final String CHANNEL_ID = "my_channel_id";
    int dem=0;

    void HienThi1PT(){
        if(messageList==null) return;
        else {
            MessageModule messageModule = messageList.get(dem);
            id.setText(String.valueOf(messageModule.getId()));
            iduser.setText(String.valueOf(messageModule.getUserId()));
            title.setText(messageModule.getTitle());
            message.setText(messageModule.getBody());
            txt_dem.setText(""+(dem+1)+"/"+messageList.size());
            showNotification(messageModule.getTitle(),messageModule.getBody());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        gson = new Gson();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageList = new ArrayList<>();

                // Tạo Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://jsonplaceholder.typicode.com/posts/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Api apiService = retrofit.create(Api.class);
                Call<List<MessageModule>> call = apiService.getJsonData();

                call.enqueue(new Callback<List<MessageModule>>() {
                    @Override
                    public void onResponse(Call<List<MessageModule>> call, Response<List<MessageModule>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            messageList = response.body();
                            dem=0;
                            if (!messageList.isEmpty()) {
                                HienThi1PT();
                                MessageAdapter adapter;
                                adapter = new MessageAdapter(getApplicationContext(), messageList);
                                recyclerView.setAdapter(adapter);
                                MessageModule messageModule = messageList.get(dem);
                                id.setText(String.valueOf(messageModule.getId()));
                                iduser.setText(String.valueOf(messageModule.getUserId()));
                                title.setText(messageModule.getTitle());
                                message.setText(messageModule.getBody());
                                dem++;
                                showNotification(messageModule.getTitle(),messageModule.getBody());
                            }
                        } else {
                            System.err.println("Response is empty or unsuccessful");
                        }
                    }
                    @Override
                    public void onFailure(Call<List<MessageModule>> call, Throwable t) {
                        System.err.println("Error: " + t.getMessage());
                    }
                });
            }
        });
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(v);
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });
    }

    private void anhXa() {
        button_back = findViewById(R.id.button_back);
        button_next = findViewById(R.id.button_next);
        button = findViewById(R.id.button);
        id = findViewById(R.id.id1);
        title = findViewById(R.id.title1);
        message = findViewById(R.id.message1);
        iduser = findViewById(R.id.iduser1);
        txt_dem = findViewById(R.id.txt_dem);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tên kênh thông báo",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Mô tả kênh thông báo");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    public void back(View view) {
        if(messageList==null)return;
        if(dem>0)dem--;
        if(dem>=0)
        {
            HienThi1PT();
        }

    }

    public void next(View view) {
        if(messageList==null)return;
        if(dem < messageList.size()-1)dem++;
        if(dem < messageList.size())
        {
            HienThi1PT();
        }
    }
}