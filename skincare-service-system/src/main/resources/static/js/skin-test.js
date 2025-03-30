document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('skinTestForm');
    
    // Form validation
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Check if all required radio buttons are selected
        const requiredRadioGroups = ['morning_skin', 'acne_frequency', 'skin_sensitivity', 'previous_experience'];
        let isValid = true;
        
        requiredRadioGroups.forEach(groupName => {
            const selectedOption = form.querySelector(`input[name="${groupName}"]:checked`);
            if (!selectedOption) {
                isValid = false;
                // Find the question section containing this group
                const questionSection = form.querySelector(`input[name="${groupName}"]`).closest('.question-section');
                // Add error styling
                questionSection.classList.add('has-error');
                // Add error message if not exists
                if (!questionSection.querySelector('.error-message')) {
                    const errorDiv = document.createElement('div');
                    errorDiv.className = 'error-message text-danger mt-2';
                    errorDiv.textContent = 'Vui lòng chọn một đáp án';
                    questionSection.appendChild(errorDiv);
                }
            }
        });
        
        // Check if at least one skin concern is selected
        const skinConcerns = form.querySelectorAll('input[name="skin_concerns"]:checked');
        const skinConcernSection = form.querySelector('input[name="skin_concerns"]').closest('.question-section');
        
        if (skinConcerns.length === 0) {
            isValid = false;
            skinConcernSection.classList.add('has-error');
            if (!skinConcernSection.querySelector('.error-message')) {
                const errorDiv = document.createElement('div');
                errorDiv.className = 'error-message text-danger mt-2';
                errorDiv.textContent = 'Vui lòng chọn ít nhất một vấn đề';
                skinConcernSection.appendChild(errorDiv);
            }
        }
        
        // If form is valid, submit it
        if (isValid) {
            // Show loading state
            const submitButton = form.querySelector('button[type="submit"]');
            const originalText = submitButton.innerHTML;
            submitButton.disabled = true;
            submitButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xử lý...';
            
            // Collect form data
            const formData = new FormData(form);
            
            // Convert FormData to JSON
            const data = {};
            for (let [key, value] of formData.entries()) {
                if (key === 'skin_concerns') {
                    if (!data[key]) {
                        data[key] = [];
                    }
                    data[key].push(value);
                } else {
                    data[key] = value;
                }
            }
            
            // Submit form data
            fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(result => {
                // Redirect to results page
                window.location.href = `/skin-test/results?id=${result.id}`;
            })
            .catch(error => {
                console.error('Error:', error);
                // Show error message
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-danger mt-3';
                alertDiv.role = 'alert';
                alertDiv.textContent = 'Có lỗi xảy ra. Vui lòng thử lại sau.';
                form.insertBefore(alertDiv, form.firstChild);
                
                // Reset button state
                submitButton.disabled = false;
                submitButton.innerHTML = originalText;
            });
        }
    });
    
    // Clear error styling when user makes a selection
    form.addEventListener('change', function(e) {
        if (e.target.type === 'radio' || e.target.type === 'checkbox') {
            const questionSection = e.target.closest('.question-section');
            questionSection.classList.remove('has-error');
            const errorMessage = questionSection.querySelector('.error-message');
            if (errorMessage) {
                errorMessage.remove();
            }
        }
    });
    
    // Add custom styling for error states
    const style = document.createElement('style');
    style.textContent = `
        .question-section.has-error {
            padding: 15px;
            border-radius: 5px;
            background-color: rgba(220, 53, 69, 0.05);
            border: 1px solid rgba(220, 53, 69, 0.2);
        }
        
        .error-message {
            font-size: 0.875rem;
            margin-top: 0.5rem;
        }
        
        .spinner-border {
            width: 1rem;
            height: 1rem;
        }
    `;
    document.head.appendChild(style);
}); 