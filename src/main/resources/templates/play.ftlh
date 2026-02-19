<#import "layout.ftlh" as layout>

    <@layout.page title="ChessFed - Play" scripts=[]>
        <div class="app-shell--nested-two-columns">
            <!-- middle column (for chessboard) -->
            <main class="center">
                <section class="board-layout">
                    <!-- black player information -->
                    <div class="player-row">
                        <div class="board-avatar avatar--is-black" id="top-player-color" aria-hidden="true"></div>
                        <div class="player-meta">
                            <div class="player-name" id="top-player-name">Black player</div>
                            <div class="player-sub" id="top-player-status">Active</div>
                        </div>
                    </div>

                    <!-- chessboard -->
                    <div class="board-wrap">
                        <div id="board" class="board" data-js-board=""></div>
                    </div>

                    <!-- bottom player information (currently always white) -->
                    <div class="player-row">
                        <div class="board-avatar avatar--is-white" id="bottom-player-color" aria-hidden="true"></div>
                        <div class="player-meta">
                            <div class="player-name" id="bottom-player-name">White Player</div>
                            <div class="player-sub" id="bottom-player-status">Active</div>
                        </div>
                    </div>
                </section>
            </main>

            <!-- right column (move history)-->
            <aside class="right-column">

                <!-- move history -->
                <section class="panel">
                    <header class="panel__header">
                        <h2 class="panel__title">Move History</h2>
                    </header>

                    <div class="panel__body panel__body--scroll">
                        <!-- rendering as table/row: MOVENUMBER | WHITE | BLACK -->
                        <div class="moves" id="move-history"></div>
                    </div>
                </section>
            </aside>
        </div>
    </@layout.page>