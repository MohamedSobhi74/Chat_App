package com.mohamedsobhi.chatapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.mohamedsobhi.chatapp.R;
import com.mohamedsobhi.chatapp.adaptors.UsersAdaptor;
import com.mohamedsobhi.chatapp.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UsersFragment extends Fragment {


    @BindView(R.id.user_recycler)
    RecyclerView userRecycler;

    FirebaseFirestore firebaseFirestore;
    List<User> usersList;
    UsersAdaptor usersAdaptor;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this,view);


        usersList = new ArrayList<>();

        usersAdaptor = new UsersAdaptor(getActivity(),usersList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        userRecycler.setLayoutManager(linearLayoutManager);
        userRecycler.setAdapter(usersAdaptor);
        firebaseFirestore = FirebaseFirestore.getInstance();

        getUsers();


        return view;
    }

    private void getUsers() {
        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    return;
                }

                usersList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {


                    if (!doc.getId().equals(FirebaseAuth.getInstance().getUid())){

                        Log.e("username", (String) doc.get("name"));
                        User user = doc.toObject(User.class);

                        user.setUserId(doc.getId());

                        usersList.add(user);

                        usersAdaptor.notifyDataSetChanged();
                    }



                }
            }
        });
    }
}