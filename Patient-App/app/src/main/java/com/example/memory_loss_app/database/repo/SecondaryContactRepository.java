package com.example.memory_loss_app.database.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;


import com.example.memory_loss_app.database.Database;
import com.example.memory_loss_app.database.dao.PrimaryContactDao;
import com.example.memory_loss_app.database.dao.SecondaryContactDao;

import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.SecondaryContactDB;

import java.util.List;

public class SecondaryContactRepository {

    private SecondaryContactDao secondaryContactDao;
    private LiveData<List<SecondaryContactDB>> secondaryContact;

    public SecondaryContactRepository(Application application) {
        Database database = Database.getInstance(application);
        secondaryContactDao = database.secondaryContactDao();
        secondaryContact = secondaryContactDao.getAllContacts();
    }

    public void insert(SecondaryContactDB secondaryContactDB) {
        new SecondaryContactRepository.InsertNoteAsyncTask(secondaryContactDao).execute(secondaryContactDB);
    }

    public void update(SecondaryContactDB secondaryContactDB) {
        new SecondaryContactRepository.UpdateNoteAsyncTask(secondaryContactDao).execute(secondaryContactDB);
    }

    public void delete(SecondaryContactDB secondaryContactDB) {
        new SecondaryContactRepository.DeleteNoteAsyncTask(secondaryContactDao).execute(secondaryContactDB);
    }
    public void deleteAll() {
        new SecondaryContactRepository.DeleteAllNoteAsyncTask(secondaryContactDao).execute();
    }

    public LiveData<List<SecondaryContactDB>> getAllSecondaryContacts(){
        return secondaryContact;
    }

    public static class InsertNoteAsyncTask extends AsyncTask<SecondaryContactDB, Void, Void> {

        SecondaryContactDao secondaryContactDao ;

        public InsertNoteAsyncTask(SecondaryContactDao secondaryContactDao) {
            this.secondaryContactDao = secondaryContactDao;
        }

        @Override
        protected Void doInBackground(SecondaryContactDB... secondaryContactDB) {
            secondaryContactDao.insert(secondaryContactDB[0]);
            System.out.println("Database entry done! for Sec~~~");
            return null;
        }
    }


    public static class UpdateNoteAsyncTask extends AsyncTask<SecondaryContactDB, Void, Void> {

        SecondaryContactDao secondaryContactDao ;

        public UpdateNoteAsyncTask(SecondaryContactDao secondaryContactDao) {
            this.secondaryContactDao = secondaryContactDao;
        }

        @Override
        protected Void doInBackground(SecondaryContactDB... secondaryContactDB) {
            secondaryContactDao.update(secondaryContactDB[0]);
            return null;
        }
    }

    public static class DeleteNoteAsyncTask extends AsyncTask<SecondaryContactDB, Void, Void> {

        SecondaryContactDao secondaryContactDao ;

        public DeleteNoteAsyncTask(SecondaryContactDao secondaryContactDao) {
            this.secondaryContactDao = secondaryContactDao;
        }

        @Override
        protected Void doInBackground(SecondaryContactDB... secondaryContactDB) {
            secondaryContactDao.delete(secondaryContactDB[0]);
            return null;
        }
    }

    public static class DeleteAllNoteAsyncTask extends AsyncTask<Void, Void, Void> {

        SecondaryContactDao secondaryContactDao ;

        public DeleteAllNoteAsyncTask(SecondaryContactDao secondaryContactDao) {
            this.secondaryContactDao = secondaryContactDao;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            secondaryContactDao.deleteAllNotes();
            return null;
        }
    }


}
