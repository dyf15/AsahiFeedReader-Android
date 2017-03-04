package local.hal.st32.android.asahifeedreader45008;

import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *　XML解析
 */

public class FeedItemListFactory {

    private static final String DEBUG_TAG = "FeedItemListFactory";

    public static List<Map<String, String>> createFeedItemList(String xml) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(xml));
        parser.nextTag();
        List<Map<String, String>> items = parseFeed(parser);
        return items;
    }

    private static List<Map<String, String>> parseFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();

        parser.require(XmlPullParser.START_TAG, null, "rdf:RDF");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("item")) {
                Map<String, String> item = parseItem(parser);
                items.add(item);
            } else if (!name.equals("rdf:RDF") && !name.equals("channel rdf:about")) {
                skip(parser);
            }
        }
        return items;
    }

    private static Map<String, String> parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        Map<String, String> item = new HashMap<String, String>();

        parser.require(XmlPullParser.START_TAG, null, "item");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                String title = readText(parser, "title");
                item.put("title", title);
            } else if (name.equals("link")) {
                String link = readText(parser, "link");
                item.put("link", link);

            } else if (name.equals("description")) {
                String description = readText(parser, "description");
                item.put("description", description);

            } else if (name.equals("dc:date")) {
                String date = readText(parser, "dc:date");
                item.put("dc:date", date);

                String dateStr = "";

                //日付変換
                Date dt = replaceDate(date);

                //変換失敗してないなら
                if (dt != null)
                {
                    // 出力形式

                    SimpleDateFormat dfo = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                    // 出力日時(TimeZone=JST)
                    //dfo.setTimeZone(TimeZone.getTimeZone("JST"));
                    dateStr = dfo.format(dt);
                } else
                {
                    System.out.println("未対応書式 : " + date);
                }

                item.put("dateStr", dateStr);
            } else {
                skip(parser);
            }
        }
        return item;
    }

    private static String readText(XmlPullParser parser, String name) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, name);
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, name);
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();

        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }

        }
    }


    /**
     * 受け取ったStringを指定したDateフォーマットに変換
     * @param dateStr
     * @return 失敗時はnull
     *
     */
    private static Date replaceDate (String dateStr)
    {
        Date date = null;


        try
        {

            //パターンマッチ
            Pattern p = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):?([0-9]{0,2})([+-])([0-9]{2}):([0-9]{2})");
            Matcher m = p.matcher(dateStr);

            if (m.find())
            {
                //フォーマットを揃える　
                String replace = String.format("%s-%s-%sT%s:%s:%s%s%s%s",
                        m.group(1), //年
                        m.group(2), //月
                        m.group(3), //日
                        m.group(4), //時
                        m.group(5), //分
                        m.group(6).equals("") ? "00" : m.group(6), //秒
                        m.group(7), //[+/-]　時差
                        m.group(8), //時
                        m.group(9)); //分


                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

                date = formatter.parse(replace);

            }
            else
            {
                Log.e(DEBUG_TAG, dateStr + "　：　フォーマットがマッチしませんでした");
            }
        }
        catch (ParseException e)
        {
            // formatter.applyPattern();
            Log.e(DEBUG_TAG, dateStr + "の日時変換", e);
        }


        return date;

    }



}
