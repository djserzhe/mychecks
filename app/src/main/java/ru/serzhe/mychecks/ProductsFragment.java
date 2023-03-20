package ru.serzhe.mychecks;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ru.serzhe.mychecks.data.ProductsAdapter;
import ru.serzhe.mychecks.objects.ProductGroup;
import ru.serzhe.mychecks.objects.ProductGroupBase;

public class ProductsFragment extends Fragment {

    public ProductsAdapter pa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.products_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view,
                               Bundle savedInstanceState) {
        pa = new ProductsAdapter(getActivity());
        ListView listview = view.findViewById(R.id.productsListView);
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        listview.setEmptyView(progressBar);
        ConstraintLayout root = view.findViewById(R.id.products_fragment);
        root.addView(progressBar);

        listview.setAdapter(pa);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                ProductsAdapter pa = (ProductsAdapter)adapter.getAdapter();
                pa.onItemClick(position);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ProductsAdapter pa = (ProductsAdapter)parent.getAdapter();
                pa.onItemLongClick(position, view);
                return true;
            }
        });

//        final FloatingActionButton fab = view.findViewById(R.id.fabProducts);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                View mView = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
//                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(view.getContext());
//                alertDialogBuilderUserInput.setView(mView);
//
//                //final EditText userInputDialogEditText = mView.findViewById(R.id.categoryInputText);
//                alertDialogBuilderUserInput
//                        .setCancelable(false)
//                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//                                Dialog d = (Dialog) dialogBox;
//                                EditText category = d.findViewById(R.id.categoryInputText);
//                                pa.createCategory(category.getText().toString());
//                            }
//                        })
//
//                        .setNegativeButton("Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialogBox, int id) {
//                                        dialogBox.cancel();
//                                    }
//                                });
//
//                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
//                alertDialogAndroid.show();
//            }
//        });

//        final ImageView imgRecycleBin = view.findViewById(R.id.ic_delete);
//        imgRecycleBin.setVisibility(View.INVISIBLE);
//        root.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                switch (event.getAction()) {
//                    case DragEvent.ACTION_DRAG_STARTED:
//                        imgRecycleBin.setVisibility(View.VISIBLE);
//                        ((MainActivity)getActivity()).fab.hide();
//                        return true;
//                    case DragEvent.ACTION_DROP:
//                        ProductGroupBase item = (ProductGroupBase) event.getLocalState();
//                        String dragItemType = item.getClass().getSimpleName();
//                        long dragItemID = item.ID;
//                        if (dragItemType.equals("ProductGroup")) {
//                            if (((ProductGroup)item).products.size() > 0) {
//                                Toast.makeText(getActivity(), "The group is not empty", Toast.LENGTH_SHORT).show();
//                                return false;
//                            }
//                            pa.findItemInProductList(dragItemID, dragItemType, null, true);
//                            pa.notifyDataSetChanged();
//                            return true;
//                        }
//                        else
//                            return false;
//                    case DragEvent.ACTION_DRAG_ENDED:
//                        imgRecycleBin.setVisibility(View.INVISIBLE);
//                        ((MainActivity)getActivity()).fab.show();
//                        return true;
//                }
//                return false;
//            }
//        });
//        imgRecycleBin.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                switch (event.getAction()) {
//                    case DragEvent.ACTION_DRAG_STARTED:
//                        return true;
//                    case DragEvent.ACTION_DROP:
//                        ProductGroupBase item = (ProductGroupBase) event.getLocalState();
//                        String dragItemType = item.getClass().getSimpleName();
//                        long dragItemID = item.ID;
//                        if (dragItemType.equals("ProductGroup")) {
//                            if (((ProductGroup)item).products.size() > 0) {
//                                Context mContext = getActivity();
//                                Toast.makeText(mContext, mContext.getString(R.string.group_not_empty), Toast.LENGTH_SHORT).show();
//                                return false;
//                            }
//
//                            return pa.removeGroup(dragItemID);
//                        }
//                        else
//                            return false;
//                    case DragEvent.ACTION_DRAG_ENDED:
//                        return true;
//                }
//                return false;
//            }
//        });

//        FloatingActionButton fab = ((MainActivity)getActivity()).fab;
//        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//        p.setAnchorId(R.id.productsListView);
//        fab.setLayoutParams(p);
    }

    public void createProductGroup() {
        View mView = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        //final EditText userInputDialogEditText = mView.findViewById(R.id.categoryInputText);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        Dialog d = (Dialog) dialogBox;
                        EditText category = d.findViewById(R.id.categoryInputText);
                        pa.createCategory(category.getText().toString());
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

}
