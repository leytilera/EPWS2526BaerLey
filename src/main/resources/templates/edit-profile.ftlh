<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>ChessFed - Edit Profile</title>
    <link rel="stylesheet" href="/styles/_combined.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script type="module" src="./scripts/main.js"></script>
    <script type="module" src="./scripts/navigation.js"></script>
</head>

<body>
    <div class="app-shell--two-columns">
        <!-- sidepanel-navigation -->
        <aside class="left-side-panel">
            <header class="left-side-panel__header">
                <!-- ChessFed logo -->
                <div class="logo">ChessFed</div>

                <!-- navigation bar -->
                <nav class="nav-bar" aria-label="Sidebar Navigation">
                    <!-- Send Invitations -->
                    <button class="nav-bar__item is-active" type="button" data-view="invite" aria-label="Send Invitations">
                        <i class="bi bi-people"></i>
                    </button>

                    <!-- Notifications -->
                    <button class="nav-bar__item is-active" type="button" data-view="invitations" aria-current="page" aria-label="Received Invitations">
                        <i class="bi bi-bell"></i>
                    </button>

                    <!-- Settings -->
                    <button class="nav-bar__item is-active" type="button" data-view="settings" aria-label="Settings">
                        <i class="bi bi-gear"></i>
                    </button>

                    <!-- Game List? -->
                    <button class="nav-bar__item is-active" type="button" data-view="game-list" aria-label="Game List">
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

                    <div class="invite__form">
                        <input class="invite__input" type="text" placeholder="Enter opponent handle or URL" data-js-opponent>
                        <button class="btn invite__btn" type="button" data-js-invite>Invite</button>
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
                    <ul data-js-gamelist></ul>
                </section>
            </div>
        </aside>

        <!-- profile card-->
        <main class="profile">
            <!-- user profile information -->
            <section class="profile-card">
                <header class="profile-card__header">
                    <h1 class="profile-card__title">Edit profile</h1>
                </header>

                <div class="profile-card__body">

                    <!-- Avatar (placeholder image)-->
                    <img class="profile-avatar" src="/assets/piotr-makowski-EuAtvK05fqE-unsplash.jpg" alt="Profile avatar" width="72"
                        height="72" loading="lazy" />

                    <form class="edit-profile-form" method="post" action="/profile/edit">
                        <div class="field">
                            <label class="field__label" for="username">Username</label>
                            <input class="field__input" type="text" name="username" value="${profile.username}" readonly>
                            <p class="field__help">Your username can&apos;t be changed</p>
                        </div>

                        <div class="field">
                            <label class="field__label" for="handle">Actor handle/URL</label>
                            <input class="field__input" type="text" value="${profile.handle}" readonly>
                            <p class="field__help">Share this with others so they can invite you.</p>
                        </div>

                        <div class="field">
                            <label class="field__label" for="bio">Bio</label>
                            <textarea id="bio" class="field__textarea" name="bio" rows="5" placeholder="Tell others something about you...">
                                ${form.bio!""}
                            </textarea>
                            <p class="field__help">Max. 280 characters</p>
                        </div>

                        <div class="edit-form__buttons">
                            <a class="btn btn--ghost" href="/profile">Cancel</a>
                            <button class="btn" type="submit">Save Changes</button>
                        </div>
                    </form>
                </div>
            </section>
        </main>
    </div>
</body>
</html>