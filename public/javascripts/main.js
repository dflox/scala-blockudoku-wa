htmx.onLoad(() => {
    document.querySelectorAll('.tile-marker-wrapper').forEach(block => {
        block.addEventListener('mouseenter', () => {
            document.querySelectorAll('.tile-marker-wrapper').forEach(el => el.classList.remove('preview-valid', 'preview-invalid'));
            block.dataset.previewvalid?.split(',').forEach(id => document.getElementById(id)?.classList.add('preview-valid'));
            block.dataset.previewinvalid?.split(',').forEach(id => document.getElementById(id)?.classList.add('preview-invalid'));
        });
        block.addEventListener('mouseleave', () => {
            document.querySelectorAll('.tile-marker-wrapper').forEach(el => el.classList.remove('preview-valid', 'preview-invalid'));
        });
    });

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
})