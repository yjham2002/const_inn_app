package kr.co.picklecode.const_inn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import bases.BaseActivity;
import comm.model.UserModel;

public class IntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(UserModel.getFromPreference() != null) Log.e("userInfo", UserModel.getFromPreference().toString());
    }
}
