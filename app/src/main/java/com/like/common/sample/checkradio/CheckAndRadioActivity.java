package com.like.common.sample.checkradio;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.like.common.sample.R;
import com.like.common.sample.databinding.ActivityCheckAndRadioBinding;
import com.like.common.util.CheckManager;
import com.like.common.util.RadioManager;

public class CheckAndRadioActivity extends AppCompatActivity {
    private ActivityCheckAndRadioBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_check_and_radio);
        initRadio();
        initCheck();
    }

    private void initRadio() {
        RadioManager<Integer> radioManager = new RadioManager<>();
        radioManager.check(-1);
        mBinding.setSelectedId(radioManager.getCurChecked());
        mBinding.radio0.setOnClickListener(view -> radioManager.check(0));
        mBinding.radio1.setOnClickListener(view -> radioManager.check(1));
        mBinding.radio2.setOnClickListener(view -> radioManager.check(2));
        mBinding.radio3.setOnClickListener(view -> radioManager.check(3));
        mBinding.radio4.setOnClickListener(view -> radioManager.check(4));
        mBinding.radio5.setOnClickListener(view -> radioManager.check(5));
    }

    private void initCheck() {
        CheckManager<Integer> checkManager = new CheckManager<>();
        for (int i = 0; i < 6; i++) {
            checkManager.add(i);
        }
        mBinding.setIsChecked0(checkManager.get(0));
        mBinding.setIsChecked1(checkManager.get(1));
        mBinding.setIsChecked2(checkManager.get(2));
        mBinding.setIsChecked3(checkManager.get(3));
        mBinding.setIsChecked4(checkManager.get(4));
        mBinding.setIsChecked5(checkManager.get(5));
        mBinding.check0.setOnClickListener(view -> checkManager.check(0));
        mBinding.check1.setOnClickListener(view -> checkManager.check(1));
        mBinding.check2.setOnClickListener(view -> checkManager.check(2));
        mBinding.check3.setOnClickListener(view -> checkManager.check(3));
        mBinding.check4.setOnClickListener(view -> checkManager.check(4));
        mBinding.check5.setOnClickListener(view -> checkManager.check(5));
    }
}
