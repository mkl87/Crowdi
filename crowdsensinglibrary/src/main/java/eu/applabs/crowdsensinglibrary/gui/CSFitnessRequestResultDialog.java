package eu.applabs.crowdsensinglibrary.gui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.applabs.crowdsensinglibrary.R;

public class CSFitnessRequestResultDialog extends Dialog implements View.OnClickListener {

    public interface ICSFitnessRequestResultDialogListener {
        void onValueSelected(String value);
    }

    private Context mContext = null;
    private CSFitnessRequestResultDialog mCSFitnessRequestResultDialog = null;
    private String mLabelVerticalAxis = null;
    private String mLabelHorizontalAxis = null;
    private List<String> mValues = null;
    private List<String> mValueLabels = null;
    private LinearLayout mLinearLayout = null;
    private boolean mViewGenerated = false;

    private List<ICSFitnessRequestResultDialogListener> mICSFitnessRequestResultDialogListenerList = null;

    public CSFitnessRequestResultDialog(Context context,
                                        String labelVerticalAxis,
                                        String labelHorizontalAxis,
                                        List<String> values,
                                        List<String> valueLabels) {
        super(context);

        mContext = context;
        mCSFitnessRequestResultDialog = this;
        mLabelVerticalAxis = labelVerticalAxis;
        mLabelHorizontalAxis = labelHorizontalAxis;
        mValues = values;
        mValueLabels = valueLabels;
        mICSFitnessRequestResultDialogListenerList = new ArrayList<>();
    }

    public void registerListener(ICSFitnessRequestResultDialogListener listener) {
        mICSFitnessRequestResultDialogListenerList.add(listener);
    }

    public void unregisterListener(ICSFitnessRequestResultDialogListener listener) {
        mICSFitnessRequestResultDialogListenerList.remove(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_csfitnessrequestresult);

        VerticalTextView vtv = (VerticalTextView) findViewById(R.id.id_CSFitnessRequestResultDialog_VerticalAxis);
        vtv.setText(mLabelVerticalAxis);

        TextView tv = (TextView) findViewById(R.id.id_CSFitnessRequestResultDialog_HorizontalAxis);
        tv.setText(mLabelHorizontalAxis);

        mLinearLayout = (LinearLayout) findViewById(R.id.id_CSFitnessRequestResultDialog_LinearLayout);
        mLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(!mViewGenerated) {
                            mViewGenerated = true;
                            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            View resultView;
                            TextView textViewDescription;
                            Button buttonValue;

                            for(int i = 0; i < mValues.size(); ++i) {
                                resultView = inflater.inflate(R.layout.view_csfitnessrequestresult, null, false);

                                textViewDescription = (TextView) resultView.findViewById(R.id.id_CSFitnessRequestResultView_TextView_Description);
                                textViewDescription.setText(mValueLabels.get(i));

                                buttonValue = (Button) resultView.findViewById(R.id.id_CSFitnessRequestResultView_Button_Value);
                                buttonValue.setOnClickListener(mCSFitnessRequestResultDialog);
                                buttonValue.setText(mValues.get(i));

                                int height = getHeight(mValues.get(i), mValues, mLinearLayout.getHeight());

                                if(height > 0) {
                                    buttonValue.setHeight(height);
                                }

                                if(i == 0) {
                                    buttonValue.requestFocus();
                                }

                                mLinearLayout.addView(resultView);
                            }
                        }
                    }
                }
        );
    }

    private int getHeight(String value, List<String> values, int parentHeight) {
        int height = 0;
        int valuesMax = 0;

        // Calculate the maximum
        for(String s : values) {
            try {
                int temp = Integer.valueOf(s);

                if(temp > valuesMax) {
                    valuesMax = temp;
                }
            } catch (Exception e) {
                float temp = Float.valueOf(s);

                if(temp > valuesMax) {
                    valuesMax = (int) temp;
                }
            }
        }

        try {
            int temp = Integer.valueOf(value);
            height = temp * parentHeight / valuesMax;
        } catch (Exception e) {
            float temp = Float.valueOf(value);
            if(valuesMax != 0) {
                height = ((int) temp) * parentHeight / valuesMax;
            }
        }

        return height;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;

        if(button != null) {
            for (ICSFitnessRequestResultDialogListener listener : mICSFitnessRequestResultDialogListenerList) {
                listener.onValueSelected(button.getText().toString());
            }
        }

        dismiss();
    }
}
