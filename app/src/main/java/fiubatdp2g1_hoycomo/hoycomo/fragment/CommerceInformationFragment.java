package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;

public class CommerceInformationFragment extends Fragment {

    static public Commerce selectedCommerce;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View commerceInformationLayout = inflater.inflate(R.layout.content_commerce_info, container, false);


        TextView commerceNameTextView = commerceInformationLayout.findViewById(R.id.commerce_name_textview);
        commerceNameTextView.setText(selectedCommerce.getName());

        TextView categoriesTextView = commerceInformationLayout.findViewById(R.id.commerce_categories_textview);
        categoriesTextView.setText(selectedCommerce.getCategoriesString(" - "));

        TextView addressTextView = commerceInformationLayout.findViewById(R.id.commerce_information_address_text);
        addressTextView.setText(selectedCommerce.getAddress());

        TextView openTextView = commerceInformationLayout.findViewById(R.id.commerce_information_open_time_text);
        openTextView.setText(this.getCurrentOpenText());


        TextView commerceeRating = commerceInformationLayout.findViewById(R.id.stars_button_information);
        if (selectedCommerce.hasRating()){
            commerceeRating.setText( selectedCommerce.getRatingAsString() );
        }
        else {
            commerceeRating.setText( "-" );
        }

        TextView openTextIcon = commerceInformationLayout.findViewById(R.id.open_button_icon);
        TextView openText = commerceInformationLayout.findViewById(R.id.open_button_information);
        TextView closedTextIcon = commerceInformationLayout.findViewById(R.id.closed_button_icon);
        TextView closedText = commerceInformationLayout.findViewById(R.id.closed_button_information);
        if (selectedCommerce.isClosed(this.getDay(), this.getHourAndMinutes())) {
            openTextIcon.setVisibility(View.INVISIBLE);
            openText.setVisibility(View.INVISIBLE);
        } else {
            closedTextIcon.setVisibility(View.INVISIBLE);
            closedText.setVisibility(View.INVISIBLE);
        }

        return commerceInformationLayout;
    }

    private String getCurrentOpenText() {
        return selectedCommerce.getOpenHours(this.getDay());
    }

    private String getHourAndMinutes() {
        Calendar c = Calendar.getInstance();// today
        return String.valueOf(c.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(c.get(Calendar.MINUTE)) ;
    }

    private String getDay() {
        String day;
        Calendar c = Calendar.getInstance();// today
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:  day = "Domingo";
                break;
            case 2:  day = "Lunes";
                break;
            case 3:  day = "Martes";
                break;
            case 4:  day = "Miércoles";
                break;
            case 5:  day = "Jueves";
                break;
            case 6:  day = "Viernes";
                break;
            case 7:  day = "Sábado";
                break;
            default: day = "Invalid month";
                break;
        }
        return day;
    }

}