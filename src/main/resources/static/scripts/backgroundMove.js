document.addEventListener('DOMContentLoaded', function () {
    const numbers = document.querySelectorAll('.number');
    const container = document.querySelector('.numbers');

    const containerRect = container.getBoundingClientRect();

    numbers.forEach(number => {
        let dx = (Math.random() - 0.5) * 0.1;
        let dy = (Math.random() - 0.5) * 0.1;
        let rotationSpeed = (Math.random() * 0.05) + 0.01;
        let rotationDirection = Math.random() < 0.5 ? -1 : 1;
        let rotation = Math.random() * 360;

        let startX = Math.random() * containerRect.width;
        let startY = Math.random() * containerRect.height;

        number.style.transform = `translate(${startX}px, ${startY}px) rotate(${rotation}deg)`;

        function move() {
            startX += dx;
            startY += dy;

            const rect = number.getBoundingClientRect();

            if (rect.left <= containerRect.left || rect.right >= containerRect.right) {
                dx *= -1;
                startX = Math.max(containerRect.left, Math.min(startX, containerRect.right - rect.width));
            }

            if (rect.top <= containerRect.top || rect.bottom >= containerRect.bottom) {
                dy *= -1;
                startY = Math.max(containerRect.top, Math.min(startY, containerRect.bottom - rect.height));
            }

            number.style.transform = `translate(${startX}px, ${startY}px) rotate(${rotation}deg)`;
            rotation += rotationSpeed * rotationDirection;

            requestAnimationFrame(move);
        }

        move();
    });
});
