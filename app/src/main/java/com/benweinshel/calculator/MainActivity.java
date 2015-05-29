package com.benweinshel.calculator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.editText) TextInputLayout inputEditText;
    @InjectView(R.id.my_recycler_view) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<CalculationLog> calculations = new ArrayList<CalculationLog>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        inputEditText.setErrorEnabled(true);
        inputEditText.setHint("Type a problem");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerViewAdapter(calculations);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void calculateResult(View view) {

        String input = inputEditText.getEditText().getText().toString();

        // Do the calculation
        try {
            doResult(input);
        } catch (Exception e) {
            if (!e.getMessage().isEmpty()) {
                CharSequence message = (CharSequence) e.getMessage();
                inputEditText.setError(message);
//                Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
//                snackbar.show();
            }
            else {
                CharSequence error = (CharSequence) e;
                inputEditText.setError(error);
//                Snackbar snackbar = Snackbar.make(view, error, Snackbar.LENGTH_LONG);
//                snackbar.show();
            }
        }

    }

    private void doResult(String input) throws Exception{

        String result = Maths.doMath(input);
        CalculationLog c = new CalculationLog(input, result);
        int calcSize = calculations.size();
        calculations.add(c);
        mAdapter.notifyItemInserted(calcSize);
        mRecyclerView.scrollToPosition(calcSize);
    }

    public void buttonPressed(View view) {
        Button b = (Button) view;
        CharSequence buttonText = b.getText();
        inputEditText.getEditText().append(buttonText);
    }
    public void delPressed (View view) {
        int length = inputEditText.getEditText().getText().length();
        if (length > 0) {
            inputEditText.getEditText().getText().delete(length - 1, length);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_about:
                return true;
            case R.id.action_clear_hist:
                calculations.clear();
                mAdapter.notifyDataSetChanged();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_about).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            }
        });
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }
}