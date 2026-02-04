/**
 * GAMING SHOP - MAIN JAVASCRIPT
 * Xử lý các chức năng chung của website
 */

// ========== DOM READY ==========
document.addEventListener('DOMContentLoaded', function() {
    
    // Initialize all functions
    initBackToTop();
    initSearchForm();
    initNavbarScroll();
    initTooltips();
    initCartBadge();
    
    console.log('🎮 Gaming Shop initialized successfully!');
});

// ========== BACK TO TOP BUTTON ==========
function initBackToTop() {
    const backToTopBtn = document.getElementById('backToTop');
    
    if (!backToTopBtn) return;
    
    // Show/hide button on scroll
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 300) {
            backToTopBtn.classList.add('show');
        } else {
            backToTopBtn.classList.remove('show');
        }
    });
    
    // Scroll to top on click
    backToTopBtn.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
}

// ========== NAVBAR SCROLL EFFECT ==========
function initNavbarScroll() {
    const navbar = document.querySelector('.navbar');
    
    if (!navbar) return;
    
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 50) {
            navbar.classList.add('navbar-scrolled');
        } else {
            navbar.classList.remove('navbar-scrolled');
        }
    });
}

// ========== SEARCH FORM ==========
function initSearchForm() {
    const searchForm = document.querySelector('.search-form');
    
    if (!searchForm) return;
    
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const searchInput = this.querySelector('input[type="search"]');
        const searchQuery = searchInput.value.trim();
        
        if (searchQuery === '') {
            alert('Vui lòng nhập từ khóa tìm kiếm!');
            return;
        }
        
        // Redirect to search results page
        window.location.href = `/products?search=${encodeURIComponent(searchQuery)}`;
    });
}

// ========== BOOTSTRAP TOOLTIPS ==========
function initTooltips() {
    const tooltipTriggerList = [].slice.call(
        document.querySelectorAll('[data-bs-toggle="tooltip"]')
    );
    
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// ========== CART BADGE UPDATE ==========
function initCartBadge() {
    updateCartBadge();
}

function updateCartBadge() {
    // Tạm thời set = 0, sau này sẽ lấy từ session/localStorage
    const cartCount = getCartItemCount();
    const cartBadge = document.querySelector('.cart-badge');
    
    if (cartBadge) {
        cartBadge.textContent = cartCount;
        
        if (cartCount > 0) {
            cartBadge.style.display = 'flex';
        } else {
            cartBadge.style.display = 'none';
        }
    }
}

function getCartItemCount() {
    // Sau này sẽ lấy từ server hoặc localStorage
    // Tạm thời return 0
    return 0;
}

// ========== NOTIFICATION HELPER ==========
function showNotification(message, type = 'info') {
    // Create toast element
    const toastHTML = `
        <div class="toast align-items-center text-white bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                        data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    // Add to toast container
    let toastContainer = document.querySelector('.toast-container');
    
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }
    
    toastContainer.insertAdjacentHTML('beforeend', toastHTML);
    
    // Initialize and show toast
    const toastElement = toastContainer.lastElementChild;
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 3000
    });
    
    toast.show();
    
    // Remove toast after hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// ========== LOADING OVERLAY ==========
function showLoading() {
    const loadingHTML = `
        <div class="loading-overlay" id="loadingOverlay">
            <div class="spinner-border text-danger" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    
    if (!document.getElementById('loadingOverlay')) {
        document.body.insertAdjacentHTML('beforeend', loadingHTML);
    }
}

function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.remove();
    }
}

// ========== FORMAT CURRENCY ==========
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// ========== DEBOUNCE HELPER ==========
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// ========== EXPORT FUNCTIONS ==========
// Export để có thể sử dụng ở file khác
window.gamingShop = {
    showNotification,
    showLoading,
    hideLoading,
    formatCurrency,
    updateCartBadge,
    debounce
};