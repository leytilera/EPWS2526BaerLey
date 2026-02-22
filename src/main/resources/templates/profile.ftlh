<#import "layout.ftlh" as layout>

    <@layout.page title="ChessFed - Profile" scripts=["/scripts/copy-to-clipboard.js"]>
        <!-- profile card-->
        <div class="profile">
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
                    <img class="profile-avatar" src="/assets/ChessFed_Default_Avatar.jpg" alt="Profile Avatar"
                        width="72" height="72" loading="lazy" />

                    <div class="profile-informations-layout">
                        <div class="profile-username">${profile.username}</div>

                        <div>
                            <div class="profile-handle-container">
                                <span class="profile-handle" id="profile-handle">${profile.handle}</span>
                                <button class="btn btn--icon" type="button" aria-label="Copy handle to clipboard"
                                    data-js-copy-handle>
                                    <i class="bi bi-clipboard"></i>
                                </button>
                            </div>
                            <#if profile.ownProfile>
                                <div class="field__help">Share this with others so they can invite you.</div>
                                <#else>
                                    <div class="field__help">Use this handle to invite this player.</div>
                            </#if>
                        </div>

                        <#if profile.bio??>
                            <p class="profile-bio">${profile.bio}</p>
                        </#if>

                        <div class="profile-meta">
                            <div class="profile-meta__item">
                                <span class="profile-meta__item-label">Joined</span>
                                <span class="profile-meta__item-value">Jan 1, 2000</span>
                            </div>
                            <div class="profile-meta__item">
                                <span class="profile-meta__item-label">Games Played</span>
                                <span class="profile-meta__item-value">${profile.countFinishedGames}</span>
                            </div>
                        </div>
                    </div>
                    <#if profile.ownProfile>
                        <a class="btn profile-edit" href="/profile/edit">Edit Profile</a>
                    </#if>
                </div>
            </section>

            <!-- game history cards -->
            <section class="game-history-card">
                <header class="game-history-card__header">
                    <h1 class="game-history-card__title">Active Games</h1>
                </header>

                <!-- active games -->
                <#if activeGames?has_content>
                    <div class="game-history__table">
                        <div class="game-history__row game-history__row-header">
                            <div class="game-history__cell">Id</div>
                            <div class="game-history__cell">Players</div>
                            <div class="game-history__cell">Handles</div>
                            <div class="game-history__cell">Result</div>
                            <div class="game-history__cell">Status</div>
                            <div class="game-history__cell">Moves</div>
                        </div>

                        <#list activeGames as game>
                            <a class="game-history__row game-history__row-item" href="/play#${game.gameId}"
                                aria-label="View Game">
                                <!-- Id -->
                                <div class="game-history__cell">
                                    <span class="game-history__id">${game.gameId}</span>
                                </div>
                                <!-- Players -->
                                <div class="game-history__cell">
                                    <div>
                                        <span class="game-history__avatar avatar--is-white" aria-hidden="true"></span>
                                        <span class="game-history__player-name">${game.whiteUsername}</span>
                                    </div>
                                    <div>
                                        <span class="game-history__avatar avatar--is-black" aria-hidden="true"></span>
                                        <span class="game-history__player-name">${game.blackUsername}</span>
                                    </div>
                                </div>
                                <div class="game-history__cell">
                                    <div class="game-history__player-handle">${game.whiteHandle}</div>
                                    <div class="game-history__player-handle">${game.blackHandle}</div>
                                </div>
                                <!-- Result -->
                                <div class="game-history__cell">
                                    <div class="game-history__result">${game.whiteScore}</div>
                                    <div class="game-history__result">${game.blackScore}</div>
                                </div>
                                <!-- Status -->
                                <div class="game-history__cell">
                                    <div class="game-history__status">${game.status}</div>
                                </div>
                                <!-- Move Count -->
                                <div class="game-history__cell">
                                    <div class="game-history__moves">${game.moveCount}</div>
                                </div>
                            </a>
                        </#list>
                    </div>
                    <#else>
                        <p>No active games.</p>
                </#if>
            </section>

            <section class="game-history-card">
                <header class="game-history-card__header">
                    <h1 class="game-history-card__title">Finished Games</h1>
                </header>

                <#if finishedGames?has_content>
                    <div class="game-history__table">
                        <div class="game-history__row game-history__row-header">
                            <div class="game-history__cell">Id</div>
                            <div class="game-history__cell">Players</div>
                            <div class="game-history__cell">Handles</div>
                            <div class="game-history__cell">Result</div>
                            <div class="game-history__cell">Status</div>
                            <div class="game-history__cell">Moves</div>
                        </div>

                        <#list finishedGames as game>
                            <a class="game-history__row game-history__row-item" href="/replay#${game.gameId}"
                                aria-label="View Game">
                                <!-- Date -->
                                <div class="game-history__cell">
                                    <span class="game-history__id">${game.gameId}</span>
                                </div>
                                <!-- Players -->
                                <div class="game-history__cell">
                                    <div>
                                        <span class="game-history__avatar avatar--is-white" aria-hidden="true"></span>
                                        <span class="game-history__player-name">${game.whiteUsername}</span>
                                    </div>
                                    <div>
                                        <span class="game-history__avatar avatar--is-black" aria-hidden="true"></span>
                                        <span class="game-history__player-name">${game.blackUsername}</span>
                                    </div>
                                </div>
                                <div class="game-history__cell">
                                    <div class="game-history__player-handle">${game.whiteHandle}</div>
                                    <div class="game-history__player-handle">${game.blackHandle}</div>
                                </div>
                                <!-- Result -->
                                <div class="game-history__cell">
                                    <div class="game-history__result">${game.whiteScore}</div>
                                    <div class="game-history__result">${game.blackScore}</div>
                                </div>
                                <!-- Status -->
                                <div class="game-history__cell">
                                    <div class="game-history__status">${game.status}</div>
                                </div>
                                <!-- Move Count -->
                                <div class="game-history__cell">
                                    <div class="game-history__moves">${game.moveCount}</div>
                                </div>
                            </a>
                        </#list>
                    </div>
                    <#else>
                        <p>No finished games yet.</p>
                </#if>
            </section>
        </div>
    </@layout.page>