package ru.serzhe.mychecks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.serzhe.mychecks.objects.Check;
import ru.serzhe.mychecks.objects.CheckItem;

public class CheckDetailsFragment extends Fragment {

    Check check;

    public static CheckDetailsFragment newInstance(int checkID) {
        Bundle bundle = new Bundle();
        bundle.putInt("checkID", checkID);

        CheckDetailsFragment fragment = new CheckDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            check = new Check(getActivity(), bundle.getInt("checkID"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.check_details_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view,
                               Bundle savedInstanceState) {

        TextView sellerTextView = view.findViewById(R.id.sellerTextView);
        TextView checkInfoTextView = view.findViewById(R.id.checkInfoTextView);
        TextView dateCheckTextView = view.findViewById(R.id.dateCheckTextView);
        TextView timeCheckTextView = view.findViewById(R.id.timeCheckTextView);
        TextView shiftNumberValueTextView = view.findViewById(R.id.shiftNumberValueTextView);
        TextView cashierNameTextView = view.findViewById(R.id.cashierNameTextView);
        TextView cashValueTextView = view.findViewById(R.id.cashValueTextView);
        TextView cardValueTextView = view.findViewById(R.id.cardValueTextView);
        TextView totalValueTextView = view.findViewById(R.id.totalValueTextView);
        TextView nds10ValueTextView = view.findViewById(R.id.nds10ValueTextView);
        TextView nds18ValueTextView = view.findViewById(R.id.nds18ValueTextView);
        TextView innValueTextView = view.findViewById(R.id.innValueTextView);
        TextView addressValueTextView = view.findViewById(R.id.addressValueTextView);
        TextView taxationTypeValueTextView = view.findViewById(R.id.taxationTypeValueTextView);
        TextView kktRegIdValueTextView = view.findViewById(R.id.kktRegIdValueTextView);
        TextView fiscalDriveNumberValueTextView = view.findViewById(R.id.fiscalDriveNumberValueTextView);
        TextView fiscalDocumentNumberValueTextView = view.findViewById(R.id.fiscalDocumentNumberValueTextView);
        TextView fiscalSignValueTextView = view.findViewById(R.id.fiscalSignValueTextView);

        readBundle(getArguments());

        sellerTextView.setText(check.seller);
        String checkType;
        if (check.operationType == 1)
            checkType = "Кассовый чек (приход)";
        else if (check.operationType == 2)
            checkType = "Кассовый чек (возврат прихода)";
        else
            checkType = "Кассовый чек";
        //checkInfoTextView.setText(checkType + " № " + String.valueOf(check.requestNumber));
        checkInfoTextView.setText(getString(R.string.check_header, checkType, check.requestNumber));
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.US);
        //dateFormat.applyPattern("d MMM yyyy");
        dateCheckTextView.setText(dateFormat.format(check.DT));
        dateFormat.applyPattern("HH:mm:ss");
        timeCheckTextView.setText(dateFormat.format(check.DT));
        shiftNumberValueTextView.setText(String.valueOf(check.shiftNumber));
        cashierNameTextView.setText(check.operator);

        LinearLayout productsLinearLayout = view.findViewById(R.id.productsLinearLayout);
        //LayoutInflater inflater = getActivity().getLayoutInflater();

        for (CheckItem item : check.items) {
            View itemView = getLayoutInflater().inflate(R.layout.check_product, productsLinearLayout, false);
            TextView checkProductTextView = itemView.findViewById(R.id.checkProductTextView);
            TextView productSumTextView = itemView.findViewById(R.id.productSumTextView);
            TextView categoryTextView = itemView.findViewById(R.id.categoryTextView);
            TextView qtyPriceTextView = itemView.findViewById(R.id.qtyPriceTextView);
            TextView ndsTextView = itemView.findViewById(R.id.ndsTextView);

            checkProductTextView.setText(item.product);
            productSumTextView.setText(String.valueOf(item.sum));
            categoryTextView.setText(item.category);
            //qtyPriceTextView.setText(String.valueOf(item.quantity) + " * " + String.valueOf(item.price));
            qtyPriceTextView.setText(getString(R.string.price_quantity, item.quantity, item.price));
            String nds = "";
            if (item.nds18 > 0)
                nds = "НДС 18%: " + item.nds18;
            else if (item.nds10 > 0)
                nds = "НДС 10%: " + item.nds10;
            else
                ndsTextView.setVisibility(View.INVISIBLE);
            ndsTextView.setText(nds);

            productsLinearLayout.addView(itemView);
        }

        cashValueTextView.setText(String.valueOf(check.cashTotalSum));
        cardValueTextView.setText(String.valueOf(check.ecashTotalSum));
        totalValueTextView.setText(String.valueOf(check.totalSum));
        if (check.nds10 > 0)
            nds10ValueTextView.setText(String.valueOf(check.nds10));
        else
            nds10ValueTextView.setVisibility(View.INVISIBLE);
        if (check.nds18 > 0)
            nds18ValueTextView.setText(String.valueOf(check.nds18));
        else
            nds18ValueTextView.setVisibility(View.INVISIBLE);
        innValueTextView.setText(check.sellerInn);
        String taxationType = "";
        switch (check.taxationType) {
            case 1: taxationType = "ОСН";
                break;
            case 2: taxationType = "УСН доход";
                break;
            case 4: taxationType = "УСН доход - расход";
                break;
            case 8: taxationType = "ЕНВД";
                break;
            case 16: taxationType = "ЕСН";
                break;
            case 32: taxationType = "Патент";
                break;
        }

        taxationTypeValueTextView.setText(taxationType);
        addressValueTextView.setText(check.retailPlaceAddress);
        fiscalDriveNumberValueTextView.setText(check.fiscalDriveNumber);
        kktRegIdValueTextView.setText(check.kktRegId);
        fiscalDocumentNumberValueTextView.setText(String.valueOf(check.fiscalDocumentNumber));
        fiscalSignValueTextView.setText(check.fiscalSign);

    }

}