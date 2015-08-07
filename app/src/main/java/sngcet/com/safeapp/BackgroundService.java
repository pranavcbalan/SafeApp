package sngcet.com.safeapp;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import root.gast.speech.activation.SpeechActivationListener;
import root.gast.speech.activation.WordActivator;




public class BackgroundService extends Service implements LocationListener {
    public static final int LOCATION_REFRESH_TIME = 30000;
    private WordActivator wordActivator;
    private Context context;
    private SpeechActivationListener speechActivationListener;
    private SqlHelper sqlHelper;
    private LocationManager mLocationManager;
    private Location cLocation;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String TAG = "Location";
    private LocationManager locationManager;
    public static SmsManager smsManager;


    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sqlHelper = new SqlHelper(this);
        smsManager = SmsManager.getDefault();
        InitLocation();
    }


    public void InitLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria locationCritera = new Criteria();

        // Getting the name of the best provider
        String provider =
                locationManager.getBestProvider(locationCritera, true);
        Log.i(TAG, "Best Location Provider-->" + provider);
        // Getting Current Location From GPS
        Location location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 0, 0, this);
        Log.i(TAG, "Location-->" + location);
        if (location != null) {
            cLocation = location;
        } else {
            Log.i(TAG, "Location is Null");
        }


    }

    @SuppressWarnings("deprecation")
    private  void takePhoto(final Context context) {
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                showMessage("Surface created");



                Camera camera = null;

                try {
                    camera = Camera.open();
                    showMessage("Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    camera.startPreview();
                    showMessage("Started preview");

                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {


                            SavePhotoTask savePhotoTask = new SavePhotoTask();
                            savePhotoTask.execute(data);

//                            Log.i("img", data.toString());
                            camera.stopPreview();
                            camera.setPreviewCallback(null);
                            camera.release();
                            camera = null;
                        }
                    });
                } catch (Exception e) {
                    if (camera != null) {
                        camera.stopPreview();
                        camera.setPreviewCallback(null);
                        camera.release();
                    }
                    camera = null;
//                    throw new RuntimeException(e);
                }
            }

            class SavePhotoTask extends AsyncTask<byte[], String, String> {
                @Override
                protected String doInBackground(byte[]... jpeg) {
                    File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpeg");

                    if (photo.exists()) {
                        photo.delete();
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(photo.getPath());

                        fos.write(jpeg[0]);
                        fos.close();
                    } catch (java.io.IOException e) {
                        Log.e("PictureDemo", "Exception in photoCallback", e);
                    }

                    String result="";
                    if (cLocation != null) {
                        result=cLocation.getAltitude()+" "+cLocation.getLongitude()+"\n";

                        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> list = null;
                        try {
                            list = geoCoder.getFromLocation(cLocation
                                    .getLatitude(), cLocation.getLongitude(), 1);

                            if (list != null & list.size() > 0) {
                                Address address = list.get(0);
                               result += address.getLocality() + " " + address.getAddressLine(0) + " " + address.getAddressLine(0) + " " + address.getCountryName() + " " + address.getPostalCode();
                                Log.i("Location name :::::", result);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    for (String num : sqlHelper.getAllCotacts()) {
                        Log.i("num", num);
                        smsManager.sendTextMessage(num, null, result, null, null);
                    }


//                    Intent mmsIntent = new Intent(Intent.ACTION_SEND);
////                    mmsIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
//                    mmsIntent.putExtra("sms_body", result);
//                    mmsIntent.putExtra("address", "121");
//                    mmsIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpeg")));
//                    mmsIntent.setType("image/jpeg");
//                    mmsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(mmsIntent);

                    return (null);
                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                }
            }


            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
    }

    private static void showMessage(String message) {
        Log.i("Camera", message);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("<<TEST>>", "<<<<<<<destroy><><>>>>>>>>>>>>>>>><<<<<<<<><><<>");
        if (wordActivator != null) {
            try {
                wordActivator.stop();
                wordActivator=null;
            } catch (Exception e) {
            }
        }
        if (locationManager != null)
            locationManager.removeUpdates(this);
        sqlHelper.close();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("<<TEST>>", "<<<<<<<start><><>>>>>>>>>>>>>>>><<<<<<<<><><<>");


        speechActivationListener = new SpeechActivationListener() {
            @Override
            public void activated(boolean success) {
                try {
                    Log.i("Location", cLocation.getLatitude() + ", " + cLocation.getLongitude());
                } catch (Exception e) {
                    Log.w("Error", e.toString());
                }
                Toast.makeText(getApplicationContext()
                        , "Matched", Toast.LENGTH_LONG).show();

                takePhoto(getApplicationContext());

            }
        };

        if (wordActivator != null) {
            try {
                wordActivator.detectActivation();
            } catch (Exception e) {
            }
        }else {
            wordActivator = new WordActivator(this, speechActivationListener, "hi", "hello", "any", "da");
            wordActivator.detectActivation();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(final Location location) {
        cLocation = location;
        Log.i("Location", cLocation.getLatitude() + ", " + cLocation.getLongitude());


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("Status", "jskjdskjdksjdksjdskdjksjdkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("Enabled", "jskjdskjdksjdksjdskdjksjdkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("Disabled", "jskjdskjdksjdksjdskdjksjdkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
    }
}
