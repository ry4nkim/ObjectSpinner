package kr.ry4nkim.objectspinner_example;

import androidx.appcompat.app.AppCompatActivity;
import kr.ry4nkim.objectspinner.ObjectSpinner;
import kr.ry4nkim.objectspinner_example.model.Goods;

import android.os.Bundle;
import android.util.Log;

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

        mObjectSpinner.setOnItemSelectedListener(new ObjectSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ObjectSpinner view, int position, Object item) {
                Log.i(TAG, "view : " + view);
                Log.i(TAG, "position : " + position);
                Log.i(TAG, "item : " + item);
            }
        });

        mObjectSpinner.setOnNothingSelectedListener(new ObjectSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(ObjectSpinner view) {
                Log.i(TAG, "onNothingSelected");
            }
        });

        List<Goods> itemList = new ArrayList<>();

        itemList.add(new Goods("gs0000", "Item 0", 0));
        itemList.add(new Goods("gs0001", "Item 1", 1000));
        itemList.add(new Goods("gs0002", "Item 2", 2000));
        itemList.add(new Goods("gs0003", "Item 3", 3000));
        itemList.add(new Goods("gs0004", "Item 4", 4000));
        itemList.add(new Goods("gs0005", "Item 5", 5000));
        itemList.add(new Goods("gs0006", "Item 6", 6000));

        mObjectSpinner.setItemList(itemList);
    }
}