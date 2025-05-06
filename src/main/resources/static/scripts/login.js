window.onload = function () {
    const form = document.getElementById("loginForm");
    const errorContainer = document.getElementById("loginErrorContainer");

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        console.log("Submitting login form...");

        const nickname = document.getElementById("nickname").value.trim();
        const password = document.getElementById("password").value.trim();

        errorContainer.classList.add("d-none");
        errorContainer.innerText = "";

        try {
            const res = await fetch("/player/login", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({nickname, password})
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "Login failed");
            }

            window.location.href = "/";
        } catch (err) {
            console.error(err);
            errorContainer.innerText = err.message || "Login failed";
            errorContainer.classList.remove("d-none");
        }

    });
};

