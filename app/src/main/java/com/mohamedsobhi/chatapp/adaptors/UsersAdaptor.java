package com.mohamedsobhi.chatapp.adaptors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamedsobhi.chatapp.ui.ChatActivity;
import com.mohamedsobhi.chatapp.R;
import com.mohamedsobhi.chatapp.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UsersAdaptor extends RecyclerView.Adapter<UsersAdaptor.ViewHolder> {

    Context context;
    List<User> usersList;


    public UsersAdaptor(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_people_contacts, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = usersList.get(position);
        holder.name.setText(user.getName());

        holder.lytParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userData",user);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.lyt_parent)
        LinearLayout lytParent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind( this,itemView);
        }
    }
}
