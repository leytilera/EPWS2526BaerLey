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
                    <img class="profile-avatar" src="/assets/ChessFed_Default_Avatar.jpg"
                        alt="Profile Avatar" width="72" height="72" loading="lazy" />

                    <form class="edit-profile-form" method="post" action="/profile/edit">
                        <div class="field">
                            <div class="field__label">Username</div>
                            <div class="field__username">${profile.username}</div>
                            <p class="field__help">Your username can&apos;t be changed.</p>
                        </div>

                        <div class="field">
                            <div class="field__label" for="handle">Actor handle</div>
                            <div class="field__handle">${profile.handle}</div>
                            <p class="field__help">Your handle can&apos;t be changed.</p>
                        </div>

                        <div class="field">
                            <label class="field__label" for="bio">Bio</label>
                            <textarea id="bio" class="field__textarea" name="bio" rows="5"
                                placeholder="Tell others something about you...">${form.bio!""}</textarea>
                            <p class="field__help">Max. 255 characters</p>
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