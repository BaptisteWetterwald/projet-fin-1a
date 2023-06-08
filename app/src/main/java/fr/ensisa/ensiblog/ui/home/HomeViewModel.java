package fr.ensisa.ensiblog.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> mText1;
    private final MutableLiveData<String> mText2;
    public HomeViewModel() {
        mText1 = new MutableLiveData<>();
        mText1.setValue("Les cartes seront ajoutées ICI ");
        mText2 = new MutableLiveData<>();
        mText2.setValue("Congratulation !!!!!! Ceci est un second text ");
    }
    public LiveData<String> getText1() { return mText1;    }
    public LiveData<String> getText2() { return mText2;    }
}