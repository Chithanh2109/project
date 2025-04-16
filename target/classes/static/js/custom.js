/**
 * Custom JavaScript for SkinCare Application
 */
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // Navbar scroll effect
    const navbar = document.querySelector('.navbar');
    window.addEventListener('scroll', function() {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // Back to top button
    const backToTopButton = document.querySelector('.back-to-top');
    if (backToTopButton) {
        window.addEventListener('scroll', function() {
            if (window.scrollY > 300) {
                backToTopButton.classList.add('show');
            } else {
                backToTopButton.classList.remove('show');
            }
        });

        backToTopButton.addEventListener('click', function() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }

    // Service category filter
    const categoryButtons = document.querySelectorAll('.category-btn');
    const serviceItems = document.querySelectorAll('.service-item');
    
    if (categoryButtons.length && serviceItems.length) {
        categoryButtons.forEach(button => {
            button.addEventListener('click', function() {
                // Remove active class from all buttons
                categoryButtons.forEach(btn => btn.classList.remove('active'));
                
                // Add active class to clicked button
                this.classList.add('active');
                
                const category = this.dataset.category;
                
                // Filter services
                serviceItems.forEach(item => {
                    if (category === 'all') {
                        item.style.display = 'block';
                    } else if (item.dataset.category === category) {
                        item.style.display = 'block';
                    } else {
                        item.style.display = 'none';
                    }
                });
            });
        });
    }

    // Testimonial slider
    const testimonialSlider = document.querySelector('.testimonial-slider');
    if (testimonialSlider && typeof Swiper !== 'undefined') {
        new Swiper('.testimonial-slider', {
            slidesPerView: 1,
            spaceBetween: 30,
            loop: true,
            pagination: {
                el: '.swiper-pagination',
                clickable: true
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev'
            },
            breakpoints: {
                768: {
                    slidesPerView: 2
                },
                992: {
                    slidesPerView: 3
                }
            }
        });
    }

    // Animate on scroll
    const animatedElements = document.querySelectorAll('.animate');
    if (animatedElements.length) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animated');
                }
            });
        }, {
            threshold: 0.1
        });

        animatedElements.forEach(element => {
            observer.observe(element);
        });
    }

    // Booking date validation - can't book in the past
    const bookingDateInput = document.getElementById('appointmentDate');
    if (bookingDateInput) {
        const today = new Date();
        const dd = String(today.getDate()).padStart(2, '0');
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const yyyy = today.getFullYear();
        const minDate = yyyy + '-' + mm + '-' + dd;
        
        bookingDateInput.setAttribute('min', minDate);
    }

    // Booking form service selection
    const serviceSelect = document.getElementById('service');
    const servicePriceElement = document.getElementById('servicePrice');
    const serviceDurationElement = document.getElementById('serviceDuration');
    
    if (serviceSelect && servicePriceElement && serviceDurationElement) {
        serviceSelect.addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            const price = selectedOption.dataset.price || '0';
            const duration = selectedOption.dataset.duration || '0';
            
            servicePriceElement.textContent = formatCurrency(price);
            serviceDurationElement.textContent = duration + ' phút';
        });
    }

    // Helper function to format currency
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    // Search functionality
    const searchInput = document.getElementById('searchInput');
    const searchResults = document.getElementById('searchResults');
    
    if (searchInput && searchResults) {
        searchInput.addEventListener('input', debounce(function() {
            const query = this.value.trim();
            
            if (query.length < 2) {
                searchResults.innerHTML = '';
                searchResults.style.display = 'none';
                return;
            }
            
            // Fetch search results
            fetch(`/api/search?query=${encodeURIComponent(query)}`)
                .then(response => response.json())
                .then(data => {
                    searchResults.innerHTML = '';
                    
                    if (data.length === 0) {
                        searchResults.innerHTML = '<div class="p-3">Không tìm thấy kết quả</div>';
                    } else {
                        data.forEach(item => {
                            const resultItem = document.createElement('div');
                            resultItem.classList.add('search-result-item');
                            resultItem.innerHTML = `
                                <a href="${item.url}">
                                    <div class="d-flex align-items-center">
                                        <img src="${item.image || '/images/placeholder.jpg'}" alt="${item.name}" class="search-result-img">
                                        <div>
                                            <h6>${item.name}</h6>
                                            <p class="mb-0">${item.type}</p>
                                        </div>
                                    </div>
                                </a>
                            `;
                            searchResults.appendChild(resultItem);
                        });
                    }
                    
                    searchResults.style.display = 'block';
                })
                .catch(error => {
                    console.error('Search error:', error);
                });
        }, 300));
        
        // Close search results when clicking outside
        document.addEventListener('click', function(e) {
            if (!searchInput.contains(e.target) && !searchResults.contains(e.target)) {
                searchResults.style.display = 'none';
            }
        });
    }
    
    // Debounce function for search
    function debounce(func, wait) {
        let timeout;
        return function() {
            const context = this;
            const args = arguments;
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                func.apply(context, args);
            }, wait);
        };
    }

    // Language switcher
    const languageSwitcher = document.querySelector('.language-switcher');
    if (languageSwitcher) {
        languageSwitcher.addEventListener('click', function(e) {
            e.preventDefault();
            const lang = this.dataset.lang;
            
            // Set cookie for language preference
            document.cookie = `lang=${lang}; path=/; max-age=31536000`; // 1 year
            
            // Reload page
            window.location.reload();
        });
    }

    // Show password toggle
    const passwordToggles = document.querySelectorAll('.password-toggle');
    if (passwordToggles.length) {
        passwordToggles.forEach(toggle => {
            toggle.addEventListener('click', function() {
                const passwordField = document.getElementById(this.dataset.target);
                
                if (passwordField.type === 'password') {
                    passwordField.type = 'text';
                    this.innerHTML = '<i class="fas fa-eye-slash"></i>';
                } else {
                    passwordField.type = 'password';
                    this.innerHTML = '<i class="fas fa-eye"></i>';
                }
            });
        });
    }
}); 