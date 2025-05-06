window.onload = async function () {
    const toggleButton = document.getElementById('toggle-review-btn');
    const toggleButtonText = toggleButton.querySelector('.menu-btn-content');
    const overlay = document.getElementById('overlay');
    const closeBtn = document.querySelector('.close-btn');
    const stars = document.querySelectorAll('#star-rating span');
    const ratingInput = document.getElementById('rating');
    const form = document.getElementById('review-submit-form');
    const commentInput = document.getElementById('comment');
    const errorMessage = document.getElementById('error-message');

    let currentRating = 0;

    const player = await getCurrentUser()

    if (!player) {
        toggleButton.classList.add('hidden');
        return;
    }

    try {
        const response = await fetch(`/player/review`);
        if (response.ok) {
            const review = await response.json();
            if (review.comment || review.rating) {
                toggleButtonText.textContent = "Edit review";
                if (review.comment) commentInput.value = review.comment;
                if (review.rating) {
                    currentRating = review.rating;
                    ratingInput.value = currentRating;
                    updateStars();
                }
            }
        }
    } catch (e) {
        console.error("Failed to fetch review:", e);
    }

    toggleButton.addEventListener('click', () => {
        overlay.classList.remove('hidden');
    });

    closeBtn.addEventListener('click', () => {
        overlay.classList.add('hidden');
    });

    stars.forEach(star => {
        star.addEventListener('click', () => {
            const selectedValue = parseInt(star.getAttribute('data-value'));
            currentRating = (currentRating === selectedValue) ? 0 : selectedValue;
            updateStars();
            ratingInput.value = currentRating;
        });
    });

    function updateStars() {
        stars.forEach(star => {
            const starValue = parseInt(star.getAttribute('data-value'));
            star.classList.toggle('selected', starValue <= currentRating);
        });
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        errorMessage.textContent = '';
        errorMessage.classList.add('d-none');

        const comment = commentInput.value.trim();
        const rating = currentRating;

        if (!comment) {
            errorMessage.textContent = "Comment is required.";
            errorMessage.classList.remove('d-none');
            return;
        }

        const reviewData = {
            comment: comment,
            rating: rating
        };

        try {
            const response = await fetch('/player/review', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reviewData)
            });

            if (response.ok) {
                location.reload();
            } else {
                const errorText = await response.text();
                errorMessage.textContent = errorText || 'Failed to submit review. Please try again.';
                errorMessage.classList.remove('d-none');
            }
        } catch (error) {
            console.error('Submit error:', error);
            errorMessage.textContent = 'An unexpected error occurred. Please try again.';
            errorMessage.classList.remove('d-none');
        }
    });
};
