package com.example.internetserver;

import androidx.annotation.Nullable;

public class SetUserPrettyNameRequest {

    @Nullable
    public String pretty_name;

    public SetUserPrettyNameRequest(@Nullable String pretty_name) {
        this.pretty_name = pretty_name;
    }
}
