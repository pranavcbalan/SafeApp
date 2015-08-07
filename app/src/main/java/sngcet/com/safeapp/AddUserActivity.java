package sngcet.com.safeapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddUserActivity extends ActionBarActivity {

    private SqlHelper sqlHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        sqlHelper = new SqlHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
        return true;
    }

    public void saveUser(View v) {

        EditText editText = (EditText) findViewById(R.id.textName);
        EditText editText1 = (EditText) findViewById(R.id.textMobile);
        Boolean valid = true;
        String name, mob;
        name = editText.getText().toString();
        mob = editText1.getText().toString();
        Log.i("Valueeeeee", editText1.getText().toString());

        if (name.trim().equals("")) {
            editText.setError("Fill this field");
            valid = false;
        }
        if (mob.trim().equals("")) {
            editText1.setError("Fill this field");
            valid = false;
        }
        if (valid) {
            sqlHelper.insertContact(name, mob);
            finish();
            Toast.makeText(getApplicationContext()
                    , "Contact added", Toast.LENGTH_LONG).show();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlHelper.close();
    }
}
