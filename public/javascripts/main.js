htmx.onLoad( () => {
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
})
