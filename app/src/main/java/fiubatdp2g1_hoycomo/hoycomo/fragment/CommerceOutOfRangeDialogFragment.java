package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fiubatdp2g1_hoycomo.hoycomo.R;

public class CommerceOutOfRangeDialogFragment extends DialogFragment {

    private OnConfirmationListener onConfirmationListener;

    public void setOnConfirmationListener(ConfirmOrderFragment onConfirmationListener) {
        this.onConfirmationListener = onConfirmationListener;
    }

    public interface OnConfirmationListener {
        void sendOrderConfirmation();
    }

    private Button confirmOutOfRangeCommerceButton;
    private Button cancelOutOfRangeCommerceButton;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.commerce_out_of_range_dialog, container, false);

        this.confirmOutOfRangeCommerceButton = view.findViewById(R.id.confirm_out_of_range_commerce_button);
        this.cancelOutOfRangeCommerceButton = view.findViewById(R.id.cancel_out_of_range_commerce_button);

        this.cancelOutOfRangeCommerceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        this.confirmOutOfRangeCommerceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommerceOutOfRangeDialogFragment.this.sendOrderConfirmation();
                getDialog().dismiss();
            }
        });

        return view;
    }

    private void sendOrderConfirmation() {
        this.onConfirmationListener.sendOrderConfirmation();
    }


    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            this.onConfirmationListener = (CommerceOutOfRangeDialogFragment.OnConfirmationListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
    */





}
