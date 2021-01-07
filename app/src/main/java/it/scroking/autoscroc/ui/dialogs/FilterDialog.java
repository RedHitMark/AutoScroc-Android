package it.scroking.autoscroc.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.scroking.autoscroc.RentSaleDatabaseHelper;
import it.scroking.autoscroc.R;

public class FilterDialog extends DialogFragment {

    private RentSaleDatabaseHelper rentSaleDatabaseHelper;
    FilterListener listener;

    private Button searchButton;

    private SeekBar kmSeekBar;
    private TextView kmCounter;

    private Button showGenParamasBtn;
    private View paramGenView;
    private NumberPicker npYear;
    private NumberPicker npPrice;
    private NumberPicker npHp;
    private NumberPicker npFiuel;

    private Button showAdvParamsBtn;
    private View paramAdvView;
    private NumberPicker npDoors;
    private NumberPicker npCarType;
    private NumberPicker npCylinderType;
    private NumberPicker npCylinderNum;

    private Button resetAll;
    private Button cancelDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        this.rentSaleDatabaseHelper = new RentSaleDatabaseHelper(getContext());
        this.searchButton = view.findViewById(R.id.searchButton);

        this.kmCounter = view.findViewById(R.id.kmCounter);
        this.kmSeekBar = view.findViewById(R.id.kmSeekBar);

        this.showGenParamasBtn = view.findViewById(R.id.showParamGenralBtn);
        this.paramGenView = view.findViewById(R.id.generalParams);
        this.npYear = view.findViewById(R.id.YyearPicker);
        this.npPrice = view.findViewById(R.id.pricePicker);
        this.npHp = view.findViewById(R.id.hpPiker);
        this.npFiuel = view.findViewById(R.id.fiuelType);


        this.showAdvParamsBtn = view.findViewById(R.id.showParamAdvBtn);
        this.paramAdvView = view.findViewById(R.id.advParams);
        this.npDoors = view.findViewById(R.id.doorsPicker);
        this.npCarType = view.findViewById(R.id.CarTypePicker);
        this.npCylinderType = view.findViewById(R.id.cylinderTypePicker);
        this.npCylinderNum = view.findViewById(R.id.cylinderNumberPicker);

        this.resetAll = view.findViewById(R.id.resetBtn);
        this.cancelDialog = view.findViewById(R.id.closeBtn);


        seekBarKmInit();
        npYearInit();
        npPriceInit();
        npHpInit();
        npFiuelInit();
        showParam(paramGenView, showGenParamasBtn);


        npDoorsInit();
        npCarTypeInit();
        npCylinderTypeInit();
        npCylinderNumInit();
        showParam(paramAdvView, showAdvParamsBtn);

        searchRent();
        restAlNp();
        dismissfragment();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (getShowsDialog()) {
            DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();

            int dialogWidth = (int) Math.min(metrics.widthPixels * 0.8, metrics.heightPixels * 0.8);

            getDialog().getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }


    public void showParam(View view, Button button) {
        view.setVisibility(View.GONE);
        button.setOnClickListener(v -> {
            if (view.getVisibility() == View.GONE)
                view.setVisibility(View.VISIBLE);
            else
                view.setVisibility(View.GONE);
        });

    }


    //KM chooser

    public void seekBarKmInit() {

        kmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kmCounter.setText("" + (progress * 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //General Params

    public void npYearInit() {


        npYear.setMinValue(1900);
        npYear.setMaxValue(2020);

        NumberPicker.OnValueChangeListener onValueChangeListener =
                (numberPicker, i, i1) -> {

                };

        npYear.setOnValueChangedListener(onValueChangeListener);
    }

    public void npPriceInit() {

        npPrice.setMinValue(500);
        npPrice.setMaxValue(100000);

        NumberPicker.OnValueChangeListener onValueChangeListener =
                (numberPicker, i, i1) -> {

                };

        npPrice.setOnValueChangedListener(onValueChangeListener);
    }

    public void npHpInit() {
        npHp.setMinValue(0);
        npHp.setMaxValue(100);

        NumberPicker.Formatter formatter = value -> {
            int temp = value * 10;
            return "" + temp;
        };

        npHp.setFormatter(formatter);

        NumberPicker.OnValueChangeListener onValueChangeListener =
                (numberPicker, i, i1) -> {

                };

        npHp.setOnValueChangedListener(onValueChangeListener);


    }

    public void npFiuelInit() {
        final String genderOfFiuel[] = {"", "aardgas", "benzine", "diesel", "LPG", "petrol",};

        npFiuel.setMinValue(0);
        npFiuel.setMaxValue(genderOfFiuel.length - 1);
        npFiuel.setDisplayedValues(genderOfFiuel);
        npFiuel.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker.OnValueChangeListener myValChangedListener = (picker, oldVal, newVal) -> {

        };
        npFiuel.setOnValueChangedListener(myValChangedListener);

    }

    //Advanced Params

    public void npDoorsInit() {
        final String gendersOfDoors[] = {"0", "2", "3", "4", "5"};

        npDoors.setMinValue(0);
        npDoors.setMaxValue(gendersOfDoors.length - 1);
        npDoors.setDisplayedValues(gendersOfDoors);
        npDoors.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker.OnValueChangeListener myValChangedListener = (picker, oldVal, newVal) -> {

        };
        npDoors.setOnValueChangedListener(myValChangedListener);

    }

    public void npCarTypeInit() {

        final String gendersOfCars[] = {"", "cabriolet", "hatchback", "sedan", "stationwagon", "coupé", "suv/crossover", "mpv", "bus", "bestelwagen", "van", "pick-up"};

        npCarType.setMinValue(0);
        npCarType.setMaxValue(gendersOfCars.length - 1);
        npCarType.setDisplayedValues(gendersOfCars);
        npCarType.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker.OnValueChangeListener myValChangedListener = (picker, oldVal, newVal) -> {

        };
        npCarType.setOnValueChangedListener(myValChangedListener);
    }

    public void npCylinderTypeInit() {

        final String genderOfCylinder[] = {"", "boxer", "in line", "V", "V-vorm", "W", "lijn", "W-vorm"};

        npCylinderType.setMinValue(0);
        npCylinderType.setMaxValue(genderOfCylinder.length - 1);
        npCylinderType.setDisplayedValues(genderOfCylinder);
        npCylinderType.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker.OnValueChangeListener myValChangedListener = (picker, oldVal, newVal) -> {

        };
        npCylinderType.setOnValueChangedListener(myValChangedListener);
    }

    public void npCylinderNumInit() {

        final String gendersOfNum[] = {"0", "3", "4", "5", "6", "8", "10", "12"};

        npCylinderNum.setMinValue(0);
        npCylinderNum.setMaxValue(gendersOfNum.length - 1);
        npCylinderNum.setDisplayedValues(gendersOfNum);
        npCylinderNum.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker.OnValueChangeListener myValChangedListener = (picker, oldVal, newVal) -> {

        };
        npCylinderNum.setOnValueChangedListener(myValChangedListener);


    }

    public void searchRent() {

        searchButton.setOnClickListener(v -> {
            listener.onFilterClick(
                    kmSeekBar.getProgress(),
                    npYear.getValue(),
                    npPrice.getValue(),
                    npHp.getValue(),
                    npFiuel.getDisplayedValues()[npFiuel.getValue()],
                    Integer.parseInt(npDoors.getDisplayedValues()[npDoors.getValue()].toString()),
                    npCarType.getDisplayedValues()[npCarType.getValue()],
                    npCylinderType.getDisplayedValues()[npCylinderType.getValue()],
                    Integer.parseInt(npCylinderNum.getDisplayedValues()[npCylinderNum.getValue()].toString())
            );
        });

    }

    public void restAlNp() {
        resetAll.setOnClickListener(v -> {
            kmSeekBar.setProgress(0);
            npYear.setValue(1900);
            npPrice.setValue(500);
            npHp.setValue(0);
            npFiuel.setValue(0);
            npDoors.setValue(0);
            npCarType.setValue(0);
            npCylinderNum.setValue(0);
        });

    }

    public void dismissfragment() {


        cancelDialog.setOnClickListener(v -> {
            dismiss();
        });
    }


    // Override the Fragment.onAttach() method to instantiate the DialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FilterListener) getTargetFragment();
    }

    public interface FilterListener {
        void onFilterClick(int km, int matriculationYear, double price, int hp, String engineType, int doors, String carType, String cylindersType, int numCylinders);
    }


}

