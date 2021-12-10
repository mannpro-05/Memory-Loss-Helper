package com.example.memory_loss_app.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memory_loss_app.database.repo.DailyActivityRepository;
import com.example.memory_loss_app.database.table.DailyActivityDB;

import java.util.List;

public class DailyActivitiesViewModel extends AndroidViewModel {
    DailyActivityRepository repository;
    private LiveData<List<DailyActivityDB>> dailyActivities;
    public DailyActivitiesViewModel(@NonNull Application application) {
        super(application);
        repository = new DailyActivityRepository(application);
        dailyActivities = repository.getDailyActivities();
    }

    public void insert(DailyActivityDB dailyActivityDB){
        repository.insert(dailyActivityDB);
    }
    public void update(DailyActivityDB dailyActivityDB){
        repository.update(dailyActivityDB);
    }
    public void delete(DailyActivityDB dailyActivityDB){
        repository.delete(dailyActivityDB);
    }
    public void deleteAll(){
        repository.deleteAll();
    }
    public LiveData<List<DailyActivityDB>> getDailyActivities(){
        return dailyActivities;
    }
}
