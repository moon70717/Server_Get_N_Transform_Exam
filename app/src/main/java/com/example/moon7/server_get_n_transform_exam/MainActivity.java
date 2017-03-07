package com.example.moon7.server_get_n_transform_exam;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_NAME="UPSO_NM";
    private static final String TAG_ADD="SITE_ADDR_ED";
    private static final String site="http://moon70717.dothome.co.kr/GetResInfo.php";
    String name,address;
    JSONArray Res=null;
    double x,y;
    Geocoder Geocoder;
    private GoogleMap mMap;
    LatLng mar;
    int flag;
    List<Address> list;
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Geocoder=new Geocoder(this, Locale.KOREA);
        marker= new MarkerOptions();
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        flag=0;
        mapFragment.getMapAsync(this);
        getData(site);
    }
    protected  void showWay(){
        try{
            JSONObject jsonObject=new JSONObject(myJSON);
            Res=jsonObject.getJSONArray(TAG_RESULTS);
            name=null;
            address=null;
            Log.d("d","json파일 받아옴");
            for (int i=0;i<Res.length();i++){//주소와 이름을 받아오고 주소는 좌표로 변환후 마커로 띄움
                JSONObject c=Res.getJSONObject(i);
                name=c.getString(TAG_NAME);//태그쪽에서 문제가 있는거 같음
                address=c.getString(TAG_ADD);
                Log.d("d","이름:"+name+"주소:"+address);
                getPath(address);
                Log.d("d","이름:"+name+"x:"+list.get(0).getLatitude()+"y:"+list.get(0).getLongitude());
                Marker(name);
                //여기서 마커띄우는곳으로 보냄

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                flag++;
                Log.d("time",flag+"번째");
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                        if (isCancelled()) break;
                    }
                    return sb.toString().trim();
                }catch(Exception e){
                    Log.d("d","버퍼 리더쪽에서 에러발생");
                    return null;
                }
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showWay();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void getPath(String Addr){

        x=0;
        y=0;
        list=null;
        try{
            list=Geocoder.getFromLocationName(Addr,10);
        }catch (IOException e){
            e.printStackTrace();
        }
        x=list.get(0).getLatitude();//여기서 이미 선언된걸 변경 못하는 문제발생
        y=list.get(0).getLongitude();
        Log.d("d","이름:"+name+"x:"+list.get(0).getLatitude()+"y:"+list.get(0).getLongitude());
    }

    void Marker(String name){//마커 추가가 않되
        /*mar=new LatLng(x,y);
        MarkerOptions marker=new MarkerOptions().position(mar);
        mMap.addMarker(marker);*/
        marker .position(new LatLng(x, y))
                .title(name)
                .snippet(address);
        mMap.addMarker(marker).showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap=googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.5462279,126.9798906),13));
    }
}
