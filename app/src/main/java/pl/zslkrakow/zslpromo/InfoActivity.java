package pl.zslkrakow.zslpromo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class InfoActivity extends AppCompatActivity {


    public static final String assetDir = "file:///android_asset/www/";
    public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Log.i("ZslInfo", "Mamy dane, wybrany wpis - " + extras.getInt("content", 1));
        }

        int nearestContent = extras.getInt("content", 1);
        switch(nearestContent) {
            case ContentType.EE:
                setTheme(R.style.AppThemeGreen);
                break;
            case ContentType.IT:
                setTheme(R.style.AppThemeTeal);
                break;
            case ContentType.TELE:
                setTheme(R.style.AppThemeDeepPurple);
                break;
        }
        setContentView(R.layout.activity_info);

        WebView viewMain = (WebView) findViewById(R.id.webViewInfo);
        switch(nearestContent) {
            case ContentType.EE:
                url = assetDir + "elektronika.html";
                getSupportActionBar().setTitle("Elektronika");
                break;
            case ContentType.IT:
                url = assetDir + "informatyka.html";
                getSupportActionBar().setTitle("Informatyka");
                break;
            case ContentType.TELE:
                url = assetDir + "teleinformatyka.html";
                getSupportActionBar().setTitle("Teleinformatyka/Telekomunikacja");
                break;
        }
        try {
            viewMain.loadUrl(url);
        } catch (Exception npe) {
            Log.e("ZslPromo", npe.getMessage());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


}
