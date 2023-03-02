package com.example.hinvas

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class ScheduleFragment : Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_schedule, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setWholeViewHeight(
            wholeViewHeight = view.findViewById<LinearLayout>(R.id.viewSize).height,
            mainViewLayout = view.findViewById<LinearLayout>(R.id.mainView),
        )

        val calendarView = view.findViewById<CalendarView>(R.id.calendar)
        calendarView.date = System.currentTimeMillis()


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val displayMonth = month + 1
            val date = "$year/$displayMonth/$dayOfMonth"

            val dateText = view.findViewById<TextView>(R.id.date_text)
            dateText.text = date

        }
    }
}