package com.ccbfm.music.player.ui.fragment;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.model.ScanningModel;
import com.ccbfm.music.player.databinding.FragmentScanningBinding;

public class ScanningFragment extends BaseFragment<FragmentScanningBinding> {

    private ScanningModel mScanningModel;
    @Override
    protected void initView(FragmentScanningBinding binding) {
        ScanningModel model = new ScanningModel(this);
        binding.setScanningModel(model);
        mScanningModel = model;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scanning;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mScanningModel != null){
            mScanningModel.onActivityResult(requestCode, resultCode, data);
        }
    }
}
