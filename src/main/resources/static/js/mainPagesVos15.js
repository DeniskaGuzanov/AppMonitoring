var countDown = 300;

function countdown() {
    setInterval(function () {
        if (countDown == 0) {
            location.reload();
        } else {
            countDown--;
            document.getElementById('refreshCounter').innerHTML = countDown;
        }
    }, 1000);
}

countdown();

window.onload = function () {
    window.setInterval(function () {
        var options = {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            timezone: 'UTC'
        };
        var currentDate = new Date()
        var clock = document.getElementById("current-clock");
        var date = document.getElementById("current-date");
        // clock.innerHTML = currentDate.toLocaleDateString("ru",options);


        date.innerHTML = currentDate.toLocaleTimeString("ru", options);
    }, 100);


};


anychart.onDocumentReady(function () {

    // Получаем элемент таблицы
    var table = document.getElementById('dataTable');

// Создаем массив для хранения данных
    var data = [];

// Проходим по каждой строке таблицы
    for (var i = 1; i < table.rows.length; i++) {
        // Создаем новый массив для хранения данных строки
        var rowData = [];

        // Проходим по каждой ячейке строки
        for (var j = 0; j < table.rows[i].cells.length; j++) {
            let tempValue = table.rows[i].cells[j].innerText;
            switch (j) {
                case 5:
                    tempValue /= 10;
                    break;
                case 8:
                    tempValue *= 9.8;
            }

            // Добавляем значение ячейки в массив данных строки
            rowData.push(tempValue);
        }

        // Добавляем массив данных строки в общий массив
        data.push(rowData);
    }


    // создайте набор данных
    var dataSet = anychart.data.set(data);

    // сопоставьте данные для всех серий
    let volExtract = dataSet.mapAs({x: 0, value: 1});
    let volCity = dataSet.mapAs({x: 0, value: 4});
    let cleanWaterSupply = dataSet.mapAs({x: 0, value: 5});
    let deltaCleanWaterSupplyCalc = dataSet.mapAs({x: 0, value: 6});
    let pressureCity = dataSet.mapAs({x: 0, value: 8});

    // создайте линейную диаграмму
    let chart = anychart.line();
    let chartP = anychart.line();

    // создайте серии и назовите их
    var volExtractSeries = chart.line(volExtract);
    volExtractSeries.name("Добыча воды со скважин");

    var volCitySeries = chart.line(volCity);
    volCitySeries.name("Подача в город");

    var cleanWaterSupplySeries = chart.line(cleanWaterSupply);
    cleanWaterSupplySeries.name("Запас в РЧВ (* 0.1)");

    var deltaCleanWaterSupplyCalcSeries = chart.line(deltaCleanWaterSupplyCalc);
    deltaCleanWaterSupplyCalcSeries.name("Рост запаса в РЧВ расчетный");

    // var deltaCleanWaterSupplySeries = chart.line(deltaCleanWaterSupply);
    // deltaCleanWaterSupplySeries.name("Рост запаса в РЧВ фактический");

    var pressureCitySeries = chartP.line(pressureCity);
    pressureCitySeries.name("Напор подачи в город");


    // включаем легенду
    chart.legend().enabled(true);
    chartP.legend().enabled(true);

    //  заголовок
    chart.title("Часовые расходы");
    chartP.title("Напор в трубопроводах");

    // названия  осей
    chart.yAxis().title("М³");
    chart.xAxis().title("Время");

    chartP.yAxis().title("М");
    chartP.xAxis().title("Время");

    // настройка маркеров серий
    volExtractSeries.hovered().markers().enabled(true).type("circle").size(4);
    volCitySeries.hovered().markers().enabled(true).type("circle").size(4);
    cleanWaterSupplySeries.hovered().markers().enabled(true).type("circle").size(4);
    deltaCleanWaterSupplyCalcSeries.hovered().markers().enabled(true).type("circle").size(4);
    // deltaCleanWaterSupplySeries.hovered().markers().enabled(true).type("circle").size(4);
    pressureCitySeries.hovered().markers().enabled(true).type("circle").size(4);


    // включите перекрестие
    chart.crosshair().enabled(true).yStroke(null).yLabel(false);
    chartP.crosshair().enabled(true).yStroke(null).yLabel(false);

    // измените положение всплывающей подсказки
    chart.tooltip().positionMode("point");
    chart.tooltip().position("right").anchor("left-center").offsetX(5).offsetY(5);

    chartP.tooltip().positionMode("point");
    chartP.tooltip().position("right").anchor("left-center").offsetX(5).offsetY(5);

    // укажите, где будет отображаться диаграмма
    chart.container("graphic-container");
    chartP.container("graphic-container-pressure");

    // нарисуйте получившуюся диаграмму
    chart.draw();
    chartP.draw();

});