const balls = [...document.querySelectorAll('.animated-bg-element')];
const container = document.getElementById('animated-bg');
const cw = container.clientWidth, ch = container.clientHeight;

const data = balls.map(b => {
    const width = b.offsetWidth;
    const height = b.offsetHeight;
    return {
        x: Math.random() * (cw - width),
        y: Math.random() * (ch - height),
        vx: (Math.random() - 0.5) * 4,
        vy: (Math.random() - 0.5) * 4,
        width,
        height
    };
});

function move() {
    balls.forEach((b, i) => {
        let d = data[i];
        d.x += d.vx;
        d.y += d.vy;

        if (d.x <= 0 || d.x >= cw - (d.width + 5)) d.vx *= -1;
        if (d.y <= 0 || d.y >= ch - (d.height + 5)) d.vy *= -1;

        b.style.left = d.x + 'px';
        b.style.top = d.y + 'px';
    });
    requestAnimationFrame(move);
}
move();