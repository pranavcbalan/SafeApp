package sngcet.com.safeapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }


    public void add(View v){
        Intent intent=new Intent(this,AddUserActivity.class);
        startActivity(intent);
    }

    public void edit(View v){
        Intent intent=new Intent(this,EditUsersActivity.class);
        startActivity(intent);
    }

    public void delete(View v){
        SqlHelper sqlHelper=new SqlHelper(this);
        Toast.makeText(getApplicationContext(), "Deleted "+sqlHelper.deleteAll()+" rows !!!!!", Toast.LENGTH_LONG).show();
    }

    public void view(View v){
        Intent intent=new Intent(this,ViewUsersActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
