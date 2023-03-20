package ru.serzhe.mychecks;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.serzhe.mychecks.data.CostsAdapter;
import ru.serzhe.mychecks.ui.DatePickerFragment;

public class CostsFragment extends Fragment implements View.OnClickListener
        //, DatePickerDialog.OnDateSetListener
{

    TextView startTextView;
    TextView endTextView;
    TextView totalTextView;
    TextView totalNDSTextView;
    Date startDate;
    Date endDate;
    CostsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.costs_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view,
                               Bundle savedInstanceState) {

        adapter = new CostsAdapter(getActivity());
        ListView listview = view.findViewById(R.id.costsListView);
//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setIndeterminate(true);
//        listview.setEmptyView(progressBar);
//        RelativeLayout root = (RelativeLayout) findViewById(R.id.activity_costs);
//        root.addView(progressBar);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                CostsAdapter ca = (CostsAdapter)adapter.getAdapter();
                ca.onItemClick(position);
            }
        });

        startTextView = view.findViewById(R.id.startTextView);
        startTextView.setOnClickListener(this);
        endTextView = view.findViewById(R.id.endTextView);
        endTextView.setOnClickListener(this);
        totalTextView = view.findViewById(R.id.totalTextView);
        totalNDSTextView = view.findViewById(R.id.totalNDSTextView);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            startDate = (Date)savedInstanceState.getSerializable("startDate");
            endDate = (Date)savedInstanceState.getSerializable("endDate");
        } else {
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            endDate = c.getTime();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1, 0, 0, 0);
            startDate = c.getTime();
        }

        refreshDates(startDate, endDate);
        listview.setAdapter(adapter);
    }

    public void onClick(View v) {
        DatePickerFragment fragment = new DatePickerFragment();
        //fragment.setTargetFragment(this, 0);
        if (v.getId() == R.id.startTextView)
            fragment.setCurrentValue(startDate);
        else if (v.getId() == R.id.endTextView)
            fragment.setCurrentValue(endDate);
        else
            return;

        //((MainActivity)getActivity()).pushFragments(MainActivity.TAB_CHECKS, fragment,true);
        fragment.show(getActivity().getSupportFragmentManager(), String.valueOf(v.getId()));

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

    public void refreshDates(Date start, Date end) {
        startDate = start;
        endDate = end;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        startTextView.setText(dateFormat.format(startDate));
        endTextView.setText(dateFormat.format(endDate));
        adapter.prepareGroupList(startDate, endDate);
        totalTextView.setText(String.format(Locale.getDefault(), "%.2f", adapter.getTotalCosts()));
        totalNDSTextView.setText(String.format(Locale.getDefault(), getString(R.string.costs_nds), adapter.getTotalNDS()));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("startDate", startDate);
        savedInstanceState.putSerializable("endDate", endDate);
    }

}
