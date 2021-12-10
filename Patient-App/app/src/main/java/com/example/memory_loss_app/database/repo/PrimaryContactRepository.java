package com.example.memory_loss_app.database.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.memory_loss_app.database.Database;
import com.example.memory_loss_app.database.dao.PrimaryContactDao;
import com.example.memory_loss_app.database.dao.UserDetailsDao;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;

import java.util.List;

public class PrimaryContactRepository {
    private PrimaryContactDao primaryContactDao;
    private LiveData<List<PrimaryContactDB>> primaryContact;

    public PrimaryContactRepository(Application application){
        Database database = Database.getInstance(application);
        primaryContactDao = database.primaryContactDao();
        primaryContact = primaryContactDao.getAllContacts();
    }

    public void insert(PrimaryContactDB primaryContactDB) {
        new PrimaryContactRepository.InsertNoteAsyncTask(primaryContactDao).execute(primaryContactDB);
    }

    public void update(PrimaryContactDB primaryContactDB) {
        new PrimaryContactRepository.UpdateNoteAsyncTask(primaryContactDao).execute(primaryContactDB);
    }


    public void deleteAllPrimaryContacts() {
        new PrimaryContactRepository.DeleteAllAsyncTask(primaryContactDao).execute();
    }

    public LiveData<List<PrimaryContactDB>> getAllNotes() {
        return primaryContact;
    }

    public static class InsertNoteAsyncTask extends AsyncTask<PrimaryContactDB, Void, Void> {

        PrimaryContactDao primaryContactDao;

        public InsertNoteAsyncTask(PrimaryContactDao primaryContactDao) {
            this.primaryContactDao = primaryContactDao;
        }

        @Override
        protected Void doInBackground(PrimaryContactDB... notes) {
            primaryContactDao.insert(notes[0]);
            return null;
        }
    }

    public static class UpdateNoteAsyncTask extends AsyncTask<PrimaryContactDB, Void, Void> {

        PrimaryContactDao primaryContactDao;

        public UpdateNoteAsyncTask(PrimaryContactDao primaryContactDao) {
            this.primaryContactDao = primaryContactDao;
        }

        @Override
        protected Void doInBackground(PrimaryContactDB... primaryContactDB) {
            primaryContactDao.update(primaryContactDB[0]);
            System.out.println("Database entry done! for Pri~~~");
            return null;
        }
    }

    public static class DeleteAllAsyncTask extends AsyncTask<PrimaryContactDB, Void, Void> {

        PrimaryContactDao primaryContactDao;

        public DeleteAllAsyncTask(PrimaryContactDao primaryContactDao) {
            this.primaryContactDao = primaryContactDao;
        }

        @Override
        protected Void doInBackground(PrimaryContactDB... primaryContactDBS) {
            primaryContactDao.deleteAllNotes();
            return null;
        }
    }
}
