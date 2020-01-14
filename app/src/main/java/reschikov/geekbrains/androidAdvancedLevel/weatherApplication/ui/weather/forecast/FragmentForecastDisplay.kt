package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.ForecastFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding.DataBindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel

class FragmentForecastDisplay : Fragment() {

    private val model: WeatherViewModel by sharedViewModel()
    private val dataBindingAdapter by inject<DataBindingAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: ForecastFrameBinding = DataBindingUtil.inflate(inflater, R.layout.forecast_frame, container, false, dataBindingAdapter)
        binding.model = model
        binding.rvItems.adapter = ForecastRVAdapter(dataBindingAdapter)
        binding.rvItems.setHasFixedSize(true)
        return binding.rvItems.rootView
    }

//    override fun collectData(context: Context, response: ServerResponse): String {
//        val location = response.getLocation()
//        val forecast = response.getForecast()
//        val forecastdayList = forecast.getForecastday()
//        val format_24 = SimpleDateFormat(context.getString(R.string.pattern_H_mm), Locale.getDefault())
//        val format_12 = SimpleDateFormat(context.getString(R.string.pattern_h_mm_a), Locale.US)
//        val sb = StringBuilder()
//        for (i in forecastdayList.indices) {
//            try {
//                sb.append(context.getString(R.string.city)).append(location.getName()).append("\n")
//                        .append(context.getString(R.string.region)).append(location.getRegion()).append("\n")
//                        .append(context.getString(R.string.day)).append(DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(Date(forecastdayList.get(i).getDateEpoch() * Rules.IN_MILLISEC))).append("\n")
//                        .append(context.getString(R.string.state)).append(forecastdayList.get(i).getDay().getCondition().getText()).append("\n")
//                        .append(context.getString(R.string.min_temp)).append(forecastdayList.get(i).getDay().getMintempC()).append(Rules.C).append("\n")
//                        .append(context.getString(R.string.max_temp)).append(forecastdayList.get(i).getDay().getMaxtempC()).append(Rules.C).append("\n")
//                        .append(context.getString(R.string.max_wind)).append(String.format(Locale.getDefault(), Rules.`$_2F_$S`, (forecastdayList.get(i).getDay().getMaxwindKph() * Rules.KMH_IN_MC) as Float, context.getString(R.string.m_c))).append("\n")
//                        .append(context.getString(R.string.aver_humidity)).append(forecastdayList.get(i).getDay().getAvghumidity()).append(context.getString(R.string.pct)).append("\n")
//                        .append(context.getString(R.string.rainfall)).append(forecastdayList.get(i).getDay().getTotalprecipMm()).append(context.getString(R.string.mm)).append("\n")
//                        .append(context.getString(R.string.sunrise)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getSunrise())!!)).append("\n")
//                        .append(context.getString(R.string.sunset)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getSunset())!!)).append("\n")
//                        .append(context.getString(R.string.moonrise)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getMoonrise())!!)).append("\n")
//                        .append(context.getString(R.string.moonset)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getMoonset())!!)).append("\n\n")
//            } catch (e: ParseException) {
//                sb.append("\n\n")
//                Log.e("ParseException", e.message)
//            }
//
//        }
//        return sb.toString()
//    }


}
