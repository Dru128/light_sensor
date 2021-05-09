package com.example.sensor

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment


class LightLevelFragment : Fragment() // Шайдуров Андрей
{
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    private lateinit var light_text: TextView
    private lateinit var max_edit_text: EditText
    private lateinit var min_edit_text: EditText
    private lateinit var imageView: ImageView
    private lateinit var lighting_square: View
    private lateinit var blackout_square: View

    private var maxLight: Int = 10000
    private var minLight: Int = 3000
    private var currentLight: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val root = inflater.inflate(R.layout.light_level_fragment, container, false)

        root.apply {
            max_edit_text = findViewById<EditText>(R.id.max_light_edit_text)
            min_edit_text = findViewById<EditText>(R.id.min_light_edit_text)
            light_text = findViewById<TextView>(R.id.progress_light_text)
            imageView = findViewById<ImageView>(R.id.imageView)
            lighting_square = findViewById<View>(R.id.lighting)
            blackout_square = findViewById<View>(R.id.blackout)
            findViewById<Button>(R.id.save_cur_light_max_button).setOnClickListener {
                maxLight = currentLight!!
                max_edit_text.setText(maxLight.toString())
            }
            findViewById<Button>(R.id.save_cur_light_min_button).setOnClickListener {
                minLight = currentLight!!
                min_edit_text.setText(minLight.toString())
            }

            sensorManager = context.getSystemService(Application.SENSOR_SERVICE) as SensorManager
        }
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor == null)
        { // устройство не имеет датчика освещенности


        }
        else
        {
            max_edit_text.doAfterTextChanged { if (it != null && it.isNotEmpty()) maxLight = it.toString().toInt() }
            min_edit_text.doAfterTextChanged { if (it != null && it.isNotEmpty()) minLight = it.toString().toInt() }
        }
        return root
    }

    private var listenerLight: SensorEventListener = object : SensorEventListener
    {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent)
        {
            currentLight = event.values[0].toInt()
            light_text.text = "текущая яркость: $currentLight lux"
            updateLight(currentLight!!)
        }
    }

    override fun onResume()
    {
        super.onResume()
        sensorManager.registerListener(listenerLight, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause()
    {
        super.onPause()
        sensorManager.unregisterListener(listenerLight)
    }

    private fun updateLight(light: Int)
    {
        when
        {
            light > maxLight -> // нужно затемнить
            {
                imageView.setImageResource(R.drawable.umbrella)
                blackout_square.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                lighting_square.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            light < minLight -> // нужно включить подсветку
            {
                imageView.setImageResource(R.drawable.lamp)
                blackout_square.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
                lighting_square.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            light in minLight..maxLight -> // нормальный уровень яркости
            {
                imageView.setImageBitmap(null)
                blackout_square.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
                lighting_square.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }
    }

}