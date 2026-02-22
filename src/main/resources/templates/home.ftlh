<#import "layout.ftlh" as layout>

    <@layout.page title="ChessFed - Welcome" scripts=[]>
        <div class="app-shell--nested-two-columns">
            <!-- middle column (chessboard) -->
            <div class="center">
                <section class="board-layout">
                    <!-- chessboard -->
                    <div class="board-slot board-slot--home">
                        <div id="board" class="board" data-js-board=""></div>
                    </div>
                </section>
            </div>

            <!-- right column (welcome card)-->
            <aside class="right-side-panel">

                <!-- move history -->
                <section class="move-history">
                    <header class="move-history__header">
                        <h2 class="move-history__title">Start playing</h2>
                    </header>

                    <div class="move-history__body">
                        <p class="">Pick an active game from the list, check your invitations or invite a friend to
                            play!</p>
                    </div>
                </section>
            </aside>
        </div>
    </@layout.page>