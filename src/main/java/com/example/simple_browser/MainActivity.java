package com.example.simple_browser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SafeBrowsingResponse;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    public static final int requestcode = 123;
    public static final String Shared_prefs = "Shared_preference_key";
    // public static final String User_Agent = "Mozilla/5.0 (Linux; Android 8.0.0;) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.119 Mobile Safari/537.36";
    public static final String mob_User_Agent = "Mozilla/5.0 (Linux; Android 8.0.0;) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Mobile Safari/537.36";
    public static final String desk_User_Agent = "Mozilla/5.0 (X11; U;Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    public static final String Key_book_title = "com.example.simple_browser_book_title";
    public static final String Key_book_url = "com.example.simple_browser_book_link";
    public static final String Key_book_id = "com.example.simple_browser_book_id";


    private static String file_type = "*/*";    // file types to be allowed for upload
    private boolean multiple_files = true;         // allowing multiple file upload


    private String cam_file_data;               // for storing camera file information
    private ValueCallback<Uri> file_data;       // data/header received after file selection
    private ValueCallback<Uri[]> file_path;     // received file(s) temp. location

    private final static int file_req_code = 1;


    //  MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();

    RelativeLayout rl2, rl4;
    RelativeLayout rl1, rl3;
    WebView webView;
    Spinner spinnerIcon;
    int clickedimage;

    TextInputLayout input_url, search_in_webview_text_layout;
    TextInputEditText textInputEditText, search_in_webview_Edittext;

    String url = "";
    FrameLayout frameLayout;
    ProgressBar progressBar;

    TextView title_textView;

    // RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<spinner_Item> spinnerlist;

    ImageView home, search_webview, cancel, forward;
    ImageView add_bookmark;
    ImageView imageView;

    boolean flag = false;
    // boolean if_clear=false;


    //static boolean incognito_mode=false;
    private boolean safeBrowsingIsInitialised;

    private CoordinatorLayout coordinatorLayout;

    AppBarLayout appBarLayout;
    Toolbar toolbar;

    String load_last_url_key, load_last_url;

    MenuItem share_menuItem, desktop_site, block_image, incognito;

    boolean desk_site, desk_site_key;

    boolean incognito_mode = false;

    boolean received_icon = false;

    String home_page = "Home";

    int brightness;


    //bookmarks Variables:
    boolean b;
    RecyclerView recyclerView;
    Toolbar toolbar_bookmark;
    BookmarkADapter mAdapter;
    // CoordinatorLayout coordinatorLayout;
    private SQLiteDatabase mdatabase_b;
    ImageView deleteAll_bookmarks, close_bookmark;
    boolean show_bookmark = false;


    //History Variables

    RecyclerView history_recyclerView;
    Toolbar toolbar_history;
    History_Adapter mAdapter_history;
    // CoordinatorLayout coordinatorLayout;
    private SQLiteDatabase mdatabase_h;
    ImageView deleteAll_history, close_history;
    boolean show_history = false;
    boolean save_history = true;

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate: called");

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

            setTheme(R.style.DarkTheme);

        } else {

            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loaddata();
        brightness = Settings.System.getInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);


        findingIds();
        setting_the_spinner();

        setSupportActionBar(toolbar);


        //input_url.setOnKeyListener(this);
        textInputEditText.setOnKeyListener(this);

        // title_textView.setOnKeyListener(this);
        search_in_webview_Edittext.setOnKeyListener(this);

        /*input_url.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputEditText.selectAll();

            }
        });
         */


        progressBar.setMax(100);

        //webView.setOnKeyListener(this);

        webViewchromeclient();

        webviewAllSettings();


        progressBar.setProgress(0);


        webviewclient();

        safeBrowsingIsInitialised = false;
        if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {

            WebViewCompat.startSafeBrowsing(this, new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                    safeBrowsingIsInitialised = true;

                }
            });

            if (!safeBrowsingIsInitialised) {
                Log.e(TAG, "Unable to initialize Safe Browsing");
            }
        }

        downloadTask();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();

            }
        });


        registerForContextMenu(webView);


        //for showing the keyboard when editText is long pressed

        clear_edittext(input_url);
        clear_edittext(search_in_webview_text_layout);


        if (load_last_url != null) {
            passing_value_to_url(load_last_url);
        }


        forward.setEnabled(false);
        forward.setImageResource(R.drawable.ic_arrow_forward_blacklight);

        if (rl2.getVisibility() == View.VISIBLE) {
            clickFunctionality(true);
            forward.setEnabled(true);
            forward.setImageResource(R.drawable.ic_arrow_forward_black);

            home.setImageResource(R.drawable.ic_home_black);
        } else {
            clickFunctionality(false);

        }


        //to open external links in this browser
        Uri data = getIntent().getData();                  //not learnt
        if (data != null) {

            String urll = data.toString();

            if (!urll.isEmpty()) {


                clickFunctionality(true);
                forward.setEnabled(true);
                forward.setImageResource(R.drawable.ic_arrow_forward_black);
                checkurl(urll);

            }

        }


        if (savedInstanceState != null) {


            if (savedInstanceState.getBoolean("check")) {

                clickFunctionality(false);

            } else {
                String message = savedInstanceState.getString("url");

                clickFunctionality(true);
                forward.setEnabled(true);
                forward.setImageResource(R.drawable.ic_arrow_forward_black);
                passing_value_to_url(message);
            }

        }


        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); //allows you to setcolor of statusbar programatically

        //window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   //allows fullscreenmode

       // window.setStatusBarColor(Color.BLUE);

        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //keeps the screen on

        //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   //for translucent status bar

        //window.addFlags((int) WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);

         */

        /*
        //receiving broadcast for finishing the activity by another activity
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action.equals("finish")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish"));

 */

        /*
        //for getting webviewpackageinfo
        PackageInfo webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this);
        Log.d(TAG, "Webview version:  " + webViewPackageInfo.versionName);
        Toast.makeText(this, webViewPackageInfo.versionName, Toast.LENGTH_SHORT).show();

        if (webViewPackageInfo == null) {
            Toast.makeText(this, "Doesn't support using webView", Toast.LENGTH_SHORT).show();
        }

         */


        //Bookmark Activities

        buildingBookmarkRecyclerView();


        //history Activities

        buildingHistoryRecyclerView();

    }


    //finding the Ids of all the views
    private void findingIds() {
        spinnerIcon = findViewById(R.id.choose_engine);
        webView = findViewById(R.id.webView);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        input_url = findViewById(R.id.enter_url_text);
        frameLayout = findViewById(R.id.frame);
        progressBar = findViewById(R.id.progress);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        title_textView = findViewById(R.id.title_textview);
        imageView = findViewById(R.id.imageview);
        //  buttonaddbookmark=findViewById(R.id.add_bookmark);
        home = findViewById(R.id.home);
        cancel = findViewById(R.id.stop_webview);
        forward = findViewById(R.id.forward_webview);
        //share = findViewById(R.id.share_webview);
        add_bookmark = findViewById(R.id.add_bookmark);
        //menu_item = findViewById(R.id.menu_item);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        appBarLayout = findViewById(R.id.appbar_layout);
        textInputEditText = findViewById(R.id.input_urll);
        toolbar = findViewById(R.id.toolbar_mainActivity);

        search_webview = findViewById(R.id.search_webview);
        search_in_webview_text_layout = findViewById(R.id.search_in_webview_text_layout);
        search_in_webview_Edittext = findViewById(R.id.search_in_webview_Edittext);

        //Bookmark ids
        rl3 = findViewById(R.id.rl3);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar_bookmark = findViewById(R.id.bookmark_toolbar);
        deleteAll_bookmarks = findViewById(R.id.delete_all_bookmark);
        close_bookmark = findViewById(R.id.close_bookmark);
        //coordinatorLayout=findViewById(R.id.coordinator);


        //History_Ids
        rl4 = findViewById(R.id.rl4);
        history_recyclerView = findViewById(R.id.history_recyclerview);
        toolbar_history = findViewById(R.id.history_toolbar);
        deleteAll_history = findViewById(R.id.delete_all_history);
        close_history = findViewById(R.id.close_history);


    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "onStart: called");

        //IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //registerReceiver(myBroadcastReceiver, filter);


    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "onStop: called");

        //unregisterReceiver(myBroadcastReceiver);


    }

    private void setting_up_the_home_text() {
        if (clickedimage == R.drawable.googleicon) {

            input_url.getEditText().setText(getString(R.string.google_com));


        } else if (clickedimage == R.drawable.bingicon) {

            input_url.getEditText().setText(getString(R.string.bing_com));

        } else if (clickedimage == R.drawable.duckduckgoicon) {

            input_url.getEditText().setText(getString(R.string.duckduckgo_com));

        } else if (clickedimage == R.drawable.yahooicon) {
            input_url.getEditText().setText(getString(R.string.yahoo_com));

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.webview_menus, menu);

        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            if (menu instanceof MenuBuilder) {
                MenuBuilder m = (MenuBuilder) menu;
                m.setOptionalIconsVisible(true);
            }
        }

        share_menuItem = menu.findItem(R.id.share);
        desktop_site = menu.findItem(R.id.desktop_site);
        block_image = menu.findItem(R.id.block_image);
        incognito = menu.findItem(R.id.incognito_mode);
        if (desk_site) {

            desktop_site.setChecked(true);
        }

        if (incognito_mode) {

            incognito.setChecked(true);
        }

        if (rl1.getVisibility() == View.VISIBLE) {
            menuItemVisibility(false);
        }


        return true;
        //return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {


            case R.id.share:

                String sharingbody = webView.getUrl();

                Intent sharingintent = new Intent(Intent.ACTION_SEND);

                sharingintent.setType("text/plain");
                sharingintent.putExtra(Intent.EXTRA_SUBJECT, "url sharing");
                sharingintent.putExtra(Intent.EXTRA_TEXT, sharingbody);
                startActivity(Intent.createChooser(sharingintent, "Share Via"));

                return true;
            case R.id.show_bookmarks:

                if (rl1.getVisibility() == View.VISIBLE) {
                    rl1.setVisibility(View.INVISIBLE);
                    rl3.setVisibility(View.VISIBLE);
                    mAdapter.swapCursor(getAllItemsBookmark());
                    appBarLayout.setVisibility(View.INVISIBLE);
                    show_bookmark = false;
                } else if (rl2.getVisibility() == View.VISIBLE) {

                    rl2.setVisibility(View.INVISIBLE);
                    rl3.setVisibility(View.VISIBLE);
                    mAdapter.swapCursor(getAllItemsBookmark());
                    appBarLayout.setVisibility(View.INVISIBLE);
                    show_bookmark = true;

                }

                return true;

            case R.id.show_history:

                if (rl1.getVisibility() == View.VISIBLE) {
                    rl1.setVisibility(View.INVISIBLE);
                    rl4.setVisibility(View.VISIBLE);
                    mAdapter_history.swapCursor(getAllItemsHistory());
                    appBarLayout.setVisibility(View.INVISIBLE);
                    show_history = false;
                } else if (rl2.getVisibility() == View.VISIBLE) {

                    rl2.setVisibility(View.INVISIBLE);
                    rl4.setVisibility(View.VISIBLE);
                    mAdapter_history.swapCursor(getAllItemsHistory());
                    appBarLayout.setVisibility(View.INVISIBLE);
                    show_history = true;

                }

                return true;
            case R.id.clear_history:
                deleteAllHistory();
                //Toast.makeText(MainActivity.this, "clear history", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete_cache:
                webView.clearCache(true);
                Toast.makeText(MainActivity.this, "Cache Deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.desktop_site:


                if (!item.isChecked()) {


                    if (rl1.getVisibility() == View.VISIBLE) {


                        item.setChecked(true);
                        Toast.makeText(this, "Desktop Site", Toast.LENGTH_SHORT).show();

                        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U;Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

                        desk_site = true;
                    } else {
                        item.setChecked(true);
                        Toast.makeText(this, "Desktop Site", Toast.LENGTH_SHORT).show();

                        webView.getSettings().setUserAgentString(desk_User_Agent);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

                        webView.reload();
                        desk_site = true;

                    }

                } else {

                    desk_site = false;

                    if (rl1.getVisibility() == View.VISIBLE) {
                        item.setChecked(false);

                        webView.getSettings().setUserAgentString(mob_User_Agent);
                        webView.getSettings().setUseWideViewPort(false);
                        webView.getSettings().setLoadWithOverviewMode(false);


                    } else {
                        item.setChecked(false);

                        webView.getSettings().setUserAgentString(mob_User_Agent);
                        webView.getSettings().setUseWideViewPort(false);
                        webView.getSettings().setLoadWithOverviewMode(false);
                        webView.reload();
                        desk_site = false;

                    }

                }

                return true;


            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                //  if_clear=true;
                startActivity(intent);

                return true;


            case R.id.incognito_mode:

                if (!incognito.isChecked()) {

                    spinnerIcon.setSelection(2);
                    incognito.setChecked(true);
                    incognito_mode = true;
                    Toast.makeText(this, "Incognito mode ON", Toast.LENGTH_SHORT).show();
                    if (rl1.getVisibility() != View.VISIBLE) {
                        go_to_Home();
                    }
                } else {

                    spinnerIcon.setSelection(0);
                    incognito.setChecked(false);
                    incognito_mode = false;
                    Toast.makeText(this, "Incognito Mode OFF", Toast.LENGTH_SHORT).show();
                    if (rl1.getVisibility() != View.VISIBLE) {
                        go_to_Home();
                    }

                }


                return true;


            case R.id.downloads:
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                return true;
            case R.id.block_image:

                if (!block_image.isChecked()) {

                    block_image.setChecked(true);
                    Toast.makeText(this, "Blocking Images", Toast.LENGTH_SHORT).show();

                    webView.getSettings().setLoadsImagesAutomatically(false);
                    webView.reload();

                } else {
                    Toast.makeText(this, "Showing Images Now", Toast.LENGTH_SHORT).show();

                    block_image.setChecked(false);
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.reload();
                }

                return true;


            default:
                return super.onOptionsItemSelected(item);

        }


    }


    private void menuItemVisibility(boolean b) {

        share_menuItem.setVisible(b);
        //desktop_site.setVisible(b);
        block_image.setVisible(b);


    }


    //all the settings required by WebView
    @SuppressLint("SetJavaScriptEnabled")
    private void webviewAllSettings() {


        if (desk_site) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setUserAgentString(desk_User_Agent);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);

        } else {
            webView.getSettings().setUserAgentString(mob_User_Agent);
            webView.getSettings().setUseWideViewPort(false);
            webView.getSettings().setLoadWithOverviewMode(false);

        }

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);               //not learned
        webView.getSettings().setAllowFileAccess(true);                 //not learned
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setMixedContentMode(0);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setAllowFileAccess(true);
        // webView.getSettings().setSupportMultipleWindows(true);
        // webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);


        //  webView.getSettings().setAllowFileAccess(true);


        // webView.getSettings().setCursiveFontFamily("serif");


    }


    //for enabling and disabling the buttons click
    void clickFunctionality(boolean b) {

        //home.setEnabled(b);
        // share.setEnabled(b);
        // search_webview.setEnabled(b);
        cancel.setEnabled(b);
        add_bookmark.setEnabled(b);


        if (!b) {
            //home.setImageResource(R.drawable.ic_home_green);
            //  share.setImageResource(R.drawable.ic_share_black_light);
            cancel.setImageResource(R.drawable.ic_clear_blacklight);
            add_bookmark.setImageResource(R.drawable.ic_star_border_blacklight);
        } else {
            home.setImageResource(R.drawable.ic_home_black);
            //  share.setImageResource(R.drawable.ic_share_black_24dp);
            cancel.setImageResource(R.drawable.ic_clear_black_24dp);
            add_bookmark.setImageResource(R.drawable.ic_star_border_black);
        }


    }


    public void go_to_Home() {

        if (home_page.equals("Home")) {

            if (incognito_mode) {

                webView.clearHistory();
                forward.setEnabled(false);
                Toast.makeText(this, "history cleared", Toast.LENGTH_SHORT).show();

            }
            webView.onPause();
            webView.stopLoading();
            rl1.setVisibility(View.VISIBLE);
            rl2.setVisibility(View.INVISIBLE);
            add_bookmark.setImageResource(R.drawable.ic_star_border_black);
            //home.setImageResource(R.drawable.ic_home_green);
            clickFunctionality(false);
            menuItemVisibility(false);

        } else {

            checkurl(home_page);
            // home.setImageResource(R.drawable.ic_home_green);
            clickFunctionality(true);


        }
        home.setImageResource(R.drawable.ic_home_green);


    }

    //for handling onClick methods
    public void onclicK(View v) {


        /* //for clearing the editText
        if (v.getTag().toString().equals("0")) {
            if (!input_url.getEditText().getText().toString().isEmpty()) {
                input_url.getEditText().getText().clear();
            } else {
                Log.i("clear", "Already clear");
            }
        }

        */

        if (v.getTag().toString().equals("1")) {

            if (!title_textView.getText().toString().isEmpty()) {
                ClipboardManager manager = (ClipboardManager) MainActivity.this.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("link", title_textView.getText().toString());
                manager.setPrimaryClip(clipData);

                showSnakbar(v);

            } else {
                Toast.makeText(this, "Cannot copy empty url.. Please reload", Toast.LENGTH_SHORT).show();
            }
        }


        //Home Button
        if (v.getTag().toString().equals(getString(R.string.Home))) {


            go_to_Home();

        }

        //Forward Button
        if (v.getTag().toString().equals(getString(R.string.Forward))) {

            changecolor(v);

            if (rl2.getVisibility() == View.VISIBLE && webView.canGoForward()) {

                webView.onResume();
                webView.goForward();


            } else if (rl1.getVisibility() == View.VISIBLE) {

                home.setImageResource(R.drawable.ic_home_black);

                rl1.setVisibility(View.INVISIBLE);
                rl2.setVisibility(View.VISIBLE);
                clickFunctionality(true);
                menuItemVisibility(true);
                addbookmark(v);
                //webView.goForward();

            }

        }

        //adding Bookmark
        if (v.getTag().toString().equals(getString(R.string.Add_Bookmark))) {

            addbookmark(v);


        }

        //stopping the loading process
        if (v.getTag().toString().equals(getString(R.string.Cancel))) {

            changecolor(v);
            webView.stopLoading();

        }

        //working on the search button
        if (v.getTag().toString().equals(getString(R.string.search_the_webview))) {


            if (!flag) {

                if (rl1.getVisibility() == View.VISIBLE) {

                    input_url.getEditText().requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    }

                    input_url.getEditText().selectAll();
                    input_url.getEditText().moveCursorToVisibleOffset();


                } else {
                    search_in_webview_text_layout.setVisibility(View.VISIBLE);
                    search_in_webview_text_layout.getEditText().setText(webView.getUrl());

                    search_in_webview_text_layout.getEditText().requestFocus();

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                    search_in_webview_text_layout.getEditText().selectAll();
                    search_in_webview_text_layout.getEditText().moveCursorToVisibleOffset();
                    flag = true;
                }


            } else {
                search_in_webview_text_layout.setVisibility(View.INVISIBLE);
                /*try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                 */
                flag = false;

            }


        }

    }


    //for adding bookmarks
    private void addbookmark(View v) {
        final Bookmar_functions bookmar_functions = new Bookmar_functions(this);

        boolean b = bookmar_functions.search(webView.getUrl());      //searching is done here

        if (v.getTag().toString().equals(getString(R.string.Add_Bookmark))) {
            if (b) {


                add_bookmark.setImageResource(R.drawable.ic_star_filled_24dp);
                DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();

                byte[] ba;

                if (received_icon) {
                    ba = dbBitmapUtility.getBytes(webView.getFavicon());


                } else {

                    ba = dbBitmapUtility.getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.noimage));

                }

                bookmar_functions.insert(webView.getTitle(), webView.getUrl(), ba);
                showSnakbar(v);

            } else {

                new AlertDialog.Builder(this).setTitle("Want to remove this from bookmark?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                long id = bookmar_functions.getId();
                                bookmar_functions.delete(id);
                                add_bookmark.setImageResource(R.drawable.ic_star_border_black);


                            }
                        }).setNegativeButton("NO", null)
                        .show();
            }
        } else if (v.getTag().toString().equals(getString(R.string.Forward))) {

            if (!b) {
                add_bookmark.setImageResource(R.drawable.ic_star_filled_24dp);
            }

        }

    }


    //for showing snackbar
    public void showSnakbar(View view) {

        Snackbar snackbar;

        if (view.getTag().toString().equals(getString(R.string.Add_Bookmark))) {

            snackbar = Snackbar.make(coordinatorLayout, "Book mark added", Snackbar.LENGTH_LONG)
                    .setAction("EDIT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bookmar_functions bookmar_function = new Bookmar_functions(MainActivity.this);
                            bookmar_function.search(webView.getUrl());

                            Intent intent = new Intent(MainActivity.this, ForEditingBokkmarksAndHistory.class);
                            intent.putExtra(Key_book_title, webView.getTitle());
                            intent.putExtra(Key_book_url, webView.getUrl());
                            intent.putExtra(Key_book_id, bookmar_function.getId());
                            startActivity(intent);

                        }
                    });
            View snackView = snackbar.getView();
            TextView textView = snackView.findViewById(com.google.android.material.R.id.snackbar_text);

            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

                snackbar.setActionTextColor(getColor(R.color.Orange));
                textView.setTextColor(getColor(R.color.Orange));
                snackView.setBackgroundColor(Color.BLACK);

            } else {

                snackbar.setActionTextColor(getColor(R.color.Orange));
                textView.setTextColor(getColor(R.color.Orange));
                snackView.setBackgroundColor(Color.WHITE);
            }


            snackbar.show();
        }

        if (view.getTag().toString().equals("1")) {

            snackbar = Snackbar.make(coordinatorLayout, "Link Copied", Snackbar.LENGTH_SHORT);

            View snackView = snackbar.getView();
            TextView textView = snackView.findViewById(com.google.android.material.R.id.snackbar_text);

            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

                textView.setTextColor(getColor(R.color.Orange));
                snackView.setBackgroundColor(Color.BLACK);

            } else {

                textView.setTextColor(getColor(R.color.Orange));
                snackView.setBackgroundColor(Color.WHITE);
            }

            snackbar.show();

        }


    }


    //adding color changed facility when botton is clicked
    private void changecolor(final View v) {

        new CountDownTimer(200, 200) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (v.getTag().toString().equals(getString(R.string.Forward))) {

                    forward.setImageResource(R.drawable.ic_arrow_forward_not_black);
                }
                if (v.getTag().toString().equals(getString(R.string.Cancel))) {
                    cancel.setImageResource(R.drawable.ic_clear_not_black);
                }
               /* if (v.getTag().toString().equals(getString(R.string.Share))) {

                    share.setImageResource(R.drawable.ic_share_not_black);
                }*/
            }

            @Override
            public void onFinish() {

                forward.setImageResource(R.drawable.ic_arrow_forward_black);
                cancel.setImageResource(R.drawable.ic_clear_black_24dp);
                // share.setImageResource(R.drawable.ic_share_black_24dp);

            }
        }.start();

    }


    @SuppressLint("ClickableViewAccessibility")
    private void clear_edittext(final TextInputLayout textInputLayout) {

        textInputLayout.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (textInputLayout.getEditText().getRight() - textInputLayout.getEditText().getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        textInputLayout.getEditText().getText().clear();
                        // your action here

                        return true;
                    }
                }
                return false;
            }
        });


    }


    //setting the spinner items
    private void setting_the_spinner() {
        spinnerinitlist();

        spinnerAdapter mAdapter = new spinnerAdapter(this, spinnerlist);

        spinnerIcon.setAdapter(mAdapter);

        spinnerIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_Item clickedItem = (spinner_Item) parent.getItemAtPosition(position);
                clickedimage = clickedItem.geticonImage();
                setting_up_the_home_text();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    //adding items to the spinnerlist
    private void spinnerinitlist() {


        spinnerlist = new ArrayList<>();
        spinnerlist.add(new spinner_Item(R.drawable.googleicon));
        spinnerlist.add(new spinner_Item(R.drawable.bingicon));
        spinnerlist.add(new spinner_Item(R.drawable.duckduckgoicon));
        spinnerlist.add(new spinner_Item(R.drawable.yahooicon));
    }


    //checking the url here and loading the webview
    private void checkurl(String message) {

        /*String url = "";
        try {

            if (message.startsWith("https://") || message.startsWith("http://")) {

                url = message;

            } else if (!message.isEmpty() && clickedimage == R.drawable.googleicon) {
                url = "https://www.google.com/search?q=" + message;

            } else if (!message.isEmpty() && clickedimage == R.drawable.duckduckgoicon) {
                url = "https://duckduckgo.com/?q=" + message;

            } else if (!message.isEmpty() && clickedimage == R.drawable.bingicon) {
                url = "https://www.bing.com/search?q=" + message;
            } else if (!message.isEmpty() && clickedimage == R.drawable.yahooicon) {
                url = "https://search.yahoo.com/search;_ylt=A00G7l7PeB5P3G0AKASl87UF?p=" + message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!url.trim().isEmpty()) {


            passing_value_to_url(url.trim());

        }*/

        checkurlAsyncTask task = new checkurlAsyncTask(this);

        String url = "";
        try {
            url = task.execute(message).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        passing_value_to_url(url);


    }

    //here the url is actually loaded
    private void passing_value_to_url(String UrL) {

        this.url = UrL;
        webView.onResume();
        if (rl2.getVisibility() == View.INVISIBLE && rl1.getVisibility() == View.VISIBLE) {
            rl2.setVisibility(View.VISIBLE);
            rl1.setVisibility(View.INVISIBLE);
        }

        if (rl3.getVisibility() == View.VISIBLE || rl4.getVisibility() == View.VISIBLE) {

            if (rl3.getVisibility() == View.VISIBLE) {

                rl3.setVisibility(View.INVISIBLE);
                rl2.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                clickFunctionality(true);
                menuItemVisibility(true);
                forward.setEnabled(true);
                // forward.setImageResource(R.drawable.ic_arrow_forward_black);

            } else {

                rl4.setVisibility(View.INVISIBLE);
                rl2.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                clickFunctionality(true);
                menuItemVisibility(true);
                forward.setEnabled(true);
                //  forward.setImageResource(R.drawable.ic_arrow_forward_black);
            }

        }

        webView.loadUrl(url);

        permissions();

    }


    public static class checkurlAsyncTask extends AsyncTask<String, Void, String> {

        int clickedimage;

        private WeakReference<MainActivity> activityWeakReference;

        checkurlAsyncTask(MainActivity activity) {

            activityWeakReference = new WeakReference<MainActivity>(activity);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();

            if (activity == null || activity.isFinishing()) {

                return;
            }

            this.clickedimage = activity.clickedimage;

        }

        @Override
        protected String doInBackground(String... strings) {

            String urll = "";
            try {

                if (strings[0].startsWith("https://") || strings[0].startsWith("http://")) {

                    urll = strings[0];

                } else if (!strings[0].isEmpty() && clickedimage == R.drawable.googleicon) {
                    urll = "https://www.google.com/search?q=" + strings[0];

                } else if (!strings[0].isEmpty() && clickedimage == R.drawable.duckduckgoicon) {
                    urll = "https://duckduckgo.com/?q=" + strings[0];

                } else if (!strings[0].isEmpty() && clickedimage == R.drawable.bingicon) {
                    urll = "https://www.bing.com/search?q=" + strings[0];
                } else if (!strings[0].isEmpty() && clickedimage == R.drawable.yahooicon) {
                    urll = "https://search.yahoo.com/search;_ylt=A00G7l7PeB5P3G0AKASl87UF?p=" + strings[0];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return urll;
        }


    }


    //here we define what should happen when a specific key is pressed
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (v.getId() == R.id.input_urll && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {


            clickFunctionality(true);
            menuItemVisibility(true);
            forward.setEnabled(true);
            forward.setImageResource(R.drawable.ic_arrow_forward_black);
            home.setImageResource(R.drawable.ic_home_black);

            String message = input_url.getEditText().getText().toString().trim();

            if (input_url.getEditText().getText().toString().isEmpty()) {

                YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(rl1);
                input_url.getEditText().requestFocus();
                input_url.setError("Input Required");

               /* try {

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                */

                clickFunctionality(false);
                menuItemVisibility(false);
                home.setImageResource(R.drawable.ic_home_green);
                forward.setEnabled(false);
                forward.setImageResource(R.drawable.ic_arrow_forward_black);


            } else if (!input_url.getEditText().getText().toString().isEmpty()) {

                save_history = true;
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkurl(message);
                rl1.setVisibility(View.INVISIBLE);
                rl2.setVisibility(View.VISIBLE);
                input_url.getEditText().getText().clear();
            }


        }

        /*if (v.getId() == R.id.title_textview && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {


            String message = title_textView.getText().toString().trim();

            if (title_textView.getText().toString().isEmpty()) {
                YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(title_textView);
                input_url.requestFocus();

            } else if (!title_textView.getText().toString().isEmpty()) {

                checkurl(message);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }


        }

         */

        if (v.getId() == R.id.search_in_webview_Edittext && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {


            String message = search_in_webview_text_layout.getEditText().getText().toString().trim();

            if (search_in_webview_text_layout.getEditText().getText().toString().isEmpty()) {

                search_in_webview_text_layout.getEditText().setError("Input Required");

                //YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(title_textView);
                //search_in_webview_text_layout.getEditText().requestFocus();

            } else if (!search_in_webview_text_layout.getEditText().getText().toString().isEmpty()) {

                save_history = true;
                checkurl(message);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                search_in_webview_text_layout.setVisibility(View.INVISIBLE);
                flag = false;
            }


        }


        return false;
    }


    //setting webViewChromeclient
    private void webViewchromeclient() {

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)         //not learned
            {
                callback.invoke(origin, true, false);
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress)         //not learned
            {

                Bookmar_functions bookmar_functions = new Bookmar_functions(MainActivity.this);
                boolean b = bookmar_functions.search(webView.getUrl());

                if (rl1.getVisibility() != View.VISIBLE) {
                    if (b) {

                        add_bookmark.setImageResource(R.drawable.ic_star_border_black);
                    } else {
                        add_bookmark.setImageResource(R.drawable.ic_star_filled_24dp);
                    }

                } else {
                    add_bookmark.setImageResource(R.drawable.ic_star_border_black);
                }


                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);


                if (newProgress == 100) {
                    frameLayout.setVisibility(View.GONE);
                    title_textView.setText(view.getUrl());
                    swipeRefreshLayout.setRefreshing(false);

                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if (save_history && !incognito_mode) {
                    Calendar calendar = Calendar.getInstance();
                    // String currenDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(calendar.getTime());

                    History_functions history_functions = new History_functions(MainActivity.this);
                    history_functions.insert(view.getTitle(),
                            view.getUrl(),
                            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(calendar.getTime()));
                }


            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {

                imageView.setImageBitmap(icon);
                received_icon = true;

                super.onReceivedIcon(view, icon);
            }


            /*-- handling input[type="file"] requests for android API 21+ --*/
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback;
                    Intent takePictureIntent = null;
                    Intent takeVideoIntent = null;

                    boolean includeVideo = false;
                    boolean includePhoto = false;

                    /*-- checking the accept parameter to determine which intent(s) to include --*/
                    paramCheck:
                    for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
                        String[] splitTypes = acceptTypes.split(", ?+"); // although it's an array, it still seems to be the whole value; split it out into chunks so that we can detect multiple values
                        for (String acceptType : splitTypes) {
                            switch (acceptType) {
                                case "*/*":
                                    includePhoto = true;
                                    includeVideo = true;
                                    break paramCheck;
                                case "image/*":
                                    includePhoto = true;
                                    break;
                                case "video/*":
                                    includeVideo = true;
                                    break;
                            }
                        }
                    }

                    if (fileChooserParams.getAcceptTypes().length == 0) {   //no `accept` parameter was specified, allow both photo and video
                        includePhoto = true;
                        includeVideo = true;
                    }

                    if (includePhoto) {
                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = create_image();
                                takePictureIntent.putExtra("PhotoPath", cam_file_data);
                            } catch (IOException ex) {
                                Log.e(TAG, "Image file creation failed", ex);
                            }
                            if (photoFile != null) {
                                cam_file_data = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }
                    }

                    if (includeVideo) {
                        takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (takeVideoIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                            File videoFile = null;
                            try {
                                videoFile = create_video();
                            } catch (IOException ex) {
                                Log.e(TAG, "Video file creation failed", ex);
                            }
                            if (videoFile != null) {
                                cam_file_data = "file:" + videoFile.getAbsolutePath();
                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                            } else {
                                takeVideoIntent = null;
                            }
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType(file_type);
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }

                    Intent[] intentArray;
                    if (takePictureIntent != null && takeVideoIntent != null) {
                        intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
                    } else if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else if (takeVideoIntent != null) {
                        intentArray = new Intent[]{takeVideoIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, file_req_code);
                    return true;
                } else {
                    return false;
                }
            }


        });


    }

    /*-- creating new image file here --*/
    private File create_image() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /*-- creating new video file here --*/
    private File create_video() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String file_name = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String new_name = "file_" + file_name + "_";
        File sd_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, ".3gp", sd_directory);
    }


    // setting webviewclient
    private void webviewclient() {

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                save_history = true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                view.loadUrl(url);
                if (url.contains("https://www.google.com/maps/") || url.contains("http://www.google.com/maps"))             //not learned
                {
                    Uri IntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, IntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }

                }
               /* else if(url.startsWith("intent://www.google.com/maps/"))
                {
                    try
                    {
                        Context context =view.getContext();
                        Intent Mapintent=Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
                        Mapintent.setPackage("com.google.android.apps.maps");
                        if(Mapintent !=null)
                        {
                            view.stopLoading();
                            PackageManager packageManager=context.getPackageManager();
                            ResolveInfo info=packageManager.resolveActivity(Mapintent,PackageManager.MATCH_DEFAULT_ONLY);
                            if(info!=null)
                            {
                                context.startActivity(Mapintent);
                            }else
                            {
                                String fallbackurl= Mapintent.getStringExtra("browser_fallback_url");
                                Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(fallbackurl));
                                context.startActivity(browserIntent);
                            }
                            return true;
                        }


                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return false;
                }*/

                if (url.startsWith("tel:")) {
                    webView.stopLoading();
                    Intent dialintent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(dialintent);
                }

                if (webView.getUrl().contains("https://play.google.com/store/apps/details?id=")) {
                    final String appPackageName = webView.getUrl();


                    try                                                                                   //not learned
                    {
                        webView.stopLoading();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appPackageName)));
                    }
                }

                if (webView.getUrl().contains("https://m.youtube.com/watch?v=")) {


                    final String appPackagename = webView.getUrl();
                    webView.stopLoading();


                    try                                    //not learned
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appPackagename)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appPackagename)));
                    }
                }

                if (url.startsWith("whatsapp://")) {
                    webView.stopLoading();

                    try {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");

                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl() + " -Shared from webview");
                        startActivity(whatsappIntent);
                    } catch (Exception e) {
                        String makeshorttext = "Whatsapp have not been installed";
                        Toast.makeText(MainActivity.this, makeshorttext, Toast.LENGTH_SHORT).show();
                    }
                }

                if (url.startsWith("intent")) {

                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        String fallbackurl = intent.getStringExtra("browser_fallback_url");
                        if (fallbackurl != null) {
                            webView.loadUrl(fallbackurl);
                            return true;
                        }

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }


                frameLayout.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {

                if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        callback.backToSafety(true);
                    }
                    Toast.makeText(MainActivity.this, "Unsafe web page Blocked", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    //for downloading medias
    private void downloadTask() {
        webView.setDownloadListener(new DownloadListener()           //not learned
        {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file..");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager) MainActivity.this.getSystemService(DOWNLOAD_SERVICE);
                if (dm != null) {
                    dm.enqueue(request);
                }
                Toast.makeText(MainActivity.this, "Downloading", Toast.LENGTH_LONG).show();
            }
        });


    }


    //creating context menu(on long press it will give some basic options)
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)    //learned
    {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);


        final WebView.HitTestResult webViewHitTestResult = webView.getHitTestResult();

        if (webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE) {


            contextMenu.add(0, 1, 3, "Save - Download Image").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {


                @Override
                public boolean onMenuItemClick(MenuItem menuitem) {
                    String DownloadImageUrl = webViewHitTestResult.getExtra();
                    if (URLUtil.isValidUrl(DownloadImageUrl)) {

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageUrl));

                        request.allowScanningByMediaScanner();
                        request.setMimeType("image/jpeg");


                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(DownloadImageUrl, null, null));

                        DownloadManager downloadManager = (DownloadManager) MainActivity.this.getSystemService(DOWNLOAD_SERVICE);
                        if (downloadManager != null) {
                            downloadManager.enqueue(request);
                        }

                        Toast.makeText(MainActivity.this, "Image Downloading...", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MainActivity.this, "Sorry.. Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });


            contextMenu.add(0, 2, 2, "open link").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {


                    Message msg = open_link_Handler.obtainMessage();
                    webView.requestFocusNodeHref(msg);


                   /* Message msg= Message.obtain();
                    msg.what=handler_thread.Message_copylink;

                    handler_thread.getHandler().sendMessage(msg);
                    webView.requestFocusNodeHref(msg);

                    */
                    return true;
                }
            });

            contextMenu.add(0, 3, 1, "copy link addresss").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Message msg = copy_link_Handler.obtainMessage();
                    webView.requestFocusNodeHref(msg);
                    return true;
                }
            });


            contextMenu.add(0, 4, 4, "copy image address").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String ImgUrl = webViewHitTestResult.getExtra();
                    ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("webView", ImgUrl);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                    Toast.makeText(MainActivity.this, "Address copied", Toast.LENGTH_LONG).show();
                    return true;
                }
            });


        }
        if (webViewHitTestResult.getType() == WebView.HitTestResult.ANCHOR_TYPE || webViewHitTestResult.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {

            contextMenu.setHeaderTitle(webViewHitTestResult.getExtra());
            contextMenu.add(1, 5, 1, " copy link address").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String CopyLink = webViewHitTestResult.getExtra();
                    ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("webView", CopyLink);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                    Toast.makeText(MainActivity.this, "Address copied", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            contextMenu.add(1, 6, 2, "open").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String URL = webViewHitTestResult.getExtra();
                    webView.loadUrl(URL);

                    return true;
                }
            });

            contextMenu.add(1, 7, 3, "share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = webViewHitTestResult.getExtra();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "url sharing");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    return true;
                }
            });

        }


    }


    //for opening context url
    @SuppressLint("HandlerLeak")
    private Handler open_link_Handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String urrl = (String) msg.getData().get("url");
            webView.loadUrl(urrl);
        }
    };

    //for copying context url
    @SuppressLint("HandlerLeak")
    private Handler copy_link_Handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String urrl = (String) msg.getData().get("url");
            if (urrl != null) {
                ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("webView", urrl);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, " Link Address copied", Toast.LENGTH_LONG).show();
            }

        }
    };


    //list of permission we have to take
    private void permissions() {

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};

        if (EasyPermissions.hasPermissions(this, perms)) {

            Log.i(TAG, "permissions: permissions granted");
        } else {

            EasyPermissions.requestPermissions(this, "This app needs all the permissions", requestcode, perms);
        }

    }


    //we have to implement EasyPermissions.PermissionCallbacks so to decide what to do after the permission is denied or granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {


    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        //here we show a dialog for asking the permissions again with Do not ask again option
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) ;
        {

            new AppSettingsDialog.Builder(this).build().show();
            //if here the user select do not ask again option and again tries to access the features then he is taken to the app settings..
        }

    }

   /*
    //here we can define what should happen after the user comes back after granting permissions
   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){

            //here we can define what should happen after the user comes back after granting permissions
        }
    }

   */


    //saving the instance so that nothing changes on screen orientation
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (rl1.getVisibility() == View.VISIBLE) {
            outState.putBoolean("check", true);
        } else if (rl2.getVisibility() == View.VISIBLE) {
            outState.putString("url", webView.getUrl());
        }
    }


    //saving the URL so that at activity launch it shpuld be loaded first
    private void savedata() {

        SharedPreferences sharedPreferences = getSharedPreferences(Shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (rl1.getVisibility() == View.VISIBLE) {
            editor.putString(load_last_url_key, null);
        } else {
            editor.putString(load_last_url_key, webView.getUrl());
        }
        editor.putBoolean(String.valueOf(desk_site_key), desk_site);
        editor.putBoolean(getString(R.string.incognito_mode_key), incognito_mode);
        editor.apply();


    }

    //loading the data at onCreae Method
    private void loaddata() {

        SharedPreferences sharedPreferences = getSharedPreferences(Shared_prefs, MODE_PRIVATE);

        load_last_url = sharedPreferences.getString(load_last_url_key, null);
        desk_site = sharedPreferences.getBoolean(String.valueOf(desk_site_key), false);
        incognito_mode = sharedPreferences.getBoolean(getString(R.string.incognito_mode_key), false);


    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        LinearLayout linearLayout = findViewById(R.id.appbar_linear_layout);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            /*
            //code to hide status bar
            View decorView=getWindow().getDecorView();
            int uiOptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

             */


            linearLayout.setGravity(Gravity.CENTER);


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            // window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            linearLayout.setGravity(Gravity.NO_GRAVITY);
        }

    }

    //overriding and defining what should happen when back button is pressed
    @Override
    public void onBackPressed() {

        if (rl2.getVisibility() == View.VISIBLE && webView.canGoBack()) {

            if (search_in_webview_text_layout.getVisibility() == View.VISIBLE) {
                search_in_webview_text_layout.setVisibility(View.INVISIBLE);

            }
            clickFunctionality(true);
            menuItemVisibility(true);
            save_history = false;
            webView.goBack();
        } else if (search_in_webview_text_layout.getVisibility() == View.VISIBLE && rl2.getVisibility() == View.VISIBLE) {
            search_in_webview_text_layout.setVisibility(View.INVISIBLE);
            flag = true;

        } else if (rl2.getVisibility() == View.VISIBLE && rl1.getVisibility() == View.INVISIBLE) {

            webView.stopLoading();
            // add_bookmark.setImageResource(R.drawable.ic_star_border_black);
            // home.setImageResource(R.drawable.ic_home_green);

            clickFunctionality(false);
            menuItemVisibility(false);
            rl1.setVisibility(View.VISIBLE);
            rl2.setVisibility(View.INVISIBLE);
        } else if (rl3.getVisibility() == View.VISIBLE && !show_bookmark) {
            rl1.setVisibility(View.VISIBLE);
            rl3.setVisibility(View.INVISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);
        } else if (rl3.getVisibility() == View.VISIBLE && show_bookmark) {

            rl2.setVisibility(View.VISIBLE);
            rl3.setVisibility(View.INVISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);

            if (b) {
                Bookmar_functions bookmar_function = new Bookmar_functions(this);
                boolean f = bookmar_function.search(webView.getUrl());
                if (f) {
                    add_bookmark.setImageResource(R.drawable.ic_star_border_black);
                }
            }
        } else if (rl4.getVisibility() == View.VISIBLE && !show_history) {

            rl1.setVisibility(View.VISIBLE);
            rl4.setVisibility(View.INVISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);

        } else if (rl4.getVisibility() == View.VISIBLE && show_history) {

            rl2.setVisibility(View.VISIBLE);
            rl4.setVisibility(View.INVISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);

        }



        /*else if (rl3.getVisibility()==View.VISIBLE && b && show) {


            Bookmar_functions bookmar_function = new Bookmar_functions(this);
            boolean f = bookmar_function.search(MainActivity.webView.getUrl());
            if (f) {
               add_bookmark.setImageResource(R.drawable.ic_star_border_black);
            }

        }*/

        else {
           /* new AlertDialog.Builder(this).setTitle("Do you want to exit?").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton("No", null).show();

            */

            super.onBackPressed();


        }


    }


    //defining the onPause method
    @Override
    protected void onPause() {

        Log.i(TAG, "onPause: called");
        savedata();


        try {
            boolean canWrite = Settings.System.canWrite(this);
            if (canWrite) {

                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (brightness));

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                this.startActivity(intent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onPause();

    }


    //defining onDestroy function
    @Override
    protected void onDestroy() {

        Log.i(TAG, "onDestroy: called");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(getString(R.string.pref_clear_data_after_app_closed_switch_key), false)) {

            String[] str = null;
            Set<String> selections = sharedPreferences.getStringSet(getString(R.string.pref_select_what_to_delete_after_app_is_closed_key), null);
            str = selections.toArray(new String[selections.size()]);

            if (str.length != 0) {

                for (String value : str) {

                    if (value.equals("Cache")) {

                        webView.clearCache(true);

                    } else if (value.equals("History")) {


                        History_functions history_functions = new History_functions(this);
                        history_functions.deleteAll();


                    } else {

                        CookieManager.getInstance().removeAllCookies(null);
                        CookieManager.getInstance().flush();
                    }


                }
            }

            Log.d(TAG, "onDestroy: data cleared");


        }

        try {
            boolean canWrite = Settings.System.canWrite(this);
            if (canWrite) {

                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (brightness));

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                this.startActivity(intent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        savedata();
        /*
        //sending broadcast to finish the BokkmarkActivity
        Intent intent = new Intent("finish");
        sendBroadcast(intent);

 */

        super.onDestroy();


    }

    @Override
    protected void onResume() {

        Log.i(TAG, "onResume: called");

        mAdapter.swapCursor(getAllItemsBookmark());
        mAdapter_history.swapCursor(getAllItemsHistory());


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString(getString(R.string.pref_themes_key), "Light").equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            int brigtness = sharedPreferences.getInt(getString(R.string.pref_dark_brightness_key), 21);

            boolean canWrite = Settings.System.canWrite(this);
            if (canWrite) {

                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (brigtness * 255) / 100);

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                this.startActivity(intent);

            }


        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


            boolean canWrite = Settings.System.canWrite(this);
            if (canWrite) {

                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (brightness));

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                this.startActivity(intent);

            }


        }

        if (sharedPreferences.getString(getString(R.string.pref_choose_engine_key), "Google").startsWith("G")) {

            spinnerIcon.setSelection(0);
        } else if (sharedPreferences.getString(getString(R.string.pref_choose_engine_key), "Google").startsWith("B")) {

            spinnerIcon.setSelection(1);
        } else if (sharedPreferences.getString(getString(R.string.pref_choose_engine_key), "Google").startsWith("d")) {

            spinnerIcon.setSelection(2);
        } else if (sharedPreferences.getString(getString(R.string.pref_choose_engine_key), "Google").startsWith("Y")) {

            spinnerIcon.setSelection(3);
        }

        if (sharedPreferences.getBoolean(getString(R.string.pref_home_page_switch_key), false)) {

            String str = sharedPreferences.getString(getString(R.string.pref_home_page_key), "Home");

            if (str.trim().isEmpty() || str.trim().startsWith("https://wwww.Home")) {

                home_page = "Home";

            } else {

                home_page = str;
            }
        } else {
            home_page = "Home";

        }




        /*if (s.length != 0) {

            for (String value : s) {

                if (value.equals("Cache")) {

                    webView.clearCache(true);

                } else if (value.equals("History")) {

                    deleteAllHistory();

                } else if (value.equals("Bookmarks")) {

                    deleteAllBookmarks();
                } else {

                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();
                }


            }




        }*/


        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;

            /*-- if file request cancelled; exited camera. we need to send null value to make future attempts workable --*/
            if (resultCode == Activity.RESULT_CANCELED) {
                if (requestCode == file_req_code) {
                    file_path.onReceiveValue(null);
                    return;
                }
            }

            /*-- continue if response is positive --*/
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == file_req_code) {
                    if (null == file_path) {
                        return;
                    }
                    if (intent == null || intent.getClipData() == null) {
                        if (cam_file_data != null) {
                            results = new Uri[]{Uri.parse(cam_file_data)};
                        }
                    } else {
                        if (null != intent.getClipData()) { // checking if multiple files selected or not
                            final int numSelectedFiles = intent.getClipData().getItemCount();
                            results = new Uri[numSelectedFiles];
                            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                                results[i] = intent.getClipData().getItemAt(i).getUri();
                            }
                        } else {
                            results = new Uri[]{Uri.parse(intent.getDataString())};
                        }
                    }
                }
            }
            file_path.onReceiveValue(results);
            file_path = null;
        } else {
            if (requestCode == file_req_code) {
                if (null == file_data) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                file_data.onReceiveValue(result);
                file_data = null;
            }
        }
    }


    //Bookmark Activities_functions


    public void buildingBookmarkRecyclerView() {
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(this);
        mdatabase_b = dbHelper.getWritableDatabase();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookmarkADapter(this, getAllItemsBookmark());
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                removeBookmarkitem((long) viewHolder.itemView.getTag());
                b = true;  //for changing the image of bookmark on back pressed
                Toast.makeText(MainActivity.this, "Bookmark deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        registerForContextMenu(recyclerView);


        mAdapter.setOnItemCLickListener(new BookmarkADapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                rl3.setVisibility(View.INVISIBLE);
                rl2.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                clickFunctionality(true);
                menuItemVisibility(true);
                forward.setEnabled(true);
                forward.setImageResource(R.drawable.ic_arrow_forward_black);
                passing_value_to_url(mAdapter.getLink());

            }


        });

        deleteAll_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAllBookmarks();
            }
        });


        close_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void deleteAllBookmarks() {

        final Bookmar_functions bookmar_functions = new Bookmar_functions(MainActivity.this);

        if (bookmar_functions.countall() != 0) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete All Bookmarks?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            bookmar_functions.deleteAll();

                            mAdapter.swapCursor(getAllItemsBookmark());
                            b = true;

                        }
                    }).setNegativeButton("No", null).show();
        } else {
            Toast.makeText(MainActivity.this, "Nothing to delete", Toast.LENGTH_SHORT).show();
        }


    }

    void removeBookmarkitem(long id) {

        Bookmar_functions bookmar_functions = new Bookmar_functions(this);
        bookmar_functions.delete(id);

        mAdapter.swapCursor(getAllItemsBookmark());

    }


    private Cursor getAllItemsBookmark() {

        /*return mdatabase_b.query(Bookmark_class.Bookmark_entry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Bookmark_class.Bookmark_entry.Column_TIMESTAMP + " DESC"
        );*/

        getAllBookmarksAsyncTask task = new getAllBookmarksAsyncTask();
        Cursor cursor = null;
        try {
            cursor = task.execute(mdatabase_b).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cursor;


    }


    public static class getAllBookmarksAsyncTask extends AsyncTask<SQLiteDatabase, Void, Cursor> {


        @Override
        protected Cursor doInBackground(SQLiteDatabase... sqLiteDatabases) {

            return sqLiteDatabases[0].query(Bookmark_class.Bookmark_entry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Bookmark_class.Bookmark_entry.Column_TIMESTAMP + " DESC"
            );
        }
    }


   /* private boolean searchItem(String link){


        searcItemAsyncTask task=new searcItemAsyncTask(link,mdatabase_b,this);


        boolean b=true;
        try {
            b=task.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return b;

    }

    private static class searcItemAsyncTask extends AsyncTask<Void, Void, Boolean> {

        String link;
        SQLiteDatabase db;
        long idd;

        private WeakReference<MainActivity> activityWeakReference;



        searcItemAsyncTask(String link, SQLiteDatabase db,MainActivity activity ){

            this.link=link;
            this.db=db;
            activityWeakReference=new WeakReference<MainActivity>(activity);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Cursor cursor=db.query(Bookmark_class.Bookmark_entry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Bookmark_class.Bookmark_entry.Column_TIMESTAMP + " DESC"
            );

            if(cursor.getCount()!=0){

                cursor.moveToFirst();

                do {
                    if (cursor.getString(cursor.getColumnIndex(Bookmark_class.Bookmark_entry.Column_link)).equals(link)) {

                        idd=(cursor.getLong(cursor.getColumnIndex(Bookmark_class.Bookmark_entry._ID)));
                        return false;

                    }

                } while (cursor.moveToNext());

            } else {
                return true;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            MainActivity activity=activityWeakReference.get();

            if(activity==null && activity.isFinishing()){

                return;
            }

            activity.id=idd;

        }
    }

    */


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case 8:
                passing_value_to_url(mAdapter.getLink());
                break;
            case 9:
                b = true;
                removeBookmarkitem(mAdapter.getItemId());
                Toast.makeText(this, "Bookmark deleted", Toast.LENGTH_SHORT).show();
                break;
            case 10:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Link");
                intent.putExtra(Intent.EXTRA_TEXT, mAdapter.getLink());
                startActivity(Intent.createChooser(intent, "Share via"));
                break;
            case 11:

                //for updating bookmarks using activity

                Intent intent1 = new Intent(MainActivity.this, ForEditingBokkmarksAndHistory.class);
                intent1.putExtra(Key_book_title, mAdapter.getTitle());
                intent1.putExtra(Key_book_url, mAdapter.getLink());
                intent1.putExtra(Key_book_id, mAdapter.getItemId());
                startActivity(intent1);

                break;


            //for History

            case 12:

                passing_value_to_url(mAdapter_history.getLink());
                break;

            case 13:
                removeHistoryitem(mAdapter_history.getItemId());
                break;
            case 14:

                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_SUBJECT, "Sharing Link");
                intent2.putExtra(Intent.EXTRA_TEXT, mAdapter_history.getLink());
                startActivity(Intent.createChooser(intent2, "Share Via"));
                break;

        }


        return super.onContextItemSelected(item);
    }


    //History Activities Functions


    public void buildingHistoryRecyclerView() {

        History_db_helper history_db_helper = new History_db_helper(this);

        mdatabase_h = history_db_helper.getWritableDatabase();
        history_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter_history = new History_Adapter(this, getAllItemsHistory());

        history_recyclerView.setAdapter(mAdapter_history);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                removeHistoryitem((long) viewHolder.itemView.getTag());
                Toast.makeText(MainActivity.this, "History deleted", Toast.LENGTH_SHORT).show();


            }
        }).attachToRecyclerView(history_recyclerView);

        registerForContextMenu(history_recyclerView);


        mAdapter_history.setOnItemCLickListener(new History_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                rl4.setVisibility(View.INVISIBLE);
                rl2.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                clickFunctionality(true);
                menuItemVisibility(true);
                forward.setEnabled(true);
                forward.setImageResource(R.drawable.ic_arrow_forward_black);
                passing_value_to_url(mAdapter_history.getLink());

            }
        });

        deleteAll_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAllHistory();

            }
        });

        close_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void deleteAllHistory() {

        final History_functions history_functions = new History_functions(MainActivity.this);

        if (history_functions.countAll() != 0) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete All History?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            history_functions.deleteAll();

                            mAdapter_history.swapCursor(getAllItemsHistory());
                            Toast.makeText(MainActivity.this, "History Deleted", Toast.LENGTH_SHORT).show();

                        }
                    }).setNegativeButton("No", null).show();
        } else {
            Toast.makeText(MainActivity.this, "Nothing to delete", Toast.LENGTH_SHORT).show();
        }


    }


    public void removeHistoryitem(long id) {

        History_functions history_functions = new History_functions(this);
        history_functions.delete(id);
        Toast.makeText(this, "All History Deleted", Toast.LENGTH_SHORT).show();
        mAdapter_history.swapCursor(getAllItemsHistory());

    }


    public Cursor getAllItemsHistory() {

        getAllItemHistoryAsyncTask task = new getAllItemHistoryAsyncTask();
        Cursor cursor = null;
        try {
            cursor = task.execute(mdatabase_h).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cursor;


    }


    public static class getAllItemHistoryAsyncTask extends AsyncTask<SQLiteDatabase, Void, Cursor> {


        @Override
        protected Cursor doInBackground(SQLiteDatabase... sqLiteDatabases) {

            return sqLiteDatabases[0].query(History_class.History_entry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    History_class.History_entry.Column_TIMESTAMP + " DESC"
            );
        }
    }


}


