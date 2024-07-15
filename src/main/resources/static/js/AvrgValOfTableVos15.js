function calcAvrfValue() {
// Предположим, что у вас есть таблица с id "myTable"
    let table = document.getElementById("dataTable");

// Получаем количество строк и столбцов
    let rows = table.rows.length;
    if (rows < 2) return;
    let cols = table.rows[0].cells.length;

    var newRow = table.insertRow(-1);
    newRow.classList.add('table-secondary')
    newRow.insertCell(0);

// Для каждого столбца
    for (let j = 1; j < 9; j++) {
        let sum = 0;
        let count = 0;
        let flag = false;

        // Суммируем все значения в столбце
        for (let i = 0; i < rows; i++) {
            let cellValue = parseFloat(table.rows[i].cells[j].innerText);
            if (!isNaN(cellValue)) {
                sum += cellValue;
                count++;
                flag = true;
            }
        }
        // Создаем новую ячейку и добавляем ее в конец каждого столбца
        let newCell = newRow.insertCell(j);

        if (flag) {//если были данные, то вычисляем значения и записываем в ячейки
            var avg = 0;
            switch (true) {
                case (j > 6 || j === 5):
                    avg = sum / count;
                    break
                default:
                    avg = sum;
            }

            if (j === 8) {
                newCell.innerText = avg.toFixed(2);
            } else {
                newCell.innerText = avg.toFixed(1);
            }
        }
    }
}

window.onload = calcAvrfValue;