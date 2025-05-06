window.onload = function () {
    const form = document.getElementById("registerForm");
    const nicknameInput = document.getElementById("nickname");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const avatarInput = document.getElementById("avatar"); // hidden input
    const errorContainer = document.getElementById("errorContainer");

    const avatarPreview = document.getElementById("avatarPreview");
    const avatarLeftBtn = document.getElementById("avatarLeft");
    const avatarRightBtn = document.getElementById("avatarRight");

    const avatars = [
        "boy.png",
        "girl.png",
        "girl2.png",
        "kid.png",
        "kid2.png",
        "teenager.png",
        "woman.png",
        "woman2.png",
        "young-woman.png"
    ];

    let currentAvatarIndex = 0;

    function updateAvatarDisplay() {
        const currentAvatar = avatars[currentAvatarIndex];
        avatarPreview.src = `/images/avatars/${currentAvatar}`;
        avatarInput.value = currentAvatar;
    }

    if (avatarLeftBtn && avatarRightBtn && avatarPreview) {
        avatarLeftBtn.addEventListener("click", () => {
            currentAvatarIndex = (currentAvatarIndex - 1 + avatars.length) % avatars.length;
            updateAvatarDisplay();
        });

        avatarRightBtn.addEventListener("click", () => {
            currentAvatarIndex = (currentAvatarIndex + 1) % avatars.length;
            updateAvatarDisplay();
        });

        updateAvatarDisplay();
    }

    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        clearError();

        const nickname = nicknameInput.value.trim();
        const password = passwordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();
        const avatar = avatarInput.value;

        if (!nickname || !password || !confirmPassword) {
            showError("All fields are required.");
            return;
        }

        if (password !== confirmPassword) {
            showError("Passwords do not match.");
            return;
        }

        try {
            const res = await fetch("/player/register", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({nickname, password, avatar})
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "Registration failed.");
            }

            window.location.href = "/";
        } catch (err) {
            showError(err.message);
        }
    });

    function showError(message) {
        errorContainer.innerText = message;
        errorContainer.classList.remove("d-none");
    }

    function clearError() {
        errorContainer.innerText = "";
        errorContainer.classList.add("d-none");
    }
};
