package com.technopark.ivankov.referee.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.technopark.ivankov.referee.R;
import com.vk.sdk.VKSdk;

public class LoginFragment extends android.support.v4.app.Fragment {

    private static final String TAG = LoginFragment.class.getSimpleName();

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        final Button btnLogin = (Button) v.findViewById(R.id.login);
        btnLogin.setText(R.string.btn_login);
        btnLogin.setOnClickListener(btnLoginClick);

        return v;
    }

    private View.OnClickListener btnLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            VKSdk.login(getActivity(), LoginActivity.vkScope);
        }
    };
}