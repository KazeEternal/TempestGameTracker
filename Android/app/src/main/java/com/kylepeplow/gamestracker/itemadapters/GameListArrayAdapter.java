package com.kylepeplow.gamestracker.itemadapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kylepeplow.gamestracker.R;
import com.kylepeplow.gamestracker.data.Game;

import java.util.ArrayList;
import java.util.List;

public class GameListArrayAdapter extends ArrayAdapter<Game>
{
    private Context mContext;
    private List<Game> mGames;
    public GameListArrayAdapter(@NonNull Context context, @NonNull List<Game> objects) {
        super(context, R.layout.games_list_item_layout, objects);
        mContext = context;
        mGames = objects;
        setNotifyOnChange(true);
    }

    @Override
    public int getCount() {
        return mGames.size();
    }

    @Override
    public Game getItem(int pos) {
        return mGames.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.games_list_item_layout, parent, false);
        TextView gameNameView = (TextView) rowView.findViewById(R.id.gameNameText);
        TextView barcodeView = (TextView) rowView.findViewById(R.id.barcodeText);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.cover_icon);

        Game currentGame = mGames.get(position);
        gameNameView.setText(currentGame.getName());
        barcodeView.setText(currentGame.getCode());
        //barcodeView.setText(values[position]);
        // change the icon for Windows and iPhone
        //String s = values[position];
        imageView.setImageResource(R.drawable.software);

        return rowView;
    }
}
