package kr.ry4nkim.objectspinner_example.view;

import androidx.appcompat.app.AppCompatActivity;
import kr.ry4nkim.objectspinner.ObjectSpinner;
import kr.ry4nkim.objectspinner_example.R;
import kr.ry4nkim.objectspinner_example.model.Goods;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ObjectSpinner<Goods> mObjectSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mObjectSpinner = findViewById(R.id.spinner);

        mObjectSpinner.setOnItemSelectedListener((view, position, item) -> {
            Snackbar.make(view, "Selected Object : " + item, Snackbar.LENGTH_SHORT).show();
        });

        mObjectSpinner.setOnNothingSelectedListener(view -> {
            Log.i(TAG, "onNothingSelected");
        });

        List<Goods> goodsList = new ArrayList<>();

        goodsList.add(new Goods("gs0001", "Item 1", 1000));
        goodsList.add(new Goods("gs0002", "Item 2", 2000));
        goodsList.add(new Goods("gs0003", "Item 3", 3000));
        goodsList.add(new Goods("gs0004", "Item 4", 4000));
        goodsList.add(new Goods("gs0005", "Item 5", 5000));
        goodsList.add(new Goods("gs0006", "Item 6", 6000));

        mObjectSpinner.setItemList(goodsList);
    }
}