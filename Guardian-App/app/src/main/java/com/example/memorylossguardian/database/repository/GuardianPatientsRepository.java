package com.example.memorylossguardian.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.memorylossguardian.database.GuardianDatabase;
import com.example.memorylossguardian.database.dao.GuardianPatientDao;
import com.example.memorylossguardian.database.table.GuardianPatients;
import com.example.memorylossguardian.database.table.GuardianTable;

import java.util.List;

public class GuardianPatientsRepository {

    private LiveData<List<GuardianPatients>> patients;
    private GuardianPatientDao guardianPatientDao;
    private Thread insertThread;
    private Thread deleteThread;
    private Thread deleteAllThread;
    private boolean successInsert;

    public GuardianPatientsRepository(Application application) {
        GuardianDatabase database = GuardianDatabase.getInstance(application);
        guardianPatientDao = database.guardianPatientDao();
        patients = guardianPatientDao.getPatients();
    }

    public boolean insert(GuardianPatients guardianPatients) {
        insertThread = new Thread(new GuardianPatientsRepository.InsertThread(guardianPatients));
        insertThread.start();
        try {
            insertThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return successInsert;
    }


    public void delete(GuardianPatients guardianPatients) {
        deleteThread = new Thread(new GuardianPatientsRepository.DeleteThread(guardianPatients));
        deleteThread.start();
        try {
            deleteThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        deleteAllThread = new Thread(new GuardianPatientsRepository.DeleteAllThread());
        deleteAllThread.start();
        try {
            deleteAllThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<GuardianPatients>> getPatients() {
        return patients;
    }

    private class InsertThread implements Runnable {

        GuardianPatients guardianPatients;

        public InsertThread(GuardianPatients guardianPatients) {
            this.guardianPatients = guardianPatients;
        }

        @Override
        public void run() {
            try {
                successInsert = true;
                guardianPatientDao.insert(guardianPatients);
            } catch (Exception e) {
                successInsert = false;
                e.printStackTrace();
            }

        }
    }

    private class DeleteThread implements Runnable {

        GuardianPatients guardianPatients;

        public DeleteThread(GuardianPatients guardianPatients) {
            this.guardianPatients = guardianPatients;
        }

        @Override
        public void run() {
            guardianPatientDao.delete(guardianPatients);
        }
    }

    private class DeleteAllThread implements Runnable {

        @Override
        public void run() {
            guardianPatientDao.deleteAll();
        }
    }


}
