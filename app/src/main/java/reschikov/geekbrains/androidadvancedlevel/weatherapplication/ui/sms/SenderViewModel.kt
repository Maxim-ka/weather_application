package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.sms

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val SINGLE_BYTE_CHAR = 127
private const val LEN_SMS_EN = 160
private const val LEN_SMS_RU = 70
private const val TELEPHONE_NUMBER = "\\+?\\d{11}"

class SenderViewModel : ViewModel() {

    val counter = ObservableField<String>()
    val phone = ObservableField<String>()
    val text = ObservableField<String>()

    private val textCallback = object : Observable.OnPropertyChangedCallback(){
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            text.get()?.let {
                viewModelScope.launch(Dispatchers.Default) {
                    determinePossibleLengthOfSMS(it)
                    countNumberOfSms(it)
                }
            }
        }
    }
    private val isPhone = {string: String? -> !string.isNullOrBlank() &&
            string.matches(TELEPHONE_NUMBER.toRegex())}
    private var maxLenSms = LEN_SMS_EN
    private var number: Int = 0

    init {
        text.addOnPropertyChangedCallback(textCallback)
    }

    fun getNumberSMS(): Int = number

    fun setText(string: String){
        text.set(string)
    }

    fun setPhone(phone: String?, errorPhone: (Boolean) -> Unit ){
        this.phone.set(phone)
        errorPhone.invoke(isPhone.invoke(phone))
    }

    fun hasPhone(errorPhone: (Boolean) -> Unit): Boolean {
        val isNumberPhone = isPhone.invoke(phone.get())
        errorPhone.invoke(isNumberPhone)
        return isNumberPhone
    }

    private fun determinePossibleLengthOfSMS(string: String){
        maxLenSms = LEN_SMS_EN
        for (char in string) {
            if (char.toInt() > SINGLE_BYTE_CHAR) {
                maxLenSms = LEN_SMS_RU
                return
            }
        }
    }

    private fun countNumberOfSms(string: String){
        var num = string.length / maxLenSms
        if (string.length % maxLenSms != 0) ++num
        if (num != number){
            number = num
            counter.set("$number")
        }
    }

    override fun onCleared() {
        super.onCleared()
        text.removeOnPropertyChangedCallback(textCallback)
    }
}