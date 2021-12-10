package com.example.memorylossguardian.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.memorylossguardian.database.GuardianDatabase;
import com.example.memorylossguardian.database.dao.GuardianDao;
import com.example.memorylossguardian.database.table.GuardianTable;

import java.util.List;

public class GuardianRepository {

    private GuardianDao guardianDao;
    private Thread insertThread;
    private Thread updateThread;
    private Thread deleteThread;


    private LiveData<List<GuardianTable>> guardianData;

    public GuardianRepository(Application application) {
        GuardianDatabase database = GuardianDatabase.getInstance(application);
        guardianDao = database.guardianDao();
        guardianData = guardianDao.getGuardian();
    }

    public void insert(GuardianTable guardianTable) {
        insertThread = new Thread(new InsertThread(guardianTable));
        insertThread.start();
        try {
            insertThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(GuardianTable guardianTable) {
        updateThread = new Thread(new UpdateThread(guardianTable));
        updateThread.start();
        try {
            updateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        deleteThread = new Thread(new DeleteThread());
        deleteThread.start();
        try {
            deleteThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public LiveData<List<GuardianTable>> getGuardianData() {
        return guardianData;
    }

    private class InsertThread implements Runnable {

        GuardianTable guardianTable;

        public InsertThread(GuardianTable guardianTable) {
            this.guardianTable = guardianTable;
        }

        @Override
        public void run() {
            guardianDao.insert(guardianTable);
        }
    }

    private class UpdateThread implements Runnable {

        GuardianTable guardianTable;

        public UpdateThread(GuardianTable guardianTable) {
            this.guardianTable = guardianTable;
        }

        @Override
        public void run() {
            guardianDao.update(guardianTable);
        }
    }

    private class DeleteThread implements Runnable {

        @Override
        public void run() {
            guardianDao.delete();
        }
    }
}
