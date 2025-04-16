/**
 * SkinCare Center - Custom JavaScript functionality
 */

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    
    // Initialize any sliders on the page
    initializeSliders();
    
    // Handle booking form validation
    setupBookingForm();
    
    // Handle service filtering
    setupServiceFilters();
    
    // Handle lightbox for images
    setupImageLightbox();
    
    // Handle smooth scrolling for anchor links
    setupSmoothScrolling();
});

/**
 * Initialize any sliders/carousels on the page
 */
function initializeSliders() {
    // Check if the Bootstrap carousel elements exist
    const carousels = document.querySelectorAll('.carousel');
    
    if (carousels.length > 0) {
        carousels.forEach(carousel => {
            // Add event listeners for pause on hover
            carousel.addEventListener('mouseenter', () => {
                bootstrap.Carousel.getInstance(carousel).pause();
            });
            
            carousel.addEventListener('mouseleave', () => {
                bootstrap.Carousel.getInstance(carousel).cycle();
            });
        });
    }
    
    // If we have testimonial sliders
    const testimonialSliders = document.querySelectorAll('.testimonial-slider');
    if (testimonialSliders.length > 0 && typeof Swiper !== 'undefined') {
        testimonialSliders.forEach(slider => {
            new Swiper(slider, {
                slidesPerView: 1,
                spaceBetween: 30,
                loop: true,
                pagination: {
                    el: '.swiper-pagination',
                    clickable: true,
                },
                navigation: {
                    nextEl: '.swiper-button-next',
                    prevEl: '.swiper-button-prev',
                },
                autoplay: {
                    delay: 5000,
                    disableOnInteraction: false,
                },
                breakpoints: {
                    768: {
                        slidesPerView: 2,
                    },
                    992: {
                        slidesPerView: 3,
                    }
                }
            });
        });
    }
}

/**
 * Setup booking form validation
 */
function setupBookingForm() {
    const bookingForm = document.getElementById('bookingForm');
    
    if (bookingForm) {
        bookingForm.addEventListener('submit', function(e) {
            if (!this.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            
            this.classList.add('was-validated');
        });
        
        // Handle date selection - disable past dates
        const dateInput = bookingForm.querySelector('input[type="date"]');
        if (dateInput) {
            const today = new Date().toISOString().split('T')[0];
            dateInput.setAttribute('min', today);
        }
        
        // Handle service selection to update pricing
        const serviceSelect = bookingForm.querySelector('#service');
        const priceDisplay = document.getElementById('servicePrice');
        const durationDisplay = document.getElementById('serviceDuration');
        
        if (serviceSelect && priceDisplay && durationDisplay) {
            serviceSelect.addEventListener('change', function() {
                const selectedOption = this.options[this.selectedIndex];
                const price = selectedOption.getAttribute('data-price');
                const duration = selectedOption.getAttribute('data-duration');
                
                if (price) {
                    priceDisplay.textContent = price;
                }
                
                if (duration) {
                    durationDisplay.textContent = duration + ' phút';
                }
            });
        }
    }
}

/**
 * Setup service filtering functionality
 */
function setupServiceFilters() {
    const filterButtons = document.querySelectorAll('.category-filter .category-btn');
    
    if (filterButtons.length > 0) {
        filterButtons.forEach(button => {
            button.addEventListener('click', function() {
                // Remove active class from all buttons
                filterButtons.forEach(btn => btn.classList.remove('active'));
                
                // Add active class to clicked button
                this.classList.add('active');
                
                // Get the filter value
                const filterValue = this.getAttribute('data-filter');
                
                // Find all service items
                const serviceItems = document.querySelectorAll('.service-item');
                
                // Filter the items
                serviceItems.forEach(item => {
                    if (filterValue === 'all' || item.getAttribute('data-category') === filterValue) {
                        item.style.display = 'block';
                    } else {
                        item.style.display = 'none';
                    }
                });
            });
        });
    }
}

/**
 * Setup image lightbox functionality
 */
function setupImageLightbox() {
    const lightboxImages = document.querySelectorAll('.lightbox-image');
    
    if (lightboxImages.length > 0) {
        lightboxImages.forEach(image => {
            image.addEventListener('click', function() {
                const src = this.getAttribute('src');
                const alt = this.getAttribute('alt') || 'Image';
                
                // Create lightbox elements
                const lightbox = document.createElement('div');
                lightbox.className = 'lightbox-overlay';
                
                const lightboxContent = document.createElement('div');
                lightboxContent.className = 'lightbox-content';
                
                const imgElement = document.createElement('img');
                imgElement.src = src;
                imgElement.alt = alt;
                
                const closeButton = document.createElement('button');
                closeButton.className = 'lightbox-close';
                closeButton.innerHTML = '&times;';
                
                // Append elements
                lightboxContent.appendChild(imgElement);
                lightboxContent.appendChild(closeButton);
                lightbox.appendChild(lightboxContent);
                document.body.appendChild(lightbox);
                
                // Add close functionality
                closeButton.addEventListener('click', () => {
                    document.body.removeChild(lightbox);
                });
                
                lightbox.addEventListener('click', (e) => {
                    if (e.target === lightbox) {
                        document.body.removeChild(lightbox);
                    }
                });
            });
        });
    }
}

/**
 * Setup smooth scrolling for anchor links
 */
function setupSmoothScrolling() {
    const anchorLinks = document.querySelectorAll('a[href^="#"]:not([href="#"])');
    
    anchorLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);
            
            if (targetElement) {
                // Get header height for offset
                const header = document.querySelector('.navbar');
                const headerHeight = header ? header.offsetHeight : 0;
                
                const targetPosition = targetElement.getBoundingClientRect().top + window.pageYOffset;
                const offsetPosition = targetPosition - headerHeight - 20;
                
                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
} 