package com.example.finalproject1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GamerAdapter extends ArrayAdapter<AdvancedGamer> {

    public GamerAdapter(@NonNull Context context, ArrayList<AdvancedGamer> gamers) {
        super(context, 0, gamers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        AdvancedGamer gamer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list, parent, false);
        }
        ImageView iv= convertView.findViewById(R.id.list_image);
        TextView name=convertView.findViewById(R.id.list_name);
        TextView email=convertView.findViewById(R.id.list_email);
        TextView password=convertView.findViewById(R.id.list_password);
        TextView gold=convertView.findViewById(R.id.list_gold);
        TextView level=convertView.findViewById(R.id.list_level);
        TextView weapon=convertView.findViewById(R.id.list_weapon);
        TextView monster=convertView.findViewById(R.id.list_monster);
        TextView gamerType =convertView.findViewById(R.id.gamerType);

        name.setText("Name: "+gamer.getName());
        email.setText("Email: "+gamer.getEmail());
        password.setText("Password: "+gamer.getPassword());
        gold.setText("Gold: "+(gamer.getGold()));
        level.setText("Level: "+(gamer.getLevel()));
        weapon.setText("Weapon: "+gamer.getWeapon().toString());
        monster.setText("Monster: "+gamer.getMonster().toString());
        gamerType.setText("Other Gamer");

        if(gamer.getPath() != null && gamer.getAccountCheck()) {
            Resources res = convertView.getResources();
            Bitmap bitmap = BitmapFactory.decodeFile(gamer.getPath());
            BitmapDrawable bd = new BitmapDrawable(res, bitmap);
            if(bitmap != null)
                iv.setBackground(bd);
            gamerType.setText("You!");
        }


        return convertView;
    }

}
