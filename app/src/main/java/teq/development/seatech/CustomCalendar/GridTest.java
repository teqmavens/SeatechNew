package teq.development.seatech.CustomCalendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import teq.development.seatech.R;

public class GridTest extends AppCompatActivity {

    AdapterGridTest adapter;
    AutoGridView gridView;
    ArrayList<CalTimeline_Skeleton> parentArrayList;

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridtext_activity);
        parentArrayList = new ArrayList<>();
        for(int i=0;i<64;i++) {
            CalTimeline_Skeleton ske = new CalTimeline_Skeleton();
            if(i%8 == 0){
                ske.setTechname("Amit Kumar Singh Teq mavens Pvt");
                ske.setArrayList(new ArrayList<EventSkeleton>());
            }
            else if(i%2 == 0){
                ske.setTechname("-");
                ArrayList<EventSkeleton> arrayList = new ArrayList<>();
                for(int k=0;k<2;k++) {
                    EventSkeleton eventske = new EventSkeleton();
                    eventske.setEventCustomer("RahulSmall");
                    eventske.setEventJobID("12345678small");
                    eventske.setEventPdfUrl("HttpURLLLSmall");
                    arrayList.add(eventske);
                }
                ske.setArrayList(arrayList);
                // abc.add("");
            }
            else if(i%3 == 0){
                ske.setTechname("-");
                ArrayList<EventSkeleton> arrayList = new ArrayList<>();
                for(int k=0;k<4;k++) {
                    EventSkeleton eventske = new EventSkeleton();
                    eventske.setEventCustomer("Rahul");
                    eventske.setEventJobID("12345678");
                    eventske.setEventPdfUrl("HttpURLLL");
                    arrayList.add(eventske);
                }
                ske.setArrayList(arrayList);
               // abc.add("");
            } else {
                ske.setTechname("");
                ske.setArrayList(new ArrayList<EventSkeleton>());
            }
            parentArrayList.add(ske);
        }

        gridView = (AutoGridView) findViewById(R.id.gridView1);
     //   adapter = new AdapterGridTest(GridTest.this,parentArrayList);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, numbers);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
