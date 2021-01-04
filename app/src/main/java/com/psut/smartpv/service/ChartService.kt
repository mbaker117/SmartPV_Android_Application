package com.psut.smartpv.service

import android.content.Context

import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle


class ChartService {

    companion object {
        private var chartService = ChartService()
        private const val BACKGROUND_COLOR = "#0f384d"
        private const val TITLE_COLOR = "#FFFFFF"
        private const val SUB_TITLE_COLOR = "#FFFFFF"
        private const val AXES_TEXT_COLOR = "#FFFFFF"
        private const val LABEL_TEXT_COLOR = "#FFFFFF"
        private const val LABEL_TEXT_OUTLINE_COLOR = "#00000000"
        private val COLORS_THEME:Array<Any> = arrayOf("#3ba9fd", "#F44336", "#d11b5f", "#facd32","#0c457d", "#5f8c4a","#e42643")

        private const val DATA_KEY = "data"
        private const val CAT_KEY = "cat"
        private const val CHART_TYPE_KEY = "chartType"
        private const val TITLE_KEY = "title"
        private const val SUB_TITLE_KEY = "subTitle"
        fun getInstance(): ChartService {
            return chartService
        }
    }

     private fun createModel(type: AAChartType, cat: Array<String>, title: String, subTitle:String, aaSeriesElement: Array<AASeriesElement>,aaChartStackingType: AAChartStackingType,context: Context): AAChartModel {
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(type)
            .title(title)
            .subtitle(subTitle)
         .backgroundColor(BACKGROUND_COLOR)
            .titleStyle(AAStyle().color(TITLE_COLOR))
            .subtitleStyle(AAStyle().color(SUB_TITLE_COLOR))
            .axesTextColor(AXES_TEXT_COLOR)
            .colorsTheme(COLORS_THEME)
            .categories(cat)
            .stacking(aaChartStackingType)
            .yAxisTitle("Energy (J)")

            .yAxisVisible(true)

            .yAxisGridLineWidth(2f)


            .dataLabelsEnabled(true)
            .dataLabelsStyle(AAStyle().color(LABEL_TEXT_COLOR).textOutline(LABEL_TEXT_OUTLINE_COLOR))
            .series(aaSeriesElement)
  /*       val aaOptions = AAOptionsConstructor.configureChartOptions(aaChartModel)

         aaOptions.legend!!
             .enabled(true)
             .verticalAlign(AAChartVerticalAlignType.Bottom)
             .layout(AAChartLayoutType.Horizontal)
             .align(AAChartAlignType.Center)
*/






        return aaChartModel
    }

    private fun createAASeriesElementsForChart( values: Array<Any>,name:String):AASeriesElement{


        return AASeriesElement()
            .name(name)
            .data(values)
            .enableMouseTracking(true).allowPointSelect(true)

    }

    public fun getAAModel(energyData:List<Double>,expectedData:List<Double>,date:List<String>,title:String,subTitle:String,energyDataName:String,expectedDataName:String,context: Context): AAChartModel{
        val energyData = createAASeriesElementsForChart(energyData.toTypedArray(),energyDataName)
        val expectedData = createAASeriesElementsForChart(expectedData.toTypedArray(),expectedDataName)
        return createModel(AAChartType.Line,date.toTypedArray(),title,subTitle,
            arrayOf(energyData,expectedData),AAChartStackingType.False,context)
    }




}