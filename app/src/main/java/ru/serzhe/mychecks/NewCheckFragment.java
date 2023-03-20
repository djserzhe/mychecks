package ru.serzhe.mychecks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.serzhe.mychecks.data.CheckProductsAdapter;
import ru.serzhe.mychecks.objects.Check;
import ru.serzhe.mychecks.objects.CheckItem;
import ru.serzhe.mychecks.ui.DatePickerFragment;
import ru.serzhe.mychecks.ui.TimePickerFragment;

public class NewCheckFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    CheckProductsAdapter adapter;
    Check check;
    TextView dateTextView;
    TextView timeTextView;
    Date dateTimeCheck;
    DynamicHeightListView productsListView;
    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_check_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        mainView = view;
        productsListView = view.findViewById(R.id.checkProductsListView);
        adapter = new CheckProductsAdapter(getActivity(), productsListView);
        if (savedInstanceState != null) {
            adapter.onRestoreInstanceState(savedInstanceState);
        }
        productsListView.setAdapter(adapter);
        productsListView.justifyListViewHeightBasedOnChildren();
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                CheckProductsAdapter cpa = (CheckProductsAdapter)adapter.getAdapter();
                cpa.onItemClick(position);
            }
        });

        check = new Check(getActivity());
        dateTextView = view.findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(this);
        timeTextView = view.findViewById(R.id.timeTextView);
        timeTextView.setOnClickListener(this);
        Calendar c = Calendar.getInstance();
        dateTimeCheck = c.getTime();
        Button saveButton = view.findViewById(R.id.saveCheckButton);
        saveButton.setOnClickListener(this);
        refreshDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveCheckButton: try {
                RadioGroup radioGroup = mainView.findViewById(R.id.typeGroupRadio);
                EditText cash = mainView.findViewById(R.id.cashEditText);
                EditText ecash = mainView.findViewById(R.id.ecashEditText);
                EditText seller = mainView.findViewById(R.id.sellerEditText);

                check.DT = dateTimeCheck;

                if (radioGroup.getCheckedRadioButtonId() == R.id.typeBuyRadio)
                    check.operationType = 1;
                else if (radioGroup.getCheckedRadioButtonId() == R.id.typeReturnRadio)
                    check.operationType = 2;
                else
                    throw new Exception("Не выбран тип чека");

                if (cash.getText().length() > 0)
                    check.cashTotalSum = Float.parseFloat(cash.getText().toString());

                if (ecash.getText().length() > 0)
                    check.ecashTotalSum = Float.parseFloat(ecash.getText().toString());

                check.totalSum = check.cashTotalSum + check.ecashTotalSum;
                float totalSum = 0;
                for (CheckItem item:adapter.items) {
                    if (item.product.equals("")) {
                        throw new Exception("В строке " + (adapter.items.indexOf(item) + 1) + "не указан товар");
                    }
                    if (item.quantity == 0) {
                        throw new Exception("В строке " + (adapter.items.indexOf(item) + 1) + "не указано количество");
                    }
                    totalSum += item.sum;
                }
                if (totalSum != check.totalSum) {
                    throw new Exception("Сумма товаров не равна сумме оплаты");
                }

                check.seller = seller.getText().toString();

                check.items = adapter.items;
                check.saveToSQL();
                ((MainActivity)getActivity()).popFragments();
            }
            catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
                break;
            case R.id.dateTextView: DatePickerFragment dateFragment = new DatePickerFragment();
                dateFragment.setCurrentValue(dateTimeCheck);
                //dateFragment.show(getSupportFragmentManager(), String.valueOf(v.getId()));
                ((MainActivity)getActivity()).pushFragments(MainActivity.TAB_CHECKS, dateFragment,true);
                break;
            case R.id.timeTextView: TimePickerFragment timeFragment = new TimePickerFragment();
                timeFragment.setCurrentValue(dateTimeCheck);
                //timeFragment.show(getSupportFragmentManager(), String.valueOf(v.getId()));
                ((MainActivity)getActivity()).pushFragments(MainActivity.TAB_CHECKS, timeFragment,true);
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        dateTimeCheck = cal.getTime();
        refreshDate();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(dateTimeCheck);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        dateTimeCheck = c.getTime();
        refreshDate();
    }

    private void refreshDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.US);
        //dateFormat.applyPattern("d MMM yyyy");
        dateTextView.setText(dateFormat.format(dateTimeCheck));
        dateFormat.applyPattern("HH:mm:ss");
        timeTextView.setText(dateFormat.format(dateTimeCheck));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    public static class DynamicHeightListView extends ListView {

        public DynamicHeightListView(Context context) {
            super(context);
        }

        public DynamicHeightListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DynamicHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void justifyListViewHeightBasedOnChildren() {
            ListAdapter adapter = getAdapter();
            if (adapter == null) {
                return;
            }
            ViewGroup vg = this;
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, vg);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams par = getLayoutParams();
            par.height = totalHeight + (8 * (adapter.getCount() - 1));
            setLayoutParams(par);
            requestLayout();
        }
    }

}
