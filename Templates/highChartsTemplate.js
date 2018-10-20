Highcharts.chart('container', {
    data: {
        table: 'nodeHopCounts'
    },
    chart: {
        type: 'line'
    },
    title: {
        text: 'Comparison of Sparse Cluster Hop Count ( Before/ After Clustering )'
    },
    yAxis: {
        allowDecimals: false,
        title: {
            text: 'Hop Count'
        }
    },
    tooltip: {
        formatter: function () {
            return '<b>' + this.series.name + '</b><br/>' +
                this.point.name.toLowerCase() + ' ' + this.point.y ;
        }
    }
});