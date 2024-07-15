//отрисовка графиков
anychart.onDocumentReady(function () {
    // Получаем элемент таблицы
    let table = document.getElementById('dataTable');

// Создаем массив для хранения данных
    let data = [];

// Проходим по каждой строке таблицы 'dataTable'
    for (let i = 1; i < table.rows.length; i++) {
        // Создаем новый массив для хранения данных строки
        let rowData = [];

        // Проходим по каждой ячейке строки
        for (let j = 0; j < table.rows[i].cells.length; j++) {

            // Добавляем значение ячейки в массив данных строки
            let tempvol = table.rows[i].cells[j].innerText

            switch (j) {
                case 6:
                    tempvol /= 10;// сжатия графика для "Запас в РЧВ"
                    break;
                case 9:
                case 10:
                case 11:
                    tempvol *= 9.8;
                    break;
            }
            // console.log(j, tempvol)
            rowData.push(tempvol);
        }
        // Добавляем массив данных строки в общий массив
        data.push(rowData);
    }


    // создайте набор данных
    let dataSet = anychart.data.set(data);

    // сопоставьте данные для всех серий
    let volExtract = dataSet.mapAs({x: 0, value: 1});
    let volBackCity = dataSet.mapAs({x: 0, value: 2});
    let volBackVos15 = dataSet.mapAs({x: 0, value: 3});
    let volAll = dataSet.mapAs({x: 0, value: 4});
    let volCity = dataSet.mapAs({x: 0, value: 5});
    let cleanWaterSupply = dataSet.mapAs({x: 0, value: 6});
    let deltaCleanWaterSupplyCalc = dataSet.mapAs({x: 0, value: 7});
    //let deltaCleanWaterSupply = dataSet.mapAs({x: 0, value: 8});
    let pressureCity = dataSet.mapAs({x: 0, value: 9});
    let pressureBackCity = dataSet.mapAs({x: 0, value: 10});
    let pressureBackVos15 = dataSet.mapAs({x: 0, value: 11});


    // создайте линейную диаграмму
    let chart = anychart.line(); //графики расходов и запаса
    let chartP = anychart.line(); //графики давлений

    // ссоздаем серии и называем их
    let volExtractSeries = chart.line(volExtract);
    volExtractSeries.name("Добыча воды со скважин");

    let volBackCitySeries = chart.line(volBackCity);
    volBackCitySeries.name("Обратка из города");

    let volBackVos15Series = chart.line(volBackVos15);
    volBackVos15Series.name("Обратка от ВОС15000");

    let volAllSeries = chart.line(volAll);
    volAllSeries.name("Общий приход");

    let volCitySeries = chart.line(volCity);
    volCitySeries.name("Подача в город");

    let cleanWaterSupplySeries = chart.line(cleanWaterSupply);
    cleanWaterSupplySeries.name("Запас в РЧВ(*0,01)");

    // let deltaCleanWaterSupplySeries = chart.line(deltaCleanWaterSupply);
    // deltaCleanWaterSupplySeries.name("Запас в РЧВ *0.01 фактический");

    let deltaCleanWaterSupplyCalcSeries = chart.line(deltaCleanWaterSupplyCalc);
    deltaCleanWaterSupplyCalcSeries.name("Рост запаса в РЧВ расчетный");

    let pressureCitySeries = chartP.line(pressureCity);
    pressureCitySeries.name("Напор подачи в город");

    let pressureBackCitySeries = chartP.line(pressureBackCity);
    pressureBackCitySeries.name("Напор обратки из города");

    let pressureBackVos15Series = chartP.line(pressureBackVos15);
    pressureBackVos15Series.name("Напор обратки от ВОС1500");


    // включаем легенду
    chart.legend().enabled(true);
    chartP.legend().enabled(true);

    //  заголовок
    chart.title("Часовые расходы");
    chartP.title("Напор в трубопроводах");

    // названия осей
    chart.yAxis().title("М³");
    chart.xAxis().title("Время");

    chartP.yAxis().title("М");
    chartP.xAxis().title("Время");




    // настройка маркеров серий
    volExtractSeries.hovered().markers().enabled(true).type("circle").size(3);
    volBackCitySeries.hovered().markers().enabled(true).type("circle").size(3);
    volBackVos15Series.hovered().markers().enabled(true).type("circle").size(3);
    volAllSeries.hovered().markers().enabled(true).type("circle").size(3);
    volCitySeries.hovered().markers().enabled(true).type("circle").size(3);
    cleanWaterSupplySeries.hovered().markers().enabled(true).type("circle").size(3);
    deltaCleanWaterSupplyCalcSeries.hovered().markers().enabled(true).type("circle").size(3);
    pressureCitySeries.hovered().markers().enabled(true).type("circle").size(3);
    pressureBackCitySeries.hovered().markers().enabled(true).type("circle").size(3);
    pressureBackVos15Series.hovered().markers().enabled(true).type("circle").size(7);

    //толщина некоторых графиков
    cleanWaterSupplySeries.stroke(function () {
        return {color: this.sourceColor, thickness: 4};
    });
    volAllSeries.stroke(function () {
        return {color: this.sourceColor, thickness: 4};
    });
    volCitySeries.stroke(function () {
        return {color: this.sourceColor, thickness: 4};
    });

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