package com.example.fengdeyu.xuanyue_reader.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.fengdeyu.xuanyue_reader.R;
import com.example.fengdeyu.xuanyue_reader.adapter.PageViewAdapter;
import com.example.fengdeyu.xuanyue_reader.fragment.ReadPageFragment;
import com.example.fengdeyu.xuanyue_reader.fragment.ReadScrollFragment;
import com.example.fengdeyu.xuanyue_reader.other.GetBookCase;
import com.example.fengdeyu.xuanyue_reader.other.GetChapterContent;
import com.example.fengdeyu.xuanyue_reader.other.GetPageAttribute;
import com.example.fengdeyu.xuanyue_reader.other.MyReadInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReadOnlineBookActivity extends AppCompatActivity implements MyReadInterface{
    private ImageView iv_back;


    private LinearLayout ll_book_read_top;
    private LinearLayout ll_book_read_bottom;


    private TextView tv_chapter_content;

    private Button btn_paging;          ///////


    Fragment mRead;

    public String mContents="";

    SharedPreferences setting;      //存取app设置的文件

    private LinearLayout ll_setting;
    private TextView tv_setting;



    private ImageView iv_font_size_add;
    private ImageView iv_font_size_minus;

    private SeekBar sk_brightness;

    private ImageView bg_theme_1;
    private ImageView bg_theme_2;
    private ImageView bg_theme_3;
    private ImageView bg_theme_4;

    private RelativeLayout activity_read;

    private ImageView iv_screen_rotate;

    private ImageView iv_font_typeface;

    private TextView tv_day_night;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_online_book);

        tv_day_night= (TextView) findViewById(R.id.tv_day_night);
        if(GetPageAttribute.getInstance().day_night.equals("day")){
            tv_day_night.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.ic_menu_mode_night_manual),null,null);
            tv_day_night.setText("夜间");
        }else {
            tv_day_night.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.ic_menu_mode_night_manual),null,null);
            tv_day_night.setText("日间");
        }
        tv_day_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                if(GetPageAttribute.getInstance().day_night.equals("day")) {
                    GetPageAttribute.getInstance().bg_theme = 0xff000000;
                    GetPageAttribute.getInstance().textColor = 0xffeeeeee;
                    GetPageAttribute.getInstance().day_night="night";
                    tv_day_night.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.ic_menu_mode_night_manual),null,null);
                    tv_day_night.setText("日间");
                    setDefaultFragment();
                }else if(GetPageAttribute.getInstance().day_night.equals("night")) {
                    GetPageAttribute.getInstance().bg_theme = 0xffffffff;
                    GetPageAttribute.getInstance().textColor = 0xff444444;
                    GetPageAttribute.getInstance().day_night="day";
                    tv_day_night.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.ic_menu_mode_night_manual),null,null);
                    tv_day_night.setText("夜间");
                    setDefaultFragment();
                }
            }
        });

        iv_font_typeface= (ImageView) findViewById(R.id.iv_font_typeface);
        iv_font_typeface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReadOnlineBookActivity.this,TypefaceSelectActivity.class));
            }
        });

        GetPageAttribute.getInstance().rate= 0f;

        activity_read= (RelativeLayout) findViewById(R.id.activity_read);
        activity_read.setBackgroundColor(GetPageAttribute.getInstance().bg_theme);

        iv_screen_rotate= (ImageView) findViewById(R.id.iv_screen_rotate);
        iv_screen_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
                }
                if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
                }
                setDefaultFragment();
                ll_setting.setVisibility(View.GONE);

            }
        });


        bg_theme_1= (ImageView) findViewById(R.id.bg_theme_1);
        bg_theme_2= (ImageView) findViewById(R.id.bg_theme_2);
        bg_theme_3= (ImageView) findViewById(R.id.bg_theme_3);
        bg_theme_4= (ImageView) findViewById(R.id.bg_theme_4);

        bg_theme_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                GetPageAttribute.getInstance().bg_theme= 0xffffffff;
                setDefaultFragment();
            }
        });
        bg_theme_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                GetPageAttribute.getInstance().bg_theme= 0xfffffaf0;
                setDefaultFragment();
            }
        });
        bg_theme_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                GetPageAttribute.getInstance().bg_theme= 0xffc1ffc1;
                setDefaultFragment();
            }
        });
        bg_theme_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                GetPageAttribute.getInstance().bg_theme= 0xffc9c9c9;
                setDefaultFragment();
            }
        });


        /**
         * 亮度调节
         */

        try {
            GetPageAttribute.getInstance().brightness= Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        sk_brightness= (SeekBar) findViewById(R.id.sk_brightness);
        sk_brightness.setProgress((int)(GetPageAttribute.getInstance().brightness/255f*100));
        sk_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Window window = getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.screenBrightness = sk_brightness.getProgress()/100f;
                window.setAttributes(lp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        setting=getSharedPreferences("setting_pref",MODE_PRIVATE);
        setting.edit().putBoolean("isChanged",true).commit();           //是否应该刷新文本

        btn_paging= (Button)findViewById(R.id.paging);         //////

        btn_paging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                mRead = new ReadScrollFragment();
                transaction.replace(R.id.id_content, mRead);
                transaction.commit();

            }
        });





        iv_back= (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        iv_font_size_add= (ImageView) findViewById(R.id.iv_font_size_add);

        iv_font_size_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                if(GetPageAttribute.getInstance().textSize==32){
                    GetPageAttribute.getInstance().textSize=32;
                }else {
                    GetPageAttribute.getInstance().textSize+=2;
                    setDefaultFragment();
                }



            }
        });

        iv_font_size_minus= (ImageView) findViewById(R.id.iv_font_size_minus);

        iv_font_size_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPageAttribute.getInstance().pageParamChanged=true;
                if (GetPageAttribute.getInstance().textSize==10){
                    GetPageAttribute.getInstance().textSize=10;
                }else {
                    GetPageAttribute.getInstance().textSize-=2;
                    setDefaultFragment();
                }


            }
        });




        ll_book_read_top= (LinearLayout) findViewById(R.id.ll_book_read_top);
        ll_book_read_bottom= (LinearLayout) findViewById(R.id.ll_book_read_bottom);

        tv_chapter_content= (TextView) findViewById(R.id.tv_chapter_content);


        tv_chapter_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ChapterContentActivity.class).putExtra("intoWay","bookIntro");
                startActivity(intent);
            }
        });


        ll_setting= (LinearLayout) findViewById(R.id.ll_setting);

        tv_setting= (TextView) findViewById(R.id.tv_setting);
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ll_setting.getVisibility()==View.GONE){
                    ll_setting.setVisibility(View.VISIBLE);
                }else {
                    ll_setting.setVisibility(View.GONE);
                }

            }
        });


        setDefaultFragment();

    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mRead = new ReadPageFragment();
        transaction.replace(R.id.id_content, mRead);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url;

//        if(setting.getBoolean("isChanged",false)) {
//
//
//            url = GetChapterContent.getInstance().mList.get(GetChapterContent.getInstance().currentChapter).chapter_url;
//
//
//            GetPageAttribute.getInstance().rate= 0f;
//
//            new loadContentsAsyncTask().execute(url);
//
//            setting.edit().putBoolean("isChanged",false).commit();
//
//        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }






    public void toggleReadBar(){
        if(ll_book_read_top.getVisibility()==View.GONE){
            ll_book_read_top.setVisibility(View.VISIBLE);
            ll_book_read_bottom.setVisibility(View.VISIBLE);


        }else {
            ll_book_read_top.setVisibility(View.GONE);
            ll_book_read_bottom.setVisibility(View.GONE);
            ll_setting.setVisibility(View.GONE);
        }
    }


    public class loadContentsAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {


            try {
//                String contents="123";
                Document doc= Jsoup.connect(params[0]).get();
                //String contents=doc.getElementById("content").text();
                String contents=doc.select("div.showtxt").get(0).text();
//                Log.i("info",params[0]);
                Log.i("txt",contents);
                return contents;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String contents) {
            super.onPostExecute(contents);

            if(contents!=null) {
                 StringBuilder sb = new StringBuilder(contents);
                int i = 0;
                while (sb.indexOf(" ", i + 3) != -1) {
                    i = sb.indexOf(" ", i + 3);
                    sb.insert(i, "\n");
                }


                mContents=sb.toString();

                GetPageAttribute.getInstance().contents=mContents;
                GetPageAttribute.getInstance().chapterName=GetChapterContent.getInstance().mList.get(GetChapterContent.getInstance().currentChapter).chapter_name;


                setDefaultFragment();


            }

        }
    }



}
