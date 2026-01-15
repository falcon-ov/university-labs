const helloDiv = document.getElementById('hello');

function getRandomDarkColor() {
    const letters = '0123456789ABC';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * letters.length)];
    }
    return color;
}

helloDiv.addEventListener('click', () => {
    helloDiv.style.color = getRandomDarkColor();
    helloDiv.style.backgroundColor = getRandomDarkColor();
});
