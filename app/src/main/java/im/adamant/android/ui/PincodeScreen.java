package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.loaderspack.loaders.ArcProgressLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.adapters.KeyPinAdapter;
import im.adamant.android.ui.custom_view.ItemSpaceDecoration;
import im.adamant.android.ui.custom_view.LTRGridLayoutManager;
import im.adamant.android.ui.custom_view.PinIndicatorLayout;
import im.adamant.android.ui.mvp_view.PinCodeView;
import im.adamant.android.ui.presenters.PincodePresenter;

import static android.view.View.OVER_SCROLL_NEVER;

public class PincodeScreen extends BaseActivity implements PinCodeView, KeyPinAdapter.PincodeListener {

    @Inject
    KeyPinAdapter keyPinAdapter;

    @Inject
    Provider<PincodePresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    PincodePresenter presenter;

    @ProvidePresenter
    public PincodePresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.activity_pincode_plv_keyboard) RecyclerView pinLockView;
    @BindView(R.id.activity_pincode_tv_suggestion) TextView suggestionView;
    @BindView(R.id.activity_pincode_tv_error) TextView errorView;
    @BindView(R.id.activity_pin_code_pb_progress) ArcProgressLoader progressView;
    @BindView(R.id.activity_pin_code_cl_keypadLayout) ConstraintLayout keypadLayoutView;
    @BindView(R.id.activity_pincode_cl_logoLayout) ConstraintLayout logoLayoutView;
    @BindView(R.id.activity_pincode_pil_indicator_dots) PinIndicatorLayout indicator;
    @BindView(R.id.activity_pin_code_btn_reset_or_cancel) MaterialButton cancelButtonView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_pin_code_screen;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        keyPinAdapter.setIndicator(indicator);
        keyPinAdapter.setPincodeListener(this);

        pinLockView.setAdapter(keyPinAdapter);
        pinLockView.setLayoutManager(new LTRGridLayoutManager(this, 3));
        int hSpace = (int)(getResources().getDisplayMetrics().density * 15);
        pinLockView.addItemDecoration(new ItemSpaceDecoration(hSpace, 0, 3, false));
        pinLockView.setOverScrollMode(OVER_SCROLL_NEVER);

        cancelButtonView.setPaintFlags(cancelButtonView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Intent intent = getIntent();

        if (intent == null){return;}
        Bundle extras = intent.getExtras();

        if (extras == null){return;}
        MODE mode = (MODE) extras.getSerializable(PinCodeView.ARG_MODE);

        if (mode == null){return;}
        presenter.setMode(mode);

    }

    @Override
    public void startProcess() {
        progressView.setVisibility(View.VISIBLE);
        Animation outAnimation = AnimationUtils.makeOutAnimation(this, false);
        keypadLayoutView.setAnimation(outAnimation);
        keypadLayoutView.setVisibility(View.GONE);
        cancelButtonView.setVisibility(View.GONE);
    }

    @Override
    public void stopProcess(boolean success) {
        if (!success) {
            Animation outAnimation = AnimationUtils.makeInAnimation(this, false);
            keypadLayoutView.setAnimation(outAnimation);
            keypadLayoutView.setVisibility(View.VISIBLE);
            cancelButtonView.setVisibility(View.VISIBLE);
        }
        progressView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setSuggestion(int resourceId) {
        suggestionView.setText(resourceId);
    }

//    @Override
//    public void shuffleKeyboard() {
//        keyPinAdapter.shuffle();
//    }

    @Override
    public void goToMain() {
        Intent intent = new Intent(this.getApplicationContext(), MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        finish();
    }

    @Override
    public void goToSplash() {
        Intent intent = new Intent(this.getApplicationContext(), SplashScreen.class);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void showError(int resourceId) {
//        errorView.setText(getString(resourceId));
        Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showWrongPin(int attemptsRemained) {
        Toast.makeText(this, getString(R.string.wrong_pincode,attemptsRemained), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCompletePin(CharSequence pincode) {
        presenter.onInputPincodeWasCompleted(pincode);
    }

    @Override
    public void onDropPin() {

    }

    @OnClick(R.id.activity_pin_code_btn_reset_or_cancel)
    public void onClickCancelButton() {
        presenter.onClickCancelButton();
    }

    @Override
    public void showRepeatableError(int resourceId, int secondsLeft) {
        String errorPattern = getString(resourceId);
        String formattedError = String.format(Locale.ENGLISH, errorPattern, secondsLeft);
//        errorView.setText(formattedError);
        Toast.makeText(this, formattedError, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setCancelButtonText(int resourceId) {
        cancelButtonView.setText(resourceId);
    }

    @Override
    public void clearError() {
        errorView.setText("");
    }


}
