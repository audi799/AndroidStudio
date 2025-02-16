package kr.gimaek.loader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebController {
    private final String TAG = "WebController";
    public Result excute(String sql){
        return connect(sql, "e");
    }

    public Result get(String sql){
        return connect(sql, "s");
    }

    private Result connect(String sql, String mode){
        if(sql.equals(""))
            return null;

        JSONObject content = new JSONObject();
        try {
            content.put("Q", sql);
            content.put("S", "BLADEDB1");
            content.put("C", "GJ_A38");
        } catch (JSONException e) {
            return null;
        }

        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL("http://211.105.106.69:10010/api/"+mode);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            String params = content.toString();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(params.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            httpURLConnection.getInputStream(),
                            "UTF-8"
                    )
            );
            String line;
            String page = "";
            while((line = bufferedReader.readLine()) != null){
                page += line;
            }

            return new Result(page);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return new Result("");
    }

    public class Result{
        private boolean Result = false;
        private JSONArray Data;

        public Result(String jsonStr){
            try {
                JSONObject jObject = new JSONObject(jsonStr);
                if(jObject.getString("Result").equals("SUCC")){
                    this.Result = true;
                    Data = (JSONArray) jObject.get("Msg");
                }else{
                    Log.e(TAG, jObject.get("Msg").toString());
                    this.Result = false;
                }
            } catch (JSONException e) {
                this.Result = false;
                e.printStackTrace();
            }
        }

        public boolean isSuccessed(){
            return this.Result;
        }

        public JSONArray getData(){
            return this.Data;
        }
    }
}
