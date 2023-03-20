package ru.serzhe.mychecks;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.serzhe.mychecks.data.ChecksRecyclerViewAdapter;
import ru.serzhe.mychecks.objects.Check;
import ru.serzhe.mychecks.ui.RecyclerItemTouchHelper;

public class ChecksFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    ChecksRecyclerViewAdapter mAdapter;
    private ConstraintLayout constraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.checks_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view,
                               Bundle savedInstanceState) {
        RecyclerView mRecyclerView = view.findViewById(R.id.checksRecyclerView);
        constraintLayout = view.findViewById(R.id.checks_fragment_layout);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mAdapter = new ChecksRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
//        FloatingActionButton fab = ((MainActivity)getActivity()).fab;
//        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//        p.setAnchorId(R.id.checksRecyclerView);
//        fab.setLayoutParams(p);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ChecksRecyclerViewAdapter.ViewHolder) {
            // backup of removed item for undo purpose
            final Check deletedCheck = mAdapter.getItem(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // get the removed item name to display it in snack bar
            //String name = String.format(Locale.getDefault(), "%s\n%s", deletedCheck.seller, deletedCheck.DT);

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(constraintLayout, R.string.check_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedCheck, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
