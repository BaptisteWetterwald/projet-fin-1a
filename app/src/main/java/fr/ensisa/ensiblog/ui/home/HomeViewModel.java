package fr.ensisa.ensiblog.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Les cartes seront ajoutées ICI ");
    }
    public LiveData<String> getText() {
        return mText;
    }
}