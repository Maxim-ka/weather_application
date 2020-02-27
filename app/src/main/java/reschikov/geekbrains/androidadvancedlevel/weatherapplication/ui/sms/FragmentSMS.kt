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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.sender_frame.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.SenderFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity.MainActivity
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showMessage

private const val REQUEST_SELECT_PHONE_NUMBER = 2
private const val REQUEST_MY_PERMISSIONS_SEND_SMS = 1

class FragmentSMS : Fragment() {

    private val model: SenderViewModel by viewModel()
    private val smsManager by lazy { SmsManager.getDefault() }
    private var snackbar: Snackbar? = null
    private var binding: SenderFrameBinding? = null
    private val errorPhone:(Boolean) -> Unit = {isPhone: Boolean ->
        isPhone.takeUnless{it} ?.let {
            til_phone.error = getString(R.string.err_only_numbers)
            snackbar = showMessage(til_phone, getString(R.string.enter_phone_number), Color.RED)
        } ?: run{
            til_phone.error = null
            closeShownMessage()
        }
    }

    private fun closeShownMessage(){
        snackbar?.dismiss()
        snackbar = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.sender_frame, container, false)
        binding?.model = model
        if (savedInstanceState == null){
            arguments?.let {bundle -> bundle.getString(KEY_MESSAGE)?.let { model.setText(it) } }
        }
        setHasOptionsMenu(true)
        activity?.let { (it as MainActivity).supportActionBar?.setTitle(getString(R.string.title_send_sms)) }
        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        setAddRecipientNumber()
    }

    private fun setAddRecipientNumber(){
        binding?.acivPhoneContact?.setOnClickListener{
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
                                        model.setPhone(cursor.getString(numberIndex), errorPhone)
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
            R.id.action_send -> {
                sendMessage()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendMessage(): Boolean {
        if (!model.hasPhone(errorPhone)) return false
        if (binding?.acetSms?.text.isNullOrBlank()) {
            snackbar = showMessage(acet_sms, getString(R.string.empty_message), Color.RED)
            return false
        }
        checkPermissionSms()
        closeShownMessage()
        return true
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
                .setTitle(getString(R.string.title_send_SMS))
                .setIcon(R.drawable.ic_question)
                .setMessage("${getString(R.string.send)} ${model.getNumberSMS()}" +
                    " ${getString(R.string.sms_subscriber)} ${binding?.tveNumberPhone?.text} ?")
                .setCancelable(false)
                .setPositiveButton(R.string.but_ok){ dialog, which ->
                    binding?.acetSms?.text?.let {text ->
                        binding?.tveNumberPhone?.text?.let {phone->
                            sendSms(text.toString(), phone.toString())
                            dialog.dismiss()
                        }
                    }
                }
                .setNegativeButton(R.string.but_cancel){ dialog, which -> dialog.dismiss()}
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

    override fun onStop() {
        super.onStop()
        binding?.acivPhoneContact?.setOnClickListener(null)
        closeShownMessage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.let {
            it.model = null
            binding = null
        }
    }
}