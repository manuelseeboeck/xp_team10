package at.tugraz.xp10.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import at.tugraz.xp10.R;
import at.tugraz.xp10.adapter.AllListOverviewAdapter;

public class AllListOverviewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_list_overview, container, false);

        String[] dummy = {"Item1", "Item2", "Item3", "Item4", "Item5", "Item6"};
        GridView gridview = view.findViewById(R.id.grid_overview);
        gridview.setAdapter(new AllListOverviewAdapter(getActivity(), dummy));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
               int position, long id) {
                //add implementation here
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Lists");

        FloatingActionButton fab = view.findViewById(R.id.button_add_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                //TODO: has to be replaced by title from database
                bundle.putString("Title", "My List");


                Fragment fragment = new ListViewFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_frame,fragment,"myFragmentTag").addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public static AllListOverviewFragment newInstance() {
        AllListOverviewFragment fragment = new AllListOverviewFragment();
        return fragment;
    }
}