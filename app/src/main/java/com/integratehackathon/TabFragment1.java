package com.integratehackathon;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by T on 2015/09/26.
 */
public class TabFragment1 extends Fragment {
    private final static String[] areaArray = {"Choose Area", "Africa", "Americas", "Asia", "Europe", "Pacific Area"};
    private final static String[] countries_america = {"USA", "Canada", "Mexico"};
    private final static String[] countries_europe = {"UK", "France", "Germany"};
    private final static String[] countries_asia = {"China", "Japan", "Korea"};
    private final static String[] countries_africa = {"South Africa", "Algeria", "Egypt"};
    private final static String[] countries_pacific = {"Australia", "New Zealand", "Indonesia"};

    private View _view;

    private Button btnDate, btnGo;
    private Spinner spnPlace;
    private List<String> listArea;

    private Item item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        findViews();
        setListeners();

        item = new Item();
        listArea = new ArrayList<String>();
        for (int i = 0; i < areaArray.length; i++) {
            listArea.add(areaArray[i]);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listArea);
        spnPlace.setAdapter(dataAdapter);

        return _view;
    }

    void findViews() {
        btnDate = (Button) _view.findViewById(R.id.btn_date);
        btnGo = (Button) _view.findViewById(R.id.btn_go);
        spnPlace = (Spinner) _view.findViewById(R.id.spn_place);
    }

    void setListeners() {
        btnDate.setOnClickListener(new ButtonClickListener());
        btnGo.setOnClickListener(new ButtonClickListener());
        spnPlace.setOnItemSelectedListener(new SpinnerItemClickListener());
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_date:
                    showDatePickerDialog();
                    break;
                case R.id.btn_go:
                    saveData();
                    break;
            }
        }
    }

    class SpinnerItemClickListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Selected item: ", parent.getItemAtPosition(position).toString());
            item.setArea(parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker picker, int year, int month, int day) {
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date date = cal.getTime();
                String str = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date);
                btnDate.setText(str);
            }
        }, year, month - 1, day);
        dialog.show();
    }

    boolean readyToSave() {
        if (item.getArea().equals("Choose Area")) { // must be equal to the stringArray[0]!!!!
            Toast.makeText(getActivity(), "Please choose area you want to travel.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void saveData() {
        if (readyToSave()) {
            new postData().execute();
        }
    }

    class postData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(Item.ITEM_NAME, item.getName());
                jsonObject.put(Item.ITEM_DATE, item.getDate());
                jsonObject.put(Item.ITEM_AREA, item.getArea());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(MainActivity.URL)
                        .post(RequestBody.create(
                                MediaType.parse("application/json; charset=utf-8"),
                                jsonObject.toString())
                        )
                        .build();
                Response response = client.newCall (request).execute();
                if (response.isSuccessful()) {
                    response.body().string();
                    Log.i("Post Request", "Post success");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Fragment1", "Post failed");
            }

            return "";
        }

        protected void onPostExecute(String str) {
        }
    }
}