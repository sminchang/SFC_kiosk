function calculateMonthlyData(filteredOrderItems) {

  var monthlyData = {
      '1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0,
      '7': 0, '8': 0, '9': 0, '10': 0, '11': 0, '12': 0
  };

  filteredOrderItems.forEach(function(orderItem) {
      var dateStr = orderItem.orderTime;
      if (typeof dateStr === 'string') {
          var date = new Date(dateStr);
          var month = date.getMonth() + 1;
          monthlyData[month] += orderItem.quantity;
      }
  });

  return monthlyData;
}

// 주차별 수량 계산 함수 추가
function calculateWeeklyData(filteredOrderItems) {
    var weeklyData = {};

    filteredOrderItems.forEach(function(orderItem) {
        var dateStr = orderItem.orderTime;
        if (typeof dateStr === 'string') {
            var date = new Date(dateStr);
            var weekNumber = Math.ceil((date.getDate() - 1) / 7);
            var weekKey = 'Week ' + weekNumber;
            if (!weeklyData[weekKey]) {
                weeklyData[weekKey] = 0;
            }
            weeklyData[weekKey] += orderItem.quantity;
        }
    });

    // weeklyData 객체의 키를 정렬하여 반환
    var sortedWeeklyData = {};
    Object.keys(weeklyData).sort().forEach(function(key) {
        sortedWeeklyData[key] = weeklyData[key];
    });

    return sortedWeeklyData;
}

// 요일별 수량 계산 함수 추가
function calculateDailyData(filteredOrderItems) {
    var dailyData = {
        'Sun': 0, 'Mon': 0, 'Tue': 0, 'Wed': 0, 'Thu': 0, 'Fri': 0, 'Sat': 0
    };

    filteredOrderItems.forEach(function(orderItem) {
        var dateStr = orderItem.orderTime;
        if (typeof dateStr === 'string') {
            var date = new Date(dateStr);
            var dayOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][date.getDay()];
            dailyData[dayOfWeek] += orderItem.quantity;
        }
    });

    return dailyData;
}

//오늘, 2시간 단위 수량 계산 함수 추가
function calculateHourlyData(filteredOrderItems) {
    var hourlyData = {};

    filteredOrderItems.forEach(function(orderItem) {
        var dateStr = orderItem.orderTime;
        if (typeof dateStr === 'string') {
            var date = new Date(dateStr);
            var hour = Math.floor(date.getHours() / 2) * 2;
            var hourKey = hour + ':00 - ' + (hour + 2) + ':00';
            if (!hourlyData[hourKey]) {
                hourlyData[hourKey] = 0;
            }
            hourlyData[hourKey] += orderItem.quantity;
        }
    });

    return hourlyData;
}

function handleChartClick(event, chart, filteredOrderItems, option) {
    var activePoints = chart.getElementsAtEvent(event);
    if (activePoints.length > 0) {
        var clickedElementIndex = activePoints[0]._index;
        var label = chart.data.labels[clickedElementIndex];
        if (option === 'This year') {
            var selectedMonth = label.slice(0, -1);
            var monthlyFilteredData = filteredOrderItems.filter(function (orderItem) {
                var dateStr = orderItem.orderTime;
                if (typeof dateStr === 'string') {
                    var date = new Date(dateStr);
                    return date.getMonth() + 1 == selectedMonth;
                }
            });
            renderChart(monthlyFilteredData, 'This month');
        } else if (option === 'This month') {
            var selectedWeek = label.split(' ')[1];
            var weeklyFilteredData = filteredOrderItems.filter(function (orderItem) {
                var dateStr = orderItem.orderTime;
                if (typeof dateStr === 'string') {
                    var date = new Date(dateStr);
                    var weekNumber = Math.ceil((date.getDate() - 1) / 7);
                    return weekNumber == selectedWeek;
                }
            });
            renderChart(weeklyFilteredData, 'This week');
        } else if (option === 'This week') {
            var selectedDay = label;
            var dailyFilteredData = filteredOrderItems.filter(function (orderItem) {
                var dateStr = orderItem.orderTime;
                if (typeof dateStr === 'string') {
                    var date = new Date(dateStr);
                    var dayOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][date.getDay()];
                    return dayOfWeek === selectedDay;
                }
            });
            renderChart(dailyFilteredData, 'Today');
        }
    }
}

window.renderChart = function (filteredOrderItems, option) {
// 기존 차트 제거
    var chartContainer = document.getElementById('myChart');
    var chartCanvas = document.createElement('canvas');
    chartCanvas.id = 'myChart';
    chartContainer.parentNode.replaceChild(chartCanvas, chartContainer);

    var data;
    var labels;

    if (option === 'This month') {
        var weeklyData = calculateWeeklyData(filteredOrderItems);
        labels = Object.keys(weeklyData);
        data = Object.values(weeklyData);
    } else if (option === 'This week') {
        var dailyData = calculateDailyData(filteredOrderItems);
        labels = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        data = labels.map(function(label) {
            return dailyData[label] || 0;
        });
    } else if (option === 'Today') {
        var hourlyData = calculateHourlyData(filteredOrderItems);
        labels = Object.keys(hourlyData);
        data = Object.values(hourlyData);
    } else {
        var monthlyData = calculateMonthlyData(filteredOrderItems);
        labels = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
        data = labels.map(function(label) {
            var month = label.slice(0, -1);
            return monthlyData[month] || 0;
        });
    }

  // 차트 그리기
  var ctx = document.getElementById('myChart').getContext('2d');
  var chart = new Chart(ctx, {
      type: 'line',
      data: {
          labels: labels,
          datasets: [{
              label: option === 'This month' ? '주차별 누적 수량' :
                  option === 'This week' ? '요일별 누적 수량' :
                      option === 'Today' ? '2시간 단위 누적 수량' : '월별 누적 수량',
              data: data,
              lineTension: 0,
              backgroundColor: 'transparent',
              borderColor: '#007bff',
              borderWidth: 4,
              pointBackgroundColor: '#007bff'
          }]
      },
      options: {
          scales: {
              y: {
                  beginAtZero: true,
                  stepSize: 1,
                  title: {
                      display: true,
                      text: '누적 수량'
                  }
              },
              x: {
                  title: {
                      display: true,
                      text: '월'
                  }
              }
          },
          onClick: function (event){
              handleChartClick(event, chart, filteredOrderItems, option)
          }
      }
  });
};