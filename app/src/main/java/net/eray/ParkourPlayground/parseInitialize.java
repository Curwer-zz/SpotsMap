package net.eray.ParkourPlayground;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Niclas on 2014-09-02.
 */
public class parseInitialize extends Application {

    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "DY1jJ8GSFdfEqi3Z1SyRAAcKiQmxt1ZNJM8I6pos", "UgEM6YE9ze3C2TjVDAFSY6QSTl8YH4X1woSYmGFZ");
    }
}
