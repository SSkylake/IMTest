package com.example.imtest.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imtest.entity.FriendEntity;
import com.example.imtest.R;
import com.example.imtest.viewmodel.ChatListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    RecyclerView recyclerView;
    ChatListAdapter adapter;
    private static final String PARA_KEY = "PARA_KEY";
    ChatListViewModel viewModel;
    String entity;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    public static ListFragment newInstance(String entity) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(PARA_KEY,entity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            entity = getArguments().getString(PARA_KEY);
        }
        viewModel = ViewModelProviders.of(this).get(ChatListViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();



        final ListFragment temp = this;

        adapter = new ChatListAdapter(new ArrayList<FriendEntity>(),getContext());
        adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, FriendEntity entity) {

                ChatFragment chatFragment = ChatFragment.newInstance(entity.getTheOther());

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_left_out);
                //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(R.id.main_layout,chatFragment,"child").hide(temp);
                transaction.commit();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getChatList().observe(this, new Observer<List<FriendEntity>>() {
            @Override
            public void onChanged(@Nullable List<FriendEntity> friendEntities) {
                adapter.setFriendList(friendEntities);

            }
        });
    }







}


