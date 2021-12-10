package com.example.memory_loss_app.framelayoutmanager;

import com.example.memory_loss_app.adapter.DailyActivitiesAdapter;

/**
 * Interface for UserPage
 */
public interface UserPageHandler {

    public void homePage();

    public void profilePage();

    public void dashboard();

    public void onClickListener(String number);

    public void dailyActivityEditor(String id, String title, String description, DailyActivitiesAdapter adapter);

    public void dailyActivityAdder(DailyActivitiesAdapter adapter);

}
