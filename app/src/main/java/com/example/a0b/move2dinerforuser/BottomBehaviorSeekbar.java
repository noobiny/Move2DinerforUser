package com.example.a0b.move2dinerforuser;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class BottomBehaviorSeekbar extends BottomSheetDialog {

    private SeekBar seekBar;
    private ActivityMaps parent;
    TextView seekbar_text;
    String selected = null;

    public BottomBehaviorSeekbar(final ActivityMaps parent) {
        super(parent);
        this.parent = parent;
        View v = (View) View.inflate(getContext(), R.layout.btbehavior_seekbar, null);
        seekBar = (SeekBar) v.findViewById(R.id.seekbar_distance);
        seekbar_text = (TextView) v.findViewById(R.id.seekbar_text);
        final Integer[] distanceList_int = {300, 500, 1000, 3000, 5000};
        final String[] distanceList_str = {"300m", "500m", "1km", "3km", "5km"};

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selected = distanceList_str[progress];
                seekbar_text.setText(selected);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                selected = distanceList_str[progress];
                parent.spinnerdistance.setText(selected);
                parent.clearView();
                parent.settingMap(distanceList_int[progress]);
                parent.retrieveWithDistance(distanceList_int[progress]);
                dismiss();

            }
        });


        setContentView(v);
        configureBottomSheetBehavior(v);
    }

    private void configureBottomSheetBehavior(View contentView) {
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());

        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    //showing the different states
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            dismiss(); //if you want the modal to be dismissed when user drags the bottomsheet down
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }
}
