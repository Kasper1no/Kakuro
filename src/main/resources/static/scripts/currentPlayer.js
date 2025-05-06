async function getCurrentUser() {
    try {
        const res = await fetch("/player/current");
        if (!res.ok) throw new Error("Not logged in");
        return await res.json();
    } catch (err) {
        return null;
    }
}