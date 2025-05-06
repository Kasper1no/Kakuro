function createTile(tile, x, y) {
    const tileDiv = document.createElement('div');
    tileDiv.classList.add('tile');
    let className = '';

    if (tile.type === 'value' && tile.correctValue !== 0) {
        className = 'tile-value';
    } else if (tile.type === 'sum') {
        className = 'tile-sum';
    } else {
        className = 'tile-empty';
    }

    tileDiv.classList.add(className);

    if (tile.type === 'sum') {
        const rowSpan = document.createElement('span');
        rowSpan.classList.add('sum-row');
        rowSpan.textContent = tile.rowSum !== 0 ? tile.rowSum : ' ';

        if (tile.rowSum !== 0) {
            rowSpan.addEventListener('mouseenter', () => {
                const possibleSums = getPossibleSumsForTile(x, y, 'row');
                showPossibleSums(rowSpan, possibleSums);
            });

            rowSpan.addEventListener('mouseleave', () => {
                hidePossibleSums();
            });

            rowSpan.addEventListener('click', () => {
                console.log("Possible row sums:", getPossibleSumsForTile(x, y, 'row'));
            });
        }

        tileDiv.appendChild(rowSpan);

        const colSpan = document.createElement('span');
        colSpan.classList.add('sum-col');
        colSpan.textContent = tile.colSum !== 0 ? tile.colSum : ' ';

        if (tile.colSum) {
            colSpan.addEventListener('mouseenter', () => {
                const possibleSums = getPossibleSumsForTile(x, y, 'col');
                showPossibleSums(colSpan, possibleSums);
            });

            colSpan.addEventListener('mouseleave', () => {
                hidePossibleSums();
            });

            colSpan.addEventListener('click', () => {
                console.log("Possible column sums:", getPossibleSumsForTile(x, y, 'col'));
            });
        }

        tileDiv.appendChild(colSpan);
    } else if (tile.type === 'value') {
        const valueSpan = document.createElement('span');
        valueSpan.classList.add('value');
        valueSpan.textContent = tile.curValue !== 0 ? tile.curValue : ' ';
        tileDiv.appendChild(valueSpan);

        if (tile.correctValue !== 0) {
            tileDiv.addEventListener('click', (e) => {
                const existingPad = document.querySelector('.pad-container');
                if (existingPad) existingPad.remove();

                const pad = getValuePadElement();
                tileDiv.appendChild(pad);

                function closePad() {
                    const pad = document.querySelector('.pad-container');
                    if (pad) pad.remove();
                    document.removeEventListener('click', outsideClickListener);
                }

                pad.querySelectorAll('button').forEach(btn => {
                    btn.addEventListener('click', async () => {
                        const val = btn.textContent === '✖' ? 0 : parseInt(btn.textContent);

                        await postAndUpdate(`/kakuro/update`, {x: x, y: y, value: val});

                        closePad();
                    });
                });

                function outsideClickListener(event) {
                    if (!pad.contains(event.target) && event.target !== tileDiv) {
                        closePad();
                    }
                }

                setTimeout(() => document.addEventListener('click', outsideClickListener));

                e.stopPropagation();
            });
        } else {
            console.log('Correct value is 0, cannot edit tile');
        }

    }
    return tileDiv;
}

async function generateTable() {
    console.log("Fetching field...");

    let fieldData;

    const timeout = new Promise((_, reject) => setTimeout(() => reject(new Error("Timeout: Server not responding")), 2000));

    try {
        const response = await Promise.race([fetch('/kakuro/field'), timeout]);

        if (!response.ok) {
            throw new Error("Помилка при запиті поля");
        }

        fieldData = await response.json();
        console.log(fieldData);

        sessionStorage.setItem("kakuroField", JSON.stringify(fieldData));

    } catch (e) {
        console.error("Не вдалося отримати поле:", e);
        window.location.reload();
        return;
    }

    const {rowsCount, columnsCount, field} = fieldData;

    const possibleSumsData = await calculatePossibleSumsForField(field, rowsCount, columnsCount);

    sessionStorage.setItem("possibleSumsData", JSON.stringify(possibleSumsData));


    const tableBody = document.getElementById('gameTableBody');
    tableBody.innerHTML = '';

    field.forEach((row, x) => {
        const tr = document.createElement('tr');
        row.forEach((tile, y) => {
            const td = document.createElement('td');
            if (tile !== null) {
                td.appendChild(createTile(tile, x, y));
            }
            tr.appendChild(td);
        });
        tableBody.appendChild(tr);
    });
}

function getValuePadElement() {
    const padContainer = document.createElement('div');
    padContainer.classList.add('container', 'pad-container', 'text-center');

    const row1 = document.createElement('div');
    row1.classList.add('row');
    for (let i = 1; i <= 3; i++) {
        const col = document.createElement('div');
        col.classList.add('col-4');
        const button = document.createElement('button');
        button.classList.add('pad-btn');
        button.textContent = i;
        col.appendChild(button);
        row1.appendChild(col);
    }
    padContainer.appendChild(row1);

    const row2 = document.createElement('div');
    row2.classList.add('row');
    for (let i = 4; i <= 6; i++) {
        const col = document.createElement('div');
        col.classList.add('col-4');
        const button = document.createElement('button');
        button.classList.add('pad-btn');
        button.textContent = i;
        col.appendChild(button);
        row2.appendChild(col);
    }
    padContainer.appendChild(row2);

    const row3 = document.createElement('div');
    row3.classList.add('row');
    for (let i = 7; i <= 9; i++) {
        const col = document.createElement('div');
        col.classList.add('col-4');
        const button = document.createElement('button');
        button.classList.add('pad-btn');
        button.textContent = i;
        col.appendChild(button);
        row3.appendChild(col);
    }
    padContainer.appendChild(row3);

    const row4 = document.createElement('div');
    row4.classList.add('row');
    const colLeft = document.createElement('div');
    colLeft.classList.add('col-4');
    const colRight = document.createElement('div');
    colRight.classList.add('col-4');
    const buttonCross = document.createElement('button');
    buttonCross.classList.add('pad-btn');
    buttonCross.textContent = '✖';
    colRight.appendChild(buttonCross);
    const colEmpty = document.createElement('div');
    colEmpty.classList.add('col-4');
    row4.appendChild(colLeft);
    row4.appendChild(colRight);
    row4.appendChild(colEmpty);

    padContainer.appendChild(row4);

    return padContainer;
}

function updateTableFromSession() {
    const savedField = sessionStorage.getItem("kakuroField");
    if (!savedField) return;

    const fieldData = JSON.parse(savedField);
    const {field, rowsCount, columnsCount} = fieldData;
    const tableBody = document.getElementById('gameTableBody');

    clearStyles(tableBody);
    checkDuplicatesAndSumViolations(field, tableBody, rowsCount, columnsCount);
    renderValues(field, tableBody);
}

function clearStyles(tableBody) {
    Array.from(tableBody.rows).forEach(row =>
        Array.from(row.cells).forEach(cell => {
            const tileDiv = cell.querySelector('.value');
            if (tileDiv) tileDiv.style.color = '#222';
        })
    );
}

function markError(tableBody, i, j) {
    const cell = tableBody.rows[i].cells[j];
    const tileDiv = cell.querySelector('.value');
    console.log("Error at: ", i, j, "");
    if (tileDiv) tileDiv.style.color = 'red';
}

function checkDuplicatesAndSumViolations(field, tableBody, rowsCount, columnsCount) {
    for (let i = 0; i < rowsCount; i++) checkRow(field, tableBody, i, columnsCount);
    for (let j = 0; j < columnsCount; j++) checkColumn(field, tableBody, j, rowsCount);
}

function checkRow(field, tableBody, rowIndex, columnsCount) {
    const seen = {};
    let sum = 0, targetSum = null, filledCount = 0, expectedCount = 0;
    const valueIndices = [];

    for (let j = 0; j < columnsCount; j++) {
        const tile = field[rowIndex][j];
        if (tile?.type === 'value') {
            expectedCount++;
            if (tile.curValue > 0) {
                filledCount++;
                sum += tile.curValue;
                valueIndices.push(j);

                if (seen[tile.curValue]) {
                    markError(tableBody, rowIndex, j);
                    markError(tableBody, rowIndex, seen[tile.curValue]);
                } else {
                    seen[tile.curValue] = j;
                }
            }
        } else if (tile?.type === 'sum' && tile.rowSum !== 0) {
            targetSum = tile.rowSum;
        }
    }

    if (targetSum !== null) {
        if (sum > targetSum || (filledCount === expectedCount && sum !== targetSum)) {
            for (let j of valueIndices) {
                markError(tableBody, rowIndex, j);
            }
        }
    }
}

function checkColumn(field, tableBody, colIndex, rowsCount) {
    const seen = {};
    let sum = 0, targetSum = null, filledCount = 0, expectedCount = 0;
    const valueIndices = [];

    for (let i = 0; i < rowsCount; i++) {
        const tile = field[i][colIndex];
        if (tile?.type === 'value') {
            expectedCount++;
            if (tile.curValue > 0) {
                filledCount++;
                sum += tile.curValue;
                valueIndices.push(i);

                if (seen[tile.curValue]) {
                    markError(tableBody, i, colIndex);
                    markError(tableBody, seen[tile.curValue], colIndex);
                } else {
                    seen[tile.curValue] = i;
                }
            }
        } else if (tile?.type === 'sum' && tile.colSum !== 0) {
            targetSum = tile.colSum;
        }
    }

    if (targetSum !== null) {
        if (sum > targetSum || (filledCount === expectedCount && sum !== targetSum)) {
            for (let i of valueIndices) {
                markError(tableBody, i, colIndex);
            }
        }
    }
}


function renderValues(field, tableBody) {
    field.forEach((row, i) =>
        row.forEach((tile, j) => {
            const cell = tableBody.rows[i].cells[j];
            const tileDiv = cell.querySelector('.tile');
            if (tile?.type === 'value') {
                const valueSpan = tileDiv.querySelector('.value');
                if (valueSpan) {
                    valueSpan.textContent = tile.curValue !== 0 ? tile.curValue : ' ';
                }
            }
        })
    );
}


async function postAndUpdate(url, body = null) {
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: body ? JSON.stringify(body) : null
        });

        const data = await response.json();

        if (response.ok) {
            console.log(data);
            let fieldData = data.field;
            if (fieldData === undefined) {
                fieldData = data;
            }
            sessionStorage.setItem("kakuroField", JSON.stringify(fieldData));
            updateTableFromSession();
            if (data.score !== 0) {
                sessionStorage.setItem("kakuroScore", data.score);
                await showEndGameModal('You win!', data.score);
            }
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        console.error(`Error during ${url}: `, error);
    }
}

async function saveScore(score) {

    const player = await getCurrentUser();

    if (player === null) {
        return;
    }

    const response = await fetch('/player/score', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({score: score})
    });

    if (response.ok) {
        console.log("Score saved");
    } else {
        console.log("Error saving score");
    }
}

async function showEndGameModal(message, points = 0) {
    stopTimer();
    await saveScore(points);

    const backBtn = document.getElementById('back-menu-btn');
    backBtn?.classList.add('d-none');

    const overlay = document.createElement('div');
    overlay.classList.add('overlay');

    const modal = document.createElement('div');
    modal.classList.add('endgame-modal');

    const closeButton = document.createElement('button');
    closeButton.classList.add('close-modal-btn');
    closeButton.innerHTML = '&minus;';

    modal.innerHTML += `
        <h2>${message}</h2>
        <p>Your score: ${points}</p>
        <div class="modal-buttons">
            <button id="play-again-btn" class="menu-btn my-bg-pink">
                <span class="menu-btn-content">Play Again</span>
            </button>
            <button id="view-solution-btn" class="menu-btn my-bg-blue">
                <span class="menu-btn-content">Show Answer</span>
            </button>
            <button id="change-difficulty-btn" class="menu-btn my-bg-yellow">
                <span class="menu-btn-content">Change Difficulty</span>
            </button>
            <button id="menu-btn" class="menu-btn my-bg-red">
                <span class="menu-btn-content">Menu</span>
            </button>
        </div>
    `;

    modal.appendChild(closeButton);

    const toggleMenuHint = document.createElement('div');
    toggleMenuHint.classList.add('toggle-menu-hint');
    toggleMenuHint.innerText = "Endgame Menu";
    toggleMenuHint.style.display = 'none';

    overlay.appendChild(modal);
    overlay.appendChild(toggleMenuHint);
    document.body.appendChild(overlay);

    document.getElementById('play-again-btn').addEventListener('click', () => window.location.reload());
    document.getElementById('change-difficulty-btn').addEventListener('click', () => window.location.href = '/game');
    document.getElementById('menu-btn').addEventListener('click', () => window.location.href = '/');

    const hideModal = () => {
        overlay.style.alignItems = 'flex-start';
        overlay.style.backgroundColor = 'rgba(34,34,34,0.3)';
        modal.style.display = 'none';
        toggleMenuHint.style.display = 'block';
    };


    closeButton.addEventListener('click', hideModal);
    document.getElementById('view-solution-btn').addEventListener('click', hideModal);

    toggleMenuHint.addEventListener('click', () => {
        overlay.style.alignItems = 'center';
        overlay.style.backgroundColor = 'rgba(34,34,34,0.9)';
        modal.style.display = 'block';
        toggleMenuHint.style.display = 'none';
    });
}

window.addEventListener("load", async () => {
    await generateTable();
    startTimer();
    setupGameControls();
    setupExitConfirmation();

});

function setupGameControls() {
    const resignBtn = document.querySelector('.resign-btn');
    const resetBtn = document.querySelector('.reset-btn');

    resignBtn?.addEventListener('click', () => {
        postAndUpdate('/kakuro/resign')
            .then(async () => {
                sessionStorage.removeItem("kakuroField");
                stopTimer();
                await showEndGameModal('You lose :(', 0);
            });
    });

    resetBtn?.addEventListener('click', () => {
        postAndUpdate('/kakuro/reset');
    });
}

function setupExitConfirmation() {
    const backBtn = document.getElementById('back-menu-btn');
    const stayBtn = document.getElementById('stay-btn');
    const exitBtn = document.getElementById('exit-btn');
    const modal = document.getElementById('exit-confirm-modal');

    backBtn?.addEventListener('click', (e) => {
        e.preventDefault();
        modal?.classList.remove('hidden');
    });

    stayBtn?.addEventListener('click', () => {
        modal?.classList.add('hidden');
    });

    exitBtn?.addEventListener('click', () => {
        modal?.classList.add('hidden');
        document.querySelector('.resign-btn')?.click();
    });
}


// SUMS FOR TILE

async function calculatePossibleSumsForField(field, rowsCount, columnsCount) {
    const possibleSums = [];

    for (let x = 0; x < rowsCount; x++) {
        for (let y = 0; y < columnsCount; y++) {
            const tile = field[x][y];

            if (tile !== null && tile.type === 'sum') {
                const possibleRowSums = calculatePossibleSumsForRow(field, x, rowsCount, tile.rowSum);
                const possibleColSums = calculatePossibleSumsForColumn(field, y, columnsCount, tile.colSum);

                possibleSums.push({
                    tilePosition: {x, y},
                    possibleRowSums,
                    possibleColSums
                });
            }
        }
    }

    return possibleSums;
}

function calculatePossibleSumsForRow(field, rowIndex, rowsCount, targetSum) {
    const rowValues = field[rowIndex].filter(tile => tile?.type === 'value' && tile.correctValue > 0);
    return getPossibleSums(targetSum, rowValues.length);
}

function calculatePossibleSumsForColumn(field, colIndex, columnsCount, targetSum) {
    const colValues = [];
    for (let i = 0; i < columnsCount; i++) {
        const tile = field[i][colIndex];
        if (tile?.type === 'value' && tile.correctValue > 0) {
            colValues.push(tile);
        }
    }
    return getPossibleSums(targetSum, colValues.length);
}

function getPossibleSums(num, count) {
    const sums = [];
    findCombinations(sums, [], 1, count, num);
    return sums;
}

function findCombinations(sums, current, start, count, targetSum) {
    if (current.length === count) {
        if (current.reduce((sum, val) => sum + val, 0) === targetSum) {
            sums.push([...current]);
        }
        return;
    }

    for (let i = start; i <= 9; i++) {
        if (!current.includes(i)) {
            current.push(i);
            findCombinations(sums, current, i + 1, count, targetSum);
            current.pop();
        }
    }
}


function getPossibleSumsForTile(x, y, type) {
    const sumsData = sessionStorage.getItem("possibleSumsData") ? JSON.parse(sessionStorage.getItem("possibleSumsData")) : [];

    const tileData = sumsData.find(data => data.tilePosition.x === x && data.tilePosition.y === y);

    if (tileData) {
        if (type === 'row') {
            return tileData.possibleRowSums || [];
        } else if (type === 'col') {
            return tileData.possibleColSums || [];
        }
    }

    return [];
}

function showPossibleSums(spanElement, possibleSums) {
    let hoverInfo = spanElement.querySelector('.hover-info');


    if (!hoverInfo) {
        hoverInfo = document.createElement('div');
        hoverInfo.classList.add('hover-info');
        spanElement.appendChild(hoverInfo);
    }

    hoverInfo.innerHTML = '';

    possibleSums.forEach(sumCombination => {
        const sumText = document.createElement('div');
        sumText.classList.add('sum-text');
        sumText.textContent = sumCombination.join(' + ');
        hoverInfo.appendChild(sumText);
    });

    hoverInfo.style.display = 'flex';

    const rect = spanElement.getBoundingClientRect();
    const parentRect = spanElement.offsetParent.getBoundingClientRect();

    if (spanElement.classList.contains('sum-row')) {
        hoverInfo.style.left = `${spanElement.offsetLeft + 5}px`;
        hoverInfo.style.top = `${spanElement.offsetTop + 2}px`;
    } else if (spanElement.classList.contains('sum-col')) {
        hoverInfo.style.left = `${spanElement.offsetLeft + 2}px`;
        hoverInfo.style.top = `${spanElement.offsetTop + 5}px`;
    }
}

function hidePossibleSums() {
    const hoverInfoElements = document.querySelectorAll('.hover-info');
    hoverInfoElements.forEach(info => info.style.display = 'none');
}




