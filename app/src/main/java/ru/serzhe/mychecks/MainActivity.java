package ru.serzhe.mychecks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Stack;

import ru.serzhe.mychecks.objects.ProductGroup;
import ru.serzhe.mychecks.objects.ProductGroupBase;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private HashMap<String, Stack<Fragment>> mStacks;
    public static final String TAB_COSTS  = "tab_costs";
    public static final String TAB_CHECKS  = "tab_checks";
    public static final String TAB_PRODUCTS  = "tab_products";
    public FloatingActionButton fab;

    private String mCurrentTab;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_costs:
                    selectedTab(TAB_COSTS);
                    fab.hide();
                    return true;
                case R.id.navigation_checks:
                    selectedTab(TAB_CHECKS);
                    fab.show();
                    return true;
                case R.id.navigation_products:
                    selectedTab(TAB_PRODUCTS);
                    fab.show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fabChecks);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentTab) {
                    case TAB_CHECKS: pushFragments(TAB_CHECKS, new NewCheckFragment(),true);
                                     break;
                    case TAB_PRODUCTS: ProductsFragment pf = (ProductsFragment) mStacks.get(TAB_PRODUCTS).peek();
                                       pf.createProductGroup();
                                       break;
                }
            }
        });

        final ImageView imgRecycleBin = findViewById(R.id.ic_delete);
        imgRecycleBin.setVisibility(View.INVISIBLE);
        final ConstraintLayout content = findViewById(R.id.content);
        content.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (mCurrentTab != TAB_PRODUCTS)
                    return false;
//                else
//                    return true;

                //Log.d("DRAG_EVENT", "Layout: " + String.valueOf(event.getAction()));

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        imgRecycleBin.setVisibility(View.VISIBLE);
                        fab.hide();
                        return true;
//                    case DragEvent.ACTION_DROP:
//                        ProductGroupBase item = (ProductGroupBase) event.getLocalState();
//                        String dragItemType = item.getClass().getSimpleName();
//                        long dragItemID = item.ID;
//                        if (dragItemType.equals("ProductGroup")) {
//                            if (((ProductGroup)item).products.size() > 0) {
//                                Toast.makeText(getApplicationContext(), "The group is not empty", Toast.LENGTH_SHORT).show();
//                                return false;
//                            }
//                            pf.pa.findItemInProductList(dragItemID, dragItemType, null, true);
//                            pf.pa.notifyDataSetChanged();
//                            return true;
//                        }
//                        else
//                            return false;
                    case DragEvent.ACTION_DROP:
                    case DragEvent.ACTION_DRAG_ENDED:
                        imgRecycleBin.setVisibility(View.INVISIBLE);
                        fab.show();
                        return true;
                }
                return false;
            }
        });

        imgRecycleBin.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                //Log.d("DRAG_EVENT", "Image: " + String.valueOf(event.getAction()));

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        ((ImageView)v).setColorFilter(Color.RED);
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DROP:
                        ((ImageView)v).setColorFilter(Color.BLACK);
                        v.invalidate();
                        final ProductsFragment pf = (ProductsFragment) mStacks.get(TAB_PRODUCTS).peek();
                        ProductGroupBase item = (ProductGroupBase) event.getLocalState();
                        String dragItemType = item.getClass().getSimpleName();
                        long dragItemID = item.ID;
                        if (dragItemType.equals("ProductGroup")) {
                            final ProductGroup pg = (ProductGroup)item;
                            if (pg.products.size() > 0) {
                                Context mContext = getApplicationContext();
                                Toast.makeText(mContext, mContext.getString(R.string.group_not_empty), Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            if (pf.pa.removeGroup(dragItemID)) {
                                Snackbar snackbar = Snackbar
                                        .make(content, R.string.group_deleted, Snackbar.LENGTH_LONG);
                                snackbar.setAction(R.string.cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        // undo is selected, restore the deleted item
                                        pf.pa.createCategory(pg.name);
                                    }
                                });
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();
                            }
                            else
                                return false;
                        }
                        else {
                            Context mContext = getApplicationContext();
                            Toast.makeText(mContext, mContext.getString(R.string.only_groups_can_be_deleted), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    case DragEvent.ACTION_DRAG_EXITED:
                        ((ImageView)v).setColorFilter(Color.BLACK);
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        imgRecycleBin.setVisibility(View.INVISIBLE);
                        fab.show();
                        return true;
                }
                return false;
            }
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(TAB_COSTS, new Stack<Fragment>());
        mStacks.put(TAB_CHECKS, new Stack<Fragment>());
        mStacks.put(TAB_PRODUCTS, new Stack<Fragment>());

        navigation.setSelectedItemId(R.id.navigation_checks);
        selectedTab(TAB_CHECKS);
    }

    private void selectedTab(String tabId)
    {
        mCurrentTab = tabId;

        if(mStacks.get(tabId).size() == 0){
            if(tabId.equals(TAB_COSTS)){
                pushFragments(tabId, new CostsFragment(),true);
            }else if(tabId.equals(TAB_CHECKS)){
                pushFragments(tabId, new ChecksFragment(),true);
            }else if(tabId.equals(TAB_PRODUCTS)){
                pushFragments(tabId, new ProductsFragment(),true);
            }
        }else {
            pushFragments(tabId, mStacks.get(tabId).lastElement(),false);
        }
    }

    public void pushFragments(String tag, Fragment fragment, boolean shouldAdd){
        if(shouldAdd)
            mStacks.get(tag).push(fragment);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    public void popFragments(){
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);

        mStacks.get(mCurrentTab).pop();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if(mStacks.get(mCurrentTab).size() == 1){
            finish();
            return;
        }

        popFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_db) {
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "/data/data/" + getPackageName() + "/databases/MyChecks.db";
                    String backupDBPath = "backupname.db";
                    File currentDB = new File(currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            } catch (Exception e) {
                //Snackbar.make(, e.getMessage(), Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                return false;
            }
            //Snackbar.make(, "Database exported", Snackbar.LENGTH_LONG)
                    //.setAction("Action", null).show();
            return true;
        }
        else if (id == R.id.action_load_db) {

        }
        else if (id == R.id.action_test_activity) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (mCurrentTab == TAB_COSTS) {
            Calendar cal = new GregorianCalendar(year, month, day);
            CostsFragment cf = (CostsFragment) mStacks.get(TAB_COSTS).peek();
            Date startDate = cf.startDate;
            Date endDate = cf.endDate;
            switch (Integer.parseInt(datePicker.getTag().toString())) {
                case R.id.startTextView:
                    startDate = cal.getTime();
                    break;
                case R.id.endTextView:
                    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                    endDate = cal.getTime();
                    break;
            }
            cf.refreshDates(startDate, endDate);
        }
    }

//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int day) {
//        Calendar cal = new GregorianCalendar(year, month, day);
//        switch (Integer.parseInt(view.getTag().toString())) {
//            case R.id.startTextView:
//                startDate = cal.getTime();
//                break;
//            case R.id.endTextView:
//                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
//                endDate = cal.getTime();
//                break;
//        }
//        refreshDates();
//    }
}
