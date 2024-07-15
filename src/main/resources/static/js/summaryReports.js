//
var countDown = 300;
function countdown() {
    setInterval(function () {
        if (0 === countDown) {
            location.reload("/report/summary/update");
        } else {
            countDown--;
            document.getElementById('refreshCounter').innerHTML = countDown;
        }
    }, 1000);
}

countdown();

anychart.onDocumentReady(function () {

    // Получаем элемент таблицы
    var table = document.getElementById('dataTable');

// Создаем массив для хранения данных
    var data = [];

// Проходим по каждой строке таблицы
    for (var i = 0; i < table.rows.length; i++) {
        // Создаем новый массив для хранения данных строки
        var rowData = [];

        // Проходим по каждой ячейке строки
        for (var j = 0; j < table.rows[i].cells.length; j++) {
            // Добавляем значение ячейки в массив данных строки
            let temp = table.rows[i].cells[j].innerText
            switch (j) {
                case 4:
                    temp /= 10;
                    break;
            }
            rowData.push(temp);
        }

        // Добавляем массив данных строки в общий массив
        data.push(rowData);
    }


    // создайте набор данных
    var dataSet = anychart.data.set(data);

    // сопоставьте данные для всех серий
    var firstSeriesData = dataSet.mapAs({x: 0, value: 1});
    var secondSeriesData = dataSet.mapAs({x: 0, value: 2});
    var thirdSeriesData = dataSet.mapAs({x: 0, value: 3});
    var fourthSeriesData = dataSet.mapAs({x: 0, value: 4});
    var fifthSeriesData = dataSet.mapAs({x: 0, value: 5});

    // создайте линейную диаграмму
    var chart = anychart.line();

    // создайте серии и назовите их
    var firstSeries = chart.line(firstSeriesData);
    firstSeries.name("Добыча воды со скважин");
    var secondSeries = chart.line(secondSeriesData);
    secondSeries.name("Потребление воды городом");
    var thirdSeries = chart.line(thirdSeriesData);
    thirdSeries.name("Среднее потребление");
    var fourthSeries = chart.line(fourthSeriesData);
    fourthSeries.name("Запас в РЧВ (*0,1)");
    var fifthSeries = chart.line(fifthSeriesData);
    fifthSeries.name("Рост запаса в РЧВ");

    // добавьте легенду
    chart.legend().enabled(true);

    // добавьте заголовок
    chart.title("Сводный график");

    // назовите оси
    chart.yAxis().title("М³");
    chart.xAxis().title("Время");

    // настройка маркеров серий
    firstSeries.hovered().markers().enabled(true).type("circle").size(4);
    secondSeries.hovered().markers().enabled(true).type("circle").size(4);
    // thirdSeries.hovered().markers().enabled(true).type("circle").size(4);

    thirdSeries.normal().stroke("#ff4f00",4,"3 3","round");


    // включите перекрестие
    chart.crosshair().enabled(true).yStroke(null).yLabel(false);

    // измените положение всплывающей подсказки
    chart.tooltip().positionMode("point");
    chart.tooltip().position("right").anchor("left-center").offsetX(5).offsetY(5);

    // укажите, где будет отображаться диаграмма
    chart.container("graphic-container");

    // нарисуйте получившуюся диаграмму
    chart.draw();

});