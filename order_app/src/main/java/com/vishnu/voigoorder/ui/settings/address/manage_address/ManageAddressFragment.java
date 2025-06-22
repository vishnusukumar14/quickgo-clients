package com.vishnu.voigoorder.ui.settings.address.manage_address;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vishnu.voigoorder.R;
import com.vishnu.voigoorder.databinding.FragmentSavedDeliveryAddressBinding;
import com.vishnu.voigoorder.eventbus.EBUpdatedAddressData;
import com.vishnu.voigoorder.server.sapi.APIService;
import com.vishnu.voigoorder.server.sapi.ApiServiceGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageAddressFragment extends Fragment {
    private final String LOG_TAG = "SavedAddressFragment";
    FirebaseFirestore db;
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final FirebaseUser currentUser = mAuth.getCurrentUser();
    static String userId;
    ProgressBar progressBar;
    TextView statusTV;
    private List<SavedAddressModel> savedAddressList;
    private SavedAddressAdapter savedAddressAdapter;

    static {
        assert currentUser != null;
        userId = currentUser.getUid();
    }


    public ManageAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        com.vishnu.voigoorder.databinding.FragmentSavedDeliveryAddressBinding binding =
                FragmentSavedDeliveryAddressBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        RecyclerView recyclerView = binding.savedAddressRecyclerView;
        progressBar = binding.savedAddressFragProgressBar;
        statusTV = binding.savedAddressStatusTVTextView;
        statusTV.setVisibility(View.GONE);

        savedAddressList = new ArrayList<>();
        savedAddressAdapter = new SavedAddressAdapter(requireContext(),
                savedAddressList);
        recyclerView.setAdapter(savedAddressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        getSavedAddressData(userId, progressBar);
        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView(@NonNull JsonArray addressData, @NonNull ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        savedAddressList.clear();
        for (JsonElement element : addressData) {
            SavedAddressModel book = new Gson().fromJson(element, SavedAddressModel.class);
            savedAddressList.add(book);
        }
        savedAddressAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private JsonArray loadAddressDataFromFile() {
        try {
            File file = new File(requireContext().getFilesDir(), "address_data.json");
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                JsonElement jsonElement = JsonParser.parseReader(reader);
                reader.close();
                if (jsonElement.isJsonArray()) {
                    return jsonElement.getAsJsonArray();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading address data from file", e);
        }
        return new JsonArray();
    }

    private void getSavedAddressData(String userId, ProgressBar progressBar) {
        JsonArray savedData = loadAddressDataFromFile();

        if (!savedData.isEmpty()) {
            updateRecyclerView(savedData, progressBar);
        } else {
            fetchAddressDataFromServer(userId, progressBar);
        }
    }

    private void saveAddressDataToFile(@NonNull JsonArray addressData) {
        try {
            File file = new File(requireContext().getFilesDir(), "address_data.json");
            FileWriter writer = new FileWriter(file);
            writer.write(addressData.toString());
            writer.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error saving address data to file", e);
        }
    }

    private void fetchAddressDataFromServer(String userId, @NonNull ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);

        APIService apiService = ApiServiceGenerator.getApiService(requireContext());
        Call<JsonObject> call0321 = apiService.getSavedAddresses(userId);

        call0321.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    statusTV.setVisibility(View.GONE);
                    if (responseBody.has("address_data")) {
                        JsonArray addressData = responseBody.getAsJsonArray("address_data");

                        saveAddressDataToFile(addressData);
                        updateRecyclerView(addressData, progressBar);

                        if (responseBody.get("message").getAsString().equals("No address found")) {
                            statusTV.setVisibility(View.VISIBLE);
                            statusTV.setText(R.string.no_saved_address_found);
                        }
                    } else {
                        statusTV.setVisibility(View.VISIBLE);
                        statusTV.setText(R.string.failed_to_fetch_data_try_again);
                        Log.e(LOG_TAG, "Invalid response format: Missing 'address_data' field");
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Failed to fetch address data: Invalid response format", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String errorMessage = "Failed to fetch address data";
                    errorMessage += ": " + response.message();
                    Log.e(LOG_TAG, errorMessage);
                    statusTV.setVisibility(View.VISIBLE);
                    statusTV.setText(R.string.failed_to_fetch_data_try_again);
                    if (isAdded()) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                statusTV.setVisibility(View.VISIBLE);
                statusTV.setText(R.string.failed_to_fetch_data_try_again);
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to fetch address data", Toast.LENGTH_SHORT).show();
                }
                Log.e(LOG_TAG, "Failed to fetch address data", t);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSavedAddressDeleted(EBUpdatedAddressData event) {
        new Handler().postDelayed(() -> getSavedAddressData(userId, progressBar), 450);
    }
}