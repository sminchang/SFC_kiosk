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

window.renderChart = function (filteredOrderItems) {
  // 기존 차트 제거
  var chartContainer = document.getElementById('myChart');
  var chartCanvas = document.createElement('canvas');
  chartCanvas.id = 'myChart';
  chartContainer.parentNode.replaceChild(chartCanvas, chartContainer);

  // 월별 누적 수량 계산
  var monthlyData = calculateMonthlyData(filteredOrderItems);

  // 차트 데이터 준비
  var labels = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
  var data = labels.map(function(label) {
      var month = label.slice(0, -1);
      return monthlyData[month] || 0;
  });

  // 차트 그리기
  var ctx = document.getElementById('myChart').getContext('2d');
  new Chart(ctx, {
      type: 'line',
      data: {
          labels: labels,
          datasets: [{
              label: '월별 누적 수량',
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
          }
      }
  });
};