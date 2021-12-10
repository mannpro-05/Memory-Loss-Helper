package com.example.memory_loss_app.validation;

import android.text.TextUtils;
import android.widget.EditText;

public class DailyActivityValidation {
    EditText title;
    EditText description;

    public DailyActivityValidation(EditText title, EditText description){
        this.title = title;
        this.description = description;
    }

    public boolean validate(){
        if (TextUtils.isEmpty(title.getText())){
            title.setError("Title cannot be empty!");
            return false;
        }
        else {
            title.setError(null);
        }

        if (TextUtils.isEmpty(description.getText())){
            description.setError("Description cannot be empty!");
            return false;
        }
        else {
            description.setError(null);
        }
        return true;
    }
}
