package com.example.newsfocus.tools;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/2/9.
 */

public class StringUtils {
    public static ArrayList<String> returnImageUrlsFromHtml(String str) {
        ArrayList<String> imgSrc = new ArrayList<String>();
        Matcher m = Pattern.compile("src=\"http?(.*?)(\"|>|\\s+)").matcher(str);
        while(m.find())
        {
            imgSrc.add("http" + m.group(1));
        }
        return imgSrc;
    }
}
