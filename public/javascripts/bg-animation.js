const balls = [...document.querySelectorAll('.animated-bg-element')];
const container = document.getElementById('animated-bg');
const cw = container.clientWidth, ch = container.clientHeight;
const REPEL_RADIUS = 150;
const REPEL_STRENGTH = 2.5;
const FRICTION = 0.93;
const mouse = {x: -1000, y: -1000};
window.addEventListener('mousemove', (e) => {
    const rect = container.getBoundingClientRect();
    mouse.x = e.clientX - rect.left;
    mouse.y = e.clientY - rect.top;
});
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

const MIN_SPEED = 3;

function move() {
    balls.forEach((b, i) => {
        let d = data[i];
        const ballCenterX = d.x + d.width / 2;
        const ballCenterY = d.y + d.height / 2;
        const dx = ballCenterX - mouse.x;
        const dy = ballCenterY - mouse.y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < REPEL_RADIUS) {
            const angle = Math.atan2(dy, dx);
            const force = (REPEL_RADIUS - distance) / REPEL_RADIUS * REPEL_STRENGTH;
            d.vx += Math.cos(angle) * force;
            d.vy += Math.sin(angle) * force;
        }

        d.vx *= FRICTION;
        d.vy *= FRICTION;
        d.vx += (Math.random() - 0.5) * 0.1;
        d.vy += (Math.random() - 0.5) * 0.1;

        // --- enforce minimum speed ---
        const speed = Math.sqrt(d.vx * d.vx + d.vy * d.vy);
        if (speed < MIN_SPEED) {
            const angle = Math.atan2(d.vy, d.vx);
            d.vx = Math.cos(angle) * MIN_SPEED;
            d.vy = Math.sin(angle) * MIN_SPEED;
        }

        d.x += d.vx;
        d.y += d.vy;

        if (d.x <= 0 || d.x >= cw - (d.width + 5)) d.vx *= -1;
        if (d.y <= 0 || d.y >= ch - (d.height + 5)) d.vy *= -1;

        d.x = Math.max(0, Math.min(cw - (d.width + 5), d.x));
        d.y = Math.max(0, Math.min(ch - (d.height + 5), d.y));

        b.style.left = d.x + 'px';
        b.style.top = d.y + 'px';
    });
    requestAnimationFrame(move);
}

move();