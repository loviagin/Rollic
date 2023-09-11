package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.USER_UID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.UserAdapter;
import com.loviagin.rollic.models.User;

import java.util.LinkedList;
import java.util.List;

public class SearchingActivity extends AppCompatActivity {

    private static final String TAG = "Search_Activity_TAG";
    private ImageButton buttonBack;
    private TabLayout tabLayout;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView textViewNothing;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        tabLayout = findViewById(R.id.tlSearch);
        searchView = findViewById(R.id.svSearching);
        recyclerView = findViewById(R.id.rvSearching);
        textViewNothing = findViewById(R.id.tvNoResults);
        buttonBack = findViewById(R.id.bBackSearch);
        userList = new LinkedList<>();
        userAdapter = new UserAdapter(userList);

        searchView.requestFocus();
        buttonBack.setOnClickListener(view -> finish());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(userAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadElements();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Выберите, какое поле вы хотите использовать для фильтрации
                loadElements();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Ничего не делать
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Ничего не делать
            }
        });
    }

    private void loadElements() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String filterField = tabLayout.getSelectedTabPosition() == 0 ? "f_name" : "username";

        String searchTextLower = searchView.getQuery().toString().trim().toLowerCase();
        String searchTextUpper = cap(searchView.getQuery().toString().trim());

        Task<QuerySnapshot> taskLower = db.collection("users")
                .orderBy(filterField)
                .startAt(searchTextLower)
                .endAt(searchTextLower + "\uf8ff")
                .get();

        Task<QuerySnapshot> taskUpper = db.collection("users")
                .orderBy(filterField)
                .startAt(searchTextUpper)
                .endAt(searchTextUpper + "\uf8ff")
                .get();

        Tasks.whenAllSuccess(taskLower, taskUpper).addOnSuccessListener(objects -> {
            userList.clear();
            for (Object object : objects) {
                QuerySnapshot querySnapshot = (QuerySnapshot) object;
                for (QueryDocumentSnapshot document : querySnapshot) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                }
                if (userList.isEmpty()) {
                    textViewNothing.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    userAdapter.notifyDataSetChanged();
                    textViewNothing.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(e -> Log.d(TAG, "Error getting documents: ", e));
    }

    public static String cap(String str) {
        String[] words = str.split("\\s");
        StringBuilder capitalizeWord = new StringBuilder();
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterFirst = w.substring(1);
            capitalizeWord.append(first.toUpperCase()).append(afterFirst).append(" ");
        }
        return capitalizeWord.toString().trim();  // удалить лишний пробел в конце строки
    }

}