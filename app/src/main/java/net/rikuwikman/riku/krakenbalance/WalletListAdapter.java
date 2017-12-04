package net.rikuwikman.riku.krakenbalance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WalletListAdapter extends ArrayAdapter<HashMap<String, String>> {
    private CopyOnWriteArrayList<HashMap<String, String>> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView currencyText;
        TextView amountText;
        TextView amountConvertedText;
    }

    public WalletListAdapter(CopyOnWriteArrayList<HashMap<String, String>> data, Context context) {
        super(context, R.layout.wallet_row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String, String> balance = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.wallet_row_item, parent, false);
            viewHolder.currencyText =  convertView.findViewById(R.id.currency);
            viewHolder.amountText = convertView.findViewById(R.id.amount);
            viewHolder.amountConvertedText = convertView.findViewById(R.id.amount_converted);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.currencyText.setText(balance.get("currency"));
        viewHolder.amountText.setText(balance.get("amount"));
        viewHolder.amountConvertedText.setText(balance.get("amount_converted"));

        return convertView;
    }
}
