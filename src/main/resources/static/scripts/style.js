document.addEventListener('DOMContentLoaded', function () {
    const colors = ['#ff6b6b', '#f0a500', '#9d4edd', '#00c2cb', '#ff85f2'];

    const isMobile = window.innerWidth < 768;
    const totalNumbers = isMobile ? 40 : 100;

    let container = document.createElement('div');
    container.classList.add('numbers');
    container.style.position = 'absolute';
    container.style.top = '0';
    container.style.left = '0';
    container.style.width = '100vw';
    container.style.height = '100vh';
    container.style.overflow = 'hidden';
    document.body.appendChild(container);

    const containerRect = container.getBoundingClientRect();

    for (let i = 0; i < totalNumbers; i++) {
        const number = document.createElement('div');
        number.classList.add('number');

        number.textContent = Math.floor(Math.random() * 10);

        const size = Math.floor(Math.random() * 50) + 20;
        const rotation = Math.floor(Math.random() * 360);
        const color = colors[Math.floor(Math.random() * colors.length)];

        const startX = Math.random() * (containerRect.width - size);
        const startY = Math.random() * (containerRect.height - size);

        number.style.fontSize = `${size}px`;
        number.style.color = color;
        number.style.position = 'absolute';
        number.style.transform = `translate(${startX}px, ${startY}px) rotate(${rotation}deg)`;

        container.appendChild(number);
    }
});

window.addEventListener("load", () => {
    const block = document.querySelector(".underscore");
    if (!block) return;
    const img = block.querySelector(".caption-img");
    const text = block.querySelector(".caption-text");

    console.log("Loading...");
    block.classList.add("active");

    img.classList.add("animate");
    text.classList.add("animate");
});


