package org.tracker.prashu.savedRoutes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.tracker.prashu.navigationApp.MyDatabaseHelper;
import org.tracker.prashu.navigationApp.R;

import java.util.ArrayList;

public class SavedRoutesActivity extends AppCompatActivity {

    RecyclerView savedRoutesRecyclerView;
    SavedRoutesAdapter adapter;
    ArrayList<SavedRoutesData> arrayList;
    MyDatabaseHelper databaseHelper;
    SQLiteDatabase db, db2;
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);
        Toast.makeText(this, "saved Routes", Toast.LENGTH_SHORT).show();
        // getting the signed-in user's email from the intent.
        currentUserEmail = getIntent().getStringExtra("emailKey");
        savedRoutesRecyclerView = (RecyclerView) findViewById(R.id.savedRoutesRecyclerView);
        // calling the method that fetches data from the database and display it to the user.
        showDataForSpecificUser();
        // registering for the context menu the recycler view.
        registerForContextMenu(savedRoutesRecyclerView);

    }

    public void showDataForSpecificUser() {

        // creating object of the custom created database class and passing the context in it.
        databaseHelper = new MyDatabaseHelper(SavedRoutesActivity.this);
        // get the writable instance of the database.
        db = databaseHelper.getWritableDatabase();

        // create a string array of the columns for the select query
        String[] columns = {
                MyDatabaseHelper.ID,
                MyDatabaseHelper.EMAIL,
                MyDatabaseHelper.NAME,
                MyDatabaseHelper.DATE,
                MyDatabaseHelper.TIME,
                MyDatabaseHelper.START_LAT,
                MyDatabaseHelper.START_LONG,
                MyDatabaseHelper.CURRENT_LAT,
                MyDatabaseHelper.CURRENT_LONG,
                MyDatabaseHelper.POLYLINE_ARRAY,
                MyDatabaseHelper.DURATION,
                MyDatabaseHelper.DISTANCE,
                MyDatabaseHelper.COLOR
        };

        // creating a cursor on the database for the given table, columns and no conditions.
        Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        // creating an object of arraylist of type saved routes pojo.
        arrayList = new ArrayList<>();

        // get the index of the column as integer from the cursor reference.
        int i1 = cursor.getColumnIndex(MyDatabaseHelper.ID);
        int i2 = cursor.getColumnIndex(MyDatabaseHelper.EMAIL);
        int i3 = cursor.getColumnIndex(MyDatabaseHelper.NAME);
        int i4 = cursor.getColumnIndex(MyDatabaseHelper.DATE);
        int i5 = cursor.getColumnIndex(MyDatabaseHelper.TIME);
        int i6 = cursor.getColumnIndex(MyDatabaseHelper.START_LAT);
        int i7 = cursor.getColumnIndex(MyDatabaseHelper.START_LONG);
        int i8 = cursor.getColumnIndex(MyDatabaseHelper.CURRENT_LAT);
        int i9 = cursor.getColumnIndex(MyDatabaseHelper.CURRENT_LONG);
        int i10 = cursor.getColumnIndex(MyDatabaseHelper.POLYLINE_ARRAY);
        int i11 = cursor.getColumnIndex(MyDatabaseHelper.DURATION);
        int i12 = cursor.getColumnIndex(MyDatabaseHelper.DISTANCE);
        int i13 = cursor.getColumnIndex(MyDatabaseHelper.COLOR);


        // set the starting of cursor to first.
        cursor.moveToFirst();
        cursor.moveToPrevious();
        // loop through whole table until all the rows are once visited.
        while (cursor.moveToNext()) {
            if (cursor.getString(i2).equals(currentUserEmail)) {
                String id = cursor.getString(i1);
                String email = cursor.getString(i2);
                String name = cursor.getString(i3);
                String date = cursor.getString(i4);
                String time = cursor.getString(i5);
                String start_lat = cursor.getString(i6);
                String start_long = cursor.getString(i7);
                String current_lat = cursor.getString(i8);
                String current_long = cursor.getString(i9);
                String polyline_array = cursor.getString(i10);
                String duration = cursor.getString(i11);
                String distance = cursor.getString(i12);
                String color = cursor.getString(i13);

                // add the extracted data to the array list by creating a new object of the saved routes instance.
                arrayList.add(new SavedRoutesData(id, email, name, date, time, start_lat, start_long, current_lat, current_long, polyline_array, duration, distance, color));
            }
        }

        if (arrayList.isEmpty()){
            Toast.makeText(this, "List Empty", Toast.LENGTH_SHORT).show();
        }

        // creating an object of the saved routes adapter and passing in it context, and arraylist
        adapter = new SavedRoutesAdapter(SavedRoutesActivity.this, arrayList);
        // set adapter to the recycler view
        savedRoutesRecyclerView.setAdapter(adapter);
        // set the layout manager to the recylcer view
        savedRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // close the database for avoiding any memory leaks
        db.close();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int identity = -1;
        try {
            // set the identity value as the one we set in adapter earlier.
            identity = ((SavedRoutesAdapter) savedRoutesRecyclerView.getAdapter()).getIdentity();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        // switch case for if the delete option is selected from the context menu.
        switch (item.getItemId()) {
            case R.id.deleteRoute:
                if (deleteData(identity)) {
                    showDataForSpecificUser();
                    Toast.makeText(this, "Route Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Deletion Error", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return super.onContextItemSelected(item);
    }


    // method that deletes the row in the database at the specific id passed to it.
    public boolean deleteData(int identity) {
        databaseHelper = new MyDatabaseHelper(SavedRoutesActivity.this);
        db2 = databaseHelper.getWritableDatabase();

        // query that deletes the rows with condition specified alongwith it.
        db2.delete(MyDatabaseHelper.TABLE_NAME, MyDatabaseHelper.ID + "=" + identity, null);
        db2.close();
        return true;
    }
}
