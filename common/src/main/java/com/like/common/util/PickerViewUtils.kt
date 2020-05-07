package com.like.common.util

import android.content.Context
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import java.util.*

object PickerViewUtils {

    fun showStringOptionsPickerView(context: Context, list: List<String>, selectedPosition: Int = -1, onPick: (Int) -> Unit) {
        if (list.isEmpty()) return
        val optionsPickerView: OptionsPickerView<String> = OptionsPickerBuilder(context,
                OnOptionsSelectListener { options1, options2, options3, v ->
                    onPick(options1)
                }).build<String>()
        optionsPickerView.setPicker(list)
        optionsPickerView.setSelectOptions(selectedPosition)
        optionsPickerView.show()
    }

    fun showTimePickerViewYear(context: Context, selectYear: String = "", onPick: (String) -> Unit) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectYear.toIntOrNull()?.let {
            selectedDate.set(it, 1, 1)
        }

//        val startDate: Calendar = Calendar.getInstance()
//        val endDate: Calendar = Calendar.getInstance()
        //正确设置方式 原因：注意事项有说明
//        startDate.set(2010, 0, 1)
//        endDate.set(2050, 12, 31)

        TimePickerBuilder(context, OnTimeSelectListener { date, v ->
            onPick(DateUtils.getYear(date.time).toString())
        })
                .setType(booleanArrayOf(true, false, false, false, false, false)) // 默认全部显示
//            .setCancelText("Cancel") //取消按钮文字
//            .setSubmitText("Sure") //确认按钮文字
//            .setContentTextSize(18) //滚轮文字大小
//            .setTitleSize(20) //标题文字大小
//            .setTitleText("Title") //标题文字
//            .setOutSideCancelable(false) //点击屏幕，点在控件外部范围时，是否取消显示
//            .isCyclic(true) //是否循环滚动
//            .setTitleColor(Color.BLACK) //标题文字颜色
//            .setSubmitColor(Color.BLUE) //确定按钮文字颜色
//            .setCancelColor(Color.BLUE) //取消按钮文字颜色
//            .setTitleBgColor(-0x99999a) //标题背景颜色 Night mode
//            .setBgColor(-0xcccccd) //滚轮背景颜色 Night mode
//            .setRangDate(startDate, endDate) //起始终止年月日设定
//            .setLabel("年", "月", "日", "时", "分", "秒") //默认设置为年月日时分秒
//            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//            .isDialog(true) //是否显示为对话框样式
                .setDate(selectedDate) // 如果不设置的话，默认是系统时间*/
                .build()
                .show()
    }

    fun showTimePickerViewYearMonthDay(context: Context, selectDate: String = "", format: String = "yyyy-MM-dd", onPick: (String) -> Unit) {
        val selectedDate: Calendar = Calendar.getInstance()
        DateUtils.format(selectDate, format)?.time?.let {
            selectedDate.set(DateUtils.getYear(it), DateUtils.getMonth(it) - 1, DateUtils.getDay(it))
        }

        TimePickerBuilder(context, OnTimeSelectListener { date, v ->
            onPick(DateUtils.format(date, format))
        })
                .setType(booleanArrayOf(true, true, true, false, false, false)) // 默认全部显示
                .setLabel("年", "月", "日", "", "", "") //默认设置为年月日时分秒
                .setDate(selectedDate)
                .build()
                .show()
    }

}

fun TextView.showStringOptionsPickerView(list: List<String>?, onPick: ((Int) -> Unit)? = null) {
    if (list.isNullOrEmpty()) return
    val selectedPosition = list.indexOf(this.text)
    PickerViewUtils.showStringOptionsPickerView(
            this.context,
            list,
            selectedPosition
    ) { position ->
        this.text = list[position]
        onPick?.invoke(position)
    }
}

fun TextView.setOnClickShowStringOptionsPickerView(list: List<String>?, onPick: ((Int) -> Unit)? = null) {
    this.setOnClickListener {
        this.showStringOptionsPickerView(list, onPick)
    }
}

fun TextView.showTimePickerViewYear(onPick: ((String) -> Unit)? = null) {
    PickerViewUtils.showTimePickerViewYear(
            this.context,
            this.text.toString()
    ) { year ->
        this.text = year
        onPick?.invoke(year)
    }
}

fun TextView.setOnClickShowTimePickerViewYear(onPick: ((String) -> Unit)? = null) {
    this.setOnClickListener {
        this.showTimePickerViewYear(onPick)
    }
}

fun TextView.showTimePickerViewYearMonthDay(format: String = "yyyy-MM-dd", onPick: ((String) -> Unit)? = null) {
    PickerViewUtils.showTimePickerViewYearMonthDay(
            this.context,
            this.text.toString(),
            format
    ) { date ->
        this.text = date
        onPick?.invoke(date)
    }
}

fun TextView.setOnClickShowTimePickerViewYearMonthDay(format: String = "yyyy-MM-dd", onPick: ((String) -> Unit)? = null) {
    this.setOnClickListener {
        this.showTimePickerViewYearMonthDay(format, onPick)
    }
}
