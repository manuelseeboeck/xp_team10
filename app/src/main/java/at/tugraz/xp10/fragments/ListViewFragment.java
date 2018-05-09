package at.tugraz.xp10.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import at.tugraz.xp10.adapter.ShoppingListItemListAdapter;
import at.tugraz.xp10.model.ShoppingListItem;
import at.tugraz.xp10.R;


public class ListViewFragment extends Fragment {
    private DatabaseReference mDB;
    private DatabaseReference mShoppingListItems;

    private static final String ARG_SHOPPING_LIST_ID = "shoppingListId";
    private static final String s_Title = "Title";

    private String mShoppingListId = "";
    private String m_Title = "";

    private OnFragmentInteractionListener mListener;

    private ArrayList<ShoppingListItem> mItemList = new ArrayList<>();
    ShoppingListItemListAdapter mAdapter;

    private Boolean mEditMode;
    private View mEditableView;

    public ListViewFragment() {
        // Required empty public constructor
    }


    public static ListViewFragment newInstance(String shoppingListId, String title) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOPPING_LIST_ID, shoppingListId);
        args.putString(s_Title, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mShoppingListId = getArguments().getString(ARG_SHOPPING_LIST_ID);
            m_Title = getArguments().getString(s_Title);
        }
        mEditMode = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_list_view, container, false);

        FloatingActionButton addItemBtn = v.findViewById(R.id.addItemButton);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToDB();
            }
        });
        final Button goShoppingBtn = v.findViewById(R.id.goShoppingButton);
        goShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        CheckBox isPurchasedBox = v.findViewById(R.id.item_isPurchased);
        isPurchasedBox.setVisibility(View.INVISIBLE);

        ImageButton editSaveBtn = v.findViewById(R.id.item_edit_save);
        editSaveBtn.setVisibility(View.INVISIBLE);

        final Button cancelButton = v.findViewById(R.id.lvCancelButton);
        final Button saveButton = v.findViewById(R.id.lvSaveButton);
        Button goShoppingButton = v.findViewById(R.id.goShoppingButton);

        SetTitle();

        mDB = FirebaseDatabase.getInstance().getReference();
        mShoppingListItems = mDB.child("items").child(mShoppingListId);


        mShoppingListItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ListView mListView = v.findViewById(R.id.item_list_view);
        mAdapter = new ShoppingListItemListAdapter(getContext(), mItemList, this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
                if(mEditMode) return true;
                mEditMode = true;
                mEditableView = view;
                mAdapter.setButtonsVisibility(view, View.VISIBLE);
                goShoppingBtn.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                return true;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditMode = false;
                mAdapter.setButtonsVisibility(mEditableView, View.INVISIBLE);
                //mEditableView = null;
                goShoppingBtn.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditMode = false;
                mEditableView = null;
                goShoppingBtn.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
            }
        });

        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void SetTitle()
    {
        if(m_Title != null && !m_Title.isEmpty())
        {
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

            if(actionBar != null)
            {
                actionBar.setTitle(m_Title);
            }
        }
    }


    private void addItemToDB()
    {
        try {
            String name = ((EditText) getView().findViewById(R.id.item_name)).getText().toString();
            String category = ((EditText) getView().findViewById(R.id.item_category)).getText().toString();
            Double unitprice = Double.parseDouble(((EditText) getView().findViewById(R.id.item_price)).getText().toString());
            Double quanitiy = Double.parseDouble(((EditText) getView().findViewById(R.id.item_quantity)).getText().toString());

            String listKey = mShoppingListItems.push().getKey();
            ShoppingListItem item = new ShoppingListItem(name, quanitiy, unitprice, category, false, listKey);
            mShoppingListItems.child(listKey).setValue(item);

            ((EditText) getView().findViewById(R.id.item_name)).setText("");
            ((EditText) getView().findViewById(R.id.item_category)).setText("");
            ((EditText) getView().findViewById(R.id.item_price)).setText("");
            ((EditText) getView().findViewById(R.id.item_quantity)).setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Wrong number format!", Toast.LENGTH_LONG).show();
        }
    }


    private void fetchData(DataSnapshot dataSnapshot)
    {
        mItemList.clear();

        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            ShoppingListItem item = ds.getValue(ShoppingListItem.class);
            mItemList.add(item);
        }

        mAdapter.notifyDataSetChanged();
    }

    public void deleteItem(String id)
    {
        mShoppingListItems.child(id).removeValue();
        mEditMode = false;
    }

    public void editItem(ShoppingListItem item){

        mAdapter.setButtonsVisibility(mEditableView, View.INVISIBLE);

        FloatingActionButton addItemBtn = getView().findViewById(R.id.addItemButton);
        addItemBtn.setVisibility(View.INVISIBLE);
        CheckBox isPurchasedBox = getView().findViewById(R.id.item_isPurchased);
        isPurchasedBox.setVisibility(View.VISIBLE);
        Button editSaveBtn = getView().findViewById(R.id.lvSaveButton);
        editSaveBtn.setVisibility(View.VISIBLE);


        ((EditText) getView().findViewById(R.id.item_name)).setText(item.getName());
        ((EditText) getView().findViewById(R.id.item_category)).setText(item.getCategory());
        ((EditText) getView().findViewById(R.id.item_price)).setText(String.format("%.2f", item.getUnitprice()));
        ((EditText) getView().findViewById(R.id.item_quantity)).setText(String.format("%.0f", item.getQuantity()));
        ((CheckBox) getView().findViewById(R.id.item_isPurchased)).setChecked(item.getIsPurchased());

        //TODO: Change Buttons after pressing save edit.
    }
}
