package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.UserOpinion;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;

public class OpinionsFragment extends Fragment{

    private LinearLayout commentsLinearlayout;
    private List<UserOpinion> userOpinions;
    private static final int LIMIT_OFF_OPINIONS = 50;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.opinions_list, container, false);
        this.commentsLinearlayout = view.findViewById(R.id.comments_linearlayout);
        HoyComoDatabase.getListOfOpinionsForCommerce(CommerceActivity.getSelectedCommerceId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                userOpinions = HoyComoDatabaseParser.ParseOpinionList(response);
                OpinionsFragment.this.sortByDate();
                if (userOpinions.isEmpty()) {
                    View opinionView = inflater.inflate(R.layout.empty_opinion_item, container, false);
                    OpinionsFragment.this.commentsLinearlayout.addView(opinionView);
                } else {
                    int count = 1;
                    for (UserOpinion userOpinion : userOpinions) {
                        View opinionView = inflater.inflate(R.layout.opinion_item, container, false);

                        TextView nameTextView = opinionView.findViewById(R.id.username_textview);
                        nameTextView.setText(userOpinion.getUserName());
    
                        TextView commentTextView = opinionView.findViewById(R.id.comment_textview);
                        String comment = userOpinion.getComment().replace("\n", " ").replace("\r", "");
                        commentTextView.setText(comment);

                        RatingBar opinionRatingbar = opinionView.findViewById(R.id.opinion_ratingbar);
                        opinionRatingbar.setRating(userOpinion.getStars());

                        TextView opinionDateTextView = opinionView.findViewById(R.id.opinion_date_textview);
                        opinionDateTextView.setText(userOpinion.getTime());

                        OpinionsFragment.this.commentsLinearlayout.addView(opinionView);
                        if (count == LIMIT_OFF_OPINIONS){
                            break;
                        }
                        count++;
                    }
                }
            }
        });
        return view;
    }

    private void sortByDate() {
        Collections.sort(this.userOpinions, new Comparator<UserOpinion>() {
            @Override
            public int compare (UserOpinion o1, UserOpinion o2){
                int leftId = o1.getId();
                int rightId = o2.getId();
                return leftId < rightId ? 1 : (leftId == rightId ? 0 : -1);
            }
        });
    }



}
