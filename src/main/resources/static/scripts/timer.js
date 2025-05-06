let timerInterval = null;
let startTime = null;

function startTimer() {
    const savedStart = sessionStorage.getItem('startTime');
    startTime = savedStart ? parseInt(savedStart) : Date.now();
    sessionStorage.setItem('startTime', startTime);

    updateTimer();
    timerInterval = setInterval(updateTimer, 1000);
}

function updateTimer() {
    const now = Date.now();
    const elapsed = Math.floor((now - startTime) / 1000);

    const minutes = String(Math.floor(elapsed / 60)).padStart(2, '0');
    const seconds = String(elapsed % 60).padStart(2, '0');

    document.querySelector('.timer-value').textContent = `${minutes}:${seconds}`;
}

function stopTimer() {
    clearInterval(timerInterval);
    sessionStorage.removeItem('startTime');
}
