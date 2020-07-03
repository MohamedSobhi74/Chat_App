package com.mohamedsobhi.chatapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.mohamedsobhi.chatapp.R;
import com.mohamedsobhi.chatapp.adaptors.ChatAdapter;
import com.mohamedsobhi.chatapp.models.Message;
import com.mohamedsobhi.chatapp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.text_content)
    EditText textContent;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    private ChatAdapter adapter;

    private ActionBar actionBar;
    User user;
    String chatId;
    String myId;
    String userId;
    List<Message> messageList;

    FirebaseFirestore firebaseFirestore;
    Boolean isChatExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        isChatExist = false;

        messageList =new ArrayList<>();
        user = (User) getIntent().getExtras().getSerializable("userData");
        myId = FirebaseAuth.getInstance().getUid();
        userId = user.getUserId();

        firebaseFirestore = FirebaseFirestore.getInstance();
        chatId = getChatId(myId,userId);


        initToolbar();
        iniComponent();
        checkChatStatus();
        getChatData(chatId);




    }

    private void checkChatStatus() {
        firebaseFirestore.collection("Users").document(myId).collection("Chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    return;
                }

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                    if (doc.getId().equals(chatId)){
                        isChatExist = true;

                    }else {

                        isChatExist = false;

                    }
                }
            }
        });
    }

    private String getChatId(String myId,String userId) {

        String id = "";
        int compare = myId.compareTo(userId);
        if (compare < 0) {
            id = myId + userId;
        } else if (compare > 0) {
            id = userId + myId;

        }
        return id;
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        toolbar.setTitle(user.getName());
        toolbar.setSubtitle("available");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


    }

    public void iniComponent() {

       // getChatData(chatId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ChatAdapter(this,messageList);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat();
            }
        });
        textContent.addTextChangedListener(contentWatcher);
    }

    private void getChatData(String chatId) {

        firebaseFirestore.collection("Users").document(myId).collection("Chats").document(chatId).collection("Messages").orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    return;
                }

                messageList.clear();
                textContent.setText("");
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                    Message message = doc.toObject(Message.class);

                    message.setMessageId(doc.getId());

                    messageList.add(message);

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void sendChat() {

        String randomId = String.valueOf(UUID.randomUUID());
        String message=textContent.getText().toString();
        String sender=myId;
        String receiver=userId;

        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("message",message);
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("timeStamp",Timestamp.now());

        WriteBatch batch = firebaseFirestore.batch();


// Set the value of 'NYC'
        DocumentReference myMessageCopy= firebaseFirestore.collection("Users").document(myId).collection("Chats").document(chatId).collection("Messages").document(randomId);
        DocumentReference hisMessageCopy =  firebaseFirestore.collection("Users").document(userId).collection("Chats").document(chatId).collection("Messages").document(randomId);

        batch.set(myMessageCopy,hashMap);
        batch.set(hisMessageCopy,hashMap);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){

                    Toast.makeText(ChatActivity.this, "failed to send message", Toast.LENGTH_SHORT).show();
                }else {


                }

            }
        });



    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btnSend.setEnabled(false);
            } else {
                btnSend.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_chat_bbm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}