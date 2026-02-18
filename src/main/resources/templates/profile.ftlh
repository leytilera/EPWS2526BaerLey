<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>ChessFed - Profile</title>
    <link rel="stylesheet" href="/styles/_combined.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script type="module" src="./scripts/main.js"></script>
    <script type="module" src="./scripts/navigation.js"></script>
    <script type="module" src="./scripts/copy-to-clipboard.js"></script>
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
                    <button class="nav-bar__item is-active" type="button" data-view="invite"
                        aria-label="Send Invitations">
                        <i class="bi bi-people"></i>
                    </button>

                    <!-- Notifications -->
                    <button class="nav-bar__item is-active" type="button" data-view="invitations" aria-current="page"
                        aria-label="Received Invitations">
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
                        <input class="invite__input" type="text" placeholder="Enter opponent handle or URL"
                            data-js-opponent>
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
                    <#if profile.ownProfile>
                        <h1 class="profile-card__title">Your profile</h1>
                        <p class="profile-card__subtitle">That&apos;s how your profile is visible to others!</p>
                    </#if>
                </header>

                <div class="profile-card__body">

                    <!-- Avatar (placeholder image)-->
                    <img class="profile-avatar" src="/assets/piotr-makowski-EuAtvK05fqE-unsplash.jpg" alt="Profile avatar" width="72"
                        height="72" loading="lazy" />

                    <div class="profile-informations-layout">
                        <div class="profile-username">${profile.username}</div>

                        <div class="profile-handle-container">
                            <span class="profile-handle" id="profile-handle">${profile.handle}</span>
                            <button class="btn btn--icon" type="button" aria-label="Copy handle to clipboard" data-js-copy-handle>
                                <i class="bi bi-clipboard"></i>
                            </button>
                        </div>
                        <#if profile.ownProfile>
                            <p class="field__help">Share this with others so they can invite you.</p>
                        <#else>
                            <p class="field_help">Use this handle to invite this player.</p>
                        </#if>

                        <#if profile.bio??>
                            <p class="profile-bio">${profile.bio}</p>
                        </#if>

                        <div class="profile-meta">
                            <div class="profile-meta__item">
                                <span class="profile-meta__item-label">Joined</span>
                                <span class="profile-meta__item-value">Jan 1rst, 2000</span>
                            </div>
                            <div class="profile-meta__item">
                                <span class="profile-meta__item-label">Games Played</span>
                                <span class="profile-meta__item-value">0</span>
                            </div>
                        </div>
                    </div>

                    <#if profile.ownProfile>
                        <a class="btn profile-edit" href="/profile/edit">Edit Profile</a>
                    </#if>
                </div>
            </section>
        </main>
    </div>
</body>

</html>