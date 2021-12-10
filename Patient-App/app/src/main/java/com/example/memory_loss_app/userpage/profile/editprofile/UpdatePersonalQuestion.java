package com.example.memory_loss_app.userpage.profile.editprofile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.example.memory_loss_app.userpage.profile.ProfileHandler;
import com.example.memory_loss_app.validation.PersonalQuestionValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class UpdatePersonalQuestion extends Fragment {
    AutoCompleteTextView personalQuestions;
    TextInputLayout answer;
    Button update;
    String selectedQuestion = null;
    TextInputLayout manualPersonalQuestion;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    ProfileHandler handler;
    String[] personalQuestionsList;
    PersonalQuestionValidation validation;
    Users user;
    UserViewModel userViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_personal_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personalQuestions = view.findViewById(R.id.personalQuestionList);
        answer = view.findViewById(R.id.answer);
        manualPersonalQuestion = view.findViewById(R.id.manualPersonalQuestion);
        update = view.findViewById(R.id.update);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users/"+mAuth.getCurrentUser().getUid());
        personalQuestionsList = getActivity().getResources().getStringArray(R.array.personalQuestions);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.personalQuestions, R.layout.personal_question_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personalQuestions.setAdapter(adapter);

        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        personalQuestions.setOnItemClickListener((adapterView, view1, pos, l) -> {
            selectedQuestion = adapterView.getItemAtPosition(pos).toString();

            // If the user wishes to input a personal question of its own.
            //Enabling the user manualPersonalQuestion edittext.
            if (pos == (getTotalNumberOfPersonalQuestions() - 1)) {
                manualPersonalQuestion.setVisibility(View.VISIBLE);
                manualPersonalQuestion.setEnabled(true);
            } else {
                manualPersonalQuestion.setEnabled(false);
            }
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                String question = user.getPersonalQuestion();
                selectedQuestion = question;
                if (Arrays.asList(personalQuestionsList).contains(question)){
                    personalQuestions.setText(personalQuestions
                            .getAdapter()
                            .getItem(Arrays
                                    .asList(personalQuestionsList)
                                    .indexOf(question))
                            .toString(), false);

                    manualPersonalQuestion.setEnabled(false);
                }
                else {
                    manualPersonalQuestion.setEnabled(true);
                    manualPersonalQuestion.getEditText().setText(question);
                }
                answer.getEditText().setText(user.getAnswer());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        update.setOnClickListener(view1 -> {
            validation = new PersonalQuestionValidation(answer, personalQuestions, manualPersonalQuestion);
            if (manualPersonalQuestion.getEditText().isEnabled()) {
                selectedQuestion = manualPersonalQuestion.getEditText().getText().toString();
            }
            // Validating the user input.
            if (validation.validator(selectedQuestion)) {
                user.setPersonalQuestion(selectedQuestion);
                user.setAnswer(answer.getEditText().getText().toString());
                reference.setValue(user);

                UserDetailsDB userDetailsDB = new UserDetailsDB(mAuth.getCurrentUser().getUid(),
                        mAuth.getCurrentUser().getEmail(), true, user.getDoctorName(), user.getDiagnosedOn(),
                        user.getLastAppointmentDate(), user.getDoctorContactNumber(), String.valueOf(user.getNotificationTime()),
                        user.getName(), user.getDateOfBirth(), user.getBloodGroup(), user.getAddress(),
                        user.getAge(), user.getPersonalQuestion(), user.getAnswer(), user.getLastMemoryLossTrauma());


                userViewModel.update(userDetailsDB);
                handler.editProfile();
            }
        });

    }


    int getTotalNumberOfPersonalQuestions() {
        return this.getResources().getStringArray(R.array.personalQuestions).length;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ProfileHandler){
            handler = (ProfileHandler) context;
        }
    }
}