package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.UserOpinion;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;

public class CommentsActivity extends AppCompatActivity {

    private LinearLayout commentsLinearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opinions_list);

        this.commentsLinearlayout = this.findViewById(R.id.comments_linearlayout);

        HoyComoDatabase.getListOfOpinionsForCommerce(CommerceActivity.getSelectedCommerceId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                List<UserOpinion> userOpinions = HoyComoDatabaseParser.ParseOpinionList(response);
                for (UserOpinion userOpinion : userOpinions) {
                    TextView opinionTextView = new TextView(CommentsActivity.this);
                    opinionTextView.setText("Usuario: " + userOpinion.getUserId() + "\n Estrellas: " + userOpinion.getStars() + "\n" + userOpinion.getComment()+ "\n");
                    CommentsActivity.this.commentsLinearlayout.addView(opinionTextView);
                }

            }
        });


    }

}
