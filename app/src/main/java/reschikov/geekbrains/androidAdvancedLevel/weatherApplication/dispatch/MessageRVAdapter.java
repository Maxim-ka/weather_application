//package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dispatch;
//
//import android.os.Handler;
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.AppCompatEditText;
//import androidx.recyclerview.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import java.util.ArrayList;
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
//import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.myView.MyCardView;
//import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.MAX_SINGLE_BYTE_CHAR;
//
//public class MessageRVAdapter extends RecyclerView.Adapter<MessageRVAdapter.ViewHolder>{
//
//    private ArrayList<String> items;
//    private boolean isSms;
//    private int maxLenSms;
//
//    public ArrayList<String> getItems() {
//        if (isSms){
//            items = getSMS(items);
//            notifyDataSetChanged();
//        }
//        return items;
//    }
//
//    MessageRVAdapter(ArrayList<String> items, boolean isSms) {
//        this.items = items;
//        if (this.items.isEmpty()) this.items.add("");
//        this.isSms = isSms;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        String item = items.get(i);
//        viewHolder.bind(item);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (items == null) return 0;
//        return items.size();
//    }
//
//    private String getString (ArrayList<String> list){
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < list.size(); i++) {
//            stringBuilder.append(list.get(i));
//        }
//        list.clear();
//        list.trimToSize();
//        return stringBuilder.toString();
//    }
//
//    private ArrayList<String> getSMS(ArrayList<String> list){
//        String string;
//        if (list.size() != 1) string = getString(list);
//        else string = list.get(0);
//        list.clear();
//        list.trimToSize();
////        SmsManager smsManager = SmsManager.getDefault();
////        return smsManager.divideMessage(string);
//        maxLenSms = Rules.MAX_LEN_SMS_EN;
//        for (int i = 0; i < string.length(); i++) {
//            if (string.charAt(i) > MAX_SINGLE_BYTE_CHAR) {
//                maxLenSms = Rules.MAX_LEN_SMS_RU;
//                break;
//            }
//        }
//        int index = 0;
//        while (index < string.length()){
//            if (string.length() - index < maxLenSms) list.add(string.substring(index, string.length()));
//            else list.add(string.substring(index, index + maxLenSms));
//            index += maxLenSms;
//        }
//        return list;
//    }
//
//    void changeMessage(boolean isSms) {
//        this.isSms = isSms;
//        if (items.isEmpty()) return;
//        if (isSms) items = getSMS(items);
//        else items.add(getString(items));
//        notifyDataSetChanged();
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder{
//
//        private final AppCompatEditText message;
//        private int index;
//        private int insertIndex;
//        private boolean removed;
//        private boolean full;
//        private final TextWatcher tw = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!message.hasFocus()) return;
//                if (s != null && s.length() != 0) {
//                    if (isSms && s.length() >= maxLenSms) {
//                        full = true;
//                        message.clearFocus();
//                    }
//                } else if (items.size() > 1) {
//                    removed = true;
//                    message.clearFocus();
//                }
//            }
//        };
//
//        ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            message = itemView.findViewById(R.id.msg);
//            ((MyCardView)itemView).turnScreen();
//        }
//
//        void bind(String item){
//            message.setText(item);
//            removed = false;
//            full = false;
//            message.setOnFocusChangeListener((v, hasFocus) -> {
//                if (hasFocus) {
//                    message.addTextChangedListener(tw);
//                    index = items.indexOf(item);
//                } else {
//                    message.removeTextChangedListener(tw);
//                    if (removed){
//                        items.remove(index);
//                        new Handler().post(() ->notifyItemRemoved(index));
//                        return;
//                    }
//                    if (message.getText() != null){
//                        if (full){
//                            insertIndex = index + 1;
//                            String string = message.getText().toString();
//                            items.set(index, string.substring(0, maxLenSms));
//                            new Handler().post(() -> notifyItemChanged(index));
//                            String newStr = string.substring(maxLenSms);
//                            if (insertIndex >= items.size() || items.get(insertIndex).length() >= maxLenSms) {
//                                if (insertIndex >= items.size()) items.add(newStr);
//                                else items.add(insertIndex, newStr);
//                                new Handler().post(() ->notifyItemInserted(insertIndex));
//                            } else {
//                                items.set(insertIndex, newStr + items.get(insertIndex));
//                                new Handler().post(() -> notifyItemChanged(insertIndex));
//                            }
//                            return;
//                        }
//                        items.set(index, message.getText().toString());
//                        new Handler().post(() -> notifyItemChanged(index));
//                    }
//                }
//            });
//        }
//    }
//}
