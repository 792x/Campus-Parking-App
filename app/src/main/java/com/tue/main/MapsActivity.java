package com.tue.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;

import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mert.testproj2.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.CameraPosition;
import com.tue.errorhandling.ErrorHandler;
import com.tue.parking.Building;
import com.tue.parking.NearestParkingDialog;
import com.tue.parking.ParkingArea;
import com.tue.parking.PersistentSearchAdapter;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.OnSheetDismissedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.melnykov.fab.FloatingActionButton;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.tue.service.BackgroundService;
import com.tue.tools.AndroidTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private GoogleMap mMap;
    BottomSheetLayout bottomSheet;
    View card;

    ParkingArea current_area;
    SearchBox search;
    String remote_address = "http://paulwijsen.com:8080";

    HashMap<Integer, ParkingArea> parking_map = new HashMap<>();
    HashMap<Integer, Building> building_map = new HashMap<>();
    ErrorHandler errorHandler = new ErrorHandler(this);

    private FloatingActionButton fabSearch, fabNavigate, fabSettings, fabPark, fabUnPark, fabShowList;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    public LatLng latLng2;
    //variable to store teh parked location
    public LatLng ParkedLatLng;
    public Calendar calArrive, calLeave, calNow;
    public Marker parkedMarker;
    public Timer t;
    public TextView bigText, smallText;
    private int gps = 0;
    public LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        card = LayoutInflater.from(getApplicationContext()).inflate(R.layout.my_sheet_layout, bottomSheet, false);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean subscriptionOwner = sharedPrefs.getBoolean("subscriptionOwner", false);
        boolean bAppUpdates = sharedPrefs.getBoolean("applicationUpdates", false);
        boolean proximityNotification = sharedPrefs.getBoolean("proximityNotification", true);
        String theme = sharedPrefs.getString("Theme", "Light");
        String language = sharedPrefs.getString("Language", "English");
        launchTestService();
//        boolean subscriptionOwner = sharedPrefs.getBoolean("subscriptionOwner", false);
//        boolean bAppUpdates = sharedPrefs.getBoolean("applicationUpdates",false);
//        boolean proximityNotification = sharedPrefs.getBoolean("proximityNotification", true);
//        String theme = sharedPrefs.getString("theme", "Light");
        bigText = (TextView) findViewById(R.id.bigText);
        smallText = (TextView) findViewById(R.id.smallText);

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        if (language.equals("English")) {
            Log.v(String.valueOf(this), "starting with english");
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources()
                    .updateConfiguration(
                            config,
                            getBaseContext().getResources()
                                    .getDisplayMetrics());
        } else if (language.equals("Dutch")) {
            Log.v(String.valueOf(this), "starting with dutch");
            Locale locale = new Locale("nl");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources()
                    .updateConfiguration(
                            config,
                            getBaseContext().getResources()
                                    .getDisplayMetrics());
        }

        if (subscriptionOwner == true) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bigText.setVisibility(View.INVISIBLE);
                    smallText.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            if (subscriptionOwner == false) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bigText.setVisibility(View.VISIBLE);
                        smallText.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomSheet = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        bottomSheet.setInterceptContentTouch(false);
        bottomSheet.setShouldDimContentView(false);


        search = (SearchBox) findViewById(R.id.searchbox);

        search.enableVoiceRecognition(this);


        //     search.setLogoText("Find building..");
        //    search.setLogoTextColor(R.color.divider_gray);
        String hint = getString(R.string.search_hint_string);
        search.setHint(hint);
        search.setMenuListener(new SearchBox.MenuListener() {

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                //       Toast.makeText(MapsActivity.this, "Menu click", Toast.LENGTH_LONG).show();


            }

        });


        search.setSearchListener(new PersistentSearchAdapter() {

            @Override
            public void onSearch(String searchTerm) {
                moveAndShowCard(searchTerm);
            }

            @Override
            public void onResultClick(SearchResult result) {
                moveAndShowCard(result.title);
            }


        });


        //buttons
        fabSearch = (FloatingActionButton) findViewById(R.id.searchButton);
        fabSettings = (FloatingActionButton) findViewById(R.id.settings);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //       Toast.makeText(MapsActivity.this, "Clicked search button", Toast.LENGTH_LONG).show();

                clearAllHighlightedBuildings();
                bottomSheet.dismissSheet();

                search.setVisibility(View.VISIBLE);
                fabSearch.setVisibility(View.INVISIBLE);
                fabSettings.setVisibility(View.INVISIBLE);
                fabPark.setVisibility(View.INVISIBLE);
                if (!search.getSearchOpen()) {
                    search.toggleSearch();
                }


            }
        });


        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MapsActivity.this, MyPreferencesActivity.class);
                startActivity(i);
            }
        });


        fabNavigate = (FloatingActionButton) findViewById(R.id.navigate);
        fabShowList = (FloatingActionButton) findViewById(R.id.show_list);

        fabNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = current_area.getAreas(0).get(0); // TODO, make entrance points for each parking area
                LatLng TUE = new LatLng(51.448464, 5.483844);

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        fabPark = (FloatingActionButton) findViewById(R.id.save);

        fabPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean subscriptionOwner = sharedPrefs.getBoolean("subscriptionOwner", false);

                calArrive = Calendar.getInstance();

                if (subscriptionOwner == true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bigText.setVisibility(View.INVISIBLE);
                            smallText.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    if (subscriptionOwner == false) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bigText.setVisibility(View.VISIBLE);
                                smallText.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
                //Declare the timer
                t = new Timer();
                //Set the schedule function and rate
                t.scheduleAtFixedRate(new TimerTask() {
                                          @Override
                                          public void run() {
                                              //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                              calNow = Calendar.getInstance();
                                              final long minutes = getTimeDif(calArrive, calNow);
                                              final double totalCost = (minutes / 15) * 0.50;

                                              if (totalCost >= 7.50) {
                                                  runOnUiThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          bigText.setText("€ " + "7.50");
                                                      }
                                                  });

                                              } else {
                                                  runOnUiThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          bigText.setText("€ " + String.format("%.2f", totalCost + 0.50));
                                                      }
                                                  });

                                              }
                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      smallText.setText(minutes + " sec");
                                                  }
                                              });


                                          }

                                      },
                        //Set how long before to start calling the TimerTask (in milliseconds)
                        0,
                        //Set the amount of time between each execution (in milliseconds)
                        1000);


                MarkerOptions options = new MarkerOptions()
                        .position(latLng2)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_caricon))
                        .title("");
                ParkedLatLng = latLng2;
                parkedMarker = mMap.addMarker(options);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng2));
                fabPark.setVisibility(View.INVISIBLE);
                fabUnPark.setVisibility(View.VISIBLE);
            }
        });

        fabUnPark = (FloatingActionButton) findViewById(R.id.unsave);
        fabUnPark.setVisibility(View.INVISIBLE);
        fabUnPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle(getString(R.string.alert2_title))
                        .setMessage(getString(R.string.alert2_content))
                        .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                calLeave = Calendar.getInstance();
                                // Toast.makeText(MapsActivity.this, (getTimeDif(calArrive, calLeave)+""), Toast.LENGTH_SHORT).show();

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLngBounds(
                                        new LatLng(51.445441, 5.478473), new LatLng(51.453679, 5.501454)).getCenter(), 16));
                                parkedMarker.remove();

                                t.cancel();


                                fabPark.setVisibility(View.VISIBLE);
                                fabUnPark.setVisibility(View.INVISIBLE);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        smallText.setText("");
                                        bigText.setText("");
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_directions_car_black_24dp)
                        .show();


            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        checkGps();

    }


    public TreeSet<Pair<Float, ParkingArea>> findClosestParking(Building b) {
        Log.i("SEARCH", "Search called.");


        LatLngBounds.Builder b_builder = LatLngBounds.builder();


        for (LatLng lats : b.getArea().get(0)) {
            b_builder.include(lats);
        }
        LatLng origin = b_builder.build().getCenter();
        Location ori = new Location("origin");
        ori.setLatitude(origin.latitude);
        ori.setLongitude(origin.longitude);

        TreeSet<Pair<Float, ParkingArea>> ret_set = new TreeSet<>(new Comparator<Pair<Float, ParkingArea>>() {
            @Override
            public int compare(Pair<Float, ParkingArea> lhs, Pair<Float, ParkingArea> rhs) {
                return (int) (lhs.first - rhs.first) * 1000; // not sure if we want 1000 , but just for accuracy
            }
        });

        for (ParkingArea pa : parking_map.values()) {

            LatLngBounds.Builder pa_builder = LatLngBounds.builder();


            for (LatLng lats : pa.getAreas().get(0)) {
                pa_builder.include(lats);
            }
            LatLng remote = pa_builder.build().getCenter();
            Location rem = new Location("remote");
            rem.setLatitude(remote.latitude);
            rem.setLongitude(remote.longitude);
            ret_set.add(new Pair<>(ori.distanceTo(rem), pa));
        }

        for (Pair<Float, ParkingArea> pairs : ret_set) {
            Log.i("SEARCH", "[" + pairs.first + "] " + pairs.second.getName());
        }
        return ret_set;

    }


    public void clearAllHighlightedBuildings() {

        for (Building b : building_map.values()) {

            for (Polygon p : b.getPolygons()) {
                p.remove();
            }
            b.getPolygons().clear();
        }
    }

    void moveToPark(final ParkingArea pa) {
        bottomSheet.dismissSheet();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(pa.getAreas(0).get(0)));
        Log.i("json", "Clicked_2");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                current_area = pa;
                ((TextView) card.findViewById(R.id.card_title)).setText(pa.getName());
                ((TextView) card.findViewById(R.id.card_spots)).setText(getString(R.string.free_slots_string) + (pa.getMaxsize() - pa.getCurrentLoad()) + "/" + pa.getMaxsize());
                if (pa.getName().equals("Simon Steveninplein")) {
                    ((ImageView) card.findViewById(R.id.card_pic)).setImageResource(R.mipmap.ic_simon);
                } else {
                    ((ImageView) card.findViewById(R.id.card_pic)).setImageResource(R.mipmap.ic_not_available);
                }
                bottomSheet.showWithSheetView(card);
                bottomSheet.expandSheet();
                //THIS IS PROBABLY NOT AN IDEAL PLACE FOR THIS, but cannot do it onCreate as a new sheet is created each time you press something.
                bottomSheet.addOnSheetDismissedListener(new OnSheetDismissedListener() {
                    @Override
                    public void onDismissed(BottomSheetLayout bottomSheetLayout) {
                        fabNavigate.setVisibility(View.INVISIBLE);
                    }
                });

                //getMaxSheetTranslation() only gives right value after it is shown fully, that is why delay of 500 ms
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fabNavigate.setVisibility(View.VISIBLE);
                        fabNavigate.setY(bottomSheet.getHeight() - bottomSheet.getMaxSheetTranslation() - fabNavigate.getPaddingTop() * 2);
                    }
                }, 500);


            }
        }, 400);

    }

    void moveAndShowCard(String term) {

        clearAllHighlightedBuildings();
        bottomSheet.dismissSheet();

        for (final Building b : building_map.values()) {
            if (b.getName().equals(term)) {
                search.setVisibility(View.INVISIBLE);
                fabSearch.setVisibility(View.VISIBLE);
                fabSettings.setVisibility(View.VISIBLE);
                fabPark.setVisibility(View.INVISIBLE);

                ((TextView) card.findViewById(R.id.card_title)).setText(b.getName());
                ((TextView) card.findViewById(R.id.card_spots)).setText(b.getDescription());
                if (b.getName().equals("Metaforum")) {
                    ((ImageView) card.findViewById(R.id.card_pic)).setImageResource(R.mipmap.ic_metaforum);
                } else {
                    ((ImageView) card.findViewById(R.id.card_pic)).setImageResource(R.mipmap.ic_not_available);
                }

                bottomSheet.showWithSheetView(card);
                bottomSheet.expandSheet();

                bottomSheet.addOnSheetDismissedListener(new OnSheetDismissedListener() {
                    @Override
                    public void onDismissed(BottomSheetLayout bottomSheetLayout) {
                        fabShowList.setVisibility(View.INVISIBLE);
                        fabPark.setVisibility(View.VISIBLE);
                    }
                });

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fabShowList.setVisibility(View.VISIBLE);
                        fabShowList.setY(bottomSheet.getHeight() - bottomSheet.getMaxSheetTranslation() - fabNavigate.getPaddingTop() * 2);
                    }
                }, 500);

                fabShowList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TreeSet<Pair<Float, ParkingArea>> set = findClosestParking(b);

                        NearestParkingDialog dialog = new NearestParkingDialog();
                        dialog.setData(set);
                        dialog.setHandler(new com.tue.tools.Handler<ParkingArea>() {
                            @Override
                            public void handle(ParkingArea... k) {
                                moveToPark(k[0]);
                            }
                        });

                        dialog.show(getFragmentManager(), "nearest_parking");

                    }
                });


                for (PolygonOptions polo : b.createPolygonOptions()) {
                    Polygon pol = mMap.addPolygon(polo);
                    b.register(pol);
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (LatLng lats : pol.getPoints()) {
                        builder.include(lats);
                    }
                    //       mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(builder.build().getCenter()));

//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(b.getArea().get(0).get(0))); // arbritary point really..

                }

                break;
            }
        }
    }

    // functie om verschil in tijd te berekenen UNTESTED
    public static long getTimeDif(Calendar cal1, Calendar cal2) {
        long arriveTime = cal1.getTimeInMillis();
        long leaveTime = cal2.getTimeInMillis();
        Log.i(TAG, "leave" + arriveTime);
        Log.i(TAG, "arrive" + leaveTime);
        long diff = leaveTime - arriveTime;
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);

        //change return to diff Minutes for actual functionality, its in seconds for test purposes
        return diffSeconds;
    }

    // Launching the service
    public void launchTestService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, BackgroundService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);
    }

    public void onStartService(View v) {
        Intent i = new Intent(this, BackgroundService.class);
        i.putExtra("foo", "bar");
        startService(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(BackgroundService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast

        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    // Define the callback for what to do when data is received
    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                String resultValue = intent.getStringExtra("resultValue");
                // Toast.makeText(MapsActivity.this, resultValue + " SERVICE STARTED", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            //updte to ask for permissions
            Toast.makeText(MapsActivity.this, "Please enable your location services.", Toast.LENGTH_LONG).show();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0 != null && arg0.getTitle().equals(parkedMarker.getTitle().toString()))
                    ; // if marker  source is clicked
                //Toast.makeText(MapsActivity.this, arg0.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle(getString(R.string.alert_title))
                        .setMessage(getString(R.string.alert_content))
                        .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLng2.latitude + "," + latLng2.longitude + "&mode=d");
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        })
                        .setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_directions_car_black_24dp)
                        .show();


                return true;
            }

        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                // cameraPosition.target
                LatLngBounds area = LatLngBounds.builder().include(new LatLng(51.451272, 5.482371))
                        .include(new LatLng(51.445387, 5.478938))
                        .include(new LatLng(51.444639, 5.49885))
                        .include(new LatLng(51.444639, 5.482371)).build();
//                Log.i("BOUNDS", "" + area.contains(cameraPosition.target));

                LatLng target = cameraPosition.target;
                LatLng new_target = new LatLng(target.latitude, target.longitude);
                if (!area.contains(cameraPosition.target)) {

                    if (area.southwest.latitude > target.latitude) {
                        new_target = new LatLng(area.southwest.latitude, new_target.longitude);
                    } else if (area.northeast.latitude < target.latitude) {
                        new_target = new LatLng(area.northeast.latitude, new_target.longitude);
                    }

                    if (area.southwest.longitude > target.longitude) {
                        new_target = new LatLng(new_target.latitude, area.southwest.longitude);
                    } else if (area.northeast.longitude < target.longitude) {
                        new_target = new LatLng(new_target.latitude, area.northeast.longitude);
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new_target));
                }


            }
        });


        // Add a marker in Sydney and move the camera
        //  LatLng TUE = new LatLng(51.4486098, 5.4885261);

        LatLng TUE = new LatLng(51.448464, 5.483844);


        //    mMap.addMarker(new MarkerOptions().position(TUE).title("TUe parking"));
        //    mMap.moveCamera(CameraUpdateFactory.newLatLng(TUE));
        //    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TUE, 16));
        Log.i("dbg", mMap.getMaxZoomLevel() + " vs " + mMap.getMinZoomLevel());


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLngBounds(
                new LatLng(51.445441, 5.478473), new LatLng(51.453679, 5.501454)).getCenter(), 16));


        ParkingAreaTask.execute();
        BuildingTask.execute(); // requires server version 3

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bottomSheet.dismissSheet();


                search.setVisibility(View.INVISIBLE);
                fabSearch.setVisibility(View.VISIBLE);
                fabSettings.setVisibility(View.VISIBLE);
                fabPark.setVisibility(View.VISIBLE);
            }
        });


        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(final Polygon polygon) {

                for (ParkingArea pa : parking_map.values()) {
                    for (ArrayList<LatLng> arr : pa.getAreas()) {
                        if (polygon.getPoints().containsAll(arr)) {
                            moveToPark(pa);
                            break;
                        }
                    }
                }

            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        latLng2 = new LatLng(currentLatitude, currentLongitude);


    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }


    final AndroidTask ParkingAreaTask = new AndroidTask(new com.tue.tools.Handler() { // will look prettier once I implement retrolambda
        @Override
        public void handle(Object[] k) {
            try {
                Document document = Jsoup.connect(remote_address + "/area").get();
                final String doc = document.body().html().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parking_map.clear();
                            JSONObject jobj = new JSONObject(doc);
                            String date = jobj.getString("date");
                            String time = jobj.getString("time");
                            JSONArray data = jobj.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject parkdata = data.getJSONObject(i);
                                String parkname = parkdata.getString("name");
                                int parkmaxsize = parkdata.getInt("maxsize");
                                int parkcurrentload = parkdata.getInt("current_load");
                                int parkid = parkdata.getInt("id");

                                parking_map.put(parkid, new ParkingArea(parkid, parkname, parkmaxsize, parkcurrentload));
                                JSONArray parkareas = parkdata.getJSONArray("areas");

                                for (int z = 0; z < parkareas.length(); z++) {

                                    JSONArray parkarea = parkareas.getJSONArray(z);


                                    ArrayList<LatLng> parkingpoints = new ArrayList<LatLng>();
                                    for (int j = 0; j < parkarea.length(); j++) {
                                        JSONObject _latlng = parkarea.getJSONObject(j);
                                        LatLng latlng = new LatLng(_latlng.getDouble("x"), _latlng.getDouble("y"));
                                        parkingpoints.add(latlng);
                                    }

                                    parking_map.get(parkid).addArea(parkingpoints);

                                }
                            }

                            for (ParkingArea pa : parking_map.values()) {
                                for (PolygonOptions pol : pa.createPolygonOptions()) {
                                    mMap.addPolygon(pol);
                                }
                            }


                        } catch (JSONException jse) {
                            jse.printStackTrace();
                        }
                    }
                });
                Log.i("JSON", "done loading parking areas");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("JSON", e.getMessage());
                // Error 404 (Not found)
                errorHandler.handleError(404);
            }
        }
    });


    final AndroidTask BuildingTask = new AndroidTask(new com.tue.tools.Handler() {

        @Override
        public void handle(Object[] k) {
            try {
                Document document = Jsoup.connect(remote_address + "/building").get();
                final String doc = document.body().html().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            building_map.clear();
                            JSONObject jobj = new JSONObject(doc);
                            String date = jobj.getString("date");
                            String time = jobj.getString("time");
                            JSONArray data = jobj.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject parkdata = data.getJSONObject(i);
                                String buildingname = parkdata.getString("name");
                                int buildingid = parkdata.getInt("id");
                                String description = parkdata.getString("description");
                                building_map.put(buildingid, new Building(buildingid, buildingname, description));
                                JSONArray buildingareas = parkdata.getJSONArray("areas");


                                for (int z = 0; z < buildingareas.length(); z++) {
                                    JSONArray buildingarea = buildingareas.getJSONArray(z);
                                    ArrayList<LatLng> buildingpoints = new ArrayList<LatLng>();
                                    for (int j = 0; j < buildingarea.length(); j++) {
                                        JSONObject _latlng = buildingarea.getJSONObject(j);
                                        LatLng latlng = new LatLng(_latlng.getDouble("x"), _latlng.getDouble("y"));
                                        buildingpoints.add(latlng);
                                    }

                                    building_map.get(buildingid).addArea(buildingpoints);

                                    Log.i("JSON", "Added building area");
                                }
                            }

                            for (Building pa : building_map.values()) {
                                for (PolygonOptions pol : pa.createPolygonOptions()) {
                                    SearchResult option = new SearchResult(pa.getName(), getResources().getDrawable(R.drawable.building_icon));
                                    search.addSearchable(option);
                                }
                            }


                        } catch (JSONException jse) {
                            jse.printStackTrace();
                        }
                    }
                });

                Log.i("JSON", "done loading buildings");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("JSON", e.getMessage());
                errorHandler.handleError(404);
            }
        }
    });

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MapsActivity.super.onBackPressed();
                    }
                }).create().show();

    }

    public void checkGps() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPS) {
            Toast toast = Toast.makeText(this, "GPS ON", Toast.LENGTH_LONG);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Location Services Not Active")
                    .setMessage("Please enable Location Services")
                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Show location settings when the user acknowledges the alert dialog
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        }
                    })
                    .setCancelable(false)
                    .create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkGps();
    }
}

