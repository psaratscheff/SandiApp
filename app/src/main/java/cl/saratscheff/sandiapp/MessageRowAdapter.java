package cl.saratscheff.sandiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
            data.add(msg);
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
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.row_message_complaint, null);
            TextView textViewSender = (TextView) vi.findViewById(R.id.textViewSender);
            TextView textViewMessage = (TextView) vi.findViewById(R.id.textViewMessage);
            textViewSender.setText(data.get(position).getAuthor());
            textViewMessage.setText(data.get(position).getContent());
            return vi;
        }
    }