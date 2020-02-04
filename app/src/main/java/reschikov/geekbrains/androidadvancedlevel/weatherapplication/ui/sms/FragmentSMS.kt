package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.sms

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.sender_frame.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.SenderFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity.MainActivity
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showMessage

class FragmentSMS : Fragment() {

    private val model: SenderViewModel by viewModel()
    private val smsManager by lazy { SmsManager.getDefault() }
    private val navController : NavController by lazy { findNavController() }
    private lateinit var binding: SenderFrameBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.sender_frame, container, false)
        binding.model = model
        if (savedInstanceState == null){
            arguments?.let {bundle -> bundle.getString(KEY_MESSAGE)?.let { model.setText(it) } }
        }
        setHasOptionsMenu(true)
        setAddRecipientNumber()
        activity?.let { (it as MainActivity).supportActionBar?.setTitle("Send SMS") }
        return binding.root
    }

    private fun setAddRecipientNumber(){
        binding.acivPhoneContact.setOnClickListener{
            startActivityForResult(Intent(Intent.ACTION_PICK).apply {
                type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            }, REQUEST_SELECT_PHONE_NUMBER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_SELECT_PHONE_NUMBER -> {
                takeIf { resultCode == Activity.RESULT_OK && data != null}?.let {
                    val contactUri = data?.data
                    contactUri?.let {uri ->
                        activity?.let {
                            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            it.contentResolver.query(uri, projection, null, null, null)
                                .use { cursor -> cursor?.let { it ->  it.takeIf {
                                    it.moveToFirst() }?.let {cursor ->
                                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    model.setNumberPhone(cursor.getString(numberIndex))
                                    isNotNumberPhone()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_sender, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home ->{
                navController.navigate(R.id.action_fragmentSMS_to_fragmentWeather)
                true
            }
            R.id.action_send -> {
                sendMessage()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendMessage(): Boolean {
        if (isNotNumberPhone()) return false
        if (binding.acetSms.text.isNullOrBlank()) {
            showMessage(acet_sms, "empty message", Color.RED)
            return false
        }
        checkPermissionSms()
        return true
    }

    private fun isNotNumberPhone(): Boolean {
        val phone = model.phone.get()
        if (phone.isNullOrBlank() || !phone.matches("\\+?\\d{11}".toRegex())){
            til_phone.error = "phone number without spaces or dashes"
            showMessage(til_phone, getString(R.string.enter_phone_number), Color.RED)
            return true
        }
        til_phone.error = null
        return false
    }

    private fun checkPermissionSms() {
        activity?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.SEND_SMS), REQUEST_MY_PERMISSIONS_SEND_SMS)
            } else showConfirmationDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_MY_PERMISSIONS_SEND_SMS -> {
                takeIf { grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED } ?.let {
                    showConfirmationDialog()
                }
            }
        }
    }

    private fun showConfirmationDialog(){
        activity?.let {
            AlertDialog.Builder(it, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                .setTitle("send SMS")
                .setIcon(R.drawable.ic_question)
                .setMessage("Send ${model.getNumberSMS()} SMS to subscriber ${binding.tveNumberPhone.text}?")
                .setCancelable(false)
                .setPositiveButton(R.string.ok){dialog, which ->
                    binding.acetSms.text?.let {text ->
                        binding.tveNumberPhone.text?.let {phone->
                            sendSms(text.toString(), phone.toString())
                            dialog.dismiss()
                        }
                    }
                }
                .setNegativeButton(R.string.cancel){dialog, which -> dialog.dismiss()}
                .create()
                .show()
        }
    }

    private fun sendSms(message :String, recipient: String ){
        val sent = Intent(ACTION_SENT_SMS)
        sent.putExtra(KEY_RECIPIENT, recipient)
        val delivered = Intent(ACTION_DELIVERED_SMS)
        delivered.putExtra(KEY_RECIPIENT, recipient)
        val sending = PendingIntent.getBroadcast(context,0, sent, 0)
        val reception = PendingIntent.getBroadcast(context, 0, delivered, 0)
        sendShortSMS(recipient, message, sending, reception)
    }

    private fun sendShortSMS(recipient: String, message: String, sending: PendingIntent, reception: PendingIntent){
        val number = model.getNumberSMS()
        val smsLength = message.length / number
        var start = 0
        var end: Int = smsLength
        repeat(number){
            smsManager.sendTextMessage(recipient, null, message.substring(start, end), sending, reception)
            start += smsLength
            end += smsLength
        }
    }
}