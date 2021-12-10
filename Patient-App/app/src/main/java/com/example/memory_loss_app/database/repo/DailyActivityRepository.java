package com.example.memory_loss_app.database.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.memory_loss_app.database.Database;
import com.example.memory_loss_app.database.dao.DailyActivityDao;
import com.example.memory_loss_app.database.table.DailyActivityDB;

import java.util.List;

public class DailyActivityRepository {
    private DailyActivityDao dailyActivityDao;
    private LiveData<List<DailyActivityDB>> dailyActivities;

    public DailyActivityRepository(Application application){
        Database database = Database.getInstance(application);
        dailyActivityDao = database.dailyActivityDao();
        dailyActivities = dailyActivityDao.getAllDailyActivities();
    }

    public void insert(DailyActivityDB dailyActivityDB){

        new InsertDailyActivity(dailyActivityDao).execute(dailyActivityDB);

    }

    public void update(DailyActivityDB dailyActivityDB){

        new UpdateDailyActivity(dailyActivityDao).execute(dailyActivityDB);

    }

    public void delete(DailyActivityDB dailyActivityDB){

        new DeleteDailyActivity(dailyActivityDao).execute(dailyActivityDB);

    }

    public void deleteAll(){

        new DeleteAllDailyActivity(dailyActivityDao).execute();

    }

    public LiveData<List<DailyActivityDB>> getDailyActivities(){
        return dailyActivities;
    }

    public static class InsertDailyActivity extends AsyncTask<DailyActivityDB, Void, Void>{

        DailyActivityDao dailyActivityDao;

        public InsertDailyActivity(DailyActivityDao dailyActivityDao){
            this.dailyActivityDao = dailyActivityDao;
        }


        @Override
        protected Void doInBackground(DailyActivityDB... dailyActivityDBS) {
            dailyActivityDao.insert(dailyActivityDBS[0]);
            return null;
        }
    }

    public static class UpdateDailyActivity extends AsyncTask<DailyActivityDB, Void, Void>{

        DailyActivityDao dailyActivityDao;

        public UpdateDailyActivity(DailyActivityDao dailyActivityDao){
            this.dailyActivityDao = dailyActivityDao;
        }


        @Override
        protected Void doInBackground(DailyActivityDB... dailyActivityDBS) {
            dailyActivityDao.update(dailyActivityDBS[0]);
            return null;
        }
    }

    public static class DeleteDailyActivity extends AsyncTask<DailyActivityDB, Void, Void>{

        DailyActivityDao dailyActivityDao;

        public DeleteDailyActivity(DailyActivityDao dailyActivityDao){
            this.dailyActivityDao = dailyActivityDao;
        }


        @Override
        protected Void doInBackground(DailyActivityDB... dailyActivityDBS) {
            dailyActivityDao.delete(dailyActivityDBS[0]);
            return null;
        }
    }

    public static class DeleteAllDailyActivity extends AsyncTask<Void, Void, Void>{

        DailyActivityDao dailyActivityDao;

        public DeleteAllDailyActivity(DailyActivityDao dailyActivityDao){
            this.dailyActivityDao = dailyActivityDao;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            dailyActivityDao.deleteAllDailyActivities();
            return null;
        }
    }





}
