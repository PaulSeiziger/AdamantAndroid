package com.dremanovich.adamant_android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.mvp_view.CreateChatView;

import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class CreateChatPresenter extends MvpPresenter<CreateChatView>{
    private Router router;
    private ChatsInteractor interactor;
    private CompositeDisposable subscriptions;

    public CreateChatPresenter(Router router, ChatsInteractor interactor, CompositeDisposable subscriptions) {
        this.router = router;
        this.interactor = interactor;
        this.subscriptions = subscriptions;
    }

    public void onClickCreateNewChat(String address) {
        if (validate(address)){
            Chat chat = new Chat();
            chat.setCompanionId(address);

            interactor.addNewChat(chat);
            router.navigateTo(Screens.MESSAGES_SCREEN, chat);

        } else {
           getViewState().showError(R.string.wrong_address);
        }
    }

    private boolean validate(String address) {
        //TODO: Write address verification rules
        if (address == null) {return false;}
        if (!"U".equalsIgnoreCase(address.substring(0, 1))){return false;}
        if (address.length() < 2){return false;}

        return true;
    }
}