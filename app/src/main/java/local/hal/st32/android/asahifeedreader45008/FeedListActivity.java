package local.hal.st32.android.asahifeedreader45008;

import android.app.ListActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * ListView表示
 */
public class FeedListActivity extends ListActivity {

    //private ListView listView;
    //rssのURL
    private static final String FEED_URL = "http://rss.asahi.com/rss/asahi/newsheadlines.rdf";

    private List<Map<String,String>> _list;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);



        ListView lvFeedList = getListView();
        RestAccess access = new RestAccess(lvFeedList);
        access.execute(FEED_URL);


    }



    private class RestAccess extends AsyncTask<String, Void, List<Map<String,String>>>
    {
        private static final String DEBUG_TAG = "RestAccess";

        private ListView _lvFeedList = null;

        public RestAccess(ListView lvFeedList)
        {
            _lvFeedList = lvFeedList;
        }

        @Override
        public List<Map<String,String>> doInBackground(String... params)
        {
            String urlStr = params[0];

            HttpURLConnection con = null;
            InputStream is = null;
            List<Map<String,String>> result = null;

            try
            {
                URL url = new URL(urlStr);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                is = con.getInputStream();

                String xmlStr = is2String(is);
                result = FeedItemListFactory.createFeedItemList(xmlStr);
            }
            catch (MalformedURLException ex)
            {
                Log.e(DEBUG_TAG,"URL変換失敗",ex);

            }
            catch (IOException ex)
            {
                Log.e(DEBUG_TAG,"通信失敗",ex);
            }
            catch (XmlPullParserException ex)
            {
                Log.e(DEBUG_TAG,"xml解析失敗",ex);
            }
            finally
            {
                if (con != null)
                {
                    con.disconnect();
                }
                try
                {
                    if (is != null)
                    {
                        is.close();
                    }

                }
                catch (IOException ex)
                {
                    Log.e(DEBUG_TAG,"InputStream解析失敗",ex);
                }
            }
            return result;
        }

        @Override
        public void onPostExecute(List<Map<String,String>> result)
        {

            _list = result;
            String[] from = {"title","dateStr"};
            int[] to = {android.R.id.text1,android.R.id.text2};
            SimpleAdapter adapter = new SimpleAdapter(FeedListActivity.this,result,android.R.layout.simple_list_item_2,from,to);
            _lvFeedList.setAdapter(adapter);
        }

        private String is2String(InputStream is) throws IOException
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while (0 <= (line = reader.read(b)))
            {
                sb.append(b,0,line);
            }
            return sb.toString();

        }





    }
    @Override
    public void onListItemClick (ListView listView, View view, int position, long id)
    {


        Map<String,String> item = _list.get(position);
        String url = item.get("link");

        Intent intent = new Intent(FeedListActivity.this, ShowLinkPageActivity.class);

        //intent.putExtra("idNo",idNo);

        intent.putExtra("url",url);
        startActivity(intent);
    }




}
