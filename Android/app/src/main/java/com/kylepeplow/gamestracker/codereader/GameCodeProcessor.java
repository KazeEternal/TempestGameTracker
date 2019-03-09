package com.kylepeplow.gamestracker.codereader;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.vision.barcode.Barcode;
import com.kylepeplow.gamestracker.MainActivity;
import com.kylepeplow.gamestracker.data.Game;
import com.kylepeplow.gamestracker.itemadapters.GameListArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GameCodeProcessor implements  CodeReaderProcessor
{

    GameListArrayAdapter mGameListArrayAdapter =
            new GameListArrayAdapter(MainActivity.MAContext, new ArrayList<Game>());
    @Override
    public CodeType getCodeType() {
        return CodeType.CODE_1D;
    }

    @Override
    public void processFoundData(Barcode barcode) {
        if(barcode.format == Barcode.EAN_13)
        {
            Log.i("[GameCodeProcessor]", "Japanese Barcode Found");
        }
        Log.i("[GameCodeProcessor]", "Code Found: " + barcode.displayValue);
        //https://api.upcitemdb.com/prod/trial/lookup?upc=
        //Game newGameItem = new Game();
        //newGameItem.setName("Place Holder");
        //newGameItem.setCode(barcode.displayValue);

        Game newGameItem = getGameData(barcode.displayValue);
        if(newGameItem != null)
            mGameListArrayAdapter.add(newGameItem);
    }

    @Override
    public ArrayAdapter getArrayAdapter() {
        return mGameListArrayAdapter;
    }

    @Override
    public boolean validate(Barcode a, Barcode b) {
        return a.displayValue.equals(b.displayValue);
    }

    private Game getGameData(String upc)
    {
        Game retVal = null;
        try
        {
            //URL infoURL = new URL("https://api.upcitemdb.com/prod/trial/lookup?upc=" + upc);
            //URLConnection urlConnection = infoURL.openConnection();
            //InputStream is = urlConnection.getInputStream();
//
            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //StringBuilder json = new StringBuilder();
            //String line;
            //while((line = reader.readLine()) != null) {
            //    json.append(line);
            //}
//
//
            //Log.i("[JSON]", json.toString());
//
            //JSONObject jsonObject = new JSONObject(new JSONTokener(json.toString()));
            //int total = jsonObject.getInt("total");
            //if(total > 0)
            //{
            //    JSONArray items = jsonObject.getJSONArray("items");
            //    JSONObject item = (JSONObject)items.get(0);
//
//
            //    retVal = new Game();
            //    retVal.setName(item.getString("title"));
            //    retVal.setCode(upc);
            //}

            URL infoURL = new URL("https://www.pricecharting.com/api/products?t=c0b53bce27c1bdab90b1605249e600dc43dfd1d5&upc=" + upc);
            URLConnection urlConnection = infoURL.openConnection();
            InputStream is = urlConnection.getInputStream();
//
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder json = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                json.append(line);
            }

            Log.i("[JSON]", json.toString());

            JSONObject jsonObject = new JSONObject(new JSONTokener(json.toString()));

            JSONArray items = jsonObject.getJSONArray("products");
            if(items.length() > 0) {
                JSONObject item = (JSONObject) items.get(0);

                retVal = new Game();
                retVal.setName(item.getString("product-name"));
                retVal.setCode(item.getString("console-name"));
            }
        }
        catch (MalformedURLException ex)
        {
            Log.e("[GET GAME DATA]", ex.getMessage());
        }
        catch(IOException ex)
        {
            Log.e("[GET GAME DATA]", ex.getLocalizedMessage());
        }
        catch(JSONException ex)
        {
            Log.e("[GET GAME DATA]", ex.getLocalizedMessage());
        }
        return retVal;
    }
}
