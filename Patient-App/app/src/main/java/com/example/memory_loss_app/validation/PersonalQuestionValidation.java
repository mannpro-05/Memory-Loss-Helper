package com.example.memory_loss_app.validation;

import android.text.TextUtils;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

/**
 * This class does the auth validation for the Personal Question.
 * Note: In the validation methods i've set the error null because if there is an error and that
 * error is resolved it will still be there as it has not been removed to we have to set it to null.
 */
public class PersonalQuestionValidation {
    TextInputLayout answer, manualPersonalQuestion;
    AutoCompleteTextView personalQuestions;

    //Constructor for inti the class variables.
    public PersonalQuestionValidation(TextInputLayout answer, AutoCompleteTextView personalQuestions,
                                      TextInputLayout manualPersonalQuestion){
        this.answer = answer;
        this.personalQuestions = personalQuestions;
        this.manualPersonalQuestion = manualPersonalQuestion;
    }

    // Validator for patient details.
    public boolean validator(String selectedQuestion){
        if (TextUtils.isEmpty(selectedQuestion) && manualPersonalQuestion.getEditText().isEnabled()){
            manualPersonalQuestion.setError("Choose a question!");
            return false;
        }
        else{
            manualPersonalQuestion.setError(null);
        }

        if (TextUtils.isEmpty(selectedQuestion)){
            personalQuestions.setError("Choose a question!");
            return false;
        }
        else {
            personalQuestions.setError(null);
        }


        if (TextUtils.isEmpty(answer.getEditText().getText().toString())){
            answer.setError("This field is required!");
            return false;
        }
        else {
            answer.setError(null);
        }

        return true;
    }
}
