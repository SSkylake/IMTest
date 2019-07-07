package com.example.imtest.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imtest.entity.ChatHistoryEntity;
import com.example.imtest.R;
import com.example.imtest.viewmodel.ChatDetailViewModel;

import java.util.LinkedList;
import java.util.List;



public class ChatFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";


    private String entity;

    Toolbar toolbar;
    ImageView btnSend;
    ImageView btnMore;
    TextView tvSendImg;
    TextView tvSendFile;
    TextView tvMakePhoneCall;
    EditText etSend;

    RecyclerView recyclerView;
    CardView cardView;

    ChatDetailAdapter adapter;
    ChatDetailViewModel viewModel;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String entity) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, entity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entity = getArguments().getString(ARG_PARAM1);
        }

        viewModel = ViewModelProviders.of(this).get(ChatDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        toolbar = view.findViewById(R.id.toolbar_chat);
        toolbar.setTitle(entity);
        recyclerView = view.findViewById(R.id.recycler_view_history);
        btnSend = view.findViewById(R.id.btn_send);
        etSend = view.findViewById(R.id.et_send);
        btnMore = view.findViewById(R.id.more_functions);
        cardView = view.findViewById(R.id.chat_card_view);
        tvMakePhoneCall = view.findViewById(R.id.make_phone_call);
        tvSendFile = view.findViewById(R.id.send_file);
        tvSendImg = view.findViewById(R.id.send_img);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new ChatDetailAdapter(new LinkedList<ChatHistoryEntity>(),getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true); //关键
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        viewModel.getHistoryList(entity).observe(this, new Observer<List<ChatHistoryEntity>>() {
            @Override
            public void onChanged(@Nullable List<ChatHistoryEntity> chatHistoryEntities) {
                adapter.setHistoryEntityList(chatHistoryEntities);
                recyclerView.smoothScrollToPosition(adapter.getItemCount()+1);
                viewModel.notifyReadingDone(entity);

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.sendMessage(entity,etSend.getText().toString());
                etSend.setText("");
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardView.getVisibility() == View.GONE){
                    cardView.setVisibility(View.VISIBLE);
                    btnMore.setImageDrawable(getContext().getDrawable(R.drawable.ic_indeterminate_check_box_black_24dp));
                }else {
                    cardView.setVisibility(View.GONE);
                    btnMore.setImageDrawable(getContext().getDrawable(R.drawable.ic_add_box_black_24dp));

                }
            }
        });

        tvSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvMakePhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //发起语音通话
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.person_function_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }
}

