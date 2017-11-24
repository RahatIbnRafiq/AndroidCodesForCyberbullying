package com.bamboo.bullyalert.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;

/**
 * Created by Rahat Ibn Rafiq on 10/11/2017.
 */

public class LoginDialogFragment extends DialogFragment {
    private AutoCompleteTextView mUserName;
    private EditText mPassword;
    private Button mButton;
    private WebView webView;
    private String instagramAuthCode = null;
    Context mContext;

    public LoginDialogFragment()
    {
    }

    public static LoginDialogFragment newInstance(String title) {
        LoginDialogFragment frag = new LoginDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        mContext = view.getContext();

        String title = getArguments().getString("title").toLowerCase();
        if(title.contains("instagram"))
        {
            Log.i(UtilityVariables.tag,"onCreateView: trying to initialize instagram login dialog");
            InstagramLoginDialog(view,mContext);
        }
        else
        {
            webView.setVisibility(View.INVISIBLE);
        }
        return view;
    }




    private void InstagramLoginDialog(View view,Context context)
    {
        this.webView = (WebView)view.findViewById(R.id.webView_login);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        clearCookies(context);

        String mAuthUrl = UtilityVariables.INSTAGRAM_AUTH_URL + "?client_id=" + UtilityVariables.INSTAGRAM_CLIENT_ID + "&redirect_uri="
                + UtilityVariables.INSTAGRAM_CALLBACK_URL + "&response_type=code&display=touch&scope=likes+comments+relationships+public_content";
        this.webView.loadUrl(mAuthUrl);

        this.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.startsWith(UtilityVariables.INSTAGRAM_CALLBACK_URL))
                {
                    Log.i(UtilityVariables.tag,"code is found 2: "+url);
                    String code = (url != null ? url.split("=") : new String[0])[1];
                    instagramAuthCode = code;
                    sendBackResult();

                }
            }
        });

    }

    // Defines the listener interface
    public interface LoginDialogFragmentListener {
        void onFinishLoginDialog(String code);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        LoginDialogFragmentListener listener = (LoginDialogFragmentListener) getTargetFragment();
        listener.onFinishLoginDialog(instagramAuthCode);
        dismiss();
    }

    private static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }








}
