package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dispatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.NoticeSms;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.SenderService;

import static android.text.InputType.TYPE_CLASS_PHONE;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;

public class FragmentShippingControl extends Fragment implements Sending, View.OnClickListener {

    public interface Transferable {
        void changeTitle (String title);
    }

    public interface Settable {
        void setDisplayMode(boolean isSMS);
        void setNestedScrollingRecyclerView(boolean set);
        ArrayList<String> getMessages();
    }

    public static FragmentShippingControl newInstance(ArrayList<String> listMessage){
        FragmentShippingControl fragment = new FragmentShippingControl();
        Bundle args = new Bundle();
        args.putStringArrayList(Rules.KEY_LIST, listMessage);
        fragment.setArguments(args);
        return fragment;
    }

    private TextInputEditText inputText;
    private boolean isSMS;
    private boolean isLetter;
    private boolean isLocked;
    private AppBarLayout appBarLayout;
    private TextInputEditText fromTextInput;
    private TextInputEditText passwordTextInput;
    private TextInputEditText topicTextInput;
    private Transferable transferableTitle;
    private Settable settable;
    private String title;
    private ImageButton butAddPhone;
    private ArrayList<String> messages;
    private String recipient;
    private MenuItem lock;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sender_frame, container, false);
        inputText = view.findViewById(R.id.input_text);
        butAddPhone = view.findViewById(R.id.add_phone_contact);
        butAddPhone.setOnClickListener(this);
        if (savedInstanceState == null){
            if (getArguments() != null){
                ArrayList<String> list = getArguments().getStringArrayList(Rules.KEY_LIST);
                if (list != null && list.isEmpty()){
                    isLetter = true;
                    isSMS = false;
                }
                getChildFragmentManager().beginTransaction()
                    .replace(R.id.list, FragmentListMessage.newInstance(list))
                    .commit();
            }
        }
        if (getActivity() != null){
            ((Sender)getActivity()).setSending(this);
            appBarLayout = getActivity().findViewById(R.id.bar);
            fromTextInput = getActivity().findViewById(R.id.from);
            passwordTextInput = getActivity().findViewById(R.id.password);
            topicTextInput = getActivity().findViewById(R.id.topic);
        }
        setHasOptionsMenu(true);
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sms:
                prohibitDragging(defineHow(true));
                transferableTitle.changeTitle(getString(R.string.send_as_SMS));
                inputText.setHint(getString(R.string.enter_the_phone_of_the_recipient));
                inputText.setInputType(TYPE_CLASS_PHONE);
                butAddPhone.setVisibility(View.VISIBLE);
                return true;
            case R.id.email:
                prohibitDragging(defineHow(false));
                transferableTitle.changeTitle(getString(R.string.send_by_email));
                if (inputText.getText() != null) inputText.setText(null);
                inputText.setHint(getString(R.string.recipient_email));
                inputText.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                butAddPhone.setVisibility(View.GONE);
                return true;
            case R.id.lock:
                isLocked = !isLocked;
                repaintMenuItem(lock);
                transferableTitle.changeTitle(getString(R.string.send_by_email));
                butAddPhone.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (getActivity() != null) getActivity().startActivityForResult(intent, Rules.REQUEST_SELECT_PHONE_NUMBER);
    }

    private boolean defineHow(boolean isSms){
        isSMS = isSms;
        settable.setDisplayMode(isSms);
        return isSms;
    }

    private void prohibitDragging(final boolean set){
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior  behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null){
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return !set;
                }
            });
        }
        settable.setNestedScrollingRecyclerView(set);
        appBarLayout.setExpanded(!set);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.main_sender, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem sms = menu.findItem(R.id.sms);
        MenuItem email = menu.findItem(R.id.email);
        lock = menu.findItem(R.id.lock);
        sms.setVisible(!isLetter);
        sms.setEnabled(!isLetter);
        email.setVisible(!isLetter);
        email.setEnabled(!isLetter);
        if (isLetter){
            repaintMenuItem(lock);
            lock.setVisible(true);
            lock.setEnabled(true);
        } else {
            sms.setChecked(isSMS);
            email.setChecked(!isSMS);
            prohibitDragging(isSMS);
        }
        transferableTitle.changeTitle(title);
    }

    private void repaintMenuItem(MenuItem lock){
        if (isLocked) lock.setIcon(R.drawable.ic_action_lock);
        else lock.setIcon(R.drawable.ic_action_unlock);
        prohibitDragging(isLocked);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putBoolean(Rules.KEY_IS_SMS, isSMS);
        saveInstanceState.putBoolean(Rules.KEY_LETTER, isLetter);
        saveInstanceState.putBoolean(Rules.KEY_LOCK, isLocked);
        saveInstanceState.putStringArrayList(Rules.KEY_MESSAGES, messages);
        saveInstanceState.putString(Rules.KEY_RECIPIENT, recipient);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        transferableTitle = (Transferable) context;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        settable = (Settable) childFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            isSMS = savedInstanceState.getBoolean(Rules.KEY_IS_SMS);
            isLetter = savedInstanceState.getBoolean(Rules.KEY_LETTER);
            isLocked = savedInstanceState.getBoolean(Rules.KEY_LOCK);
            messages = savedInstanceState.getStringArrayList(Rules.KEY_MESSAGES);
            recipient = savedInstanceState.getString(Rules.KEY_RECIPIENT);
        }
        if (isSMS) {
            inputText.setHint(getString(R.string.enter_the_phone_of_the_recipient));
            title = getString(R.string.send_as_SMS);
            butAddPhone.setVisibility(View.VISIBLE);
        }else {
            if (isLetter) inputText.setText(getString(R.string.email_author));
            else inputText.setHint(getString(R.string.recipient_email));
            makeUnavailable(isLetter);
            title = getString(R.string.send_by_email);
            fromTextInput.setText(preferences.getString(Rules.KEY_FROM, null));
            butAddPhone.setVisibility(View.GONE);
        }
    }

    private void makeUnavailable(boolean yes){
        inputText.setEnabled(!yes);
        inputText.setClickable(!yes);
        inputText.setSelected(!yes);
        inputText.setFocusable(!yes);
        inputText.setFocusableInTouchMode(!yes);
        inputText.setFreezesText(yes);
    }

    @Override
    public void sendMessage() {
        if (inputText.getText() != null){
            recipient = inputText.getText().toString().trim();
            if (recipient.isEmpty()){
                Toast.makeText(getContext(), getString(R.string.fill_contact), Toast.LENGTH_LONG).show();
                return;
            }
            messages = settable.getMessages();
            if (isSMS){
                if (recipient.matches(getString(R.string.numbers))) sendSms();
                else Toast.makeText(getContext(), getString(R.string.enter_phone_number), Toast.LENGTH_LONG).show();
            } else {
                if (recipient.matches(getString(R.string.reg_email))){
                    Intent intent = checkInputFields();
                    if (intent != null){
                        intent.putExtra(Rules.KEY_RECIPIENT, recipient);
                        intent.putExtra(Rules.KEY_MESSAGES, messages);
                        if (getActivity() != null){
                            getActivity().startService(intent);
                        }
                    }
                } else Toast.makeText(getContext(), getString(R.string.enter_recipient_email), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void getPhone(String phone) {
        if (isSMS) inputText.setText(phone);
    }

    @Override
    public void send() {
        if (getActivity() != null) NoticeSms.newInstance(recipient, messages).show(getActivity().getSupportFragmentManager(), Rules.TAG_SEND_SMS);
    }

    private void openCurtain(){
        if (isLetter)  {
            if (isLocked) isLocked = false;
            repaintMenuItem(lock);
        }
        Toast.makeText(getContext(), getString(R.string.not_all_fields_filled), Toast.LENGTH_LONG).show();
    }

    private Intent checkInputFields(){
        if (fromTextInput.getText() == null || topicTextInput.getText() == null || passwordTextInput.getText() == null){
            openCurtain();
            return null;
        }
        String from = fromTextInput.getText().toString().trim();
        String topic = topicTextInput.getText().toString().trim();
        String password = passwordTextInput.getText().toString().trim();
        if (from.isEmpty() || topic.isEmpty() || password.isEmpty()) {
            openCurtain();
            return null;
        }
        if (!from.matches(getString(R.string.reg_email))){
            Toast.makeText(getContext(), getString(R.string.enter_your_email), Toast.LENGTH_LONG).show();
            return null;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Rules.KEY_FROM, from);
        editor.apply();
        Intent send = new Intent(getContext(), SenderService.class);
        send.putExtra(Rules.KEY_FROM, from);
        send.putExtra(Rules.KEY_TOPIC, topic);
        send.putExtra(Rules.KEY_PASSWORD, password);
        return send;
    }

    private void sendSms(){
        if (getActivity() != null){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, Rules.MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else send();
        }
    }
}
