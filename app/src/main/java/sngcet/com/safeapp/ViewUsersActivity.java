package sngcet.com.safeapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ViewUsersActivity extends ActionBarActivity {

    private SqlHelper sqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        sqlHelper = new SqlHelper(this);

        Cursor cursor = sqlHelper.getAll();



        String[] name = {SqlHelper.CONTACTS_COLUMN_NAME,SqlHelper.CONTACTS_COLUMN_PHONE};
        int[] id = {R.id.textView2,R.id.textView3};

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.row, cursor, name, id);

        ListView listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(cursorAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
