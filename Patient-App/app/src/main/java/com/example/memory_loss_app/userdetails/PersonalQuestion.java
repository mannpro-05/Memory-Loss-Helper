package com.example.memory_loss_app.userdetails;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.framelayoutmanager.ObjectHandler;
import com.example.memory_loss_app.validation.PersonalQuestionValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * This class will enter the personal question.
 */
public class PersonalQuestion extends Fragment implements AdapterView.OnItemClickListener {
    View view;
    AutoCompleteTextView personalQuestions;
    TextInputLayout answer;
    Button next;
    String selectedQuestion = null;
    TextInputLayout manualPersonalQuestion;
    PersonalQuestionValidation validation;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    Users user;
    ObjectHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_personal_question, container, false);

        personalQuestions = view.findViewById(R.id.personalQuestionList);
        answer = view.findViewById(R.id.answer);
        manualPersonalQuestion = view.findViewById(R.id.manualPersonalQuestion);
        next = view.findViewById(R.id.next);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.personalQuestions, R.layout.personal_question_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personalQuestions.setAdapter(adapter);
        personalQuestions.setOnItemClickListener(this);

        // Button to move to the next activity.
        next.setOnClickListener(view -> {

            // Checking if user has selected to enter a question of its own or
            if (manualPersonalQuestion.getEditText().isEnabled()) {
                selectedQuestion = manualPersonalQuestion.getEditText().getText().toString();
            }
            validation = new PersonalQuestionValidation(answer, personalQuestions, manualPersonalQuestion);

            // Validating the user input.
            if (validation.validator(selectedQuestion)) {
                user.setPersonalQuestion(selectedQuestion);
                user.setAnswer(answer.getEditText().getText().toString());
                handler.personalQuestions(user);
            }
        });

        return view;
    }

    // Updating the user object once this activity is created.
    // This is done to insert the data of the previous activity to this so that the all the data
    // could be stored into the fire base when the user setup is complete.
    public void updateUserObjectInfo(Users user) {
        this.user = user;
    }

    // Getting the value and position of the selected question.
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        selectedQuestion = adapterView.getItemAtPosition(pos).toString();

        // If the user wishes to input a personal question of its own.
        //Enabling the user manualPersonalQuestion edittext.
        if (pos == (getTotalNumberOfPersonalQuestions() - 1)) {
            manualPersonalQuestion.setVisibility(View.VISIBLE);
            manualPersonalQuestion.setEnabled(true);
        } else {
            manualPersonalQuestion.setEnabled(false);
        }
    }

    //Setting the context to the reference of ObjectHandler Interface.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ObjectHandler) {
            handler = (ObjectHandler) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ObjectHandler");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }

    //this method will return the count of total personal question in the system.
    int getTotalNumberOfPersonalQuestions() {
        return this.getResources().getStringArray(R.array.personalQuestions).length;
    }
}