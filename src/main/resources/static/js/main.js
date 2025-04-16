// Main JavaScript file for Spa Website

document.addEventListener('DOMContentLoaded', function() {
    // Navbar Scroll Effect
    const navbar = document.querySelector('.navbar');
    if (navbar) {
        window.addEventListener('scroll', function() {
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });
    }

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            if (this.getAttribute('href') !== '#') {
                e.preventDefault();
                const targetId = this.getAttribute('href');
                const targetElement = document.querySelector(targetId);
                
                if (targetElement) {
                    window.scrollTo({
                        top: targetElement.offsetTop - 80,
                        behavior: 'smooth'
                    });
                }
            }
        });
    });

    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    if (forms.length > 0) {
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }

    // Service Card hover animation
    const serviceCards = document.querySelectorAll('.service-card');
    serviceCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.classList.add('shadow-hover');
        });
        card.addEventListener('mouseleave', function() {
            this.classList.remove('shadow-hover');
        });
    });

    // Therapist Card hover animation
    const therapistCards = document.querySelectorAll('.card-therapist');
    therapistCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.classList.add('shadow-hover');
        });
        card.addEventListener('mouseleave', function() {
            this.classList.remove('shadow-hover');
        });
    });

    // Testimonial animation
    const testimonials = document.querySelectorAll('.testimonial-card');
    testimonials.forEach(testimonial => {
        testimonial.addEventListener('mouseenter', function() {
            this.classList.add('shadow-hover');
        });
        testimonial.addEventListener('mouseleave', function() {
            this.classList.remove('shadow-hover');
        });
    });

    // Booking form date validation (prevent past dates)
    const dateInput = document.getElementById('date');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);
        
        dateInput.addEventListener('change', function() {
            const selectedDate = new Date(this.value);
            const now = new Date();
            
            if (selectedDate < now && selectedDate.toDateString() !== now.toDateString()) {
                alert('Không thể chọn ngày trong quá khứ!');
                this.value = today;
            }
        });
    }

    // Mobile menu toggle
    const navbarToggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');
    
    if (navbarToggler && navbarCollapse) {
        document.addEventListener('click', function(e) {
            const isNavbarToggler = navbarToggler.contains(e.target);
            const isNavbarCollapse = navbarCollapse.contains(e.target);
            
            if (!isNavbarToggler && !isNavbarCollapse && navbarCollapse.classList.contains('show')) {
                navbarToggler.click();
            }
        });
    }

    // Newsletter form submission
    const newsletterForm = document.querySelector('.newsletter-form');
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const emailInput = this.querySelector('input[type="email"]');
            if (emailInput && emailInput.value) {
                // Here you would normally send the data to the server
                alert('Cảm ơn bạn đã đăng ký nhận bản tin!');
                emailInput.value = '';
            }
        });
    }

    // Add active class to current navigation item
    const currentLocation = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
    
    navLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath === currentLocation || 
            (currentLocation === '/' && linkPath === '/') ||
            (linkPath !== '/' && currentLocation.startsWith(linkPath))) {
            link.classList.add('active');
        }
    });

    // Initialize animations for elements
    function initAnimations() {
        const fadeElements = document.querySelectorAll('.fade-in');
        const slideElements = document.querySelectorAll('.slide-up');
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = 1;
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, { threshold: 0.1 });
        
        fadeElements.forEach(el => {
            el.style.opacity = 0;
            el.style.transition = 'opacity 0.8s ease-out';
            observer.observe(el);
        });
        
        slideElements.forEach(el => {
            el.style.opacity = 0;
            el.style.transform = 'translateY(30px)';
            el.style.transition = 'opacity 0.8s ease-out, transform 0.8s ease-out';
            observer.observe(el);
        });
    }
    
    // Call animation initialization
    initAnimations();
}); 