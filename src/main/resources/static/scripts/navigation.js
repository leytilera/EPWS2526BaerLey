const navItems = document.querySelectorAll(".nav-bar__item[data-view]");
const views = document.querySelectorAll(".sidebar-view");

navItems.forEach((btn) => {
    btn.addEventListener("click", () => {
        const target = btn.dataset.view;

        navItems.forEach(b => b.classList.remove("is-active"));
        btn.classList.add("is-active");

        views.forEach(v => {
            v.hidden = v.dataset.view !== target;
        });
    });
});