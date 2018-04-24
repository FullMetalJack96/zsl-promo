package pl.zslkrakow.zslpromo;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    /*
        Beacony:
        - 36146:42429   => Ice          => EE
        - 42913:31197   => Mint         => IT
        - 1956:15309    => Blueberry    => TELE
     */

    protected BeaconManager beaconManager;
    protected Region region;
    protected HashMap<String, Integer> beaconList = new HashMap<>();
    public Vibrator vibrator;
    public static int nearestPlace = 0;

    @Override
    protected void onStart() {
        beaconList.put("36146:42429", ContentType.EE);
        beaconList.put("42913:31197", ContentType.IT);
        beaconList.put("1956:15309", ContentType.TELE);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        beaconManager = new BeaconManager(this);
        region = new Region("ranged region", null, null, null);
        getSupportActionBar().setTitle(R.string.app_title);

        WebView viewMain = (WebView) findViewById(R.id.webView1);

        try {
            viewMain.loadUrl("file:///android_asset/www/mainPage.html");
        } catch (Exception npe) {
            Log.e("ZslPromo", npe.getMessage());
        }
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("zslpromo", "onResume dziala bijacz");
        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},10);
            }
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {

            @Override
            public void onServiceReady() {

                beaconManager.startRanging(region);
                beaconManager.setRangingListener(new BeaconManager.RangingListener() {
                    @Override
                    public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                        //Log.i("ZslPromo", list.size() + "");
                        if(list.size() > 0) {
//                            //Log.i("ZslPromo", list.toString());
                                Beacon nearest = (Beacon) list.get(0);
                                String beaconKey = String.format("%d:%d", nearest.getMajor(), nearest.getMinor());
//                            Log.i("ZslBeacon", beaconList.get(beaconKey) + " <- najbliższy beacon");
//                            Intent startIntent = new Intent(MainActivity.this, InfoActivity.class);
//                            startIntent.putExtra("content", beaconList.get(beaconKey));
//                            startActivity(startIntent);

                            //Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            //Ringtone ring = RingtoneManager.getRingtone(MainActivity.this, notificationSound);
                            if(nearestPlace != beaconList.get(beaconKey)) {
                                nearestPlace = beaconList.get(beaconKey);
                                final int nearestPlaceF = nearestPlace;
                                LinearLayout layoutMoreInfo = (LinearLayout) findViewById(R.id.layoutDisplay);
                                TextView fieldName = (TextView) findViewById(R.id.lblNearestField);
                                Button getMoreInfo = (Button) findViewById(R.id.btnMoreInfo);
                                getMoreInfo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                                        intent.putExtra("content", nearestPlaceF);
                                        startActivity(intent);
                                    }
                                });
                                fieldName.setText(ContentType.getFieldName(beaconList.get(beaconKey)));

                                if(layoutMoreInfo.getVisibility() == View.GONE) layoutMoreInfo.setVisibility(View.VISIBLE);
                                //ring.play();
                                vibrator.vibrate(500);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_DENIED ){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


}
