const SIDEBAR_VIEW_KEY = "chessfed.sidebar.view";

function setSidebarView(selected) {
    const navItems = document.querySelectorAll(".nav-bar__item[data-view]");
    const views = document.querySelectorAll(".sidebar-view[data-view]");

    navItems.forEach((btn) => {
        const isActive = btn.dataset.view === selected;
        btn.classList.toggle("is-active", isActive);
        if (isActive) {
            btn.setAttribute("aria-current", "page");
        } else {
            btn.removeAttribute("aria-current");
        }
    });

    views.forEach((view) => {
        view.hidden = view.dataset.view !== selected;
    });

    localStorage.setItem(SIDEBAR_VIEW_KEY, selected);
}

function getInitialSidebarView() {
    const lastView = localStorage.getItem(SIDEBAR_VIEW_KEY);
    if (lastView && document.querySelector(`.sidebar-view[data-view="${lastView}"]`)) {
        return lastView;
    }
    return "invitations";
}

document.addEventListener("DOMContentLoaded", () => {
    const navItems = document.querySelectorAll(".nav-bar__item[data-view]");
    navItems.forEach((btn) => {
        btn.addEventListener("click", () => {
            setSidebarView(btn.dataset.view);
        });
    });
    setSidebarView(getInitialSidebarView());
});