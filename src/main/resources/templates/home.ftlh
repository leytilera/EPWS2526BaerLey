<#import "layout.ftlh" as layout>

    <@layout.page title="ChessFed - Welcome" scripts=[]>
        <div class="app-shell--nested-two-columns">
            <!-- middle column (chessboard) -->
            <div class="center">
                <section class="board-layout">
                    <!-- chessboard -->
                    <div class="board-wrap">
                        <div id="board" class="board" data-js-board=""></div>
                    </div>
                </section>
            </div>

            <!-- right column (welcome card)-->
            <aside class="right-column">

                <!-- move history -->
                <section class="panel">
                    <header class="panel__header">
                        <h2 class="panel__title">Start playing</h2>
                    </header>

                    <div class="panel__body">
                        <p>Pick an active game from the list, check your invitations or invite a friend to play!</p>
                    </div>
                </section>
            </aside>
        </div>
    </@layout.page>