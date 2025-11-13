htmx.onLoad(() => {
    // document.querySelectorAll('.tile-marker-wrapper').forEach(block => {
    //     block.addEventListener('mouseenter', () => {
    //         document.querySelectorAll('.tile-marker-wrapper').forEach(el => el.classList.remove('preview-valid', 'preview-invalid'));
    //         block.dataset.previewvalid?.split(',').forEach(id => document.getElementById(id)?.classList.add('preview-valid'));
    //         block.dataset.previewinvalid?.split(',').forEach(id => document.getElementById(id)?.classList.add('preview-invalid'));
    //     });
    //     block.addEventListener('mouseleave', () => {
    //         document.querySelectorAll('.tile-marker-wrapper').forEach(el => el.classList.remove('preview-valid', 'preview-invalid'));
    //     });
    // });

    const carouselElement = document.querySelector('#colorCarousel');
    const carousel = new bootstrap.Carousel(carouselElement);

    document.querySelector('#btnAquatic').addEventListener('click', () => {
        carousel.to(1);
        fetch('/update/color/1', {method: 'POST'});
    });

    document.querySelector('#btnHellfire').addEventListener('click', () => {
        carousel.to(3);
        fetch('/update/color/1', {method: 'POST'});
    });

    document.querySelector('#btnRetro').addEventListener('click', () => {
        carousel.to(0);
        fetch('/update/color/0', {method: 'POST'});
    });

    document.querySelector('#btnTropical').addEventListener('click', () => {
        carousel.to(2);
        fetch('/update/color/2', {method: 'POST'});
    });
});

const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))

// ----------------------- Drag and Drop Logic -----------------------
const grid = document.getElementById('marker-grid');
const GRID_SIZE = 9;
const gridRect = grid.getBoundingClientRect();
const TILE_SIZE = 50;

function isValidPlacement(elementInd, gridRow, gridCol) {
    const indTile = gridCol + gridRow * GRID_SIZE;
    const tile = document.getElementById(`tile${indTile}`); // Fixed syntax
    if (!tile) return false;
    switch (elementInd) {
        case '0':
            return tile.dataset.previewinvalid0?.split(',').length !== 0;
        case '1':
            return tile.dataset.previewinvalid1?.split(',').length !== 0;
        case '2':
        default:
            return false;
    }
}

function getGridPosition(x, y) {
    const gridRect = grid.getBoundingClientRect();
    const relX = x - gridRect.left;
    const relY = y - gridRect.top;
    const col = Math.floor(relX / TILE_SIZE);
    const row = Math.floor(relY / TILE_SIZE);
    return { row, col };
}

document.querySelectorAll('.element').forEach(element => {
    let isValid = true;

    interact(element)
        .draggable({
            modifiers: [
                interact.modifiers.snap({
                    targets: [
                        interact.snappers.grid({
                            x: TILE_SIZE,
                            y: TILE_SIZE
                        })
                    ],
                    range: Infinity, // Always snap to nearest grid point
                    relativePoints: [{ x: 0.5, y: 0.5 }] // Snap from center of element
                })
            ],
            listeners: {
                start(event) {
                    event.target.classList.add('dragging');
                },
                move(event) {
                    const x = (parseFloat(event.target.dataset.x) || 0) + event.dx;
                    const y = (parseFloat(event.target.dataset.y) || 0) + event.dy;

                    event.target.style.transform = `translate(${x}px, ${y}px)`;
                    event.target.dataset.x = x;
                    event.target.dataset.y = y;

                    // Check validity - use center of element for checking
                    const rect = event.target.getBoundingClientRect();
                    const centerX = rect.left + rect.width / 2;
                    const centerY = rect.top + rect.height / 2;
                    const { row, col } = getGridPosition(centerX, centerY);

                    const elementType = event.target.dataset.ind;
                    isValid = row >= 0 && col >= 0 &&
                        row < GRID_SIZE && col < GRID_SIZE &&
                        isValidPlacement(elementType, row, col);

                    event.target.classList.toggle('invalid', !isValid);
                },
                end(event) {
                    event.target.classList.remove('dragging', 'invalid');

                    // if (!isValid) {
                    //     // Snap back to original position
                    //     event.target.style.transform = 'translate(0px, 0px)';
                    //     event.target.dataset.x = 0;
                    //     event.target.dataset.y = 0;
                    // }
                }
            }
        });
});