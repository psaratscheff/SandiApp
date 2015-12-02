package cl.saratscheff.sandiapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by psara on 02-11-2015.
 */
class MessageRowAdapter extends BaseAdapter {

        Context context;
        ArrayList<MessageClass> data;
        private static LayoutInflater inflater = null;

        public MessageRowAdapter(Context context) {
            // TODO Auto-generated constructor stub
            this.context = context;
            // this.data = data;
            data = new ArrayList<MessageClass>();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void add(MessageClass msg) {
            final MessageRowAdapter t = this;
            data.add(0, msg); // Agrego al inicio del array el siguiente mensaje
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null)
                convertView = inflater.inflate(R.layout.row_message_complaint, null);
            TextView textViewSender = (TextView) convertView.findViewById(R.id.textViewSender);
            TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
            TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
            textViewSender.setText(data.get(position).getAuthor());
            textViewMessage.setText(data.get(position).getContent());
            textViewDate.setText(getDate(data.get(position).getCreatedAt()));
            return convertView;
        }

        private String getDate(long time) {
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(time);
            String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
            return date;
        }
    }