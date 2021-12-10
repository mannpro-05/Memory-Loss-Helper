package com.example.memory_loss_app.database.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Update;

import com.example.memory_loss_app.database.Database;
import com.example.memory_loss_app.database.dao.UserDetailsDao;
import com.example.memory_loss_app.database.table.UserDetailsDB;

import java.util.List;

public class UserDetailsRepository {

    private UserDetailsDao userDetailsDao;
    private LiveData<List<UserDetailsDB>> userDetails;
    private LiveData<Integer> userExist;

    public UserDetailsRepository(Application application){
        Database database = Database.getInstance(application);
        System.out.println(database);
        userDetailsDao = database.userDetailsDao();
        userDetails = userDetailsDao.getAllDetails();
        userExist = userDetailsDao.userExist();
    }

    public void insert(UserDetailsDB userDetailsDB) {
        new InsertNoteAsyncTask(userDetailsDao).execute(userDetailsDB);
    }

    public void update(UserDetailsDB userDetailsDB) {
        new UpdateNoteAsyncTask(userDetailsDao).execute(userDetailsDB);
    }


    public void deleteAllNotes() {
        new DeleteAllAsyncTask(userDetailsDao).execute();
    }

    public LiveData<List<UserDetailsDB>> getAllDetails() {
        return userDetails;
    }

    public LiveData<Integer> userExists(){ return userExist; }

    public static class InsertNoteAsyncTask extends AsyncTask<UserDetailsDB, Void, Void> {

        UserDetailsDao userDetailsDao;

        public InsertNoteAsyncTask(UserDetailsDao userDetailsDao) {
            this.userDetailsDao = userDetailsDao;
        }

        @Override
        protected Void doInBackground(UserDetailsDB... notes) {
            userDetailsDao.insert(notes[0]);
            System.out.println("Database entry done! for Users~~~");
            return null;
        }
    }

    public static class UpdateNoteAsyncTask extends AsyncTask<UserDetailsDB, Void, Void> {

        UserDetailsDao userDetailsDao;

        public UpdateNoteAsyncTask(UserDetailsDao userDetailsDao) {
            this.userDetailsDao = userDetailsDao;
        }

        @Override
        protected Void doInBackground(UserDetailsDB... userDetails) {
            userDetailsDao.update(userDetails[0]);
            return null;
        }
    }

    public static class DeleteAllAsyncTask extends AsyncTask<UserDetailsDB, Void, Void> {

        UserDetailsDao userDetailsDao;

        public DeleteAllAsyncTask(UserDetailsDao userDetailsDao) {
            this.userDetailsDao = userDetailsDao;
        }

        @Override
        protected Void doInBackground(UserDetailsDB... userDetails) {
            userDetailsDao.deleteAllNotes();
            return null;
        }
    }




}
