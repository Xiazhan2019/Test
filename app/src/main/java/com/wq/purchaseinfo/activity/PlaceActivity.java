package com.wq.purchaseinfo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.entity.Bdmark;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.utils.HttpConnect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class PlaceActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {
    // 百度地图控件
    private MapView mMapView = null;
    //private TextureMapView mMapView = null;
    // 百度地图
    private BaiduMap mBdMap;
    // 按钮
    private ImageButton ib_mode, ib_loc, ib_traffic;
    // 模式切换，正常模式
    private boolean modeFlag = true;
    // 定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    //添加marker
    private List<Bdmark> mapBeans;

    // GEO
    private Marker mMark;
    private GeoCoder mGeoSearch;
    TextView mTvinfo;

    GeoCoder mSearch = null;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_place);

        // 初始化控件
        InitView();
        // 初始化地图
        InitMap();
        // 定位
        //InitLocal();
        //GEO
        InitGeo();

        //初始化搜索模块，注册监听事件
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        initData();

    }

    // 初始化地图
    private void InitMap(){
        // 获取地图控件
        mMapView = (MapView) findViewById(R.id.bmapView);
        // 不显示缩放比例尺
        //mMapView.showZoomControls(false);
        // 不显示百度地图Logo
        mMapView.removeViewAt(1);

        // 百度地图
        mBdMap = mMapView.getMap();
        // 普通地图
        mBdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 设定中心点坐标（西安）
        LatLng cenpt = new LatLng(34.2777998978,108.953098279);
        // 定义地图状态
        //MapStatus mMapStatus = new MapStatus.Builder().zoom(15).build();
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(11)
                .build();

        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        mBdMap.setMapStatus(mMapStatusUpdate);

        mBdMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                final Bdmark beans = (Bdmark)marker.getExtraInfo().get("BEAN");
                View markView = View.inflate(getApplicationContext(), R.layout.item_maker, null);
                TextView tv_id = (TextView)markView.findViewById(R.id.mark_id);
                TextView tv_number = (TextView)markView.findViewById(R.id.mark_filenumber);
                TextView tv_title = (TextView)markView.findViewById(R.id.mark_title);
                tv_id.setText(beans.getId()  + "");
                tv_number.setText("编 号：" + beans.getNumber());
                tv_title.setText("题 目：" + beans.getTitle());
                InfoWindow.OnInfoWindowClickListener listener = null;
                listener = new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        Intent intent = new Intent(PlaceActivity.this,NoticeActivity.class);
                        intent.putExtra("filters_title", beans.getTitle());
                        intent.putExtra("filters_content", beans.getContent());
                        startActivity(intent);
                        mBdMap.hideInfoWindow();
                    }
                };
                LatLng ll = marker.getPosition();
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(markView), ll, -47, listener);
                mBdMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    /**
     * 从数据库添加数据
     */
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mapBeans = new ArrayList<Bdmark>();
              //  Filter_item_Dao f = new Filter_item_Dao();
              //  List<Filtered_item> plist = f.findCurrent();
                HttpConnect con = new HttpConnect();
                List<Notice> plist = con.request("FindPlace");
                Log.d("TAG","数据"+plist);
                int count = 0;
                for (final Notice item : plist) {
                    if(count<plist.size()){
                        count = count+1;
                    }
                    GeoCodeOption GeoOption =new GeoCodeOption().city("陕西省").address(item.getAgent());
                    mSearch.geocode(GeoOption);
                    final int finalCount = count;
                    mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult result) {
                            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                //     Toast.makeText(PlaceActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
                                Log.d("TAG","抱歉，未能找到结果"+item.getAgent());
                                return;
                            }
                            //添加 marker
                            mapBeans.add(new Bdmark(finalCount, item.getId(), item.getTitle(),result.getLocation().latitude, result.getLocation().longitude,item.getContent()));
                            Log.d("TAG","地址 "+mapBeans);
                            for(Bdmark bean : mapBeans){
                                LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("BEAN", bean);
                                View view = View.inflate(getApplicationContext(), R.layout.item_mark, null);
                                TextView tView = (TextView)view.findViewById(R.id.item_bean);
                                tView.setText(bean.getId() + "");
                                //将View转化为Bitmap
                                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(view);
                                OverlayOptions options = new MarkerOptions().position(latLng).icon(descriptor).extraInfo(bundle).zIndex(9).draggable(true);
                                mBdMap.addOverlay(options);
                            }
                        }
                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        }
                    });
                }
            }
        }).start();
    }

    private void InitView(){
        //地图控制按钮
        ib_mode = (ImageButton)findViewById(R.id.ib_mode);
        ib_mode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(modeFlag){
                    modeFlag = false;
                    mBdMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    showInfo("卫星模式");
                }else{
                    modeFlag = true;
                    mBdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    showInfo("普通模式");
                }
            }
        });

        ib_traffic = (ImageButton)findViewById(R.id.ib_traffic);
        ib_traffic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mBdMap.isTrafficEnabled()){
                    mBdMap.setTrafficEnabled(false);
                    //ib_traffic.setBackgroundResource(R.drawable.offtraffic);
                    showInfo("普通地图");
                }else{
                    mBdMap.setTrafficEnabled(true);
                    //ib_traffic.setBackgroundResource(R.drawable.ontraffic);
                    showInfo("实时路况图");
                }
            }
        });
        mTvinfo =(TextView)findViewById(R.id.tv_info);
    }



    //定位初始化
    private void InitLocal() {
        mBdMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        // 配置定位信息
        initLocation();
    }

    //定位监听
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mBdMap.setMyLocationEnabled(true);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBdMap.setMyLocationData(locData);
            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);  //第三个参数是位置图片没有就默认
            mBdMap.setMyLocationConfigeration(config);
            //以我的位置为中心
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mBdMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
        }
    }


    //配置定位信息
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02 国测局经纬度坐标系；bd09 百度墨卡托坐标系；bd09ll
        int span = 10000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        Log.i("2", "2");
        mLocationClient.start();

    }


    //监听地图点击事件，将点击获取的经纬度转换为具体地点信息，并在点击的位置做个标记
    private void InitGeo() {
        mGeoSearch = GeoCoder.newInstance();
        mGeoSearch.setOnGetGeoCodeResultListener(this);

        //地图监听GEO转换
        mBdMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                final LatLng lat = latLng;
                mGeoSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(lat));
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                return;
            }
        });
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (TextUtils.isEmpty(reverseGeoCodeResult.getAddress())) {
            //Toast.makeText(MainActivity.this, "地点解析失败，请重新选择", Toast.LENGTH_SHORT).show();
            showInfo("地点解析失败，请重新选择");
        } else {
            if (null != mMark) {
                mMark.remove();
            }


            mTvinfo.setText(reverseGeoCodeResult.getAddress());
            //show pos
            LatLng from = new LatLng(reverseGeoCodeResult.getLocation().latitude,
                    reverseGeoCodeResult.getLocation().longitude);
            BitmapDescriptor bdB = BitmapDescriptorFactory
                    .fromResource(R.mipmap.click_location_blue);
            OverlayOptions ooP = new MarkerOptions().position(from).icon(bdB);
            mMark = (Marker) (mBdMap.addOverlay(ooP));
            MapStatus mMapStatus = new MapStatus.Builder().target(from)
                    .build();
        }
    }

    // 显示消息
    private void showInfo(String str){
        Toast.makeText(PlaceActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mSearch.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载菜单文件
        getMenuInflater().inflate(R.menu.title_menu, menu);
        //显示图标
        setIconsVisible(menu,true);
        //默认显示三个点
        //new MenuInflater(this).inflate(R.menu.title_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if(menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //在点击这个菜单的时候，会做对应的事，类似于侦听事件，这里我们也要重写它
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这里是一个switch语句,主要通过menu文件中的id值来确定点了哪个菜单，然后做对应的操作，这里的menu是指你加载的那个菜单文件
        switch (item.getItemId()) {
            case R.id.calendar:
                Toast.makeText(this, "点击日历视图", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this,MainActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.place:
                Toast.makeText(this, "点击地图视图", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, PlaceActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.more:
                Toast.makeText(this, "点击状态查询", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, SearchActivity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.event:
                Toast.makeText(this, "点击日程管理", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, EventActivity.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.clock:
                Toast.makeText(this, "设置闹钟", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, ClockActivity.class);
                startActivity(intent5);
                finish();
                break;
            case R.id.user:
                Toast.makeText(this, "个人中心", Toast.LENGTH_SHORT).show();
                Intent intent6 = new Intent(this, UserActivity.class);
                startActivity(intent6);
                finish();
                break;
            case R.id.quit:
                showdialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //退出程序
    public void showdialog() {
        //定义一个新的对话框对象
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
        //设置对话框提示内容
        alertdialogbuilder.setMessage("确定要退出吗？");
        //定义对话框2个按钮标题及接受事件的函数
        alertdialogbuilder.setPositiveButton("确定", click1);
        alertdialogbuilder.setNegativeButton("取消", click2);
        //创建并显示对话框
        AlertDialog alertdialog1 = alertdialogbuilder.create();
        alertdialog1.show();
    }

    private DialogInterface.OnClickListener click1 = new DialogInterface.OnClickListener() {
        //使用该标记是为了增强程序在编译时候的检查，如果该方法并不是一个覆盖父类的方法，在编译时编译器就会报告错误。
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            //当按钮click1被按下时执行结束进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };
    private DialogInterface.OnClickListener click2 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            //当按钮click2被按下时则取消操作
            arg0.cancel();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlaceActivity.this,MainActivity.class);
        startActivity(intent);
        PlaceActivity.this.finish();
    }
}
