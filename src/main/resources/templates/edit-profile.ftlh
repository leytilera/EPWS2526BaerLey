<#import "layout.ftlh" as layout>

    <@layout.page title="ChessFed - Welcome" scripts=[]>
        <!-- profile card-->
        <div class="profile">
            <!-- user profile information -->
            <section class="profile-card">
                <header class="profile-card__header">
                    <h1 class="profile-card__title">Edit profile</h1>
                </header>

                <div class="profile-card__body">

                    <!-- Avatar (placeholder image)-->
                    <img class="profile-avatar" src="/assets/piotr-makowski-EuAtvK05fqE-unsplash.jpg"
                        alt="Profile avatar" width="72" height="72" loading="lazy" />

                    <form class="edit-profile-form" method="post" action="/profile/edit">
                        <div class="field">
                            <label class="field__label" for="username">Username</label>
                            <input class="field__input" type="text" name="username" value="${profile.username}"
                                readonly>
                            <p class="field__help">Your username can&apos;t be changed</p>
                        </div>

                        <div class="field">
                            <label class="field__label" for="handle">Actor handle/URL</label>
                            <input class="field__input" type="text" value="${profile.handle}" readonly>
                            <p class="field__help">Share this with others so they can invite you.</p>
                        </div>

                        <div class="field">
                            <label class="field__label" for="bio">Bio</label>
                            <textarea id="bio" class="field__textarea" name="bio" rows="5"
                                placeholder="Tell others something about you...">
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
        </div>
    </@layout.page>