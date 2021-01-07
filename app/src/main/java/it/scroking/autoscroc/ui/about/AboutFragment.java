package it.scroking.autoscroc.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.scroking.autoscroc.R;

public class AboutFragment extends Fragment {

    WebSettings webSettings;
    WebView webView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about, container, false);
        this.webView = root.findViewById(R.id.webViewScroc);

        this.webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        webView.loadUrl("http://scroking.ddns.net/scroKING-TravelAgency/about.htm");




        return root;
    }
}