window.addEventListener("load", async () => {
    const user = await getCurrentUser();

    const unloggedBlock = document.querySelector(".top-right-login .unlogged");
    const loggedBlock = document.querySelector(".top-right-login .logged");
    const avatarImg = document.getElementById("userAvatar");
    const nicknameSpan = document.getElementById("userNickname");
    const logoutDropdown = document.getElementById("logoutDropdown");
    const userInfo = document.getElementById("userInfo");

    if (user) {
        avatarImg.src = "/images/avatars/" + user.avatar;
        nicknameSpan.textContent = user.nickname;
        loggedBlock.classList.remove("d-none");
    } else {
        unloggedBlock.classList.remove("d-none");
        const hasSeenModal = localStorage.getItem('hasSeenLoginModal');

        if (!hasSeenModal) {
            showLoginModal();
            localStorage.setItem('hasSeenLoginModal', 'true');
        }
    }

    userInfo?.addEventListener("click", () => {
        logoutDropdown.classList.toggle("d-none");
        userInfo.classList.toggle("active");
    });

    document.getElementById("logoutBtn")?.addEventListener("click", async () => {
        await fetch("/player/logout", {method: "POST"});
        window.location.reload();
    });
});

function showLoginModal() {
    const closeButton = document.getElementById('closeButton');
    const countdown = document.getElementById('countdown');
    const overlay = document.getElementById('overlay');

    overlay.classList.remove('d-none');
    closeButton.style.display = 'none';

    let secondsRemaining = 5;

    const interval = setInterval(() => {
        secondsRemaining--;

        if (secondsRemaining > 0) {
            countdown.textContent = `You can close this in ${secondsRemaining} seconds...`;
        } else {
            clearInterval(interval);
            countdown.textContent = 'You can now close the message.';
            closeButton.style.display = 'inline-block';
        }
    }, 1000);

    closeButton.addEventListener('click', () => {
        overlay.classList.add('d-none');
    }, {once: true});
}




