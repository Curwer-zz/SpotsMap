package net.eray.ParkourPlayground;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Niclas on 2014-11-12.
 */
public class fetchImages extends AsyncTask<Integer, Void, Bitmap> {

    ParseImageView ImageView;
    String mObjID, idObject;
    List<ParseObject> parseObjects;
    ProgressBar loading;

    public fetchImages(ParseImageView ImageView, String objID, ProgressBar loading){
        super();
        mObjID = objID;
        this.ImageView = ImageView;
        this.loading = loading;

    }

    @Override
    protected Bitmap doInBackground(Integer... uri) {

        ParseQuery query = ParseQuery.getQuery("ImageFiles");
        ParseObject obj = ParseObject.createWithoutData("Map", mObjID);
        query.whereEqualTo("geoPosition", obj);
        try {
            parseObjects = query.find();
            idObject = parseObjects.get(uri[0]).getObjectId();
            ParseFile file =parseObjects.get(uri[0]).getParseFile("image");
            ImageView.setParseFile(file);
            ImageView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    loading.setVisibility(View.GONE);
                }
            });
            return null;
        } catch (ParseException e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap s) {
        super.onPostExecute(s);

    }
}
