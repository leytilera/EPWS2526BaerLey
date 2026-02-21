<!-- left side bar -->
<aside class="left-side-panel">
    <header class="left-side-panel__header">
        <!-- ChessFed logo -->
        <div class="logo">ChessFed</div>

        <!-- navigation bar -->
        <nav class="nav-bar" aria-label="Sidebar Navigation">
            <!-- Send Invitations -->
            <button class="nav-bar__item" type="button" data-view="invite" aria-label="Send Invitations">
                <i class="bi bi-people"></i>
            </button>

            <!-- Notifications -->
            <button class="nav-bar__item" type="button" data-view="invitations" aria-label="Received Invitations">
                <i class="bi bi-bell"></i>
            </button>

            <!-- Settings -->
            <button class="nav-bar__item" type="button" data-view="settings" aria-label="Settings">
                <i class="bi bi-gear"></i>
            </button>

            <!-- Game List -->
            <button class="nav-bar__item" type="button" data-view="game-list" aria-label="Game List">
                <i class="bi bi-list"></i>
            </button>

            <!-- Profile -->
            <a class="nav-bar__item" href="/profile" aria-label="Profile">
                <i class="bi bi-person"></i>
            </a>
        </nav>
    </header>

    <!-- content of left side panel, changes depending on which menu item has been selected-->
    <div class="left-side-panel__content">
        <!-- Send Invitations -->
        <section class="sidebar-view" data-view="invite" hidden>
            <div class="left-side-panel__section-header">
                <h2 class="left-side-panel__section-title">Invite Friend to play</h2>
            </div>

            <div class="invite-form">
                <input class="invite-form__input" type="text" placeholder="Enter opponent handle"
                    data-js-opponent>
                <div class="invite-form__buttons">
                    <button class="btn invite__btn" type="button" data-js-invite>Invite</button>
                    <button class="btn invite__btn btn--ghost" type="button" data-js-view-profile>View Profile</button>
                </div>
            </div>

            <p class="invite__status" data-js-invite-status></p>
        </section>

        <!-- Notifications/Invitations -->
        <section class="sidebar-view" data-view="invitations">
            <div class="left-side-panel__section-header">
                <h2 class="left-side-panel__section-title">Invitations</h2>
            </div>

            <div class="left-panel__scroll">
                <ul class="invitations-list" data-js-invitations>
                </ul>
            </div>
        </section>

        <!-- Settings -->
        <section class="sidebar-view" data-view="settings" hidden>
            <div class="left-side-panel__section-header">
                <h2 class="left-side-panel__section-title">Settings</h2>
            </div>
        </section>

        <!-- Game List -->
        <section class="sidebar-view" data-view="game-list" hidden>
            <div class="left-side-panel__section-header">
                <h2 class="left-side-panel__section-title">Active Games</h2>
            </div>
            <ul class="gamelist" data-js-gamelist></ul>
        </section>
    </div>
</aside>