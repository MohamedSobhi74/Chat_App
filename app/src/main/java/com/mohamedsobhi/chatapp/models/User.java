package com.mohamedsobhi.chatapp.models;

import androidx.annotation.Keep;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

@Keep
public class User  implements Serializable {

    @Exclude
    String userId;
    String name;

    @Exclude
    public String getUserId() {
        return userId;
    }

    @Exclude
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
