<template>
  <el-card class="chart-card">
    <h4 class="chart-title">{{ title }}</h4>
    <div class="chart-wrapper" ref="chartWrapperRef">
      <v-chart 
        v-if="hasData" 
        class="chart" 
        :option="chartOption" 
        autoresize 
      />
      <div v-else class="chart-empty">
        <el-empty description="暂无数据" />
      </div>
    </div>
  </el-card>
</template>

<script>
import { computed, markRaw } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, PieChart, LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

use([
  CanvasRenderer,
  BarChart,
  PieChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

export default {
  name: 'ChartCard',
  components: {
    VChart
  },
  props: {
    title: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'bar'
    },
    data: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props) {
    const hasData = computed(() => {
      const { type, data } = props
      if (type === 'pie') {
        return data.items && data.items.length > 0
      } else if (type === 'line') {
        return data.series && data.series.length > 0
      } else {
        return data.xData && data.xData.length > 0 && data.yData && data.yData.length > 0
      }
    })

    const chartOption = computed(() => {
      const { type, data } = props
      
      if (type === 'pie') {
        return {
          animation: false,
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            right: 5,
            top: 'center',
            itemWidth: 12,
            itemHeight: 12,
            textStyle: {
              fontSize: 12
            }
          },
          series: [
            {
              type: 'pie',
              radius: ['45%', '70%'],
              center: ['50%', '45%'],
              avoidLabelOverlap: true,
              itemStyle: {
                borderRadius: 8,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false
              },
              labelLine: {
                show: false
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: 12,
                  fontWeight: 'bold',
                  formatter: '{b}: {c}'
                },
                labelLine: {
                  show: true
                }
              },
              data: data.items || []
            }
          ]
        }
      } else if (type === 'line') {
        return {
          animation: false,
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: data.legend || [],
            bottom: 0
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: data.xData || []
          },
          yAxis: {
            type: 'value'
          },
          series: data.series || []
        }
      } else {
        // bar chart
        return {
          animation: false,
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: data.xData || []
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              type: 'bar',
              data: data.yData || [],
              itemStyle: {
                borderRadius: [6, 6, 0, 0]
              }
            }
          ]
        }
      }
    })

    return {
      chartOption,
      hasData
    }
  }
}
</script>

<style scoped>
.chart-card {
  height: 220px;
  width: 100%;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden !important;
}

.chart-card :deep(.el-card__body) {
  padding: 12px;
  height: 100%;
  overflow: hidden;
}

.chart-title {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chart-wrapper {
  flex: 0 0 180px;
  position: relative;
  overflow: hidden;
}

.chart {
  width: 100%;
  height: 180px;
}

.chart-empty {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>