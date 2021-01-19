package com.example.admin.printqr.ui.Qr_Generator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QRGeneratorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QRGeneratorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}